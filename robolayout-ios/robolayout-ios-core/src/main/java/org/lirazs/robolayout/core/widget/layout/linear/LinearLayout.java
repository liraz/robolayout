package org.lirazs.robolayout.core.widget.layout.linear;

import org.lirazs.robolayout.core.util.ResourceAttributesUtil;
import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.lirazs.robolayout.core.util.UIViewViewGroupUtil;
import org.lirazs.robolayout.core.view.*;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.*;

import java.util.Map;

/**
 * Created on 8/5/2015.
 */
public class LinearLayout extends ViewGroup implements LayoutViewDelegate, LayoutParamsDelegate {

    public static int MAX_ASCENT_DESCENT_INDEX_CENTER_VERTICAL = 0;
    public static int MAX_ASCENT_DESCENT_INDEX_TOP = 1;
    public static int MAX_ASCENT_DESCENT_INDEX_BOTTOM = 2;
    public static int MAX_ASCENT_DESCENT_INDEX_FILL = 3;
    public static int VERTICAL_GRAVITY_COUNT = 4;
    
    private LinearLayoutOrientation orientation;
    private ViewContentGravity gravity;
    private double weightSum;

    private double totalLength;

    /**
     * Whether the children of this layout are baseline aligned.  Only applicable
     * if _orientation is horizontal.
     */
    boolean baselineAligned;
    int[] maxAscent = new int[VERTICAL_GRAVITY_COUNT];
    int[] maxDescent = new int[VERTICAL_GRAVITY_COUNT];
    int baselineAlignedChildIndex;
    double baselineChildTop;
    boolean useLargestChild;

    public LinearLayout(Map<String, String> attrs) {
        super(attrs);

        this.gravity = ViewContentGravity.getFromAttribute(attrs.get("gravity"));

        String orientationString = attrs.get("orientation");
        if(orientationString != null && orientationString.equals("horizontal")) {
            this.orientation = LinearLayoutOrientation.Horizontal;
        } else {
            this.orientation = LinearLayoutOrientation.Vertical;
        }

        this.baselineAligned = true;
        this.baselineAlignedChildIndex = -1;
        this.baselineChildTop = 0;
    }

    public LinearLayout(CGRect frame) {
        super(frame);

        this.gravity = ViewContentGravity.Left.set(ViewContentGravity.Top);
        this.orientation = LinearLayoutOrientation.Vertical;
        this.baselineAligned = true;
        this.baselineAlignedChildIndex = -1;
        this.baselineChildTop = 0;
    }

    public LinearLayoutOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(LinearLayoutOrientation orientation) {
        if (this.orientation != orientation) {
            this.orientation = orientation;

            UIViewLayoutUtil.requestLayout(this);
        }
    }

    public ViewContentGravity getGravity() {
        return gravity;
    }

