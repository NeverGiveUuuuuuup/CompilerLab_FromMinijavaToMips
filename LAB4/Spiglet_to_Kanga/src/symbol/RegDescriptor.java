package symbol;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import syntaxtree.*;
import visitor.*;
import symbol.*;

import java.util.Map.Entry;

public class RegDescriptor extends MySpigletType {
	final int regnum = 24;
	MyProcedure proc;
	int spillnum=0;
	Vector<HashMap<Integer, Integer>> regs = new Vector<HashMap<Integer, Integer>>();
	Vector<HashMap<Integer, Integer>> spills = new Vector<HashMap<Integer, Integer>>();
	static String[] regmap = {"s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7",
					   "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8", "t9",
					   "v1", "v0", "a3", "a2", "a1", "a0"};
	HashMap<Integer,AddrDescriptor> varmap = new HashMap<Integer,AddrDescriptor>();
	
	public RegDescriptor(){
		for(int i=0;i<regnum;++i) {
			regs.add(new HashMap<Integer, Integer>());
		}
	}
	
	static public String getRegName(int i) {
		return regmap[i];
	}
	
	public void procinit(MyProcedure _proc) {
		proc = _proc;
		for(int i=0;i<regnum;++i) {
			regs.elementAt(i).clear();
		}
		spills.clear();
		varmap.clear();
		spillnum=0;
		for(int i=0;i<proc.paranum;++i) {
			Integer temp = new Integer(i);
			if(proc.stmtlist.elementAt(0).livevars.containsKey(temp)==false) {
				System.out.println("warning: unused para TEMP "+ temp + " in function " + proc.name);
				continue;
			}
			Integer live = proc.stmtlist.elementAt(0).livevars.get(temp);
			if(i<4) {
				// 复制到一个新的寄存器中
				MyStmt stmt1 = proc.stmtlist.firstElement();
				Integer reg =  getReg(temp, stmt1, true);
				stmt1.prestmts.add(this.makeMove(reg, (regnum-i-1)));
			}
			else {
				this.addvarspill(temp, live);
			}
		}
		System.out.println("procinit end");
	}
	
	//将寄存器与临时变量temp对应起来
	public void addvarreg(Integer reg, Integer temp, MyStmt mystmt) {
		if(reg==null || temp==null) {
			System.out.println("warning null in addvarreg.");
			return;
		}
		Integer live = this.getLiveness(temp, mystmt);
		regs.elementAt(reg).put(temp, live);
		if(varmap.containsKey(temp)==false) {
			varmap.put(temp, new AddrDescriptor());
		}
		if(varmap.get(temp).regslist.contains(reg)==false) {
			varmap.get(temp).regslist.add(new Integer(reg));
		}
	}
	
	public void setavrreg_only(Integer reg, Integer temp, MyStmt mystmt) {
		Integer live = this.getLiveness(temp, mystmt);
		AddrDescriptor var = varmap.get(temp);
		
		//1. 修改x的地址描述符，把reg作为唯一的存放x的寄存器地址
		var.regslist.clear();
		var.regslist.add(reg);
		//2. 修改reg的寄存器描述符，使之只包含x
		//3. 从任何不同于x的变量的地址描述符中删除r
		Iterator<Entry<Integer, Integer>> it = regs.elementAt(reg.intValue()).entrySet().iterator();
	    while (it.hasNext()) {
	    	HashMap.Entry<Integer, Integer> entry = (HashMap.Entry<Integer, Integer>) it.next();
	    	Integer _temp = entry.getKey();
	    	if(_temp == temp) {
	    		continue;
	    	}
	    	varmap.get(_temp).regslist.remove(reg);
	    }
	    regs.elementAt(reg.intValue()).clear();
	    regs.elementAt(reg.intValue()).put(temp, live);
	}
	
