import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Serveur {

    public Serveur(int port) {
        DatagramSocket ds;
        DatagramPacket dp;
        byte[] buff;
        try {
            buff = new byte[128];
            ds = new DatagramSocket(port);
            dp = new DatagramPacket(buff, buff.length);
            ds.receive(dp);
            InetAddress addr = dp.getAddress();
            int portEnvoi = dp.getPort();
            byte[] data = dp.getData();

            String texte = new String(data) ;
            texte = texte.substring(0, dp.getLength());
            System.out.println("Reception du port " + portEnvoi + " de la machine " + addr.getHostName() + " : " + texte);

            DatagramPacket echo = new DatagramPacket(data, data.length, addr, portEnvoi);
            
            ds.send(echo);
            ds.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());

        }

    }

}
