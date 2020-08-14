class Test
{
    public static void main(String[] a)
    {
        System.out.println(new Start().start());
    }
}
class Start
{
    public int start()
    {
        return 0;
    }
}
class A
{
    public int start()
    {
        return 0;
    }
}
class B extends A
{
    public boolean start()
    {
        return false;
    }
}