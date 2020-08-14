package symbol;

import java.util.*;
import java.util.Map.Entry;


public class MClass extends MIdentifier{
	public boolean isExtendClass=false; //是否是子类
	public String parentClassName; //父类名字
	public MClass parentClass; // 父类
	public HashMap<String, MClass> childClassTable;
	public HashMap<String, MMethod> mMethodTable;//key是方法名字
	public HashMap<String, MVar> mVarTable;//key是变量名字
	
	
	void _init(){
		mMethodTable = new HashMap<String, MMethod>();
		mVarTable = new HashMap<String, MVar>();
		parentClass=null;
		childClassTable=new HashMap<String, MClass>();
	}
	
	public MClass(String _name){ //基类调用这个构造函数
		/*构造函数  */
		super(_name);
		_init();
	}
	public MClass(String _name, String _parentClassName){ //继承类调用这个构造函数
		/*构造函数  */
		super(_name);
		isExtendClass=true;
		parentClassName=_parentClassName;
		_init();
	}
	
	//关于循环继承，实质上是判断有向图是否有环
	
	//插入方法
	public String insertMethod(MMethod newMethod){
		String methodName=newMethod.methodName;
		//检查是否重定义
		if(mMethodTable.containsKey(methodName)==true){
			return new String("Method overloading / Duplicate method definition");
		}
		mMethodTable.put(methodName, newMethod);
		return null;
	}
	//插入字段
	public String insertVar(MVar newVar){
		String varName=newVar.varName;
		//检查是否重定义
		if(mVarTable.containsKey(varName)==true){
			return new String("Duplicate class field "+varName);
		}
		mVarTable.put(varName, newVar);
		return null;
	}
	
	//重载返回false
	public boolean inheritParentMethod() {
		if(parentClass==null) {
			return true;
		}
		for (Entry<String, MMethod> entry : parentClass.mMethodTable.entrySet()) {
			MMethod parentMethod=entry.getValue();
			String parentMethodName=entry.getKey();
			if(this.mMethodTable.containsKey(parentMethodName)==true) {
				//子类有和父类名字相同的方法，判断是否为重载
				MMethod childMethod=this.mMethodTable.get(parentMethodName);
				if(childMethod.isOverloading(parentMethod)==true) {
					return false;
				}
			}
			else {
				this.insertMethod(parentMethod);//继承父类的方法
			}
		}
		return true;
	}
	public void inheritParentVar() {
		if(parentClass==null) {
			return ;
		}
		for (Entry<String, MVar> entry : parentClass.mVarTable.entrySet()) {
			MVar parentVar=entry.getValue();
			String parentVarName=entry.getKey();
			if(this.mVarTable.containsKey(parentVarName)==true) {
				//子类有和父类名字相同的变量，隐藏
				continue;
			}
			else {
				this.insertVar(parentVar);//继承父类的变量
			}
		}
	}
	public MType containVar(String name) {
		MVar _var = this.mVarTable.get(name);
		return _var;	
	}
	public MMethod containMethod(String name) {
		MMethod _method = this.mMethodTable.get(name);
		return _method;	
	}
	
	public int getMethodIndex(String _methodName) {
		int i=0;
		for (String methodName : mMethodTable.keySet()) {
			if(methodName.equals(_methodName)==true) {
				return i;
			}
			++i;
		}
		return -1;
	}
	public int getVarIndex(String _varName) {
		int i=0;
		for (String varName : mVarTable.keySet()) {
			if(varName.equals(_varName)==true) {
				return i;
			}
			++i;
		}
		return -1;
	}
}

	