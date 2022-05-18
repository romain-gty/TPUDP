import java.net.DatagramSocket;
import java.util.ArrayList;

public class Util {
    public static ArrayList<Integer> ports;

    public  static void scan(int deb, int fin){
        ports = new ArrayList<Integer>();
        for(int i= deb; i<fin; i++){
            try{
                DatagramSocket ds = new DatagramSocket(i);
                ds.close();
                
            }catch(Exception e){                
                ports.add(i);
            }

        }
    }

    public static void affichePorts(){
        for(int k  :ports){
            System.out.println("port utilisé : "+k+"\n" );
        }
    }

}
