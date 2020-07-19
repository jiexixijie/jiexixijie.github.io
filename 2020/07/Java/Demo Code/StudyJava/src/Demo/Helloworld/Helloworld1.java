package Demo.Helloworld;
import Demo.classloader.hello;

public class Helloworld1 implements hello{
    static {
        System.out.println("Static for classload.Helloworld1");
    }
    @Override
    public String hello() {
        return "'Hello World' from classloader.Helloworld1.class";
    }
    @Override
    public String hello(String str) {
        System.out.println("Helloworld1:" + str);
        return  str;
    }
}
