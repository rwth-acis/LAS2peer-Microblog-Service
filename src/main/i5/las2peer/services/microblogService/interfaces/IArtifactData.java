package i5.las2peer.services.microblogService.interfaces;


import i5.las2peer.services.microblogService.exceptions.StorageException;

public interface IArtifactData<T> extends IStorable
{
    public Class<?> getType();
    public T getContent();
    public void setContent(T content) throws StorageException;

}
