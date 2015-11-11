package org.lirazs.robolayout.core.util;

import org.lirazs.robolayout.core.resource.drawable.Drawable;
import org.lirazs.robolayout.core.view.*;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSValue;
import org.robovm.apple.uikit.UIEdgeInsets;
import org.robovm.apple.uikit.UIScrollView;
import org.robovm.apple.uikit.UIView;

import java.util.Map;

/**
 * Created by mac on 7/29/15.
 */
public class UIViewViewGroupUtil {

    public static boolean isViewGroup(UIView view) {
        return view instanceof ViewGroupDelegate && ((ViewGroupDelegate) view).isViewGroup();
    }

    public static LayoutMeasureSpec getChildMeasureSpec(LayoutMeasureSpec parentSpec, double padding, double childDimension) {
        LayoutMeasureSpecMode parentSpecMode = parentSpec.getMode();
        double parentSpecSize = parentSpec.getSize();

        double size = Math.max(0, parentSpecSize - padding);

        LayoutMeasureSpec result = new LayoutMeasureSpec();
        result.setSize(0);
        result.setMode(LayoutMeasureSpecMode.Unspecified);

        switch (parentSpecMode) {
            // Parent has imposed an exact size on us
            case Exactly:
                if(childDimension >= 0) {
                    result.setSize(childDimension);
                    result.setMode(LayoutMeasureSpecMode.Exactly);

                } else if(childDimension == LayoutParamsSize.MatchParent.getValue()) {
                    // Child wants to be our size. So be it.
                    result.setSize(size);
                    result.setMode(LayoutMeasureSpecMode.Exactly);

                } else if(childDimension == LayoutParamsSize.WrapContent.getValue()) {
                    // Child wants to determine its own size. It can't be
                    // bigger than us.
                    result.setSize(size);
                    result.setMode(LayoutMeasureSpecMode.AtMost);
                }
                break;

            // Parent has imposed a maximum size on us
            case AtMost:
                if(childDimension >= 0) {
                    // Child wants a specific size... so be it
                    result.setSize(childDimension);
                    result.setMode(LayoutMeasureSpecMode.Exactly);

                } else if(childDimension == LayoutParamsSize.MatchParent.getValue()) {
                    // Child wants to be our size, but our size is not fixed.
                    // Constrain child to not be bigger than us.
                    result.setSize(size);
                    result.setMode(LayoutMeasureSpecMode.AtMost);

                } else if(childDimension == LayoutParamsSize.WrapContent.getValue()) {
                    // Child wants to determine its own size. It can't be
                    // bigger than us.
                    result.setSize(size);
                    result.setMode(LayoutMeasureSpecMode.AtMost);
                }
                break;

            // Parent asked to see how big we want to be
            case Unspecified:
                if(childDimension >= 0) {
                    // Child wants a specific size... let him have it
                    result.setSize(childDimension);
                    result.setMode(LayoutMeasureSpecMode.Exactly);

                } else if(childDimension == LayoutParamsSize.MatchParent.getValue()) {
                    // Child wants to be our size... find out how big it should be
                    result.setSize(0);
                    result.setMode(LayoutMeasureSpecMode.Unspecified);

                } else if(childDimension == LayoutParamsSize.MatchParent.getValue()) {
                    // Child wants to determine its own size.... find out how
                    // big it should be
                    result.setSize(0);
                    result.setMode(LayoutMeasureSpecMode.Unspecified);
                }
                break;
        }
        return result;
    }

