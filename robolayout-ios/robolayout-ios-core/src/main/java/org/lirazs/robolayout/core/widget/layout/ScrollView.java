package org.lirazs.robolayout.core.widget.layout;

import org.lirazs.robolayout.core.resource.drawable.Drawable;
import org.lirazs.robolayout.core.resource.drawable.DrawableDelegate;
import org.lirazs.robolayout.core.util.*;
import org.lirazs.robolayout.core.view.*;
import org.lirazs.robolayout.core.widget.layout.frame.FrameLayoutLayoutParams;
import org.lirazs.robolayout.core.widget.layout.linear.LinearLayoutLayoutParams;
import org.lirazs.robolayout.core.widget.layout.linear.LinearLayoutOrientation;
import org.robovm.apple.coregraphics.CGContext;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSKeyValueChangeInfo;
import org.robovm.apple.foundation.NSKeyValueObservingOptions;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created on 8/5/2015.
 */
public class ScrollView extends UIScrollView implements LayoutViewDelegate,
        LayoutParamsDelegate, DrawableBackgroundDelegate, ViewGroupDelegate, DrawableDelegate {

    public static ViewContentGravity DEFAULT_CHILD_GRAVITY = ViewContentGravity.Top.set(ViewContentGravity.Left);

    private List<UIView> matchParentChildren;

    public ScrollView(Map<String, String> attrs) {
        UIViewLayoutUtil.applyAttributes(this, attrs);

        matchParentChildren = new ArrayList<>();
    }

    public List<UIView> getMatchParentChildren() {
        return matchParentChildren;
    }

    public void setMatchParentChildren(List<UIView> matchParentChildren) {
        this.matchParentChildren = matchParentChildren;
    }

    @Override
    public void onMeasure(LayoutMeasureSpec widthMeasureSpec, LayoutMeasureSpec heightMeasureSpec) {
        int count = Math.min(1, getSubviews().size());

        boolean measureMatchParentChildren = widthMeasureSpec.getMode() != LayoutMeasureSpecMode.Exactly || heightMeasureSpec.getMode() != LayoutMeasureSpecMode.Exactly;
        matchParentChildren.clear();

        double maxHeight = 0;
        double maxWidth = 0;

        UIEdgeInsets padding = UIViewLayoutUtil.getPadding(this);

        LayoutMeasuredWidthHeightState childState = new LayoutMeasuredWidthHeightState();
        childState.setHeightState(LayoutMeasuredState.None);
        childState.setWidthState(LayoutMeasuredState.None);

        for (int i = 0; i < count; i++) {
            UIView child = getSubviews().get(i);
            if (child.getClass().getSimpleName().equals("UIWebDocumentView")) {
                continue;
            }

            if (UIViewLayoutUtil.getVisibility(child) != ViewVisibility.Gone) {
                CGSize childMeasuredSize = UIViewLayoutUtil.getMeasuredSize(child);
                UIViewViewGroupUtil.measureChild(this, child, widthMeasureSpec, 0, heightMeasureSpec, 0);

                FrameLayoutLayoutParams lp = (FrameLayoutLayoutParams)UIViewLayoutUtil.getLayoutParams(child);
                UIEdgeInsets lpMargin = lp.getMargin();
                maxWidth = Math.max(maxWidth, childMeasuredSize.getWidth() + lpMargin.getLeft() + lpMargin.getRight());
                maxHeight = Math.max(maxHeight, childMeasuredSize.getHeight() + lpMargin.getTop() + lpMargin.getBottom());

                LayoutMeasuredWidthHeightState.combineMeasuredStates(childState, UIViewLayoutUtil.getMeasuredState(child));

                if (measureMatchParentChildren) {
                    if (lp.getWidth() == LayoutParamsSize.MatchParent.getValue() || lp.getHeight() == LayoutParamsSize.MatchParent.getValue()) {
                        matchParentChildren.add(child);
                    }
                }
            }
        }

        // Account for padding too
        maxWidth += padding.getLeft() + padding.getRight();
        maxHeight += padding.getTop() + padding.getBottom();

        // Check against our minimum height and width
        CGSize minSize = UIViewLayoutUtil.getMinSize(this);
        maxHeight = Math.max(maxHeight, minSize.getHeight());
        maxWidth = Math.max(maxWidth, minSize.getWidth());

        // Check against our foreground's minimum height and width
        LayoutMeasuredDimension width = LayoutMeasuredDimension.resolveSizeAndState(maxWidth, widthMeasureSpec, childState.getWidthState());
        LayoutMeasuredDimension height = LayoutMeasuredDimension.resolveSizeAndState(maxHeight, heightMeasureSpec, childState.getHeightState());

        LayoutMeasuredSize measuredSize = new LayoutMeasuredSize(width, height);
        UIViewLayoutUtil.setMeasuredDimensionSize(this, measuredSize);

        count = matchParentChildren.size();
        if (count > 1) {
            for (int i = 0; i < count; i++) {
                UIView child = matchParentChildren.get(i);

                if (child.getClass().getSimpleName().equals("UIWebDocumentView")) {
                    continue;
                }

                MarginLayoutParams lp = (MarginLayoutParams)UIViewLayoutUtil.getLayoutParams(child);
                UIEdgeInsets lpMargin = lp.getMargin();
                LayoutMeasureSpec childWidthMeasureSpec = new LayoutMeasureSpec();
                LayoutMeasureSpec childHeightMeasureSpec = new LayoutMeasureSpec();

                if (lp.getWidth() == LayoutParamsSize.MatchParent.getValue()) {
                    childWidthMeasureSpec.setSize(UIViewLayoutUtil.getMeasuredSize(this).getWidth() - padding.getLeft() - padding.getRight() - lpMargin.getLeft() - lpMargin.getRight());
                    childWidthMeasureSpec.setMode(LayoutMeasureSpecMode.Exactly);
                } else {
                    childWidthMeasureSpec = UIViewViewGroupUtil.getChildMeasureSpec(widthMeasureSpec, (padding.getLeft() + padding.getRight() + lpMargin.getLeft() + lpMargin.getRight()), lp.getWidth());
                }

                if (lp.getHeight() == LayoutParamsSize.MatchParent.getValue()) {
                    childHeightMeasureSpec.setSize(UIViewLayoutUtil.getMeasuredSize(this).getHeight() - padding.getTop() - padding.getBottom() - lpMargin.getTop() - lpMargin.getBottom());
                    childHeightMeasureSpec.setMode(LayoutMeasureSpecMode.Exactly);
                } else {
                    childHeightMeasureSpec = UIViewViewGroupUtil.getChildMeasureSpec(heightMeasureSpec, (padding.getTop() + padding.getBottom() + lpMargin.getTop() + lpMargin.getBottom()), lp.getHeight());
                }
                UIViewLayoutUtil.measure(child, childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }

        LayoutMeasureSpecMode heightMode = heightMeasureSpec.getMode();
        if (heightMode == LayoutMeasureSpecMode.Unspecified) {
            return;
        }

        /*if ([self.subviews count] > 0) {
             UIView *child = [self.subviews objectAtIndex:0];
             CGFloat height = self.measuredSize.getHeight();
             CGSize childMeasuredSize = child.measuredSize;

             if (child.measuredSize.getHeight() < height) {
                 FrameLayoutLayoutParams *lp = (FrameLayoutLayoutParams *) child.layoutParams;

                 IDLLayoutMeasureSpec childWidthMeasureSpec = [self childMeasureSpecWithMeasureSpec:widthMeasureSpec padding:(padding.left + padding.right) childDimension:lp.getWidth()];
                 height -= padding.top;
                 height -= padding.bottom;
                 IDLLayoutMeasureSpec childHeightMeasureSpec = IDLLayoutMeasureSpecMake(height, IDLLayoutMeasureSpecModeExactly);

                 [child measureWithWidthMeasureSpec:childWidthMeasureSpec heightMeasureSpec:childHeightMeasureSpec];
             }
         }*/
    }


    @Override
    public void onLayout(CGRect frame, boolean changed) {
        int count = Math.min(1, getSubviews().size());

        UIEdgeInsets padding = UIViewLayoutUtil.getPadding(this);
        double parentLeft = padding.getLeft();
        double parentRight = frame.getSize().getWidth() - padding.getRight();

        double parentTop = padding.getTop();
        double parentBottom = frame.getSize().getHeight() - padding.getBottom();
        double maxX = 0;
        double maxY = 0;

        for (int i = 0; i < count; i++) {
            UIView child = getSubviews().get(i);

            if (UIViewLayoutUtil.getVisibility(child) != ViewVisibility.Gone && !child.getClass().getSimpleName().equals("UIWebDocumentView")) {
                FrameLayoutLayoutParams lp = (FrameLayoutLayoutParams) UIViewLayoutUtil.getLayoutParams(child);
                UIEdgeInsets lpMargin = lp.getMargin();

                CGSize childMeasureSize = UIViewLayoutUtil.getMeasuredSize(child);
                double width = childMeasureSize.getWidth();
                double height = childMeasureSize.getHeight();

                double childLeft;
                double childTop;

                ViewContentGravity gravity = lp.getGravity();
                if (gravity.value() == -1) {
                    gravity = DEFAULT_CHILD_GRAVITY;
                }

                ViewContentGravity verticalGravity = ViewContentGravity.create(gravity.value() & ViewContentGravity.VERTICAL_GRAVITY_MASK);
                ViewContentGravity horizontalGravity = ViewContentGravity.create(gravity.value() & ViewContentGravity.HORIZONTAL_GRAVITY_MASK);

                if(horizontalGravity.equals(ViewContentGravity.Left)) {
                    childLeft = parentLeft + lpMargin.getLeft();
                } else if(horizontalGravity.equals(ViewContentGravity.CenterHorizontal)) {
                    childLeft = parentLeft + (parentRight - parentLeft - width) / 2 + lpMargin.getLeft() - lpMargin.getRight();
                } else if(horizontalGravity.equals(ViewContentGravity.Right)) {
                    childLeft = parentRight - width - lpMargin.getRight();
                } else {
                    childLeft = parentLeft + lpMargin.getLeft();
                }

                if(verticalGravity.equals(ViewContentGravity.Top)) {
                    childTop = parentTop + lpMargin.getTop();
                } else if(verticalGravity.equals(ViewContentGravity.CenterVertical)) {
                    childTop = parentTop + (parentBottom - parentTop - height) / 2 + lpMargin.getTop() - lpMargin.getBottom();
                } else if(verticalGravity.equals(ViewContentGravity.Bottom)) {
                    childTop = parentBottom - height - lpMargin.getBottom();
                } else {
                    childTop = parentTop + lpMargin.getTop();
                }

                UIViewLayoutUtil.layout(child, new CGRect(childLeft, childTop, width, height));

                maxX = Math.max(maxX, childLeft + width);
                maxY = Math.max(maxY, childTop + height);
            }
        }
        setContentSize(new CGSize(maxX + padding.getRight(), maxY + padding.getBottom()));
    }

    @Override
    public boolean isViewGroup() {
        return true;
    }

    @Override
    public void onViewRemoved(UIView view) {

    }

    public boolean checkLayoutParams(LayoutParams layoutParams) {
        return layoutParams != null;
    }

    public LayoutParams generateDefaultLayoutParams() {
        return new FrameLayoutLayoutParams(LayoutParamsSize.MatchParent, LayoutParamsSize.MatchParent);
    }

    public LayoutParams generateLayoutParams(Map<String, String> attrs) {
        return new FrameLayoutLayoutParams(attrs);
    }

    @Override
    public LayoutParams generateLayoutParams(LayoutParams params) {
        return new FrameLayoutLayoutParams(params);
    }



    public void onBackgroundDrawableChanged() {
        final String backgroundDrawableFrameTag = "backgroundDrawableFrame";
        final Drawable drawable = UIViewDrawableUtil.getBackgroundDrawable(this);

        if(drawable != null) {
            drawable.setDelegate(null);
            drawable.setDelegate(new DrawableDelegate() {
                @Override
                public void drawableDidInvalidate(Drawable drawable) {
                    ScrollView.this.setNeedsDisplay();
                }
            });
            drawable.setState(UIControlState.Normal);
            this.setBackgroundColor(UIColor.clear());

            if(!NSKeyValueObserverUtil.hasObserver(this, backgroundDrawableFrameTag)) {
                NSKeyValueObserverUtil.addObserver(this, new org.lirazs.robolayout.core.util.NSKeyValueObserver() {
                    @Override
                    public void observeValue(String s, NSObject nsObject, NSKeyValueChangeInfo nsKeyValueChangeInfo) {
                        drawable.setBounds(ScrollView.this.getBounds());
                        ScrollView.this.setNeedsDisplay();
                    }
                }, backgroundDrawableFrameTag, Collections.singletonList("frame"), NSKeyValueObservingOptions.New);
            }
        } else {
            NSKeyValueObserverUtil.removeObserver(this, backgroundDrawableFrameTag);
        }
        this.setNeedsDisplay();
    }


    @Override
    public void draw(CGRect cgRect) {
        Drawable drawable = UIViewDrawableUtil.getBackgroundDrawable(this);
        if(drawable != null) {
            CGContext context = UIGraphics.getCurrentContext();
            context.saveGState();

            drawable.setBounds(getBounds());
            drawable.drawInContext(context);

            context.restoreGState();
        } else {
            if(isOpaque()) {
                UIColor color = getBackgroundColor();
                if(color == null) {
                    color = UIColor.white();
                }
                CGContext context = UIGraphics.getCurrentContext();
                context.setFillColor(color.getCGColor());
                context.fillRect(getBounds());
            }
        }

        super.draw(cgRect);
    }

    @Override
    public void drawableDidInvalidate(Drawable drawable) {
        setNeedsDisplay();
    }
}
