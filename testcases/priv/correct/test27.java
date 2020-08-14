class test27{
    public static void main(String[] a){
	System.out.println(new Test().start());
    }
}

class Test {

    int test;
    int i;

    public int start(){

	i = this.next(this.third(0));
	
	return 0;
    }

    public int next(int t){

	return t;
    }

    public int third(int i){

	return i;
    }
}
