package Demo.classloader;

import com.sun.tools.javac.Main;

import java.lang.reflect.Method;

public class ClassLoad2 {
    public static void main(String[] args) {
        try {
//          加载Helloworld2类,加载后会直接调用类中的static代码块
            Class aClass = Class.forName("Demo.classloader.Helloworld2");
//            Class aClass = Main.class.getClassLoader().loadClass("Demo.classloader.Helloworld2");
//          反射创建Helloworld2类，等价于 Helloworld a = new Helloworld();
            Object aInstance = aClass.getDeclaredConstructor().newInstance();
            Object bInstance = aClass.getDeclaredConstructor().newInstance();
//            反射获得hello方法
            Method aMethod = aInstance.getClass().getMethod("hello");
//            反射调用Hello方法，等价于String a = a.hello();
            System.out.println(aMethod.invoke(aInstance));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

