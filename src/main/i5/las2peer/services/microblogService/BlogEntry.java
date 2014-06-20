package i5.las2peer.services.microblogService;


import i5.las2peer.services.microblogService.data.StringPair;

/**
 * @author Alexander
 */
public class BlogEntry extends StaticArtifact<String>
{
    private static final long serialVersionUID = -1973451048652550859L;

    public BlogEntry(String ownerId, String content)
    {
        super(ownerId, content);
    }

    public BlogEntry(String ownerId, String owner, String content)
    {
        super(ownerId, owner, content);
    }

    @Override
    public StringPair[] getProperties()
    {
        StringPair[] attributes = new StringPair[]{
                new StringPair("id",this.getId()),
                new StringPair("ownerId",this.getOwnerId()),
                new StringPair("owner",this.getOwner()),
                new StringPair("time",String.valueOf(StaticArtifact.getDate(this.getCreationTime())))
        };
        return attributes;
    }


}
