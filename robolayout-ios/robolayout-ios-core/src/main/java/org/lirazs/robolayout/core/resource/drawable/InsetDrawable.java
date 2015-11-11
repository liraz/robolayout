package org.lirazs.robolayout.core.resource.drawable;

import org.lirazs.robolayout.core.resource.ResourceManager;
import org.lirazs.robolayout.core.util.DOMUtil;
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
public class InsetDrawable extends Drawable implements DrawableDelegate {

    private InsetDrawableConstantState internalConstantState;

    public InsetDrawable() {
        this(null);
    }
    public InsetDrawable(InsetDrawableConstantState state) {
        super();

        this.internalConstantState = new InsetDrawableConstantState(state, this);
    }

    @Override
    public void drawInContext(CGContext context) {
        internalConstantState.getDrawable().drawInContext(context);
        super.drawInContext(context);
    }

    @Override
    public CGSize getMinimumSize() {
        return internalConstantState.getDrawable().getMinimumSize();
    }

    @Override
    public CGSize getIntrinsicSize() {
        return internalConstantState.getDrawable().getIntrinsicSize();
    }

    @Override
    public void inflate(Element element) throws ParseException {
        Map<String, String> attrs = DOMUtil.getAttributesFromElement(element);

        UIEdgeInsets insets = UIEdgeInsets.Zero();
        insets.setLeft(Double.parseDouble(attrs.get("insetLeft")));
        insets.setTop(Double.parseDouble(attrs.get("insetTop")));
        insets.setRight(Double.parseDouble(attrs.get("insetRight")));
        insets.setBottom(Double.parseDouble(attrs.get("insetBottom")));

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
            drawable.setState(this.getState());
            internalConstantState.setDrawable(drawable);
            internalConstantState.setInsets(insets);
        }
    }

    @Override
    public void onStateChangeToState(UIControlState state) {
        internalConstantState.getDrawable().setState(state);
        onBoundsChangeToRect(getBounds());
    }

    @Override
    public void onBoundsChangeToRect(CGRect bounds) {
        CGRect insetRect = getBounds().inset(internalConstantState.getInsets());
        internalConstantState.getDrawable().setBounds(insetRect);
    }

    @Override
    public boolean onLevelChangeToLevel(int level) {
        return internalConstantState.getDrawable().setLevel(level);
    }

    @Override
    public UIEdgeInsets getPadding() {
        UIEdgeInsets insets = this.internalConstantState.getInsets();
        if(internalConstantState.getDrawable().hasPadding()) {
            UIEdgeInsets childInsets = internalConstantState.getDrawable().getPadding();
            insets.setLeft(insets.getLeft() + childInsets.getLeft());
            insets.setTop(insets.getTop() + childInsets.getTop());
            insets.setRight(insets.getRight() + childInsets.getRight());
            insets.setBottom(insets.getBottom() + childInsets.getBottom());
        }
        return insets;
    }

    @Override
    public boolean hasPadding() {
        return internalConstantState.getDrawable().hasPadding() || !internalConstantState.getInsets().equalsTo(UIEdgeInsets.Zero());
    }

    @Override
    public DrawableConstantState getConstantState() {
        return internalConstantState;
    }

    @Override
    public void drawableDidInvalidate(Drawable drawable) {
        if(delegate != null) delegate.drawableDidInvalidate(this);
    }
}