	public Integer getLiveness(Integer temp, MyStmt mystmt) {
		if(mystmt.index > 23) {
			int t;
			t=2;
		}
		Integer live = mystmt.livevars.get(temp);
		
		if(live==null) {
			if(mystmt.blockend==false) {
				live = proc.stmtlist.elementAt(mystmt.index+1).livevars.get(temp);
			}
			if(live==null) {
				live = new Integer(mystmt.index);
			}
		}
		return live;
	}
	
	//向栈中添加临时变量temp的记录，并修改varmap
	public Integer addvarspill(Integer temp, Integer live) {
		spills.add(new HashMap<Integer, Integer>());
		spills.elementAt(spillnum).put(temp, live);
		if(varmap.containsKey(temp)==false) {
			varmap.put(temp, new AddrDescriptor());
		}
		Integer spill = new Integer(spillnum);
		varmap.get(temp).spillslist.add(spill);
		spillnum++;
		return spill;
	}
	
	//为一个临时变量指定一个寄存器用于读出或者写入
	public Integer getReg(Integer temp, MyStmt mystmt, boolean override) {
		if(varmap.containsKey(temp)==false) {
			varmap.put(temp, new AddrDescriptor());
		}
		AddrDescriptor var = varmap.get(temp);
		Integer reg = null;
		
		if(var.regslist.isEmpty()==false) {
			reg = var.regslist.elementAt(0);
		}
		else {
			reg = allocateReg(temp, mystmt);
			if(override==false) {
				//将临时变量的值加载到寄存器中:找到对应的spillarg，然后生成aload指令
				Integer spill = this.getSpillLoc(temp);
				mystmt.prestmts.add(this.makeALoad(reg, spill));
				
				this.addvarreg(reg, temp, mystmt);
			}
		}
		if(override==true) {
			setavrreg_only(reg, temp, mystmt);
		}
		return reg;
	}
	
	public Integer allocateReg(Integer temp, MyStmt mystmt) {
		/*  线性扫描算法
		 * 当一个新的活性区间起始点到来时
		 *	顺序扫描队列
		 *	移除过期变量
		 *	试图将新变量加入队列
		 *	当寄存器不足时，“溢出”结束最晚的变量
		 * 
		 */
		Integer reg = null;
		int latesttemp=mystmt.index-1;
		int latestreg = -1;
		for(int i=0;i<regnum-4;++i) {
			Iterator<Entry<Integer, Integer>> it = regs.elementAt(i).entrySet().iterator();
		    if(regs.elementAt(i).isEmpty()) {
				reg = new Integer(i);
				break;
			}
		    
		    int minlive=proc.stmtlen;
		    while (it.hasNext()) {
		    	HashMap.Entry<Integer, Integer> entry = (HashMap.Entry<Integer, Integer>) it.next();
		    	int live = entry.getValue().intValue();
		    	if(live < mystmt.index) {
		    		
		    		Integer _temp = entry.getKey();
		    		varmap.get(_temp).regslist.remove(new Integer(i));
		    		it.remove();
		    		continue;
		    	}
		    	if(live < minlive) {
		    		minlive = live;
		    	}
		    }
		    if(regs.elementAt(i).isEmpty()) {
				reg = new Integer(i);
				break;
			}
			if(minlive > latesttemp) {
	    		latesttemp = minlive;
	    		latestreg = i;
	    	} 
		}
		
		if(reg==null) {
			if(latestreg < 0 || latestreg >= regnum) {
				System.out.println("Warning: RegDescriptor go wrong.");
				latestreg = 0;
			}
			reg = new Integer(latestreg);
			
			Iterator<Entry<Integer, Integer>> it = regs.elementAt(latestreg).entrySet().iterator();
			while (it.hasNext()) {
		    	HashMap.Entry<Integer, Integer> entry = (HashMap.Entry<Integer, Integer>) it.next();
		    	Integer _temp = entry.getKey();
				Integer _spill = getSpillLoc(_temp);
				
				mystmt.prestmts.add(this.makeAStore(reg, _spill));
			}
			regs.elementAt(latestreg).clear();
		}
		
		return reg;
	}
	
	
	public int getSpillNum() {
		return spillnum;
	}
	
	
	public boolean checkUninitialVar(int index) {
		MyStmt mystmt = proc.stmtlist.elementAt(index);
		
		/*
		 * for(int i=0;i<regnum-4;++i) {
			regs.elementAt(i).clear();
		}
		Iterator<Entry<Integer, AddrDescriptor>> it = varmap.entrySet().iterator();
		while (it.hasNext()) {
	    	HashMap.Entry<Integer,AddrDescriptor>  entry = (HashMap.Entry<Integer,AddrDescriptor> ) it.next();
	    	entry.getValue().regslist.clear();
		}
		 * 
		 */
		if(mystmt.labeled==true && mystmt.index>0) {
			for(int i=0;i<regnum-4;++i) {
				regs.elementAt(i).clear();
			}
			Iterator<Entry<Integer, AddrDescriptor>> it = varmap.entrySet().iterator();
			while (it.hasNext()) {
		    	HashMap.Entry<Integer,AddrDescriptor>  entry = (HashMap.Entry<Integer,AddrDescriptor> ) it.next();
		    	entry.getValue().regslist.clear();
			}
		}
		Iterator<Entry<Integer, Integer>> it2 = mystmt.livevars.entrySet().iterator();
		while (it2.hasNext()) {
	    	HashMap.Entry<Integer, Integer> entry = (HashMap.Entry<Integer, Integer>) it2.next();
	    	Integer temp = entry.getKey();
	    	getSpillLoc(temp);
		}
		return false;
	}
	
