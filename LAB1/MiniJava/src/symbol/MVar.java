
package symbol;

public class MVar extends MIdentifier{
	public MType varType=null;
	public String varName=null;
	public boolean isClassVar=true;//默认是类的字段，当取false时为方法的字段
	public MClass varClass=null;
	public MMethod varMethod=null;
	
	public MVar(String _type , String _name, MClass _class){
		super(_type);
		varClass=_class;
		varName=_name;
	}
	public MVar(String _type , String _name, MMethod _method){
		super(_type);
		isClassVar=false;
		varMethod=_method;
		varName=_name;
	}
	public MVar(String _type , String _name){
		super(_type);
		isClassVar=false;
		varName=_name;
	}
	public void SetVarType(MType _type) {
		varType=_type;
	}
	public MType GetVarType() {
		return varType;
	}
}