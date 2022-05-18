import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Serveur {
    int p_s;

    public Serveur(int port) {
        p_s = port;

    }

    public void start() {
        DatagramSocket ds;
        DatagramPacket dp;
        byte[] buff;
        try {
            buff = new byte[128];
            ds = new DatagramSocket(p_s);
            dp = new DatagramPacket(buff, buff.length);
            int i= 0;
            while (i<10) {
                ds.receive(dp);
                InetAddress addr = dp.getAddress();
                int portEnvoi = dp.getPort();
                byte[] data = dp.getData();

                String texte = new String(data);
                texte = texte.substring(0, dp.getLength());
                System.out.println(
                        "Reception du port " + portEnvoi + " de la machine " + addr.getHostName() + " : " + texte);

                DatagramPacket echo = new DatagramPacket(data, data.length, addr, portEnvoi);

                ds.send(echo);
                i++;
            }
            ds.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }

}
