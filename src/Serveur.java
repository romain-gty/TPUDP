import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class Serveur {
    int p_s;

    public Serveur(int port) {
        p_s = port;

    }

    public void start() throws NoSuchAlgorithmException, NoSuchProviderException {
        DatagramSocket ds;
        DatagramPacket dp;
        byte[] buff;
        try {
            buff = new byte[128];
            ds = new DatagramSocket(p_s);
            dp = new DatagramPacket(buff, buff.length);
            int i = 0;
            while (i < 10) {
                ds.receive(dp);
                new Thread() {
                    public void run() {
                        DatagramSocket envoi = null;
                        try {
                            envoi = new DatagramSocket();
                            InetAddress addr = dp.getAddress();
                            int portEnvoi = dp.getPort();
                            byte[] data = dp.getData();

                            String texte = new String(data);
                            texte = texte.substring(0, dp.getLength());

                            /*Décomposition des informations du message dans texte */
                            String[] words = texte.split(";");
                            String username = words[0];
                            String mdp = words[1];
                            String mess = words[2];


                            System.out.println(
                                    "Reception du port " + portEnvoi + " de la machine " + addr.getHostName() + " : "
                                            + mess);

                            DatagramPacket echo = new DatagramPacket(data, data.length, addr, portEnvoi);
                            //Si les informations de connexions sont bonnes, on echo le message

                            if(connexion(username,mdp)){
                                envoi.send(echo);
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        envoi.close();
                    }
                }.start();

                i++;
            }
            ds.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }

    public static boolean connexion(String username, String pass){
            if (Listusername.containsKey(username)) {
                    String salt = ListSalt.get(username);
                    String securePassword = getSecurePassword(pass,salt);
                    if (securePassword.equalsIgnoreCase(Listusername.get(username))) {
                        System.out.println("Le mot de pass est bon, vous pouvez communiquer avec le serveur");
                    } else System.out.println("Le mot de pass est mauvais");

                } else System.out.println("Utilisateur inexistant");
        return true;
    }

    public static Map<String, String> Listusername;
    static {
        Listusername = new HashMap<>();
        Listusername.put("Melinda", "a892e0f6bc6e635a63d748f293750e9d");
        Listusername.put("Romain", "7cede59cc3443263f3a5f7e1763b4550");
    }
    public static Map<String, String> ListSalt;
    static {
        ListSalt = new HashMap<>();
        ListSalt.put("Melinda", "91,66,64,53,51,51,100,100,98,97");
        ListSalt.put("Romain", "92,68,64,53,51,51,15,100,98,99");
    }

    private static String getSecurePassword(String passwordToHash,
                                            String salt) {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Add password bytes to digest
            md.update(salt.getBytes());

            // Get the hash's bytes
            byte[] bytes = md.digest(passwordToHash.getBytes());

            // This bytes[] has bytes in decimal format;
            // Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }

            // Get complete hashed password in hex format
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }


    //Classe static afin de conserver le même Salt
    public static String getSalt() throws NoSuchAlgorithmException, NoSuchProviderException
    {
        // Always use a SecureRandom generator
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
        // Create array for salt
        byte[] salt = new byte[16];
        // Get a random salt
        sr.nextBytes(salt);
        // return salt
        return salt.toString();

    }
}
