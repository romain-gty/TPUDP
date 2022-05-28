package p2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
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
            logique.communicant = P2PStartCom.getPeer(IP);
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
            if (intInValue < 0) {
                throw new Exception("L'entier n'est pas positif");
            }

        } catch (Exception e) {
            System.out.println("La valeur rentrée n'est pas un entier positif");
        }

        return intInValue;
    }

    /**
     * Permet la lecture d'une chaîne de caractère depuis l'entrée standard
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
     * @param dp un DatagramPacket contenant le message à afficher
     */
    public void displayInMessage(DatagramPacket dp) {
        InetAddress addr = dp.getAddress();
        byte[] data = dp.getData();
        String message = new String(data);
        String expediteur = communicant.get(addr);

        System.out.println("\nNouveau message de " + expediteur + " :\n" + message);

    }

}
