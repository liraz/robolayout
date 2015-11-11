package org.lirazs.robolayout.core.view;

import org.robovm.apple.foundation.NSObject;

import java.util.Map;

/**
 * Created on 8/3/2015.
 */
public class LayoutParams extends NSObject {

    public static LayoutParams generate() {
        return new LayoutParams(LayoutParamsSize.WrapContent, LayoutParamsSize.WrapContent);
    }
    public static LayoutParams generate(Map<String, String> attrs) {
        return new LayoutParams(attrs);
    }



    private double width;
    private double height;

    public LayoutParams(LayoutParamsSize width, LayoutParamsSize height) {
        this(width.getValue(), height.getValue());
    }

    public LayoutParams(double width, double height) {
        super();

        this.width = width;
        this.height = height;
    }

    public LayoutParams(LayoutParams layoutParams) {
        this(layoutParams.getWidth(), layoutParams.getHeight());
    }

    public LayoutParams(Map<String, String> attrs) {
        super();

        String widthAttr = attrs.get("layout_width");
        String heightAttr = attrs.get("layout_height");

        if(widthAttr == null || heightAttr == null) {
            throw new IllegalArgumentException("You have to set the layout_width and layout_height parameters.");
        }
        this.width = LayoutParamsSize.getSizeForLayoutSizeAttribute(widthAttr);
        this.height = LayoutParamsSize.getSizeForLayoutSizeAttribute(heightAttr);
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
