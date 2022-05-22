package client;
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

        System.out.println("Entrez le port de destination");
        int port_dest;
        do {
            port_dest = lectureIntClavier();
        } while (port_dest == -1);

        int[] ip = new int[4];

        for (int i = 0; i < 4; i++) {
            System.out.println("Entrez l'octet " + i + " de l'IP de destination");
            do {
                ip[i] = lectureIntClavier();
            } while (port_dest == -1);
        }

        mess = lectureStringClavier();

        try {
            sendMessage(mess, port_dest,
                    InetAddress.getByAddress(new byte[] { (byte) ip[0], (byte) ip[1], (byte) ip[2], (byte) ip[3] }));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Lit l'entrée entière au clavier et gère les exceptions
     * 
     * @return int : l'entier lu au clavier
     */
    private int lectureIntClavier() {
        int intInValue = -1;
        String inValue;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            inValue = br.readLine();
            intInValue = Integer.parseInt(inValue);
            if (intInValue < 0) {
                throw new Exception("L'entier n'est pas positif");
            }

        } catch (Exception e) {
            System.out.println("La valeur rentrée n'est pas un entier positif");
        }

        return intInValue;
    }

    private String lectureStringClavier() {
        String mess = "";
        System.out.println("Entrez un message");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            do {
                if (mess.getBytes().length > 128) {
                    System.out.println("Message Trop grand");
                }
                mess = br.readLine();
            } while (mess.getBytes().length > 128);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return mess;
    }
}
