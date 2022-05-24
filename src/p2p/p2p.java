package p2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

import MyException.MyStandardException;
import MyException.MyTimeoutException;
import Utilities.Util;

public class p2p {
    private int p_s;
    private String username;
    private HashMap<InetAddress, String> communicant; // Ip et username

    public p2p(int port) throws UnknownHostException {
        p_s = port;
        communicant = new HashMap<InetAddress, String>();
        communicant.put(InetAddress.getByName("10.42.134.74"), "Melinda");
        communicant.put(InetAddress.getByName("10.42.206.16"), "Romain");
    }

    public void start() {
        //System.out.println("Quel est votre nom d'utilisateur pour cette session ?");
        //username = lectureStringClavier();

        System.out.println("username: " + username);

        new Thread() {
            public void run() {
                // System.out.println("Serveur");
                DatagramSocket ds = null;
                DatagramPacket dp;
                byte[] buff;
                try {

                    // sendBroadcast();
                    buff = new byte[128];
                    ds = new DatagramSocket(p_s);
                    dp = new DatagramPacket(buff, buff.length);
                    while (true) {
                        ds.receive(dp); // en cas de réception d'une requête
                        newConnection(dp); // on lance un nouveau thread de traitement et on se remet en attente
                    }

                } catch (SocketException e) {
                    System.out.println("Erreur lors de l'ouverture de la communication");
                } catch (IOException e) {
                    System.out.println("Erreur lors de la recherche d'autres clients");
                }

                catch (Exception e) {
                    System.out.println(e.getMessage());

                }
                if (ds != null) {
                    ds.close();
                }
            }
        }.start();

        while (true) {
            sendMessage();
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void sendBroadcast() throws SocketException, IOException {

        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);
        DatagramPacket packet = new DatagramPacket("broadcast".getBytes(), "broadcast".getBytes().length,
                InetAddress.getByName("10.6.0.255"), p_s);
        socket.send(packet);
        socket.close();

    }

    /**
     * Démarre un nouveau thread et répond à la demande de connexion du client
     * 
     * @param dp DatagrammPacket contenant la demande de connexion du client, ainsi
     *           que son IP, son port...
     */
    private void newConnection(DatagramPacket dp) {
        new Thread() {
            public void run() {
                DatagramSocket envoi = null;
                try {
                    envoi = new DatagramSocket(); // ouverture d'un nouveau port pour répondre au client


                    displayInMessage(dp);
                    /*
                     * else if (texte.equals("broadcast")) {
                     * DatagramPacket broadCastResponse = new DatagramPacket(username.getBytes(),
                     * username.getBytes().length,
                     * addr,
                     * portEnvoi);
                     * envoi.send(broadCastResponse); // on acquiesce
                     * } else {
                     * if (communicant.get(addr) != null) {
                     * communicant.remove(addr);
                     * }
                     * communicant.put(addr, texte);
                     * }
                     */

                } catch (MyTimeoutException e) {
                    System.out.println("Temps d'attente dépassée\n");
                }

                catch (Exception e) {
                    System.out.println("Une erreur est survenue lors de la communication :\nMessage de l'ereur :\n"
                            + e.getMessage() + "\n");
                }
                envoi.close();
                // System.out.println("Fin de connexion\n");
            }
        }.start();
    }

    /***
     * Permet à l'utilisateur d'envoyer un message par la CLI
     * 
     * @return false si l'utilsateur n'a pas demandé à quitter (en tapant q ou exit
     *         à la place du message), true sinon
     */
    public void sendMessage() {
        // if (communicant.size() > 0) {
        HashMap<Integer, InetAddress> ListIP = new HashMap<Integer, InetAddress>();
        String mess;
        int i = 1;
        for (HashMap.Entry<InetAddress, String> entry : communicant.entrySet()) {
            System.out.println(i + "   " + entry.getValue());
            ListIP.put(i, entry.getKey());
            i++;
        }

        System.out.println("Entrez le numéro du destinataire");
        int port_dest = 5000;
        int num_dest;

        do {

            num_dest = lectureIntClavier();
        } while ((num_dest <= 0) || (num_dest >= i));
        mess = "";
        do {
            System.out.println("entrez un message");
            mess = lectureStringClavier();
        } while (mess.equals(""));

        try {
            sendMessage(mess, port_dest, ListIP.get(num_dest));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        /*
         * } else {
         * try {
         * sendBroadcast();
         * } catch (Exception e) {
         * System.out.println(e.getMessage());
         * }
         * }
         */

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

    private void displayInMessage(DatagramPacket dp) {
        InetAddress addr = dp.getAddress();
        byte[] data = dp.getData();
        String message = new String(data);
        String expediteur = communicant.get(addr);

        System.out.println("\nNouveau message de " + expediteur + " :\n" + message);

    }

    /**
     * Envoie le message au destinataire en parametre
     * 
     * @param Message  Le message à envoyer
     * @param portdest Le port de la machine de destination
     * @param addrDest L'ip de la machine de destination, une InetAddress
     */
    private void sendMessage(String Message, int portdest, InetAddress addrDest) {
        DatagramSocket ds = null;
        DatagramPacket dp;
        byte[] buff;

        try {
            ds = new DatagramSocket();
            String ligne = Message;

            // envoi du message
            byte[] message = ligne.getBytes();
            dp = new DatagramPacket(message, message.length, addrDest, portdest);
            ds.send(dp);

        } catch (MyTimeoutException e) {
            System.out.println("Temps d'attente dépassée\n");
        }

        catch (Exception e) {
            System.out.println("Une erreur est survenue lors de la communication :\nMessage de l'ereur :\n"
                    + e.getMessage() + "\n");
        }
        ds.close(); // on ferme le socket dans tous les cas
        // System.out.println("Fin de connexion\n");
    }
}
