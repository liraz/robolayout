package org.lirazs.robolayout.core.resource.drawable;

import org.lirazs.robolayout.core.resource.ResourceManager;
import org.lirazs.robolayout.core.util.DOMUtil;
import org.lirazs.robolayout.core.util.ResourceAttributesUtil;
import org.robovm.apple.coregraphics.CGContext;
import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEdgeInsets;
import org.w3c.dom.Element;

import java.text.ParseException;
import java.util.Map;

/**
 * Created on 7/31/2015.
 */
public class RotateDrawable extends Drawable implements DrawableDelegate {

    private RotateDrawableConstantState internalConstantState;

    public RotateDrawable() {
        this(null);
    }
    public RotateDrawable(RotateDrawableConstantState state) {
        super();

        this.internalConstantState = new RotateDrawableConstantState(state, this);
    }

    @Override
    public void drawInContext(CGContext context) {
        RotateDrawableConstantState state = this.internalConstantState;
        CGRect bounds = getBounds();

        // Calculate pivot point
        double px = state.isPivotXRelative() ? (bounds.getSize().getWidth() * state.getPivot().getX()) : state.getPivot().getX();
        double py = state.isPivotYRelative() ? (bounds.getSize().getHeight() * state.getPivot().getY()) : state.getPivot().getY();

        // Save context state
        context.saveGState();

        // Rotate
        context.translateCTM(px, py);
        context.rotateCTM(state.getCurrentDegrees() * Math.PI / 180.d);
        context.translateCTM(-px, -py);

        // Draw child
        state.getDrawable().drawInContext(context);

        // Restore context state
        context.restoreGState();
    }

    @Override
    public DrawableConstantState getConstantState() {
        return internalConstantState;
    }

    @Override
    public void inflate(Element element) throws ParseException {
        RotateDrawableConstantState state = internalConstantState;

        Map<String, String> attrs = DOMUtil.getAttributesFromElement(element);

        CGPoint pivot = new CGPoint(0.5d, 0.5d);
        boolean pivotXRelative = true;
        boolean pivotYRelative = true;

        if(ResourceAttributesUtil.isFractionValue(attrs, "pivotX")) {
            pivot.setX(ResourceAttributesUtil.getFractionValue(attrs, "pivotX"));
        } else if(attrs.containsKey("pivotX")) {
            pivot.setX(ResourceAttributesUtil.getFractionValue(attrs, "pivotX", 0.5d));
            pivotXRelative = false;
        }
        if(ResourceAttributesUtil.isFractionValue(attrs, "pivotY")) {
            pivot.setY(ResourceAttributesUtil.getFractionValue(attrs, "pivotY"));
        } else if(attrs.containsKey("pivotY")) {
            pivot.setY(ResourceAttributesUtil.getFractionValue(attrs, "pivotY", 0.5d));
            pivotYRelative = false;
        }

        state.setPivot(pivot);
        state.setPivotXRelative(pivotXRelative);
        state.setPivotYRelative(pivotYRelative);

        double fromDegrees = ResourceAttributesUtil.getDimensionValue(attrs, "fromDegrees", 0.d);
        double toDegrees = ResourceAttributesUtil.getDimensionValue(attrs, "toDegrees", 360.d);

        toDegrees = Math.max(fromDegrees, toDegrees);

        state.setCurrentDegrees(fromDegrees);
        state.setFromDegrees(state.getCurrentDegrees());
        state.setToDegrees(toDegrees);

        String drawableResId = attrs.get("drawables");
        Drawable drawable = null;

        Element firstElementChild = DOMUtil.getFirstElementChild(element);

        if(drawableResId != null) {
            ResourceManager.getCurrent().getDrawable(drawableResId);
        } else if(firstElementChild != null) {
            drawable = Drawable.create(firstElementChild);
        } else {
            throw new ParseException("<item> tag requires a 'drawable' attribute or child tag defining a drawable", 0);
        }

        if(drawable != null) {
            drawable.setDelegate(this);
            drawable.setState(this.getState());
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
        RotateDrawableConstantState state = this.internalConstantState;

        state.getDrawable().setLevel(level);
        state.setCurrentDegrees(state.getFromDegrees() + (state.getToDegrees() - state.getFromDegrees()) * (level / Drawable.MAX_LEVEL));
        this.invalidateSelf();

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
        if(delegate != null) delegate.drawableDidInvalidate(this);
    }
}
