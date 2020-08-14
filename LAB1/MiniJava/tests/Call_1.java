class Call_1{
	public static void main(String[] s){
		Call_1_A a;
		a = new Call_1_A();
		System.out.println(a.f1(1, 2));	
	}
} 
class Call_1_A{
	public int f1(int x, int y){
		System.out.println(x+y);
		return 0;
	}
	public int f2(int t){
		return 0;
	}
}