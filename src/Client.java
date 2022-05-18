import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {

    public Client(){        
    }

    public void sendMessage(String Message, int portdest, InetAddress addrDest){
        DatagramSocket ds;
        DatagramPacket dp;
        byte[] buff;

        try{
            ds = new DatagramSocket();
            String ligne = Message;
            byte[] message = ligne.getBytes();
            dp = new DatagramPacket(message, message.length,addrDest,  portdest);
            ds.send(dp);

            buff = new byte[128];
            DatagramPacket recep = new DatagramPacket(buff, buff.length);
            ds.receive(recep);
            InetAddress addr = recep.getAddress();
            int portEnvoi = recep.getPort();
            byte[] data = recep.getData();
            String texte = new String(data) ;
            texte = texte.substring(0, dp.getLength());
            System.out.println("Reception du port " + portEnvoi + " de la machine " + addr.getHostName() + " : " + texte);

            ds.close();

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
