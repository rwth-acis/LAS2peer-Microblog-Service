package i5.las2peer.services.microblogService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.InputSource;

import i5.las2peer.p2p.LocalNode;
import i5.las2peer.security.ServiceAgent;
import i5.las2peer.security.UserAgent;
import i5.las2peer.testing.MockAgentFactory;
import i5.las2peer.webConnector.WebConnector;
import i5.las2peer.webConnector.client.ClientResponse;
import i5.las2peer.webConnector.client.MiniClient;


public class BlogTest
{

    private static final String HTTP_ADDRESS = "http://127.0.0.1";
    private static final int HTTP_PORT = WebConnector.DEFAULT_HTTP_PORT;
    public static final String TESTCONTENT1 = "All your blog belong to us!";
    public static final String TESTCONTENT2 = "I love Firefly!";
    public static final String TESTCONTENT3 = "Troll!";
    public static final String TESTCONTENT4 = "Why?";
    public static final String TESTCONTENT5 = "Me too!";

    private static LocalNode node;
    private static WebConnector connector;
    private static ByteArrayOutputStream logStream;

    private static UserAgent testAgent;
    private static final String testPass = "adamspass";

    private static final String testServiceClass = "i5.las2peer.services.microblogService.MicroblogService";

    @BeforeClass
    public static void startServer () throws Exception {
        // start Node
        node = LocalNode.newNode();
        node.storeAgent(MockAgentFactory.getEve());
        node.storeAgent(MockAgentFactory.getAdam());
        node.storeAgent(MockAgentFactory.getAbel());
        node.storeAgent( MockAgentFactory.getGroup1());
        node.launch();

        ServiceAgent testService = ServiceAgent.createServiceAgent(testServiceClass, "a pass");
        testService.unlockPrivateKey("a pass");
        node.registerReceiver(testService);

        // start connector

        logStream = new ByteArrayOutputStream();



        connector = new WebConnector(true,HTTP_PORT,false,1000);
        connector.setLogStream(new PrintStream( logStream));
        connector.start ( node );
        Thread.sleep(5000);
        // eve is the anonymous agent!
        testAgent = MockAgentFactory.getAdam();
        //avoid timing errors: wait for the repository manager to get all services, before invoking them

    }

    @AfterClass
    public static void shutDownServer () throws Exception {
        //connector.interrupt();

        connector.stop();
        node.shutDown();

        connector = null;
        node = null;

        LocalNode.reset();

        System.out.println("Connector-Log:");
        System.out.println("--------------");

        System.out.println(logStream.toString());
        //System.out.println(connector.sslKeystore);


    }

