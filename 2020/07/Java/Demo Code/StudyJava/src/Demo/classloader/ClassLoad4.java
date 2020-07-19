package Demo.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

public class ClassLoad4 extends ClassLoader{
    public static void main(String[] args) {
        ClassLoad4 loadder = new ClassLoad4();
        try {
            Class aClass = loadder.loadClass("Demo.Helloworld.Helloworld1");
            Object aInstance = aClass.getDeclaredConstructor().newInstance();
            Method aMethod = aInstance.getClass().getMethod("hello");
            System.out.println(aMethod.invoke(aInstance));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //需要重载findClass
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        Class loadclass = null;
        String path = "";
        path = System.getProperty("user.dir");
        String fileSub = path + "/out/production/StudyJava/" + name.replace(".", "/");
        //对应class的路径
        String ClassFilename = fileSub + ".class";
        File ClassFile = new File(ClassFilename);
        if(ClassFile.exists() ){
            try {
                byte[] raw = getBytes(ClassFile);
                //重定义loadclass,手动将无法获取的class读取，传入defineClass
                loadclass = defineClass(name, raw, 0, raw.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(loadclass == null){
            throw new ClassNotFoundException(name);
        }
        return loadclass;
    }

    private byte[] getBytes(File f) throws IOException{
        int len = (int)f.length();
        byte raw[] = new byte[len];
        FileInputStream in = new FileInputStream(f);
        int r = in.read(raw);
        if (r != len){
            throw  new IOException("fail to read "+ f.getName());
        }
        in.close();
        return  raw;
    }
}
