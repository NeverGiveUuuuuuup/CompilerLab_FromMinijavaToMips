package visitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import syntaxtree.*;

public class KangaToMips extends GJDepthFirst<String, String> {

	int x, y, z;
	static String FilePath;
	String code;

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

	int getSpOffset(boolean ismain) {
		int offset = 0;
		if (x <= 4) {
			offset = y;
		} else {
			offset = y - (x - 4);
		}
		offset += Math.max(0, z - 4);
		if (ismain == false) {
			offset += 2;
		} else {
			offset += 1;
		}
		offset = offset * 4;
		return offset;
	}

	String prochead(String name, boolean ismain) {
		String _ret = "";
		code += "         .text\r\n" + "         .globl " + name + "\r\n" + name + ":\r\n";
		if (ismain == false) {
			code += "sw $fp, -8($sp)\n";
		}
		code += "move $fp, $sp\n";
		int offset = getSpOffset(ismain);
		code += "subu $sp, $sp, " + Integer.toString(offset) + "\n";
		code += "sw $ra, -4($fp)\n";

		return _ret;
	}

	String procend(boolean ismain) {
		String _ret = "";
		int offset = getSpOffset(ismain);
		code += "lw $ra, -4($fp)\n";
		if (ismain == false) {
			code += "lw $fp, " + Integer.toString(offset - 8) + "($sp)\n";
		}
		code += "addu $sp, $sp, " + Integer.toString(offset) + "\n";
		code += "j $ra\n\n";
		return _ret;
	}

	String helpfunc() {
		String _ret = "";
		code += "         .text\r\n" + "         .globl _halloc\r\n" + "_halloc:\r\n" + "         li $v0, 9\r\n"
				+ "         syscall\r\n" + "         j $ra\r\n" + "\r\n" + "         .text\r\n"
				+ "         .globl _print\r\n" + "_print:\r\n" + "         li $v0, 1\r\n" + "         syscall\r\n"
				+ "         la $a0, newl\r\n" + "         li $v0, 4\r\n" + "         syscall\r\n" + "         j $ra\r\n"
				+ "\r\n" + "         .data\r\n" + "         .align   0\r\n" + "newl:    .asciiz \"\\n\" \r\n"
				+ "         .data\r\n" + "         .align   0\r\n"
				+ "str_er:  .asciiz \" ERROR: abnormal termination\\n\" \r\n" + "         .data\r\n"
				+ "         .align   0\r\n" + "str_kger:  .asciiz \" ERROR: syntax error in kanga code\\n\" \r\n";
		return _ret;
	}

	/**
	 * f0 -> "MAIN" f1 -> "[" f2 -> IntegerLiteral() f3 -> "]" f4 -> "[" f5 ->
	 * IntegerLiteral() f6 -> "]" f7 -> "[" f8 -> IntegerLiteral() f9 -> "]" f10 ->
	 * StmtList() f11 -> "END" f12 -> ( Procedure() )* f13 -> <EOF>
	 */
	public String visit(Goal n, String argu) {
		String _ret = "";
		code = "";
		x = Integer.parseInt(n.f2.accept(this, argu));
		y = Integer.parseInt(n.f5.accept(this, argu));
		z = Integer.parseInt(n.f8.accept(this, argu));

		prochead("main", true);
		n.f10.accept(this, argu);
		procend(true);

		n.f12.accept(this, argu);
		helpfunc();

		// 输出到文件
		outputToFile(code, FilePath);
		return _ret;
	}

	/**
	 * f0 -> Label() f1 -> "[" f2 -> IntegerLiteral() f3 -> "]" f4 -> "[" f5 ->
	 * IntegerLiteral() f6 -> "]" f7 -> "[" f8 -> IntegerLiteral() f9 -> "]" f10 ->
	 * StmtList() f11 -> "END"
	 */
	public String visit(Procedure n, String argu) {
		String _ret = "";
		String name = n.f0.accept(this, argu);
		x = Integer.parseInt(n.f2.accept(this, argu));
		y = Integer.parseInt(n.f5.accept(this, argu));
		z = Integer.parseInt(n.f8.accept(this, argu));

		prochead(name, false);
		n.f10.accept(this, argu);
		procend(false);

		return _ret;
	}

	/**
	 * f0 -> ( ( Label() )? Stmt() )*
	 */
	public String visit(StmtList n, String argu) {
		String _ret = "";
		Vector<Node> nodes = n.f0.nodes;
		int stmtlen = nodes.size();
		for (int i = 0; i < stmtlen; ++i) {
			NodeSequence stmt = (NodeSequence) nodes.elementAt(i);
			NodeOptional label = (NodeOptional) stmt.elements().nextElement();
			// Label要翻译成Label:
			if (label.present() == true) {
				code += label.accept(this, argu) + ": ";
			}
			stmt.accept(this, argu);
		}
		return _ret;
	}

