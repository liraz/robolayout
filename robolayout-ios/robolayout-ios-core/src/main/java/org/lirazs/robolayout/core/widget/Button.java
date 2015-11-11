package org.lirazs.robolayout.core.widget;

import org.lirazs.robolayout.core.widget.category.UIButtonLayoutUtil;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.UIButton;

import java.util.Map;

/**
 * Created by mac on 7/29/15.
 */
public class Button extends UIButton {

    public Button(Map<String, String> attrs, NSObject actionTarget) {
        UIButtonLayoutUtil.applyAttributes(this, attrs, actionTarget);
    }
}
