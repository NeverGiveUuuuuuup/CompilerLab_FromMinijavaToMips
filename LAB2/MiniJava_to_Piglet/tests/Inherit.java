class Inherit{
	public static void main(String[] s){
		inherit_0 a;
		inherit_1 b;
		a = new inherit_0();
		b = new inherit_1();
		System.out.println(b.f0());
	}
}
class inherit_0 {
	
	public int f0(){
		return 0;
	}
}
class inherit_1 extends inherit_0{
	public int f0(){
		return 1;
	}
}