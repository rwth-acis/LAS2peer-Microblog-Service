package i5.las2peer.services.microblogService;

import i5.las2peer.services.microblogService.data.StringPair;
import i5.las2peer.services.microblogService.interfaces.Medium;

/**
 * @author Alexander
 */
public class Microblog extends StaticArtifact<String> implements Medium
{
    private static final long serialVersionUID = 5174121002402191223L;
    String name;
    String description;
    String id;

    public Microblog(String id, String owner, String name, String description, String content)
    {
        super(owner, content);
        this.name=name;
        this.id=id;
        this.description=description;

    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getDescription()
    {
        return description;
    }



    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public String getContent()
    {
        return getDescription();
    }

    @Override
    public StringPair[] getProperties()
    {
        StringPair[] attributes = new StringPair[]{
                new StringPair("id",this.getId()),
                new StringPair("ownerId",this.getOwnerId()),
                new StringPair("owner",this.getOwner()),
                new StringPair("time",String.valueOf(StaticArtifact.getDate(this.getCreationTime()))),
                new StringPair("name",this.getName()),
                new StringPair("description",this.getDescription())
        };
        return attributes;
    }


}
