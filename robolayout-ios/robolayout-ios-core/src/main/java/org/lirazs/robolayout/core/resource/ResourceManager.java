package org.lirazs.robolayout.core.resource;

import org.lirazs.robolayout.core.resource.drawable.BitmapDrawable;
import org.lirazs.robolayout.core.resource.drawable.ColorDrawable;
import org.lirazs.robolayout.core.resource.drawable.Drawable;
import org.lirazs.robolayout.core.resource.state.ColorStateList;
import org.lirazs.robolayout.core.resource.state.DrawableStateList;
import org.lirazs.robolayout.core.resource.values.ResourceValueSet;
import org.lirazs.robolayout.core.resource.values.StringArray;
import org.lirazs.robolayout.core.resource.values.Style;
import org.lirazs.robolayout.core.util.ColorParser;
import org.lirazs.robolayout.core.util.UIImageUtil;
import org.apache.commons.io.FilenameUtils;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.*;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIImage;
import org.robovm.objc.Selector;
import org.robovm.objc.annotation.Method;

import java.util.*;

/**
 * Created on 7/30/2015.
 */
public class ResourceManager extends NSObject {

    private static ResourceManager current = null;
    protected ResourceManager() {
        this.xmlCache = XMLCache.getSharedInstance();
        this.resourceIdentifierCache = new HashMap<>();

        NSNotificationCenter.getDefaultCenter().addObserver(this,
                Selector.register("didReceiveMemoryWarning:"),
                UIApplication.DidReceiveMemoryWarningNotification(), null);
    }
    public static ResourceManager getCurrent() {
        if(current == null) {
            current = new ResourceManager();
        }
        return current;
    }

    private Map<String, ResourceIdentifier> resourceIdentifierCache;
    private XMLCache xmlCache;

    public XMLCache getXmlCache() {
        return xmlCache;
    }

    @Method(selector = "didReceiveMemoryWarning:")
    private void didReceiveMemoryWarning(NSNotification nsNotification) {
        for (ResourceIdentifier identifier : resourceIdentifierCache.values()) {
            identifier.setCachedObject(null);
        }
    }

    public boolean isValidIdentifier(String identifier) {
        return ResourceIdentifier.isResourceIdentifier(identifier);
    }

    public boolean invalidateCache(NSBundle bundle) {
        boolean changed = false;

        for (Map.Entry<String, ResourceIdentifier> entry : resourceIdentifierCache.entrySet()) {
            ResourceIdentifier resId = entry.getValue();
            NSBundle resBundle = resId.getBundle();

            if(resBundle.isEqual(bundle) || resBundle.getBundleIdentifier().equals(bundle.getBundleIdentifier())) {
                this.resourceIdentifierCache.remove(entry.getKey());
                changed = true;
            }
        }
        return changed;
    }

    public ResourceIdentifier getResourceIdentifier(String identifierString) {
        ResourceIdentifier identifier = resourceIdentifierCache.get(identifierString);

        if(!resourceIdentifierCache.containsKey(identifierString)) {
            identifier = new ResourceIdentifier(identifierString);
            resourceIdentifierCache.put(identifierString, identifier);
            resourceIdentifierCache.put(identifier.getDescription(), identifier);
        }
        return identifier;
    }

    public NSBundle resolveBundle(ResourceIdentifier identifier) {
        if(identifier.getBundle() == null) {
            if(identifier.getBundleIdentifier() == null) {
                identifier.setBundle(NSBundle.getMainBundle());
            } else {
                identifier.setBundle(NSBundle.getBundle(identifier.getBundleIdentifier()));
            }
        }
        return identifier.getBundle();
    }

    public NSURL getLayoutURL(String identifierString) {
        NSURL result = null;
        ResourceIdentifier identifier = getResourceIdentifier(identifierString);
        if(identifier != null) {
            NSBundle bundle = resolveBundle(identifier);
            String extension = FilenameUtils.getExtension(identifier.getIdentifier());

            if(extension.isEmpty()) {
                extension = "xml";
            }
            String identifierIdentifier = identifier.getIdentifier();
            result = bundle.findResourceURL(identifierIdentifier.substring(0, identifierIdentifier.indexOf('.')), extension);
        }
        return result;
    }

    public UIImage getImage(String identifierString) {
        return getImage(identifierString, true);
    }

    public UIImage getImage(String identifierString, boolean caching) {
        UIImage result = null;
        ResourceIdentifier identifier = getResourceIdentifier(identifierString);
        if(identifier.getType() == ResourceType.Color) {
            UIColor color = getColor(identifierString);
            result = UIImageUtil.createImageFromColor(color, new CGSize(1, 1));

        } else if(identifier.getType() == ResourceType.Drawable) {
            if(identifier.getCachedObject() != null) {
                result = (UIImage) identifier.getCachedObject();
            } else {
                NSBundle bundle = resolveBundle(identifier);
                result = UIImageUtil.createImage(identifier.getIdentifier(), bundle);
            }
            if(caching && result != null) {
                identifier.setCachedObject(result);
            }

        } else {
            String message = String.format("Could not create image from resource identifier %s: Invalid resource type", identifierString);
            throw new IllegalArgumentException(message);
        }
        return result;
    }


