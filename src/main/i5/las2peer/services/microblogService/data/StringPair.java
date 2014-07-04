package i5.las2peer.services.microblogService.data;


/**
 * Simple class to store two strings
 */
public class StringPair
{
    private String key;
    private String value;
    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public StringPair(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

}
