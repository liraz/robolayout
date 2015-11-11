package org.lirazs.robolayout.core.resource.state;

import org.lirazs.robolayout.core.util.UIImageUtil;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.uikit.UIImage;

/**
 * Created on 7/30/2015.
 */
public class ColorWrapperDrawableStateItem extends DrawableStateItem {

    private ColorStateItem colorStateItem;

    public ColorWrapperDrawableStateItem(ColorStateItem colorStateItem) {
        super(colorStateItem.getControlState(), null);
        this.colorStateItem = colorStateItem;
    }

    @Override
    public UIImage getImage() {
        return UIImageUtil.createImageFromColor(colorStateItem.getColor(), new CGSize(1, 1));
    }
}
