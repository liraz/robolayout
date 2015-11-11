package org.lirazs.robolayout.core.util;

import org.lirazs.robolayout.core.resource.ResourceManager;
import org.lirazs.robolayout.core.resource.drawable.Drawable;
import org.lirazs.robolayout.core.view.*;
import org.lirazs.robolayout.core.widget.category.UIButtonLayoutUtil;
import org.lirazs.robolayout.core.widget.category.UIImageViewLayoutUtil;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSNumber;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.foundation.NSValue;
import org.robovm.apple.uikit.*;

import java.util.Map;

/**
 * Created by mac on 7/29/15.
 */
public class UIViewLayoutUtil {

    private static String IDENTIFIER_KEY = "IDENTIFIER_KEY";
    private static String MIN_SIZE_KEY = "MIN_SIZE_KEY";
    private static String PADDING_KEY = "PADDING_KEY";
    private static String LAYOUT_PARAMS_KEY = "LAYOUT_PARAMS_KEY";
    private static String IS_LAYOUT_REQUESTED_KEY = "IS_LAYOUT_REQUESTED_KEY";
    private static String VISIBILITY_KEY = "VISIBILITY_KEY";
    private static String MEASURED_SIZE_KEY = "MEASURED_SIZE_KEY";

    public static UIView create(Map<String, String> attrs) {
        UIView view = new UIView();
        applyAttributes(view, attrs);

        return view;
    }

    public static void applyAttributes(UIView view, Map<String, String> attrs) {
        // visibility
        String visibilityString = attrs.get("visibility");
        if (visibilityString != null) {
            setVisibility(view, ViewVisibility.get(visibilityString));
        }

        // background
        String backgroundString = attrs.get("background");
        if(backgroundString != null) {
            Drawable backgroundDrawable = ResourceManager.getCurrent().getDrawable(backgroundString);
            if (backgroundDrawable != null) {
                UIViewDrawableUtil.setBackgroundDrawable(view, backgroundDrawable);
            }
        }

        // padding
        String paddingString = attrs.get("padding");
        if(paddingString != null) {
            double padding = Double.parseDouble(paddingString);
            setPadding(view, new UIEdgeInsets(padding, padding, padding, padding));
        } else {
            UIEdgeInsets padding = getPadding(view);
            UIEdgeInsets initialPadding = padding.copy();

            String paddingTopString = attrs.get("paddingTop");
            String paddingLeftString = attrs.get("paddingLeft");
            String paddingBottomString = attrs.get("paddingBottom");
            String paddingRightString = attrs.get("paddingRight");

            if(paddingTopString != null && !paddingTopString.isEmpty())
                padding.setTop(Double.parseDouble(paddingTopString));
            if(paddingLeftString != null && !paddingLeftString.isEmpty())
                padding.setLeft(Double.parseDouble(paddingLeftString));
            if(paddingBottomString != null && !paddingBottomString.isEmpty())
                padding.setBottom(Double.parseDouble(paddingBottomString));
            if(paddingRightString != null && !paddingRightString.isEmpty())
                padding.setRight(Double.parseDouble(paddingRightString));

            if(!padding.equalsTo(initialPadding)) {
                setPadding(view, padding);
            }
        }

        // alpha
        String alphaString = attrs.get("alpha");
        if(alphaString != null) {
            double alpha = Math.min(1.0, Math.max(0.0, Double.parseDouble(alphaString)));
            view.setAlpha(alpha);
        }

        // minSize
        double minWidth = attrs.containsKey("minWidth") ? Double.parseDouble(attrs.get("minWidth")) : 0;
        double minHeight = attrs.containsKey("minHeight") ? Double.parseDouble(attrs.get("minHeight")) : 0;
        setMinSize(view, new CGSize(minWidth, minHeight));

        // identifier
        String identifier = attrs.get("id");
        if(identifier != null) {
            int index = identifier.indexOf("@id/");
            int searchLength = "@id/".length();

            if(index == -1) {
                index = identifier.indexOf("@+id/");
                searchLength = "@+id/".length();
            }

            if(index == 0) {
                identifier = identifier.substring(index + searchLength);
            }
            setIdentifier(view, identifier);
        }

        // border
        String borderWidth = attrs.get("borderWidth");
        if(borderWidth != null) {
            view.getLayer().setBorderWidth(Double.parseDouble(borderWidth));
        }

        UIColor borderColor = ResourceAttributesUtil.getColorValue(attrs, "borderColor");
        if(borderColor != null) {
            view.getLayer().setBorderColor(borderColor.getCGColor());
        }

        String cornerRadius = attrs.get("cornerRadius");
        if(cornerRadius != null) {
            view.getLayer().setCornerRadius(Double.parseDouble(cornerRadius));
        }
    }