	/**
	 * f0 -> NoOpStmt() | ErrorStmt() | CJumpStmt() | JumpStmt() | HStoreStmt() |
	 * HLoadStmt() | MoveStmt() | PrintStmt() | ALoadStmt() | AStoreStmt() |
	 * PassArgStmt() | CallStmt()
	 */
	public String visit(Stmt n, String argu) {
		String _ret = "";
		n.f0.accept(this, argu);
		code += "\n";
		return _ret;
	}

	/**
	 * f0 -> "NOOP"
	 */
	public String visit(NoOpStmt n, String argu) {
		String _ret = "";
		code += "nop";
		return _ret;
	}

	/**
	 * f0 -> "ERROR"
	 */
	public String visit(ErrorStmt n, String argu) {
		String _ret = "";
		code += "         li $v0, 4\r\n" + "         la $a0, str_er\r\n" + "         syscall\r\n"
				+ "         li $v0, 10\r\n" + "         syscall\n";
		return _ret;
	}

	/**
	 * f0 -> "CJUMP" f1 -> Reg() f2 -> Label()
	 */
	public String visit(CJumpStmt n, String argu) {
		String _ret = "";
		String reg = n.f1.accept(this, argu);
		String label = n.f2.accept(this, argu);
		code += "beqz " + reg + " " + label;
		return _ret;
	}

	/**
	 * f0 -> "JUMP" f1 -> Label()
	 */
	public String visit(JumpStmt n, String argu) {
		String _ret = "";
		String label = n.f1.accept(this, argu);
		code += "b " + label;
		return _ret;
	}

	/**
	 * f0 -> "HSTORE" f1 -> Reg() f2 -> IntegerLiteral() f3 -> Reg()
	 */
	public String visit(HStoreStmt n, String argu) {
		String _ret = "";
		String r1 = n.f1.accept(this, argu);
		String r2 = n.f3.accept(this, argu);
		String integer = n.f2.accept(this, argu);
		// HSTORE t0 0 t2 ——> sw $t2, 0($t0)
		code += "sw " + r2 + ", " + integer + "(" + r1 + ")";
		return _ret;
	}

	/**
	 * f0 -> "HLOAD" f1 -> Reg() f2 -> Reg() f3 -> IntegerLiteral()
	 */
	public String visit(HLoadStmt n, String argu) {
		String _ret = "";
		// HLOAD t1 t0 0 ——> lw $t1 0($t0)
		String r1 = n.f1.accept(this, argu);
		String r2 = n.f2.accept(this, argu);
		String integer = n.f3.accept(this, argu);
		code += "lw " + r1 + " " + integer + "(" + r2 + ")";
		return _ret;
	}

	/**
	 * f0 -> "MOVE" f1 -> Reg() f2 -> Exp()
	 */
	public String visit(MoveStmt n, String argu) {
		String _ret = "";
		String reg = n.f1.accept(this, argu);
		n.f2.accept(this, reg);
		return _ret;
	}

	/**
	 * f0 -> "PRINT" f1 -> SimpleExp()
	 */
	public String visit(PrintStmt n, String argu) {
		String _ret = "";
		n.f1.accept(this, "$a0");
		code += "\n";
		code += "jal _print";
		return _ret;
	}

