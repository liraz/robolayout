package org.lirazs.robolayout.core.widget.layout.relative;

import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.lirazs.robolayout.core.view.*;
import org.lirazs.robolayout.core.widget.layout.linear.LinearLayoutLayoutParams;
import org.lirazs.robolayout.core.widget.layout.linear.LinearLayoutOrientation;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.uikit.UIEdgeInsets;
import org.robovm.apple.uikit.UIView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created on 8/5/2015.
 */
public class RelativeLayout extends ViewGroup implements LayoutViewDelegate, LayoutParamsDelegate {

    private ViewContentGravity gravity;
    private String ignoreGravity;

    private List<UIView> sortedVerticalChildren;
    private List<UIView> sortedHorizontalChildren;
    private DependencyGraph graph;
    private UIView baselineView;
    private CGRect selfBounds;
    private CGRect contentBounds;

    private boolean dirtyHierarchy;
    private boolean hasBaselineAlignedChild;

    public RelativeLayout() {
        this.dirtyHierarchy = true;
        this.graph = new DependencyGraph();
    }

    public RelativeLayout(CGRect frame) {
        super(frame);

        this.dirtyHierarchy = true;
        this.graph = new DependencyGraph();
    }

    public RelativeLayout(Map<String, String> attrs) {
        super(attrs);

        this.gravity = ViewContentGravity.getFromAttribute(attrs.get("gravity"));
        this.ignoreGravity = attrs.get("ignoreGravity");

        this.dirtyHierarchy = true;
        this.graph = new DependencyGraph();
    }

    public boolean checkLayoutParams(LayoutParams layoutParams) {
        return layoutParams instanceof RelativeLayoutLayoutParams;
    }

    @Override
    public LayoutParams generateDefaultLayoutParams() {
        return new RelativeLayoutLayoutParams(LayoutParamsSize.WrapContent, LayoutParamsSize.WrapContent);
    }

    @Override
    public LayoutParams generateLayoutParams(LayoutParams params) {
        return new RelativeLayoutLayoutParams(params);
    }

    @Override
    public LayoutParams generateLayoutParams(Map<String, String> attrs) {
        return new RelativeLayoutLayoutParams(attrs);
    }

    public ViewContentGravity getGravity() {
        return gravity;
    }

    public String getIgnoreGravity() {
        return ignoreGravity;
    }

    public void sortChildren() {
        int count = getSubviews().size();

        if(sortedVerticalChildren == null || sortedVerticalChildren.size() != count) {
            if(sortedVerticalChildren == null) {
                //sortedVerticalChildren = new ArrayList<>(count);
                sortedVerticalChildren = new ArrayList<>();
            } else {
                sortedVerticalChildren.clear();
            }
            /*for (int i = 0; i < count; i++) {
                sortedVerticalChildren.add(null);
            }*/
        }

        if(sortedHorizontalChildren == null || sortedHorizontalChildren.size() != count) {
            if(sortedHorizontalChildren == null) {
                //sortedHorizontalChildren = new ArrayList<>(count);
                sortedHorizontalChildren = new ArrayList<>();
            } else {
                sortedHorizontalChildren.clear();
            }
            /*for (int i = 0; i < count; i++) {
                sortedHorizontalChildren.add(null);
            }*/
        }

        DependencyGraph graph = this.graph;
        graph.clear();

        for (UIView child : getSubviews()) {
            graph.addView(child);
        }

        graph.getSortedViews(sortedVerticalChildren, new int[] { RelativeLayoutRule.Above.getValue(),
                RelativeLayoutRule.Below.getValue(), RelativeLayoutRule.AlignBaseline.getValue(),
                RelativeLayoutRule.AlignTop.getValue(), RelativeLayoutRule.AlignBottom.getValue() });

        graph.getSortedViews(sortedHorizontalChildren, new int[] { RelativeLayoutRule.LeftOf.getValue(),
                RelativeLayoutRule.RightOf.getValue(), RelativeLayoutRule.AlignLeft.getValue(),
                RelativeLayoutRule.AlignRight.getValue() });
    }

