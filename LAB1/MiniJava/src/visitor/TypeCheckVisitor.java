package visitor;

import syntaxtree.*;
import java.util.Vector;

import errorPrinter.*;
import symbol.*;

public class TypeCheckVisitor extends GJDepthFirst<MType, MType> {
	MClassList classList = null;
	/**
	 * f0 -> MainClass() f1 -> ( TypeDeclaration() )* f2 -> <EOF>
	 */
	public MType visit(Goal n, MType argu) {
		this.classList = (MClassList) argu;
		//继承检查
		new InheritCheck(classList).check();
		//未定义检查
		n.accept(new UndefinedVarCheckVisitor(), classList);
		//类型检查
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		return null;
	}

	/**
	 * f0 -> "class" f1 -> Identifier() f2 -> "{" f3 -> "public" f4 -> "static" f5
	 * -> "void" f6 -> "main" f7 -> "(" f8 -> "String" f9 -> "[" f10 -> "]" f11 ->
	 * Identifier() f12 -> ")" f13 -> "{" f14 -> ( VarDeclaration() )* f15 -> (
	 * Statement() )* f16 -> "}" f17 -> "}"
	 */
	public MType visit(MainClass n, MType argu) {
		MClass mainClass = (MClass) n.f1.accept(this, argu);
		MMethod mainMethod = mainClass.containMethod("main");
		n.f15.accept(this, mainMethod);
		return null;
	}

	/**
	 * f0 -> "class" f1 -> Identifier() f2 -> "{" f3 -> ( VarDeclaration() )* f4 ->
	 * ( MethodDeclaration() )* f5 -> "}"
	 */
	public MType visit(ClassDeclaration n, MType argu) {
		MType thisClass = (MClass) (n.f1.accept(this, argu));
		n.f4.accept(this, thisClass);
		return null;
	}

	/**
	 * f0 -> "class" f1 -> Identifier() f2 -> "extends" f3 -> Identifier() f4 -> "{"
	 * f5 -> ( VarDeclaration() )* f6 -> ( MethodDeclaration() )* f7 -> "}"
	 */
	public MType visit(ClassExtendsDeclaration n, MType argu) {
		MType thisClass = (MClass) (n.f1.accept(this, argu));
		n.f6.accept(this, thisClass);
		return null;

	}

	/**
	 * f0 -> "public" f1 -> Type() f2 -> Identifier() f3 -> "(" f4 -> (
	 * FormalParameterList() )? f5 -> ")" f6 -> "{" f7 -> ( VarDeclaration() )* f8
	 * -> ( Statement() )* f9 -> "return" f10 -> Expression() f11 -> ";" f12 -> "}"
	 */

	public MType visit(MethodDeclaration n, MType argu) {
		String methodName = n.f2.f0.toString();
		MMethod thisMethod = ((MClass) argu).containMethod(methodName);
		n.f8.accept(this, thisMethod);
		//检查返回值类型是否匹配
		MType returnExpressionType = n.f10.accept(this, thisMethod);
		if(returnExpressionType.isTypeOf(thisMethod.returnType) == false){
			ErrorPrinter.addErrorMsg("Return Type error");
		}
		return null;
	}

	/**
	 * f0 -> Identifier() f1 -> "=" f2 -> Expression() f3 -> ";"
	 */
	public MType visit(AssignmentStatement n, MType argu) {
		MType leftValue = n.f0.accept(this, argu);
		n.f1.accept(this, null);
		MType rightValue = n.f2.accept(this, argu);
		n.f3.accept(this, null);

		if (rightValue.isTypeOf(leftValue) == false) {
			ErrorPrinter.addErrorMsg("Assignment Statement Type error");
		}
		return null;
	}

	/**
	 * f0 -> Identifier() f1 -> "[" f2 -> Expression() f3 -> "]" f4 -> "=" f5 ->
	 * Expression() f6 -> ";"
	 */
	public MType visit(ArrayAssignmentStatement n, MType argu) {
		MType arrayType = n.f0.accept(this, argu);
		n.f1.accept(this, null);
		MType arrayIndexType = n.f2.accept(this, argu);
		n.f3.accept(this, null);
		n.f4.accept(this, null);
		MType intType = n.f5.accept(this, argu);
		n.f6.accept(this, null);

		if (arrayType.isSameTypeOf("array") == false || arrayIndexType.isSameTypeOf("int") == false
				|| intType.isSameTypeOf("int") == false) {
			String msg = "Array Assignment Statement error";
			ErrorPrinter.addErrorMsg(msg);
		}

		return null;
	}

	/**
	 * f0 -> "if" f1 -> "(" f2 -> Expression() f3 -> ")" f4 -> Statement() f5 ->
	 * "else" f6 -> Statement()
	 */
	public MType visit(IfStatement n, MType argu) {

		MType booleanType = n.f2.accept(this, argu);
		if (booleanType.isSameTypeOf("boolean") == false) {
			String msg = "IfStatement error";
			ErrorPrinter.addErrorMsg(msg);
		}
		n.f4.accept(this, argu);
		n.f6.accept(this, argu);
		return null;
	}

