package org.lirazs.robolayout.core.view;

import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.lirazs.robolayout.core.view.inflater.LayoutInflater;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewAutoresizing;

import java.util.Map;

/**
 * Created on 8/4/2015.
 */
public class TableViewCell extends UITableViewCell implements ViewGroupDelegate, LayoutParamsDelegate {

    private LayoutBridge layoutBridge;

    public TableViewCell(String resource, String reuseIdentifier) {
        super(UITableViewCellStyle.Default, reuseIdentifier);

        LayoutBridge bridge = new LayoutBridge(getContentView().getBounds());
        bridge.setAutoresizingMask(UIViewAutoresizing.FlexibleWidth.set(UIViewAutoresizing.FlexibleHeight));

        getContentView().addSubview(bridge);

        LayoutInflater inflater = new LayoutInflater();
        inflater.inflate(resource, bridge, true);

        this.layoutBridge = bridge;
    }

    public TableViewCell(NSURL url, String reuseIdentifier) {
        super(UITableViewCellStyle.Default, reuseIdentifier);

        LayoutBridge bridge = new LayoutBridge(getContentView().getBounds());
        bridge.setAutoresizingMask(UIViewAutoresizing.FlexibleWidth.set(UIViewAutoresizing.FlexibleHeight));

        getContentView().addSubview(bridge);

        LayoutInflater inflater = new LayoutInflater();
        inflater.inflate(url, bridge, true);

        this.layoutBridge = bridge;
    }

    public LayoutBridge getLayoutBridge() {
        return layoutBridge;
    }

    public void setLayoutBridge(LayoutBridge layoutBridge) {
        this.layoutBridge = layoutBridge;
    }

    @Override
    public boolean isViewGroup() {
        return true;
    }

    @Override
    public void onViewRemoved(UIView view) {

    }

    @Override
    public LayoutParams generateDefaultLayoutParams() {
        return null;
    }

    @Override
    public LayoutParams generateLayoutParams(LayoutParams params) {
        return null;
    }

    @Override
    public LayoutParams generateLayoutParams(Map<String, String> attrs) {
        return null;
    }

    @Override
    public boolean checkLayoutParams(LayoutParams layoutParams) {
        return false;
    }

    @Override
    public CGSize getSizeThatFits(CGSize cgSize) {
        return layoutBridge.getSizeThatFits(cgSize);
    }

    public double getRequiredHeightInView(UIView view) {
        LayoutMeasureSpec widthMeasureSpec = new LayoutMeasureSpec(view.getBounds().getSize().getWidth(), LayoutMeasureSpecMode.Exactly);
        LayoutMeasureSpec heightMeasureSpec = new LayoutMeasureSpec(Double.MAX_VALUE, LayoutMeasureSpecMode.AtMost);

        UIViewLayoutUtil.measure(layoutBridge, widthMeasureSpec, heightMeasureSpec);

        return UIViewLayoutUtil.getMeasuredSize(layoutBridge).getHeight();
    }
}
