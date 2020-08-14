package visitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import syntaxtree.*;

public class TranslateToSpiglet extends GJNoArguDepthFirst<String> {
	String spigletCode;
	int addTempFromIndex;
	static String FilePath;
	
	String getTemp() {
		return "TEMP " + Integer.toString(addTempFromIndex++);
	}
	
	void outputToFile(String str, String filePath) {
		try {
			File f = new File(filePath);
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(str.getBytes());
			fos.close();
		} catch (IOException e) {
			System.out.println("output failed\n");
		}
	}
	public static void setFilePath(String filePath) {
		FilePath = filePath;
	}
	/**
	 * f0 -> "MAIN" f1 -> StmtList() f2 -> "END" f3 -> ( Procedure() )* f4 -> <EOF>
	 */
	public String visit(Goal n) {
		
		PigletTemp t = new PigletTemp();
		n.accept(t);
		addTempFromIndex = t.getMaxNumber() + 1;
		
		spigletCode = "MAIN\n";
		n.f1.accept(this);
		spigletCode += "END\n";
		n.f3.accept(this);
		
		outputToFile(spigletCode, FilePath);
		return null;
	}

	/**
	 * f0 -> ( ( Label() )? Stmt() )*
	 */
	public String visit(StmtList n) {
		int nodeNum = n.f0.size();
		for(int i=0;i<nodeNum;++i) {
			NodeSequence nodeSequence = (NodeSequence)n.f0.nodes.elementAt(i);
			int sequenceSize = nodeSequence.size();
			for(int j=0;j<sequenceSize;++j) {
				Node node = nodeSequence.elementAt(j);
				//System.out.println(node.getClass().getName());

				if(node instanceof NodeOptional && ((NodeOptional)node).node!=null) {
					String label = ((NodeOptional)node).node.accept(this) + "\n";
					spigletCode += label;
				}
				else {
					node.accept(this);
				}
			}
		}
		return null;
	}

	/**
	 * f0 -> Label() f1 -> "[" f2 -> IntegerLiteral() f3 -> "]" f4 -> StmtExp()
	 */
	public String visit(Procedure n) {
		String label = n.f0.accept(this);
		String integer = n.f2.accept(this);
		
		spigletCode += label + " [" + integer + "]\n";
		spigletCode += "BEGIN\n";
		String returnVal = n.f4.accept(this);
		spigletCode += "RETURN " + returnVal + "\n";
		spigletCode += "END\n";
		
		return null;
	}

	/**
	 * f0 -> NoOpStmt() | ErrorStmt() | CJumpStmt() | JumpStmt() | HStoreStmt() |
	 * HLoadStmt() | MoveStmt() | PrintStmt()
	 */
	public String visit(Stmt n) {
		n.f0.accept(this);
		return null;
	}

	/**
	 * f0 -> "NOOP"
	 */
	public String visit(NoOpStmt n) {
		
		spigletCode += "NOOP\n";
		return null;
	}

	/**
	 * f0 -> "ERROR"
	 */
	public String visit(ErrorStmt n) {

		spigletCode += "ERROR\n";
		return null;
	}

	/**
	 * f0 -> "CJUMP" f1 -> Exp() f2 -> Label()
	 */
	public String visit(CJumpStmt n) {
		String exp = n.f1.accept(this);
		String label = n.f2.accept(this);
		
		spigletCode += "CJUMP " + exp + " " + label + "\n";
		return null;
	}

	/**
	 * f0 -> "JUMP" f1 -> Label()
	 */
	public String visit(JumpStmt n) {
		String label = n.f1.accept(this);
		spigletCode += "JUMP " + label + "\n";
		return null;
	}

	/**
	 * f0 -> "HSTORE" f1 -> Exp() f2 -> IntegerLiteral() f3 -> Exp()
	 */
	public String visit(HStoreStmt n) {
		String exp1 = n.f1.accept(this);
		String exp2 = n.f3.accept(this);
		String integer = n.f2.accept(this);
		
		spigletCode += "HSTORE " + exp1 + " " + integer + " " + exp2 + "\n";
		
		return null;
	}

