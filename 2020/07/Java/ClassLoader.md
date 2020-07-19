# ClassLoader

----

- Demo.Helloworld.Helloworld1.java

```Java
package Demo.Helloworld;

public class Helloworld1 {
    public String hello() {
        return "'Hello World' from classloader.Helloworld1.class";
    }
}
```

- Demo.classloader.Helloworld2.java

```Java
package Demo.classloader;

public class Helloworld2 {
    public String hello() {
        return "'Hello World' from Helloworld2.class";
    }
    static {
        System.out.println("Static for classload.Helloworld2");
    }
}

```

---

## 1. New()

​	与Hellworld.java位于同一路径下(包含了对应package)时，通过new() 调用。

```Java
package Demo.classloader;

public class ClassLoad1 extends ClassLoader{
    public static void main(String[] args) {
        try {
            //Helloworld存在，直接New生成
            Helloworld2 a = new Helloworld2();
            System.out.println(a.hello());
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }
}
```

## 2. Class.forName()

### 2.1. Helloword2类已知

​	直接通过**Class.forName -> newInstance() -> getClass().getMethod -> invoke()** 调用对应函数。

```Java
package Demo.classloader;

import java.lang.reflect.Method;
/*
java Demo.classloader.ClassLoad2

Output:
	Static for classload.Helloworld2
	'Hello World' from Helloworld2.class
*/
public class ClassLoad2 {
    public static void main(String[] args) {
        try {
            //显示加载：直接动态加载类对象
            //加载Helloworld2类
            Class aClass = Class.forName("Demo.classloader.Helloworld2");
            //反射创建Helloworld2类，等价于 Helloworld a = new Helloworld();
            //aClass.newInstance was replaced by aClass.getDeclaredConstructor().newInstance()
            Object aInstance = aClass.getDeclaredConstructor().newInstance();
            //反射获得hello方法
            Method aMethod = aInstance.getClass().getMethod("hello");
            //反射调用Hello方法，等价于String a = a.hello();
            System.out.println(aMethod.invoke(aInstance));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
```

​	查看**Class.forName()**内部函数调用

```Java
@CallerSensitive
public static Class<?> forName(String className)
            throws ClassNotFoundException {
    //获取调用者的类
    Class<?> caller = Reflection.getCallerClass();
    //其中true为自动加载static
    return forName0(className, true, ClassLoader.getClassLoader(caller), caller);
}
```

​	**forName0()** 被native修饰为原生函数，底层实现。Java方法只是调用

```Java
/** Called after security check for system loader access checks have been made. */
private static native Class<?> forName0(String name, boolean initialize,
                                        ClassLoader loader,
                                        Class<?> caller)
    throws ClassNotFoundException;
```



## 3. ClassLoader().loadClass()

### 3.1. Helloword2类已知

​	与Class.forName()不同的是，在执行会下面语句后并不会打印”Static for classload.Helloworld2“，即不会自动初始化static中内容。前者在加载后对加载类的静态变量分配了存储空间，该者则没有。
```Java
package Demo.classloader;

import com.sun.tools.javac.Main;

import java.lang.reflect.Method;
/*
java Demo.classloader.ClassLoad3

Output:
Static for classload.Helloworld2   		：该语句是在newInstance()时才打印
'Hello World' from Helloworld2.class
*/
public class ClassLoad3 {
    public static void main(String[] args) {
        try {
            //与Class.forName()不同的是，在执行会下面语句后并不会打印”Static for classload.Helloworld2“，即不会自动初始化static中内容。
            //前者在加载后对加载类的静态变量分配了存储空间，该这则没有。
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

```

### 3.2. Hellowrld2类未知

​	当已经路径下没有要加载的类时，则需要我们去手动加载类(调用`defineClass`方法去JVM中注册该类)。

