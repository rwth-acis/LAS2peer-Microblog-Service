package i5.las2peer.services.microblogService.data;

import i5.las2peer.services.microblogService.exceptions.StorageException;
import i5.las2peer.services.microblogService.interfaces.IArtifact;
import i5.las2peer.services.microblogService.interfaces.IArtifactData;
import i5.las2peer.services.microblogService.storage.ArtifactStorage;
import i5.las2peer.services.microblogService.storage.IdGenerator;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Basic class for storing artifacts, can ba also used for mediums
 * @param <CONT> type of the content an Artifact sotres
 * @param <CONTBOX> type of the generic container, which contains the content data and type (Java generics are poorly designed)
 * @param <CHILD> type of the children of the artifact
 */
public abstract class StaticArtifact<CONT extends Serializable, CONTBOX extends IArtifactData<CONT>, CHILD extends StaticArtifact<?, ?, ?>>
		implements IArtifact
{
	private static final long serialVersionUID = 538390869975844432L;
	private final Class<CONTBOX> contentBoxClass;
	private final Class<CHILD> childClass;
	private String ownerId;
	private String owner;
	private String id;
	private String contentId;
	private transient ArtifactStorage storage = null;
	private long creationTime;
	private boolean onceSaved;
	private ArrayList<String> children = new ArrayList<String>();

	/**
	 * Constructor for first initialization
	 * @param contentBoxClass type of the container class managing the content of the artifact
	 * @param childClass type of the children
	 * @param ownerId id of the owner in the system
	 * @param owner name of the owner for display
	 */
	public StaticArtifact(Class<CONTBOX> contentBoxClass, Class<CHILD> childClass, String ownerId, String owner)
	{

		this.contentBoxClass = contentBoxClass; // because java generics are a very bad joke
		this.childClass = childClass;
		this.ownerId = ownerId;
		this.owner = owner;
		this.id = IdGenerator.generateId();

		creationTime = System.currentTimeMillis();

	}

	/**
	 * Constructor just incokes a constructor with more parameters, here the owner is set as the ownerId (if no owner name given)
	 * @param contentBoxClass type of the container class managing the content of the artifact
	 * @param childClass type of the children
	 * @param ownerId id of the owner in the system
	 */
	public StaticArtifact(Class<CONTBOX> contentBoxClass, Class<CHILD> childClass, String ownerId)
	{
		this(contentBoxClass, childClass, ownerId, ownerId);

	}

	/**
	 *
	 * @param time timestamp
	 * @return time and date as readable string
	 */
	public static String getDate(long time)
	{
		Date date = new Date(time);
		DateFormat df = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
		return df.format(date);
	}

	/**
	 * Useful when new children are added: if child was not already saved in the system it can be saved when added to the artifact
	 * @return if the artifact was already saved
	 */
	public boolean wasOnceSaved()
	{
		return onceSaved;
	}

	/**
	 * Saves the artifact (this)
	 * @param storage the storage manager to use (i.e. Las2peer, database etc...)
	 * @throws StorageException
	 */
	public void save(ArtifactStorage storage) throws StorageException
	{
		this.storage = storage;
		save();
	}

	/**
	 * Saves the artifact (this)
	 * @throws StorageException
	 */
	public void save() throws StorageException
	{
		if (storage != null)
		{
			storage.save(this);
			onceSaved = true;
		}
		else
			throw new StorageException("Class has no ArtifactStorage assigned!");
	}

	/**
	 *
	 * @return the id of the artifact creator
	 */
	@Override
	public String getOwnerId()
	{
		return ownerId;
	}

	/**
	 *
	 * @return the automatically generated random id of the artifact
	 */
	@Override
	public String getId()
	{
		return id;
	}

	/**
	 *
	 * @return the id of the content container
	 */
	@Override
	public String getContentId()
	{
		return contentId;
	}

	/**
	 *
	 * @return timestamp, when the object was created
	 */
	@Override
	public long getCreationTime()
	{
		return creationTime;
	}

	/**
	 *
	 * @return the name of the owner, if available, otherwise the id of the owner
	 */
	public String getOwner()
	{
		return owner;
	}

	/**
	 *
	 * @return array of ids belonging to the children of the artifact
	 */
	public String[] getChildrenIds()
	{
		String[] result = new String[children.size()];
		result = children.toArray(result);
		return result;
	}

	/**
	 * Tries to load a child object with the given id
	 * @param id id of a child
	 * @return child object
	 * @throws StorageException
	 */
	@SuppressWarnings("unchecked")
	public CHILD getChild(String id) throws StorageException
	{
		if (id == null)
			return null;
		if (storage == null)
			return null;

		CHILD result = null;
		result = (CHILD) storage.load(childClass, id);
		return result;
	}

	/**
	 * Removes a list of children from the local children list and saves the artifact
	 * @param ids ids of the children to remove
	 * @throws StorageException
	 */
	public void removeChildren(String... ids) throws StorageException
	{
		for (String id : ids)
		{
			children.remove(id);
		}
		save();
	}

	/**
	 * Removes a child with the given id and saves the artifact
	 * @param id id of the child to remove
	 * @throws StorageException
	 */
	public void removeChild(String id) throws StorageException
	{
		removeChildren(id);

	}

	/**
	 * Deletes a list of children and removes the id references from the local children list
	 * @param ids ids of the children to delete
	 * @throws StorageException
	 */
	public void deleteChildren(String... ids) throws StorageException
	{
		removeChildren(ids);
		for (String id : ids)
		{
			try
			{
				CHILD child = getChild(id);
				child.delete();
			} catch (StorageException e)
			{
				// ignore not found elements
			}
		}
		save();
	}

	/**
	 * Deletes a child an removes the id reference from the local children list
	 * @param id child to delete
	 * @throws StorageException
	 */
	public void deleteChild(String id) throws StorageException
	{
		deleteChildren(id);
	}

	/**
	 * Loads a child based on its index in the local children list
	 * @param index index of the child to load
	 * @return object of the child
	 * @throws StorageException
	 */
	public CHILD getChild(int index) throws StorageException
	{
		return getChild(getChildId(index));
	}

	/**
	 * Adds a child to the local children list and saves the artifact. If not already saved the child is saved too
	 * @param child child object to add
	 * @throws StorageException
	 */
	@SuppressWarnings("unchecked")
	public void addChild(CHILD child) throws StorageException
	{
		addChildren(child);
	}

	/**
	 * Adds multiple children to the local children list and saves the artifact. If not already saved, each child is saved too
	 * @param children children to add
	 * @throws StorageException
	 */
	public void addChildren(CHILD... children) throws StorageException
	{
		for (CHILD child : children)
		{
			this.children.add(child.getId());
			if (!child.wasOnceSaved()) { // only save implicitly, when feshly created
				storage.save(child);
			}
		}
		this.save();
	}

	/**
	 * Gets the id of a child in the children list at the given index
	 * @param index
	 * @return
	 */
	public String getChildId(int index)
	{
		if (index < children.size())
			return children.get(index);
		return null;
	}

	/**
	 * Each inherited class should implement a method which collects metadata that can be used later for output
	 * @return array of key value pairs
	 */
	public abstract StringPair[] getProperties();

	/**
	 * Loads the content container of the artifact (not the content itself)
	 * @return container with the content
	 * @throws StorageException
	 */
	@SuppressWarnings("unchecked")
	public CONTBOX getContent() throws StorageException
	{
		if (contentId == null)
			return null;
		return (CONTBOX) storage.load(contentBoxClass, contentId);
	}

	/**
	 * Sets a new content container reference for the artifact
	 * @param content
	 * @throws StorageException
	 */
	public void setContent(CONTBOX content) throws StorageException
	{
		this.contentId = content.getId();
		storage.save(content);
		this.save();
	}

	/**
	 * Removes the current content container reference (does not delete it).
	 */
	public void removeContent()
	{
		this.contentId = null;
	}

	/**
	 * Updates the content of the current content container
	 * @param content new content
	 * @throws StorageException
	 */
	public void updateContent(CONT content) throws StorageException
	{
		if (contentId == null)
			throw new StorageException(this.getId() + " has no content to update!");

		CONTBOX contentBox = getContent();
		contentBox.setContent(content);
		contentBox.save();

	}

	/**
	 * Gets the actual content of the content container
	 * @return
	 * @throws StorageException
	 */
	public CONT readContent() throws StorageException
	{
		return getContent().getContent();
	}

	/**
	 * Gets the current storage manager object
	 * @return the storage manager object currently used by the artifact
	 */
	public ArtifactStorage getStorage()
	{
		return storage;
	}

	/**
	 * Set the storage manager object to use in this artifact. This is necessary for many methods, such as save or adding children.
	 * The object is automatically set, if the artifact is loaded (i.e. the loading storage manager becomes the artifacts storage manager).
	 * @param storage the storage manager object
	 */
	public void setStorage(ArtifactStorage storage)
	{
		this.storage = storage;
	}

	/**
	 * Deletes the object, including all children and the content.
	 * If you don't want to delete children/content, then remove their references first.
	 * @throws StorageException
	 */
	public void delete() throws StorageException
	{
		deleteChildren(getChildrenIds());
		if (contentId != null)
		{
			CONTBOX content = getContent();
			content.delete();
		}
		save();
		storage.delete(this.getClass(), getId());

	}

	/**
	 * Returns the creation timestamp in a human readable form.
	 * @return
	 */
	public String getCreationDate()
	{
		return getDate(getCreationTime());
	}
}
