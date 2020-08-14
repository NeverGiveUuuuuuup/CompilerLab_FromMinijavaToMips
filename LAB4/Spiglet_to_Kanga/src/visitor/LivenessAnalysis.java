package visitor;
import java.util.HashMap;
import java.util.Vector;

import symbol.*;
import syntaxtree.*;

public class LivenessAnalysis extends GJDepthFirst<MySpigletType, MySpigletType> {
	MyGoal mygoal;
	
	MyProcedure proc;
	HashMap<Integer, Integer> oldlives;
	
	public MySpigletType visit(Goal n, MySpigletType argu) {
		mygoal = (MyGoal)argu;
		proc = mygoal.getProcess("MAIN");
		System.out.println("MAIN: begin liveness analysis");
		n.f1.accept(this, proc);
		proc.printlive();
		
		n.f3.accept(this, mygoal);
		return mygoal;
	}
	public MySpigletType visit(Procedure n, MySpigletType argu) { 
		String procname = n.f0.f0.tokenImage;
		proc = mygoal.getProcess(procname);
		System.out.println(procname+": begin liveness analysis");
		n.f4.accept(this, proc);
		proc.printlive();
		return null;
	}
	
	public MySpigletType visit(StmtExp n, MySpigletType argu) {
		Vector<MyStmt> stmtlist = proc.stmtlist;
		
		if(n.f3.f0.choice instanceof Temp) {
			int stmtlen = n.f1.f0.nodes.size();
			Integer temp = new Integer(((Temp)n.f3.f0.choice).f1.f0.tokenImage);
			Integer liveness = new Integer(stmtlen);
			stmtlist.elementAt(proc.stmtlen-1).addlive(temp, liveness);
		}
		n.f1.accept(this, argu);
		return null;
	}
	
	public MySpigletType visit(StmtList n, MySpigletType argu) {
		boolean changed = true;
		int t=0;
		oldlives = new HashMap<Integer, Integer>();
		while(changed) {
			
			changed = false;
			for(int i=proc.stmtlen-1;i>=0;--i) {
				Node stmt = n.f0.nodes.elementAt(i);
				MyStmt mystmt = proc.stmtlist.elementAt(i);
				oldlives.clear();
				oldlives.putAll(mystmt.livevars);		
				stmt.accept(this, mystmt);			
				changed = (mystmt.liveschanged(oldlives))?true:changed;
			}
		}
		return null;
	}
	
	public MySpigletType visit(NoOpStmt n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		mystmt.addnextlive(proc);
		return null;
	}
	
	public MySpigletType visit(ErrorStmt n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		mystmt.addnextlive(proc);
		return null;
	}
	
	public MySpigletType visit(CJumpStmt n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		String label = n.f2.f0.tokenImage;
		
		mystmt.addnextlive(proc);
		mystmt.addjumplive(proc, label);
		n.f1.accept(this, mystmt);
		return null;
	}
	
	public MySpigletType visit(JumpStmt n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		String label = n.f1.f0.tokenImage;
		mystmt.addjumplive(proc, label);
		return null;
	}
	
	public MySpigletType visit(HStoreStmt n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		mystmt.addnextlive(proc);
		n.f1.accept(this, mystmt);
		n.f3.accept(this, mystmt);
		return null;
	}
	
	public MySpigletType visit(HLoadStmt n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		Integer temp1 = new Integer (n.f1.f1.f0.tokenImage);
		
		mystmt.addnextlive(proc);
		mystmt.deletelive(temp1);
		n.f2.accept(this, mystmt);
		return null;
	}
	
	public MySpigletType visit(MoveStmt n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		Integer temp1 = new Integer (n.f1.f1.f0.tokenImage);
		
		mystmt.addnextlive(proc);
		mystmt.deletelive(temp1);
		n.f2.accept(this, mystmt);
		return null;
	}
	
	public MySpigletType visit(PrintStmt n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		mystmt.addnextlive(proc);
		n.f1.accept(this, mystmt);
		return null;
	}
	
	public MySpigletType visit(Temp n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		Integer temp = new Integer(n.f1.f0.tokenImage);
		Integer index = new Integer(mystmt.index);
		
		mystmt.addlive(temp, index);
		return null;
	}
}