    public static String getIdentifier(UIView view) {
        NSString value = (NSString) view.getAssociatedObject(IDENTIFIER_KEY);
        return value != null ? value.toString() : null;
    }

    public static void setIdentifier(UIView view, String identifier) {
        view.setAssociatedObject(IDENTIFIER_KEY, new NSString(identifier));
    }

    public static UIEdgeInsets getPadding(UIView view) {
        NSValue value = (NSValue) view.getAssociatedObject(PADDING_KEY);
        return value != null ? value.edgeInsetsValue() : new UIEdgeInsets();
    }

    public static void setPadding(UIView view, UIEdgeInsets padding) {
        UIEdgeInsets prevPadding = getPadding(view);
        if(!prevPadding.equalsTo(padding)) {
            view.setAssociatedObject(PADDING_KEY, NSValue.valueOf(padding));
            requestLayout(view);
        }

        if (view instanceof UIButton)
            UIButtonLayoutUtil.setPadding((UIButton) view, padding);
    }

    public static LayoutParams getLayoutParams(UIView view) {
        LayoutParams nsValue = (LayoutParams) view.getAssociatedObject(LAYOUT_PARAMS_KEY);
        return nsValue != null ? nsValue : null;
    }

    public static void setLayoutParams(UIView view, LayoutParams layoutParams) {
        view.setAssociatedObject(LAYOUT_PARAMS_KEY, layoutParams);
    }

    public static ViewVisibility getVisibility(UIView view) {
        NSNumber nsNumber = (NSNumber) view.getAssociatedObject(VISIBILITY_KEY);
        ViewVisibility visibility = null;

        if (nsNumber != null) {
            visibility = ViewVisibility.valueOf(nsNumber.intValue());

            if(visibility == ViewVisibility.Visible && view.isHidden()) {
                // Visibility has been set independently
                visibility = ViewVisibility.Invisible;
            }
        }
        return visibility;
    }

    public static void setVisibility(UIView view, ViewVisibility visibility) {
        ViewVisibility curVisibility = getVisibility(view);

        view.setHidden(visibility != ViewVisibility.Visible);

        NSNumber newVisibility = NSNumber.valueOf(visibility.getValue());
        view.setAssociatedObject(VISIBILITY_KEY, newVisibility);

        if((curVisibility != visibility) && (curVisibility == ViewVisibility.Gone || visibility == ViewVisibility.Gone)) {
            requestLayout(view);
        }
    }

    public static CGSize getMinSize(UIView view) {
        NSValue nsValue = (NSValue) view.getAssociatedObject(MIN_SIZE_KEY);
        return nsValue != null ? nsValue.sizeValue() : null;
    }

    public static void setMinSize(UIView view, CGSize size) {
        view.setAssociatedObject(MIN_SIZE_KEY, NSValue.valueOf(size));
    }

    public static CGSize getSuggestedMinimumSize(UIView view) {
        return getMinSize(view);
    }

    public static LayoutMeasuredSize getMeasuredDimensionSize(UIView view) {
        NSValue nsValue = (NSValue) view.getAssociatedObject(MEASURED_SIZE_KEY);
        return nsValue != null ?(LayoutMeasuredSize) nsValue.objectValue() : null;
    }

    public static void setMeasuredDimensionSize(UIView view, LayoutMeasuredSize layoutMeasuredSize) {
        view.setAssociatedObject(MEASURED_SIZE_KEY, NSValue.valueOf(layoutMeasuredSize));
    }

    public static void requestLayout(UIView view) {
        view.setNeedsLayout();
        setIsLayoutRequested(view, true);

        if(view.getSuperview() != null) {
            if(!isLayoutRequested(view.getSuperview())) {
                requestLayout(view.getSuperview());
            }
        }
    }

    public static boolean isLayoutRequested(UIView view) {
        NSNumber value = (NSNumber) view.getAssociatedObject(IS_LAYOUT_REQUESTED_KEY);
        return value != null && value.booleanValue();
    }

    public static void setIsLayoutRequested(UIView view, boolean isRequested) {
        view.setAssociatedObject(IS_LAYOUT_REQUESTED_KEY, NSNumber.valueOf(isRequested));
    }

    public static CGSize getMeasuredSize(UIView view) {
        LayoutMeasuredSize size = getMeasuredDimensionSize(view);
        return size != null ? new CGSize(size.getWidth().getSize(), size.getHeight().getSize()) : CGSize.Zero();
    }

    public static void measure(UIView view, LayoutMeasureSpec widthMeasureSpec, LayoutMeasureSpec heightMeasureSpec) {
        onMeasure(view, widthMeasureSpec, heightMeasureSpec);
    }

