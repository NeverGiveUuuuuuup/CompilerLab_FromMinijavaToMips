package symbol;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.*;
import java.util.Vector;


public class MyStmt extends MySpigletType {
	public int index;
	public HashMap<Integer, Integer> livevars;
	public boolean blockend;
	
	public boolean labeled = false;
	public String label;
	
	public MyStmt() {
		index = 0;
		livevars = new HashMap<Integer, Integer>();
	}
	public MyStmt(int i) {
		index = i;
		livevars = new HashMap<Integer, Integer>();
	}
	public MyStmt(int i, HashMap<Integer, Integer> live) {
		index = i;
		livevars = live;
	}
	
	public boolean addlive(Integer temp, Integer liveness) {
		boolean changed = false;
		if(blockend == true) {
    		liveness = new Integer(index);
		}
		else {
			liveness = Math.max(index, liveness);
			if(livevars.containsKey(temp)==true) {
				Integer oldliveness = livevars.get(temp);
				liveness = Math.max(liveness, oldliveness);
			}
		}
		livevars.put(temp, liveness);
		return changed;
	}
	public boolean addlive(MyStmt src){
		boolean changed=false;
		Iterator<Entry<Integer, Integer>> it = src.livevars.entrySet().iterator();
	    while (it.hasNext()) {
	    	HashMap.Entry<Integer, Integer> entry = (HashMap.Entry<Integer, Integer>) it.next();
	        Integer temp = entry.getKey();
	        this.addlive(temp, entry.getValue());
		}
		return changed;
	}
	public boolean deletelive(Integer temp) {
		boolean changed = false;
		if(livevars.containsKey(temp)==true) {
			livevars.remove(temp, livevars.get(temp));
			changed = true;
		}
		return changed;
	}
	public boolean addnextlive(MyProcedure proc) {
		boolean changed = false;
		if(index < proc.stmtlen-1) {
			MyStmt nextstmt = proc.stmtlist.elementAt(index + 1);
			changed = this.addlive(nextstmt);
		}
		return changed;
	}
	public boolean addjumplive(MyProcedure proc, String label) {
		boolean changed = false;
		if(proc.labels.containsKey(label)==true) {
			int loc = proc.labels.get(label).intValue();
			MyStmt deststmt = proc.stmtlist.elementAt(loc);
			changed = (this.addlive(deststmt))?true:changed;
		}
		return changed;
	}
	public boolean liveschanged(HashMap<Integer, Integer> oldlives) {
		boolean changed = false;
		if(oldlives.size() != livevars.size()) {
			changed = true;
			return true;
		}
		Iterator<Entry<Integer, Integer>> it = oldlives.entrySet().iterator();
	    while (it.hasNext()) {
	    	HashMap.Entry<Integer, Integer> entry = (HashMap.Entry<Integer, Integer>) it.next();
	        Integer temp = entry.getKey();
	        if(this.livevars.containsKey(temp)==false) {
	        	changed = true;
	        	break;
	        }
	        else if(this.livevars.get(temp).equals(entry.getValue())==false) {
	        	changed = true;
	        	break;
	        }
		}
		return changed;
	}
	public void printlive() {
		Iterator<Entry<Integer, Integer>> it = livevars.entrySet().iterator();
		while (it.hasNext()) {
	    	HashMap.Entry<Integer, Integer> entry = (HashMap.Entry<Integer, Integer>) it.next();
	        System.out.print(entry.getKey()+"="+entry.getValue()+" ");
		}
	    System.out.println();
	}	
}