    public UIColor getColor(String identifierString) {
        UIColor result = null;
        ResourceIdentifier identifier = getResourceIdentifier(identifierString);
        if(identifier != null) {
            if(identifier.getType() == ResourceType.Drawable) {
                UIImage image = getImage(identifierString);
                if(image != null) {
                    result = UIColor.fromPatternImage(image);
                }
            }
        }
        return result;
    }

    public ColorStateList getColorStateList(String identifierString) {
        ColorStateList colorStateList = null;
        ResourceIdentifier identifier = getResourceIdentifier(identifierString);

        Object cachedObject = identifier.getCachedObject();
        if(cachedObject != null && (cachedObject instanceof  ColorStateList || cachedObject instanceof UIColor)) {
            if(cachedObject instanceof ColorStateList) {
                colorStateList = (ColorStateList) cachedObject;
            } else if(cachedObject instanceof UIColor) {
                colorStateList = ColorStateList.create(identifierString);
            }

        } else if(identifier.getType() == ResourceType.Color) {
            NSBundle bundle = resolveBundle(identifier);
            String identifierIdentifier = identifier.getIdentifier();

            String extension = FilenameUtils.getExtension(identifierIdentifier);
            String fileName = FilenameUtils.getExtension(identifierIdentifier);

            if(extension.isEmpty()) {
                extension = "xml";
            }

            NSURL url = bundle.findResourceURL(fileName, extension);

            if(url != null) {
                colorStateList = ColorStateList.create(url);
            }
            if(colorStateList != null) {
                identifier.setCachedObject(colorStateList);
            }
        }

        if(colorStateList == null) {
            UIColor color = getColor(identifierString);
            if(color != null) {
                colorStateList = ColorStateList.create(identifierString);
            }
        }

        return colorStateList;
    }

    public String getValueSetIdentifier(ResourceIdentifier identifier) {
        String result = null;

        if(identifier.getValueIdentifier() != null) {
            result = identifier.getValueIdentifier();
        } else if(identifier.getIdentifier() != null) {
            int dotIndex = identifier.getIdentifier().indexOf(".");
            if(dotIndex != -1 && dotIndex > 0) {
                String valueSetIdentifier = identifier.getIdentifier().substring(0, dotIndex);
                String bundleIdentifier = identifier.getBundle() != null ? identifier.getBundle().getBundleIdentifier()
                        : identifier.getBundleIdentifier();
                String typeName = ResourceType.asString(ResourceType.Value);

                if(bundleIdentifier != null) {
                    result = String.format("@%s:%s/%s", bundleIdentifier, typeName, valueSetIdentifier);
                } else {
                    result = String.format("@%s/%s", typeName, valueSetIdentifier);
                }
                identifier.setValueIdentifier(result);
            }
        }
        return result;
    }

    public ResourceValueSet getResourceValueSet(String identifierString) {
        ResourceValueSet result = null;
        ResourceIdentifier identifier = getResourceIdentifier(identifierString);

        if(identifier != null && identifier.getType() != ResourceType.Value) {
            String valueSetIdentifier = getValueSetIdentifier(identifier);
            identifier = getResourceIdentifier(valueSetIdentifier);
        }

        if(identifier != null) {
            Object cachedObject = identifier.getCachedObject();
            if(cachedObject != null && cachedObject instanceof ResourceValueSet) {
                result = (ResourceValueSet) cachedObject;
            } else {
                NSBundle bundle = resolveBundle(identifier);
                String extension = FilenameUtils.getExtension(identifier.getIdentifier());

                if(extension.isEmpty()) {
                    extension = "xml";
                }
                String identifierIdentifier = identifier.getIdentifier();
                NSURL url = bundle.findResourceURL(identifierIdentifier.substring(0, identifierIdentifier.indexOf('.')), extension);

                if(url != null) {
                    result = ResourceValueSet.create(url);
                }
                if(result != null) {
                    identifier.setCachedObject(result);
                }
            }
        }
        return result;
    }

    public Style getStyle(String identifierString) {
        Style style = null;
        ResourceIdentifier identifier = getResourceIdentifier(identifierString);

        if(identifier.getType() == ResourceType.Style) {
            if(identifier.getCachedObject() != null) {
                style = (Style) identifier.getCachedObject();
            } else {
                ResourceValueSet valueSet = getResourceValueSet(identifierString);
                if(valueSet != null) {
                    int dotIndex = identifier.getIdentifier().indexOf(".");
                    if(dotIndex != -1 && dotIndex > 0) {
                        style = valueSet.getStyle(identifier.getIdentifier().substring(dotIndex + 1));
                    }
                }
                if(style != null) {
                    identifier.setCachedObject(style);
                }
            }
        }
        return style;
    }

