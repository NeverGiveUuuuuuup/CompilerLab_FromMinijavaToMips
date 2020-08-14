package symbol;

import java.util.HashMap;

public class MyGoal extends MySpigletType {
	public HashMap<String, MyProcedure> table;
	public MyGoal() {
		table = new HashMap<String, MyProcedure>();
	}
	public MyProcedure getProcess(String name) {
		return table.get(name);
	}
	
}
