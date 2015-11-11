package org.lirazs.robolayout.core.resource.drawable;

import org.lirazs.robolayout.core.resource.ResourceManager;
import org.lirazs.robolayout.core.util.ColorParser;
import org.lirazs.robolayout.core.util.DOMUtil;
import org.robovm.apple.coregraphics.CGContext;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIGraphics;
import org.w3c.dom.Element;

import java.text.ParseException;
import java.util.Map;

/**
 * Created on 7/31/2015.
 */
public class ColorDrawable extends Drawable {

    private ColorDrawableConstantState internalConstantState;

    public ColorDrawable() {
        this(null, null);
    }
    public ColorDrawable(UIColor color) {
        this(null, color);

    }
    public ColorDrawable(ColorDrawableConstantState state) {
        this(state, null);

    }
    public ColorDrawable(ColorDrawableConstantState state, UIColor color) {
        super();

        this.internalConstantState = new ColorDrawableConstantState(state);
        this.internalConstantState.setColor(color != null ? color : UIColor.clear());
    }

    public UIColor getColor() {
        return internalConstantState.getColor();
    }

    public void drawInContext(CGContext context) {
        UIGraphics.pushContext(context);

        internalConstantState.getColor().setFillAndStroke();
        context.fillRect(getBounds());

        UIGraphics.popContext();

        super.drawInContext(context); // same as OUTLINE_RECT
    }

    @Override
    public void inflate(Element element) throws ParseException {
        Map<String, String> attrs = DOMUtil.getAttributesFromElement(element);
        String colorString = attrs.get("color");
        if(colorString != null) {
            UIColor color = ResourceManager.getCurrent().getColor(colorString);
            if(color == null) {
                color = ColorParser.getColorFromColorString(colorString);
            }
            internalConstantState.setColor(color);
        }
    }

    @Override
    public DrawableConstantState getConstantState() {
        return internalConstantState;
    }

    @Override
    public void onStateChangeToState(UIControlState state) {

    }

    @Override
    public void onBoundsChangeToRect(CGRect bounds) {

    }

    @Override
    public boolean onLevelChangeToLevel(int level) {
        return false;
    }
}
