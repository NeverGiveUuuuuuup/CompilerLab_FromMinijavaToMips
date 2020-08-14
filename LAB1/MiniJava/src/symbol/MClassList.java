/*估计要import，待定*/
/*所在文件夹的类应该可以直接访问吧*/
package symbol;
import java.util.HashMap;

public class MClassList extends MType{
	/*将整个java源文件中的所有类，放入一个表中*/
	/*用hashmap表示符号表，键是string类型——类名，值是MClass类型。暂定*/
	public HashMap<String, MClass> mclassTable;
	
	
	public MClassList(){
		/*构造函数  */
		super("ClassList");
		mclassTable = new HashMap<String, MClass>();
		
	}
	public String insertClass(MClass newClass){
		String className=newClass.kind;
		//检查是否重定义
		if(mclassTable.containsKey(className)==true){
			return new String("class redefinition");
		}
		mclassTable.put(className, newClass);
		return null;
	}
	public MType containVar(String name) {
		return mclassTable.get(name);
	}
}