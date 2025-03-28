package net.whydah.identity.spring;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class XMLHelper {
    private DocumentBuilderFactory dbf;
    private DocumentBuilder documentBuilder;
    private Document doc;
    private XPath xPath;

    public XMLHelper(String logonResult) throws Exception {
        dbf = DocumentBuilderFactory.newInstance();
        documentBuilder = dbf.newDocumentBuilder();
        doc = documentBuilder.parse(new InputSource(new StringReader(logonResult)));
        xPath = XPathFactory.newInstance().newXPath();
    }

    public String findString(String path) throws XPathExpressionException {
        return (String) xPath.evaluate(path, doc, XPathConstants.STRING);
    }

    public Long findLong(String path) throws XPathExpressionException, NumberFormatException {
        String xmlValue = findString(path);
        Long number = null;
        if (xmlValue != null && !xmlValue.isEmpty()) {
            number = Long.valueOf(xmlValue);
        }
        return number;
    }

    public NodeList findNodes(String path) throws XPathExpressionException {
        return (NodeList) xPath.compile(path).evaluate(doc, XPathConstants.NODESET);
    }

    public String findString(String path, Node parentNode) throws XPathExpressionException {
        return (String) xPath.evaluate(path, parentNode, XPathConstants.STRING);
    }

    public NodeList findNodes(String path, Node parentNode) throws XPathExpressionException {
        return (NodeList) xPath.compile(path).evaluate(parentNode, XPathConstants.NODESET);
    }

    public String printNode(Node node) {
        String nodeTxt = "Node:";
        NamedNodeMap nl = node.getAttributes();
        int length = nl.getLength();
        for (int i = 0; i < length; i++) {
            Attr attr = (Attr) nl.item(i);
            String name = attr.getName();
            String value = attr.getValue();
            nodeTxt += "\n  Name: " + name + ", Value: " + value;
        }
        return nodeTxt;

    }

    public NodeList findByName(String name) {
        return doc.getElementsByTagName(name);
    }

    public static String findNodeValue(Element firstElement, String name) {
        String nodeValue = null;
        NodeList firstNameList = firstElement.getElementsByTagName(name);
        Element firstNameElement = (Element) firstNameList.item(0);

        NodeList textFNList = firstNameElement.getChildNodes();

        if (textFNList.getLength() > 0) {
            nodeValue = (textFNList.item(0)).getNodeValue();
        }
        return nodeValue;
    }
}
