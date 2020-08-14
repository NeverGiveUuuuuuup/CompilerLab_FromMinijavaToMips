package visitor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.Map.Entry;

import errorPrinter.*;
import symbol.MClass;
import symbol.MClassList;
public class InheritCheck {
	MClassList classList=null;
	String msg;
	public InheritCheck(MClassList argu) {
		classList = argu;
	}
	public void check() {
		msg = noMultiInheritCheck();
		if(msg!=null) {
			ErrorPrinter.addErrorMsg(msg);
		}
		msg = no_CircleInherit_Overloading();
		if(msg!=null) {
			ErrorPrinter.addErrorMsg(msg);
		}
	}
	// 检查是否存在多重继承，由词法决定了不存在多重继承，所以返回null
	public String noMultiInheritCheck() {
		return null;// 不存在多重继承
	}

	// 检查是否存在循环继承
	public String no_CircleInherit_Overloading() {
		HashMap<String, MClass> mClassTable = classList.mclassTable;
		int classNum = mClassTable.size();
		// 更新每个类的子类列表
		for (String className : mClassTable.keySet()) {
			MClass curClass = mClassTable.get(className);
			if (curClass.parentClassName != null) {
				curClass.parentClass = mClassTable.get(curClass.parentClassName);
				if(curClass.parentClass==null) {
					return "Undefined parentClass: "+curClass.parentClassName;
				}
				curClass.parentClass.childClassTable.put(className, curClass);
			}
		}
		Vector<String> visitedClassVec = new Vector<String>();
		Queue<MClass> queue = new LinkedList<MClass>();
		// 把基类放入队列中
		for (Entry<String, MClass> entry : mClassTable.entrySet()) {
			MClass curClass = entry.getValue();
			if (curClass.parentClass == null) {
				queue.add(curClass);// 基类
				visitedClassVec.add(entry.getKey());
			}
		}
		// BST遍历由类和继承关系构成的森林
		while (queue.isEmpty() == false) {
			
			MClass curClass = queue.poll();// 返回队列头部元素并弹出
			/*
			 * System.out.println(curClass.kind);
			 * System.out.println("childTable size="+curClass.childClassTable.size());
			 */
			
			if (curClass.inheritParentMethod() == false) {//判断继承是否有重载现象
				return "Overloading";
			}
			curClass.inheritParentVar();
			for (Entry<String, MClass> entry : curClass.childClassTable.entrySet()) {
				MClass childClass = entry.getValue();
				String childName = entry.getKey();
				//System.out.println(childName);
				
				if (visitedClassVec.contains(childName) == true) {
					// never reach here
					return "Circle Inheritance";
				} else {
					queue.add(childClass);
					visitedClassVec.add(childName);
				}
			}
		}
		if (visitedClassVec.size() < classNum) {
			return "Circle Inheritance";
		}
		return null;// 不存在循环继承或者重载
	}
}
