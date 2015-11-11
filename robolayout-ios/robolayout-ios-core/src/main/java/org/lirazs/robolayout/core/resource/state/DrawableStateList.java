package org.lirazs.robolayout.core.resource.state;

import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIImage;
import org.w3c.dom.Element;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 7/30/2015.
 */
public class DrawableStateList extends ResourceStateList {

    public static DrawableStateItem createItem(UIControlState controlState, Element element) throws ParseException {
        DrawableStateItem result = null;
        String drawableIdentifier = element.getAttribute("drawables");

        if(drawableIdentifier == null) {
            throw new ParseException("<item> tag requires a 'drawable' attribute. I'm ignoring this drawable state item.", 0);
        } else {
            result = new DrawableStateItem(controlState, drawableIdentifier);
        }
        return result;
    }

    public static DrawableStateList create(String imageIdentifier) {
        DrawableStateList result = new DrawableStateList();
        DrawableStateItem item = new DrawableStateItem(UIControlState.Normal, imageIdentifier);

        result.internalItems = Collections.<ResourceStateItem>singletonList(item);
        return result;
    }

    public static DrawableStateList create(NSData data) {
        return (DrawableStateList) create(data, new DrawableStateList());
    }
    public static DrawableStateList create(NSURL url) {
        return (DrawableStateList) create(url, new DrawableStateList());
    }

    public static DrawableStateList create(ColorStateList colorStateList) {
        DrawableStateList result = null;

        if(colorStateList != null) {
            result = new DrawableStateList();
            List<ResourceStateItem> items = new ArrayList<>();

            for (ResourceStateItem internalItem : colorStateList.internalItems) {
                DrawableStateItem item = new ColorWrapperDrawableStateItem((ColorStateItem) internalItem);
                items.add(item);
            }
            result.internalItems = items;
        }
        return result;
    }

    public DrawableStateItem createItem(UIControlState controlState) {
        return new DrawableStateItem(controlState);
    }

    public UIImage getImageForControlState(UIControlState controlState) {
        return getImageForControlState(controlState, null);
    }
    public UIImage getImageForControlState(UIControlState controlState, UIImage defaultImage) {
        UIImage result = defaultImage;
        DrawableStateItem item = (DrawableStateItem) getItem(controlState);

        UIImage image = item.getImage();
        if(image != null) {
            result = image;
        }
        return result;
    }
}