    public UIView getRelatedViewForRules(String[] rules, RelativeLayoutRule relation) {
        String identifier = rules[relation.getValue()];
        if(identifier != null) {
            DependencyGraphNode node = this.graph.getKeyNodes().get(identifier);
            if(node == null)
                return null;

            UIView v = node.getView();

            // Find the first non-GONE view up the chain
            while(UIViewLayoutUtil.getVisibility(v) == ViewVisibility.Gone) {
                rules = ((RelativeLayoutLayoutParams)UIViewLayoutUtil.getLayoutParams(v)).getRules();
                node = this.graph.getKeyNodes().get(rules[relation.getValue()]);

                if(node == null)
                    return null;

                v = node.getView();
            }
            return v;
        }
        return null;
    }

    public RelativeLayoutLayoutParams getRelatedViewParams(String[] rules, RelativeLayoutRule relation) {
        UIView v = getRelatedViewForRules(rules, relation);
        if(v != null) {
            LayoutParams params = UIViewLayoutUtil.getLayoutParams(v);
            if(params instanceof RelativeLayoutLayoutParams) {
                return (RelativeLayoutLayoutParams) params;
            }
        }
        return null;
    }

    public void applyHorizontalSizeRules(RelativeLayoutLayoutParams childParams, double myWidth) {
        UIEdgeInsets childParamsMargin = childParams.getMargin();

        String[] rules = childParams.getRules();
        RelativeLayoutLayoutParams anchorParams;

        // -1 indicated a "soft requirement" in that direction. For example:
        // left=10, right=-1 means the view must start at 10, but can go as far as it wants to the right
        // left =-1, right=10 means the view must end at 10, but can go as far as it wants to the left
        // left=10, right=20 means the left and right ends are both fixed
        childParams.setLeft(-1);
        childParams.setRight(-1);

        UIEdgeInsets padding = UIViewLayoutUtil.getPadding(this);

        anchorParams = getRelatedViewParams(rules, RelativeLayoutRule.LeftOf);
        if(anchorParams != null) {
            childParams.setRight(anchorParams.getLeft() -
                    (anchorParams.getMargin().getLeft() + anchorParams.getMargin().getRight()));

        } else if(childParams.isAlignWithParent() && rules[RelativeLayoutRule.LeftOf.getValue()] != null) {
            if(myWidth >= 0) {
                childParams.setRight(myWidth - padding.getRight() - childParamsMargin.getRight());
            } else {
                // FIXME uh oh...
            }
        }

        anchorParams = getRelatedViewParams(rules, RelativeLayoutRule.RightOf);
        if(anchorParams != null) {
            childParams.setLeft(anchorParams.getRight() +
                    (anchorParams.getMargin().getRight() + childParamsMargin.getLeft()));

        } else if(childParams.isAlignWithParent() && rules[RelativeLayoutRule.RightOf.getValue()] != null) {
            childParams.setLeft(padding.getLeft() + childParamsMargin.getLeft());
        }

        anchorParams = getRelatedViewParams(rules, RelativeLayoutRule.AlignLeft);
        if(anchorParams != null) {
            childParams.setLeft(anchorParams.getLeft() + childParamsMargin.getLeft());

        } else if(childParams.isAlignWithParent() && rules[RelativeLayoutRule.AlignLeft.getValue()] != null) {
            childParams.setLeft(padding.getLeft() + childParamsMargin.getLeft());
        }

        anchorParams = getRelatedViewParams(rules, RelativeLayoutRule.AlignRight);
        if(anchorParams != null) {
            childParams.setRight(anchorParams.getRight() - childParamsMargin.getRight());

        } else if(childParams.isAlignWithParent() && rules[RelativeLayoutRule.AlignRight.getValue()] != null) {
            if(myWidth >= 0) {
                childParams.setRight(myWidth = padding.getRight() - childParamsMargin.getRight());
            } else {
                // FIXME uh oh...
            }
        }

        String alignParentLeft = rules[RelativeLayoutRule.AlignParentLeft.getValue()];
        if(alignParentLeft != null && Boolean.parseBoolean(alignParentLeft)) {
            childParams.setLeft(padding.getLeft() + childParamsMargin.getLeft());
        }

        String alignParentRight = rules[RelativeLayoutRule.AlignParentRight.getValue()];
        if(alignParentRight != null && Boolean.parseBoolean(alignParentRight)) {
            if(myWidth >= 0) {
                childParams.setRight(myWidth - padding.getRight() - childParamsMargin.getRight());
            } else {
                // FIXME uh oh...
            }
        }
    }

