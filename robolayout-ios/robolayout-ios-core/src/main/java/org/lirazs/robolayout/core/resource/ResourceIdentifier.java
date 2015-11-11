package org.lirazs.robolayout.core.resource;

import org.robovm.apple.foundation.*;

/**
 * Created on 8/3/2015.
 */
public class ResourceIdentifier {

    public static boolean isResourceIdentifier(String string) {
        NSRegularExpression regex = null;
        NSRange rangeOfFirstMatch = null;

        try {
            regex = new NSRegularExpression("@([A-Za-z0-9\\.\\-]+:)?[a-z]+/[A-Za-z0-9_\\.]+", NSRegularExpressionOptions.CaseInsensitive);
            rangeOfFirstMatch = regex.getRangeOfFirstMatch(string, NSMatchingOptions.None, new NSRange(0, string.length()));
        } catch (NSErrorException e) {
            e.printStackTrace();
        }

        return !string.isEmpty() && rangeOfFirstMatch != null && rangeOfFirstMatch.getLocation() != -1;
    }

    private String bundleIdentifier;
    private ResourceType type;
    private String identifier;
    private NSBundle bundle;
    private Object cachedObject;
    private String valueIdentifier;

    public ResourceIdentifier(String string) {

        if(!string.isEmpty() && string.charAt(0) == '@') {
            int separatorIndex = string.indexOf("/");
            if(separatorIndex != -1) {
                NSRange firstPartRange = new NSRange(1, separatorIndex - 1);
                NSRange identifierRange = new NSRange(separatorIndex + 1, string.length() - separatorIndex - 1);
                String identifier = string.substring((int)identifierRange.getLocation(), (int)identifierRange.getLocation() + (int)identifierRange.getLength());

                int colonIndex = string.substring((int) firstPartRange.getLocation(),
                        (int) firstPartRange.getLocation() + (int) firstPartRange.getLength()).indexOf(":");

                String bundleIdentifier = null;
                String typeIdentifier = null;

                if(colonIndex != -1) {
                    bundleIdentifier = string.substring(1, colonIndex);
                    int beginIndex = colonIndex + (int) firstPartRange.getLocation();
                    typeIdentifier = string.substring(beginIndex, beginIndex + (int)firstPartRange.getLength() - colonIndex);
                } else {
                    typeIdentifier = string.substring((int)firstPartRange.getLocation(), (int)firstPartRange.getLocation() + (int)firstPartRange.getLength());
                }

                this.bundleIdentifier = bundleIdentifier;
                this.type = ResourceType.get(typeIdentifier);

                Foundation.log("%@ %@", new NSString(string), new NSString(typeIdentifier));

                this.identifier = identifier;
            }
        }
    }

    public String getDescription() {
        String result = null;
        String bundleIdentifier = this.bundle != null ? this.bundle.getBundleIdentifier() : this.bundleIdentifier;
        String typeName = this.type != null ? ResourceType.asString(this.type) : null;

        if(bundleIdentifier != null && typeName != null) {
            result = String.format("@%s:%s/%s", bundleIdentifier, typeName, this.identifier);
        } else if(typeName != null) {
            result = String.format("@%s/%s", typeName, this.identifier);
        } else {
            result = String.format("@+id/%s", this.identifier);
        }
        return result;
    }

    public String getBundleIdentifier() {
        return bundleIdentifier;
    }

    public void setBundleIdentifier(String bundleIdentifier) {
        this.bundleIdentifier = bundleIdentifier;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public NSBundle getBundle() {
        return bundle;
    }

    public void setBundle(NSBundle bundle) {
        this.bundle = bundle;
    }

    public Object getCachedObject() {
        return cachedObject;
    }

    public void setCachedObject(Object cachedObject) {
        this.cachedObject = cachedObject;
    }

    public String getValueIdentifier() {
        return valueIdentifier;
    }

    public void setValueIdentifier(String valueIdentifier) {
        this.valueIdentifier = valueIdentifier;
    }
}
