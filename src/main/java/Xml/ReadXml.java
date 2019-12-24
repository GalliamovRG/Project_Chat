package Xml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ReadXml {

    private static HashMap listProperty;

    private void InitXml(String p_sFileName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory rFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder rBuilder = rFactory.newDocumentBuilder();
        Document rDoc = rBuilder.parse(new File(p_sFileName));
        NodeList listElement = rDoc.getDocumentElement().getElementsByTagName("property");

        for (int iIdx = 0; iIdx < listElement.getLength(); iIdx++){
            Node rProp = listElement.item(iIdx);

            NamedNodeMap rAttr = rProp.getAttributes();
            listProperty.put(rAttr.getNamedItem("name").getNodeValue(), rAttr.getNamedItem("value").getNodeValue());
        }
    }

    public ReadXml(String p_sFileName) throws IOException, SAXException, ParserConfigurationException {
        listProperty = new HashMap<>();

        InitXml(p_sFileName);
    }

    public static String GetParam(String p_sParam){
        if (listProperty.containsKey(p_sParam)){
            return (String) listProperty.get(p_sParam);
        }
        return null;
    }

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        ReadXml rXml = new ReadXml("config.xml");

        System.out.println(GetParam("db_url"));
    }
}
