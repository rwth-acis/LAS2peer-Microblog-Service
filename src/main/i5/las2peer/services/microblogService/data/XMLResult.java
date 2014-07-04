package i5.las2peer.services.microblogService.data;

import i5.las2peer.services.microblogService.data.StringPair;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 * Class to create simple XML response objects. Depth is limited to 1.
 */
public class XMLResult
{
    public static final String ROOT = "root";
    private DocumentBuilderFactory docFactory;
    private DocumentBuilder docBuilder;
    private Document doc;
    private Element root;
    public  XMLResult()
    {
        docFactory = DocumentBuilderFactory.newInstance();
        try
        {
            docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
            root=doc.createElement(ROOT);
            doc.appendChild(root);
        }
        catch(ParserConfigurationException e)//should never happen
        {
            e.printStackTrace();//TODO
        }
    }

    /**
     * Append an new element
     * @param tag the XML-tag
     * @param attributes optional attributes (key, value pairs)
     * @param content optional content
     */
    public void appendElement(String tag, StringPair[] attributes, String content)
    {
        Element elem = doc.createElement(tag);
        root.appendChild(elem);
        for(StringPair sp : attributes)
        {
            Attr attr = doc.createAttribute(sp.getKey());
            attr.setValue(sp.getValue());
            elem.setAttributeNode(attr);
        }
        elem.setTextContent(content);

    }

    /**
     * Appends an element without attributes
     * @param tag the XML-tag
     * @param content optional content
     */
    public void appendElement(String tag, String content)
    {
        appendElement(tag, new StringPair[] {}, content);
    }

    /**
     * Formats the XML for output
     * @return XML string representation
     */
    public String toString()
    {
        if(doc!=null)
        {
            try
            {
                Transformer t = TransformerFactory.newInstance().newTransformer();
                StreamResult out = new StreamResult(new StringWriter());
                t.setOutputProperty(OutputKeys.INDENT, "yes"); //pretty printing
                t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                t.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
                t.transform(new DOMSource(doc),out);
                return out.getWriter().toString();
            }
            catch(Exception e)
            {
                return "";
            }
        }
        else
            return "";
    }
}
