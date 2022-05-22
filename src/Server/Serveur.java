package Server;

import MyException.MyTimeoutException;
import Utilities.Util;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Serveur {
    int p_s;

    /**
     * Le serveur n'est pas lancé tant que la fonction start n'est pas appellée
     * @param port
     */
    public Serveur(int port) {
        p_s = port;

    }

    /**
     * démarre le serveur sur le port spécifié par le constructeur si il est libre, sinon lance une erreur
     */
    public void start() {
        DatagramSocket ds;
        DatagramPacket dp;
        byte[] buff;
        try {
            buff = new byte[128];
            ds = new DatagramSocket(p_s);
            dp = new DatagramPacket(buff, buff.length);
            int i = 0;
            while (i < 10) {
                ds.receive(dp); //en cas de réception d'une requête
                newConnection(dp); //on lance un nouveau threade de traitement et on se remet en attente
                i++;
            }
            ds.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }

    /**
     * Démarre un nouveau thread et répond à la demande de connexion du client
     * @param dp DatagrammPacket contenant la demande de connexion du client, ainsi que son IP, son port...
     */
    private void newConnection(DatagramPacket dp) {
        new Thread() {
            public void run() {
                DatagramSocket envoi = null;
                try {
                    envoi = new DatagramSocket(); //ouverture d'un nouveau port pour répondre au client
                    InetAddress addr = dp.getAddress();
                    int portEnvoi = dp.getPort();
                    byte[] data = dp.getData();

                    String texte = new String(data);
                    texte = texte.substring(0, dp.getLength());
                    if (texte.equals("ok?")) { //si c'était une demande d'ouverture
                        DatagramPacket openCom = new DatagramPacket("ok".getBytes(), "ok".getBytes().length, addr,
                                portEnvoi);
                        envoi.send(openCom); //on acquiesce

                        Util.waitresponse(dp, envoi); //on attend la requête de l'utilisateur

                        byte[] dataToSend = dataSelector(data, dp.getLength());
                        DatagramPacket response = new DatagramPacket(dataToSend, dataToSend.length, addr, portEnvoi);
                        envoi.send(response); //on répond à la requête

                        dataToSend = "ko".getBytes(); // on demande la fermeture de connection
                        DatagramPacket endCom = new DatagramPacket(dataToSend, dataToSend.length, addr, portEnvoi);
                        envoi.send(endCom);

                        //on attend la réponse pendant 500ms, on quitte avec ou sans réponse
                        Util.waitresponse(dp, envoi);
                        System.out.println("Communication effectuée avec succès\n");
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
     * Choisi la réponse à renvoyé au client
     * @param datas le tableau de byte contenu dans le DatagramPackage de réception
     * @param lenghtMessage la taille de ce tableau
     * @return le tableau de byte contenant la réponse du server
     */
    public byte[] dataSelector(byte[] datas, int lenghtMessage) {
        switch (new String(datas).substring(0, lenghtMessage)) {
            case "index":
                return "Welcome you ! How are you ?\n".getBytes();
            case "situation":
                return "Mais, vous savez, moi je ne crois pas qu’il y ait de bonne ou de mauvaise situation. Moi, si je devais résumer ma vie aujourd’hui avec vous, je dirais que c’est d’abord des rencontres, des gens qui m’ont tendu la main, peut-être à un moment où je ne pouvais pas, où j’étais seul chez moi. Et c’est assez curieux de se dire que les hasards, les rencontres forgent une destinée… Parce que quand on a le goût de la chose, quand on a le goût de la chose bien faite, le beau geste, parfois on ne trouve pas l’interlocuteur en face, je dirais, le miroir qui vous aide à avancer. Alors ce n’est pas mon cas, comme je le disais là, puisque moi au contraire, j’ai pu ; et je dis merci à la vie, je lui dis merci, je chante la vie, je danse la vie… Je ne suis qu’amour ! Et finalement, quand beaucoup de gens aujourd’hui me disent : « Mais comment fais-tu pour avoir cette humanité ? » Eh bien je leur réponds très simplement, je leur dis que c’est ce goût de l’amour, ce goût donc qui m’a poussé aujourd’hui à entreprendre une construction mécanique, mais demain, qui sait, peut-être simplement à me mettre au service de la communauté, à faire le don, le don de soi…\n"
                        .getBytes();
            case "NOOOOOOOOOO":
                return "Luke, you can destroy the Emperor. He has foreseen this. It is your destiny. Join me, and together we can rule the galaxy as father and son. \n"
                        .getBytes();
            case "Hello there !":
                return "General Kenobi".getBytes();
            case "Execute order 66":
                return "Yes my lord".getBytes();
            case "...":
                return "Did you ever hear the tragedy of Darth Plagueis The Wise? I thought not. It’s not a story the Jedi would tell you. It’s a Sith legend. Darth Plagueis was a Dark Lord of the Sith, so powerful and so wise he could use the Force to influence the midichlorians to create life… He had such a knowledge of the dark side that he could even keep the ones he cared about from dying. The dark side of the Force is a pathway to many abilities some consider to be unnatural. He became so powerful… the only thing he was afraid of was losing his power, which eventually, of course, he did. Unfortunately, he taught his apprentice everything he knew, then his apprentice killed him in his sleep. Ironic. He could save others from death, but not himself."
                        .getBytes();

            case "woosh":
                return "You were the Chosen One! It was said that you would destroy the Sith, not join them. bring balance to the force, not leave it in darkness. You were my brother, Anakin! I loved you."
                        .getBytes();
            case "I love you":
                return "I know".getBytes();
            default:
                return "Erreur 404 : Message de réponse introuvable !\n".getBytes();

        }
    }

}
