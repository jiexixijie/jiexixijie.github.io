package Demo.classloader;

//import Demo.Helloworld.Helloworld1;

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
