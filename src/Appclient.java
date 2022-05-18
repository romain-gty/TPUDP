
public class Appclient {
    public static void main(String[] args) throws Exception {

        Client c = new Client();
        for (int i = 0; i < 10; i++) {
            c.sendMessage();
        }
    }
}
