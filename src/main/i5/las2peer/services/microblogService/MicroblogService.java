package i5.las2peer.services.microblogService;

import java.net.HttpURLConnection;

import i5.las2peer.api.Service;
import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.restMapper.MediaType;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.ContentParam;
import i5.las2peer.restMapper.annotations.GET;
import i5.las2peer.restMapper.annotations.POST;
import i5.las2peer.restMapper.annotations.PUT;
import i5.las2peer.restMapper.annotations.Path;
import i5.las2peer.restMapper.annotations.PathParam;
import i5.las2peer.restMapper.annotations.Produces;
import i5.las2peer.restMapper.annotations.QueryParam;
import i5.las2peer.security.Agent;
import i5.las2peer.security.UserAgent;
import i5.las2peer.services.microblogService.data.BlogComment;
import i5.las2peer.services.microblogService.data.BlogEntry;
import i5.las2peer.services.microblogService.data.Microblog;
import i5.las2peer.services.microblogService.data.MicroblogManager;
import i5.las2peer.services.microblogService.data.StaticArtifact;
import i5.las2peer.services.microblogService.data.TextData;
import i5.las2peer.services.microblogService.data.XMLResult;
import i5.las2peer.services.microblogService.exceptions.StorageException;
import i5.las2peer.services.microblogService.interfaces.IStorable;
import i5.las2peer.services.microblogService.storage.ArtifactStorage;
import i5.las2peer.services.microblogService.storage.DHTArtifactStorage;

@Path("microblog")
@Produces(MediaType.TEXT_XML)
public class MicroblogService extends Service {

	public static final String TRUE = String.valueOf(true);
	public static final String FALSE = String.valueOf(false);

