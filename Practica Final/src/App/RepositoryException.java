package App;

public class RepositoryException extends Exception {
    public RepositoryException(String message,Throwable cause){
        super(message);
    }

    public RepositoryException(String message){
        super(message);
    }
}
