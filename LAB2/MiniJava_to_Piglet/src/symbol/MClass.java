package symbol;

import java.util.*;
import java.util.Map.Entry;

public class MClass extends MIdentifier {
	public boolean isExtendClass = false; // 是否是子类
	public String parentClassName; // 父类名字
	public MClass parentClass; // 父类
	public HashMap<String, MClass> childClassTable;
	public HashMap<String, MMethod> mMethodTable;// key是方法名字
	public HashMap<String, MVar> mVarTable;// key是变量名字

	public Vector<String> vtable;
	public Vector<String> fullnameDTable;//类名_方法名
	public Vector<String> dtable;//方法名

	void _init() {
		mMethodTable = new HashMap<String, MMethod>();
		mVarTable = new HashMap<String, MVar>();
		parentClass = null;
		childClassTable = new HashMap<String, MClass>();
	}

	public MClass(String _name) { // 基类调用这个构造函数
		/* 构造函数 */
		super(_name);
		_init();
	}

	public MClass(String _name, String _parentClassName) { // 继承类调用这个构造函数
		/* 构造函数 */
		super(_name);
		isExtendClass = true;
		parentClassName = _parentClassName;
		_init();
	}

	// 关于循环继承，实质上是判断有向图是否有环

	// 插入方法
	public String insertMethod(MMethod newMethod) {
		String methodName = newMethod.methodName;
		// 检查是否重定义
		if (mMethodTable.containsKey(methodName) == true) {
			return new String("Method overloading / Duplicate method definition");
		}
		mMethodTable.put(methodName, newMethod);
		return null;
	}

	// 插入字段
	public String insertVar(MVar newVar) {
		String varName = newVar.varName;
		// 检查是否重定义
		if (mVarTable.containsKey(varName) == true) {
			return new String("Duplicate class field " + varName);
		}
		mVarTable.put(varName, newVar);
		return null;
	}

	// 重载返回false
	public boolean inheritParentMethod() {
		buildDTable();
		if (parentClass == null) {
			return true;
		}

		for (Entry<String, MMethod> entry : parentClass.mMethodTable.entrySet()) {
			MMethod parentMethod = entry.getValue();
			String parentMethodName = entry.getKey();
			if (this.mMethodTable.containsKey(parentMethodName) == true) {
				// 子类有和父类名字相同的方法，判断是否为重载
				MMethod childMethod = this.mMethodTable.get(parentMethodName);
				if (childMethod.isOverloading(parentMethod) == true) {
					return false;
				}
			} else {
				this.insertMethod(parentMethod);// 继承父类的方法
			}
		}
		return true;
	}

	public void inheritParentVar() {
		buildVTable();
		if (parentClass == null) {
			return;
		}

		for (Entry<String, MVar> entry : parentClass.mVarTable.entrySet()) {
			MVar parentVar = entry.getValue();
			String parentVarName = entry.getKey();
			if (this.mVarTable.containsKey(parentVarName) == true) {
				// 子类有和父类名字相同的变量，隐藏
				continue;
			} else {
				this.insertVar(parentVar);// 继承父类的变量
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
	
	// 建立方法表
	public void buildDTable() {
		// System.out.println(this.kind);
		if (isExtendClass == true) {
			dtable = new Vector<String>(parentClass.dtable);
			fullnameDTable = new Vector<String>(parentClass.fullnameDTable);
			for(int i=0;i<dtable.size();++i) {
				String name = dtable.elementAt(i);
				
				if(this.mMethodTable.containsKey(name)==true) {
					String newName = this.kind + "_" + name;
					fullnameDTable.setElementAt(newName, i);
				}
			}
		} else {
			dtable = new Vector<String>();
			fullnameDTable = new Vector<String>();
		}
		for (Entry<String, MMethod> entry : this.mMethodTable.entrySet()) {
			String name = entry.getKey();
			String fullName = this.kind + "_" + name;
			if (isExtendClass == false || parentClass.mMethodTable.containsKey(name) == false) {
				dtable.add(name);
				fullnameDTable.add(fullName);
			}
		}
		/*
		 System.out.println(this.kind + " buildDTable finished."); 
		 for(int i=0;i<dtable.size();++i) { System.out.println(dtable.elementAt(i)); }
		 System.out.println(this.kind + " buildFullNameDTable finished."); 
		 for(int i=0;i<dtable.size();++i) { System.out.println(fullnameDTable.elementAt(i)); }
		 */
	}

	// 建立属性表
	public void buildVTable() {
		if (isExtendClass == true) {
			vtable = new Vector<String>(parentClass.vtable);
		} else {
			vtable = new Vector<String>();
		}
		for (Entry<String, MVar> entry : this.mVarTable.entrySet()) {
			vtable.add(entry.getKey());
		}
		/*
		 * System.out.println(this.kind + " buildVTable finished."); for(int
		 * i=0;i<vtable.size();++i) { System.out.println(vtable.elementAt(i)); }
		 */
	}
	//获取在方法表中的下标
		public int getMethodIndex(String _methodName) {
			for(int i=0;i<dtable.size();++i) {
				if (dtable.elementAt(i).equals(_methodName) == true) {
					return i;
				}	
			}
			return -1;
		}
		//获取在属性表中的下标
		public int getVarIndex(String _varName) {
			for(int i=vtable.size()-1;i>=0;--i) {
				if (vtable.elementAt(i).equals(_varName) == true) {
					return i;
				}	
			}
			return -1;
		}

}
