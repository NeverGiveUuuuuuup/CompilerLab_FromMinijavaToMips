package visitor;

import symbol.*;
import syntaxtree.*;

public class LinearScan extends GJDepthFirst<MySpigletType, MySpigletType> {
	MyGoal mygoal;
	MyProcedure proc;
	RegDescriptor regs;

	public LinearScan() {
		super();
		regs = new RegDescriptor();
	}
	
	public MySpigletType visit(Goal n, MySpigletType argu) {
		mygoal = (MyGoal)argu;
		
		proc = mygoal.getProcess("MAIN");
		System.out.println("MAIN: begin linear scan");
		
		regs.procinit(proc);
		proc.maxparanum = proc.paranum;
		n.f1.accept(this, proc);
		proc.spillnum = regs.getSpillNum();
		
		n.f3.accept(this, mygoal);
		return mygoal;
	}
	
	public MySpigletType visit(Procedure n, MySpigletType argu) {
		String procname = n.f0.f0.tokenImage;
		proc = mygoal.getProcess(procname);
		System.out.println(procname+": begin linear scan");
		
		regs.procinit(proc);
		proc.maxparanum = proc.paranum;
		n.f4.accept(this, argu);
		proc.spillnum = regs.getSpillNum();
		return null;
	}
	
	public MySpigletType visit(StmtExp n, MySpigletType argu) {
		n.f1.accept(this, argu);
		//TODO 如何为返回值生成语句
		regs.makeReturn(n.f3);
		return null;
	}
	
	public MySpigletType visit(StmtList n, MySpigletType argu) {
		MyStmt mystmt;
		for(int i=0;i<proc.stmtlen;++i) {
			mystmt = proc.stmtlist.elementAt(i);
			
			n.f0.nodes.elementAt(i).accept(this, mystmt);
			
			
		}
		return null;
	}
	
	public MySpigletType visit(NoOpStmt n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		mystmt.type = "NOOP";
		return null;
	}
	public MySpigletType visit(ErrorStmt n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		mystmt.type = "ERROR";
		return null;
	}
	
	public MySpigletType visit(CJumpStmt n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		mystmt.type = "CJUMP";
		n.f1.accept(this, argu);
		return null;
	}
	
	public MySpigletType visit(JumpStmt n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		mystmt.type = "JUMP";
		return null;
	}
	
	public MySpigletType visit(HStoreStmt n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		mystmt.type = "HSTORE";
		n.f3.accept(this, argu);
		n.f1.accept(this, argu);
		return null;
	}
	
	public MySpigletType visit(HLoadStmt n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		mystmt.type = "HLOAD";
		n.f2.accept(this, argu);
		
		Integer temp1 = new Integer(n.f1.f1.f0.tokenImage);
		mystmt.settemp(temp1, regs.getReg(temp1, mystmt, true));
		return null;
	}
	
	public MySpigletType visit(MoveStmt n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		mystmt.type = "MOVE";
		n.f2.accept(this, mystmt);
		
		Integer temp = new Integer(n.f1.f1.f0.tokenImage);
		Integer reg = regs.getReg(temp, mystmt, true);
		mystmt.settemp(temp, reg);
		return null;
	}
	
	public MySpigletType visit(PrintStmt n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		mystmt.type = "PRINT";
		n.f1.accept(this, mystmt);
		return null;
	}
	
	public MySpigletType visit(Call n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		int paranum = n.f3.nodes.size();
		proc.maxparanum = Math.max(paranum, proc.maxparanum);
		//需要将call指令提前
		regs.makeCall(n, mystmt);
		return null;
	}
	
	public MySpigletType visit(Temp n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		Integer temp = new Integer(n.f1.f0.tokenImage);
		Integer reg = regs.getReg(temp, mystmt, false);
		mystmt.settemp(temp, reg);
		if(reg==null) {
			System.out.println("stmt"+mystmt.index+" reg null");
		}
		//System.out.println("stmt"+mystmt.index+": allocate temp"+temp+" "+RegDescriptor.getRegName(reg.intValue()));
		return null;
	}
	
	
}
