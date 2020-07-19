package Demo.classloader;

import com.sun.tools.javac.Main;

import javax.print.DocFlavor;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class test extends ClassLoader{
    public static void main(String[] args) {
        test a = new test();
        try {
            Class aCLass = a.loadClass("Demo.classloader.Helloworld2");
            Method aMehtod = aCLass.getMethod("hello",String.class,String.class);
            aMehtod.invoke(null,"test","test");
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }
}
