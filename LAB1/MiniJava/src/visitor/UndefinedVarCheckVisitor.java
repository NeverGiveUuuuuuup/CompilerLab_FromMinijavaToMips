package visitor;

import syntaxtree.*;
import errorPrinter.*;
import symbol.*;

public class UndefinedVarCheckVisitor extends GJDepthFirst<MType, MType> {
	/*
	 * 访问Type和Identifier结点时进行检查
	 * 检查是否用到了未定义的类(Type结点）或未定义的变量（Identifier结点）
	 */
	MClassList classList;
	public MType visit(Goal n, MType argu) {
		this.classList = (MClassList) argu;
		n.f0.accept(this, argu);
	    n.f1.accept(this, argu);
	    n.f2.accept(this, argu);
		return null;
	}
	// 访问Type结点时，要判断是否有未定义的类
	public MType visit(Type n, MType argu) {
		String s = null;
		if (n.f0.choice instanceof ArrayType) {
			s = "array";
		} 
		else if (n.f0.choice instanceof BooleanType) {
			s = "boolean";
		} 
		else if (n.f0.choice instanceof IntegerType) {
			s = "int";
		} 
		else if (n.f0.choice instanceof Identifier) {
			s = ((Identifier) n.f0.choice).f0.tokenImage;// 类名
			MClass classType = (MClass)(classList.containVar(s));
			if (classType == null) {
				ErrorPrinter.addErrorMsg("undefined class: "+s);
				return null;
			}
			return classType;
		}
		return new MType(s);
	}
	/**
	 * f0 -> <IDENTIFIER> 待定
	 */
	public MType visit(Identifier n, MType argu) {
		n.f0.accept(this, argu);
		if(argu!=null) {//判断是否未定义
			String name = n.f0.toString();
			MType id = argu.containVar(name);
			if(id==null) {
				ErrorPrinter.addErrorMsg("undefined identifier: "+name);
			}
			return id;
		}
		return null;
	}
	/**
	 * f0 -> Type() f1 -> Identifier() f2 -> ";"
	 */
	public MType visit(VarDeclaration n, MType argu) {
		MType type = n.f0.accept(this, null);
		MType id= n.f1.accept(this, argu);//argu可能是MClass，也可能是MMethod. 返回一个MVar
		if(id instanceof MVar) {
			((MVar)id).SetVarType(type);
		}
		return null;
	}
	
	/**
	 * f0 -> "public" f1 -> Type() f2 -> Identifier() f3 -> "(" f4 -> (
	 * FormalParameterList() )? f5 -> ")" f6 -> "{" f7 -> ( VarDeclaration() )* f8
	 * -> ( Statement() )* f9 -> "return" f10 -> Expression() f11 -> ";" f12 -> "}"
	 */
	public MType visit(MethodDeclaration n, MType argu) {
		n.f0.accept(this, null);
		MType returnType = n.f1.accept(this, null);
		
		String methodName = n.f2.f0.toString();
		
		MMethod thisMethod = ((MClass)argu).containMethod(methodName);
		
		thisMethod.SetReturnType(returnType);//设置返回值类型
		
		n.f3.accept(this, null);
		n.f4.accept(this, thisMethod);//用于设置参数的类型
		n.f5.accept(this, null);
		n.f6.accept(this, null);
		n.f7.accept(this, thisMethod);//用于设置变量的类型
		n.f8.accept(this, thisMethod);
		n.f9.accept(this, null);
		n.f10.accept(this, thisMethod);//用于判断表达式是否含有未定义的变量
		n.f11.accept(this, null);
		n.f12.accept(this, null);

		return null;
	}
	

	   /**
	    * f0 -> FormalParameter()
	    * f1 -> ( FormalParameterRest() )*
	    */
	   public MType visit(FormalParameterList n, MType argu) {
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      return null;
	   }

	   /**
	    * f0 -> Type()
	    * f1 -> Identifier()
	    */
	   public MType visit(FormalParameter n, MType argu) {
		   MType type = n.f0.accept(this, null);
			MType id= n.f1.accept(this, argu);//argu是MMethod. 返回一个MVar
			if(id instanceof MVar) {
				((MVar)id).SetVarType(type);
				((MMethod)argu).mParameterTypeList.add(type);
			}
			return null;
	   }
	
	/**
	 * f0 -> "new" f1 -> Identifier() f2 -> "(" f3 -> ")"
	 */
	public MType visit(AllocationExpression n, MType argu) {
		n.f0.accept(this, null);
		n.f1.accept(this, classList);//这里传的参数很特别，因为要求new的必须是一个类。
		n.f2.accept(this, null);
		n.f3.accept(this, null);
		return null;
	}

	/**
	 * 待定 Grammar production: f0 -> PrimaryExpression() f1 -> "." f2 -> Identifier()
	 * f3 -> "(" f4 -> ( ExpressionList() )? f5 -> ")"
	 */
	public MType visit(MessageSend n, MType argu) {
		n.f0.accept(this, argu);//传递参数用于判断表达式是否含有未定义的变量
		n.f1.accept(this, null);
		n.f2.accept(this, null);
		n.f3.accept(this, null);
		n.f4.accept(this, argu);
		n.f5.accept(this, null);
		return null;
	}

	/**
	 * f0 -> "class" f1 -> Identifier() f2 -> "{" f3 -> ( VarDeclaration() )* f4 ->
	 * ( MethodDeclaration() )* f5 -> "}"
	 */
	public MType visit(ClassDeclaration n, MType argu) {
		n.f0.accept(this, null);
		MClass thisClass = (MClass)(n.f1.accept(this, argu));
		n.f2.accept(this, null);
		n.f3.accept(this, thisClass);
		n.f4.accept(this, thisClass);
		n.f5.accept(this, null);
		return null;
	}

	/**
	 * f0 -> "class" f1 -> Identifier() f2 -> "extends" f3 -> Identifier() f4 -> "{"
	 * f5 -> ( VarDeclaration() )* f6 -> ( MethodDeclaration() )* f7 -> "}"
	 */
	public MType visit(ClassExtendsDeclaration n, MType argu) {
		n.f0.accept(this, null);
		MClass thisClass = (MClass)(n.f1.accept(this, argu));
		n.f2.accept(this, null);
		n.f3.accept(this, argu);
		n.f4.accept(this, null);
		n.f5.accept(this, thisClass);
		n.f6.accept(this, thisClass);
		n.f7.accept(this, null);
		return null;
		
	}

	/**
	 * f0 -> "class" f1 -> Identifier() f2 -> "{" f3 -> "public" f4 -> "static" f5
	 * -> "void" f6 -> "main" f7 -> "(" f8 -> "String" f9 -> "[" f10 -> "]" f11 ->
	 * Identifier() f12 -> ")" f13 -> "{" f14 -> ( VarDeclaration() )* f15 -> (
	 * Statement() )* f16 -> "}" f17 -> "}"
	 */
	public MType visit(MainClass n, MType argu) {
		n.f0.accept(this, null);
		MClass mainClass = (MClass)(n.f1.accept(this, argu));
		MMethod mainMethod = mainClass.containMethod("main");
		
		n.f14.accept(this, mainMethod);
		n.f15.accept(this, mainMethod);
		return null;
	}

}