    public void setGravity(ViewContentGravity gravity) {
        if(this.gravity.value() != gravity.value()) {
            if((gravity.value() & ViewContentGravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
                gravity.set(ViewContentGravity.Left);
            }

            if((gravity.value() & ViewContentGravity.VERTICAL_GRAVITY_MASK) == 0) {
                gravity.set(ViewContentGravity.Top);
            }

            this.gravity = gravity;
            UIViewLayoutUtil.requestLayout(this);
        }
    }

    public double getWeightSum() {
        return weightSum;
    }

    public void setWeightSum(double weightSum) {
        this.weightSum = Math.max(0.d, weightSum);
    }

    public double getBaseline() {
        if(baselineAlignedChildIndex < 0) {
            return UIViewLayoutUtil.getBaseLine(this);
        }

        if(getSubviews().size() <= baselineAlignedChildIndex) {
            throw new IllegalStateException("mBaselineAlignedChildIndex of LinearLayout set to an index that is out of bounds.");
        }

        UIView child = getSubviews().get(baselineAlignedChildIndex);
        double childBaseline = UIViewLayoutUtil.getBaseLine(child);

        if(childBaseline == -1) {
            if(baselineAlignedChildIndex == 0) {
                // this is just the default case, safe to return -1
                return -1;
            }
            // the user picked an index that points to something that doesn't
            // know how to calculate its baseline.
            throw new IllegalStateException("mBaselineAlignedChildIndex of LinearLayout points to a View that doesn't know how to get its baseline.");
        }

        // TODO: This should try to take into account the virtual offsets
        // (See getNextLocationOffset and getLocationOffset)
        // We should add to childTop:
        // sum([getNextLocationOffset(getChildAt(i)) / i < mBaselineAlignedChildIndex])
        // and also add:
        // getLocationOffset(child)
        double childTop = baselineChildTop;

        if(orientation == LinearLayoutOrientation.Vertical) {
            ViewContentGravity majorGravity = new ViewContentGravity(gravity.value() & ViewContentGravity.VERTICAL_GRAVITY_MASK);

            if(!majorGravity.equals(ViewContentGravity.Top)) {
                UIEdgeInsets padding = UIViewLayoutUtil.getPadding(this);

                if(majorGravity.equals(ViewContentGravity.Bottom)) {
                    childTop = getFrame().getSize().getHeight() - padding.getBottom() - totalLength;

                } else if(majorGravity.equals(ViewContentGravity.CenterVertical)) {
                    childTop += ((getFrame().getSize().getHeight() - padding.getTop() - padding.getBottom()) - totalLength) / 2;
                }
            }
        }

        LinearLayoutLayoutParams lp = (LinearLayoutLayoutParams) UIViewLayoutUtil.getLayoutParams(child);
        return childTop + lp.getMargin().getTop() + childBaseline;
    }

    /**
     * <p>Return the size offset of the next sibling of the specified child.
     * This can be used by subclasses to change the location of the widget
     * following <code>child</code>.</p>
     *
     * @param child the child whose next sibling will be moved
     * @return the location offset of the next child in pixels
     */
    public double getNextLocationOffsetOfChild(UIView child) {
        return 0;
    }

    /**
     * <p>Returns the number of children to skip after measuring/laying out
     * the specified child.</p>
     *
     * @param child the child after which we want to skip children
     * @param index the index of the child after which we want to skip children
     * @return the number of children to skip, 0 by default
     */
    public int getChildrenSkipCountAfterChild(UIView child, int index) {
        return 0;
    }

    private void forceUniformWidth(int count, LayoutMeasureSpec heightMeasureSpec) {
        CGSize measuredSize = UIViewLayoutUtil.getMeasuredSize(this);

        // Pretend that the linear layout has an exact size.
        LayoutMeasureSpec uniformMeasureSpec = new LayoutMeasureSpec();
        uniformMeasureSpec.setSize(measuredSize.getWidth());
        uniformMeasureSpec.setMode(LayoutMeasureSpecMode.Exactly);

        for (UIView child : getSubviews()) {
            if(UIViewLayoutUtil.getVisibility(child) == ViewVisibility.Gone) {
                continue;
            }

            LinearLayoutLayoutParams lp = (LinearLayoutLayoutParams) UIViewLayoutUtil.getLayoutParams(child);

            if (lp.getWidth() == LayoutParamsSize.MatchParent.getValue()) {
                // Temporarily force children to reuse their old measured height
                // FIXME: this may not be right for something like wrapping text?
                double oldHeight = lp.getHeight();
                CGSize childMeasuredSize = UIViewLayoutUtil.getMeasuredSize(child);
                lp.setHeight(childMeasuredSize.getHeight());

                // Remeasue with new dimensions
                UIViewViewGroupUtil.measureChild(this, child, uniformMeasureSpec, 0.d, heightMeasureSpec, 0.d);
                lp.setHeight(oldHeight);
            }
        }
    }

    /**
     * Measures the children when the orientation of this LinearLayout is set
     * to {@link #VERTICAL}.
     *
     * @param widthMeasureSpec Horizontal space requirements as imposed by the parent.
     * @param heightMeasureSpec Vertical space requirements as imposed by the parent.
     *
     * @see #getOrientation()
     * @see #setOrientation(int)
     * @see #onMeasure(int, int)
     */
    public void measureVertical(LayoutMeasureSpec widthMeasureSpec, LayoutMeasureSpec heightMeasureSpec) {
        totalLength = 0;
        double maxWidth = 0;
        LayoutMeasuredWidthHeightState childState = new LayoutMeasuredWidthHeightState(LayoutMeasuredState.None, LayoutMeasuredState.None);
        double alternativeMaxWidth = 0;
        double weightedMaxWidth = 0;

        boolean allFillParent = true;
        double totalWeight = 0;

        int count = getSubviews().size();

        LayoutMeasureSpecMode widthMode = widthMeasureSpec.getMode();
        LayoutMeasureSpecMode heightMode = heightMeasureSpec.getMode();

        boolean matchWidth = false;

        int baselineChildIndex = baselineAlignedChildIndex;
        boolean useLargestChild = this.useLargestChild;

        double largestChildHeight = Double.MIN_VALUE;

        // See how tall everyone is. Also remember max width.
        for (int i = 0; i < count; i++) {
            UIView child = getSubviews().get(i);

            if(UIViewLayoutUtil.getVisibility(child) == ViewVisibility.Gone) {
                i += getChildrenSkipCountAfterChild(child, i);
                continue;
            }

            LinearLayoutLayoutParams lp = (LinearLayoutLayoutParams) UIViewLayoutUtil.getLayoutParams(child);
            UIEdgeInsets lpMargin = lp.getMargin();
            totalWeight += lp.getWeight();

            if(heightMode == LayoutMeasureSpecMode.Exactly && lp.getHeight() == 0 && lp.getWeight() > 0) {
                // Optimization: don't bother measuring children who are going to use
                // leftover space. These views will get measured again down below if
                // there is any leftover space.
                double totalLength = this.totalLength;
                this.totalLength = Math.max(totalLength, totalLength + lpMargin.getTop() + lpMargin.getBottom());
            } else {
                double oldHeight = Double.MIN_VALUE;

                if(lp.getHeight() == 0 && lp.getWeight() > 0) {
                    // heightMode is either UNSPECIFIED or AT_MOST, and this
                    // child wanted to stretch to fill available space.
                    // Translate that to WRAP_CONTENT so that it does not end up
                    // with a height of 0
                    oldHeight = 0;
                    lp.setHeight(LayoutParamsSize.WrapContent.getValue());
                }

                // Determine how big this child would like to be. If this or
                // previous children have given a weight, then we allow it to
                // use all available space (and we will shrink things later
                // if needed).
                measureChildAtIndex(child, i, widthMeasureSpec, 0, heightMeasureSpec, totalWeight == 0 ? totalLength : 0);

                if(oldHeight != Double.MIN_VALUE) {
                    lp.setHeight(oldHeight);
                }

                double childHeight = UIViewLayoutUtil.getMeasuredSize(child).getHeight();
                double totalLength = this.totalLength;
                this.totalLength = Math.max(totalLength, totalLength + childHeight + lpMargin.getTop() + lpMargin.getBottom() + getNextLocationOffsetOfChild(child));

                if(useLargestChild) {
                    largestChildHeight = Math.max(childHeight, largestChildHeight);
                }
            }

            /**
             * If applicable, compute the additional offset to the child's baseline
             * we'll need later when asked {@link #getBaseline}.
             */
            if((baselineChildIndex >= 0) && (baselineChildIndex == i + 1)) {
                this.baselineChildTop = this.totalLength;
            }

            // if we are trying to use a child index for our baseline, the above
            // book keeping only works if there are no children above it with
            // weight.  fail fast to aid the developer.
            if(i < baselineChildIndex && lp.getWeight() > 0) {
                throw new IllegalStateException("A child of LinearLayout with index less than mBaselineAlignedChildIndex has weight > 0, which won't work.  Either remove the weight, or don't set mBaselineAlignedChildIndex.");
            }

            boolean matchWidthLocally = false;
            if(widthMode != LayoutMeasureSpecMode.Exactly && lp.getWidth() == LayoutParamsSize.MatchParent.getValue()) {
                // The width of the linear layout will scale, and at least one
                // child said it wanted to match our width. Set a flag
                // indicating that we need to remeasure at least that view when
                // we know our width.
                matchWidth = true;
                matchWidthLocally = true;
            }

            LayoutMeasuredWidthHeightState measuredState = UIViewLayoutUtil.getMeasuredState(child);
            double margin = lpMargin.getLeft() + lpMargin.getRight();
            double measuredWidth = UIViewLayoutUtil.getMeasuredSize(child).getWidth() + margin;

            maxWidth = Math.max(maxWidth, measuredWidth);
            childState = LayoutMeasuredWidthHeightState.combineMeasuredStates(childState, measuredState);

            allFillParent = allFillParent && lp.getWidth() == LayoutParamsSize.MatchParent.getValue();
            if(lp.getWeight() > 0) {
                /*
                 * Widths of weighted Views are bogus if we end up
                 * remeasuring, so keep them separate.
                 */
                weightedMaxWidth = Math.max(weightedMaxWidth, matchWidthLocally ? margin : measuredWidth);
            } else {
                alternativeMaxWidth = Math.max(alternativeMaxWidth, matchWidthLocally ? margin : measuredWidth);
            }

            i += getChildrenSkipCountAfterChild(child, i);
        }

        if(useLargestChild &&
                (heightMode == LayoutMeasureSpecMode.AtMost || heightMode == LayoutMeasureSpecMode.Unspecified)) {
            this.totalLength = 0;

            for (int i = 0; i < count; i++) {
                UIView child = getSubviews().get(i);

                if(UIViewLayoutUtil.getVisibility(child) == ViewVisibility.Gone) {
                    i += getChildrenSkipCountAfterChild(child, i);
                }

                LinearLayoutLayoutParams lp = (LinearLayoutLayoutParams) UIViewLayoutUtil.getLayoutParams(child);
                // Account for negative margins
                double totalLength = this.totalLength;
                UIEdgeInsets lpMargin = lp.getMargin();
                this.totalLength = Math.max(totalLength, totalLength + largestChildHeight +
                        lpMargin.getTop() + lpMargin.getBottom() + getNextLocationOffsetOfChild(child));
            }
        }

        // Add in our padding
        UIEdgeInsets padding = UIViewLayoutUtil.getPadding(this);
        this.totalLength += padding.getTop() + padding.getBottom();

        double heightSize = this.totalLength;

        // Check against our minimum height
        CGSize minSize = UIViewLayoutUtil.getMinSize(this);
        heightSize = Math.max(heightSize, minSize.getHeight());

        // Reconcile our calculated size with the heightMeasureSpec
        LayoutMeasuredDimension heightSizeAndState = LayoutMeasuredDimension.resolveSizeAndState(heightSize, heightMeasureSpec, LayoutMeasuredState.None);
        heightSize = heightSizeAndState.getSize();

        // Either expand children with weight to take up available space or
        // shrink them if they extend beyond our current bounds
        double delta = heightSize - this.totalLength;
        if(delta != 0 && totalWeight > 0.0d) {
            double weightSum = this.weightSum > 0.0d ? this.weightSum : totalWeight;

            this.totalLength = 0;

            for (int i = 0; i < count; i++) {
                UIView child = getSubviews().get(i);

                if (UIViewLayoutUtil.getVisibility(child) == ViewVisibility.Gone) {
                    continue;
                }

                LinearLayoutLayoutParams lp = (LinearLayoutLayoutParams) UIViewLayoutUtil.getLayoutParams(child);
                UIEdgeInsets lpMargin = lp.getMargin();

                double childExtra = lp.getWeight();
                if(childExtra > 0) {
                    // Child said it could absorb extra space -- give him his share
                    double share = (childExtra * delta / weightSum);
                    weightSum -= childExtra;
                    delta -= share;

                    LayoutMeasureSpec childWidthMeasureSpec = UIViewViewGroupUtil.getChildMeasureSpec(widthMeasureSpec,
                            (padding.getLeft() + padding.getRight() + lpMargin.getLeft() + lpMargin.getRight()),
                            lp.getWidth());

                    // TODO: Use a field like lp.isMeasured to figure out if this
                    // child has been previously measured
                    if((lp.getHeight() != 0) || (heightMode != LayoutMeasureSpecMode.Exactly)) {
                        // child was measured once already above...
                        // base new measurement on stored values
                        double childHeight = UIViewLayoutUtil.getMeasuredSize(child).getHeight() + share;
                        if(childHeight < 0) {
                            childHeight = 0;
                        }

                        LayoutMeasureSpec childHeightMeasureSpec = new LayoutMeasureSpec(childHeight, LayoutMeasureSpecMode.Exactly);
                        UIViewLayoutUtil.measure(child, childWidthMeasureSpec, childHeightMeasureSpec);
                    } else {
                        // child was skipped in the loop above.
                        // Measure for this first time here
                        LayoutMeasureSpec childHeightMeasureSpec = new LayoutMeasureSpec((share > 0 ? share : 0), LayoutMeasureSpecMode.Exactly);
                        UIViewLayoutUtil.measure(child, childWidthMeasureSpec, childHeightMeasureSpec);
                    }

                    // Child may now not fit in vertical dimension.
                    LayoutMeasuredWidthHeightState newState = UIViewLayoutUtil.getMeasuredState(child);
                    newState.setWidthState(LayoutMeasuredState.None);
                    childState = LayoutMeasuredWidthHeightState.combineMeasuredStates(childState, newState);
                }

                double margin = lpMargin.getLeft() + lpMargin.getRight();
                double measuredWidth = UIViewLayoutUtil.getMeasuredSize(child).getWidth() + margin;
                maxWidth = Math.max(maxWidth, measuredWidth);

                boolean matchWidthLocally = widthMode != LayoutMeasureSpecMode.Exactly && lp.getWidth() == LayoutParamsSize.MatchParent.getValue();

                alternativeMaxWidth = Math.max(alternativeMaxWidth,
                        matchWidthLocally ? margin : measuredWidth);

                allFillParent = allFillParent && lp.getWidth() == LayoutParamsSize.MatchParent.getValue();

                double totalLength = this.totalLength;
                CGSize childMeasuredSize = UIViewLayoutUtil.getMeasuredSize(child);

                this.totalLength = Math.max(totalLength,
                        totalLength + childMeasuredSize.getHeight() + lpMargin.getTop() + lpMargin.getBottom() + getNextLocationOffsetOfChild(child));
            }

            // Add in our padding
            this.totalLength += padding.getTop() + padding.getBottom();
            // TODO: Should we recompute the heightSpec based on the new total length?
        } else {
            alternativeMaxWidth = Math.max(alternativeMaxWidth, weightedMaxWidth);

            // We have no limit, so make all weighted views as tall as the largest child.
            // Children will have already been measured once.
            if(useLargestChild && widthMode == LayoutMeasureSpecMode.Unspecified) {
                for (int i = 0; i < count; i++) {
                    UIView child = getSubviews().get(i);

                    if (UIViewLayoutUtil.getVisibility(child) == ViewVisibility.Gone) {
                        continue;
                    }

                    LinearLayoutLayoutParams lp = (LinearLayoutLayoutParams) UIViewLayoutUtil.getLayoutParams(child);

                    double childExtra = lp.getWeight();
                    if(childExtra > 0) {
                        CGSize childMeasuredSize = UIViewLayoutUtil.getMeasuredSize(child);

                        LayoutMeasureSpec childWidthMeasureSpec = new LayoutMeasureSpec(childMeasuredSize.getWidth(), LayoutMeasureSpecMode.Exactly);
                        LayoutMeasureSpec childHeightMeasureSpec = new LayoutMeasureSpec(largestChildHeight, LayoutMeasureSpecMode.Exactly);

                        UIViewLayoutUtil.measure(child, childWidthMeasureSpec, childHeightMeasureSpec);
                    }
                }
            }
        }

        if (!allFillParent && widthMode != LayoutMeasureSpecMode.Exactly) {
            maxWidth = alternativeMaxWidth;
        }

        maxWidth += padding.getLeft() + padding.getRight();

        // Check against our minimum width
        maxWidth = Math.max(maxWidth, minSize.getWidth());

        LayoutMeasuredDimension layoutMeasuredDimension = LayoutMeasuredDimension.resolveSizeAndState(maxWidth, widthMeasureSpec, childState.getWidthState());
        LayoutMeasuredSize measuredSize = new LayoutMeasuredSize(layoutMeasuredDimension, heightSizeAndState);

        UIViewLayoutUtil.setMeasuredDimensionSize(this, measuredSize);

        if(matchWidth) {
            forceUniformWidth(count, heightMeasureSpec);
        }
    }

    private void measureChildAtIndex(UIView child, int index, LayoutMeasureSpec widthMeasureSpec,
                                     double totalWidth,
                                     LayoutMeasureSpec parentHeightMeasureSpec,
                                     double totalHeight) {

        UIViewViewGroupUtil.measureChild(this, child, widthMeasureSpec, totalWidth, parentHeightMeasureSpec, totalHeight);
    }

    private void forceUniformHeight(int count, LayoutMeasureSpec widthMeasureSpec) {
        // Pretend that the linear layout has an exact size. This is the measured height of
        // ourselves. The measured height should be the max height of the children, changed
        // to accomodate the heightMesureSpec from the parent
        CGSize measuredSize = UIViewLayoutUtil.getMeasuredSize(this);
        LayoutMeasureSpec uniformMeasureSpec = new LayoutMeasureSpec(measuredSize.getHeight(), LayoutMeasureSpecMode.Exactly);

        for (int i = 0; i < count; i++) {
            UIView child = getSubviews().get(i);

            if (UIViewLayoutUtil.getVisibility(child) == ViewVisibility.Gone) {
                continue;
            }

            LinearLayoutLayoutParams lp = (LinearLayoutLayoutParams) UIViewLayoutUtil.getLayoutParams(child);

            if(lp.getHeight() == LayoutParamsSize.MatchParent.getValue()) {
                // Temporarily force children to reuse their old measured width
                // FIXME: this may not be right for something like wrapping text?
                double oldWidth = lp.getWidth();
                CGSize childMeasuredSize = UIViewLayoutUtil.getMeasuredSize(child);

                lp.setWidth(childMeasuredSize.getWidth());

                // Remeasure with new dimensions
                UIViewViewGroupUtil.measureChild(this, child, widthMeasureSpec, 0, uniformMeasureSpec, 0);
                lp.setWeight(oldWidth);
            }
        }
    }

    /**
     * Measures the children when the orientation of this LinearLayout is set
     * to {@link #HORIZONTAL}.
     *
     * @param widthMeasureSpec Horizontal space requirements as imposed by the parent.
     * @param heightMeasureSpec Vertical space requirements as imposed by the parent.
     *
     * @see #getOrientation()
     * @see #setOrientation(int)
     * @see #onMeasure(int, int)
     */
    public void measureHorizontal(LayoutMeasureSpec widthMeasureSpec, LayoutMeasureSpec heightMeasureSpec) {
        this.totalLength = 0.d;
        double maxHeight = 0.d;
        LayoutMeasuredWidthHeightState childState = new LayoutMeasuredWidthHeightState(LayoutMeasuredState.None, LayoutMeasuredState.None);
        double alternativeMaxHeight = 0.d;
        double weightedMaxHeight = 0.d;
        boolean allFillParent = true;
        double totalWeight = 0.d;

        int count = getSubviews().size();

        LayoutMeasureSpecMode widthMode = widthMeasureSpec.getMode();
        LayoutMeasureSpecMode heightMode = heightMeasureSpec.getMode();

        boolean matchHeight = false;

        int[] maxAscent = new int[VERTICAL_GRAVITY_COUNT];
        int[] maxDescent = new int[VERTICAL_GRAVITY_COUNT];

        maxAscent[0] = maxAscent[1] = maxAscent[2] = maxAscent[3] = -1;
        maxDescent[0] = maxDescent[1] = maxDescent[2] = maxDescent[3] = -1;

        boolean baselineAligned = this.baselineAligned;
        boolean useLargestChild = this.useLargestChild;

        boolean isExactly = widthMode == LayoutMeasureSpecMode.Exactly;

        double largestChildWidth = Double.MIN_VALUE;

        // See how wide everyone is. Also remember max height.
        for (int i = 0; i < count; i++) {
            UIView child = getSubviews().get(i);

            if (UIViewLayoutUtil.getVisibility(child) == ViewVisibility.Gone) {
                i += getChildrenSkipCountAfterChild(child, i);
                continue;
            }

            LinearLayoutLayoutParams lp = (LinearLayoutLayoutParams) UIViewLayoutUtil.getLayoutParams(child);
            UIEdgeInsets lpMargin = lp.getMargin();

            totalWeight += lp.getWeight();

            if(widthMode == LayoutMeasureSpecMode.Exactly && lp.getWidth() == 0 && lp.getWeight() > 0) {
                // Optimization: don't bother measuring children who are going to use
                // leftover space. These views will get measured again down below if
                // there is any leftover space.
                if(isExactly) {
                    this.totalLength += lpMargin.getLeft() + lpMargin.getRight();
                } else {
                    double totalLength = this.totalLength;
                    this.totalLength = Math.max(totalLength, totalLength + lpMargin.getLeft() + lpMargin.getRight());
                }

                // Baseline alignment requires to measure widgets to obtain the
                // baseline offset (in particular for TextViews). The following
                // defeats the optimization mentioned above. Allow the child to
                // use as much space as it wants because we can shrink things
                // later (and re-measure).
                if(baselineAligned) {
                    LayoutMeasureSpec freeSpec = new LayoutMeasureSpec(0, LayoutMeasureSpecMode.Unspecified);
                    UIViewLayoutUtil.measure(child, freeSpec, freeSpec);
                }
            } else {
                double oldWidth = Double.MIN_VALUE;

                if(lp.getWidth() == 0 && lp.getWeight() > 0) {
                    // widthMode is either UNSPECIFIED or AT_MOST, and this
                    // child
                    // wanted to stretch to fill available space. Translate that to
                    // WRAP_CONTENT so that it does not end up with a width of 0
                    oldWidth = 0.d;
                    lp.setWidth(LayoutParamsSize.WrapContent.getValue());
                }

                // Determine how big this child would like to be. If this or
                // previous children have given a weight, then we allow it to
                // use all available space (and we will shrink things later
                // if needed).
                measureChildAtIndex(child, i, widthMeasureSpec, (totalWeight == 0 ? this.totalLength : 0), heightMeasureSpec, 0);

                if(oldWidth != Double.MIN_VALUE) {
                    lp.setWidth(oldWidth);
                }

                CGSize childMeasuredSize = UIViewLayoutUtil.getMeasuredSize(child);
                double childWidth = childMeasuredSize.getWidth();
                if(isExactly) {
                    this.totalLength += childWidth + lpMargin.getLeft() + lpMargin.getRight() + getNextLocationOffsetOfChild(child);
                } else {
                    double totalLength = this.totalLength;
                    this.totalLength = Math.max(totalLength, totalLength + childWidth + lpMargin.getLeft() + lpMargin.getRight() + getNextLocationOffsetOfChild(child));
                }

                if(useLargestChild) {
                    largestChildWidth = Math.max(childWidth, largestChildWidth);
                }
            }

            boolean matchHeightLocally = false;
            if(heightMode != LayoutMeasureSpecMode.Exactly && lp.getHeight() == LayoutParamsSize.MatchParent.getValue()) {
                // The height of the linear layout will scale, and at least one
                // child said it wanted to match our height. Set a flag indicating that
                // we need to remeasure at least that view when we know our height.
                matchHeight = true;
                matchHeightLocally = true;
            }

            CGSize childMeasuredSize = UIViewLayoutUtil.getMeasuredSize(child);
            double margin = lpMargin.getTop() + lpMargin.getBottom();
            double childHeight = childMeasuredSize.getHeight() + margin;
            childState = LayoutMeasuredWidthHeightState.combineMeasuredStates(childState, UIViewLayoutUtil.getMeasuredState(child));

            if(baselineAligned) {
                double childBaseline = UIViewLayoutUtil.getBaseLine(child);
                if(childBaseline != -1) {
                    // Translates the child's vertical gravity into an index
                    // in the range 0..VERTICAL_GRAVITY_COUNT
                    ViewContentGravity gravity = ViewContentGravity.create(
                            (lp.getGravity().value() < ViewContentGravity.None.value() ? this.gravity : lp.getGravity()).value()
                            & ViewContentGravity.VERTICAL_GRAVITY_MASK
                    );

                    int index = (int) (((gravity.value() >> ViewContentGravity.GravityAxis.AXIS_Y_SHIFT)
                            & ~ViewContentGravity.GravityAxis.AXIS_SPECIFIED) >> 1);

                    maxAscent[index] = (int) Math.max(maxAscent[index], childBaseline);
                    maxDescent[index] = (int) Math.max(maxDescent[index], childHeight - childBaseline);
                }
            }

            maxHeight = Math.max(maxHeight, childHeight);

            allFillParent = allFillParent && lp.getHeight() == LayoutParamsSize.MatchParent.getValue();
            if(lp.getWeight() > 0) {
                /*
                 * Heights of weighted Views are bogus if we end up
                 * remeasuring, so keep them separate.
                 */
                weightedMaxHeight = Math.max(weightedMaxHeight, matchHeightLocally ? margin : childHeight);
            } else {
                alternativeMaxHeight = Math.max(alternativeMaxHeight, matchHeightLocally ? margin : childHeight);
            }

            i += getChildrenSkipCountAfterChild(child, i);
        }

        // Check mMaxAscent[INDEX_TOP] first because it maps to Gravity.TOP,
        // the most common case
        if(maxAscent[MAX_ASCENT_DESCENT_INDEX_TOP] != -1 ||
                maxAscent[MAX_ASCENT_DESCENT_INDEX_CENTER_VERTICAL] != -1 ||
                maxAscent[MAX_ASCENT_DESCENT_INDEX_BOTTOM] != -1 ||
                maxAscent[MAX_ASCENT_DESCENT_INDEX_FILL] != -1) {

            int ascent = Math.max(maxAscent[MAX_ASCENT_DESCENT_INDEX_FILL],
                    Math.max(maxAscent[MAX_ASCENT_DESCENT_INDEX_CENTER_VERTICAL],
                            Math.max(maxAscent[MAX_ASCENT_DESCENT_INDEX_TOP], maxAscent[MAX_ASCENT_DESCENT_INDEX_BOTTOM])));
            int descent = Math.max(maxDescent[MAX_ASCENT_DESCENT_INDEX_FILL],
                    Math.max(maxDescent[MAX_ASCENT_DESCENT_INDEX_CENTER_VERTICAL],
                            Math.max(maxDescent[MAX_ASCENT_DESCENT_INDEX_TOP], maxDescent[MAX_ASCENT_DESCENT_INDEX_BOTTOM])));
            maxHeight = Math.max(maxHeight, ascent + descent);
        }

        if(useLargestChild &&
                (widthMode == LayoutMeasureSpecMode.AtMost || widthMode == LayoutMeasureSpecMode.Unspecified)) {
            this.totalLength = 0;

            for (int i = 0; i < count; i++) {
                UIView child = getSubviews().get(i);

                if (UIViewLayoutUtil.getVisibility(child) == ViewVisibility.Gone) {
                    i += getChildrenSkipCountAfterChild(child, i);
                    continue;
                }

                LinearLayoutLayoutParams lp = (LinearLayoutLayoutParams) UIViewLayoutUtil.getLayoutParams(child);
                UIEdgeInsets lpMargin = lp.getMargin();
                if(isExactly) {
                    this.totalLength += largestChildWidth + lpMargin.getLeft() + lpMargin.getRight() + getNextLocationOffsetOfChild(child);
                } else {
                    double totalLength = this.totalLength;
                    this.totalLength = Math.max(totalLength, totalLength + largestChildWidth + lpMargin.getLeft() + lpMargin.getRight() + getNextLocationOffsetOfChild(child));
                }
            }
        }

        // Add in our padding
        UIEdgeInsets padding = UIViewLayoutUtil.getPadding(this);
        this.totalLength += padding.getLeft() + padding.getRight();

        double widthSize = this.totalLength;

        // Check against our minimum width
        CGSize minSize = UIViewLayoutUtil.getMinSize(this);
        widthSize = Math.max(widthSize, minSize.getWidth());

        // Reconcile our calculated size with the widthMeasureSpec
        LayoutMeasuredDimension widthSizeAndState = LayoutMeasuredDimension.resolveSizeAndState(widthSize, widthMeasureSpec, LayoutMeasuredState.None);
        widthSize = widthSizeAndState.getSize();

        // Either expand children with weight to take up available space or
        // shrink them if they extend beyond our current bounds
        double delta = widthSize - this.totalLength;
        if(delta != 0 && totalWeight > 0.0d) {
            double weightSum = this.weightSum > 0.0d ? this.weightSum : totalWeight;

            maxAscent[0] = maxAscent[1] = maxAscent[2] = maxAscent[3] = -1;
            maxDescent[0] = maxDescent[1] = maxDescent[2] = maxDescent[3] = -1;
            maxHeight = -1;

            this.totalLength = 0;

            for (int i = 0; i < count; i++) {
                UIView child = getSubviews().get(i);

                if (UIViewLayoutUtil.getVisibility(child) == ViewVisibility.Gone) {
                    continue;
                }

                LinearLayoutLayoutParams lp = (LinearLayoutLayoutParams) UIViewLayoutUtil.getLayoutParams(child);
                UIEdgeInsets lpMargin = lp.getMargin();

                double childExtra = lp.getWeight();
                if(childExtra > 0) {
                    // Child said it could absorb extra space -- give him his share
                    int share = (int) (childExtra * delta / weightSum);
                    weightSum -= childExtra;
                    delta -= share;

                    LayoutMeasureSpec childHeightMeasureSpec = UIViewViewGroupUtil.getChildMeasureSpec(
                            heightMeasureSpec,
                            (padding.getTop() + padding.getBottom() + lpMargin.getTop() + lpMargin.getBottom()),
                            lp.getHeight()
                    );

                    // TODO: Use a field like lp.isMeasured to figure out if this
                    // child has been previously measured
                    if((lp.getWidth() != 0) || (widthMode != LayoutMeasureSpecMode.Exactly)) {
                        // child was measured once already above ... base new measurement
                        // on stored values
                        CGSize childMeasuredSize = UIViewLayoutUtil.getMeasuredSize(child);
                        double childWidth = childMeasuredSize.getWidth();
                        if(childWidth < 0) {
                            childWidth = 0;
                        }

                        LayoutMeasureSpec childWidthMeasureSpec = new LayoutMeasureSpec(childWidth, LayoutMeasureSpecMode.Exactly);
                        UIViewLayoutUtil.measure(child, childWidthMeasureSpec, childHeightMeasureSpec);
                    } else {
                        // child was skipped in the loop above. Measure for this first time here
                        LayoutMeasureSpec childWidthMeasureSpec = new LayoutMeasureSpec((share > 0 ? share : 0), LayoutMeasureSpecMode.Exactly);
                        UIViewLayoutUtil.measure(child, childWidthMeasureSpec, childHeightMeasureSpec);
                    }

                    // Child may now not fit in horizontal dimension.
                    LayoutMeasuredWidthHeightState newState = UIViewLayoutUtil.getMeasuredState(child);
                    newState.setHeightState(LayoutMeasuredState.None);
                    childState = LayoutMeasuredWidthHeightState.combineMeasuredStates(childState, newState);
                }

                CGSize childMeasuredSize = UIViewLayoutUtil.getMeasuredSize(child);
                if(isExactly) {
                    this.totalLength += childMeasuredSize.getWidth() + lpMargin.getLeft() + lpMargin.getRight() + getNextLocationOffsetOfChild(child);
                } else {
                    double totalLength = this.totalLength;
                    this.totalLength = Math.max(totalLength,
                            totalLength + childMeasuredSize.getWidth() + lpMargin.getLeft() + lpMargin.getRight() + getNextLocationOffsetOfChild(child));
                }

                boolean matchHeightLocally = heightMode != LayoutMeasureSpecMode.Exactly && lp.getHeight() == LayoutParamsSize.MatchParent.getValue();

                double margin = lpMargin.getTop() + lpMargin.getBottom();
                double childHeight = childMeasuredSize.getHeight() + margin;
                maxHeight = Math.max(maxHeight, childHeight);
                alternativeMaxHeight = Math.max(alternativeMaxHeight, matchHeightLocally ? margin : childHeight);

                allFillParent = allFillParent && lp.getHeight() == LayoutParamsSize.MatchParent.getValue();

                if(baselineAligned) {
                    double childBaseline = UIViewLayoutUtil.getBaseLine(child);
                    if(childBaseline != -1) {
                        // Translates the child's vertical gravity into an index in the range 0..2
                        ViewContentGravity gravity = ViewContentGravity.create((lp.getGravity().value() < ViewContentGravity.None.value()
                                ? this.gravity : lp.getGravity()).value() & ViewContentGravity.VERTICAL_GRAVITY_MASK);

                        int index = (int) (((gravity.value() >> ViewContentGravity.GravityAxis.AXIS_Y_SHIFT)
                                                        & ~ViewContentGravity.GravityAxis.AXIS_SPECIFIED) >> 1);

                        maxAscent[index] = (int) Math.max(maxAscent[index], childBaseline);
                        maxDescent[index] = (int) Math.max(maxDescent[index], childHeight - childBaseline);
                    }
                }
            }

            // Add in our padding
            this.totalLength += padding.getLeft() + padding.getRight();
            // TODO: Should we update widthSize with the new total length?

            // Check mMaxAscent[INDEX_TOP] first because it maps to Gravity.TOP,
            // the most common case
            if (maxAscent[MAX_ASCENT_DESCENT_INDEX_TOP] != -1 ||
                    maxAscent[MAX_ASCENT_DESCENT_INDEX_CENTER_VERTICAL] != -1 ||
                    maxAscent[MAX_ASCENT_DESCENT_INDEX_BOTTOM] != -1 ||
                    maxAscent[MAX_ASCENT_DESCENT_INDEX_FILL] != -1) {
                int ascent = Math.max(maxAscent[MAX_ASCENT_DESCENT_INDEX_FILL],
                        Math.max(maxAscent[MAX_ASCENT_DESCENT_INDEX_CENTER_VERTICAL],
                                Math.max(maxAscent[MAX_ASCENT_DESCENT_INDEX_TOP], maxAscent[MAX_ASCENT_DESCENT_INDEX_BOTTOM])));
                int descent = Math.max(maxDescent[MAX_ASCENT_DESCENT_INDEX_FILL],
                        Math.max(maxDescent[MAX_ASCENT_DESCENT_INDEX_CENTER_VERTICAL],
                                Math.max(maxDescent[MAX_ASCENT_DESCENT_INDEX_TOP], maxDescent[MAX_ASCENT_DESCENT_INDEX_BOTTOM])));
                maxHeight = Math.max(maxHeight, ascent + descent);
            }
        } else {
            alternativeMaxHeight = Math.max(alternativeMaxHeight, weightedMaxHeight);

            // We have no limit, so make all weighted views as wide as the largest child.
            // Children will have already been measured once.
            if (useLargestChild && widthMode == LayoutMeasureSpecMode.Unspecified) {
                for (int i = 0; i < count; i++) {
                    UIView child = getSubviews().get(i);

                    if (UIViewLayoutUtil.getVisibility(child) == ViewVisibility.Gone) {
                        continue;
                    }

                    LinearLayoutLayoutParams lp = (LinearLayoutLayoutParams) UIViewLayoutUtil.getLayoutParams(child);

                    CGSize childMeasuredSize = UIViewLayoutUtil.getMeasuredSize(child);
                    double childExtra = lp.getWeight();
                    if (childExtra > 0) {
                        LayoutMeasureSpec childWidthMeasureSpec = new LayoutMeasureSpec(largestChildWidth, LayoutMeasureSpecMode.Exactly);
                        LayoutMeasureSpec childHeightMeasureSpec = new LayoutMeasureSpec(childMeasuredSize.getHeight(), LayoutMeasureSpecMode.Exactly);

                        UIViewLayoutUtil.measure(child, childWidthMeasureSpec, childHeightMeasureSpec);
                    }
                }
            }
        }

        if (!allFillParent && heightMode != LayoutMeasureSpecMode.Exactly) {
            maxHeight = alternativeMaxHeight;
        }

        maxHeight += padding.getTop() + padding.getBottom();

        // Check against our minimum height
        maxHeight = Math.max(maxHeight, minSize.getHeight());

        widthSizeAndState.setState( widthSizeAndState.getState().set(childState.getWidthState()) );

        LayoutMeasuredDimension heightSizeAndState = LayoutMeasuredDimension.resolveSizeAndState(maxHeight, heightMeasureSpec, childState.getHeightState());
        LayoutMeasuredSize measuredSize = new LayoutMeasuredSize(widthSizeAndState, heightSizeAndState);

        UIViewLayoutUtil.setMeasuredDimensionSize(this, measuredSize);

        if (matchHeight) {
            forceUniformHeight(count, widthMeasureSpec);
        }
    }

    @Override
    public void onMeasure(LayoutMeasureSpec widthMeasureSpec, LayoutMeasureSpec heightMeasureSpec) {
        if(this.orientation == LinearLayoutOrientation.Vertical) {
            measureVertical(widthMeasureSpec, heightMeasureSpec);
        } else {
            measureHorizontal(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * <p>Return the location offset of the specified child. This can be used
     * by subclasses to change the location of a given widget.</p>
     *
     * @param child the child for which to obtain the location offset
     * @return the location offset in pixels
     */
    public double getLocationOffsetOfChild(UIView child) {
        return 0;
    }

    public void setChildFrameOfChild(UIView child, CGRect frame) {
        UIViewLayoutUtil.layout(child, frame);
    }

    /**
     * Position the children during a layout pass if the orientation of this
     * LinearLayout is set to LinearLayoutOrientationVertical.
     */
    private void layoutVertical() {
        UIEdgeInsets padding = UIViewLayoutUtil.getPadding(this);

        double childTop = 0;
        double childLeft = 0;

        // Where right end of child should go
        double width = getFrame().getSize().getWidth();
        double childRight = width - padding.getRight();

        // Space available for child
        double childSpace = width - padding.getLeft() - padding.getRight();

        int count = getSubviews().size();

        ViewContentGravity majorGravity = new ViewContentGravity(gravity.value() & ViewContentGravity.VERTICAL_GRAVITY_MASK);
        ViewContentGravity minorGravity = new ViewContentGravity(gravity.value() & ViewContentGravity.RELATIVE_HORIZONTAL_GRAVITY_MASK);

        if(majorGravity.value() == ViewContentGravity.Bottom.value()) {
            // mTotalLength contains the padding already
            childTop = padding.getTop() + getFrame().getSize().getHeight() - this.totalLength;

        } else if(majorGravity.value() == ViewContentGravity.CenterVertical.value()) {
            childTop = padding.getTop() + (getFrame().getSize().getHeight() - this.totalLength) / 2;

        } else if(majorGravity.value() == ViewContentGravity.Top.value()) {
            childTop = padding.getTop();
        }

        for (int i = 0; i < count; i++) {
            UIView child = getSubviews().get(i);

            if (UIViewLayoutUtil.getVisibility(child) != ViewVisibility.Gone) {
                CGSize childSize = UIViewLayoutUtil.getMeasuredSize(child);

                LinearLayoutLayoutParams lp = (LinearLayoutLayoutParams) UIViewLayoutUtil.getLayoutParams(child);
                UIEdgeInsets lpMargin = lp.getMargin();

                ViewContentGravity gravity = lp.getGravity();
                if (gravity.value() < ViewContentGravity.None.value()) {
                    gravity = minorGravity;
                }

                long gravityValue = gravity.value() & ViewContentGravity.HORIZONTAL_GRAVITY_MASK;
                if(gravityValue == ViewContentGravity.CenterHorizontal.value()) {
                    childLeft = padding.getLeft() + ((childSpace - childSize.getWidth()) / 2)
                            + lpMargin.getLeft() - lpMargin.getRight();

                } else if(gravityValue == ViewContentGravity.Right.value()) {
                    childLeft = childRight - childSize.getWidth() - lpMargin.getRight();

                } else if(gravityValue == ViewContentGravity.Left.value()) {
                    childLeft = padding.getLeft() + lpMargin.getLeft();
                }

                childTop += lpMargin.getTop();
                setChildFrameOfChild(child, new CGRect(childLeft, childTop + getLocationOffsetOfChild(child), childSize.getWidth(), childSize.getHeight()));

                childTop += childSize.getHeight() + lpMargin.getBottom() + getNextLocationOffsetOfChild(child);

                i += getChildrenSkipCountAfterChild(child, i);
            }
        }
    }

    /**
     * Position the children during a layout pass if the orientation of this
     * LinearLayout is set to LinearLayoutOrientationHorizontal.
     */
    private void layoutHorizontal() {
        UIEdgeInsets padding = UIViewLayoutUtil.getPadding(this);

        double childTop = 0;
        double childLeft = 0;

        // Where bottom of child should go
        double height = getFrame().getSize().getHeight();
        double childRight = height - padding.getRight();

        // Space available for child
        double childSpace = height - padding.getTop() - padding.getBottom();

        int count = getSubviews().size();

        ViewContentGravity majorGravity = new ViewContentGravity(gravity.value() & ViewContentGravity.RELATIVE_HORIZONTAL_GRAVITY_MASK);
        ViewContentGravity minorGravity = new ViewContentGravity(gravity.value() & ViewContentGravity.VERTICAL_GRAVITY_MASK);

        boolean baselineAligned = this.baselineAligned;
        if(majorGravity.value() == ViewContentGravity.Right.value()) {
            // mTotalLength contains the padding already
            childTop = padding.getLeft() + getFrame().getSize().getWidth() - this.totalLength;

        } else if(majorGravity.value() == ViewContentGravity.CenterHorizontal.value()) {
            childTop = padding.getLeft() + (getFrame().getSize().getWidth() - this.totalLength) / 2;

        } else if(majorGravity.value() == ViewContentGravity.Left.value()) {
            childTop = padding.getLeft();
        }

        for (int i = 0; i < count; i++) {
            UIView child = getSubviews().get(i);

            if (UIViewLayoutUtil.getVisibility(child) != ViewVisibility.Gone) {
                CGSize childSize = UIViewLayoutUtil.getMeasuredSize(child);
                double childBaseline = -1;

                LinearLayoutLayoutParams lp = (LinearLayoutLayoutParams) UIViewLayoutUtil.getLayoutParams(child);
                UIEdgeInsets lpMargin = lp.getMargin();

                if(baselineAligned && lp.getHeight() != LayoutParamsSize.MatchParent.getValue()) {
                    childBaseline = UIViewLayoutUtil.getBaseLine(child);
                }

                ViewContentGravity gravity = lp.getGravity();
                if (gravity.value() < ViewContentGravity.None.value()) {
                    gravity = minorGravity;
                }

                long gravityValue = gravity.value() & ViewContentGravity.VERTICAL_GRAVITY_MASK;
                if(gravityValue == ViewContentGravity.Top.value()) {
                    childTop = padding.getTop() + lpMargin.getTop();
                    if(childBaseline != -1) {
                        childTop += this.maxAscent[MAX_ASCENT_DESCENT_INDEX_TOP] - childBaseline;
                    }

                } else if(gravityValue == ViewContentGravity.CenterVertical.value()) {
                    childTop = padding.getTop() + ((childSpace - childSize.getHeight()) / 2) + lpMargin.getTop() - lpMargin.getBottom();

                } else if(gravityValue == ViewContentGravity.Bottom.value()) {
                    childTop = padding.getLeft() + lpMargin.getLeft();
                } else {
                    childTop = padding.getTop();
                }

                childLeft += lpMargin.getLeft();
                setChildFrameOfChild(child, new CGRect(childLeft + getLocationOffsetOfChild(child), childTop, childSize.getWidth(), childSize.getHeight()));

                childLeft += childSize.getWidth() + lpMargin.getRight() + getNextLocationOffsetOfChild(child);

                i += getChildrenSkipCountAfterChild(child, i);
            }
        }
    }

    @Override
    public void onLayout(CGRect frame, boolean changed) {
        if(this.orientation == LinearLayoutOrientation.Vertical) {
            layoutVertical();
        } else {
            layoutHorizontal();
        }
    }

    public boolean checkLayoutParams(LayoutParams layoutParams) {
        return layoutParams instanceof LinearLayoutLayoutParams;
    }

    /**
     * Returns a set of layout parameters with a width of
     * {@link android.view.ViewGroup.LayoutParams#MATCH_PARENT}
     * and a height of {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}
     * when the layout's orientation is {@link #VERTICAL}. When the orientation is
     * {@link #HORIZONTAL}, the width is set to {@link LayoutParams#WRAP_CONTENT}
     * and the height to {@link LayoutParams#WRAP_CONTENT}.
     */
    @Override
    public LayoutParams generateDefaultLayoutParams() {
        if(this.orientation == LinearLayoutOrientation.Horizontal) {
            return new LinearLayoutLayoutParams(LayoutParamsSize.WrapContent, LayoutParamsSize.WrapContent);
        } else if(this.orientation == LinearLayoutOrientation.Vertical) {
            return new LinearLayoutLayoutParams(LayoutParamsSize.MatchParent, LayoutParamsSize.WrapContent);
        }
        return null;
    }

    @Override
    public LayoutParams generateLayoutParams(LayoutParams params) {
        return new LinearLayoutLayoutParams(params);
    }

    @Override
    public LayoutParams generateLayoutParams(Map<String, String> attrs) {
        return new LinearLayoutLayoutParams(attrs);
    }
}
