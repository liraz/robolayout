package org.lirazs.robolayout.core.resource.state;

import org.lirazs.robolayout.core.resource.drawable.Drawable;
import org.lirazs.robolayout.core.resource.drawable.StateListDrawable;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControlState;
import org.w3c.dom.Element;

import java.text.ParseException;
import java.util.Collections;

/**
 * Created on 7/30/2015.
 */
public class ColorStateList extends ResourceStateList {

    public static ColorStateItem createItem(UIControlState controlState, Element element) throws ParseException {
        ColorStateItem result = null;
        String drawableIdentifier = element.getAttribute("color");

        if(drawableIdentifier == null) {
            throw new ParseException("<item> tag requires a 'color' attribute. I'm ignoring this color state item.", 0);
        } else {
            result = new ColorStateItem(controlState, drawableIdentifier);
        }
        return result;
    }

    public static ColorStateList create(String colorIdentifier) {
        ColorStateList result = new ColorStateList();
        ColorStateItem item = new ColorStateItem(UIControlState.Normal, colorIdentifier);

        result.internalItems = Collections.<ResourceStateItem>singletonList(item);
        return result;
    }

    public static ColorStateList create(NSData data) {
        return (ColorStateList) create(data, new ColorStateList());
    }
    public static ColorStateList create(NSURL url) {
        return (ColorStateList) create(url, new ColorStateList());
    }

    public ColorStateItem createItem(UIControlState controlState) {
        return new ColorStateItem(controlState);
    }

    public UIColor createColor(UIControlState controlState) {
        return createColor(controlState, null);
    }
    public UIColor createColor(UIControlState controlState, UIColor defaultColor) {
        UIColor result = defaultColor;
        ColorStateItem item = createItem(controlState);

        UIColor color = item.getColor();
        if(color != null) {
            result = color;
        }
        return result;
    }

    public Drawable convertToDrawable() {
        StateListDrawable drawable = new StateListDrawable(this);
        return drawable;
    }
}
