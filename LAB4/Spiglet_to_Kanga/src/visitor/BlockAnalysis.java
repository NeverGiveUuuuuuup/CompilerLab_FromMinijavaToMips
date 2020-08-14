package visitor;

import java.util.HashMap;
import java.util.Vector;

import symbol.*;
import syntaxtree.*;

 
public class BlockAnalysis extends GJDepthFirst<MySpigletType, MySpigletType> {
	MyGoal mygoal;
	MyProcedure proc;
	Vector<String> unvisitedlabels;
	HashMap<String, Integer> visitedlabels;
	Vector<Integer> blockloc;
	boolean next;
	boolean stmthead=true;
	
	public MySpigletType visit(Goal n, MySpigletType argu) {
		mygoal = (MyGoal)argu;
		int mainstmtlen = n.f1.f0.nodes.size();
		proc = new MyProcedure("MAIN", 0, mainstmtlen+1);
		n.f1.f0.nodes.add(new NoOpStmt());
		mygoal.table.put("MAIN", proc);
		
		n.f1.accept(this, proc);
		//proc.printLabels();
		
		n.f3.accept(this, mygoal);
		
		return mygoal;
	}
	public MySpigletType visit(Procedure n, MySpigletType argu) { 
		String procname = n.f0.f0.tokenImage;
		int paranum = Integer.valueOf(n.f2.f0.tokenImage);
		int stmtlen = n.f4.f1.f0.nodes.size();
		
		proc = new MyProcedure(procname, paranum, stmtlen);
		((MyGoal)argu).table.put(procname, proc);
		
		n.f4.accept(this, proc);
		//proc.printLabels();
		return null;
	}
	public MySpigletType visit(StmtExp n, MySpigletType argu) {
		MyProcedure proc = (MyProcedure)argu;
		Vector<Node> stmtlist = n.f1.f0.nodes;
		
		stmtlist.add(new NoOpStmt());
		proc.addstmt(new MyStmt(proc.stmtlen));
		
		n.f1.accept(this, argu);
		return null;
	}
	
	public MySpigletType visit(StmtList n, MySpigletType argu) {
		MyProcedure proc = ((MyProcedure)(argu));
		blockloc = proc.blockloc;
		visitedlabels = proc.labels;
		unvisitedlabels = new Vector<String>();
		next=false;
		blockloc.add(new Integer(0));
		proc.stmtlist.elementAt(proc.stmtlen-1).blockend = true;
		
		
		for(int i=0;i<proc.stmtlen;++i) {
			if(next==true) {
				blockloc.add(new Integer(i));
				proc.stmtlist.elementAt(i-1).blockend = true;
			}
			next = false;
			stmthead = true;
			Node stmt = n.f0.nodes.elementAt(i);
			MyStmt mystmt = proc.stmtlist.elementAt(i);
			stmt.accept(this, mystmt);
		}
		return null;
	}
	
	public MySpigletType visit(Label n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		int index = mystmt.index;
		String label = n.f0.tokenImage;
		
		visitedlabels.put(label, new Integer(index));
		if(unvisitedlabels.contains(label)) {
			blockloc.add(new Integer(index));
			proc.stmtlist.elementAt(index-1).blockend = true;
			unvisitedlabels.remove(label);
		}
		if(stmthead==true) {
			mystmt.labeled = true;
		}
		return null;
	}
	
	public MySpigletType visit(Stmt n, MySpigletType argu) {
		stmthead = false;
		if(n.f0.choice instanceof CJumpStmt || n.f0.choice instanceof JumpStmt) {
			n.f0.accept(this, argu);
		}
		stmthead = true;
		return null;
	}
	
	public MySpigletType visit(JumpStmt n, MySpigletType argu) {
		
		String label = n.f1.f0.tokenImage;
		if(visitedlabels.containsKey(label)) {
			int index = visitedlabels.get(label).intValue();
			blockloc.add(new Integer(index));
			if(index>0) {
				proc.stmtlist.elementAt(index-1).blockend = true;
			}
		}
		else {
			unvisitedlabels.add(label);
		}
		return null;
	}
	public MySpigletType visit(CJumpStmt n, MySpigletType argu) {
		
		String label = n.f2.f0.tokenImage;
		if(visitedlabels.containsKey(label)) {
			int index = visitedlabels.get(label).intValue();
			blockloc.add(new Integer(index));
			if(index>0) {
				proc.stmtlist.elementAt(index-1).blockend = true;
			}
		}
		else {
			unvisitedlabels.add(label);
		}
		next = true;
		return null;
	}
	
	
}