    /**
     * Get a measure spec that accounts for all of the constraints on this view.
     * This includes size contstraints imposed by the RelativeLayout as well as
     * the View's desired dimension.
     *
     * @param childStart The left or top field of the child's layout params
     * @param childEnd The right or bottom field of the child's layout params
     * @param childSize The child's desired size (the width or height field of
     *        the child's layout params)
     * @param startMargin The left or top margin
     * @param endMargin The right or bottom margin
     * @param startPadding mPaddingLeft or mPaddingTop
     * @param endPadding mPaddingRight or mPaddingBottom
     * @param mySize The width or height of this view (the RelativeLayout)
     * @return MeasureSpec for the child
     */
    public LayoutMeasureSpec getChildMeasureSpec(double childStart, double childEnd,
                                                 double childSize, double startMargin,
                                                 double endMargin, double startPadding,
                                                 double endPadding, double mySize) {

        LayoutMeasureSpecMode childSpecMode = LayoutMeasureSpecMode.Unspecified;
        double childSpecSize = 0.f;

        // Figure out start and end bounds.
        double tempStart = childStart;
        double tempEnd = childEnd;

        // If the view did not express a layout constraint for an edge, use
        // view's margins and our padding
        if (tempStart < 0) {
            tempStart = startPadding + startMargin;
        }
        if (tempEnd < 0) {
            tempEnd = mySize - endPadding - endMargin;
        }

        // Figure out maximum size available to this view
        double maxAvailable = tempEnd - tempStart;

        if (childStart >= 0 && childEnd >= 0) {
            // Constraints fixed both edges, so child must be an exact size
            childSpecMode = LayoutMeasureSpecMode.Exactly;
            childSpecSize = maxAvailable;
        } else {
            if (childSize >= 0) {
                // Child wanted an exact size. Give as much as possible
                childSpecMode = LayoutMeasureSpecMode.Exactly;

                if (maxAvailable >= 0) {
                    // We have a maxmum size in this dimension.
                    childSpecSize = Math.min(maxAvailable, childSize);
                } else {
                    // We can grow in this dimension.
                    childSpecSize = childSize;
                }
            } else if (childSize == LayoutParamsSize.MatchParent.getValue()) {
                // Child wanted to be as big as possible. Give all availble
                // space
                childSpecMode = LayoutMeasureSpecMode.Exactly;
                childSpecSize = maxAvailable;
            } else if (childSize == LayoutParamsSize.WrapContent.getValue()) {
                // Child wants to wrap content. Use AT_MOST
                // to communicate available space if we know
                // our max size
                if (maxAvailable >= 0) {
                    // We have a maxmum size in this dimension.
                    childSpecMode = LayoutMeasureSpecMode.AtMost;
                    childSpecSize = maxAvailable;
                } else {
                    // We can grow in this dimension. Child can be as big as it
                    // wants
                    childSpecMode = LayoutMeasureSpecMode.Unspecified;
                    childSpecSize = 0;
                }
            }
        }

        return new LayoutMeasureSpec(childSpecSize, childSpecMode);
    }

    public void measureChildHorizontal(UIView child, RelativeLayoutLayoutParams params, double myWidth, double myHeight) {
        UIEdgeInsets paramsMargin = params.getMargin();
        UIEdgeInsets padding = UIViewLayoutUtil.getPadding(this);

        LayoutMeasureSpec childWidthMeasureSpec = getChildMeasureSpec(params.getLeft(), params.getRight(), params.getWidth(),
                paramsMargin.getLeft(), paramsMargin.getRight(), padding.getLeft(),
                padding.getRight(), myWidth);

        LayoutMeasureSpec childHeightMeasureSpec;
        if(params.getWidth() == LayoutParamsSize.MatchParent.getValue()) {
            childHeightMeasureSpec = new LayoutMeasureSpec(myHeight - padding.getTop()- padding.getBottom(), LayoutMeasureSpecMode.Exactly);
        } else {
            childHeightMeasureSpec = new LayoutMeasureSpec(myHeight - padding.getTop()- padding.getBottom(), LayoutMeasureSpecMode.AtMost);
        }
        UIViewLayoutUtil.measure(child, childWidthMeasureSpec, childHeightMeasureSpec);
    }

