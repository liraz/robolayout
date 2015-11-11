package org.lirazs.robolayout.core.view;

import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.lirazs.robolayout.core.util.UIViewViewGroupUtil;
import org.lirazs.robolayout.core.widget.View;
import org.lirazs.robolayout.core.widget.category.UIButtonLayoutUtil;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSValue;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIEdgeInsets;
import org.robovm.apple.uikit.UIView;

import java.util.Map;

/**
 * Created on 8/3/2015.
 */
public class ViewGroup extends View implements ViewGroupDelegate {
    public ViewGroup() {
    }

    public ViewGroup(CGRect frame) {
        super(frame);
    }

    public ViewGroup(Map<String, String> attrs) {
        super(attrs);
    }

    public void addView(UIView child, int index) {
        UIViewViewGroupUtil.addView(this, child, index);
    }

    public void addView(UIView child, LayoutParams lp) {
        UIViewViewGroupUtil.addView(this, child, lp);
    }

    public void addView(UIView child) {
        UIViewViewGroupUtil.addView(this, child);
    }

    public void addView(UIView child, CGSize size) {
        UIViewViewGroupUtil.addView(this, child, size);
    }

    public void removeView(UIView child) {
        UIViewViewGroupUtil.removeView(this, child);
    }

    public void removeView(int index) {
        UIViewViewGroupUtil.removeView(this, index);
    }

    public void removeAllViews() {
        UIViewViewGroupUtil.removeAllViews(this);
    }

    @Override
    public boolean isViewGroup() {
        return true;
    }

    @Override
    public void onViewRemoved(UIView view) {

    }
}
