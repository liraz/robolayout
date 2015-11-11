package org.lirazs.robolayout.core.resource.values;

import org.lirazs.robolayout.core.resource.ResourceManager;
import org.robovm.apple.corefoundation.CFMutableBitVector;
import org.robovm.apple.foundation.NSCharacterSet;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 8/3/2015.
 */
public class StringArray extends ArrayList<String> {

    public static List<String> parseStringArray(Element element) {
        List<String> result = new ArrayList<>();

        Node child = element.getFirstChild();
        NSCharacterSet whitespaceCharacterSet = NSCharacterSet.getWhitespaceAndNewlineCharacterSet();

        while(child != null) {
            String tagName = child.getNodeName();

            if(tagName.equals("item")) {
                String regex = whitespaceCharacterSet.toString();
                String value = child.getTextContent().replaceAll(String.format("%s$|^%s", regex, regex), "");

                result.add(value);
            }
            child = child.getNextSibling();
        }
        return new StringArray(result);
    }

    private CFMutableBitVector resolvedInfo;

    public StringArray(List<String> strings) {
        super(strings);
        resolvedInfo = CFMutableBitVector.create(strings.size());
    }

    @Override
    public String get(int index) {
        String value = super.get(index);

        if (resolvedInfo.get(index) == 0) {
            ResourceManager resMgr = ResourceManager.getCurrent();
            if(resMgr.isValidIdentifier(value)) {
                value = resMgr.getString(value);
                set(index, value);
            }
            resolvedInfo.flipBit(index);
        }
        return value;
    }
}
