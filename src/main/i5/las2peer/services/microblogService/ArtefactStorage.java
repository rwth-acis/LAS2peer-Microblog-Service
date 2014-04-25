package i5.las2peer.services.microblogService;

import i5.las2peer.services.microblogService.exceptions.ArtefactStorageException;
import i5.las2peer.services.microblogService.interfaces.Artefact;

/**
 * @author Alexander
 */
public abstract class ArtefactStorage
{
    public abstract void save(StaticArtefact artefact) throws ArtefactStorageException;
    public abstract StaticArtefact load(Class<?> cls, String id) throws ArtefactStorageException;
    public abstract void delete(Class<?> cls, String id) throws ArtefactStorageException;

}
