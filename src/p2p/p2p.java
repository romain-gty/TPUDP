package p2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

import MyException.MyStandardException;
import MyException.MyTimeoutException;
import Utilities.Util;

public class p2p {
    private int p_s;
    private String username;
    private HashMap<InetAddress, String> communicant; // Ip et username

    public p2p(int port) {
        p_s = port;
    }

    public void start() {
        System.out.println("Quel est votre nom d'uitilisateur pour cette session ?");
        username = lectureStringClavier();

        DatagramSocket ds = null;
        DatagramPacket dp;
        byte[] buff;

        new Thread() {
            public void run() {
                try {
                    sendBroadcast();
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
                ds.close();
            }
        }.start();

        boolean a = true;
        while (a) {
            sendMessage();
        }
    }

    private void sendBroadcast() throws SocketException, IOException {

        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);
        DatagramPacket packet = new DatagramPacket("broadcast".getBytes(), "broadcast".getBytes().length,
                InetAddress.getByName("255.255.255.255"), p_s);
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
                    InetAddress addr = dp.getAddress();
                    int portEnvoi = dp.getPort();
                    byte[] data = dp.getData();

                    String texte = new String(data);
                    System.out.println(texte);
                    texte = texte.substring(0, dp.getLength());

                    if (texte.equals("ok?")) { // si c'était une demande d'ouverture
                        DatagramPacket openCom = new DatagramPacket("ok".getBytes(), "ok".getBytes().length, addr,
                                portEnvoi);
                        envoi.send(openCom); // on acquiesce

                        Util.waitresponse(dp, envoi); // on attend la requête de l'utilisateur

                        displayInMessage(dp);

                        byte[] dataToSend = "ko".getBytes(); // on demande la fermeture de connection
                        DatagramPacket endCom = new DatagramPacket(dataToSend, dataToSend.length, addr, portEnvoi);
                        envoi.send(endCom);

                        // on attend la réponse pendant 500ms, on quitte avec ou sans réponse
                        Util.waitresponse(dp, envoi);
                        System.out.println("Communication effectuée avec succès\n");
                    } else if (texte.equals("broadcast")) {
                        DatagramPacket broadCastResponse = new DatagramPacket(username.getBytes(),
                                username.getBytes().length,
                                addr,
                                portEnvoi);
                        envoi.send(broadCastResponse); // on acquiesce
                    } else {
                        sendBroadcast();
                        if (communicant.get(addr) != null) {
                            communicant.remove(addr);
                        }
                        communicant.put(addr, texte);
                    }

                } catch (MyTimeoutException e) {
                    System.out.println("Temps d'attente dépassée\n");
                }

                catch (Exception e) {
                    System.out.println("Une erreur est survenue lors de la communication :\nMessage de l'ereur :\n"
                            + e.getMessage() + "\n");
                }
                envoi.close();
                System.out.println("Fin de connexion\n");
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
        HashMap<Integer, InetAddress> ListIP = new HashMap<Integer, InetAddress>();
        String mess;
        int i = 1;
        for (HashMap.Entry<InetAddress, String> entry : communicant.entrySet()) {
            System.out.println(i + '\t' + entry.getValue());
            ListIP.put(i, entry.getKey());
        }
        System.out.println("Entrez le numéro du destinataire");
        int port_dest = 5000;
        int num_dest;

        do {
            num_dest = lectureIntClavier();
        } while ((num_dest == -1) || (num_dest >= i));

        mess = lectureStringClavier();

        try {
            sendMessage(mess, port_dest, ListIP.get(num_dest));
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

    private void displayInMessage(DatagramPacket dp) {
        InetAddress addr = dp.getAddress();
        byte[] data = dp.getData();
        String message = new String(data);
        String expediteur = communicant.get(addr);

        System.out.println("Nouveau message de " + expediteur + " :\n" + message);

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
            dp = new DatagramPacket("ok?".getBytes(), "ok?".getBytes().length, addrDest, portdest); // ouverture de la
                                                                                                    // connexion
            ds.send(dp);

            buff = new byte[128]; // réception de l'acquiescement d'ouverture
            DatagramPacket dpR = new DatagramPacket(buff, buff.length);
            Util.waitresponse(dpR, ds);
            portdest = dpR.getPort();
            String messageRecu = new String(dpR.getData());
            messageRecu = messageRecu.substring(0, 2);
            if ("ok".equals(messageRecu)) { // si acquiescement reçu

                // envoi du message
                byte[] message = ligne.getBytes();
                dp = new DatagramPacket(message, message.length, addrDest, portdest);
                ds.send(dp);

                // attente de fermeture
                DatagramPacket endCom = new DatagramPacket(buff, buff.length);
                Util.waitresponse(endCom, ds);
                messageRecu = new String(endCom.getData());
                messageRecu = messageRecu.substring(0, 2);
                if ("ko".equals(messageRecu)) { // si demande de femeture reçu on aquiesce
                    dp = new DatagramPacket("ok".getBytes(), "ok".getBytes().length, addrDest, portdest);
                    ds.send(dp);
                    System.out.println("Communication effectuée avec Succès !\n");
                } else { // si pas d'acquiescement reçu on lance une erreur
                    throw new MyStandardException("Erreur lors de la fermeture de la connexion.\n");
                }

            } else { // si pas d'acquiescement reçu
                throw new MyStandardException("Connection non validée par l'hôte\n");
            }

        } catch (MyTimeoutException e) {
            System.out.println("Temps d'attente dépassée\n");
        }

        catch (Exception e) {
            System.out.println("Une erreur est survenue lors de la communication :\nMessage de l'ereur :\n"
                    + e.getMessage() + "\n");
        }
        ds.close(); // on ferme le socket dans tous les cas
        System.out.println("Fin de connexion\n");
    }
}
