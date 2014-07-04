package i5.las2peer.services.microblogService.storage;

import i5.las2peer.services.microblogService.exceptions.StorageException;
import i5.las2peer.services.microblogService.interfaces.IStorable;

/**
 * Abstract Storage class. A Storage class should at least be able to save, load and delete data
 */
public abstract class ArtifactStorage
{
    public abstract void save(IStorable artefact) throws StorageException;
    public abstract IStorable load(Class<? extends IStorable> cls, String id) throws StorageException;
    public abstract void delete(Class<? extends IStorable> cls, String id) throws StorageException;

}
