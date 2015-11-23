package i5.las2peer.services.microblogService.storage;

import i5.las2peer.persistency.DecodingFailedException;
import i5.las2peer.persistency.EncodingFailedException;
import i5.las2peer.persistency.Envelope;
import i5.las2peer.security.Agent;
import i5.las2peer.security.L2pSecurityException;
import i5.las2peer.services.microblogService.data.StaticArtifact;
import i5.las2peer.services.microblogService.exceptions.StorageException;
import i5.las2peer.services.microblogService.interfaces.IStorable;
import i5.las2peer.tools.SerializationException;

public class DHTArtifactStorage extends ArtifactStorage
{

	private Agent agent;
	private static final String ENVELOPE_PREFIX = "DHTSTORAGE";

	@Override
	public void save(IStorable artefact) throws StorageException
	{
		// System.out.println("+++"+artefact.getClass().getName());
		setEnvelopeData(artefact);
	}

	@Override
	public IStorable load(Class<? extends IStorable> cls, String id) throws StorageException
	{

		IStorable result = getEnvelopeData(cls, id);
		if (result instanceof StaticArtifact)
			((StaticArtifact<?, ?, ?>) result).setStorage(this);
		return result;
	}

	@Override
	public void delete(Class<? extends IStorable> cls, String id) throws StorageException
	{
		IStorable artifact = getEnvelopeData(cls, id);

		Envelope env = null;
		try
		{
			env = fetchEnvelope(artifact.getClass(), artifact.getId());
		} catch (StorageException e)
		{
			// do nothing if not found etc
		}

		try
		{
			env.open();
			env.setOverWriteBlindly(true);
			env.updateContent("");
			env.store();
			env.close();

		} catch (Exception e)
		{
			// do nothing
		}
		// TODO
		// artefact.deleteContent();
	}

	public DHTArtifactStorage(Agent agent)
	{
		this.agent = agent;

	}

	private Envelope createNewEnvelope(IStorable artifact) throws StorageException
	{
		try
		{

			return Envelope.createClassIdEnvelope(artifact, getEnvelopeName(artifact.getId()), agent);

		} catch (Exception e)
		{
			throw new StorageException(e);
		}

		// return null;
	}

	private void setEnvelopeData(IStorable artifact) throws StorageException
	{
		Envelope env;
		try
		{
			env = fetchEnvelope(artifact.getClass(), artifact.getId());
		} catch (StorageException e)
		{

			env = createNewEnvelope(artifact);
			try
			{
				env.open();
				env.setOverWriteBlindly(true);
				env.updateContent(artifact);
				env.store();
			} catch (i5.las2peer.p2p.StorageException e1)
			{
				e1.printStackTrace();
			} catch (L2pSecurityException e1)
			{
				e1.printStackTrace();
			} catch (DecodingFailedException e1)
			{
				e1.printStackTrace();
			} catch (SerializationException e1)
			{
				e1.printStackTrace();
			}
			return;
		}

		try
		{
			env.open();
			env.setOverWriteBlindly(true);
			env.updateContent(artifact);
			env.store();
			env.close();

		} catch (DecodingFailedException e)
		{
			throw new StorageException("Envelope could not be decoded", e);
		} catch (L2pSecurityException e)
		{
			throw new StorageException("Security Exception", e);
		} catch (SerializationException e)
		{
			throw new StorageException("Data could not be serialized", e);
		} catch (i5.las2peer.p2p.StorageException e)
		{
			throw new StorageException("Envelope could not be stored", e);
		} catch (EncodingFailedException e)
		{
			throw new StorageException("Encoding Execption", e);
		}

	}

	private IStorable getEnvelopeData(Class<?> cls, String id) throws StorageException
	{
		Envelope env = fetchEnvelope(cls, id);
		IStorable data = null;
		if (env == null)
			throw new StorageException("Envelope could not be found, nor created!");
		try
		{

			env.open();
			data = env.getContent(IStorable.class);
			env.close();

		} catch (DecodingFailedException e)
		{
			throw new StorageException("Envelope could not be decoded!", e);
		} catch (L2pSecurityException e)
		{
			throw new StorageException("Security Exception", e);
		} catch (Exception e)
		{
			throw new StorageException(e);
		}

		return data;
	}

	private Envelope fetchEnvelope(Class<?> cls, String id) throws StorageException
	{
		Envelope env;
		try
		{
			env = Envelope.fetchClassIdEnvelope(cls, getEnvelopeName(id));
		} catch (Exception e)
		{
			throw new StorageException(e);
		}

		return env;
	}

	private String getEnvelopeName(String id)
	{
		return ENVELOPE_PREFIX + id;
	}
}