	/**
	 * f0 -> "HLOAD" f1 -> Temp() f2 -> Exp() f3 -> IntegerLiteral()
	 */
	public String visit(HLoadStmt n) {
		String temp = n.f1.accept(this);
		String exp = n.f2.accept(this);
		String integer = n.f3.accept(this);
		
		spigletCode += "HLOAD " + temp + " " + exp + " " + integer + "\n";
		return null;
	}

	/**
	 * f0 -> "MOVE" f1 -> Temp() f2 -> Exp()
	 */
	public String visit(MoveStmt n) {
		String exp = n.f2.accept(this);
		String temp = n.f1.accept(this);
		spigletCode += "MOVE " + temp + " " + exp + "\n";
		return null;
	}

	/**
	 * f0 -> "PRINT" f1 -> Exp()
	 */
	public String visit(PrintStmt n) {
		String tempForExp = n.f1.accept(this);
		spigletCode += "PRINT " + tempForExp + "\n";
		
		return null;
	}

	/**
	 * f0 -> StmtExp() | Call() | HAllocate() | BinOp() | Temp() | IntegerLiteral()
	 * | Label()
	 */
	public String visit(Exp n) {
		String exp = n.f0.choice.accept(this);
		if(exp.startsWith("TEMP ")==true) {
			return exp;
		}
		else {
			String temp = getTemp();
			spigletCode += "MOVE " + temp + " " + exp + "\n";
			return temp;
		}
	}

	/**
	 * f0 -> "BEGIN" f1 -> StmtList() f2 -> "RETURN" f3 -> Exp() f4 ->
	 * "END"
	 */
	public String visit(StmtExp n) {
		n.f1.accept(this);
		String exp = n.f3.accept(this);
		String temp = getTemp();
		spigletCode += "MOVE " + temp + " " + exp + "\n";
		return temp;
	}

	/**
	 * f0 -> "CALL" f1 -> Exp() f2 -> "(" f3 -> ( Exp() )* f4 -> ")"
	 */
	public String visit(Call n) {
		String addr = n.f1.accept(this);
		int arguNum = n.f3.size();
		Vector<String> arguList = new Vector<String>();
		arguList.setSize(arguNum);
		//记录参数
		for(int i=0;i<arguNum;++i) {
			arguList.setElementAt(n.f3.nodes.elementAt(i).accept(this), i);
		}
		//记录结果
		String valTemp = getTemp();
		spigletCode += "MOVE " + valTemp + " ";
		//执行指令
		spigletCode += "CALL " + addr + " (";
		for(int i=0;i<arguNum;++i) {
			spigletCode += arguList.elementAt(i) + " ";
		}
		spigletCode += ")\n";
		return valTemp;
	}

	/**
	 * f0 -> "HALLOCATE" f1 -> Exp()
	 */
	public String visit(HAllocate n) {
		String exp = n.f1.accept(this);
		//记录结果
		String valTemp = getTemp();
		spigletCode += "MOVE " + valTemp + " ";
		//执行指令
		spigletCode += "HALLOCATE "  + exp + "\n";
		return valTemp;
	}

	/**
	 * f0 -> Operator() f1 -> Exp() f2 -> Exp()
	 */
	public String visit(BinOp n) {
		String exp1 = n.f1.accept(this);
		String exp2 = n.f2.accept(this);
		
		//记录结果
		String valTemp = getTemp();
		spigletCode += "MOVE " + valTemp + " ";
		//执行指令
		n.f0.accept(this);
		spigletCode += " " + exp1 + " " + exp2 + "\n";
		
		return valTemp;
	}

	/**
	 * f0 -> "LT" | "PLUS" | "MINUS" | "TIMES"
	 */
	public String visit(Operator n) {
		String operator = ((NodeToken)n.f0.choice).tokenImage;
		spigletCode += operator;
		return null;
	}

	/**
	 * f0 -> "TEMP" f1 -> IntegerLiteral()
	 */
	public String visit(Temp n) {
		return "TEMP " + n.f1.f0.tokenImage;
	}

	/**
	 * f0 -> <INTEGEString_LITEStringAL>
	 */
	public String visit(IntegerLiteral n) {
		return n.f0.tokenImage;
	}

	/**
	 * f0 -> <IDENTIFIER>
	 */
	public String visit(Label n) {
		return n.f0.tokenImage;
	}

}
