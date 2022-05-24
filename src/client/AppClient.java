package client;
//import java.net.InetAddress;

public class AppClient {
    public static void main(String[] args) throws Exception {

        Client c = new Client();
        boolean exit = false;

        c.sendBroadcast();

        /*while (!exit) {
            exit = c.sendMessage();
            //c.sendMessage("Hello There" + i, 5000, InetAddress.getByAddress(new byte[]{127,0,0,1}));
        }*/
    }
}
