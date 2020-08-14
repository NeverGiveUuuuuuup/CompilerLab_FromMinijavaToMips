package visitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import symbol.*;
import syntaxtree.*;

public class SpigletToKanga_new extends GJDepthFirst<MySpigletType, MySpigletType> {
	MyGoal mygoal;
	MyProcedure proc;
	static String FilePath;
	String code;
	boolean v1used=false;
	
	public MySpigletType visit(Goal n, MySpigletType argu) {
		mygoal = new MyGoal();
		n.accept((new BasicBlockAnalysis()), mygoal);
		n.accept((new LivenessAnalysis()), mygoal);
		n.accept(new InterferenceGraph(), mygoal);
		
		code = "";
		_initproc(n.f1);
		n.f3.accept(this, mygoal);
		
		outputToFile(code, FilePath);
		return mygoal;
	}
	void _initproc(Node n) {
		Node stmts;
		if(n instanceof StmtList) {
			stmts = n;
			proc = mygoal.getProcess("MAIN");
		}
		else if(n instanceof Procedure) {
			stmts = ((Procedure) n).f4;
			String procname = ((Procedure)n).f0.f0.tokenImage;
			proc = mygoal.getProcess(procname);
		}
		else {
			return;
		}
		code += proc.name+"["+proc.paranum+"]"+"["+proc.spillnum+"]"+"["+proc.maxparanum+"]"+"\n";
		//parameter
		for(int i=0;i<proc.paranum;++i) {
			MyTemp mytemp = proc.temps.get(i);
			if(mytemp==null || proc.stmtlist.elementAt(0).livevars.containsKey(i)==false) continue;
			if(i<4) {
				if(mytemp.spilled==true) {
					code += "ASTORE SPILLEDARG " + mytemp.spill + " a"+i + "\n";
				}
				else {
					code += "MOVE " + getReg(mytemp.reg) + " a"+i + "\n";
				}
			}
			else {
				if(mytemp.spilled==false) {
					code += "ALOAD " + getReg(mytemp.reg) + " SPILLEDARG " + mytemp.spill+"\n";
				}
			}
		}
		stmts.accept(this, proc);
		code += "END\n";
	}
	public MySpigletType visit(Procedure n, MySpigletType argu) {
		_initproc(n);
		return null;
	}
	
	public MySpigletType visit(StmtExp n, MySpigletType argu) {
		n.f1.accept(this, argu);
		String s = visitSimpleExp(n.f3);
		code += "MOVE v0 " + s + "\n";
		return null;
	}
	
	public MySpigletType visit(StmtList n, MySpigletType argu) {
		MyStmt mystmt;
		for(int i=0;i<proc.stmtlen;++i) {
			System.out.println(i);
			v1used = false;
			mystmt = proc.stmtlist.elementAt(i);
			
			n.f0.nodes.elementAt(i).accept(this, mystmt);
		}
		return null;
	}
	
	public MySpigletType visit(NoOpStmt n, MySpigletType argu) {
		code += "NOOP\n";
		return null;
	}
	
	public MySpigletType visit(ErrorStmt n, MySpigletType argu) {
		code += "ERROR\n";
		return null;
	}
	//CJumpStmt	::=	"CJUMP" Temp Label
	public MySpigletType visit(CJumpStmt n, MySpigletType argu) {
		String reg = getReg(n.f1);
		code += "CJUMP " + reg + " " + n.f2.f0.tokenImage + "\n";
		return null;
	}
	public MySpigletType visit(JumpStmt n, MySpigletType argu) {
		code += "JUMP "+ n.f1.f0.tokenImage + "\n";
		return null;
	}
	//HStoreStmt	::=	"HSTORE" Temp IntegerLiteral Temp
	public MySpigletType visit(HStoreStmt n, MySpigletType argu) {
		String reg1 = getReg(n.f1);
		String reg2 = getReg(n.f3);
		code += "HSTORE " + reg1 + " " + n.f2.f0.tokenImage + " " +reg2 + "\n";
		return null;
	}
	//HLoadStmt	::=	"HLOAD" Temp Temp IntegerLiteral
	public MySpigletType visit(HLoadStmt n, MySpigletType argu) {
		String reg1 = getReg(n.f1, argu);
		String reg2 = getReg(n.f2);
		code += "HLOAD " + reg1  + " " + reg2 + " " + n.f3.f0.tokenImage + "\n";
		writeback(n.f1, reg1);
		return null;
	}
	
