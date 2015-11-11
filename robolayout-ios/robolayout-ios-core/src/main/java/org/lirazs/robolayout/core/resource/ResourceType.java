package org.lirazs.robolayout.core.resource;

/**
 * Created on 8/3/2015.
 */
public enum ResourceType {
    Unknown,
    String,
    Layout,
    Drawable,
    Color,
    Style,
    Value,
    Array;

    public static ResourceType get(String typeString) {
        ResourceType result = Unknown;
        if (typeString.equals("string")) {
            result = String;
        } else if (typeString.equals("layout")) {
            result = Layout;
        } else if (typeString.equals("drawable")) {
            result = Drawable;
        } else if (typeString.equals("color")) {
            result = Color;
        } else if (typeString.equals("style")) {
            result = Style;
        } else if (typeString.equals("value")) {
            result = Value;
        } else if (typeString.equals("array")) {
            result = Array;
        }
        return result;
    }
    
    public static String asString(ResourceType resourceType) {
        String result = null;
        switch (resourceType) {
            case String:
                result = "string";
                break;
            case Layout:
                result = "layout";
                break;
            case Drawable:
                result = "drawable";
                break;
            case Color:
                result = "color";
                break;
            case Style:
                result = "style";
                break;
            case Value:
                result = "value";
                break;
            case Array:
                result = "array";
                break;
        }
        return result;
    }
}