    public static void onMeasure(UIView view, LayoutMeasureSpec widthMeasureSpec, LayoutMeasureSpec heightMeasureSpec) {
        if(view instanceof LayoutViewDelegate) {
            LayoutViewDelegate lvd = (LayoutViewDelegate) view;
            lvd.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else if(view instanceof UIButton) {
            UIButtonLayoutUtil.measure((UIButton) view, widthMeasureSpec, heightMeasureSpec);
        } else if(view instanceof UIImageView) {
            UIImageViewLayoutUtil.measure((UIImageView) view, widthMeasureSpec, heightMeasureSpec);
        } else {
            defaultMeasure(view, widthMeasureSpec, heightMeasureSpec);
        }
    }

    public static void defaultMeasure(UIView view, LayoutMeasureSpec widthMeasureSpec, LayoutMeasureSpec heightMeasureSpec) {
        CGSize minSize = getSuggestedMinimumSize(view);

        LayoutMeasuredSize size = new LayoutMeasuredSize();
        size.setWidth(LayoutMeasuredDimension.getDefaultSize(minSize.getWidth(), widthMeasureSpec));
        size.setHeight(LayoutMeasuredDimension.getDefaultSize(minSize.getHeight(), heightMeasureSpec));

        setMeasuredDimensionSize(view, size);
    }

    public static void onLayout(UIView view, CGRect frame, boolean changed) {
        if(view instanceof LayoutViewDelegate) {
            LayoutViewDelegate lvd = (LayoutViewDelegate) view;
            lvd.onLayout(frame, changed);
        }
    }

    public static CGRect roundFrame(CGRect frame) {
        frame.getOrigin().setX(Math.ceil(frame.getOrigin().getX()));
        frame.getOrigin().setY(Math.ceil(frame.getOrigin().getY()));

        frame.getSize().setWidth(Math.ceil(frame.getSize().getWidth()));
        frame.getSize().setHeight(Math.ceil(frame.getSize().getHeight()));

        return frame;
    }

    public static void layout(UIView view, CGRect frame) {
        setIsLayoutRequested(view, false);

        CGRect oldFrame = view.getFrame();
        CGRect newFrame = roundFrame(frame);

        boolean changed = !oldFrame.equalsTo(newFrame);
        if(changed) {
            view.setFrame(newFrame);
        }

        onLayout(view, frame, changed);
    }

    public static double getBaseLine(UIView view) {
        return -1;
    }

    public static LayoutMeasuredWidthHeightState getMeasuredState(UIView view) {
        LayoutMeasuredWidthHeightState result = new LayoutMeasuredWidthHeightState();
        LayoutMeasuredSize measuredSize = getMeasuredDimensionSize(view);

        if (measuredSize != null) {
            result.setWidthState(measuredSize.getWidth().getState());
            result.setHeightState(measuredSize.getHeight().getState());
        }

        return result;
    }

    public static UIView findViewById(UIView view, String identifier) {
        UIView result = null;
        String currIdentifier = getIdentifier(view);

        if(UIViewViewGroupUtil.isViewGroup(view)) {
            result = UIViewViewGroupUtil.findViewTraversal(view, identifier);
        } else if(currIdentifier != null && currIdentifier.equals(identifier)) {
            result = view;
        }
        return result;
    }

    public static LayoutParams generateDefaultLayoutParams(UIView view) {
        LayoutParams result = null;

        if(view instanceof LayoutParamsDelegate) {
            result = ((LayoutParamsDelegate) view).generateDefaultLayoutParams();
        } else {
            result = LayoutParams.generate();
        }

        return result;
    }

    public static LayoutParams generateLayoutParams(UIView view, LayoutParams params) {
        LayoutParams result = params;

        if(view instanceof LayoutParamsDelegate) {
            result = ((LayoutParamsDelegate) view).generateLayoutParams(params);
        }

        return result;
    }

    public static LayoutParams generateLayoutParams(UIView view, Map<String, String> attrs) {
        LayoutParams result = null;

        if(view instanceof LayoutParamsDelegate) {
            result = ((LayoutParamsDelegate) view).generateLayoutParams(attrs);
        } else {
            result = new LayoutParams(attrs);
        }
        return result;
    }

    public static boolean checkLayoutParams(UIView view, LayoutParams layoutParams) {
        boolean result = layoutParams != null;

        if(view instanceof LayoutParamsDelegate) {
            result = ((LayoutParamsDelegate) view).checkLayoutParams(layoutParams);
        }

        return result;
    }

    public static void onFinishInflate(UIView view) {
        if(view instanceof InflatedViewDelegate) {
            ((InflatedViewDelegate)view).onFinishInflate();
        }
    }
}
