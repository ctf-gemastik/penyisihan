import java.net.URL;
import java.net.URLConnection;

public class Main {
    public static void main(String[] args) throws Exception {
        URL url = new URL(args[0]);
        URLConnection conn = url.openConnection();
        conn.getInputStream();
    }
}