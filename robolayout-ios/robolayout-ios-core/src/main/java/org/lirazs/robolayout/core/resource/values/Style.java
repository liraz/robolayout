package org.lirazs.robolayout.core.resource.values;

import org.lirazs.robolayout.core.resource.ResourceManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 8/3/2015.
 */
public class Style {

    public static Style create(Element element) {
        String parentStyleId = element.getAttribute("parent");

        Node child = element.getFirstChild();
        Map<String, String> attributes = new HashMap<>();

        while(child != null) {
            if(child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;

                String name = childElement.getTagName();
                int dotIndex = name.indexOf(":");
                if(dotIndex != -1) {
                    name = name.substring(dotIndex + 1);
                }

                String value = childElement.getTextContent();
                if(!name.isEmpty() && value != null) {
                    attributes.put(name, value);
                }
            }
            child = child.getNextSibling();
        }
        return new Style(attributes, parentStyleId);
    }

    private Map<String, String> internalAttributes;
    private String parentIdentifier;
    private Style internalParentStyle;
    private boolean includesParentStyleAttributes;

    public Style(Map<String, String> attributes, String parentIdentifier) {
        if(!parentIdentifier.isEmpty()) {
            this.parentIdentifier = parentIdentifier;
        } else {
            this.includesParentStyleAttributes = true;
        }
        this.internalAttributes = attributes;
    }

    public Style getParentStyle() {
        Style parentStyle = internalParentStyle;
        if(parentStyle == null && !parentIdentifier.isEmpty()) {
            parentStyle = ResourceManager.getCurrent().getStyle(parentIdentifier);
            internalParentStyle = parentStyle;
        }
        return parentStyle;
    }

    public Map<String, String> getAttributes() {
        // Lazy-load parent style attributes
        // Double-Checked locking should be fine here, even though it is an anti-pattern in other cases
        if(!includesParentStyleAttributes) {
            synchronized (this) {
                if(!includesParentStyleAttributes) {
                    Map<String, String> parentAttributes = getParentStyle().getAttributes();
                    for (String name : parentAttributes.keySet()) {
                        if(!internalAttributes.containsKey(name)) {
                            String value = parentAttributes.get(name);
                            internalAttributes.put(name, value);
                        }
                    }
                    includesParentStyleAttributes = true;
                }
            }
        }
        return internalAttributes;
    }
}