	/**
	 * f0 -> "ALOAD" f1 -> Reg() f2 -> SpilledArg()
	 */
	public String visit(ALoadStmt n, String argu) {
		String _ret = "";
		String reg = n.f1.accept(this, argu);
		code += "lw " + reg + ", ";
		n.f2.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "ASTORE" f1 -> SpilledArg() f2 -> Reg()
	 */
	public String visit(AStoreStmt n, String argu) {
		String _ret = "";
		String reg = n.f2.accept(this, argu);
		_ret = "sw " + reg + ", ";
		code += _ret;
		n.f1.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "PASSARG" f1 -> IntegerLiteral() f2 -> Reg()
	 */
	public String visit(PassArgStmt n, String argu) {
		String _ret = "";
		String reg = n.f2.accept(this, argu);
		int offset = 4 * Integer.parseInt(n.f1.accept(this, argu)) - 4;
		_ret = "sw " + reg + ", " + offset + "($sp)";
		code += _ret;
		return _ret;
	}

	/**
	 * f0 -> "CALL" f1 -> SimpleExp()
	 */
	public String visit(CallStmt n, String argu) {
		String _ret = "";

		int sexptype = getSimpleExpType(n.f1);
		if (sexptype == 1) {
			String reg = ((Reg) n.f1.f0.choice).accept(this, argu);
			_ret = "jalr " + reg;
		} else if (sexptype == 3) {
			String label = ((Label) n.f1.f0.choice).accept(this, argu);
			_ret = "jal " + label;
		} else if (sexptype == 2) {
			code += "         li $v0, 4\r\n" + "         la $a0, str_kger\r\n" + "         syscall\r\n"
					+ "         li $v0, 10\r\n" + "         syscall\n";
		}
		code += _ret;
		return _ret;
	}

	/**
	 * f0 -> HAllocate() | BinOp() | SimpleExp()
	 */
	public String visit(Exp n, String argu) {
		String _ret = "";
		n.f0.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "HALLOCATE" f1 -> SimpleExp()
	 */
	public String visit(HAllocate n, String argu) {
		String _ret = "";
		n.f1.accept(this, "$a0");
		_ret += "\n";
		_ret += "jal _halloc\n";
		_ret += "move " + argu + " $v0";
		code += _ret;
		return _ret;
	}

	/**
	 * f0 -> Operator() f1 -> Reg() f2 -> SimpleExp()
	 */
	public String visit(BinOp n, String argu) {
		String _ret = "";
		int optype = getOpType(n.f0);
		int exptype = getSimpleExpType(n.f2);
		String[] mipsOpType = { "", "slt", "add", "sub", "mul" };
		String s1 = n.f1.accept(this, argu);
		if (exptype == 1) {
			String s2 = ((Reg) n.f2.f0.choice).accept(this, argu);
			_ret += mipsOpType[optype] + " " + argu + ", " + s1 + ", " + s2;
		} else if (exptype == 2) {
			String s2 = ((IntegerLiteral) n.f2.f0.choice).accept(this, argu);
			_ret += mipsOpType[optype] + " " + argu + ", " + s1 + ", " + s2;
		} else if (exptype == 3) {
			
			code += "         li $v0, 4\r\n" + "         la $a0, str_kger\r\n" + "         syscall\r\n"
					+ "         li $v0, 10\r\n" + "         syscall\n";
		}
		code += _ret;
		return _ret;
	}

	/**
	 * operator -> "LT" | "PLUS" | "MINUS" | "TIMES"
	 */

	/**
	 * f0 -> "SPILLEDARG" f1 -> IntegerLiteral()
	 */
	public String visit(SpilledArg n, String argu) {
		String _ret = "";
		int offset = Math.max(0, z - 4);
		String reg = null;
		int i = Integer.parseInt(n.f1.accept(this, argu));
		if (x <= 4) {
			offset += i;
			reg = "$sp";
		} else if (i + 4 < x) {
			offset = i;
			reg = "$fp";
		} else {
			offset += i + 4 - x;
			reg = "$sp";
		}
		_ret = Integer.toString(4 * offset) + "(" + reg + ")";
		code += _ret;
		return _ret;
	}

	/**
	 * f0 -> Reg() | IntegerLiteral() | Label()
	 */
	public String visit(SimpleExp n, String argu) {
		// 返回值如何设
		String _ret = "";
		int exptype = getSimpleExpType(n);
		String[] op = { "", "move", "li", "la" };
		_ret += op[exptype] + " " + argu + " " + n.f0.accept(this, argu);
		code += _ret;
		return _ret;
	}

	/**
	 * f0 -> "a0" | "a1" | "a2" | "a3" | "t0" | "t1" | "t2" | "t3" | "t4" | "t5" |
	 * "t6" | "t7" | "s0" | "s1" | "s2" | "s3" | "s4" | "s5" | "s6" | "s7" | "t8" |
	 * "t9" | "v0" | "v1"
	 */
	public String visit(Reg n, String argu) {
		String _ret = "$" + ((NodeToken) n.f0.choice).tokenImage;
		return _ret;
	}

	/**
	 * f0 -> <INTEGER_LITERAL>
	 */
	public String visit(IntegerLiteral n, String argu) {
		String _ret = n.f0.tokenImage;
		return _ret;
	}

	/**
	 * f0 -> <IDENTIFIER>
	 */
	public String visit(Label n, String argu) {
		String _ret = n.f0.tokenImage;
		return _ret;
	}

	int getOpType(Operator n) {
		int optype = -1;
		String op = ((NodeToken) n.f0.choice).tokenImage;
		if (op == "LT") {
			optype = 1;
		} else if (op == "PLUS") {
			optype = 2;
		} else if (op == "MINUS") {
			optype = 3;
		} else if (op == "TIMES") {
			optype = 4;
		}
		return optype;
	}

	int getExpType(Exp n) {
		int choice = -1;
		if (n.f0.choice instanceof HAllocate) {
			choice = 1;
		} else if (n.f0.choice instanceof BinOp) {
			choice = 2;
		} else if (n.f0.choice instanceof SimpleExp) {
			choice = 3;
		}
		return choice;
	}

	int getSimpleExpType(SimpleExp n) {
		int choice = -1;
		if (n.f0.choice instanceof Reg) {
			choice = 1;
		} else if (n.f0.choice instanceof IntegerLiteral) {
			choice = 2;
		} else if (n.f0.choice instanceof Label) {
			choice = 3;
		}
		return choice;
	}
}