    public String getString(String identifierString) {
        String result = null;
        ResourceIdentifier identifier = getResourceIdentifier(identifierString);

        if(identifier != null) {
            String valueSetIdentifier = getValueSetIdentifier(identifier);

            if(valueSetIdentifier != null && !valueSetIdentifier.isEmpty()) {
                ResourceValueSet valueSet = getResourceValueSet(valueSetIdentifier);
                if(valueSet != null) {
                    int dotIndex = identifier.getIdentifier().indexOf(".");
                    if(dotIndex != -1 && dotIndex > 0) {
                        result = valueSet.getString(identifier.getIdentifier().substring(dotIndex + 1));
                    }
                }
            }


            if(result == null) {
                // Fallback to localized strings
                NSBundle bundle = resolveBundle(identifier);
                result = bundle.getLocalizedString(identifier.getIdentifier(), null, null);
            }
        }
        return result;
    }

    public List<String> getStringArray(String identifierString) {
        List<String> result = null;
        ResourceIdentifier identifier = getResourceIdentifier(identifierString);

        if(identifier.getType() == ResourceType.Array) {
            if(identifier.getCachedObject() != null) {
                result = (StringArray) identifier.getCachedObject();
            } else {
                ResourceValueSet valueSet = getResourceValueSet(identifierString);
                if(valueSet != null) {
                    int dotIndex = identifier.getIdentifier().indexOf(".");
                    if(dotIndex != -1 && dotIndex > 0) {
                        result = valueSet.getStringArray(identifier.getIdentifier().substring(dotIndex + 1));
                    }
                }
                if(result != null) {
                    identifier.setCachedObject(result);
                }
            }
        }
        return result;
    }

    public DrawableStateList getDrawableStateList(String identifierString) {
        DrawableStateList drawableStateList = null;
        ResourceIdentifier identifier = getResourceIdentifier(identifierString);

        Object cachedObject = identifier.getCachedObject();
        if(cachedObject != null && (cachedObject instanceof  DrawableStateList || cachedObject instanceof UIImage)) {
            if(cachedObject instanceof DrawableStateList) {
                drawableStateList = (DrawableStateList) cachedObject;
            } else if(cachedObject instanceof UIImage) {
                drawableStateList = DrawableStateList.create(identifierString);
            }

        } else if(identifier.getType() == ResourceType.Drawable) {
            NSBundle bundle = resolveBundle(identifier);
            String identifierIdentifier = identifier.getIdentifier();

            String extension = FilenameUtils.getExtension(identifierIdentifier);
            String fileName = FilenameUtils.getBaseName(identifierIdentifier);

            if(extension.isEmpty()) {
                extension = "xml";
            }

            NSURL url = bundle.findResourceURL(fileName, extension);

            if(url != null && extension.equals("xml")) {
                drawableStateList = DrawableStateList.create(url);
            }
            if(drawableStateList != null) {
                identifier.setCachedObject(drawableStateList);
            }
        } else if(identifier.getType() == ResourceType.Color) {
            ColorStateList colorStateList = getColorStateList(identifierString);
            if(colorStateList != null) {
                drawableStateList = DrawableStateList.create(colorStateList);
            }
        }

        if(drawableStateList == null) {
            UIImage image = getImage(identifierString);
            if(image != null) {
                drawableStateList = DrawableStateList.create(identifierString);
            }
        }

        return drawableStateList;
    }


    public Drawable getDrawable(String identifierString) {
        Drawable result = null;
        ResourceIdentifier identifier = getResourceIdentifier(identifierString);

        Object cachedObject = identifier.getCachedObject();
        if(identifier.getType() == ResourceType.Drawable && cachedObject != null
                && (cachedObject instanceof  Drawable || cachedObject instanceof UIImage)) {
            if(cachedObject instanceof Drawable) {
                result = ((Drawable) cachedObject).copy();
            } else if(cachedObject instanceof UIImage) {
                result = new BitmapDrawable((UIImage) identifier.getCachedObject());
            }

        } else if(identifier.getType() == ResourceType.Drawable) {
            NSBundle bundle = resolveBundle(identifier);
            String extension = FilenameUtils.getExtension(identifier.getIdentifier());
            String fileName = FilenameUtils.getBaseName(identifier.getIdentifier());

            if(extension.isEmpty()) {
                extension = "xml";
            }
            NSURL url = bundle.findResourceURL(fileName, extension);

            if(url != null) {
                result = Drawable.create(url);
            } else {
                UIImage image = getImage(identifierString);
                if(image != null) {
                    result = new BitmapDrawable(image);
                }
            }
            if(result != null) {
                identifier.setCachedObject(result);
                result = result.copy();
            }
        } else if(identifier.getType() == ResourceType.Color) {
            ColorStateList colorStateList = getColorStateList(identifierString);
            if(colorStateList != null) {
                result = colorStateList.convertToDrawable();
            }
        }

        if(result == null) {
            UIColor color = ColorParser.getColorFromColorString(identifierString);
            if(color != null) {
                result = new ColorDrawable(color);
            } else {
                UIImage image = getImage(identifierString);
                if(image != null) {
                    result = new BitmapDrawable(image);
                }
            }
        }

        return result;
    }


}