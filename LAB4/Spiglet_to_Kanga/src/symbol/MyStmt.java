package symbol;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.*;
import java.util.Vector;


public class MyStmt extends MySpigletType {
	public int index;
	public HashMap<Integer, Integer> livevars;
	public String type;
	public boolean blockend;
	
	public Vector<MyStmt> prestmts = new Vector<MyStmt>();
	public Vector<MyStmt> backstmts = new Vector<MyStmt>();
	public HashMap<Integer, Integer> stmtregs;
	
	public Integer additionalregs;
	public Integer integerLiteral;
	public Integer spillarg;
	public int calltype;//1: reg; 2: int; 3: label
	public String label;
	public Integer[] moveregs = new Integer[2];
	public int movetype;//1:reg; 2:int; 3:label
	public boolean labeled = false;
	public MyStmt() {
		index = 0;
		livevars = new HashMap<Integer, Integer>();
		type = null;
	}
	public MyStmt(int i) {
		index = i;
		livevars = new HashMap<Integer, Integer>();
		type = null;
	}
	public MyStmt(int i, HashMap<Integer, Integer> live) {
		index = i;
		livevars = live;
		type = null;
	}
	public MyStmt(String _type) {
		type = _type;
		index= -1;
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
	public void settemp(Integer temp, Integer reg) {
		if(stmtregs==null) {
			stmtregs = new HashMap<Integer, Integer>();
		}
		stmtregs.put(temp, reg);
	}
	
	public String getCode() {
		String code = type + " ";
		
		if(type=="ALOAD") {
			code += RegDescriptor.getRegName(this.additionalregs.intValue());
			code += " SPILLEDARG " + this.spillarg;
		}
		else if(type=="ASTORE") {
			code += " SPILLEDARG " + this.spillarg + " ";
			code += RegDescriptor.getRegName(this.additionalregs.intValue());
		}
		else if(type=="CALL") {
			//"CALL" SimpleExp
			switch(calltype) {
			case 1:
				code += RegDescriptor.getRegName(this.additionalregs.intValue());
				break;
			case 2:
				code += integerLiteral;
				break;
			case 3:
				code += label;
				break;
			default:
				System.out.println("warning: unexpected calltype.");
			}
		}
		else if(type=="MOVE") {
			code += RegDescriptor.getRegName(this.moveregs[0].intValue()) + " ";
			
			switch(movetype) {
			case 1:
				code += RegDescriptor.getRegName(this.moveregs[1].intValue());
				break;
			case 2:
				code += integerLiteral;
				break;
			case 3:
				code += label;
				break;
			default:
				System.out.println("warning: unexpected movetype.");
			}
		}
		else if(type=="PASSARG") {
			code += integerLiteral + " " + RegDescriptor.getRegName(this.additionalregs.intValue());
		}
		else {
			System.out.println("warning: unexpected stmt type " + type);
		}
		code += "\n";
		return code;
	}
	
}
