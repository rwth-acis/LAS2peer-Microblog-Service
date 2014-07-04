package i5.las2peer.services.microblogService.data;

/**
 * Class to store comments in the blog, same structure as blog entries
 */
public class BlogComment extends BlogEntry
{

    private static final long serialVersionUID = 6255837862755354834L;


    public BlogComment(String ownerId, String owner)
    {
        super(ownerId, owner);
    }
}
