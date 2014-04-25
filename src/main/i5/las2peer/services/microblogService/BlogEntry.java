package i5.las2peer.services.microblogService;


import i5.las2peer.persistency.XmlAble;

/**
 * @author Alexander
 */
public class BlogEntry extends StaticArtefact<String>
{
    private static final long serialVersionUID = -1973451048652550859L;

    public BlogEntry(String owner, String content)
    {
        super(owner, content);
    }


}