	/**
	 * f0 -> "while" f1 -> "(" f2 -> Expression() f3 -> ")" f4 -> Statement()
	 */
	public MType visit(WhileStatement n, MType argu) {
		MType booleanType = n.f2.accept(this, argu);
		if (booleanType.isSameTypeOf("boolean") == false) {
			String msg = "WhileStatement error";
			ErrorPrinter.addErrorMsg(msg);
		}
		n.f4.accept(this, argu);
		return null;
	}

	/**
	 * f0 -> "System.out.println" f1 -> "(" f2 -> Expression() f3 -> ")" f4 -> ";"
	 */
	public MType visit(PrintStatement n, MType argu) {
		MType intType = n.f2.accept(this, argu);
		if (intType.isSameTypeOf("int") == false) {
			String msg = "Print type error!";
			ErrorPrinter.addErrorMsg(msg);
		}
		return null;
	}

	/**
	 * f0 -> AndExpression() | CompareExpression() | PlusExpression() |
	 * MinusExpression() | TimesExpression() | ArrayLookup() | ArrayLength() |
	 * MessageSend() | PrimaryExpression()
	 */
	public MType visit(Expression n, MType argu) {
		return n.f0.accept(this, argu);
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "&&" f2 -> PrimaryExpression()
	 */
	public MType visit(AndExpression n, MType argu) {
		MType op1 = n.f0.accept(this, argu);
		n.f1.accept(this, null);
		MType op2 = n.f2.accept(this, argu);
		if (op1.isSameTypeOf("boolean") == false || op2.isSameTypeOf("boolean") == false) {
			String msg = "AndExpression op isn't int!";
			ErrorPrinter.addErrorMsg(msg);
		}
		return new MType("boolean");
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "<" f2 -> PrimaryExpression()
	 */
	public MType visit(CompareExpression n, MType argu) {
		MType op1 = n.f0.accept(this, argu);
		n.f1.accept(this, null);
		MType op2 = n.f2.accept(this, argu);
		if (op1.isSameTypeOf("int") == false || op2.isSameTypeOf("int") == false) {
			String msg = "AndExpression error!";
			ErrorPrinter.addErrorMsg(msg);
		}
		return new MType("boolean");
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "+" f2 -> PrimaryExpression()
	 */
	public MType visit(PlusExpression n, MType argu) {
		MType op1 = n.f0.accept(this, argu);
		n.f1.accept(this, null);
		MType op2 = n.f2.accept(this, argu);
		if (op1.isSameTypeOf("int") == false || op2.isSameTypeOf("int") == false) {
			String msg = "PlusExpression error!";
			ErrorPrinter.addErrorMsg(msg);
		}
		return new MType("int");
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "-" f2 -> PrimaryExpression()
	 */
	public MType visit(MinusExpression n, MType argu) {
		MType op1 = n.f0.accept(this, argu);
		n.f1.accept(this, null);
		MType op2 = n.f2.accept(this, argu);
		if (op1.isSameTypeOf("int") == false || op2.isSameTypeOf("int") == false) {
			String msg = "MinusExpression error!";
			ErrorPrinter.addErrorMsg(msg);
		}
		return new MType("int");
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "*" f2 -> PrimaryExpression()
	 */
	public MType visit(TimesExpression n, MType argu) {
		MType op1 = n.f0.accept(this, argu);
		n.f1.accept(this, null);
		MType op2 = n.f2.accept(this, argu);

		if (op1.isSameTypeOf("int") == false || op2.isSameTypeOf("int") == false) {
			String msg = "TimesExpression error!";
			ErrorPrinter.addErrorMsg(msg);
		}
		return new MType("int");
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "[" f2 -> PrimaryExpression() f3 -> "]"
	 */
	public MType visit(ArrayLookup n, MType argu) {
		MType op1 = n.f0.accept(this, argu);
		n.f1.accept(this, null);
		MType op2 = n.f2.accept(this, argu);

		if (op1.isSameTypeOf("array") == false) {
			String msg = "ArrayLookupExpression op1 isn't array!";
			ErrorPrinter.addErrorMsg(msg);
		}
		if (op2.isSameTypeOf("int") == false) {
			String msg = "ArrayLookupExpression op2 isn't int!";
			ErrorPrinter.addErrorMsg(msg);
		}
		return new MType("int");
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "." f2 -> "length"
	 */
	public MType visit(ArrayLength n, MType argu) {
		MType op = n.f0.accept(this, argu);
		n.f1.accept(this, null);
		n.f2.accept(this, null);

		if (op.isSameTypeOf("array") == false) {
			String msg = "ArrayLenthExpression op isn't int!";
			ErrorPrinter.addErrorMsg(msg);
		}
		return new MType("int");
	}

	/**
	 * 待定 Grammar production: f0 -> PrimaryExpression() f1 -> "." f2 -> Identifier()
	 * f3 -> "(" f4 -> ( ExpressionList() )? f5 -> ")"
	 */
	Vector<MType> actualParaTypeList = new Vector<MType>();

	public MType visit(MessageSend n, MType argu) {
		MType callerType = n.f0.accept(this, argu);
		if (callerType instanceof MClass == false) {
			ErrorPrinter.addErrorMsg("messageSend caller error");
			return null;
		}
		String methodName = n.f2.f0.toString();
		MMethod callee = ((MClass) callerType).containMethod(methodName);
		if (callee == null) {
			ErrorPrinter.addErrorMsg("messageSend callee error");
			return null;
		}
		// 检查类型是否匹配，待定
		Vector<MType> tempParaList = new Vector<MType>(actualParaTypeList);
		actualParaTypeList.clear();
		n.f4.accept(this, argu);
		int formalParaNum = callee.getParamNum();
		int actualParaNum = actualParaTypeList.size();
		if (actualParaNum!= formalParaNum) {
			System.out.println(methodName);
			System.out.println("formalParaNum: " + formalParaNum);
			System.out.println("actualParaNum: " + actualParaNum);
			for(int i=0;i<actualParaNum;++i) {
				System.out.println(actualParaTypeList.elementAt(i).kind);
			}
			ErrorPrinter.addErrorMsg("parameter num don't match");
		} else {
			for (int i = 0; i < formalParaNum; ++i) {
				if (actualParaTypeList.elementAt(i).isTypeOf(callee.getParamType(i)) == false) {
					ErrorPrinter.addErrorMsg("parameter type don't match");
					break;
				}
			}
		}
		actualParaTypeList = tempParaList;
		return callee.returnType;
	}

	/**
	 * f0 -> Expression() f1 -> ( ExpressionRest() )*
	 */
	public MType visit(ExpressionList n, MType argu) {
		MType paraType = n.f0.accept(this, argu);
		actualParaTypeList.add(paraType);
		n.f1.accept(this, argu);
		return null;
	}

	/**
	 * f0 -> "," f1 -> Expression()
	 */
	public MType visit(ExpressionRest n, MType argu) {
		MType paraType = n.f1.accept(this, argu);
		actualParaTypeList.add(paraType);
		return null;
	}

	/**
	 * f0 -> IntegerLiteral() | TrueLiteral() | FalseLiteral() | Identifier() |
	 * ThisExpression() | ArrayAllocationExpression() | AllocationExpression() |
	 * NotExpression() | BracketExpression()
	 */
	public MType visit(PrimaryExpression n, MType argu) {
		return n.f0.accept(this, argu);
	}

	/**
	 * f0 -> <INTEGER_LITERAL>
	 */
	public MType visit(IntegerLiteral n, MType argu) {
		return new MType("int");
	}

	/**
	 * f0 -> "true"
	 */
	public MType visit(TrueLiteral n, MType argu) {
		return new MType("boolean");
	}

	/**
	 * f0 -> "false"
	 */
	public MType visit(FalseLiteral n, MType argu) {
		return new MType("boolean");
	}

	/**
	 * f0 -> <IDENTIFIER> 待定
	 */
	public MType visit(Identifier n, MType argu) {
		if (argu == null) {
			return null;
		}
		n.f0.accept(this, argu);
		String name = n.f0.toString();
		MType id = argu.containVar(name);
		if (id instanceof MVar) {
			if(((MVar) id).varType==null) {
				ErrorPrinter.addErrorMsg("unrecognizable identifier");
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

	/**
	 * f0 -> "this"
	 */
	public MType visit(ThisExpression n, MType argu) {
		return ((MMethod) argu).methodClass;
	}

	/**
	 * f0 -> "new" f1 -> "int" f2 -> "[" f3 -> Expression() f4 -> "]"
	 */
	public MType visit(ArrayAllocationExpression n, MType argu) {

		String expressionType = n.f3.accept(this, argu).kind;
		if (expressionType.equals("int") == false) {
			String msg = "ArrayAllocationExpression expressionType isn't int!";
			ErrorPrinter.addErrorMsg(msg);
		}
		return new MType("array");
	}

	/**
	 * f0 -> "new" f1 -> Identifier() f2 -> "(" f3 -> ")"
	 */
	public MType visit(AllocationExpression n, MType argu) {
		MType newClassType = (n.f1.accept(this, classList));
		return newClassType;
	}

	/**
	 * f0 -> "!" f1 -> Expression()
	 */
	public MType visit(NotExpression n, MType argu) {

		MType notExpressionType = n.f1.accept(this, argu);
		if (notExpressionType.isSameTypeOf("boolean") == false) {
			String msg = "NotExpression op type error!";
			ErrorPrinter.addErrorMsg(msg);
		}
		return new MType("boolean");
	}

	/**
	 * f0 -> "(" f1 -> Expression() f2 -> ")"
	 */
	public MType visit(BracketExpression n, MType argu) {
		return n.f1.accept(this, argu);
	}

}
