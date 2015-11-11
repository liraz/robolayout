package org.lirazs.robolayout.core.view;

import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.lirazs.robolayout.core.view.inflater.LayoutInflater;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UICollectionViewCell;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewAutoresizing;

/**
 * Created on 8/7/2015.
 */
public class CollectionViewCell extends UICollectionViewCell {

    private LayoutBridge layoutBridge;

    public CollectionViewCell() {
    }

    public CollectionViewCell(CGRect frame) {
        super(frame);
    }

    public CollectionViewCell(String resource) {
        super(CGRect.Zero());

        LayoutBridge bridge = new LayoutBridge(getContentView().getBounds());
        bridge.setAutoresizingMask(UIViewAutoresizing.FlexibleWidth.set(UIViewAutoresizing.FlexibleHeight));

        getContentView().addSubview(bridge);
        getContentView().setTranslatesAutoresizingMaskIntoConstraints(true);

        LayoutInflater inflater = new LayoutInflater();
        inflater.inflate(resource, bridge, true);

        layoutBridge = bridge;
    }

    public CollectionViewCell(NSURL url) {
        super(CGRect.Zero());

        LayoutBridge bridge = new LayoutBridge(getContentView().getBounds());
        bridge.setAutoresizingMask(UIViewAutoresizing.FlexibleWidth.set(UIViewAutoresizing.FlexibleHeight));

        getContentView().addSubview(bridge);
        getContentView().setTranslatesAutoresizingMaskIntoConstraints(true);

        LayoutInflater inflater = new LayoutInflater();
        inflater.inflate(url, bridge, true);

        layoutBridge = bridge;
    }

    public UIView findViewById(String identifier) {
        return UIViewLayoutUtil.findViewById(this, identifier);
    }

    @Override
    public CGSize getSizeThatFits(CGSize cgSize) {
        return layoutBridge.getSizeThatFits(cgSize);
    }

    public CGSize getPreferredSize() {
        LayoutMeasureSpec widthMeasureSpec = new LayoutMeasureSpec(Double.MAX_VALUE, LayoutMeasureSpecMode.AtMost);
        LayoutMeasureSpec heightMeasureSpec = new LayoutMeasureSpec(Double.MAX_VALUE, LayoutMeasureSpecMode.AtMost);

        UIViewLayoutUtil.measure(layoutBridge, widthMeasureSpec, heightMeasureSpec);

        return UIViewLayoutUtil.getMeasuredSize(layoutBridge);
    }

    public double getRequiredWidthForHeight(double height) {
        LayoutMeasureSpec widthMeasureSpec = new LayoutMeasureSpec(Double.MAX_VALUE, LayoutMeasureSpecMode.AtMost);
        LayoutMeasureSpec heightMeasureSpec = new LayoutMeasureSpec(height, LayoutMeasureSpecMode.Exactly);

        UIViewLayoutUtil.measure(layoutBridge, widthMeasureSpec, heightMeasureSpec);

        return UIViewLayoutUtil.getMeasuredSize(layoutBridge).getWidth();
    }

    public double getRequiredHeightForWidth(double width) {
        LayoutMeasureSpec widthMeasureSpec = new LayoutMeasureSpec(width, LayoutMeasureSpecMode.Exactly);
        LayoutMeasureSpec heightMeasureSpec = new LayoutMeasureSpec(Double.MAX_VALUE, LayoutMeasureSpecMode.AtMost);

        UIViewLayoutUtil.measure(layoutBridge, widthMeasureSpec, heightMeasureSpec);

        return UIViewLayoutUtil.getMeasuredSize(layoutBridge).getHeight();
    }


}
