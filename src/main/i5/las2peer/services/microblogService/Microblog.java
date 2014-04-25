package i5.las2peer.services.microblogService;

import i5.las2peer.services.microblogService.interfaces.Medium;

import java.io.Serializable;

/**
 * @author Alexander
 */
public class Microblog extends StaticArtefact<String> implements Medium
{
    private static final long serialVersionUID = 5174121002402191223L;
    String name;
    String description;


    public Microblog(String name, String owner, String description, String content)
    {
        super(owner, content);
        this.name=name;
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
        return name;
    }

    @Override
    public String getContent()
    {
        return getDescription();
    }


}
