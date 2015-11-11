package org.lirazs.robolayout.core.view;

import org.robovm.apple.uikit.UIEdgeInsets;

import java.util.Map;

/**
 * Created on 8/3/2015.
 */
public class MarginLayoutParams extends LayoutParams {

    private UIEdgeInsets margin;

    public MarginLayoutParams(LayoutParamsSize width, LayoutParamsSize height) {
        super(width, height);
    }

    public MarginLayoutParams(double width, double height) {
        super(width, height);
    }

    public MarginLayoutParams(LayoutParams layoutParams) {
        super(layoutParams);

        if(layoutParams instanceof MarginLayoutParams) {
            MarginLayoutParams otherLP = (MarginLayoutParams) layoutParams;
            this.margin = otherLP.getMargin();
        }
    }

    public MarginLayoutParams(Map<String, String> attrs) {
        super(attrs);

        String marginString = attrs.get("layout_margin");
        if(marginString != null) {
            double margin = Double.parseDouble(marginString);
            this.margin = new UIEdgeInsets(margin, margin, margin, margin);
        } else {
            String marginLeftString = attrs.get("layout_marginLeft");
            String marginTopString = attrs.get("layout_marginTop");
            String marginBottomString = attrs.get("layout_marginBottom");
            String marginRightString = attrs.get("layout_marginRight");

            this.margin = new UIEdgeInsets(
                    attrs.containsKey("layout_marginTop") ? Double.parseDouble(marginTopString) : 0,
                    attrs.containsKey("layout_marginLeft") ? Double.parseDouble(marginLeftString) : 0,
                    attrs.containsKey("layout_marginBottom") ? Double.parseDouble(marginBottomString) : 0,
                    attrs.containsKey("layout_marginRight") ? Double.parseDouble(marginRightString) : 0
            );
        }
    }

    public UIEdgeInsets getMargin() {
        return margin;
    }

    public void setMargin(UIEdgeInsets margin) {
        this.margin = margin;
    }
}
