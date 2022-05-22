package Utilities;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import MyException.MyStandardException;
import MyException.MyTimeoutException;

public class Util {
    public static ArrayList<Integer> ports;

    /**
     * permet de scanner la plage de port spécifiée en paramètres
     * @param deb debut de la plage de scan
     * @param fin fin de la plage de scan
     */
    public static void scan(int deb, int fin) {
        ports = new ArrayList<Integer>();
        for (int i = deb; i < fin; i++) {
            try {
                DatagramSocket ds = new DatagramSocket(i);
                ds.close();

            } catch (Exception e) {
                ports.add(i);
            }

        }
    }

    /**
     * Affiche le tableau ports de la classe util.
     * Il faut déjà avoir utilisé la fonction scan auparavant
     */
    public static void affichePorts() {
        for (int k : ports) {
            System.out.println("port utilisé : " + k + "\n");
        }
    }

    /**
     * permet d'attendre 500ms la réponse de l'autre machine. En case de timeout,
     * renvoie une MyTimeoutException sinon renvoie une MyStandardException
     * 
     * @param dp     datagramm packet de réception
     * @param socket socket de réception
     */
    public static void waitresponse(DatagramPacket dp, DatagramSocket socket) {
        try {
            socket.setSoTimeout(500);
            socket.receive(dp);

        } catch (java.net.SocketTimeoutException e) {
            throw new MyTimeoutException(e.getMessage());
        } catch (Exception e) {
            throw new MyStandardException(e.getMessage());
        }
        try {
            socket.setSoTimeout(0);
        } catch (Exception e) {
            throw new MyStandardException(e.getMessage());
        }
    }

}
