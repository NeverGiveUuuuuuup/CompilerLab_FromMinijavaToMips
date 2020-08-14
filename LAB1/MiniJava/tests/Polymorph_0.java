class Polymorph_0{
	public static void main(String[] a){
		Derived_1 d;
		d = new Derived_2();
		d.f3();
		d.f1(5);
		d.eat();
		System.out.println("finish");
		
	}
}
class Base{
	int a = 1;
	int b = 1;
	public int f1(int t){
		System.out.println(a);
		a = t;
		return a;
	}
	public int eat(){
		System.out.println(a);
		return 0;
	}
}
class Derived_1 extends Base{
	int a = 0;
	public int f2(int t){
		a = t + 1;
		return a;
	}
	public int eat(){
		System.out.println(a);
		return 0;
	}
	public int f3(){
		System.out.println(b);
		return b;
	}
}
class Derived_2 extends Derived_1{
	int b=2;
	

}
