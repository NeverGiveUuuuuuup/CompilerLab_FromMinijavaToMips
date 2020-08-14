class _20{
	public static void main(String[] s){
		System.out.println(10);
	}
}
class A{
	B b;
	public int f(int t){
		b = new B();
		return b.f(t);
	}

}
class B{
	int b;
	boolean c;
	public int f( int a){
		boolean d;
		c = true;
		d = false;
		if(c && d){
			b = a;
		}
		else{
			b = a * 2;
		}
		return b;
	}
	
}