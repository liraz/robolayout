package org.lirazs.robolayout.core.resource.drawable;

import org.robovm.apple.uikit.UIColor;

/**
 * Created on 7/31/2015.
 */
public class ColorDrawableConstantState extends DrawableConstantState {

    private UIColor color;

    public ColorDrawableConstantState() {
        this(null);
    }
    public ColorDrawableConstantState(ColorDrawableConstantState state) {
        super();

        if(state != null) {
            this.color = state.getColor();
        } else {
            this.color = UIColor.clear();
        }
    }

    public UIColor getColor() {
        return color;
    }

    public void setColor(UIColor color) {
        this.color = color;
    }
}