    @Test
    public void testGenerateBlogs()
    {
        XPath xpath = XPathFactory.newInstance().newXPath();
        connector.updateServiceList();
        MiniClient c = new MiniClient();
        c.setAddressPort(HTTP_ADDRESS, HTTP_PORT);
        try
        {
            c.setLogin(Long.toString(testAgent.getId()), testPass);
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
            fail("Exception: "+ e);
        }


        String id1, id2, id3,id4,id5=null;
        try
        {


            //first create some entries
            ClientResponse result=c.sendRequest("put", "microblog/blogs/myBlog", "");
            assertTrue(result.getHttpCode() < HttpURLConnection.HTTP_BAD_REQUEST);


            result=c.sendRequest("put", "microblog/blogs/yourBlog", "");
            assertTrue(result.getHttpCode() < HttpURLConnection.HTTP_BAD_REQUEST);

            result=c.sendRequest("post", "microblog/blogs/myBlog", TESTCONTENT1);
            assertTrue(result.getHttpCode() < HttpURLConnection.HTTP_BAD_REQUEST);
            id1= getXPathEvaluate(xpath, result, "//id/text()");


            result=c.sendRequest("post", "microblog/blogs/myBlog", TESTCONTENT2);
            assertTrue(result.getHttpCode() < HttpURLConnection.HTTP_BAD_REQUEST);
            id2= getXPathEvaluate(xpath, result, "//id/text()");

            result=c.sendRequest("post", "microblog/comments/"+id1, TESTCONTENT3);
            assertTrue(result.getHttpCode() < HttpURLConnection.HTTP_BAD_REQUEST);

            id3= getXPathEvaluate(xpath, result, "//id/text()");

            result=c.sendRequest("post", "microblog/comments/"+id1, TESTCONTENT4);
            assertTrue(result.getHttpCode() < HttpURLConnection.HTTP_BAD_REQUEST);
            id4= getXPathEvaluate(xpath, result, "//id/text()");

            result=c.sendRequest("post", "microblog/comments/"+id2, TESTCONTENT5);
            assertTrue(result.getHttpCode() < HttpURLConnection.HTTP_BAD_REQUEST);
            id5= getXPathEvaluate(xpath, result, "//id/text()");


            //then try to read created entries
            String blog1, blog2, entry1, entry2, comment1, comment2, comment3=null;

            result=c.sendRequest("get", "microblog/blogs", "");

            assertTrue(result.getHttpCode() < HttpURLConnection.HTTP_BAD_REQUEST);

            blog1=getXPathEvaluate(xpath, result, "/root/child[1]/@id");
            blog2=getXPathEvaluate(xpath, result, "/root/child[2]/@id");
            assertEquals("myBlog",blog1);
            assertEquals("yourBlog",blog2);

            result=c.sendRequest("get", "microblog/blogs/myBlog", "");
            assertTrue(result.getHttpCode() < HttpURLConnection.HTTP_BAD_REQUEST);
            entry1= getXPathEvaluate(xpath, result, "/root/child[1]/@id");
            assertEquals(id1,entry1);
            result=c.sendRequest("get", "microblog/entries/"+entry1, "");
            assertTrue(result.getHttpCode() < HttpURLConnection.HTTP_BAD_REQUEST);
            entry1= getXPathEvaluate(xpath, result, "/root/resource/text()");


            result=c.sendRequest("get", "microblog/blogs/myBlog", "");
            assertTrue(result.getHttpCode() < HttpURLConnection.HTTP_BAD_REQUEST);
            entry2= getXPathEvaluate(xpath, result, "/root/child[2]/@id");
            assertEquals(id2,entry2);
            result=c.sendRequest("get", "microblog/entries/"+entry2, "");
            assertTrue(result.getHttpCode() < HttpURLConnection.HTTP_BAD_REQUEST);
            entry2= getXPathEvaluate(xpath, result, "/root/resource/text()");


            assertEquals(TESTCONTENT1,entry1);
            assertEquals(TESTCONTENT2,entry2);


            result=c.sendRequest("get", "microblog/comments/"+id3, "");
            assertTrue(result.getHttpCode() < HttpURLConnection.HTTP_BAD_REQUEST);
            comment1= getXPathEvaluate(xpath, result, "/root/resource/text()");
            assertEquals(TESTCONTENT3,comment1);

            result=c.sendRequest("get", "microblog/comments/"+id4, "");
            assertTrue(result.getHttpCode() < HttpURLConnection.HTTP_BAD_REQUEST);
            comment2= getXPathEvaluate(xpath, result, "/root/resource/text()");
            assertEquals(TESTCONTENT4,comment2);

            result=c.sendRequest("get", "microblog/comments/"+id5, "");
            assertTrue(result.getHttpCode() < HttpURLConnection.HTTP_BAD_REQUEST);
            comment3= getXPathEvaluate(xpath, result, "/root/resource/text()");
            assertEquals(TESTCONTENT5,comment3);

        }
        catch(Exception e)
        {
            e.printStackTrace();
            fail ( "Exception: " + e );
        }


    }

    private String getXPathEvaluate(XPath xpath, ClientResponse result, String expression) throws XPathExpressionException
    {
        return xpath.evaluate(expression, new InputSource(new StringReader(result.getResponse())));
    }
}
