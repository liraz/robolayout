package org.lirazs.robolayout.core.resource.state;

import org.lirazs.robolayout.core.collection.NamedNodeMapIterable;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UIControlState;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 7/30/2015.
 */
public class ResourceStateList {

    protected List<ResourceStateItem> internalItems;

    public static UIControlState getControlState(String attributeName) {
        long controlState = UIControlState.Normal.value();

        if(attributeName.equals("state_disabled")) {
            controlState |= UIControlState.Disabled.value();
        } else if(attributeName.equals("state_highlighted")) {
            controlState |= UIControlState.Highlighted.value();
        } else if(attributeName.equals("state_selected")) {
            controlState |= UIControlState.Selected.value();
        }
        return new UIControlState(controlState);
    }

    public static UIControlState getControlState(Element element) {
        long controlState = UIControlState.Normal.value();

        NamedNodeMap attributes = element.getAttributes();
        for (Node node : NamedNodeMapIterable.of(attributes)) {
            String attributeName = node.getNodeName();
            String attributeValue = node.getNodeValue();

            int prefixIndex = attributeName.indexOf(':');
            if(prefixIndex != -1) {
                attributeName = attributeName.substring(prefixIndex + 1);
            }

            boolean value = Boolean.valueOf(attributeValue);
            if(value) {
                controlState |= getControlState(attributeName).value();
            }
        }

        return new UIControlState(controlState);
    }

    public static ResourceStateList inflateDocument(Document document) {
        return inflateDocument(document, new ResourceStateList());
    }
    public static ResourceStateList inflateDocument(Document document, ResourceStateList resourceStateList) {

        Element root = document.getDocumentElement();
        if(root.getTagName().equals("selector")) {
            List<ResourceStateItem> items = new ArrayList<>();

            Node child = root.getFirstChild();
            if(child != null) {
                do {
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        Element elem = (Element) child;

                        UIControlState controlState = getControlState(elem);
                        ResourceStateItem item = resourceStateList.createItem(controlState);

                        items.add(item);
                    }
                } while ((child = child.getNextSibling()) != null);
            }

            resourceStateList.internalItems = items;
        }

        return resourceStateList;
    }

    public static ResourceStateList create(NSData data) {
        return create(data, new ResourceStateList());
    }
    public static ResourceStateList create(NSData data, ResourceStateList resourceStateList) {
        if(data == null)
            return null;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        byte[] bytes = data.getBytes();
        if(bytes != null) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

            try {
                DocumentBuilder db = factory.newDocumentBuilder();
                InputSource inputSource = new InputSource(inputStream);
                Document document = db.parse(inputSource);

                resourceStateList = inflateDocument(document, resourceStateList);
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }
        return resourceStateList;
    }

    public static ResourceStateList create(NSURL url) {
        return create(url, new ResourceStateList());
    }
    public static ResourceStateList create(NSURL url, ResourceStateList resourceStateList) {
        return create(NSData.read(url), resourceStateList);
    }

    public ResourceStateList() {
        internalItems = new ArrayList<>();
    }

    public ResourceStateItem createItem(UIControlState controlState) {
        return new ResourceStateItem(controlState);
    }

    public List<ResourceStateItem> getItems() {
        return internalItems;
    }

    public ResourceStateItem getItem(UIControlState controlState) {
        ResourceStateItem result = null;

        for (ResourceStateItem internalItem : internalItems) {
            long controlStateValue = internalItem.getControlState().value() & controlState.value();
            if(controlStateValue == internalItem.getControlState().value()) {
                result = internalItem;
                break;
            }
        }
        return result;
    }
}
