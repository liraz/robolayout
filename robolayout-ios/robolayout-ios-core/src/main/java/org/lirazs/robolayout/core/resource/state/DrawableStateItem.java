package org.lirazs.robolayout.core.resource.state;

import org.lirazs.robolayout.core.resource.ResourceManager;
import org.lirazs.robolayout.core.util.ColorParser;
import org.lirazs.robolayout.core.util.UIImageUtil;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIImage;

/**
 * Created on 7/30/2015.
 */
public class DrawableStateItem extends ResourceStateItem {

    private String resourceIdentifier;

    public DrawableStateItem(UIControlState controlState) {
        super(controlState);
    }
    public DrawableStateItem(UIControlState controlState, String resourceIdentifier) {
        super(controlState);
        this.resourceIdentifier = resourceIdentifier;
    }

    public UIImage getImage() {
        UIImage result = null;

        if(resourceIdentifier != null && ResourceManager.getCurrent().isValidIdentifier(resourceIdentifier)) {
            result = ResourceManager.getCurrent().getImage(resourceIdentifier);
        } else {
            // Try parse color string
            UIColor color = ColorParser.getColorFromColorString(resourceIdentifier);
            if(color != null) {
                result = UIImageUtil.createImageFromColor(color, new CGSize(1, 1));
            }
        }

        return result;
    }
}
