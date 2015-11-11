package org.lirazs.robolayout.core.resource.drawable;

import org.robovm.apple.coregraphics.CGContext;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEdgeInsets;
import org.w3c.dom.Element;

import java.text.ParseException;

/**
 * Created on 7/31/2015.
 */
public class DrawableContainer extends Drawable implements DrawableDelegate {

    private DrawableContainerConstantState internalConstantState;
    private int currentIndex;
    private Drawable currentDrawable;

    public DrawableContainer() {
        this.currentIndex = -1;
    }

    @Override
    public void inflate(Element element)  throws ParseException { }

    @Override
    public void onStateChangeToState(UIControlState state) {
        currentDrawable.setState(state);
    }

    @Override
    public void onBoundsChangeToRect(CGRect bounds) {
        currentDrawable.setBounds(bounds);
    }

    @Override
    public boolean onLevelChangeToLevel(int level) {
        boolean result = false;

        if(currentDrawable != null) {
            result = currentDrawable.setLevel(level);
        }
        return result;
    }

    @Override
    public boolean isStateful() {
        return internalConstantState.isStateful();
    }

    @Override
    public UIEdgeInsets getPadding() {
        return internalConstantState.getPadding();
    }

    @Override
    public boolean hasPadding() {
        return internalConstantState.hasPadding();
    }

    @Override
    public DrawableConstantState getConstantState() {
        return internalConstantState;
    }

    @Override
    public Drawable getCurrent() {
        return currentDrawable;
    }

    @Override
    public void drawableDidInvalidate(Drawable drawable) {
        if(drawable.equals(currentDrawable)) {
            delegate.drawableDidInvalidate(this);
        }
    }

    public void drawInContext(CGContext context) {
        currentDrawable.drawInContext(context);
    }

    public boolean selectDrawableAtIndex(int index) {
        boolean result = true;
        DrawableContainerConstantState state = internalConstantState;
        if(index == currentIndex) {
            result = false;
        } else if(index >= 0 && index < state.getDrawables().size()) {
            Drawable drawable = state.getDrawables().get(index);
            currentDrawable = drawable;
            currentIndex = index;

            drawable.setState(getState());
            drawable.setBounds(getBounds());
            drawable.setLevel(getLevel());
        } else {
            this.currentDrawable = null;
            this.currentIndex = -1;
        }

        if(result)
            invalidateSelf();
        return result;
    }

    public CGSize getIntrinsicSize() {
        CGSize result = CGSize.Zero();
        DrawableContainerConstantState state = internalConstantState;

        if(state.isConstantSize()) {
            result = state.getConstantIntrinsicSize();
        } else {
            result = currentDrawable.getIntrinsicSize();
        }
        return result;
    }

    public CGSize getMinimumSize() {
        CGSize result = CGSize.Zero();
        DrawableContainerConstantState state = this.internalConstantState;

        if(state.isConstantSize()) {
            result = state.getConstantMinimumSize();
        } else {
            result = currentDrawable.getMinimumSize();
        }
        return result;
    }
}
