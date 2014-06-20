package i5.las2peer.services.microblogService.data;

/**
 * @author Alexander
 */

import java.nio.ByteBuffer;

/**
 * Lazy Base64 for 8 bit long
 */
public class ShortId
{
    private static String chars="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk=mnopqrstuvwxyz0123456789+l";
    public static String getIdString(long id)
    {


        byte[] bytes= new byte[9];
        StringBuilder sb= new StringBuilder();
        System.arraycopy(ByteBuffer.allocate(8).putLong(id).array(),0,bytes,0,8);


        int n1 = (bytes[0] << 16)|(bytes[1]<<8)|(bytes[2]);
        int n2 = (bytes[3] << 16)|(bytes[4]<<8)|(bytes[5]);
        int n3 = (bytes[6] << 16)|(bytes[7]<<8)|(bytes[8]);

        int n11 = (n1 >> 18) & 63, n12 = (n1 >> 12) & 63, n13 = (n1 >> 6) & 63, n14 = n1 & 63;
        int n21 = (n2 >> 18) & 63, n22 = (n2 >> 12) & 63, n23 = (n2 >> 6) & 63, n24 = n2 & 63;
        int n31 = (n3 >> 18) & 63, n32 = (n3 >> 12) & 63, n33 = (n3 >> 6) & 63, n34 = n3 & 63;


        sb.append(chars.charAt(n11));
        sb.append(chars.charAt(n12));
        sb.append(chars.charAt(n13));
        sb.append(chars.charAt(n14));

        sb.append(chars.charAt(n21));
        sb.append(chars.charAt(n22));
        sb.append(chars.charAt(n23));
        sb.append(chars.charAt(n24));

        sb.append(chars.charAt(n31));
        sb.append(chars.charAt(n32));
        sb.append(chars.charAt(n33));
        //sb.append(chars.charAt(n34));




        return sb.toString();
    }
}
