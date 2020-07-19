package Demo.classloader;

import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoad5 {
    public static void main(String[] args) {
        try {
//            https://javaweb.org/tools/cmd.jar
             URL url = new URL("http://10.0.1.132:8080/RemoteLoad.jar");
            //创建URLClassLoader对象，远程加载jar包
            URLClassLoader ucl = new URLClassLoader(new URL[]{url});

            Class aClass = ucl.loadClass("Remote.RemoteHello");
            aClass.getMethod("hello",String.class).invoke(aClass.newInstance(),"test");
            aClass.getMethod("calc",long.class,long.class).invoke(null,123,456);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
