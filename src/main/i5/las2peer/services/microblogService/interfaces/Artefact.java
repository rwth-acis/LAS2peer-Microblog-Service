package i5.las2peer.services.microblogService.interfaces;

import java.io.Serializable;

/**
 * @author Alexander
 */
public interface Artefact extends Serializable
{
    public String getOwner();
    public String getId();
    public Object getContent();
    public long getCreationTime();
}
