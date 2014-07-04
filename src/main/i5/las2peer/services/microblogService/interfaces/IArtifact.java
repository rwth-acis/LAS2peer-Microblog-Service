package i5.las2peer.services.microblogService.interfaces;

public interface IArtifact extends IStorable
{
    public String getOwnerId();
    public String getContentId();
    public long getCreationTime();
}
