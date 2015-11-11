package org.lirazs.robolayout.core.resource.drawable;

import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.uikit.UIEdgeInsets;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 7/30/2015.
 */
public class DrawableContainerConstantState extends DrawableConstantState {

    private DrawableContainer owner;

    // Drawables
    private List<Drawable> drawables;

    // Dimension
    private CGSize constantIntrinsicSize;
    private CGSize constantMinimumSize;
    private boolean constantSizeComputed;
    private boolean constantSize;

    // Statful
    private boolean haveStateful;
    private boolean stateful;

    // Padding
    private boolean paddingComputed;
    private UIEdgeInsets padding;
    private boolean hasPadding;

    public DrawableContainerConstantState(DrawableContainer owner) {
        this(null, owner);
    }

    public DrawableContainerConstantState(DrawableContainerConstantState state, DrawableContainer owner) {
        this.owner = owner;

        if(state != null) {
            List<Drawable> drawables = new ArrayList<>();

            for (Drawable drawable : state.getDrawables()) {
                Drawable copyDrawable = drawable.copy();
                copyDrawable.setDelegate(owner);

                drawables.add(copyDrawable);
            }

            this.drawables = drawables;
            this.constantIntrinsicSize = state.constantIntrinsicSize;
            this.constantMinimumSize = state.constantMinimumSize;
            this.constantSizeComputed = state.constantSizeComputed;
            this.haveStateful = state.haveStateful;
            this.stateful = state.stateful;
            this.paddingComputed = state.paddingComputed;
            this.padding = state.padding;
            this.hasPadding = state.hasPadding;
        } else {
            this.drawables = new ArrayList<>();
        }
    }

    public void addChildDrawable(Drawable drawable) {
        drawables.add(drawable);
        drawable.setDelegate(this.owner);

        this.haveStateful = false;
        this.constantSizeComputed = false;
        this.paddingComputed = false;
    }

    public void computeConstantSize() {
        CGSize minSize = CGSize.Zero();
        CGSize intrinsicSize = CGSize.Zero();

        for (Drawable drawable : drawables) {
            CGSize min = drawable.getMinimumSize();
            CGSize intrinsic = drawable.getIntrinsicSize();

            if(min.getWidth() > minSize.getWidth()) minSize.setWidth(min.getWidth());
            if(min.getHeight() > minSize.getHeight()) minSize.setHeight(min.getHeight());

            if(intrinsic.getWidth() > intrinsicSize.getWidth()) intrinsicSize.setWidth(intrinsic.getWidth());
            if(intrinsic.getHeight() > intrinsicSize.getHeight()) intrinsicSize.setHeight(intrinsic.getHeight());
        }

        this.constantIntrinsicSize = intrinsicSize;
        this.constantMinimumSize = minSize;
        this.constantSizeComputed = true;
    }

    public CGSize getConstantIntrinsicSize() {
        if(!isConstantSizeComputed()) {
            computeConstantSize();
        }
        return constantIntrinsicSize;
    }

    public CGSize getConstantMinimumSize() {
        if(!isConstantSizeComputed()) {
            computeConstantSize();
        }
        return constantMinimumSize;
    }

    public boolean isStateful() {
        if(haveStateful) {
            return stateful;
        }
        boolean stateful = false;
        for (Drawable drawable : drawables) {
            if(drawable.isStateful()) {
                stateful = true;
                break;
            }
        }
        this.stateful = stateful;
        haveStateful = true;

        return stateful;
    }

    public void computePadding() {
        UIEdgeInsets padding = UIEdgeInsets.Zero();
        boolean hasPadding = false;

        for (Drawable drawable : drawables) {
            if(drawable.hasPadding()) {
                hasPadding = true;

                UIEdgeInsets childPadding = drawable.getPadding();
                padding.setLeft(Math.max(padding.getLeft(), childPadding.getLeft()));
                padding.setRight(Math.max(padding.getRight(), childPadding.getRight()));
                padding.setTop(Math.max(padding.getTop(), childPadding.getTop()));
                padding.setBottom(Math.max(padding.getBottom(), childPadding.getBottom()));
            }
        }

        this.padding = padding;
        this.hasPadding = hasPadding;
        this.paddingComputed = true;
    }

    public boolean hasPadding() {
        if(!isPaddingComputed()) {
            computePadding();
        }
        return hasPadding;
    }

    public UIEdgeInsets getPadding() {
        return padding;
    }

    public boolean isConstantSizeComputed() {
        return constantSizeComputed;
    }

    public boolean isConstantSize() {
        return constantSize;
    }

    public boolean isPaddingComputed() {
        return paddingComputed;
    }

    public List<Drawable> getDrawables() {
        return drawables;
    }

    public void setConstantSize(boolean constantSize) {
        this.constantSize = constantSize;
    }
}
