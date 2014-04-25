package i5.las2peer.services.microblogService;


import i5.las2peer.p2p.StorageException;
import i5.las2peer.persistency.DecodingFailedException;
import i5.las2peer.persistency.EncodingFailedException;
import i5.las2peer.persistency.Envelope;

import i5.las2peer.security.L2pSecurityException;

import i5.las2peer.security.ServiceAgent;
import i5.las2peer.services.microblogService.exceptions.ArtefactStorageException;
import i5.las2peer.security.Agent;

import i5.las2peer.tools.SerializationException;



/**
 * @author Alexander
 */
public class DHTArtefactStorage extends ArtefactStorage
{
    private ServiceAgent service;
    private Agent agent;
    private static final String ENVELOPE_PREFIX="DHTSTORAGE";
    @Override
    public void save(StaticArtefact artefact) throws ArtefactStorageException
    {
        setEnvelopeData(artefact);
    }

    @Override
    public StaticArtefact load(Class<?> cls,String id) throws ArtefactStorageException
    {
        return getEnvelopeData(cls,id);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void delete(Class<?> cls, String id) throws ArtefactStorageException
    {
        StaticArtefact artefact=getEnvelopeData(cls,id);
        artefact.deleteContent();
    }

    public DHTArtefactStorage(Agent agent)
    {
        this.agent=agent;

    }



    private Envelope createNewEnvelope(StaticArtefact artefact) throws ArtefactStorageException
    {
        try
        {

            return Envelope.createClassIdEnvelope(artefact, getEnvelopeName(artefact.getId()), agent);

        }
        catch(Exception e)
        {
            throw new ArtefactStorageException(e);
        }

        //return null;
    }


    private void setEnvelopeData(StaticArtefact artefact) throws ArtefactStorageException
    {
        Envelope env;
        try
        {
            env=fetchEnvelope(artefact.getClass(),artefact.getId());
        }
        catch(ArtefactStorageException e)
        {

            env=createNewEnvelope(artefact);
            try
            {
                env.open();
                env.setOverWriteBlindly(true);
                env.updateContent(artefact);
                env.store();
            }
            catch(StorageException e1)
            {
                e1.printStackTrace();
            }
            catch(L2pSecurityException e1)
            {
                e1.printStackTrace();
            }
            catch(DecodingFailedException e1)
            {
                e1.printStackTrace();
            }
            catch(SerializationException e1)
            {
                e1.printStackTrace();
            }
            return;
        }


        try
        {
            env.open();
            env.setOverWriteBlindly(true);
            env.updateContent(artefact);
            env.store();
            env.close();

        }
        catch(DecodingFailedException e)
        {
            throw new ArtefactStorageException("Envelope could not be decoded",e);
        }
        catch(L2pSecurityException e)
        {
            throw new ArtefactStorageException("Security Exception",e);
        }
        catch(SerializationException e)
        {
            throw new ArtefactStorageException("Data could not be serialized",e);
        }
        catch(StorageException e)
        {
            throw new ArtefactStorageException("Envelope could not be stored",e);
        }
        catch(EncodingFailedException e)
        {
            throw new ArtefactStorageException("Encoding Execption",e);
        }


    }

    private StaticArtefact getEnvelopeData(Class<?> cls,String id) throws ArtefactStorageException
    {
        Envelope env=fetchEnvelope(cls,id);
        StaticArtefact data=null;
        if(env==null)
            throw new ArtefactStorageException("Envelope could not be found, nor created!");
        try
        {

            env.open();
            data = env.getContent(StaticArtefact.class);
            env.close();

        }
        catch(DecodingFailedException e)
        {
            throw new ArtefactStorageException("Envelope could not be decoded!",e);
        }
        catch(L2pSecurityException e)
        {
            throw new ArtefactStorageException("Security Exception",e);
        }
        catch(Exception e)
        {
            throw new ArtefactStorageException(e);
        }


        return data;
    }


    private Envelope fetchEnvelope(Class<?> cls, String id) throws ArtefactStorageException
    {
        Envelope env;
        try
        {
            env=Envelope.fetchClassIdEnvelope(cls, getEnvelopeName(id));
        }
        catch(Exception e)
        {
            throw new ArtefactStorageException(e);
        }


        return env;
    }

    private String getEnvelopeName(String id)
    {
        return ENVELOPE_PREFIX+id;
    }
}
