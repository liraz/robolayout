package org.lirazs.robolayout.core.resource.state;

import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControlState;

/**
 * Created on 7/30/2015.
 */
public class ResourceStateItem {

    private UIControlState controlState;
    private UIColor color;

    public ResourceStateItem(UIControlState controlState) {

        this.controlState = controlState;
    }

    public UIControlState getControlState() {
        return controlState;
    }

    public UIColor getColor() {
        return color;
    }
}
