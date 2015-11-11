package org.lirazs.robolayout.core.resource.values;

import org.lirazs.robolayout.core.resource.ResourceManager;
import org.lirazs.robolayout.core.util.DOMUtil;
import org.lirazs.robolayout.core.util.XMLUtil;
import org.robovm.apple.foundation.NSCharacterSet;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSURL;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 8/3/2015.
 */
public class ResourceValueSet {

    public static ResourceValueSet inflate(Document document) {
        ResourceValueSet result = null;
        Element root = document.getDocumentElement();

        if(root.getTagName().equals("resources")) {
            result = new ResourceValueSet();
            String whitespaceCharacterSetRegEx = NSCharacterSet.getWhitespaceAndNewlineCharacterSet().toString();

            Map<String, Object> values = new HashMap<>();

            Node child = root.getFirstChild();
            while(child != null) {
                if(child.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) child;

                    String tagName = element.getTagName();
                    String resourceName = element.getAttribute("name");

                    if(!resourceName.isEmpty()) {
                        if(tagName.equals("style")) {
                            Style style = Style.create(element);
                            values.put(resourceName, style);

                        } else if(tagName.equals("string")) {
                            String string = element.getTextContent().replaceAll(whitespaceCharacterSetRegEx, "");
                            values.put(resourceName, string);

                        } else if(tagName.equals("string-array")) {
                            List<String> stringList = StringArray.parseStringArray(element);
                            values.put(resourceName, stringList);
                        }
                    }
                }
                child = child.getNextSibling();
            }
            result.setValues(values);
        }
        return result;
    }

    public static ResourceValueSet create(NSURL url) {
        return create(NSData.read(url));
    }

    public static ResourceValueSet create(NSData data) {
        if(data == null) return null;

        Document xml = XMLUtil.getXML(data);
        return inflate(xml);
    }

    private Map<String, Object> values;

    public Style getStyle(String name) {
        Style style = null;
        Object value = values.get(name);

        if(value instanceof Style) {
            style = (Style) value;
        }
        return style;
    }

    public String getString(String name) {
        String result = null;
        Object value = values.get(name);

        if(value instanceof String) {
            result = (String) value;
            ResourceManager resourceManager = ResourceManager.getCurrent();
            if(resourceManager.isValidIdentifier(result)) {
                result = resourceManager.getString(result);
            }
        }
        return result;
    }

    public List<String> getStringArray(String name) {
        List<String> result = null;
        Object value = values.get(name);

        if(value instanceof StringArray) {
            result = (StringArray) value;
        }
        return result;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    private void setValues(Map<String, Object> values) {
        this.values = values;
    }
}
