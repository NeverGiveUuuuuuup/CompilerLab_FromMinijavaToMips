class Call_2{
	public static void main(String[] s){
		Call_2_A a;
		boolean b;
		a = new Call_2_A();
		b = a.f2(10);
		b = a.f1(1,2,3);
				
	}
} 
class Call_2_A{
	public boolean f1(int a, int b, int c){
		c = a + b;
		System.out.println(c);
		return this.f2(c);
	}
	public boolean f2(int t){
		System.out.println(2);
		return true;
	}
}