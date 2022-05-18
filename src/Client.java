import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {

    public Client() {
    }

    public void sendMessage(String Message, int portdest, InetAddress addrDest) {
        DatagramSocket ds;
        DatagramPacket dp;
        byte[] buff;

        try {
            ds = new DatagramSocket();
            String ligne = Message;
            byte[] message = ligne.getBytes();
            dp = new DatagramPacket(message, message.length, addrDest, portdest);
            ds.send(dp);

            buff = new byte[128];
            DatagramPacket recep = new DatagramPacket(buff, buff.length);
            ds.receive(recep);
            InetAddress addr = recep.getAddress();
            int portEnvoi = recep.getPort();
            byte[] data = recep.getData();
            String texte = new String(data);
            texte = texte.substring(0, dp.getLength());
            System.out
                    .println("Reception du port " + portEnvoi + " de la machine " + addr.getHostName() + " : " + texte);

            ds.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendMessage() {
        String mess; // We're going to read all user's text into a String and we try to convert it
                     // later
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); // Here you declare your
                                                                                  // BufferedReader object and instance
                                                                                  // it.
        System.out.println("Entrez un message");
        try {
            mess = br.readLine(); // We read from user's input
            if (mess.getBytes().length > 128) {
                System.out.println("Message Trop grand");
            }
            sendMessage(mess, 1666, InetAddress.getByAddress(new byte[] { 10, 42, (byte) 137, 108 }));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
