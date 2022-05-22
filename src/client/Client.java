package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import MyException.MyStandardException;
import MyException.MyTimeoutException;
import Utilities.Util;

public class Client {

    public Client() {
    }


    /**
     * Envoie le message au destinataire en parametre
     * @param Message Le message à envoyer
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
            dp = new DatagramPacket("ok?".getBytes(), "ok?".getBytes().length, addrDest, portdest); //ouverture de la connexion
            ds.send(dp);

            buff = new byte[128]; //réception de l'acquiescement d'ouverture
            DatagramPacket dpR = new DatagramPacket(buff, buff.length);
            Util.waitresponse(dpR, ds);
            portdest = dpR.getPort();
            String messageRecu = new String(dpR.getData());
            messageRecu = messageRecu.substring(0, 2);
            if ("ok".equals(messageRecu)) { //si acquiescement reçu


                //envoi du message
                byte[] message = ligne.getBytes();
                dp = new DatagramPacket(message, message.length, addrDest, portdest);
                ds.send(dp);


                //réception du message du serveur
                buff = new byte[8192];
                DatagramPacket recep = new DatagramPacket(buff, buff.length);
                Util.waitresponse(recep, ds);
                InetAddress addr = recep.getAddress();
                int portEnvoi = recep.getPort();
                byte[] data = recep.getData();
                String texte = new String(data);
                System.out
                        .println("Reception du port " + portEnvoi + " de la machine " + addr.getHostName() + " :\n"
                                + texte + "\n");
                
                //attente de fermeture
                DatagramPacket endCom = new DatagramPacket(buff, buff.length);
                Util.waitresponse(endCom, ds);
                messageRecu = new String(endCom.getData());
                messageRecu = messageRecu.substring(0, 2);
                if ("ko".equals(messageRecu)) { //si demande de femeture reçu on aquiesce
                    dp = new DatagramPacket("ok".getBytes(), "ok".getBytes().length, addrDest, portdest);
                    ds.send(dp);
                    System.out.println("Communication effectuée avec Succès !\n");
                } else { //si pas d'acquiescement reçu on lance une erreur
                    throw new MyStandardException("Erreur lors de la fermeture de la connexion.\n");
                }

            } else { //si pas d'acquiescement reçu
                throw new MyStandardException("Connection non validée par l'hôte\n");
            }

            

        } catch (MyTimeoutException e) {
            System.out.println("Temps d'attente dépassée\n");
        }

        catch (Exception e) {
            System.out.println("Une erreur est survenue lors de la communication :\nMessage de l'ereur :\n"
                    + e.getMessage() + "\n");
        }
        ds.close(); //on ferme le socket dans tous les cas
        System.out.println("Fin de connexion\n");
    }



    /***
     * Permet à l'utilisateur d'envoyer un message par la CLI
     * @return false si l'utilsateur n'a pas demandé à quitter (en tapant q ou exit à la place du message), true sinon
     */
    public boolean sendMessage() {
        String mess;
        System.out.println("Entrez le port de destination");
        int port_dest;
        do {
            port_dest = lectureIntClavier();
        } while (port_dest == -1);

        int[] ip = new int[4];

        for (int i = 0; i < 4; i++) {
            System.out.println("Entrez l'octet " + (i + 1) + " de l'IP de destination");
            do {
                ip[i] = lectureIntClavier();
            } while (port_dest == -1);
        }

        mess = lectureStringClavier();
        if (mess.equals("q") || mess.equals("exit")) {
            System.out.println("Sortie du programme !\n");
            return true;
        } else {

            try {
                sendMessage(mess, port_dest,
                        InetAddress
                                .getByAddress(new byte[] { (byte) ip[0], (byte) ip[1], (byte) ip[2], (byte) ip[3] }));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return false;
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
