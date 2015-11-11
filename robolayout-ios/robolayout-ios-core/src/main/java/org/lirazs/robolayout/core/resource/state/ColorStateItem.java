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
public class ColorStateItem extends ResourceStateItem {

    private String resourceIdentifier;

    public ColorStateItem(UIControlState controlState) {
        super(controlState);
    }
    public ColorStateItem(UIControlState controlState, String resourceIdentifier) {
        super(controlState);
        this.resourceIdentifier = resourceIdentifier;
    }

    public UIColor getColor() {
        UIColor result = null;

        if(ResourceManager.getCurrent().isValidIdentifier(resourceIdentifier)) {
            result = ResourceManager.getCurrent().getColor(resourceIdentifier);
        } else {
            // Try parse color string
            result = ColorParser.getColorFromColorString(resourceIdentifier);
        }

        return result;
    }
}
