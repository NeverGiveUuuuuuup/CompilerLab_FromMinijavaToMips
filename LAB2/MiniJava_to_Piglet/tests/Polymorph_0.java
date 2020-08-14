class Polymorph_0{
	public static void main(String[] a){
		Derived_1 d;
		d = new Derived_2();
		
		
		System.out.println(d.f2(5));//6
		System.out.println(d.f1(5));//5	
		System.out.println(d.eat());//6
		
	}
}
class Base{
	int a;
	int b;
	public int f1(int t){
		a = t;
		return a;
	}
	public int eat(){
		return 0;
	}
}
class Derived_1 extends Base{
	int a;
	public int f2(int t){
		a = t + 1;
		return a;
	}
	public int eat(){
		return a;
	}
	public int f3(){
		b=3;
		return b;
	}
}
class Derived_2 extends Derived_1{
	int b;
	public int f4(){
		b=4;
		return b;
	}
	public int getB(){
		return b;
	}
}