    /**
     * Measure a child. The child should have left, top, right and bottom information
     * stored in its LayoutParams. If any of these values is -1 it means that the view
     * can extend up to the corresponding edge.
     *
     * @param child Child to measure
     * @param params LayoutParams associated with child
     * @param myWidth Width of the the RelativeLayout
     * @param myHeight Height of the RelativeLayout
     */
    public void measureChild(UIView child, RelativeLayoutLayoutParams params, double myWidth, double myHeight) {
        UIEdgeInsets paramsMargin = params.getMargin();
        UIEdgeInsets padding = UIViewLayoutUtil.getPadding(this);

        LayoutMeasureSpec childWidthMeasureSpec = getChildMeasureSpec(params.getLeft(), params.getRight(),
                params.getWidth(), paramsMargin.getLeft(), paramsMargin.getRight(), padding.getLeft(),
                padding.getRight(), myWidth);
        LayoutMeasureSpec childHeightMeasureSpec = getChildMeasureSpec(params.getTop(), params.getBottom(), params.getHeight(), paramsMargin.getTop(),
                paramsMargin.getBottom(), padding.getTop(), padding.getBottom(), myHeight);

        UIViewLayoutUtil.measure(child, childWidthMeasureSpec, childHeightMeasureSpec);
    }

    public void centerChildHorizontal(UIView child, RelativeLayoutLayoutParams params, double myWidth) {
        double childWidth = UIViewLayoutUtil.getMeasuredSize(child).getWidth();
        double left = (myWidth - childWidth) / 2.d;

        params.setLeft(left);
        params.setRight(left + childWidth);
    }

    public void centerChildVertical(UIView child, RelativeLayoutLayoutParams params, double myHeight) {
        double childHeight = UIViewLayoutUtil.getMeasuredSize(child).getHeight();
        double top = (myHeight - childHeight) / 2.d;

        params.setTop(top);
        params.setBottom(top + childHeight);
    }

    public boolean positionChildHorizontal(UIView child, RelativeLayoutLayoutParams params, double myWidth, boolean wrapContent) {
        CGSize childMeasuredSize = UIViewLayoutUtil.getMeasuredSize(child);
        String[] rules = params.getRules();
        UIEdgeInsets padding = UIViewLayoutUtil.getPadding(child);

        if(params.getLeft() < 0 && params.getRight() >= 0) {
            // Right is fixed, but left varies
            params.setLeft(params.getRight() - childMeasuredSize.getWidth());

        } else if (params.getLeft() >= 0 && params.getRight() < 0) {
            // Left is fixed, but right varies
            params.setRight(params.getLeft() + childMeasuredSize.getWidth());

        } else if (params.getLeft() < 0 && params.getRight() < 0) {
            // Both left and right vary
            String centerInParent = rules[RelativeLayoutRule.CenterInParent.getValue()];
            String centerHorizontal = rules[RelativeLayoutRule.CenterHorizontal.getValue()];

            if ((centerInParent != null && Boolean.parseBoolean(centerInParent)) ||
                    (centerHorizontal != null && Boolean.parseBoolean(centerHorizontal))) {
                if (!wrapContent) {
                    centerChildHorizontal(child, params, myWidth);
                } else {
                    params.setLeft(padding.getLeft() + params.getMargin().getLeft());
                    params.setRight(params.getLeft() + childMeasuredSize.getWidth());
                }
                return true;
            } else {
                params.setLeft(padding.getLeft() + params.getMargin().getLeft());
                params.setRight(params.getLeft() + childMeasuredSize.getWidth());
            }
        }
        String alignParentRight = rules[RelativeLayoutRule.AlignParentRight.getValue()];
        return  (alignParentRight != null && Boolean.parseBoolean(alignParentRight));
    }

