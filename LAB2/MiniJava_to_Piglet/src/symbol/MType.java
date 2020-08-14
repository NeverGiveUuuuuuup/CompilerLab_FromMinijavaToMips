/*不知道要import什么包，待定*/
package symbol;

public class MType{
	public String kind;//可以是"int", "boolean", "array", 或者是类名
	public MType(){}
	public MType(String _type){
		kind=_type;
	}
	public String getType(){
		return kind;
	}
	
	public MType containVar(String name) {
		return null;
	}
	
	public boolean isSameTypeOf(MType t) {
		return this.kind.equals(t.kind);
	}
	public boolean isSameTypeOf(String s) {
		return this.kind.equals(s);
	}
	public boolean isTypeOf(MType type) {
		if(type instanceof MClass) {
			if(this instanceof MClass) {
				MClass curClass = (MClass)this;
				while(curClass!=null && curClass.isSameTypeOf(type)==false) {
					curClass=curClass.parentClass;
				}
				return (curClass!=null);
			}
			else {
				return false;
			}
		}
		else {
			return kind.equals(type.kind);
		}
	}
}