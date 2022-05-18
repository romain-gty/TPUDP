import java.net.InetAddress;

public class Appclient {
    public static void main(String[] args) throws Exception {

        Client c = new Client();
        for (int i = 0; i < 10; i++) {
            //c.sendMessage();
            c.sendMessage("Hello There" + i, 5000, InetAddress.getByAddress(new byte[]{127,0,0,1}));
        }
    }
}
