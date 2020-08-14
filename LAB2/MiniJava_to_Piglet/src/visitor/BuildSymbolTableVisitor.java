package visitor;
import syntaxtree.*;
import errorPrinter.*;
import symbol.*;

public class BuildSymbolTableVisitor extends GJDepthFirst<MType, MType> {
	/*
	在DFS构建符号表时，只考虑以下几种结点：（暂定）
	ClassDeclaration
	ClassExtendsDeclaration
	VarDeclaration
	MethodDelcaration
	FormalParameter
	*/
	public MType visit(ClassDeclaration n, MType argu) {                                                                                                                        
		//这里最重要的操作是，向符号表中添加一个类，并检查是否重复定义
		
		/**
		 * Grammar production:
		 * f0 -> "class"
		 * f1 -> Identifier()
		 * f2 -> "{"
		 * f3 -> ( VarDeclaration() )*
		 * f4 -> ( MethodDeclaration() )*
		 * f5 -> "}"
		 */     
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);//这里有点问题，不确定第二个参数用argu还是newClass
		//（1）创建一个MClass实例
		MClass newClass = new MClass(((Identifier)n.f1).f0.toString());
		
		//（2）将该实例插入符号表中
		String msg = ((MClassList)argu).insertClass(newClass);
		//（3）判断是否出错
		if(msg!=null){
			ErrorPrinter.addErrorMsg(msg);
			return null;
		}
		
