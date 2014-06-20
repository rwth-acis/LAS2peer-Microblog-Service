package i5.las2peer.services.microblogService;

/**
 * @author Alexander
 */
public class BlogComment extends BlogEntry
{

    private static final long serialVersionUID = 6255837862755354834L;

    public BlogComment(String ownerId, String content)
    {
        super(ownerId, content);
    }

    public BlogComment(String ownerId, String owner, String content)
    {
        super(ownerId, owner, content);
    }
}
