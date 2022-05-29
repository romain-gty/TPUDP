package p2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

public class P2Pui {

    HashMap<InetAddress, String> communicant;
    p2p logique;

    public P2Pui(p2p logique) {
        this.logique = logique;
        communicant = this.logique.communicant;
    }

    /**
     * Cette fonction demande à l'utilisateur son nom d'utilisateur et l'ip d'une
     * des machines
     */
    public void startFunction() {

        String bufferString = "";
        do {
            System.out.println("Entrez votre nom d'utilisateur");
            bufferString = lectureStringClavier();
        } while (bufferString.equals(""));

        logique.username = bufferString;

        boolean notKnownIp;
        InetAddress IP = null;
        do {
            bufferString = "";
            notKnownIp = false;
            System.out.println("Entrez l'ip d'une machine connue (pas d'ip si vous êtes la première paire du réseau)");

            bufferString = lectureStringClavier();
            try {
                IP = InetAddress.getByName(bufferString);
            } catch (UnknownHostException e) {
                if (!bufferString.equals("")) {
                    notKnownIp = true;
                    System.out.println("Cette IP n'existe pas");
                }

            }

        } while (notKnownIp);

        if (!bufferString.equals("")) {

            for (HashMap.Entry<InetAddress, String> entry : P2PStartCom.getPeer(IP, logique.username).entrySet()) {
                if (entry.getValue().equals("UNinconnu")) {
                    logique.communicant.put(entry.getKey(), logique.username);
                } else {
                    logique.communicant.put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            do {
                bufferString = "";
                notKnownIp = false;
                System.out.println(
                        "Vous êtes la première machine, veuillez spécifier votre IP");

                bufferString = lectureStringClavier();
                try {
                    IP = InetAddress.getByName(bufferString);
                } catch (UnknownHostException e) {
                    notKnownIp = true;
                    System.out.println("Cette IP n'existe pas");

                }

            } while (notKnownIp);
            logique.communicant.put(IP, logique.username);
        }

    }

    /***
     * Permet à l'utilisateur d'envoyer un message par la CLI
     * 
     * @Warning bien appelé cette fonction si des gens sont dans la liste des
     *          personnes pouvant communiquer
     * @return false si l'utilsateur n'a pas demandé à quitter (en tapant q ou exit
     *         à la place du message), true sinon
     */
    public void sendMessage() {
        HashMap<Integer, InetAddress> ListIP = new HashMap<Integer, InetAddress>();
        String mess;
        
        int port_dest = 5000;
        int num_dest;
        boolean nombreNonCorrecte;
        do {int i = 1;
            for (HashMap.Entry<InetAddress, String> entry : communicant.entrySet()) {
                if (entry.getValue().equals("UNinconnu") || entry.getValue().equals("UN")) {
                    try {
                        DatagramSocket ds = new DatagramSocket();
                        communicant.put(entry.getKey(), P2PStartCom.getUserName(ds, entry.getKey()));
                    } catch (Exception e) {
                        System.out.println("Erreur lors de la réception d'un nom");
                    }
                }
                System.out.println(i + "   " + entry.getValue());
                ListIP.put(i, entry.getKey());
                i++;
            }

            System.out.println("Entrez le numéro du destinataire");
            
            
            nombreNonCorrecte = false;
            num_dest = lectureIntClavier();
            if ((num_dest <= 0) || (num_dest >= i)) {
                nombreNonCorrecte = true;
                System.out.println(
                        "Le numéro de destinataire n'est pas un entier positif ou est plus grand que le nombre de destinataires : "
                                + i);
            }
        } while (nombreNonCorrecte);
        mess = "";
        boolean messageNonCorrecte;
        do {
            messageNonCorrecte = false;
            System.out.println("entrez un message");
            mess = lectureStringClavier();
            if (mess.equals("") || mess.getBytes().length > 128) {
                System.out.println("Le message est vide ou est trop grand\nLa taille du message est de "
                        + mess.getBytes().length + " > 128");
                messageNonCorrecte =true;
            }
        } while (messageNonCorrecte);

        try {
            logique.sendMessage(mess, port_dest, ListIP.get(num_dest));
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

        } catch (Exception e) {}

        return intInValue;
    }

    /**
     * Permet la lecture d'une chaîne de caractère depuis l'entrée standard
     * 
     * @return la chaîne de caractère entrée par l'utilisateur
     */
    private String lectureStringClavier() {
        String mess = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {

            mess = br.readLine();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return mess;
    }

    /**
     * Affiche le message entrant contenu dans dp
     * 
     * @param dp un DatagramPacket contenant le message à afficher
     */
    public void displayInMessage(DatagramPacket dp) throws IOException{
        InetAddress addr = dp.getAddress();
        byte[] data = dp.getData();
        String message = new String(data);
        String expediteur = communicant.get(addr);
        message = message.substring(0, dp.getLength());

        System.out.println("\nNouveau message de " + expediteur + " :\n" + message);
    }

}
