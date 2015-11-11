package org.lirazs.robolayout.core.resource.drawable;

import org.lirazs.robolayout.core.resource.ResourceManager;
import org.lirazs.robolayout.core.util.DOMUtil;
import org.lirazs.robolayout.core.util.ResourceAttributesUtil;
import org.robovm.apple.coregraphics.CGContext;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEdgeInsets;
import org.w3c.dom.Element;

import java.text.ParseException;
import java.util.Map;

/**
 * Created on 7/31/2015.
 */
public class ShadowDrawable extends Drawable implements DrawableDelegate {

    private ShadowDrawableConstantState internalConstantState;

    public ShadowDrawable() {
        this(null);
    }
    public ShadowDrawable(ShadowDrawableConstantState state) {
        super();

        this.internalConstantState = new ShadowDrawableConstantState(state, this);
    }

    @Override
    public void drawInContext(CGContext context) {
        ShadowDrawableConstantState state = this.internalConstantState;

        context.setAlpha(state.getAlpha());
        if(state.getShadowColor() != null) {
            context.setShadow(state.getOffset(), state.getBlur(), state.getShadowColor().getCGColor());

        } else if(state.getBlur() > 0 || !state.getOffset().equalsTo(CGSize.Zero())) {
            context.setShadow(state.getOffset(), state.getBlur());
        }

        context.beginTransparencyLayer(this.getBounds(), null);
        // Draw child
        state.getDrawable().drawInContext(context);
        context.endTransparencyLayer();
    }

    @Override
    public DrawableConstantState getConstantState() {
        return internalConstantState;
    }

    @Override
    public void inflate(Element element) throws ParseException {
        ShadowDrawableConstantState state = this.internalConstantState;

        Map<String, String> attrs = DOMUtil.getAttributesFromElement(element);

        state.setAlpha(ResourceAttributesUtil.getFractionValue(attrs, "alpha", 1));

        CGSize offset = new CGSize(0, 0);
        offset.setWidth(ResourceAttributesUtil.getDimensionValue(attrs, "shadowHorizontalOffset", 0));
        offset.setHeight(ResourceAttributesUtil.getDimensionValue(attrs, "shadowVerticalOffset", 0));
        state.setOffset(offset);

        state.setBlur(Math.abs(ResourceAttributesUtil.getDimensionValue(attrs, "blur", 0)));
        state.setShadowColor(ResourceAttributesUtil.getColorValue(attrs, "shadowColor"));

        String drawableResId = attrs.get("drawables");
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
        ShadowDrawableConstantState state = this.internalConstantState;

        CGSize boundsSize = bounds.getSize();
        double offsetWidth = state.getOffset().getWidth();
        double offsetHeight = state.getOffset().getHeight();

        if(offsetWidth > 0) {
            boundsSize.setWidth(boundsSize.getWidth() - offsetWidth);
        } else if(offsetWidth < 0) {
            bounds.getOrigin().setX(bounds.getOrigin().getX() - offsetWidth);
            boundsSize.setWidth(boundsSize.getWidth() + offsetWidth);
        }

        if(offsetHeight > 0) {
            boundsSize.setHeight(boundsSize.getHeight() - offsetHeight);
        } else if(offsetWidth < 0) {
            bounds.getOrigin().setY(bounds.getOrigin().getY() - offsetWidth);
            boundsSize.setHeight(boundsSize.getHeight() + offsetHeight);
        }

        internalConstantState.getDrawable().setBounds(bounds);
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
    public boolean onLevelChangeToLevel(int level) {
        return false;
    }

    @Override
    public void drawableDidInvalidate(Drawable drawable) {
        if(delegate != null) delegate.drawableDidInvalidate(this);
    }
}