	public MySpigletType visit(MoveStmt n, MySpigletType argu) {
		if(n.f2.f0.choice instanceof Call) {
			makeCall((Call)n.f2.f0.choice, argu);
			
			String reg1 = getReg(n.f1, argu);
			code += "MOVE " + reg1 + " v0\n";
			writeback(n.f1, reg1);
		}
		else if(n.f2.f0.choice instanceof HAllocate) {
			String reg1 = getReg(n.f1, argu);
			String s = visitSimpleExp(((HAllocate)n.f2.f0.choice).f1);
			code += "MOVE " + reg1 + " HALLOCATE " + s +"\n";
			writeback(n.f1, reg1);
		}
		else if(n.f2.f0.choice instanceof BinOp) {
			String reg1 = getReg(n.f1, argu);
			String reg2 = getReg(((BinOp)n.f2.f0.choice).f1);
			String s = visitSimpleExp(((BinOp)n.f2.f0.choice).f2);
			code += "MOVE "+reg1+" "+((NodeToken)((BinOp)n.f2.f0.choice).f0.f0.choice).tokenImage+" "+reg2+" "+s+"\n";
			writeback(n.f1, reg1);
		}
		else if(n.f2.f0.choice instanceof SimpleExp) {
			String reg1 = getReg(n.f1, argu);
			String s = visitSimpleExp((SimpleExp)n.f2.f0.choice);
			code += "MOVE " + reg1 + " " + s +"\n";
			writeback(n.f1, reg1);
		}
		return null;
	}
	void makeCall(Call n, MySpigletType argu) {
		MyStmt nextstmt = proc.stmtlist.elementAt(((MyStmt)argu).index+1);
		HashMap<Integer, Integer> nextlives = nextstmt.livevars;
		HashMap<Integer, Integer> lives = ((MyStmt)argu).livevars;
		Iterator<Entry<Integer, Integer>> it = nextlives.entrySet().iterator();
	    int _count = 0;
		while (it.hasNext()) {
	    	HashMap.Entry<Integer, Integer> entry = (HashMap.Entry<Integer, Integer>) it.next();
	        Integer temp = entry.getKey();
	        v1used=false;
	        MyTemp mytemp = proc.temps.get(temp);
	        if(mytemp.spilled==false && lives.containsKey(temp)==true) {
	        	code += "ASTORE SPILLEDARG " + (proc.tempspillnum+_count) + " " + getReg(mytemp.reg)+"\n";
	        }
	        _count++;
		}
		if (n.f3.present() ) {
	         _count=0;
	         for ( Enumeration<Node> e = n.f3.elements(); e.hasMoreElements(); ) {
	            v1used=false;
	        	Temp temp = (Temp)e.nextElement();
	            String reg = getReg(temp);
	            if(_count<4) {
	            	code += "MOVE a"+_count+" "+reg+"\n";
	            }
	            else {
	            	code += "PASSARG "+(_count-3)+" "+reg+"\n";
	            }
	            _count++;
	         }
	      }
		String s = visitSimpleExp(n.f1);
		code += "CALL " + s +"\n";
		it = nextlives.entrySet().iterator();
		_count = 0;
		while (it.hasNext()) {
	    	HashMap.Entry<Integer, Integer> entry = (HashMap.Entry<Integer, Integer>) it.next();
	        Integer temp = entry.getKey();
	        v1used=false;
	        MyTemp mytemp = proc.temps.get(temp);
	        if(mytemp.spilled==false && lives.containsKey(temp)==true) {
	        	code += "ALOAD "+ getReg(mytemp.reg) +" SPILLEDARG " + (proc.tempspillnum+_count) +"\n";
		    }
	        _count++;
		}
		return;
	}
	String visitSimpleExp(SimpleExp n) {
		String _ret;
		if(n.f0.choice instanceof Temp) {
			_ret = getReg((Temp)n.f0.choice);
		}
		else if(n.f0.choice instanceof Label) {
			_ret = ((Label)n.f0.choice).f0.tokenImage;
		}
		else {
			_ret = ((IntegerLiteral)n.f0.choice).f0.tokenImage;
		}
		return _ret;
	}
	
	public MySpigletType visit(Label n, MySpigletType argu) {
		code += n.f0.tokenImage +" ";
		return null;
	}
	
	public MySpigletType visit(PrintStmt n, MySpigletType argu) {
		String s = visitSimpleExp(n.f1);
		code += "PRINT " +s +"\n";
		return null;
	}
	static String[] regmap = {"s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7",
			   "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8", "t9",
			   "v1", "v0", "a3", "a2", "a1", "a0"};
	String getReg(int index) {
		if(index>=0&&index<18) {
			return regmap[index];
		}
		return null;
	}
	String getReg(Integer index) {
		if(index>=0&&index<18) {
			return regmap[index];
		}
		return null;
	}
	public String getReg(Temp n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		Integer temp = new Integer(n.f1.f0.tokenImage);
		if(mystmt.livevars.containsKey(temp)==true) {
			return getReg(n);
		}
		else {
			MyStmt nextstmt = proc.stmtlist.elementAt(mystmt.index+1);
			HashMap<Integer, Integer> lives = nextstmt.livevars;
			if(lives.containsKey(temp)==true) {
				return getReg(n);
			}
			else{
				return "v1";
			}
		}
	}
	public String getReg(Temp n) {
		Integer temp = new Integer(n.f1.f0.tokenImage);
		MyTemp mytemp = proc.temps.get(temp);
		if(mytemp.spilled==true) {
			String s="v1";
			if(v1used==true) {
				s="v0";
			}
			else {
				v1used=true;
			}
			code += "ALOAD " + s + " SPILLEDARG " + mytemp.spill + "\n";
			return s;
		}
		else {
			return getReg(mytemp.reg);
		}
	}
	
	void  writeback(Temp n, String reg) {
		Integer temp = new Integer(n.f1.f0.tokenImage);
		MyTemp mytemp = proc.temps.get(temp);
		if(mytemp.spilled==true) {
			code += "ASTORE SPILLEDARG " + mytemp.spill + " " + reg + "\n";
		}
	}
	void outputToFile(String str, String filePath) {
		try {
			File f = new File(filePath);
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(str.getBytes());
			fos.close();
		} catch (IOException e) {
			System.out.println("output failed\n");
		}
	}
	public static void setFilePath(String filePath) {
		FilePath = filePath;
	}
	
	
	
}