```Java
package Demo.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
/*
java Demo.classloader.ClassLoad4

Output:
Static for classload.Helloworld1
'Hello World' from classloader.Helloworld1.class
*/
public class ClassLoad4 extends ClassLoader{
    public static void main(String[] args) {
        ClassLoad5 loadder = new ClassLoad5();
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
```

​	查看`Class.java：loadClass()`的实现过程，可以看到如果是未知类，`findLoadedClass()`无法加载反回NULL。接下来则会尝试调用findClass()去获取对应类，默认会加载失败(`findClass()`函数实现为空)，所以需要我们去重载。

```JAVa
    protected Class<?> loadClass(String name, boolean resolve)
        throws ClassNotFoundException
    {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                long t0 = System.nanoTime();
                try {
                    if (parent != null) {
                        c = parent.loadClass(name, false);
                    } else {
                        c = findBootstrapClassOrNull(name);
                    }
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
                }

                if (c == null) {
                    // If still not found, then invoke findClass in order
                    // to find the class.
                    long t1 = System.nanoTime();
                    c = findClass(name);	//该函数则为需要我们重载的函数，return Type = Class<?>

                    // this is the defining class loader; record the stats
                    PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                    PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                    PerfCounter.getFindClasses().increment();
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }

```

​		重载过程中，在获取了对应类的的字节码后，调用`defineClass()`去JVM中注册该类。查看JDK源码，实现如下：

```Java
  /**
     * Converts an array of bytes into an instance of class {@code Class}.
     * Before the {@code Class} can be used it must be resolved.
     ...
     * @param  name
     *         The expected <a href="#binary-name">binary name</a> of the class, or
     *         {@code null} if not known
     *
     * @param  b
     *         The bytes that make up the class data.  The bytes in positions
     *         {@code off} through {@code off+len-1} should have the format
     *         of a valid class file as defined by
     *         <cite>The Java&trade; Virtual Machine Specification</cite>.
     *
     * @param  off
     *         The start offset in {@code b} of the class data
     *
     * @param  len
     *         The length of the class data
     *
     * @return  The {@code Class} object that was created from the specified
     *          class data.
     ...
     */
       protected final Class<?> defineClass(String name, byte[] b, int off, int len)
        throws ClassFormatError
    {
        return defineClass(name, b, off, len, null);
    }
	
	    protected final Class<?> defineClass(String name, byte[] b, int off, int len,
                                         ProtectionDomain protectionDomain)
        throws ClassFormatError
    {
        protectionDomain = preDefineClass(name, protectionDomain);
        String source = defineClassSourceLocation(protectionDomain);
        Class<?> c = defineClass1(this, name, b, off, len, protectionDomain, source);
        postDefineClass(c, protectionDomain);
        return c;
    }
	
     
```

### 3.3. 远程加载Helloworld类

​	使用如下文件生成对应的jar包放在服务器上用来远程加载

```Java
package Remote;

public class RemoteHello implements hello{
    @Override
    public String hello() {
        return "Remote Class Hello";
    }

    @Override
    public String hello(String str) {
        System.out.println("RemoteClass:" + str);
        return str;
    }
	//static 用于invoke(null,long.class,long.class)
    static public void calc(long a,long b){
        System.out.println("static RemoteClass: resule = " + (a + b));
    }


}
```

```Java
package Remote;

public interface hello {
    String  hello();
    String  hello(String str);
}
```

​	远程获取jar包加载类，这里有个比较坑的是，当要本地有和加载远程类路径相同的类话会优先加载本地的（即本地可获取`Remote.RemoteHello` 就不会通过url去获取了）。因为在loadClass函数中会先调用`findLoadedClass(name)`加载本地类，无法获取才会去调用`java.net.URLClassLoader#findClass(Remote.hello)`，远程获取jar包，然后通过`java.net.URLClassLoader#defineClass(Remote.RemoteHello,res)`加载。总的加载流程和 **3.2. Helloworld类未知** 相同。

```Java
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

```

