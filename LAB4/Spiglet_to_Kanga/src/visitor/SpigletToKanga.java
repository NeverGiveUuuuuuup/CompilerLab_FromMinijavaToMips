package visitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import symbol.*;
import syntaxtree.*;

public class SpigletToKanga extends GJDepthFirst<MySpigletType, MySpigletType> {
	MyGoal mygoal;
	MyProcedure proc;
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
	
	public MySpigletType visit(Goal n, MySpigletType argu) {
		mygoal = new MyGoal();
		n.accept((new BasicBlockAnalysis()), mygoal);
		n.accept((new LivenessAnalysis()), mygoal);
		n.accept(new RegAllocationAnalysis(), mygoal);
		
		code = "";
		proc = mygoal.getProcess("MAIN");
		System.out.println("MAIN: begin outputing code");
		code += proc.name+"["+proc.paranum+"]"+"["+proc.spillnum+"]"+"["+proc.maxparanum+"]"+"\n";
		n.f1.accept(this, proc);
		code += "END\n";
		
		n.f3.accept(this, mygoal);
		// 输出到文件
		outputToFile(code, FilePath);
		return mygoal;
	}
	
	public MySpigletType visit(Procedure n, MySpigletType argu) {
		String procname = n.f0.f0.tokenImage;
		proc = mygoal.getProcess(procname);
		System.out.println(procname+": begin outputing code");
		code += proc.name+"["+proc.paranum+"]"+"["+proc.spillnum+"]"+"["+proc.maxparanum+"]"+"\n";
		n.f4.accept(this, proc);
		code += "END\n";
		return null;
	}
	
	public MySpigletType visit(StmtExp n, MySpigletType argu) {
		n.f1.accept(this, argu);
		return null;
	}
	
	public MySpigletType visit(StmtList n, MySpigletType argu) {
		MyStmt mystmt;
		
		for(int i=0;i<proc.stmtlen;++i) {
			mystmt = proc.stmtlist.elementAt(i);
			int prestmtnum = mystmt.prestmts.size();
			int backstmtnum = mystmt.backstmts.size();
			for(int j=0;j<prestmtnum;++j) {
				code += mystmt.prestmts.elementAt(j).getCode();
			}
			n.f0.nodes.elementAt(i).accept(this, mystmt);
			for(int j=0;j<backstmtnum;++j) {
				code += mystmt.backstmts.elementAt(j).getCode();
			}
		}
		return null;
	}
	

	public MySpigletType visit(NoOpStmt n, MySpigletType argu) {
		code += "NOOP\n";
		return null;
	}
	
	public MySpigletType visit(ErrorStmt n, MySpigletType argu) {
		code += "ERROR\n";
		return null;
	}
	
	public MySpigletType visit(CJumpStmt n, MySpigletType argu) {
		code += "CJUMP ";
		n.f1.accept(this, argu);
		code += " ";
		n.f2.accept(this, argu);
		code += "\n";
		return null;
	}
	
	public MySpigletType visit(JumpStmt n, MySpigletType argu) {
		code += "JUMP ";
		n.f1.accept(this, argu);
		code += "\n";
		return null;
	}
	
	public MySpigletType visit(HStoreStmt n, MySpigletType argu) {
		code += "HSTORE ";
		n.f1.accept(this, argu);
		code +=  " " + n.f2.f0.tokenImage + " ";
		n.f3.accept(this, argu);
		code += "\n";
		return null;
	}
	
	public MySpigletType visit(HLoadStmt n, MySpigletType argu) {
		code += "HLOAD ";
		n.f1.accept(this, argu);
		code += " ";
		n.f2.accept(this, argu);
		code +=  " " + n.f3.f0.tokenImage;
		code += "\n";
		return null;
	}
	
	public MySpigletType visit(MoveStmt n, MySpigletType argu) {
		code += "MOVE ";
		n.f1.accept(this, argu);
		code += " ";
		//TODO exp对应于哪一个temp?
		n.f2.accept(this, argu);
		code += "\n";
		return null;
	}
	
	public MySpigletType visit(Call n, MySpigletType argu) {
		//CALL应该在move指令的前面，单独成一条指令。所以这里就不用了
		code += "v0";
		return null;
	}
	
	public MySpigletType visit(PrintStmt n, MySpigletType argu) {
		code += "PRINT ";
		n.f1.accept(this, argu);
		code += "\n";
		return null;
	}
	
	public MySpigletType visit(HAllocate n, MySpigletType argu) {
		code += "HALLOCATE ";
		n.f1.accept(this, argu);
		return null;
	}
	
	public MySpigletType visit(BinOp n, MySpigletType argu) { 
		n.f0.accept(this, argu);
		code += " ";
		n.f1.accept(this, argu);
		code += " ";
		n.f2.accept(this, argu);
		return null;
	}
	
	public MySpigletType visit(Operator n, MySpigletType argu) {
		code += ((NodeToken)n.f0.choice).tokenImage;
		return null;
	}
	
	public MySpigletType visit(Temp n, MySpigletType argu) {
		MyStmt mystmt = (MyStmt)argu;
		
		Integer temp = new Integer(n.f1.f0.tokenImage);
		if(mystmt.stmtregs==null) {
			System.out.println(mystmt.index);
			System.out.println(mystmt.type);
			System.out.println("temp"+temp);
		}
		Integer reg = mystmt.stmtregs.get(temp);
		if(reg==null) {
			System.out.println(mystmt.index);
			System.out.println(mystmt.type);
			System.out.println("temp"+temp);
		}
		code += RegDescriptor.getRegName(reg);
		return null;
	}
	
	public MySpigletType visit(Label n, MySpigletType argu) {
		code += n.f0.tokenImage + " ";
		return null;
	}
	
	public MySpigletType visit(IntegerLiteral n, MySpigletType argu) {
		code += n.f0.tokenImage;
		return null;
	}
	
}
