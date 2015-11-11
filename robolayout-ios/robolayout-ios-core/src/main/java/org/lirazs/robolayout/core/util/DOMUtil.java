package org.lirazs.robolayout.core.util;

import org.lirazs.robolayout.core.collection.NamedNodeMapIterable;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 7/30/2015.
 */
public class DOMUtil {

    public static Map<String, String> getAttributesFromNode(Node node) {
        return getAttributesFromNode(node, null);
    }
    public static Map<String, String> getAttributesFromNode(Node node,  Map<String, String> reuseMap) {
        Element element = null;
        if(node.getNodeType() == Node.ELEMENT_NODE) {
            element = (Element) node;
        }
        return element != null ? getAttributesFromElement(element, reuseMap) : new HashMap<String, String>();
    }

    public static Map<String, String> getAttributesFromElement(Element element) {
        return getAttributesFromElement(element, null);
    }

    public static Map<String, String> getAttributesFromElement(Element element, Map<String, String> reuseMap) {
        if(reuseMap == null) {
            reuseMap = new HashMap<>(20);
        } else {
            reuseMap.clear();
        }

        NamedNodeMap attributes = element.getAttributes();
        for (Node node : NamedNodeMapIterable.of(attributes)) {
            reuseMap.put(node.getNodeName(), node.getNodeValue());
        }
        return reuseMap;
    }

    public static Element getFirstElementChild(Element element) {
        Node child = element.getFirstChild();

        while(child != null) {
            if(child.getNodeType() == Node.ELEMENT_NODE)
                return (Element) child;

            child = child.getNextSibling();
        }
        return null;
    }
}