    public static void measureChild(UIView view, UIView child, LayoutMeasureSpec parentWidthMeasureSpec,
                                    double widthUsed, LayoutMeasureSpec parentHeightMeasureSpec, double heightUsed) {

        MarginLayoutParams lp = (MarginLayoutParams) UIViewLayoutUtil.getLayoutParams(child);
        UIEdgeInsets lpMargin = lp.getMargin();
        UIEdgeInsets padding = UIViewLayoutUtil.getPadding(view);

        double leftRightPaddingMargin = padding.getLeft() + padding.getRight() + lpMargin.getLeft() + lpMargin.getRight();
        double topBottomPaddingMargin = padding.getTop() + padding.getBottom() + lpMargin.getTop() + lpMargin.getBottom();

        LayoutMeasureSpec childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, leftRightPaddingMargin + widthUsed, lp.getWidth());
        LayoutMeasureSpec childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec, topBottomPaddingMargin + heightUsed, lp.getHeight());

        //Foundation.log(String.format("Measure child -> %s", UIViewLayoutUtil.getIdentifier(child)));

        UIViewLayoutUtil.measure(child, childWidthMeasureSpec, childHeightMeasureSpec);
    }

    /**
     * Ask one of the children of this view to measure itself, taking into
     * account both the MeasureSpec requirements for this view and its padding.
     * The heavy lifting is done in getChildMeasureSpec.
     *
     * @param child The child to measure
     * @param parentWidthMeasureSpec The width requirements for this view
     * @param parentHeightMeasureSpec The height requirements for this view
     */
    public static void measureChild(UIView view, UIView child, LayoutMeasureSpec parentWidthMeasureSpec,
                                    LayoutMeasureSpec parentHeightMeasureSpec) {

        LayoutParams lp = UIViewLayoutUtil.getLayoutParams(child);
        UIEdgeInsets padding = UIViewLayoutUtil.getPadding(view);

        double leftRightPadding = padding.getLeft() + padding.getRight();
        double topBottomPadding = padding.getTop() + padding.getBottom();

        LayoutMeasureSpec childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, leftRightPadding, lp.getWidth());
        LayoutMeasureSpec childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec, topBottomPadding, lp.getHeight());

        UIViewLayoutUtil.measure(child, childWidthMeasureSpec, childHeightMeasureSpec);
    }

    /**
     * Ask all of the children of this view to measure themselves, taking into
     * account both the MeasureSpec requirements for this view and its padding.
     * We skip children that are in the GONE state The heavy lifting is done in
     * getChildMeasureSpec.
     *
     * @param widthMeasureSpec The width requirements for this view
     * @param heightMeasureSpec The height requirements for this view
     */
    public static void measureChildren(UIView view, LayoutMeasureSpec widthMeasureSpec, LayoutMeasureSpec heightMeasureSpec) {

        for (UIView child : view.getSubviews()) {
            if(UIViewLayoutUtil.getVisibility(child) != ViewVisibility.Gone) {
                measureChild(view, child, widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    public static UIView findViewTraversal(UIView view, String identifier) {
        if(identifier.equals(UIViewLayoutUtil.getIdentifier(view))) {
            return view;
        }

        NSArray<UIView> subviews = view.getSubviews();
        for (UIView subview : subviews) {
            UIView viewById = UIViewLayoutUtil.findViewById(subview, identifier);
            if(viewById != null) {
                return viewById;
            }
        }

        return null;
    }


    public static void addView(UIView parent, UIView child, int index, LayoutParams lp) {
        if(!isViewGroup(parent)) {
            throw new UnsupportedOperationException("Views can only be added on ViewGroup objects");
        }

        if(!UIViewLayoutUtil.checkLayoutParams(parent, lp)) {
            if(lp != null) {
                lp = UIViewLayoutUtil.generateLayoutParams(parent, lp);
            }
            if(lp == null || !UIViewLayoutUtil.checkLayoutParams(parent, lp)) {
                lp = UIViewLayoutUtil.generateDefaultLayoutParams(parent);
            }
        }

        UIViewLayoutUtil.setLayoutParams(child, lp);

        if(index == -1) {
            parent.addSubview(child);
        } else {
            parent.insertSubview(child, index);
        }
        UIViewLayoutUtil.requestLayout(parent);
    }


    public static void addView(UIView parent, UIView child, int index) {
        LayoutParams params = UIViewLayoutUtil.getLayoutParams(child);
        if(params == null) {
            params = UIViewLayoutUtil.generateDefaultLayoutParams(parent);
            if(params == null) {
                throw new IllegalArgumentException("generateDefaultLayoutParams() cannot return null");
            }
        }
        addView(parent, child, index, params);
    }

    public static void addView(UIView parent, UIView child, LayoutParams lp) {
        addView(parent, child, -1, lp);
    }

    public static void addView(UIView parent, UIView child) {
        addView(parent, child, -1);
    }

    public static void addView(UIView parent, UIView child, CGSize size) {
        LayoutParams lp = UIViewLayoutUtil.generateDefaultLayoutParams(parent);
        lp.setWidth(size.getWidth());
        lp.setHeight(size.getHeight());

        addView(parent, child, -1, lp);
    }

    public static void removeAllViews(UIView parent) {
        NSArray<UIView> subviews = parent.getSubviews();
        for (UIView subview : subviews) {
            removeView(parent, subview);
        }
    }

    public static void removeView(UIView parent, UIView child) {
        removeViewInternal(parent, child);

        UIViewLayoutUtil.requestLayout(parent);
        parent.setNeedsDisplay();
    }

    public static void removeView(UIView parent, int index) {
        NSArray<UIView> subviews = parent.getSubviews();
        if(index < subviews.size()) {
            removeView(parent, subviews.get(index));
        }
    }

    private static void removeViewInternal(UIView parent, UIView child) {
        if(!isViewGroup(parent)) {
            throw new UnsupportedOperationException("Views can only be added on ViewGroup objects");
        }
        if(child.getSuperview().isEqual(parent)) {
            child.removeFromSuperview();
            onViewRemoved(parent, child);
        }
    }

    private static void onViewRemoved(UIView parent, UIView child) {
        if(parent instanceof ViewGroupDelegate) {
            ViewGroupDelegate vgd = (ViewGroupDelegate) parent;
            vgd.onViewRemoved(child);
        }
    }
}
