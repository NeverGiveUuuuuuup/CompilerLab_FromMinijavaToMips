class Polymorph_0{
	public static void main(String[] a){
		Base d = new Derived();
		d.f1(5);
		d.eat();
		System.out.println("finish");
	}
}
class Base{
	int a = 0;
	public int f1(int t){
		a = t;
		return a;
	}
	public int eat(){
		System.out.println(a);
		return 0;
	}
}
class Derived extends Base{
	int a = 0;
	public int f2(int t){
		a = t + 1;
		return a;
	}
	public int eat(){
		System.out.println(a);
		return 0;
	}
}
