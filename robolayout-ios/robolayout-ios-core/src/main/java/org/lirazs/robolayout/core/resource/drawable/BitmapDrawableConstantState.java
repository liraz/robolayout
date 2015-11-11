package org.lirazs.robolayout.core.resource.drawable;

import org.lirazs.robolayout.core.view.ViewContentGravity;
import org.robovm.apple.uikit.UIImage;

/**
 * Created on 7/31/2015.
 */
public class BitmapDrawableConstantState extends DrawableConstantState {

    private UIImage image;
    private ViewContentGravity gravity;

    public BitmapDrawableConstantState() {
        this(null);
    }

    public BitmapDrawableConstantState(BitmapDrawableConstantState state) {
        super();

        if(state != null) {
            this.image = state.image;
            this.gravity = state.gravity;
        } else {
            this.gravity = ViewContentGravity.Fill;
        }
    }

    public UIImage getImage() {
        return image;
    }

    public void setImage(UIImage image) {
        this.image = image;
    }

    public ViewContentGravity getGravity() {
        return gravity;
    }

    public void setGravity(ViewContentGravity gravity) {
        this.gravity = gravity;
    }
}
