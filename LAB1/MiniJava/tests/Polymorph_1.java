class Polymorph_1{
	public static void main(String[] s){
		A a = new B();
		a.f1();
	}
}
class A{
	int a;
	public int f1(){
		eat();
		this.eat();
		return 0;
	}
	public int eat(){
		System.out.println("1");
		return 1;
	}
}
class B extends A{
	int a;
	public int eat(){
		System.out.println("2");
		return 2;
	}
}