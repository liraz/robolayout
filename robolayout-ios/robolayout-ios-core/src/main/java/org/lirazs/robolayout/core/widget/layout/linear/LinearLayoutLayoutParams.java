package org.lirazs.robolayout.core.widget.layout.linear;

import org.lirazs.robolayout.core.view.LayoutParams;
import org.lirazs.robolayout.core.view.LayoutParamsSize;
import org.lirazs.robolayout.core.view.MarginLayoutParams;
import org.lirazs.robolayout.core.view.ViewContentGravity;

import java.util.Map;

/**
 * Created on 8/5/2015.
 */
public class LinearLayoutLayoutParams extends MarginLayoutParams {

    private ViewContentGravity gravity;
    private double weight;

    public LinearLayoutLayoutParams(LayoutParamsSize width, LayoutParamsSize height) {
        super(width, height);
    }

    public LinearLayoutLayoutParams(double width, double height) {
        super(width, height);
    }

    public LinearLayoutLayoutParams(LayoutParams layoutParams) {
        super(layoutParams);

        if(layoutParams instanceof LinearLayoutLayoutParams) {
            LinearLayoutLayoutParams params = (LinearLayoutLayoutParams) layoutParams;
            this.setGravity(params.getGravity());
            this.setWeight(params.getWeight());
        }
    }

    public LinearLayoutLayoutParams(Map<String, String> attrs) {
        super(attrs);

        this.gravity = ViewContentGravity.getFromAttribute(attrs.get("layout_gravity"));
        this.weight = attrs.containsKey("layout_weight") ? Double.parseDouble(attrs.get("layout_weight")) : 0;
    }

    public ViewContentGravity getGravity() {
        return gravity;
    }

    public void setGravity(ViewContentGravity gravity) {
        this.gravity = gravity;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
