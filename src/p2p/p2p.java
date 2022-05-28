package p2p;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

import MyException.MyTimeoutException;

public class p2p {
    public static int p_s = 5000;
    public String username;
    public HashMap<InetAddress, String> communicant; // Ip et username
    private P2Pui ui;

    public p2p(int port) throws UnknownHostException {
        p_s = port;
        communicant = new HashMap<InetAddress, String>();
        ui = new P2Pui(this);
    }

    public void start() {
        ui.startFunction();

        System.out.println("username: " + username);

        new Thread() {
            public void run() {
                DatagramSocket ds = null;
                DatagramPacket dp;
                byte[] buff;
                try {

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
            ui.sendMessage();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
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
                    byte[] data = dp.getData();
                    String message = new String(data);

                    switch (message) {
                        case ("clients"):
                            P2PStartCom.sendMap(envoi, addr, communicant);
                            break;
                        case ("UN"):
                            P2PStartCom.sendUserName(envoi, username, addr);
                            communicant.put(addr, P2PStartCom.getUserName(envoi, addr));
                            break;
                        default:
                            ui.displayInMessage(dp);
                            break;
                    }

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

    /**
     * Envoie le message au destinataire en parametre
     * 
     * @param Message  Le message à envoyer
     * @param portdest Le port de la machine de destination
     * @param addrDest L'ip de la machine de destination, une InetAddress
     */
    public void sendMessage(String Message, int portdest, InetAddress addrDest) {
        DatagramSocket ds = null;
        DatagramPacket dp;

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
    }
}
