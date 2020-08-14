class Arrays{
	public static void main(String[] s){
		ARRAY a;
		int[] b;
		ARRAY_1 c;
		a = new ARRAY();
		b = new int[25];
		c = new ARRAY_1();
		b[24] = 6;
		System.out.println(a.f0(0, 1, 2, 3, 4, 
					5, 6, 7, 8, 9, 
					10, 11, 12, 13, 14, 
					15, 16, 17, 18, 19,
					20, b, c));
	}
}

class ARRAY{
	public int f0(int x0, int x1, int x2, int x3, int x4, 
					int x5, int x6, int x7, int x8, int x9, 
					int x10, int x11, int x12, int x13, int x14, 
					int x15, int x16, int x17, int x18, int x19,
					int x20, int[] x21, ARRAY_1 x22
					){
		return x21[24];
	}
}
class ARRAY_1{
	
	
}