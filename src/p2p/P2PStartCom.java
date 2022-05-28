package p2p;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import MyException.MyTimeoutException;
import Utilities.Util;

/**
 * Cette classe comprend les fonctions nécessaires pour effectuer et répondre
 * aux demandes de noms
 */
public class P2PStartCom {
    /**
     * Récupère les noms et adresses des paires connectées
     * 
     * @param peerClient Adresse ip d'un client du réseau
     * @return HashMap<InetAddress, String> contenant l'ip d'une paie en clef et le
     *         nom d'utilisateur associé en valeur
     */
    public static HashMap<InetAddress, String> getPeer(InetAddress peerClient, String username) {
        ArrayList<InetAddress> addresses = new ArrayList<InetAddress>();
        HashMap<InetAddress, String> result = new HashMap<InetAddress, String>();

        DatagramSocket envoi = null;
        DatagramPacket dp = null;
        try {
            envoi = new DatagramSocket();

            dp = new DatagramPacket("clients".getBytes(), "clients".getBytes().length, peerClient, 5000);
            envoi.send(dp);

            byte[] buffRecep = new byte[128];
            DatagramPacket dpRecep = new DatagramPacket(buffRecep, 128);
            Util.waitresponse(dpRecep, envoi);

            byte[] data = dpRecep.getData();
            String texte = new String(data);
            System.out.println(texte);
            texte = texte.substring(0, dpRecep.getLength());
            InetAddress ipRecep = null;
            while (!texte.equals("ko")) { // tant que la machine serveur de noms n'a dit que l'on termine
                try {
                    ipRecep = InetAddress.getByName(texte);
                    addresses.add(ipRecep);
                } catch (Exception e) {
                    System.out.println("erreur lors de l'enregistrement de l'ip reçu");
                    System.out.println(e.getMessage());
                }

                buffRecep = new byte[128];
                dpRecep = new DatagramPacket(buffRecep, 128);
                Util.waitresponse(dpRecep, envoi);

                data = dpRecep.getData();
                texte = new String(data);
                System.out.println(texte);
                texte = texte.substring(0, dpRecep.getLength());
            }

            // On demande le nom d'utilisateur de chaque paire et on l'enregistre dans la
            // hashmap

            for (InetAddress ip : addresses) {
                try {
                    result.put(ip, getUserName(envoi, ip));
                } catch (MyTimeoutException e) {
                    result.put(ip, username);
                }
                System.out.println(ip.getHostAddress() + " ajoutée avec le nom " + result.get(ip));
            }

        } catch (Exception e) {
            System.out.println("Une erreur est survenue lors de la récupération des noms :\nMessage de l'ereur :\n"
                    + e.getMessage() + "\n");
        }
        envoi.close();
        System.out.println("Fin de récupération des noms\n");
        return result;
    }

    /**
     * Permet de recevoir le nopm d'utilisateur de l'IP passée en paramètres
     * 
     * @param ds   socket d'envoi de la requête
     * @param addr addresse dont on veut connaître le nom
     * @return String, nom d'utilisateur
     * @throws IOException
     */
    public static String getUserName(DatagramSocket ds, InetAddress addr) throws IOException {
        DatagramPacket dp = new DatagramPacket("UN".getBytes(), "UN".getBytes().length, addr, 5000);
        ds.send(dp);
        byte[] buffRecep = new byte[128];
        DatagramPacket dpRecep = new DatagramPacket(buffRecep, 128);
        Util.waitresponse(dpRecep, ds);
        byte[] name = dpRecep.getData();
        String text = new String(name);
        text =text.substring(0, dpRecep.getLength());
        return text;
    }

    /**
     * Envoi la hashmap d'ips à l'adresse addr par le socket ds
     * 
     * @param ds       socket d'envoi de la requête
     * @param addr
     * @param port     port d'envoie de la machine demandeuse
     * @param knownIPs
     * @throws IOException
     */
    public static void sendMap(DatagramSocket ds, InetAddress addr, int port, HashMap<InetAddress, String> knownIPs)
            throws IOException {
        for (HashMap.Entry<InetAddress, String> entry : knownIPs.entrySet()) {
            DatagramPacket packet = new DatagramPacket(entry.getKey().getHostAddress().getBytes(),
                    entry.getKey().getHostAddress().getBytes().length, addr, port);
            ds.send(packet);
            System.out.println(
                    "Entrée " + entry.getValue() + " envoyée à " + addr.getHostAddress() + " sur le port " + port);
        }
        byte[] dataToSend = "ko".getBytes();
        DatagramPacket endCom = new DatagramPacket(dataToSend, dataToSend.length, addr, port);
        ds.send(endCom);
    }

    /**
     * Envoi son nom d'utilisateur à la machine demandeuse
     * 
     * @param ds       socket d'envoi de la requête
     * @param userName nom d'utilisateur à envoyer
     * @param addr     adresse de la machine demandeuse
     * @param port     port d'envoie de la machine demandeuse
     * @throws IOException
     */
    public static void sendUserName(DatagramSocket ds, String userName, InetAddress addr, int port) throws IOException {
        DatagramPacket packet = new DatagramPacket(userName.getBytes(), userName.getBytes().length, addr, port);
        ds.send(packet);

    }

}
