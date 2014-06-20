package i5.las2peer.services.microblogService;

import i5.las2peer.services.microblogService.exceptions.ArtefactStorageException;

/**
 * @author Alexander
 */
public abstract class ArtifactStorage
{
    public abstract void save(StaticArtifact artefact) throws ArtefactStorageException;
    public abstract StaticArtifact load(Class<?> cls, String id) throws ArtefactStorageException;
    public abstract void delete(Class<?> cls, String id) throws ArtefactStorageException;

}
