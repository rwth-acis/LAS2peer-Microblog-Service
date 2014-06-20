package i5.las2peer.services.microblogService;

import i5.las2peer.api.Service;
import i5.las2peer.p2p.AgentNotKnownException;
import i5.las2peer.restMapper.MediaType;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.*;

import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.security.Agent;
import i5.las2peer.security.UserAgent;
import i5.las2peer.services.microblogService.data.XMLResult;
import i5.las2peer.services.microblogService.exceptions.ArtefactStorageException;

import java.util.ArrayList;

/**
 * @author Alexander
 */
@Path("microblog")
@Produces(MediaType.TEXT_XML)
public class MicroblogService extends Service
{

    public static final String TRUE=String.valueOf(true);
    public static final String FALSE=String.valueOf(false);
    public static final int HTTP_OK=200;
    public static final int HTTP_CREATED=201;
    public static final int HTTP_NOT_FOUND=404;
    public static final int HTTP_ERROR=500;

    public String getRESTMapping()
    {
        String result="";
        try
        {
            result= RESTMapper.getMethodsAsXML(this.getClass());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }


    @GET
    public String isRunning()
    {
        XMLResult xmlResult= new XMLResult();
        xmlResult.appendElement("isRunning",TRUE);
        return xmlResult.toString();
    }

    /**
     * Creates a new blog
     * @param blogName Name of the blog to create
     * @return if blog already exists, a 200, else 201
     */
    @Path("{blog}")
    @PUT
    public HttpResponse createBlog(@PathParam("blog") String blogName, @QueryParam(name="name",defaultValue = "") String name, @QueryParam(name="description",defaultValue = "") String description)
    {



        ArtifactStorage storage = getStorage();
        HttpResponse result;
        try //first try to load blog
        {
            Microblog blog= (Microblog) storage.load(Microblog.class,blogName);
            result= new HttpResponse(getIdXML(blog),HTTP_OK); //already existing, so do nothing
        }
        catch(ArtefactStorageException e)
        {
            try
            {

                if(name.trim().length()==0)
                    name=blogName;

                String agentId=getAgentId();
                Microblog blog = new Microblog(blogName,agentId,name,description,"");
                storage.save(blog);
                result= new HttpResponse(getIdXML(blog),HTTP_CREATED);

            }
            catch(ArtefactStorageException e1)
            {
                result= new HttpResponse(getErrorXML("Storage error: " + e1.getMessage()),HTTP_ERROR);
            }
        }
        return result;
    }

    /**
     * Post a new entry to a blog
     * @param blogName
     * @param blogEntry
     * @return
     */
    @Path("{blog}")
    @POST
    public HttpResponse createBlogEntry(@PathParam("blog") String blogName, @ContentParam String blogEntry)
    {
        BlogEntry entry= new BlogEntry(getAgentId(), getAgentName(), blogEntry);
        return createResource(entry,Microblog.class,blogName);

    }

    /**
     * Creates a comment for an entry
     * @param entryId
     * @param content
     * @return
     */
    @Path("comments/{entryId}")
    @POST
    public HttpResponse createBlogComment(@PathParam("entryId") String entryId, @ContentParam String content)
    {
        BlogComment comment = new BlogComment(getAgentId(), getAgentName(),content);
        return createResource(comment,BlogEntry.class,entryId);
    }
    public HttpResponse createResource(StaticArtifact resource, Class<?> clsParent, String parentId)
    {
        ArtifactStorage storage = getStorage();
        HttpResponse result=null;
        StaticArtifact parent=null;
        try
        {
            try
            {
                parent=  storage.load(clsParent, parentId);
            }
            catch(ArtefactStorageException e)
            {
                result= new HttpResponse(getErrorXML("Parent \""+parentId+"\" not found! "+e.getMessage()),HTTP_NOT_FOUND);
            }

            if(parent!=null)
            {
                storage.save(resource);
                parent.addChild(resource.getId());
                storage.save(parent);
                result= new HttpResponse(getIdXML(resource),HTTP_CREATED);
            }
        }
        catch(ArtefactStorageException e1)
        {
            result= new HttpResponse(getErrorXML("Resource could not be created! " +e1.getMessage()),HTTP_ERROR);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public HttpResponse getResource(Class<?> clsParent, Class<?> clsChildren, String resourceId)
    {
        ArtifactStorage storage = getStorage();
        HttpResponse result=null;
        XMLResult xmlResult = new XMLResult();
        StaticArtifact resource=null;
        try
        {
            resource=  storage.load(clsParent,resourceId);
        }
        catch(ArtefactStorageException e)
        {
            result= new HttpResponse(getErrorXML("Resource \""+resourceId+"\" not found! "+e.getMessage()),HTTP_NOT_FOUND);
        }

        if(resource!=null)
        {
            xmlResult.appendElement("resource",resource.getProperties(),resource.getContent().toString());

            for(int i = 0; i < resource.getChildren().size(); i++)
            {
                try
                {
                    StaticArtifact child=  storage.load(clsChildren, ((ArrayList<String>) resource.getChildren()).get(i));
                    xmlResult.appendElement("child",child.getProperties(),"");
                }
                catch(ArtefactStorageException e1)
                {
                    //ignore unreadable entries
                }
            }
            result= new HttpResponse(xmlResult.toString(),HTTP_OK);
        }
        return result;
    }

    /**
     * Retrieves blog object with list of entries
     * @param blogName
     * @return
     */
    @Path("{blog}")
    @GET
    public HttpResponse getBlog(@PathParam("blog") String blogName)
    {
        return getResource(Microblog.class,BlogEntry.class,blogName);
    }

    /**
     * Retrieves entry with the given id
     * @param entryId
     * @return
     */
    @Path("entries/{entryId}")
    @GET
    public HttpResponse getBlogEntry(@PathParam("entryId") String entryId)
    {
        return getResource(BlogEntry.class,BlogComment.class,entryId);
    }

    /**
     * Retrieves a comment with the given id
     * @param commentId
     * @return
     */
    @Path("comments/{commentId}")
    @GET
    public HttpResponse getComment(@PathParam("commentId") String commentId)
    {
        return getResource(BlogComment.class, BlogComment.class, commentId);
    }





    private String getAgentName()
    {
        Agent agent=this.getContext().getMainAgent();
        return ((UserAgent)(agent)).getLoginName();
    }
    private String getAgentName(String id) throws AgentNotKnownException
    {
        Long lid=Long.parseLong(id);
        Agent agent=this.getContext().getAgent(lid);
        return ((UserAgent)(agent)).getLoginName();
    }
    private String getAgentId()
    {
        return String.valueOf(this.getContext().getMainAgent().getId());
    }
    private ArtifactStorage getStorage()
    {
        ArtifactStorage storage=null;

        Agent agent=this.getContext().getMainAgent();
        storage = new DHTArtifactStorage(agent);


        return storage;
    }
    private String getErrorXML(String error)
    {
        XMLResult xmlResult= new XMLResult();
        xmlResult.appendElement("error",error);
        return xmlResult.toString();
    }
    private String getIdXML(StaticArtifact artifact)
    {
        XMLResult xmlResult= new XMLResult();
        xmlResult.appendElement("id",artifact.getId());
        return xmlResult.toString();
    }



}
