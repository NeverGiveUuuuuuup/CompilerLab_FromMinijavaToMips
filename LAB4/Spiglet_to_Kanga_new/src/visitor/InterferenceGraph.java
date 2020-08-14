package visitor;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.Vector;

import symbol.*;
import syntaxtree.*;

public class InterferenceGraph extends GJDepthFirst<MySpigletType, MySpigletType> {
	MyGoal mygoal;
	MyProcedure proc;
	
	static int K=18;
	HashMap<Integer, MyTemp> temps;

	
	public MySpigletType visit(Goal n, MySpigletType argu) {
		mygoal = (MyGoal)argu;
		
		_initproc(n.f1, null);//MAIN
		
		n.f3.accept(this, mygoal);//procs
		return mygoal;
	}
	public MySpigletType visit(Procedure n, MySpigletType argu) {
		_initproc(n, null);
		n.f4.accept(this, argu);
		return null;
	}
	public MySpigletType _initproc(Node n,  MySpigletType argu) {
		StmtList stmts;
		if(n instanceof StmtList) {
			stmts = (StmtList)n;
			proc = mygoal.getProcess("MAIN");
			proc.paranum=0;
		}
		else if(n instanceof Procedure) {
			stmts = ((Procedure) n).f4.f1;
			String procname = ((Procedure)n).f0.f0.tokenImage;
			proc = mygoal.getProcess(procname);
			proc.paranum= Integer.parseInt(((Procedure) n).f2.f0.tokenImage);
		}
		else {
			return null;
		}
		stmts.accept(new MaxParanum(), null);
		//InitialTmp 
		temps = new HashMap<Integer, MyTemp>();
		stmts.accept(new InitialTmp(), proc);
		//interference
		MyStmt mystmt;
		HashMap<Integer, Integer> lives;
		for(int i=0;i<proc.stmtlen;++i) {
			mystmt = proc.stmtlist.elementAt(i);
			lives = mystmt.livevars;
			Iterator<Entry<Integer, Integer>> it = lives.entrySet().iterator();
		    while (it.hasNext()) {
		    	HashMap.Entry<Integer, Integer> entry = (HashMap.Entry<Integer, Integer>) it.next();
		        Integer temp = entry.getKey();
		        MyTemp mytemp = temps.get(temp);
		        mytemp.interferences.addAll(lives.keySet());
		        mytemp.interferences.remove(temp);
			}
		}
		//color
		Queue<MyTemp> q=new PriorityQueue<>(cMyTemp);
		Stack<MyTemp> s=new Stack<>();
		Vector<MyTemp> spill=new Vector<>();
		q.addAll(temps.values());
		while(q.isEmpty()==false) {
			MyTemp head = q.peek();
			
			if(head.interferences.size()<K) {
				q.poll();
				s.push(head);
			}
			else {
				Queue<MyTemp> bq=new PriorityQueue<>(cMyTemp2);
				bq.addAll(q);
				head = bq.peek();
				q.remove(head);
				spill.add(head);
				head.spilled=true;
			}
			Iterator<Integer> it = head.interferences.iterator();
			while(it.hasNext()){
				Integer temp = it.next();
				MyTemp mytemp = temps.get(temp);
				if(q.contains(mytemp)==true) {
					mytemp.interferences.remove(head.index);
				}
			}
		}
		//allocate
		while(s.isEmpty()==false) {
			MyTemp top = s.pop();
			boolean[] arrayB = new boolean[K];
			Iterator<Integer> it = top.interferences.iterator();
			while(it.hasNext()){
				Integer temp = it.next();
				MyTemp mytemp = temps.get(temp);
				if(mytemp.reg!=-1) {
					arrayB[mytemp.reg]=true;
				}
			}
			for(int i=0;i<K;++i) {
				if(arrayB[i]==false) {
					top.reg=i;
					break;
				}
			}
		}
		//parameter
		proc.tempspillnum = Math.max(0, proc.paranum-4);
		for(int i=0;i<proc.paranum;++i) {
			Integer temp = i;
			MyTemp mytemp = temps.get(temp);
			if(i<4) {
				if(mytemp==null)continue;
				if(mytemp.spilled==true) {
					mytemp.spill=proc.tempspillnum++;
				}
			}
			else {
				if(mytemp==null)continue;
				
				mytemp.spill=i-4;
			}
		}
		//spill
		Iterator<MyTemp> it = spill.iterator();
		while(it.hasNext()){
			MyTemp mytemp = it.next();
			if(mytemp.spill!=-1) continue;
			mytemp.spill=proc.tempspillnum++;
		}
		proc.spillnum=0;
		stmts.accept(new RegConvention(), proc);
		proc.spillnum +=proc.tempspillnum;
		proc.temps = temps;
		
		return null;
	}
	static Comparator<MyTemp> cMyTemp=new Comparator<MyTemp>() {
        public int compare(MyTemp o1, MyTemp o2) {
            int size1 = o1.interferences.size();
            int size2 = o2.interferences.size();
            
        	if(size1!=size2) {
            	return size1-size2; 
            }
        	else {
        		return -1;
        	}
        }
        
    };
   //TODO spill
    static Comparator<MyTemp> cMyTemp2=new Comparator<MyTemp>() {
        public int compare(MyTemp o1, MyTemp o2) {
            int size1 = o1.interferences.size();
            int size2 = o2.interferences.size();
            
        	if(size1!=size2) {
            	return size1-size2; 
            }
        	else {
        		return -1;
        	}
        }
        
    };
	
	class InitialTmp extends GJDepthFirst<MySpigletType, MySpigletType>{
		public MySpigletType visit(Temp n, MySpigletType argu) {
			Integer temp = new Integer(n.f1.f0.tokenImage);
			if(temps.containsKey(temp)==false) {
				MyTemp mytemp = new MyTemp(temp);
				temps.put(temp, mytemp);
			}
			return null;
		}
	}
	class MaxParanum extends GJDepthFirst<MySpigletType, MySpigletType>{
		public MySpigletType visit(Call n, MySpigletType argu) {
			int paranum=n.f3.nodes.size();
			if(paranum>proc.maxparanum) {
				proc.maxparanum=paranum;
			}
			return null;
		}
	}
	class RegConvention extends GJDepthFirst<MySpigletType, MySpigletType>{
		public MySpigletType visit(StmtList n, MySpigletType argu) {
			MyStmt mystmt;
			for(int i=0;i<proc.stmtlen-1;++i) {
				mystmt = proc.stmtlist.elementAt(i+1);
				n.f0.nodes.elementAt(i).accept(this, mystmt);
			}
			return null;
		}
		public MySpigletType visit(Call n, MySpigletType argu) {
			MyStmt mystmt = (MyStmt)argu;
			int livenum = mystmt.livevars.size();
			if(livenum>proc.spillnum) {
				proc.spillnum=livenum;
			}
			return null;
		}
	}
}


