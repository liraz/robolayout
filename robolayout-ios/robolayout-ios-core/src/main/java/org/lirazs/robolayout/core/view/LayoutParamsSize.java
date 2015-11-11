package org.lirazs.robolayout.core.view;

/**
 * Created on 8/3/2015.
 */
public enum LayoutParamsSize {
    MatchParent(-1),
    WrapContent(-2);

    public static String LAYOUT_SIZE_MATCH_PARENT = "match_parent";
    public static String LAYOUT_SIZE_FILL_PARENT = "fill_parent";
    public static String LAYOUT_SIZE_WRAP_CONTENT = "wrap_content";

    public static double getSizeForLayoutSizeAttribute(String sizeAttr) {
        double result = 0;
        if(sizeAttr.equals(LAYOUT_SIZE_MATCH_PARENT) || sizeAttr.equals(LAYOUT_SIZE_FILL_PARENT)) {
            result = LayoutParamsSize.MatchParent.getValue();
        } else if(sizeAttr.equals(LAYOUT_SIZE_WRAP_CONTENT)) {
            result = LayoutParamsSize.WrapContent.getValue();
        } else {
            result = Double.parseDouble(sizeAttr);
        }
        return result;
    }

    public static LayoutParamsSize valueOf(int value) {
        for (LayoutParamsSize layoutParamsSize : LayoutParamsSize.values()) {
            if(layoutParamsSize.getValue() == value)
                return layoutParamsSize;
        }
        return null;
    }

    private int value;

    LayoutParamsSize(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