	/**
	 * Standard mapping retrieval for the WebCoonnector
	 *
	 * @return
	 */
	public String getRESTMapping() {
		String result = "";
		try {
			result = RESTMapper.getMethodsAsXML(this.getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Simple check, if the service is running
	 *
	 * @return
	 */
	@GET
	public String isRunning() {
		XMLResult xmlResult = new XMLResult();
		xmlResult.appendElement("isRunning", TRUE);
		return xmlResult.toString();
	}

	/**
	 *
	 * @return a list of all blogs registered in the system
	 */
	@Path("blogs")
	@GET
	public HttpResponse getBlogs() {
		return getResource(MicroblogManager.class, Microblog.class, MicroblogManager.NAME);
	}

	/**
	 * Creates a blog
	 *
	 * @param blogName    blogid (only id that the user can choose)
	 * @param name        name of the blog for public display
	 * @param description description of the blog
	 * @return {@value java.net.HttpURLConnection#HTTP_OK}, if blog already exists, {@value HttpURLConnection#HTTP_CREATED}, if a new one was created
	 */
	@Path("blogs/{blog}")
	@PUT
	public HttpResponse createBlog(@PathParam("blog") String blogName,
			@QueryParam(name = "name", defaultValue = "") String name,
			@QueryParam(name = "description", defaultValue = "") String description) {

		ArtifactStorage storage = getStorage();
		HttpResponse result;
		try { // first try to load blog
			Microblog blog = (Microblog) storage.load(Microblog.class, blogName);
			result = new HttpResponse(getIdXML(blog), HttpURLConnection.HTTP_OK); // already existing, so do nothing
		} catch (StorageException e) {
			try {
				if (name.trim().length() == 0) {
					name = blogName;
				}
				Microblog blog = new Microblog(blogName, getAgentId(), getAgentName(), name, description);
				// add to manager
				MicroblogManager manager = null;
				try {
					manager = (MicroblogManager) storage.load(MicroblogManager.class, MicroblogManager.NAME);
				} catch (StorageException e2) {
					manager = new MicroblogManager(TextData.class, Microblog.class, getAgentId());
					manager.setStorage(storage);
				}
				manager.addChild(blog);

				result = new HttpResponse(getIdXML(blog), HttpURLConnection.HTTP_CREATED);
			} catch (StorageException e1) {
				result = getErrorResponse("Storage error: ", e1, HttpURLConnection.HTTP_INTERNAL_ERROR);
			}
		}
		return result;
	}

	/**
	 * Edits the text content of a blog entry
	 *
	 * @param entryId          id of the entry to edit
	 * @param blogEntryContent new content
	 * @return {@value java.net.HttpURLConnection#HTTP_OK} if it worked or else {@value HttpURLConnection#HTTP_NOT_FOUND}
	 */
	@Path("edit/entries/{entryId}")
	@POST
	public HttpResponse editBlogEntry(@PathParam("entryId") String entryId, @ContentParam String blogEntryContent) {
		return editResource(BlogEntry.class, entryId, blogEntryContent);
	}

	/**
	 * Edits the text content of a comment
	 *
	 * @param entryId            id of the comment to edit
	 * @param blogCommentContent new content
	 * @return {@value java.net.HttpURLConnection#HTTP_OK} if it worked or else {@value HttpURLConnection#HTTP_NOT_FOUND}
	 */
	@Path("edit/comments/{commentId}")
	@POST
	public HttpResponse editBlogComment(@PathParam("commentId") String entryId, @ContentParam String blogCommentContent) {
		return editResource(BlogComment.class, entryId, blogCommentContent);
	}

	/**
	 * Method edits the TextData content of a resource
	 *
	 * @param clsResource class of the resource to edit
	 * @param resourceId  the id of the resource to edit
	 * @param newContent  the new content to apply
	 * @return {@value java.net.HttpURLConnection#HTTP_OK} if it worked or else {@value HttpURLConnection#HTTP_NOT_FOUND}
	 */
	private HttpResponse editResource(Class<? extends IStorable> clsResource, String resourceId, String newContent) {
		ArtifactStorage storage = getStorage();
		HttpResponse result = null;
		StaticArtifact<?, ?, ?> resource = null;
		try {
			resource = (StaticArtifact<?, ?, ?>) storage.load(clsResource, resourceId);
		} catch (StorageException e) {
			result = getErrorResponse("Resource \"" + resourceId + "\" not found! ", e, HttpURLConnection.HTTP_NOT_FOUND);
		}

		result = new HttpResponse("", HttpURLConnection.HTTP_NOT_FOUND);
		TextData content;
		if (resource != null) {

			if (resource.getContentId() != null) {

				try {
					content = (TextData) resource.getContent();
					content.setContent(newContent);
					result = new HttpResponse("", HttpURLConnection.HTTP_OK);
				} catch (StorageException e) {
					result = getErrorResponse("Content with id \"" + resource.getContentId() + "\" of resource \""
							+ resourceId + "\" not found! ", e, HttpURLConnection.HTTP_NOT_FOUND);
				}
			}
		}

		return result;
	}

	/**
	 * Post a new entry to a blog
	 *
	 * @param blogName         id of the blog
	 * @param blogEntryContent content of the entry
	 * @return
	 */
	@Path("blogs/{blog}")
	@POST
	public HttpResponse createBlogEntry(@PathParam("blog") String blogName, @ContentParam String blogEntryContent) {
		BlogEntry entry = new BlogEntry(getAgentId(), getAgentName());
		return createResource(entry, blogEntryContent, Microblog.class, blogName);

	}

	/**
	 * Creates a comment for an entry
	 *
	 * @param entryId id of the entry
	 * @param content contento f the comment
	 * @return
	 */
	@Path("comments/{entryId}")
	@POST
	public HttpResponse createBlogComment(@PathParam("entryId") String entryId, @ContentParam String content) {
		BlogComment comment = new BlogComment(getAgentId(), getAgentName());
		return createResource(comment, content, BlogEntry.class, entryId);
	}

	/**
	 * Creates a ressource as achild of a parent
	 *
	 * @param resource  the resource to add to the parent
	 * @param content   the content of the resource
	 * @param clsParent the class of the parent
	 * @param parentId  the id of the parent
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HttpResponse createResource(StaticArtifact<?, TextData, ?> resource, String content,
			Class<? extends IStorable> clsParent, String parentId) {
		ArtifactStorage storage = getStorage();
		HttpResponse result = null;
		StaticArtifact<?, ?, StaticArtifact<?, TextData, ?>> parent = null;
		try {
			try {
				parent = (StaticArtifact<?, ?, StaticArtifact<?, TextData, ?>>) storage.load(clsParent, parentId);
			} catch (StorageException e) {
				result = getErrorResponse("Parent \"" + parentId + "\" not found!", e, HttpURLConnection.HTTP_NOT_FOUND);
			}

			if (parent != null) {
				resource.setStorage(storage);
				TextData textData = new TextData(content);
				resource.setContent(textData);

				parent.addChild(resource);

				result = new HttpResponse(getIdXML(resource), HttpURLConnection.HTTP_CREATED);
			}
		} catch (StorageException e1) {
			result = getErrorResponse("Resource could not be created!", e1, HttpURLConnection.HTTP_INTERNAL_ERROR);
		}
		return result;
	}

	/**
	 * Retrieves a resource based on its id
	 *
	 * @param clsResource the class of the resource
	 * @param clsChildren the class of the children of the resource
	 * @param resourceId  the id of the resource
	 * @return
	 */
	public HttpResponse getResource(Class<? extends IStorable> clsResource, Class<? extends IStorable> clsChildren,
			String resourceId) {
		ArtifactStorage storage = getStorage();
		HttpResponse result = null;
		XMLResult xmlResult = new XMLResult();
		StaticArtifact<?, ?, ?> resource = null;
		try {
			resource = (StaticArtifact<?, ?, ?>) storage.load(clsResource, resourceId);
		} catch (StorageException e) {
			result = getErrorResponse("Resource \"" + resourceId + "\" not found! ", e, HttpURLConnection.HTTP_NOT_FOUND);
		}

		if (resource != null) {
			try {
				xmlResult.appendElement("resource", resource.getProperties(), resource.readContent().toString());
			} catch (StorageException e) {
				result = getErrorResponse("Resource \"" + resourceId + "\" content not found! ", e, HttpURLConnection.HTTP_NOT_FOUND);
				return result;
			}

			for (int i = 0; i < resource.getChildrenIds().length; i++) {
				try {
					StaticArtifact<?, ?, ?> child = resource.getChild(i);
					xmlResult.appendElement("child", child.getProperties(), "");
				} catch (StorageException e1) {
					// ignore unreadable entries
				}
			}
			result = new HttpResponse(xmlResult.toString(), HttpURLConnection.HTTP_OK);
		}
		return result;
	}

	/**
	 * Retrieves blog object with list of entries
	 *
	 * @param blogName
	 * @return
	 */
	@Path("blogs/{blog}")
	@GET
	public HttpResponse getBlog(@PathParam("blog") String blogName) {
		return getResource(Microblog.class, BlogEntry.class, blogName);
	}

	/**
	 * Retrieves entry with the given id
	 *
	 * @param entryId
	 * @return
	 */
	@Path("entries/{entryId}")
	@GET
	public HttpResponse getBlogEntry(@PathParam("entryId") String entryId) {
		return getResource(BlogEntry.class, BlogComment.class, entryId);
	}

	/**
	 * Retrieves a comment with the given id
	 *
	 * @param commentId
	 * @return
	 */
	@Path("comments/{commentId}")
	@GET
	public HttpResponse getComment(@PathParam("commentId") String commentId) {
		return getResource(BlogComment.class, BlogComment.class, commentId);
	}

	/**
	 * Retrieves a Storable object
	 *
	 * @param id     the id of the object
	 * @param rClass the class of the object
	 * @param cClass the class of its children
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Path("ids/{id}/{rClass}/{cClass}")
	@GET
	public HttpResponse getResource(@PathParam("id") String id, @PathParam("rClass") String rClass,
			@PathParam("cClass") String cClass) {
		Class<? extends IStorable> clsResource = null;
		Class<? extends IStorable> clsChildren = null;
		try {
			clsResource = (Class<? extends IStorable>) Class.forName(rClass);
			clsChildren = (Class<? extends IStorable>) Class.forName(cClass);
		} catch (ClassNotFoundException e) {
			return getErrorResponse("Invalid class name!", e, HttpURLConnection.HTTP_BAD_REQUEST);
		}

		return getResource(clsResource, clsChildren, id);
	}

	/**
	 * @return the name of the currently active user
	 */
	private String getAgentName() {
		Agent agent = this.getContext().getMainAgent();
		return ((UserAgent) (agent)).getLoginName();
	}

	/**
	 * @return the id of the currently active user
	 */
	private String getAgentId() {
		return String.valueOf(this.getContext().getMainAgent().getId());
	}

	private ArtifactStorage getStorage() {
		ArtifactStorage storage = null;

		Agent agent = this.getContext().getMainAgent();
		storage = new DHTArtifactStorage(agent);

		return storage;
	}

	/**
	 * Puts an error message into an XML container
	 *
	 * @param error error message
	 * @return String representation of an XML including the error message
	 */
	private String getErrorXML(String error) {
		XMLResult xmlResult = new XMLResult();
		xmlResult.appendElement("error", error);
		return xmlResult.toString();
	}

	/**
	 * Puts the id of an artifact into an XML container
	 *
	 * @param artifact the artifact with the relevant id
	 * @return String representation of an XML including the artifact id
	 */
	private String getIdXML(StaticArtifact<?, TextData, ?> artifact) {
		XMLResult xmlResult = new XMLResult();
		xmlResult.appendElement("id", artifact.getId());
		return xmlResult.toString();
	}

	/**
	 * Creates a HttpResponse
	 *
	 * @param text error text
	 * @param e    exception information
	 * @param code HTTP error code
	 * @return response to send to the WebConnector
	 */
	private HttpResponse getErrorResponse(String text, Throwable e, int code) {
		return new HttpResponse(getErrorXML(text + " " + e.getMessage()), code);
	}

}
