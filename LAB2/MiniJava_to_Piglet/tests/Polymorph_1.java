class Polymorph_1{
	public static void main(String[] s){
		A a; 
		a= new B();
		System.out.println(a.f1());
	}
}
class A{
	int a;
	public int f1(){
		return this.eat();
	}
	public int eat(){
		return 1;
	}
}
class B extends A{
	int a;
	public int eat(){
		return 2;
	}
}