    public boolean positionChildVertical(UIView child, RelativeLayoutLayoutParams params, double myHeight, boolean wrapContent) {
        CGSize childMeasuredSize = UIViewLayoutUtil.getMeasuredSize(child);
        String[] rules = params.getRules();
        UIEdgeInsets padding = UIViewLayoutUtil.getPadding(child);

        if (params.getTop() < 0 && params.getBottom() >= 0) {
            // Bottom is fixed, but top varies
            params.setTop(params.getBottom() - childMeasuredSize.getHeight());
        } else if (params.getTop() >= 0 && params.getBottom() < 0) {
            // Top is fixed, but bottom varies
            params.setBottom(params.getTop() + childMeasuredSize.getHeight());
        } else if (params.getTop() < 0 && params.getBottom() < 0) {
            // Both top and bottom vary
            String centerInParent = rules[RelativeLayoutRule.CenterInParent.getValue()];
            String centerVertical = rules[RelativeLayoutRule.CenterVertical.getValue()];
            if ((centerInParent != null && Boolean.parseBoolean(centerInParent)) || (centerVertical != null && Boolean.parseBoolean(centerVertical))) {
                if (!wrapContent) {
                    centerChildVertical(child, params, myHeight);
                } else {
                    params.setTop(padding.getTop() + params.getMargin().getTop());
                    params.setBottom(params.getTop() + childMeasuredSize.getHeight());
                }
                return true;
            } else {
                params.setTop(padding.getTop() + params.getMargin().getTop());
                params.setBottom(params.getTop() + childMeasuredSize.getHeight());
            }
        }
        String alignParentBottom = rules[RelativeLayoutRule.AlignParentBottom.getValue()];
        return  (alignParentBottom != null && Boolean.parseBoolean(alignParentBottom));
    }

    public void applyVerticalSizeRules(RelativeLayoutLayoutParams childParams, double myHeight) {
        String[] rules = childParams.getRules();
        RelativeLayoutLayoutParams anchorParams;

        UIEdgeInsets childParamsMargin = childParams.getMargin();
        UIEdgeInsets padding = UIViewLayoutUtil.getPadding(this);

        childParams.setTop(-1);
        childParams.setBottom(-1);

        anchorParams = getRelatedViewParams(rules, RelativeLayoutRule.Above);
        if(anchorParams != null) {
            childParams.setBottom(anchorParams.getTop() - (anchorParams.getMargin().getTop() + childParamsMargin.getBottom()));

        } else if(childParams.isAlignWithParent() && rules[RelativeLayoutRule.Above.getValue()] != null) {
            if (myHeight >= 0) {
                childParams.setBottom(myHeight - padding.getBottom() - childParamsMargin.getBottom());
            } else {
                // FIXME uh oh...
            }
        }

        anchorParams = getRelatedViewParams(rules, RelativeLayoutRule.Below);
        if (anchorParams != null) {
            childParams.setTop(anchorParams.getBottom() + (anchorParams.getMargin().getBottom() +
                    childParamsMargin.getTop()));
        } else if (childParams.isAlignWithParent() && rules[RelativeLayoutRule.Below.getValue()] != null) {
            childParams.setTop(padding.getTop() + childParamsMargin.getTop());
        }

        anchorParams = getRelatedViewParams(rules, RelativeLayoutRule.AlignTop);
        if (anchorParams != null) {
            childParams.setTop(anchorParams.getTop() + childParamsMargin.getTop());
        } else if (childParams.isAlignWithParent() && rules[RelativeLayoutRule.AlignTop.getValue()] != null) {
            childParams.setTop(padding.getTop() + childParamsMargin.getTop());
        }

        anchorParams = getRelatedViewParams(rules, RelativeLayoutRule.AlignBottom);
        if (anchorParams != null) {
            childParams.setBottom(anchorParams.getBottom() - childParamsMargin.getBottom());
        } else if (childParams.isAlignWithParent() && rules[RelativeLayoutRule.AlignBottom.getValue()] != null) {
            if (myHeight >= 0) {
                childParams.setBottom(myHeight - padding.getBottom() - childParamsMargin.getBottom());
            } else {
                // FIXME uh oh...
            }
        }

        String alignParentTop = rules[RelativeLayoutRule.AlignParentTop.getValue()];
        if (null != alignParentTop && Boolean.parseBoolean(alignParentTop)) {
            childParams.setTop(padding.getTop() + childParamsMargin.getTop());
        }

        String alignParentBottom = rules[RelativeLayoutRule.AlignParentBottom.getValue()];
        if (null != alignParentBottom && Boolean.parseBoolean(alignParentBottom)) {
            if (myHeight >= 0) {
                childParams.setBottom(myHeight - padding.getBottom() - childParamsMargin.getBottom());
            } else {
                // FIXME uh oh...
            }
        }

        String alignBaseline = rules[RelativeLayoutRule.AlignBaseline.getValue()];
        if (alignBaseline != null && Boolean.parseBoolean(alignBaseline)) {
            this.hasBaselineAlignedChild = true;
        }
    }

