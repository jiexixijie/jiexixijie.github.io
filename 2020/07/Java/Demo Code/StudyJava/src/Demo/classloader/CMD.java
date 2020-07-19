package Demo.classloader;
import java.io.IOException;

public class CMD {
    public static void main(String[] args) {
        System.out.println("CMD Class");
    }
    public static Process exec(String cmd) throws IOException {
        return Runtime.getRuntime().exec(cmd);
    }
}
