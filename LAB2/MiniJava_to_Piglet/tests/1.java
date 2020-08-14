class test35{
    public static void main(String[] a){
		System.out.println(new Test().start());
    }
}

class Test {
    
	public int start(){
		return this.next(this);
    }

    public int next(Test t){
		return 0;
    }
}
