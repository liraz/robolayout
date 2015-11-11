package org.lirazs.robolayout.core.widget.layout.frame;

import org.lirazs.robolayout.core.view.LayoutParams;
import org.lirazs.robolayout.core.view.LayoutParamsSize;
import org.lirazs.robolayout.core.view.MarginLayoutParams;
import org.lirazs.robolayout.core.view.ViewContentGravity;

import java.util.Map;

/**
 * Created on 8/5/2015.
 */
public class FrameLayoutLayoutParams extends MarginLayoutParams {
    private ViewContentGravity gravity;

    public FrameLayoutLayoutParams(LayoutParamsSize width, LayoutParamsSize height) {
        super(width, height);
    }

    public FrameLayoutLayoutParams(double width, double height) {
        super(width, height);
    }

    public FrameLayoutLayoutParams(LayoutParams layoutParams) {
        super(layoutParams);
    }

    public FrameLayoutLayoutParams(Map<String, String> attrs) {
        super(attrs);

        String gravityString = attrs.get("layout_gravity");
        this.gravity = ViewContentGravity.getFromAttribute(gravityString);
    }

    public ViewContentGravity getGravity() {
        return gravity;
    }

    public void setGravity(ViewContentGravity gravity) {
        this.gravity = gravity;
    }
}
