package org.lirazs.robolayout.core.resource.drawable;

import org.lirazs.robolayout.core.resource.ResourceManager;
import org.lirazs.robolayout.core.util.DOMUtil;
import org.lirazs.robolayout.core.view.Gravity;
import org.lirazs.robolayout.core.view.ViewContentGravity;
import org.robovm.apple.coregraphics.CGContext;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEdgeInsets;
import org.w3c.dom.Element;

import java.text.ParseException;
import java.util.Map;

/**
 * Created on 7/31/2015.
 */
public class ClipDrawable extends Drawable implements DrawableDelegate {

    private ClipDrawableConstantState internalConstantState;

    public ClipDrawable() {
        this(null);
    }
    public ClipDrawable(ClipDrawableConstantState state) {
        super();

        this.internalConstantState = new ClipDrawableConstantState(state, this);
    }

    public void drawInContext(CGContext context) {
        int level = this.getLevel();
        if(level > 0) {
            ClipDrawableConstantState state = internalConstantState;
            ClipDrawableOrientation orientation = state.getOrientation();

            CGRect r = CGRect.Zero();
            CGRect bounds = this.getBounds();

            double w = bounds.getSize().getWidth();
            double iw = 0; //mClipState.mDrawable.getIntrinsicWidth();

            if(orientation == ClipDrawableOrientation.Horizontal) {
                w -= (w - iw) * (10000 - level) / 10000;
            }

            double h = bounds.getSize().getHeight();
            double ih = 0; //mClipState.mDrawable.getIntrinsicHeight();

            if(orientation == ClipDrawableOrientation.Vertical) {
                h -= (h - ih) * (10000 - level) / 10000;
            }

            Gravity.applyGravity(state.getGravity(), w, h, bounds, r);

            if(w > 0 && h > 0) {
                context.saveGState();
                context.clipToRect(r);

                state.getDrawable().drawInContext(context);
                context.restoreGState();
            }
        }
    }

    @Override
    public DrawableConstantState getConstantState() {
        return this.internalConstantState;
    }

    @Override
    public void inflate(Element element) throws ParseException {
        ClipDrawableConstantState state = this.internalConstantState;

        Map<String, String> attributesFromElement = DOMUtil.getAttributesFromElement(element);
        String orientationString = attributesFromElement.get("clipOrientation");
        state.setOrientation(ClipDrawableOrientation.get(orientationString));

        String gravityString = attributesFromElement.get("gravity");
        if(gravityString != null) {
            state.setGravity(ViewContentGravity.getFromAttribute(gravityString));
        }

        String drawableResId = attributesFromElement.get("drawables");
        Drawable drawable = null;

        Element firstElementChild = DOMUtil.getFirstElementChild(element);

        if(drawableResId != null) {
            drawable = ResourceManager.getCurrent().getDrawable(drawableResId);
        } else if(firstElementChild != null) {
            drawable = Drawable.create(firstElementChild);
        } else {
            throw new ParseException("<item> tag requires a 'drawable' attribute or child tag defining a drawable", 0);
        }

        if(drawable != null) {
            drawable.setDelegate(this);
            drawable.setState(getState());

            state.setDrawable(drawable);
        }
    }

    @Override
    public void onStateChangeToState(UIControlState state) {
        internalConstantState.getDrawable().setState(state);
    }

    @Override
    public void onBoundsChangeToRect(CGRect bounds) {
        internalConstantState.getDrawable().setBounds(bounds);
    }

    @Override
    public boolean onLevelChangeToLevel(int level) {
        internalConstantState.getDrawable().setLevel(level);
        invalidateSelf();

        return true;
    }

    @Override
    public boolean isStateful() {
        return internalConstantState.getDrawable().isStateful();
    }

    @Override
    public UIEdgeInsets getPadding() {
        return internalConstantState.getDrawable().getPadding();
    }

    @Override
    public boolean hasPadding() {
        return internalConstantState.getDrawable().hasPadding();
    }

    @Override
    public void drawableDidInvalidate(Drawable drawable) {
        if (delegate != null) {
            delegate.drawableDidInvalidate(this);
        }
    }
}
