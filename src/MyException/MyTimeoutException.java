package MyException;

public class MyTimeoutException extends RuntimeException {

    public MyTimeoutException(String message){
        super(message);
    }
    
}
