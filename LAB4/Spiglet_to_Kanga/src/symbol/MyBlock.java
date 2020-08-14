package symbol;

import java.util.Vector;

public class MyBlock extends MySpigletType {
	public int head;
	public int end;
	public Vector<Integer> ins;
	public Vector<Integer> outs;
	
	void _init() {
		
	}
	public MyBlock() {
		_init();
	}
	public MyBlock(int _head) {
		_init();
		head = _head;
	}
	
}
