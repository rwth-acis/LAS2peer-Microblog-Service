package i5.las2peer.services.microblogService.exceptions;


public class StorageException extends Exception
{
    private static final long serialVersionUID = 8652245549274332817L;

    public StorageException()
    {
    }

    public StorageException(String message)
    {
        super (message);
    }

    public StorageException(Throwable cause)
    {
        super (cause);
    }

    public StorageException(String message, Throwable cause)
    {
        super (message, cause);
    }
}
