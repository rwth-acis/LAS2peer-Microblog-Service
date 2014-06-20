package i5.las2peer.services.microblogService;

import i5.las2peer.services.microblogService.data.ShortId;
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
        long id= 458714582415784567l;

        String result=ShortId.getIdString(id);
        System.out.println(result);

        id= 12564186761984l;
        result=ShortId.getIdString(id);
        System.out.println(result);
        id= 999999999999l;
        result=ShortId.getIdString(id);
        System.out.println(result);

        for(int i=0;i<10;i++)
        {
            SecureRandom prng = new SecureRandom();
            Long randomNum = prng.nextLong();

            System.out.println(ShortId.getIdString(randomNum));
        }

    }
}
