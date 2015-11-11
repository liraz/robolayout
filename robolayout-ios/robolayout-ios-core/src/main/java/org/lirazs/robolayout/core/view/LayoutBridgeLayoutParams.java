package org.lirazs.robolayout.core.view;

import java.util.Map;

/**
 * Created on 8/7/2015.
 */
public class LayoutBridgeLayoutParams extends MarginLayoutParams {

    public LayoutBridgeLayoutParams(double width, double height) {
        super(width, height);
    }

    public LayoutBridgeLayoutParams(LayoutParams layoutParams) {
        super(layoutParams);
    }

    public LayoutBridgeLayoutParams(Map<String, String> attrs) {
        super(attrs);
    }

    public LayoutBridgeLayoutParams(LayoutParamsSize width, LayoutParamsSize height) {
        super(width, height);
    }
}
