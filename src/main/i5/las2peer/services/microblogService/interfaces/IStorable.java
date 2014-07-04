package i5.las2peer.services.microblogService.interfaces;

import i5.las2peer.services.microblogService.storage.ArtifactStorage;
import i5.las2peer.services.microblogService.exceptions.StorageException;

import java.io.Serializable;


public interface IStorable extends Serializable
{
    public String getId();
    void setStorage(ArtifactStorage storage);
    public void save() throws StorageException;
    public void delete() throws StorageException;
}