		n.f2.accept(this, newClass);
		n.f3.accept(this, newClass);
		n.f4.accept(this, newClass);
		n.f5.accept(this, newClass);
		return newClass;
	}
	
	public MType visit(ClassExtendsDeclaration n, MType argu) {                                                                                                                        
		
		/**
		 * Grammar production:
		 * f0 -> "class"
		 * f1 -> Identifier()
		 * f2 -> "extends"
		 * f3 -> Identifier()
		 * f4 -> "{"
		 * f5 -> ( VarDeclaration() )*
		 * f6 -> ( MethodDeclaration() )*
		 * f7 -> "}"
		 */
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);//同上一个visit
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		//（1）创建一个MClass实例，参数有类名、父类名
		MClass newClass = new MClass(((Identifier)n.f1).f0.toString(), ((Identifier)n.f3).f0.toString());
		
		//（2）将该实例插入符号表argu中
		String msg = ((MClassList)argu).insertClass(newClass);
		//（3）判断是否出错
		if(msg!=null){
			ErrorPrinter.addErrorMsg(msg);
			return null;
		}
		n.f4.accept(this, newClass);
		n.f5.accept(this, newClass);
		n.f6.accept(this, newClass);
		n.f7.accept(this, newClass);
		return newClass;
	}
	
	public MType visit(MethodDeclaration n, MType argu) {                                                                                                                        
		/**
		 * Grammar production:
		 * f0 -> "public"
		 * f1 -> Type()
		 * f2 -> Identifier()
		 * f3 -> "("
		 * f4 -> (FormalParameterList() )?
		 * f5 -> ")"
		 * f6 -> "{"
		 * f7 -> ( VarDeclaration() )*
		 * f8 -> ( Statement() )*
		 * f9 -> "return"
		 * f10 -> Expression()
		 * f11 -> ";"
		 * f12 -> "}"
		 */
		n.f0.accept(this, argu);
		
		MType type=n.f1.accept(this, argu);//返回值类型
		String methodName = ((Identifier)n.f2).f0.toString();//方法名
		
		MMethod newMethod = new MMethod(methodName, type.getType(), (MClass)argu);
		String msg = ((MClass)argu).insertMethod(newMethod);
		if(msg!=null){
			ErrorPrinter.addErrorMsg(msg);
			return null;
		}
		
		n.f3.accept(this, newMethod);
		n.f4.accept(this, newMethod);
		n.f5.accept(this, newMethod);
		n.f6.accept(this, newMethod);
		n.f7.accept(this, newMethod);
		n.f8.accept(this, newMethod);
		n.f9.accept(this, newMethod);
		n.f10.accept(this, newMethod);
		n.f11.accept(this, newMethod);
		n.f12.accept(this, newMethod);
		
		return newMethod;
	}
	public MType visit(Type n, MType argu){
		/**
		 * Grammar production:
		 * f0 -> ArrayType()
		 *       | BooleanType()
		 *       | IntegerType()
		 *       | Identifier()
		 */
		 String s;
		 if(n.f0.choice instanceof ArrayType){
			 s="array";
		 }
		 else if(n.f0.choice instanceof BooleanType){
			 s="boolean";
		 }
		 else if(n.f0.choice instanceof IntegerType){
			 s="int";
		 }
		 else{// n.f0.choice is instance of Identifier
			 s=((Identifier)n.f0.choice).f0.tokenImage;
		 }
		 return new MType(s);
	}
	
	public MType visit(VarDeclaration n, MType argu) {                                                                                                                        
		/**
		 * Grammar production:
		 * f0 -> Type()
		 * f1 -> Identifier()
		 * f2 -> ";"
		 */
		
		MType type=n.f0.accept(this, argu);//返回值类型
		String varName = ((Identifier)n.f1).f0.toString();//方法名
		String msg=null;
		MVar newVar;
		if(argu instanceof MClass) {
			newVar = new MVar(type.getType(),varName,(MClass)argu);
			msg=((MClass)argu).insertVar(newVar);
		}
		else { //if argu instance of MMethod)
			newVar = new MVar(type.getType(),varName,(MMethod)argu);
			msg=((MMethod)argu).insertVar(newVar);
		}

		if(msg!=null){
			ErrorPrinter.addErrorMsg(msg);
			return null;
		}
		return null;
	}
	
	public MType visit(FormalParameter n, MType argu) {                                                                                                                        
		/**
		 * Grammar production:
		 * f0 -> Type()
		 * f1 -> Identifier()
		 */
		MType type=n.f0.accept(this, argu);//返回值类型
		String varName = ((Identifier)n.f1).f0.toString();//方法名
		String msg=null;
		MVar newVar;
		if(argu instanceof MMethod) {
			newVar = new MVar(type.getType(),varName,(MMethod)argu);
			msg=((MMethod)argu).insertParameter(newVar);
		}
		
		if(msg!=null){
			ErrorPrinter.addErrorMsg(msg);
			return null;
		}
		return null;
	}
	
	
	public MType visit(MainClass n, MType argu){
		//MainClass的情况比较特殊，访问这个结点时，不仅要新建一个类MClass，还要顺带着新建一个方法MMethod.
		/*Grammar production:
		 * f0 -> "class"
		 * f1 -> Identifier()
		 * f2 -> "{"
		 * f3 -> "public"
		 * f4 -> "static"
		 * f5 -> "void"
		 * f6 -> "main"
		 * f7 -> "("
		 * f8 -> "String"
		 * f9 -> "["
		 * f10 -> "]"
		 * f11 -> Identifier()
		 * f12 -> ")"
		 * f13 -> "{"
		 * f14 -> ( VarDeclaration() )*
		 * f15 -> ( Statement() )*
		 * f16 -> "}"
		 * f17 -> "}"
		 */
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		//（1）创建一个MClass实例
		MClass newClass = new MClass(((Identifier)n.f1).f0.toString());
		
		//（2）将该实例插入符号表argu中
		String msg = ((MClassList)argu).insertClass(newClass);
		//（3）判断是否出错
		if(msg!=null){
			ErrorPrinter.addErrorMsg(msg);
			return null;
		}
		//（4）创建一个MMethod实例，代表main函数
		MMethod newMethod = new MMethod("main", "void", newClass);
		msg = (newClass).insertMethod(newMethod);
		if(msg!=null){
			ErrorPrinter.addErrorMsg(msg);
			return null;
		}
		
		/* 
		 * (5）向main方法中添加一个参数String[] identifier()
		*/
		String paraName = n.f11.f0.toString();
		newMethod.insertVar(new MVar("String[]", paraName, newMethod));
		
		n.f3.accept(this, newMethod);
		n.f4.accept(this, newMethod);
		n.f5.accept(this, newMethod);
		n.f6.accept(this, newMethod);
		n.f7.accept(this, newMethod);
		n.f8.accept(this, newMethod);
		n.f9.accept(this, newMethod);
		n.f10.accept(this, newMethod);
		n.f11.accept(this, newMethod);
		n.f12.accept(this, newMethod);
		
		n.f13.accept(this, newMethod);
		n.f14.accept(this, newMethod);
		n.f15.accept(this, newMethod);
		n.f16.accept(this, newMethod);
		n.f17.accept(this, newMethod);
		return newClass;
	}
}

