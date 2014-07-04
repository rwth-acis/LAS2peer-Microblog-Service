package i5.las2peer.services.microblogService.data;


import i5.las2peer.services.microblogService.exceptions.StorageException;

/**
 * Class to manage blog entries. Content is stored as string, children are @link{BlogComment}
 */
public class BlogEntry extends StaticArtifact<String, TextData, BlogComment>
{
    private static final long serialVersionUID = -1973451048652550859L;

    public BlogEntry(String ownerId, String owner)
    {
        super(TextData.class, BlogComment.class, ownerId, owner);
    }

    /**
     *
     * @return empty string, if no content stored
     * @throws StorageException
     */
    @Override
    public String readContent() throws StorageException
    {
        String result=super.readContent();
        if(result==null)
            result="";
        return  result;

    }

    /**
     *
     * @return id, ownerId, owner and time
     */
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
