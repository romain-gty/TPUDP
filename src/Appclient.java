import java.net.InetAddress;

public class Appclient {
    public static void main(String[] args) throws Exception {
       /*Util.scan(1024, 10000);
       Util.affichePorts();*/

       //Serveur s = new Serveur(5000);
       Client c= new Client(5001, 5000, InetAddress.getByAddress(new byte[]{127,0,0,1}));
    }
}
