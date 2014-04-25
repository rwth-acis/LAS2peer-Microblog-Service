package i5.las2peer.services.microblogService;

import org.junit.Test;
import rice.p2p.util.Base64;
import sun.misc.BASE64Encoder;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * @author Alexander
 */
public class BlogTest
{
    @Test
    public void generateId()
    {
        String owner="Karl";
        Random rand = new Random();
        int max=1000000000;
        int min=0;
        //int randomNum = rand.nextInt((max - min) + 1) + min;

            SecureRandom prng = new SecureRandom();
            Long randomNum = new Long(prng.nextLong());
            byte[] bytes = ByteBuffer.allocate(8).putLong(randomNum).array();
            System.out.println(Base64.encodeBytes(bytes));
            System.out.println(Base64.encodeBytes(UUID.randomUUID().toString().getBytes()));






    }
}
