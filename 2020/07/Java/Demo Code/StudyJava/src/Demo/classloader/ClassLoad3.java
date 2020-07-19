package Demo.classloader;

import com.sun.tools.javac.Main;

import java.lang.reflect.Method;

public class ClassLoad3 {
    public static void main(String[] args) {
        try {
            //与Class.forName()不同的是，在执行会下面语句后并不会打印”Static for classload.Helloworld2“，即不会自动初始化static中内容。
            //前者在加载后对加载类的静态变量分配了存储空间，该者则没有。
            Class aClass = Main.class.getClassLoader().loadClass("Demo.classloader.Helloworld2");
//          反射创建Helloworld2类，等价于 Helloworld a = new Helloworld();
            Object aInstance = aClass.getDeclaredConstructor().newInstance();
            Method aMethod = aInstance.getClass().getMethod("hello");
            System.out.println(aMethod.invoke(aInstance));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
