package org.lirazs.robolayout.core.widget;

import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.lirazs.robolayout.core.view.LayoutMeasuredSize;
import org.lirazs.robolayout.core.view.ViewVisibility;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.uikit.UIEdgeInsets;
import org.robovm.apple.uikit.UIGestureRecognizer;
import org.robovm.apple.uikit.UITapGestureRecognizer;
import org.robovm.apple.uikit.UIView;

import java.util.Map;

/**
 * Created on 8/9/2015.
 */
public class View extends UIView {

    public View() {
    }

    public View(CGRect frame) {
        super(frame);
    }

    public View(Map<String, String> attrs) {
        UIViewLayoutUtil.applyAttributes(this, attrs);
    }

    public ViewVisibility getVisibility() {
        return UIViewLayoutUtil.getVisibility(this);
    }

    public void setVisibility(ViewVisibility visibility) {
        UIViewLayoutUtil.setVisibility(this, visibility);
    }

    public CGSize getMeasuredSize() {
        return UIViewLayoutUtil.getMeasuredSize(this);
    }

    public UIEdgeInsets getPadding() {
        return UIViewLayoutUtil.getPadding(this);
    }

    public void setPadding(UIEdgeInsets padding) {
        UIViewLayoutUtil.setPadding(this, padding);
    }

    public CGSize getMinSize() {
        return UIViewLayoutUtil.getMinSize(this);
    }

    public void setMinSize(CGSize size) {
        UIViewLayoutUtil.setMinSize(this, size);
    }

    public CGSize getSuggestedMinimumSize() {
        return getMinSize();
    }

    public LayoutMeasuredSize getMeasuredDimensionSize() {
        return UIViewLayoutUtil.getMeasuredDimensionSize(this);
    }

    public void setMeasuredDimensionSize(LayoutMeasuredSize layoutMeasuredSize) {
        UIViewLayoutUtil.setMeasuredDimensionSize(this, layoutMeasuredSize);
    }

    public UIView findViewById(String identifier) {
        return UIViewLayoutUtil.findViewById(this, identifier);
    }

    public void addOnTouchListener(UIGestureRecognizer.OnGestureListener listener) {

        UITapGestureRecognizer singleTap = new UITapGestureRecognizer();

        singleTap.setNumberOfTapsRequired(1);
        singleTap.setNumberOfTouchesRequired(1);

        addGestureRecognizer(singleTap);

        singleTap.addListener(listener);
    }
}
