package org.lirazs.robolayout.core.test.layout.view;

import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.robovm.apple.uikit.UIView;

import java.util.Map;

/**
 * Created by mac on 8/8/15.
 */
public class CustomTestView extends UIView {
    public CustomTestView(Map<String, String> attrs) {
        UIViewLayoutUtil.applyAttributes(this, attrs);
    }
}
