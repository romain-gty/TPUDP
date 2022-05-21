public class AppServer {
    public static void main(String[] args) throws Exception {
      /* Util.scan(1024, 10000);
       Util.affichePorts();*/

       Serveur s = new Serveur(5000);
      s.start();
    }
}
