package i5.las2peer.services.microblogService;

import i5.las2peer.api.Service;
import i5.las2peer.p2p.AgentAlreadyRegisteredException;
import i5.las2peer.p2p.AgentNotKnownException;
import i5.las2peer.persistency.Envelope;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.*;

import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.security.Agent;
import i5.las2peer.security.AgentException;
import i5.las2peer.security.L2pSecurityException;
import i5.las2peer.security.UserAgent;
import i5.las2peer.services.microblogService.exceptions.ArtefactStorageException;

/**
 * @author Alexander
 */
@Path("microblog")
public class MicroblogService extends Service
{

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
    public String loginCheck()
    {
        return "OK";
    }

    @Path("{blog}")
    @GET
    public HttpResponse getBlog(@PathParam("blog") String blogName)
    {

        ArtefactStorage storage = getStorage();
        HttpResponse result;
        try
        {

            Microblog blog= (Microblog) storage.load(Microblog.class,blogName);

            StringBuilder output= new StringBuilder();
            output.append(blog.getName()).append("\n");
            for(int i = 0; i < blog.getChildren().size(); i++)
            {

                try
                {
                    BlogEntry entry= (BlogEntry) storage.load(BlogEntry.class,blog.getChildren().get(i));
                    output.append(entry.getOwner()).append(" wrote:\n").append(
                            BlogEntry.getDate(entry.getCreationTime())).append("\n").append(entry.getContent()).append("\n");
                }
                catch(ArtefactStorageException e)
                {
                    output.append("Could not load blog entry: ").append(blog.getChildren().get(i)).append("\n");

                }

            }
            result= new HttpResponse(output.toString(),200);
        }
        catch(ArtefactStorageException e)
        {

            result= new HttpResponse("Error retrieving blog: "+e.getMessage(),404);
        }
        return result;
    }

    @Path("{blog}")
    @PUT
    public HttpResponse createBlog(@PathParam("blog") String blogName)
    {



        ArtefactStorage storage = getStorage();
        HttpResponse result;
        try
        {
            Microblog blog= (Microblog) storage.load(Microblog.class,blogName);
            result= new HttpResponse("Blog already existing",200);
        }
        catch(ArtefactStorageException e)
        {
            try
            {
                String agentName=getAgentName();
                Microblog blog = new Microblog(blogName,agentName,"","");
                storage.save(blog);




                result= new HttpResponse("Blog created: "+blog.getId()+" by "+agentName,200);
            }
            catch(ArtefactStorageException e1)
            {

                result= new HttpResponse("Blog could not be created",400);
            }


        }
        return result;
    }

    @Path("{blog}")
    @POST
    public HttpResponse createBlogEntry(@PathParam("blog") String blogName, @ContentParam String blogEntry)
    {

        ArtefactStorage storage = getStorage();
        HttpResponse result;
        try
        {
            Microblog blog= (Microblog) storage.load(Microblog.class,blogName);

            String agentName=getAgentName();
            BlogEntry entry= new BlogEntry(agentName,blogEntry);
            storage.save(entry);

            blog.addChild(entry.getId());
            storage.save(blog);
            result= new HttpResponse("Blog entry created",200);
        }
        catch(ArtefactStorageException e1)
        {
            result= new HttpResponse("Blog entry could not be created",400);
        }
        return result;

    }

    private String getAgentName()
    {
        Agent agent=this.getContext().getMainAgent();
        return ((UserAgent)(agent)).getLoginName();
    }

    private ArtefactStorage getStorage()
    {
        ArtefactStorage storage=null;

        Agent agent=this.getContext().getMainAgent();
        storage = new DHTArtefactStorage(agent);


        return storage;
    }

   /*@Path("save")
    @GET
    public boolean save()
    {
        Envelope env;
        Agent agent=this.getContext().getMainAgent();
        try
        {
            Microblog be= new Microblog("blog1","hans","asd","wurst");
            env=Envelope.createClassIdEnvelope(be, be.getOwner(), agent);
            env.open();
            env.setOverWriteBlindly(true);
            env.updateContent(be);
            env.store();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return true;

    }

    @Path("load")
    @GET
    public String load()
    {
        Agent agent=this.getContext().getMainAgent();
        String result="";
        Envelope env;
        try
        {
            env=Envelope.fetchClassIdEnvelope(Microblog.class, "hans");
            env.open();
            Microblog be = env.getContent(Microblog.class);
            env.close();
            result=be.getContent();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


        return result;
    }*/
}
