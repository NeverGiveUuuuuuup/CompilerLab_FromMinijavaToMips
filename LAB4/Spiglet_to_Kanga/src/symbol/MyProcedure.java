package symbol;

import java.util.HashMap;
import java.util.Vector;

public class MyProcedure extends MySpigletType {
	public int stmtlen;
	public Vector<Integer> blockloc;
	public Vector<MyStmt> stmtlist;
	public HashMap<String, Integer> labels;
	public String name;
	public int paranum;
	public int maxparanum;
	public int spillnum;
	public void _init(int n) {
		stmtlen = n;
		stmtlist = new Vector<MyStmt>();	
		for(int i=0;i<n;++i) {	
			stmtlist.add(new MyStmt(i));
		}
		blockloc = new Vector<Integer>();
		labels = new HashMap<String, Integer>();
	}
	public MyProcedure() {
		super();
		_init(0);
		name = "";
		paranum=0;
	}
	public MyProcedure(String _name, int _paranum, int _stmtlen) {
		super();
		_init(_stmtlen);
		name = _name;
		paranum=_paranum;
	}
	public boolean addstmt(MyStmt mystmt) {
		stmtlist.add(mystmt);
		stmtlen++;
		return true;
	}
	public boolean addlabel(String label, int index) {
		labels.put(label, new Integer(index));
		return true;
	}
	public void printlive() {
		for(int i=0;i<stmtlen;++i) {
			System.out.print("stmt"+i+": ");
			this.stmtlist.elementAt(i).printlive();
		}
	}
	
	public void printLabels() {
		for(int i=0;i<this.stmtlen;++i) {
			if(this.stmtlist.elementAt(i).labeled==true) {
				System.out.println(i);	
			}
		}
	}
}
