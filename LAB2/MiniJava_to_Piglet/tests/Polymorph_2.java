class Polymorph_2{
	public static void main(String[] s){
		F a;
        a = new F();
		System.out.println(a.f3());
	}
}
class D{
	public int eat(){
		return 1;
	}
}
class E extends D{
	public int f3(){
		return this.eat();
	}
	public int eat(){
		return 2;
	}
}
class F extends E{
	public int eat(){
		return 3;
	}
}
