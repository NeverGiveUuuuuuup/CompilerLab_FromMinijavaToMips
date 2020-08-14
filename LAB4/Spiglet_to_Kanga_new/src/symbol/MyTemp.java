package symbol;

import java.util.HashSet;
import java.util.Vector;

public class MyTemp extends MySpigletType {
	public int reg = -1;
	public int spill = -1;
	public boolean spilled=false;
	public HashSet<Integer> interferences = new HashSet<Integer>();
	public Integer index=0;
	public MyTemp() {
		
	}
	public MyTemp(Integer i) {
		index = i;
	}

}
