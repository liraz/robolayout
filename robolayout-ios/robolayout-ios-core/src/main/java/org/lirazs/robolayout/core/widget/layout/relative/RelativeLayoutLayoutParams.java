package org.lirazs.robolayout.core.widget.layout.relative;

import org.lirazs.robolayout.core.view.LayoutParams;
import org.lirazs.robolayout.core.view.LayoutParamsSize;
import org.lirazs.robolayout.core.view.MarginLayoutParams;

import java.util.Map;

/**
 * Created on 8/5/2015.
 */
public class RelativeLayoutLayoutParams extends MarginLayoutParams {

    private double left;
    private double right;
    private double top;
    private double bottom;

    private boolean alignWithParent;

    private boolean alignParentLeft;
    private boolean alignParentTop;
    private boolean alignParentRight;
    private boolean alignParentBottom;
    private boolean centerInParent;
    private boolean centerHorizontal;
    private boolean centerVertical;

    private String[] rules;

    public RelativeLayoutLayoutParams(LayoutParamsSize width, LayoutParamsSize height) {
        super(width, height);
    }

    public RelativeLayoutLayoutParams(LayoutParams layoutParams) {
        super(layoutParams);
    }

    public RelativeLayoutLayoutParams(Map<String, String> attrs) {
        super(attrs);

        String leftOf = attrs.get("layout_toLeftOf");
        String rightOf = attrs.get("layout_toRightOf");
        String above = attrs.get("layout_above");
        String below = attrs.get("layout_below");
        String alignBaseline = attrs.get("layout_alignBaseline");
        String alignLeft = attrs.get("layout_alignLeft");
        String alignTop = attrs.get("layout_alignTop");
        String alignRight = attrs.get("layout_alignRight");
        String alignBottom = attrs.get("layout_alignBottom");

        alignParentLeft = Boolean.parseBoolean(attrs.get("layout_alignParentLeft"));
        alignParentTop = Boolean.parseBoolean(attrs.get("layout_alignParentTop"));
        alignParentRight = Boolean.parseBoolean(attrs.get("layout_alignParentRight"));
        alignParentBottom = Boolean.parseBoolean(attrs.get("layout_alignParentBottom"));
        centerInParent = Boolean.parseBoolean(attrs.get("layout_centerInParent"));
        centerHorizontal = Boolean.parseBoolean(attrs.get("layout_centerHorizontal"));
        centerVertical = Boolean.parseBoolean(attrs.get("layout_centerVertical"));

        this.rules = new String[] {
                leftOf,
                rightOf,
                above,
                below,
                alignBaseline,
                alignLeft,
                alignTop,
                alignRight,
                alignBottom,
                String.valueOf(alignParentLeft),
                String.valueOf(alignParentTop),
                String.valueOf(alignParentRight),
                String.valueOf(alignParentBottom),
                String.valueOf(centerInParent),
                String.valueOf(centerHorizontal),
                String.valueOf(centerVertical)
        };
    }

    public double getLeft() {
        return left;
    }

    public void setLeft(double left) {
        this.left = left;
    }

    public double getRight() {
        return right;
    }

    public void setRight(double right) {
        this.right = right;
    }

    public double getTop() {
        return top;
    }

    public void setTop(double top) {
        this.top = top;
    }

    public double getBottom() {
        return bottom;
    }

    public void setBottom(double bottom) {
        this.bottom = bottom;
    }

    public boolean isAlignWithParent() {
        return alignWithParent;
    }

    public void setAlignWithParent(boolean alignWithParent) {
        this.alignWithParent = alignWithParent;
    }

    public String[] getRules() {
        return rules;
    }

    public void setRules(String[] rules) {
        this.rules = rules;
    }
}
