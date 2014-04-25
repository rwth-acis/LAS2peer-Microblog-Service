package i5.las2peer.services.microblogService.exceptions;

/**
 * @author Alexander
 */
public class ArtefactStorageException extends Exception
{
    public ArtefactStorageException ()
    {
    }

    public ArtefactStorageException (String message)
    {
        super (message);
    }

    public ArtefactStorageException (Throwable cause)
    {
        super (cause);
    }

    public ArtefactStorageException (String message, Throwable cause)
    {
        super (message, cause);
    }
}
