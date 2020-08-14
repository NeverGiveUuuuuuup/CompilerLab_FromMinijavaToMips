package visitor;

import syntaxtree.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import errorPrinter.*;
import symbol.*;

/**
 * 有待解决的问题： 多态 
 * 
 * @author hanjiaheng
 *
 */

public class TranslateToPiglet extends GJDepthFirst<MType, MType> {
	public static String FilePath = "p.pg";
	String pigletCode;
	HashMap<String, String> currentFunctionUsedVarList = new HashMap<String, String>();//将变量与临时变量一一对应
	MClassList classList = null;

	int tempId = 21;

	String getTemp() {
		return "TEMP " + Integer.toString(tempId++);
	}

	int labelId = 0;

	String getLabel() {
		return "Label" + Integer.toString(labelId++);
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

	/*
	 * 首先考虑部分结点： MainClass MethodDeclaration
	 */
	public MType visit(Goal n, MType argu) {
		pigletCode = "";
		classList = (MClassList) argu;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		outputToFile(pigletCode, FilePath);
		return null;
	}

	/**
	 * “MAIN” StmtList “END”
	 */
	public MType visit(MainClass n, MType argu) {
		pigletCode += "MAIN\n";

		String mainClassName = ((Identifier) n.f1).f0.toString();
		MClass mainClass = (MClass) argu.containVar(mainClassName);
		MMethod mainMethod = mainClass.containMethod("main");

		currentFunctionUsedVarList.clear();
		n.f14.accept(this, mainMethod);// (varDeclaration)*
		n.f15.accept(this, mainMethod);// (statement)*

		pigletCode += "END\n\n";
		return null;
	}

	public MType visit(ClassDeclaration n, MType argu) {
		String ClassName = ((Identifier) n.f1).f0.toString();
		MClass Class = (MClass) argu.containVar(ClassName);
		n.f3.accept(this, Class);// varDeclaration
		n.f4.accept(this, Class);// methodDeclaration
		return null;
	}

	public MType visit(ClassExtendsDeclaration n, MType argu) {
		String childClassName = ((Identifier) n.f1).f0.toString();
		MClass childClass = (MClass) argu.containVar(childClassName);
		n.f5.accept(this, childClass);// varDeclaration
		n.f6.accept(this, childClass);// methodDeclaration
		return null;
	}

	public MType visit(VarDeclaration n, MType argu) {
		if (argu instanceof MMethod) {
			/*
			 * 方法内的变量声明，趁机将变量与临时变量temp绑定
			 */
			String temp = getTemp();
			String varName = n.f1.f0.toString();
			currentFunctionUsedVarList.put(varName, temp);
			pigletCode += "MOVE " + temp + " 0\n";
		}
		return null;
	}

	/**
	 * Label “[” IntegerLiteral “]”"BEGIN" StmtList "RETURN" Exp "END"
	 */
	public MType visit(MethodDeclaration n, MType argu) {
		/*
		 * argu必须是提供所在类信息
		 */
		currentFunctionUsedVarList.clear();

		String className = argu.kind;
		MClass methodClass = (MClass) argu;

		String methodName = ((Identifier) n.f2).f0.toString();
		MMethod method = (MMethod) methodClass.containMethod(methodName);
		String newMethodName = className + "_" + methodName;

		int parameterNum = method.getParamNum() + 1;
		if(parameterNum > 19) {
			parameterNum = 20;
		}
		pigletCode += newMethodName + "[" + Integer.toString(parameterNum) + "]\n";
		pigletCode += "BEGIN\n";
		
		n.f4.accept(this, method);// FormalParameterList 将参数绑定
		n.f7.accept(this, method);// ( VarDeclaration() )*
		n.f8.accept(this, method);// ( Statement() )*

		pigletCode += "RETURN ";
		n.f10.accept(this, method);// return expression;
		pigletCode += "\n";

		pigletCode += "END\n\n";

		return null;
	}

	int paraIndex = 1;//不从0开始，是因为规定第0项为属性表地址

	public MType visit(FormalParameterList n, MType argu) {
		paraIndex = 1;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		return null;
	}

	public MType visit(FormalParameter n, MType argu) {
		// 按照顺序将参数与传递参数的临时变量进行绑定，记录在currentFunctionUsedVarList中
		String temp = "";
		if(paraIndex < 19) {
			temp = "TEMP " + Integer.toString(paraIndex);
		}
		else {
			//参数大于18个，需要从内存中加载到临时变量
			temp = getTemp();
			String offset = Integer.toString(4 * (paraIndex - 19));
			pigletCode += "HLOAD " + temp +  " TEMP 19 " + offset + "\n";
		}
		String varName = n.f1.f0.toString();
		currentFunctionUsedVarList.put(varName, temp);
		//System.out.println(paraIndex + ": " + varName);
		++paraIndex;
		return null;
	}

	public MType visit(IfStatement n, MType argu) {
		String label_2 = getLabel();
		String label_end = getLabel();

		pigletCode += "CJUMP ";
		n.f2.accept(this, argu);
		pigletCode += " " + label_2 + "\n";

		n.f4.accept(this, argu);// else statement
		pigletCode += "JUMP " + label_end + "\n";

		pigletCode += label_2 + "\n";
		n.f6.accept(this, argu);

		pigletCode += label_end + " NOOP\n";
		return null;
	}

	public MType visit(WhileStatement n, MType argu) {
		String label_condition = getLabel();
		String label_end = getLabel();

		pigletCode += label_condition + "\n";
		pigletCode += "CJUMP ";
		n.f2.accept(this, argu);
		pigletCode += " " + label_end + "\n";

		n.f4.accept(this, argu);
		pigletCode += "JUMP " + label_condition + "\n";

		pigletCode += label_end + " NOOP\n";
		return null;
	}

	public MType visit(PrintStatement n, MType argu) {
		pigletCode += "PRINT ";
		n.f2.accept(this, argu);
		pigletCode += "\n";
		return null;
	}

	public MType visit(AssignmentStatement n, MType argu) {
		String leftValueName = n.f0.f0.toString();//identifier
		MVar leftValue = (MVar) argu.containVar(leftValueName);
		//类的字段的赋值涉及到了对属性表的修改（HSTORE)
		if (leftValue.isClassVar == true) {
			MClass varClass = ((MMethod) argu).methodClass;
			String varOffset = Integer.toString(4 * (varClass.getVarIndex(leftValueName) + 1));
			String temp;
			if (currentFunctionUsedVarList.containsKey(leftValueName) == false) {
				temp = getTemp();
				currentFunctionUsedVarList.put(leftValueName, temp);
				pigletCode += "HLOAD " + temp + " TEMP 0 " + varOffset + "\n";
			} else {
				temp = currentFunctionUsedVarList.get(leftValueName);
			}
			pigletCode += "MOVE " + temp + " ";
			n.f2.accept(this, argu);// expression
			pigletCode += "\n";
			pigletCode += "HSTORE TEMP 0 " + varOffset + " " + temp + "\n";

		} else {
			pigletCode += "MOVE ";
			n.f0.accept(this, argu);// identifier
			pigletCode += " ";
			n.f2.accept(this, argu);// expression
			pigletCode += "\n";
		}

		return null;
	}

	public MType visit(Expression n, MType argu) {
		return n.f0.accept(this, argu);
	}

	public MType visit(PrimaryExpression n, MType argu) {
		return n.f0.accept(this, argu);
	}

	/**
	 * 暂不处理
	 */
	public MType visit(Identifier n, MType argu) {
		if (argu == null) {
			return null;
		}
		String name = n.f0.toString();
		MType id = argu.containVar(name);
		
		if (id instanceof MVar) {
			if (((MVar) id).varType == null) {
				ErrorPrinter.addErrorMsg("unrecognizable identifier");
			}
			if (((MVar) id).isClassVar == true && currentFunctionUsedVarList.containsKey(name) == false) {
				MClass varClass = ((MMethod)argu).methodClass;
				String temp = getTemp();
				String varOffset = Integer.toString(4 * (varClass.getVarIndex(name) + 1));

				currentFunctionUsedVarList.put(name, temp);

				pigletCode += "\nBEGIN\n";
				pigletCode += "HLOAD " + temp + " TEMP 0 " + varOffset + "\n";
				pigletCode += "RETURN " + temp + "\n";
				pigletCode += "END\n";

			} else {
				String temp = currentFunctionUsedVarList.get(name);
				pigletCode += temp;
			}
			return ((MVar) id).varType;

		}
		if (id instanceof MMethod) {
			return id;
		}
		if (id instanceof MClass) {
			return id;
		}
		return null;

	}

	public MType visit(IntegerLiteral n, MType argu) {
		String i = n.f0.tokenImage;// f0 is nodeToken
		pigletCode += i;
		return new MType("int");
	}

	public MType visit(TrueLiteral n, MType argu) {
		String i = Integer.toString(1);
		pigletCode += i;
		return new MType("boolean");
	}

	public MType visit(FalseLiteral n, MType argu) {
		String i = Integer.toString(0);
		pigletCode += i;
		return new MType("boolean");
	}

	public MType visit(AllocationExpression n, MType argu) {
		String _ret = "";

		String ClassName = ((Identifier) n.f1).f0.toString();
		MClass Class = (MClass) classList.containVar(ClassName);
		HashMap<String, MVar> varList = Class.mVarTable;
		HashMap<String, MMethod> methodList = Class.mMethodTable;
		
		Vector<String> dtable = Class.fullnameDTable;
		Vector<String> vtable = Class.vtable;
		
		String dTableAddr = getTemp();
		String vTableAddr = getTemp();
		String vTableSize = Integer.toString(4 * (vtable.size() + 1));
		String dTableSize = Integer.toString(4 * dtable.size());

		_ret += "BEGIN\n";
		_ret += "MOVE " + dTableAddr + " HALLOCATE " + dTableSize + "\n";
		_ret += "MOVE " + vTableAddr + " HALLOCATE " + vTableSize + "\n";
		_ret += "HSTORE " + vTableAddr + " 0 " + dTableAddr + "\n";
		int i = 0;
		for (; i < vtable.size();) {
			_ret += "HSTORE " + vTableAddr + " " + Integer.toString(4 * (++i)) + " " + "0" + "\n";
		}
		i = 0;
		for(;i < dtable.size();) {
			//String newMethodName = ClassName + "_" + dtable.elementAt(i);
			String newMethodName = dtable.elementAt(i);
			_ret += "HSTORE " + dTableAddr + " " + Integer.toString(4 * (i++)) + " " + newMethodName + "\n";
			
		}
		
		_ret += "RETURN " + vTableAddr + "\n";
		_ret += "END\n";

		pigletCode += _ret;

		MType newClassType = (n.f1.accept(this, classList));
		return newClassType;
	}

	public MType visit(MessageSend n, MType argu) {
		pigletCode += "BEGIN\n";
		/*
		// 0. 在调用之前应该先保存临时变量
		int targetTempIndex = tempId;
		for (int i = 0; i < 19; ++i) {
			pigletCode += "MOVE " + getTemp() + " TEMP " + Integer.toString(i) + "\n";
		}
		*/
		// 1. 将call的返回值保存在临时变量中
		String tempForReturnValue = getTemp();
		pigletCode += "MOVE " + tempForReturnValue + " CALL\n";
		// 1.0 确定调用方法的地址
		pigletCode += "BEGIN\n";
		String callerAddr = getTemp();
		pigletCode += "MOVE " + callerAddr + " ";
		String callerType = n.f0.accept(this, argu).kind;
		pigletCode += "\n";
		MClass caller = (MClass) classList.containVar(callerType);

		String methodName = ((Identifier) n.f2).f0.toString();
		MMethod method = (MMethod) caller.containMethod(methodName);

		String dTableAddr = getTemp();
		String methodAddr = getTemp();
		pigletCode += "HLOAD " + dTableAddr + " " + callerAddr + " " + "0" + "\n";

		String methodIndex = Integer.toString(4 * (caller.getMethodIndex(methodName)));//是该类的第几个方法
		pigletCode += "HLOAD " + methodAddr + " " + dTableAddr + " " + methodIndex + "\n";
		
		pigletCode += "RETURN " + methodAddr + "\n";
		pigletCode += "END\n";
		
		// 1.2参数列表
		pigletCode += "( ";
		pigletCode += callerAddr;
		int tempForParaNum = paraNum, tempForExpressionIndex= expressionIndex;
		String temp = tempForTemp19;
		paraNum = method.getParamNum();
		expressionIndex = 1;
		tempForTemp19 = "";
		n.f4.accept(this, argu);
		paraNum = tempForParaNum;
		expressionIndex = tempForExpressionIndex;
		tempForTemp19 = temp;
		pigletCode += ")\n";
		/*
		// 2. 恢复临时变量
		for (int i = 0; i < 20; ++i) {
			pigletCode += "MOVE TEMP " + Integer.toString(i) + " TEMP " + Integer.toString(targetTempIndex + i) + "\n";
		}
		*/
		// 3. 返回调用结果
		pigletCode += "RETURN " + tempForReturnValue + "\nEND\n";
		return method.returnType;
	}
	int expressionIndex = 1;
	int paraNum = 1;
	String tempForTemp19;
	public MType visit(ExpressionList n, MType argu) {
		pigletCode += " ";
		n.f0.accept(this, argu);// expression
		expressionIndex ++;
		n.f1.accept(this, argu);// expressionRest
		return null;
	}
	//ExpressionList ::= Expression ( ExpressionRest )*
	//ExpressionRest ::= "," Expression
	public MType visit(ExpressionRest n, MType argu) {
		
		//System.out.println(expressionIndex);
		if(expressionIndex <= 18) {
			pigletCode += " ";
			n.f1.accept(this, argu);// expression
		}
		else {
			
			if(expressionIndex == 19) {
				pigletCode += " ";
				pigletCode += "BEGIN\n";
				tempForTemp19 = getTemp();
				//System.out.println("method.getParamNum(): " + method.getParamNum());
				String remainArguNum = Integer.toString(4 * (paraNum - 18));
				pigletCode += "MOVE " + tempForTemp19 + " HALLOCATE " + remainArguNum + "\n";
				
			}
			String offset = Integer.toString(4 * (expressionIndex - 19));
			pigletCode += "HSTORE " + tempForTemp19 + " " + offset + " ";
			n.f1.accept(this, argu);// expression
			pigletCode += "\n";
			if(paraNum == expressionIndex) {
				pigletCode += "RETURN " +  tempForTemp19 + "\n";
				pigletCode += "END\n";
			}	
		}
		expressionIndex ++;
		return null;
	}

	public MType visit(ThisExpression n, MType argu) {
		pigletCode += "TEMP 0";
		return ((MMethod) argu).methodClass;
	}

	public MType visit(NotExpression n, MType argu) {
		pigletCode += "MINUS 1 ";
		n.f1.accept(this, argu);
		return new MType("boolean");
	}

	public MType visit(AndExpression n, MType argu) {
		pigletCode += "LT 1 PLUS ";
		n.f0.accept(this, argu);
		pigletCode += " ";
		n.f2.accept(this, argu);
		return new MType("boolean");
	}

	public MType visit(CompareExpression n, MType argu) {
		pigletCode += "LT ";
		n.f0.accept(this, argu);
		pigletCode += " ";
		n.f2.accept(this, argu);
		return new MType("boolean");
	}

	public MType visit(PlusExpression n, MType argu) {
		pigletCode += "PLUS ";
		n.f0.accept(this, argu);
		pigletCode += " ";
		n.f2.accept(this, argu);
		return new MType("int");
	}

	public MType visit(MinusExpression n, MType argu) {
		pigletCode += "MINUS ";
		n.f0.accept(this, argu);
		pigletCode += " ";
		n.f2.accept(this, argu);
		return new MType("int");
	}

	public MType visit(TimesExpression n, MType argu) {
		pigletCode += "TIMES ";
		n.f0.accept(this, argu);
		pigletCode += " ";
		n.f2.accept(this, argu);
		return new MType("int");
	}
	
	public MType visit(BracketExpression n, MType argu) {
		return n.f1.accept(this, argu);
	}

	public MType visit(ArrayAllocationExpression n, MType argu) {
		pigletCode += "BEGIN\n";

		String tempForLength = getTemp();
		pigletCode += "MOVE " + tempForLength + " ";
		n.f3.accept(this, argu);
		pigletCode += "\n";

		String tempForHeader = getTemp();
		pigletCode += "MOVE " + tempForHeader + " HALLOCATE TIMES 4 PLUS 1" + tempForLength + "\n";
		pigletCode += "HSTORE " + tempForHeader + " 0 " + tempForLength + "\n";
		pigletCode += "RETURN " + tempForHeader + "\n";

		pigletCode += "END\n";

		return new MType("array");
	}

	public MType visit(ArrayAssignmentStatement n, MType argu) {
		pigletCode += "HSTORE ";
		
		pigletCode += "PLUS ";
		n.f0.accept(this, argu);//identifier, 返回一个temp，其中存放的是header的地址
		pigletCode += " TIMES 4 ";
		n.f2.accept(this, argu);//expression, 数组下标
		
		pigletCode += " 4 ";//在根据下标和基址计算的地址上，再加4，因为第一项被占用了
		n.f5.accept(this, argu);
		pigletCode += "\n";
		
		return new MType("int");
	}

	public MType visit(ArrayLookup n, MType argu) {
		pigletCode += "BEGIN\n";
		String tempForValue = getTemp();
		
		pigletCode += "HLOAD ";
		pigletCode += tempForValue + " ";
		
		pigletCode += "PLUS ";
		n.f0.accept(this, argu);//primary expression, 返回temp，存放基址的值
	    pigletCode += " TIMES 4 ";
	    n.f2.accept(this, argu);//primary expression, 返回temp，存放下标的值
	    
	    pigletCode += " 4\n";
		
		pigletCode += "RETURN " + tempForValue + "\n";
		pigletCode += "END\n";
		
		return new MType("int");
	}

	public MType visit(ArrayLength n, MType argu) {
		pigletCode += "BEGIN\n";
		String tempForLength = getTemp();
		
		pigletCode += "HLOAD ";
		pigletCode += tempForLength + " ";
		n.f0.accept(this, argu);//primary expression, 返回temp，存放基址的值
		pigletCode += " 0\n";//第一项存放的就是数组长度
		
		pigletCode += "RETURN " + tempForLength + "\n";
		pigletCode += "END\n";
		
		return new MType("int");
	}

	
	/**
	 * 待完成
	 */

}