    public double getRelatedViewBaseline(String[] rules, RelativeLayoutRule relation) {
        UIView v = getRelatedViewForRules(rules, relation);
        if(v != null) {
            return UIViewLayoutUtil.getBaseLine(v);
        }
        return -1;
    }

    public void alignChild(UIView child, RelativeLayoutLayoutParams params) {
        String[] rules = params.getRules();
        double anchorBaseline = getRelatedViewBaseline(rules, RelativeLayoutRule.AlignBaseline);

        if(anchorBaseline != -1) {
            RelativeLayoutLayoutParams anchorParams = getRelatedViewParams(rules, RelativeLayoutRule.AlignBaseline);
            if (anchorParams != null) {
                double offset = anchorParams.getTop() + anchorBaseline;
                double baseline = UIViewLayoutUtil.getBaseLine(child);
                if (baseline != -1) {
                    offset -= baseline;
                }
                double height = params.getBottom() - params.getTop();
                params.setTop(offset);
                params.setBottom(params.getTop() + height);
            }
        }

        if(this.baselineView == null) {
            this.baselineView = child;
        } else {
            RelativeLayoutLayoutParams lp = (RelativeLayoutLayoutParams) UIViewLayoutUtil.getLayoutParams(this.baselineView);
            if (params.getTop() < lp.getTop() || (params.getTop() == lp.getTop() && params.getLeft() < lp.getLeft())) {
                this.baselineView = child;
            }
        }
    }

