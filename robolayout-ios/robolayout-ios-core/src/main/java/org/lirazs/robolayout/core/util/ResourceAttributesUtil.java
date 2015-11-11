package org.lirazs.robolayout.core.util;

import org.lirazs.robolayout.core.resource.ResourceManager;
import org.lirazs.robolayout.core.resource.state.ColorStateList;
import org.robovm.apple.uikit.UIColor;

import java.util.Map;

/**
 * Created on 7/31/2015.
 */
public class ResourceAttributesUtil {

    public static String getStringValue(Map<String, String> attrs, String key) {
        String result;
        String value = attrs.get(key);

        if(value != null && ResourceManager.getCurrent().isValidIdentifier(value)) {
            String textFromResources = ResourceManager.getCurrent().getString(value);
            result = textFromResources;
        } else {
            result = value;
        }
        return result;
    }

    public static UIColor getColorValue(Map<String, String> attrs, String key) {
        return getColorValue(attrs, key, null);
    }
    public static UIColor getColorValue(Map<String, String> attrs, String key, UIColor defaultValue) {
        UIColor result = null;
        String value = attrs.get(key);

        if (value != null) {
            if(ResourceManager.getCurrent().isValidIdentifier(value)) {
                result = ResourceManager.getCurrent().getColor(value);
            } else {
                result = ColorParser.getColorFromColorString(value);
            }
        }
        return result != null ? result : defaultValue;
    }

    public static ColorStateList getColorStateListValue(Map<String, String> attrs, String key) {
        ColorStateList result = null;
        String value = attrs.get(key);

        if (value != null) {
            if(ResourceManager.getCurrent().isValidIdentifier(value)) {
                result = ResourceManager.getCurrent().getColorStateList(value);
            }
        }
        return result;
    }

    public static boolean isFractionValue(Map<String, String> attrs, String key) {
        boolean result = false;
        String value = attrs.get(key);

        if(!attrs.containsKey(key)) {
            return false;
        } else {
            if(ResourceManager.getCurrent().isValidIdentifier(value)) {
                //TODO: Implement dimension resources
            }
            result = value.endsWith("%");
        }
        return result;
    }

    public static double getFractionValue(Map<String, String> attrs, String key) {
        return getFractionValue(attrs, key, 0);
    }
    public static double getFractionValue(Map<String, String> attrs, String key, double defaultValue) {
        double result;
        String value = attrs.get(key);

        if(!attrs.containsKey(value)) {
            result = defaultValue;
        } else {
            if(ResourceManager.getCurrent().isValidIdentifier(value)) {
                //TODO: Implement dimension resources
            }
            if(value.endsWith("%")) {
                result = Double.parseDouble(value) / 100.d;
            } else {
                result = Double.parseDouble(value);
            }
        }
        return result;
    }

    public static double getDimensionValue(Map<String, String> attrs, String key) {
        return getDimensionValue(attrs, key, 0);
    }
    public static double getDimensionValue(Map<String, String> attrs, String key, double defaultValue) {
        double result = defaultValue;
        String value = attrs.get(key);

        if(!attrs.containsKey(value)) {
            result = defaultValue;
        } else {
            if(ResourceManager.getCurrent().isValidIdentifier(value)) {
                //TODO: Implement dimension resources
            } else {
                result = Double.parseDouble(value);
            }
        }
        return result;
    }

    public static boolean getBooleanValue(Map<String, String> attrs, String key) {
        return getBooleanValue(attrs, key, false);
    }
    public static boolean getBooleanValue(Map<String, String> attrs, String key, boolean defaultValue) {
        boolean result = defaultValue;
        String value = attrs.get(key);

        if(!attrs.containsKey(value)) {
            result = defaultValue;
        } else {
            if(ResourceManager.getCurrent().isValidIdentifier(value)) {
                //TODO: Implement dimension resources
            } else {
                result = Boolean.valueOf(value);
            }
        }
        return result;
    }
}
