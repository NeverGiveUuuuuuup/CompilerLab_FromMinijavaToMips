class Polymorph_2{
	public static void main(String[] s){
		F a = new F();
		a.f3();
	}
}
class D{
	public int eat(){
		System.out.println("1");
		return 1;
	}
}
class E extends D{
	public int f3(){
		eat();
		return 0;
	}
	public int eat(){
		System.out.println("2");
		return 2;
	}
}
class F extends E{
	public int eat(){
		System.out.println("3");
		return 3;
	}
}
