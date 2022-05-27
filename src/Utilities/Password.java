package Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class Password {


    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, Exception {
        connexion();
    }

    public static boolean connexion(){
        String pass="";
        String username = "";
        byte[] numbers = {91, 66, 64, 53, 51, 51, 100, 100, 98, 97};

        while(!Listusername.containsKey(username) && !username.equalsIgnoreCase("quit")){
            username = lectureStringUsername();
            if (Listusername.containsKey(username)) {
                while (!pass.equalsIgnoreCase(Listusername.get(username))) {
                    pass = lectureStringMdp();
                    String salt = numbers.toString();
                    String securePassword = getSecurePassword(pass, salt);
                    if (pass.equalsIgnoreCase("quit")) {
                        break;
                    }

                    if (securePassword.equalsIgnoreCase(Listusername.get(username))) {
                        System.out.println("Le mot de pass est bon, vous pouvez communiquer avec le serveur");
                        break;
                    } else System.out.println("Le mot de pass est mauvais");
                }
            } else System.out.println("Utilisateur inexistant");
        }
        return true;
    }

    public static Map<String, String> Listusername;
    static {
        Listusername = new HashMap<>();
        Listusername.put("Melinda", "63607b94cbb8ce71002ee2d39cca14ab");
        Listusername.put("Romain", "6ffb57ef5565189a61a3ace543234eef");
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

    public static String lectureStringMdp() {
        String mess = "";
        System.out.println("Entrez votre mot de passe");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            do {
                if (mess.getBytes().length > 128) {
                    System.out.println("Mot de passe trop long");
                }
                mess = br.readLine();
            } while (mess.getBytes().length > 128);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return mess;
    }

    public static String lectureStringUsername() {
        String mess = "";
        System.out.println("Entrez votre Username");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            do {
                mess = br.readLine();
            } while (mess.getBytes().length > 128);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return mess;
    }
}