    @Override
    public void onMeasure(LayoutMeasureSpec widthMeasureSpec, LayoutMeasureSpec heightMeasureSpec) {
        if (this.dirtyHierarchy) {
            this.dirtyHierarchy = false;
            sortChildren();
        }

        double myWidth = -1;
        double myHeight = -1;

        double width = 0;
        double height = 0;

        LayoutMeasureSpecMode widthMode = widthMeasureSpec.getMode();
        LayoutMeasureSpecMode heightMode = heightMeasureSpec.getMode();
        double widthSize = widthMeasureSpec.getSize();
        double heightSize = heightMeasureSpec.getSize();

        // Record our dimensions if they are known;
        if (widthMode != LayoutMeasureSpecMode.Unspecified) {
            myWidth = widthSize;
        }

        if (heightMode != LayoutMeasureSpecMode.Unspecified) {
            myHeight = heightSize;
        }

        if (widthMode == LayoutMeasureSpecMode.Exactly) {
            width = myWidth;
        }

        if (heightMode == LayoutMeasureSpecMode.Exactly) {
            height = myHeight;
        }

        this.hasBaselineAlignedChild = false;

        UIView ignore = null;
        ViewContentGravity gravity = ViewContentGravity.create(this.gravity.value() & ViewContentGravity.RELATIVE_HORIZONTAL_GRAVITY_MASK);
        boolean horizontalGravity = gravity != ViewContentGravity.Left && gravity.value() != 0;

        gravity = ViewContentGravity.create(this.gravity.value() & ViewContentGravity.VERTICAL_GRAVITY_MASK);
        boolean verticalGravity = gravity != ViewContentGravity.Top && gravity.value() != 0;

        double left = Double.MAX_VALUE;
        double top = Double.MAX_VALUE;
        double right = Double.MIN_VALUE;
        double bottom = Double.MIN_VALUE;

        boolean offsetHorizontalAxis = false;
        boolean offsetVerticalAxis = false;

        if ((horizontalGravity || verticalGravity) && this.ignoreGravity != null) {
            ignore = UIViewLayoutUtil.findViewById(this, this.ignoreGravity);
        }

        boolean isWrapContentWidth = widthMode != LayoutMeasureSpecMode.Exactly;
        boolean isWrapContentHeight = heightMode != LayoutMeasureSpecMode.Exactly;

        List<UIView> views = this.sortedHorizontalChildren;
        int count = views.size();

        for (int i = 0; i < count; i++) {
            UIView child = views.get(i);
            if (UIViewLayoutUtil.getVisibility(child) != ViewVisibility.Gone) {
                RelativeLayoutLayoutParams params = (RelativeLayoutLayoutParams )UIViewLayoutUtil.getLayoutParams(child);

                applyHorizontalSizeRules(params, myWidth);
                measureChildHorizontal(child, params, myWidth, myHeight);

                if (positionChildHorizontal(child, params, myWidth, isWrapContentWidth)) {
                    offsetHorizontalAxis = true;
                }
            }
        }

        views = this.sortedVerticalChildren;
        count = views.size();

        for (int i = 0; i < count; i++) {
            UIView child = views.get(i);

            if (UIViewLayoutUtil.getVisibility(child) != ViewVisibility.Gone) {
                RelativeLayoutLayoutParams params = (RelativeLayoutLayoutParams )UIViewLayoutUtil.getLayoutParams(child);
                UIEdgeInsets paramsMargin = params.getMargin();

                applyVerticalSizeRules(params, myHeight);
                measureChild(child, params, myWidth, myHeight);

                if (positionChildVertical(child, params, myHeight, isWrapContentHeight)) {
                    offsetVerticalAxis = true;
                }

                if (isWrapContentWidth) {
                    width = Math.max(width, params.getRight());
                }

                if (isWrapContentHeight) {
                    height = Math.max(height, params.getBottom());
                }

                if (child != ignore || verticalGravity) {
                    left = Math.min(left, params.getLeft() - paramsMargin.getLeft());
                    top = Math.min(top, params.getTop() - paramsMargin.getTop());
                }

                if (child != ignore || horizontalGravity) {
                    right = Math.max(right, params.getRight() + paramsMargin.getRight());
                    bottom = Math.max(bottom, params.getBottom() + paramsMargin.getBottom());
                }
            }
        }

        if (this.hasBaselineAlignedChild) {
            for (int i = 0; i < count; i++) {
                UIView child = getSubviews().get(i);

                if (UIViewLayoutUtil.getVisibility(child) != ViewVisibility.Gone) {
                    RelativeLayoutLayoutParams params = (RelativeLayoutLayoutParams )UIViewLayoutUtil.getLayoutParams(child);
                    alignChild(child, params);

                    UIEdgeInsets paramsMargin = params.getMargin();
                    if (child != ignore || verticalGravity) {
                        left = Math.min(left, params.getLeft() - paramsMargin.getLeft());
                        top = Math.min(top, params.getTop() - paramsMargin.getTop());
                    }

                    if (child != ignore || horizontalGravity) {
                        right = Math.max(right, params.getRight() + paramsMargin.getRight());
                        bottom = Math.max(bottom, params.getBottom() + paramsMargin.getBottom());
                    }
                }
            }
        }

        UIEdgeInsets padding = UIViewLayoutUtil.getPadding(this);
        CGSize minSize = UIViewLayoutUtil.getMinSize(this);
        if (isWrapContentWidth) {
            // Width already has left padding in it since it was calculated by looking at
            // the right of each child view
            width += padding.getRight();

            if (UIViewLayoutUtil.getLayoutParams(this).getWidth() >= 0) {
                width = Math.max(width, UIViewLayoutUtil.getLayoutParams(this).getWidth());
            }

            width = Math.max(width, minSize.getWidth());
            width = LayoutMeasuredDimension.resolveSize(width, widthMeasureSpec);

            if (offsetHorizontalAxis) {
                for (int i = 0; i < count; i++) {
                    UIView child = getSubviews().get(i);

                    if (UIViewLayoutUtil.getVisibility(child) != ViewVisibility.Gone) {
                        RelativeLayoutLayoutParams params = (RelativeLayoutLayoutParams )UIViewLayoutUtil.getLayoutParams(child);
                        String[] rules = params.getRules();

                        String centerInParent = rules[RelativeLayoutRule.CenterInParent.getValue()];
                        String centerHorizontal = rules[RelativeLayoutRule.CenterHorizontal.getValue()];
                        String alignParentRight = rules[RelativeLayoutRule.AlignParentRight.getValue()];

                        if ((centerInParent != null && Boolean.parseBoolean(centerInParent)) || (centerHorizontal != null && Boolean.parseBoolean(centerHorizontal))) {
                            centerChildHorizontal(child, params, width);
                        } else if (alignParentRight != null && Boolean.parseBoolean(alignParentRight)) {
                            CGSize childMeasureSize = UIViewLayoutUtil.getMeasuredSize(child);

                            double childWidth = childMeasureSize.getWidth();
                            params.setLeft(width - padding.getRight() - childWidth);
                            params.setRight(params.getLeft() + childWidth);
                        }
                    }
                }
            }
        }

        if (isWrapContentHeight) {
            // Height already has top padding in it since it was calculated by looking at
            // the bottom of each child view
            height += padding.getBottom();

            if (UIViewLayoutUtil.getLayoutParams(this).getHeight() >= 0) {
                height = Math.max(height, UIViewLayoutUtil.getLayoutParams(this).getHeight());
            }

            height = Math.max(height, minSize.getHeight());
            height = LayoutMeasuredDimension.resolveSize(height, heightMeasureSpec);

            if (offsetVerticalAxis) {
                for (int i = 0; i < count; i++) {
                    UIView child = getSubviews().get(i);

                    if (UIViewLayoutUtil.getVisibility(child) != ViewVisibility.Gone) {
                        RelativeLayoutLayoutParams params = (RelativeLayoutLayoutParams )UIViewLayoutUtil.getLayoutParams(child);
                        String[] rules = params.getRules();
                        String centerInParent = rules[RelativeLayoutRule.CenterInParent.getValue()];
                        String centerVertical = rules[RelativeLayoutRule.CenterVertical.getValue()];
                        String alignParentBottom = rules[RelativeLayoutRule.AlignParentBottom.getValue()];
                        if ((centerInParent != null && Boolean.parseBoolean(centerInParent)) || (centerVertical != null && Boolean.parseBoolean(centerVertical))) {
                            centerChildVertical(child, params, height);
                        } else if (alignParentBottom != null && Boolean.parseBoolean(alignParentBottom)) {
                            CGSize childMeasureSize = UIViewLayoutUtil.getMeasuredSize(child);

                            double childHeight = childMeasureSize.getHeight();
                            params.setTop(height - padding.getBottom() - childHeight);
                            params.setBottom(params.getTop() + childHeight);
                        }
                    }
                }
            }
        }

        if (horizontalGravity || verticalGravity) {
            this.selfBounds = new CGRect(padding.getLeft(), padding.getTop(), width, height);
            Gravity.applyGravity(this.gravity, right - left, bottom - top, this.selfBounds, this.contentBounds);

            double horizontalOffset = this.contentBounds.getOrigin().getX() - left;
            double verticalOffset = this.contentBounds.getOrigin().getY() - top;
            if (horizontalOffset != 0 || verticalOffset != 0) {
                for (int i = 0; i < count; i++) {
                    UIView child = getSubviews().get(i);

                    if (UIViewLayoutUtil.getVisibility(child) != ViewVisibility.Gone && child != ignore) {
                        RelativeLayoutLayoutParams params = (RelativeLayoutLayoutParams )UIViewLayoutUtil.getLayoutParams(child);
                        if (horizontalGravity) {
                            params.setLeft(params.getLeft() + horizontalOffset);
                            params.setRight(params.getRight() + horizontalOffset);
                        }
                        if (verticalGravity) {
                            params.setTop(params.getTop() + verticalOffset);
                            params.setBottom(params.getBottom() + verticalOffset);
                        }
                    }
                }
            }
        }
        LayoutMeasuredSize measuredSize = new LayoutMeasuredSize();
        measuredSize.getWidth().setState(LayoutMeasuredState.None);
        measuredSize.getWidth().setSize(width);
        measuredSize.getHeight().setState(LayoutMeasuredState.None);
        measuredSize.getHeight().setSize(height);

        UIViewLayoutUtil.setMeasuredDimensionSize(this, measuredSize);
    }

    @Override
    public void onLayout(CGRect frame, boolean changed) {
        //  The layout has actually already been performed and the positions
        //  cached.  Apply the cached values to the children.
        for (UIView child : getSubviews()) {
            if (UIViewLayoutUtil.getVisibility(child) != ViewVisibility.Gone) {
                RelativeLayoutLayoutParams st = (RelativeLayoutLayoutParams )UIViewLayoutUtil.getLayoutParams(child);
                UIViewLayoutUtil.layout(child, new CGRect(st.getLeft(), st.getTop(), st.getRight() - st.getLeft(), st.getBottom() - st.getTop()));
            }
        }
    }
}
