package Demo.classloader;

public class Helloworld2 {
    public String hello() {
        return "'Hello World' from Helloworld2.class";
    }
    static {
        System.out.println("Static for classload.Helloworld2");
    }

    static public String hello(String str1,String str2){
        System.out.println("static RemoteClass:" + str1 + str2);
        return str1;
    }
}