	public void finishBlock(int index) {
		MyStmt mystmt = proc.stmtlist.elementAt(index);
		if(proc.stmtlist.elementAt(index).type=="CJUMP" || proc.stmtlist.elementAt(index).type =="JUMP") {
			mystmt.prestmts.addAll(saveLives(index));
		}
		else {
			mystmt.backstmts.addAll(saveLives(index+1));
		}
		
	}
	
	public Vector<MyStmt> saveLives(int index){
		Vector<MyStmt> stmts = new Vector<MyStmt>();
		MyStmt mystmt = proc.stmtlist.elementAt(index);
		Iterator<Entry<Integer, Integer>> it = mystmt.livevars.entrySet().iterator();
		while (it.hasNext()) {
	    	HashMap.Entry<Integer, Integer> entry = (HashMap.Entry<Integer, Integer>) it.next();
	    	Integer temp = entry.getKey();
	    	Integer spill = getSpillLoc(temp);
	    	if(varmap.get(temp).regslist.isEmpty()==false) {
	    		Integer reg = varmap.get(temp).regslist.firstElement();
	    		stmts.add(makeAStore(reg, spill));
	    	}
		}
		return stmts;
	}
	
	public Vector<MyStmt> loadLives(int index){
		Vector<MyStmt> stmts = new Vector<MyStmt>();
		MyStmt mystmt = proc.stmtlist.elementAt(index);
		Iterator<Entry<Integer, Integer>> it = mystmt.livevars.entrySet().iterator();
		while (it.hasNext()) {
	    	HashMap.Entry<Integer, Integer> entry = (HashMap.Entry<Integer, Integer>) it.next();
	    	Integer temp = entry.getKey();
	    	Integer spill = getSpillLoc(temp);
	    	if(varmap.get(temp).regslist.isEmpty()==false) {
	    		Integer reg = varmap.get(temp).regslist.firstElement();
	    		stmts.add(this.makeALoad(reg, spill));
	    	}
		}
		return stmts;
	}
	
	public void makeReturn(SimpleExp n) {
		MyStmt laststmt = proc.stmtlist.lastElement();
		MyStmt move = new MyStmt("MOVE");
		move.moveregs[0] = 19;//"v0"
		move.moveregs[1] = -1;
		if(n.f0.choice instanceof Temp) {
			move.movetype = 1;
			Integer temp = new Integer(((Temp)n.f0.choice).f1.f0.tokenImage);
			move.moveregs[1] = this.getReg(temp, laststmt, false);
		}
		else if(n.f0.choice instanceof IntegerLiteral) {
			move.movetype = 2;
			move.integerLiteral = new Integer(((IntegerLiteral)n.f0.choice).f0.tokenImage);
		}
		else if(n.f0.choice instanceof Label) {
			move.movetype = 3;
			move.label = ((Label)n.f0.choice).f0.tokenImage;
		}
		laststmt.backstmts.add(move);
	}
	
