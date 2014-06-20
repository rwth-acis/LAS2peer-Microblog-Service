package i5.las2peer.services.microblogService;



import i5.las2peer.services.microblogService.data.ShortId;
import i5.las2peer.services.microblogService.data.StringPair;
import i5.las2peer.services.microblogService.interfaces.Artefact;


import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;

/**
 * @author Alexander
 */
public abstract class StaticArtifact<T extends Serializable> implements Artefact
{
    private static final long serialVersionUID = 538390869975844431L;
    private String ownerId;
    private String owner;
    private String id;
    private T content;
    private long creationTime;
    private ArrayList<String> children=new ArrayList<String>();

    @Override
    public String getOwnerId()
    {
        return ownerId;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public T getContent()
    {
        return content;
    }

    @Override
    public long getCreationTime()
    {
        return creationTime;
    }

    public String getOwner()
    {
        return owner;
    }

    public Class<?> getContentClass()
    {
        return content.getClass();
    }
    public ArrayList<String> getChildren()
    {
        return children;
    }

    public void addChild(String child)
    {
       children.add(child);
    }

    public String getChild(int pos)
    {
        if(pos<children.size())
            return children.get(pos);
        return null;
    }

    public void removeChild(String id)
    {
        children.remove(id);
    }

    public StaticArtifact(String ownerId, String owner, T content)
    {
        this.ownerId=ownerId;
        this.owner=owner;
        generateId();
        setContent(content);
        creationTime=System.currentTimeMillis();
    }
    public StaticArtifact(String ownerId, T content)
    {
        this(ownerId,ownerId,content);
    }

    private void generateId()
    {
        SecureRandom prng = new SecureRandom();
        Long randomNum = prng.nextLong();

        id= ShortId.getIdString(randomNum);
    }

    public abstract StringPair[] getProperties();

    public void setContent(T content)
    {
        this.content=content;
    }


    public void deleteContent()
    {
        content=null;
    }

    public static String getDate(long time)
    {
        Date date = new Date(time);
        DateFormat df = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        return df.format(date);
    }
}
