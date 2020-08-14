class Test
{
    public static void main(String[] a)
    {
        System.out.println(new Start().start());
    }
}
class A
{

}
class B extends A
{

}
class Start
{
    public int start()
    {
        A a;
        B x;
        a=new B();
        x=new A();
        return 0;
    }
}