	public Integer getSpillLoc(Integer temp) {
		AddrDescriptor addr = varmap.get(temp);
		Integer spill=null;
		
		if(addr==null) {
			addr = new AddrDescriptor();
			varmap.put(temp, addr);
		}
    	if(addr.spillslist.isEmpty()) {
    		Integer live=new Integer(-1);
    		spill = addvarspill(temp, live);
    	}
    	else {
    		spill = addr.spillslist.firstElement();
    	}
    	return spill;
	}
	
	public MyStmt makeAStore(Integer reg, Integer spill) {
		MyStmt astore = new MyStmt("ASTORE");
		
		astore.additionalregs = reg;
		astore.spillarg = spill;
		return astore;
	}
	public MyStmt makeALoad(Integer reg, Integer spill) {
		MyStmt aload = new MyStmt("ALOAD");
		aload.additionalregs = reg;
		aload.spillarg = spill;
		return aload;
	}
	public MyStmt makeMove(Integer destreg, Integer srcreg) {
		MyStmt move = new MyStmt("MOVE");
		move.moveregs[0] = destreg;
		move.moveregs[1] = srcreg;
		move.movetype=1;
		return move;
	}
	public MyStmt makePassArg(int index, Integer reg) {
		MyStmt pass = new MyStmt("PASSARG");
		pass.integerLiteral = new Integer(index);
		pass.additionalregs = reg;
		return pass;
	}
	public void makeCall(Call n, MyStmt mystmt) {
		MyStmt call = new MyStmt("CALL");
		if(n.f1.f0.choice instanceof Temp) {
			call.calltype = 1;
			Temp _temp = (Temp)n.f1.f0.choice;
			Integer temp = new Integer(_temp.f1.f0.tokenImage);
			Integer reg = this.getReg(temp, mystmt, false);
			call.additionalregs = reg;
			mystmt.settemp(temp, reg);
		}
		else if(n.f1.f0.choice instanceof IntegerLiteral) {
			call.calltype = 2;
			call.integerLiteral = new Integer(((IntegerLiteral)n.f1.f0.choice).f0.tokenImage);
			
		}
		else if(n.f1.f0.choice instanceof Label) {
			call.calltype = 3;
			call.label = ((Label)n.f1.f0.choice).f0.tokenImage;
		}
		else {
			call.calltype = 0;
			System.out.println("Warning: unexpected call addr.");
			return;
		}
		int paranum = n.f3.nodes.size();
		for(int i=0;i<paranum;++i) {
			Temp _temp = (Temp)n.f3.nodes.elementAt(i);
			Integer temp = new Integer(_temp.f1.f0.tokenImage);
			Integer reg = this.getReg(temp, mystmt, false);
			if(i<4) {
				Integer destReg = this.regnum-i-1;
				mystmt.prestmts.add(this.makeMove(destReg, reg));
			}
			else {
				mystmt.prestmts.add(this.makePassArg(i-3, reg));
			}
		}
		
		if(mystmt.index<proc.stmtlen-1) {
			//保存所有active的变量
			mystmt.prestmts.addAll(saveLives(mystmt.index+1));
			mystmt.prestmts.add(call);
			//恢复所有active的变量
			mystmt.prestmts.addAll(this.loadLives(mystmt.index+1));
		}
		else {
			mystmt.prestmts.add(call);
		}
		
	}
	
}

class AddrDescriptor{
	Vector<Integer> regslist;
	Vector<Integer> spillslist;
	AddrDescriptor(){
		regslist = new Vector<Integer>();
		spillslist = new Vector<Integer>();
	}
}
