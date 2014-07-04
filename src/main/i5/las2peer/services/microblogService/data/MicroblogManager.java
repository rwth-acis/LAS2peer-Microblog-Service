package i5.las2peer.services.microblogService.data;

import i5.las2peer.services.microblogService.interfaces.IMedium;

/**
 * @author Alexander
 */
public class MicroblogManager extends StaticArtifact<String, TextData, Microblog> implements IMedium
{
    private static final long serialVersionUID = 4110864850042029237L;
    public static final String NAME = "MicroblogManager";
    public static final String DESCRIPTION = "List of all registered microblogs";

    public MicroblogManager(Class<TextData> contentBoxClass, Class<Microblog> microblogClass, String ownerId, String owner)
    {
        super(contentBoxClass, microblogClass, ownerId, owner);
    }

    public MicroblogManager(Class<TextData> contentBoxClass, Class<Microblog> microblogClass, String ownerId)
    {
        super(contentBoxClass, microblogClass, ownerId);
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public String getDescription()
    {
        return DESCRIPTION;
    }

    @Override
    public StringPair[] getProperties()
    {
        StringPair[] attributes = new StringPair[]{
                new StringPair("name",this.getName()),
                new StringPair("description",this.getDescription())
        };
        return attributes;
    }
    @Override
    public String getId()
    {
        return NAME;
    }

    @Override
    public String readContent()
    {
        return "";
    }
}
