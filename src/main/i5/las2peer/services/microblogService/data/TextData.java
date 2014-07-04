package i5.las2peer.services.microblogService.data;

import i5.las2peer.services.microblogService.interfaces.IArtifactData;
import i5.las2peer.services.microblogService.storage.IdGenerator;
import i5.las2peer.services.microblogService.exceptions.StorageException;
import i5.las2peer.services.microblogService.storage.ArtifactStorage;

/**
 * Container Class for storing String data
 */
public class TextData implements IArtifactData<String>
{
    private static final long serialVersionUID = -1581834711275375564L;

    private String content;
    private String id;
    private ArtifactStorage storage;

    public TextData (String content)
    {
        this.content=content;
        this.id= IdGenerator.generateId();
    }
    @Override
    public Class<?> getType()
    {
        return String.class;
    }

    @Override
    public String getContent()
    {
        return content;
    }

    @Override
    public void setContent(String content) throws StorageException
    {
        this.content=content;
        save();
    }

    @Override
    public void setStorage(ArtifactStorage storage)
    {
        this.storage=storage;
    }

    @Override
    public String getId()
    {
        return id;
    }


    @Override
    public void save() throws StorageException
    {
        storage.save(this);
    }

    @Override
    public void delete() throws StorageException
    {
        content="";
        save();

    }
}
