/*import待定*/
package symbol;

import java.util.HashMap;
import java.util.Vector;
import javafx.util.Pair;

public class MMethod extends MIdentifier {
	public String methodName;
	public MClass methodClass;// 这里指的是方法所在的类，没想到合适的简写，暂定
	public HashMap<String, MVar> mVarTable;
	public Vector<String> mParameterList;
	public Vector<MType> mParameterTypeList;
	public MType returnType;
	void _init() {
		mVarTable = new HashMap<String, MVar>();
		// 注意：变量列表是双索引，即变量类型（int,array,boolean,类名）和变量名字。
		mParameterList = new Vector<String>();
		returnType=null;
		mParameterTypeList=new Vector<MType>();
	}

	public MMethod(String _name, String _returnType, MClass _Class) {
		// 注意这里参数_parentClass的类型写成了MClass，不过老师的参考代码中是MIdentifier
		super(_returnType);
		methodClass = _Class;
		methodName = _name;
		_init();
	}

	// 插入字段
	public String insertVar(MVar newVar) {
		String varName = newVar.varName;
		// 检查是否重定义
		if (mVarTable.containsKey(varName) == true) {
			return new String("Duplicate local variable "+varName);
		}
		mVarTable.put(varName, newVar);
		return null;
	}
	
	public String insertParameter(MVar newVar) {
		String varName = newVar.varName;
		String type = newVar.kind;
		// 检查是否重定义
		if (mVarTable.containsKey(varName) == true) {
			return new String("Duplicate parameter "+varName);
		}
		mVarTable.put(varName, newVar);
		mParameterList.add(type);//把参数添加到参数表中
		
		return null;
	}
	public boolean isOverloading(MMethod parentMethod) {
		/*
		 * 子类有相同名字的方法
		 *这时要判断是重载还是覆盖（参数列表、返回值都相同）
		 *重载则直接返回true，覆盖则false
		*/
		if(parentMethod.kind.equals(this.kind)==false) {
			//1.返回值类型不同
			return true;
		}
		int paraNum=parentMethod.mParameterList.size();
		if(paraNum!=this.mParameterList.size()) {
			//2.参数数目不同
			return true;
		}
		Vector<String> parentParaList=parentMethod.mParameterList;
		Vector<String> childParaList=this.mParameterList;
		
		for(int paraIndex=0;paraIndex<paraNum;++paraIndex) {
			String parentParaKind=parentParaList.elementAt(paraIndex);
			String childParaKind=childParaList.elementAt(paraIndex);
			if(parentParaKind.equals(childParaKind)==false) {
			//3.参数类型不同
				return true;
			}
		}
		return false;
	}
	public MType containVar(String name) {
		MVar _var = this.mVarTable.get(name);
		if(_var!=null) {
			return _var;	
		}
		else {
			return this.methodClass.containVar(name);
		}
	}
	public void SetReturnType(MType _type) {
		returnType = _type;
	}
	public MType getParamType(int index) {
		return this.mParameterTypeList.elementAt(index);
	}
	public int getParamNum() {
		return this.mParameterTypeList.size();
	}
}