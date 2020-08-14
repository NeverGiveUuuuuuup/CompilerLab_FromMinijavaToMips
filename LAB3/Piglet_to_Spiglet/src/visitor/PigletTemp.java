package visitor;

import syntaxtree.*;

public class PigletTemp extends DepthFirstVisitor {
	int maxNumber;

	public PigletTemp() {
		maxNumber = 20;
	}

	/**
	 * f0 -> "TEMP" f1 -> IntegerLiteral()
	 */
	public void visit(Temp n) {
		int tempIndex = Integer.valueOf(n.f1.f0.toString());
		if (tempIndex > maxNumber) {
			maxNumber = tempIndex;
		}
	}

	public int getMaxNumber() {
		return maxNumber;
	}
}
