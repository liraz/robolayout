package org.lirazs.robolayout.core.widget;

import org.lirazs.robolayout.core.resource.drawable.Drawable;
import org.lirazs.robolayout.core.resource.drawable.DrawableDelegate;
import org.lirazs.robolayout.core.resource.state.ColorStateList;
import org.lirazs.robolayout.core.util.*;
import org.lirazs.robolayout.core.view.*;
import org.robovm.apple.coregraphics.CGContext;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.*;
import org.robovm.apple.uikit.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Created on 8/7/2015.
 */
public class Label extends UILabel implements LayoutViewDelegate, DrawableBackgroundDelegate {
    public Label(CGRect frame) {
        super(frame);
    }

    public Label(Map<String, String> attrs) {
        UIViewLayoutUtil.applyAttributes(this, attrs);

        setText(ResourceAttributesUtil.getStringValue(attrs, "text"));
        setGravity(ViewContentGravity.getFromAttribute(attrs.get("gravity")));

        String lines = attrs.get("lines");
        if (lines != null) {
            setNumberOfLines(Long.parseLong(lines));
        }

        ColorStateList textColorStateList = ResourceAttributesUtil.getColorStateListValue(attrs, "textColor");
        if(textColorStateList != null) {
            setTextColor(textColorStateList.createColor(UIControlState.Normal));

            UIColor highlightedColor = textColorStateList.createColor(UIControlState.Highlighted);
            if(highlightedColor != null) {
                setHighlightedTextColor(highlightedColor);
            }
        } else {
            UIColor color = ResourceAttributesUtil.getColorValue(attrs, "textColor");
            if(color != null) {
                setTextColor(color);
            }
        }

        String fontName = attrs.get("font");
        String textSize = attrs.get("textSize");

        if(fontName != null) {
            double size = getFont().getPointSize();
            if(textSize != null) {
                size = Double.parseDouble(textSize);
            }
            setFont(UIFont.getFont(fontName, size));
        } else if(textSize != null) {
            double size = Double.parseDouble(textSize);
            setFont(UIFont.getSystemFont(size));
        }
    }

    public ViewContentGravity getGravity() {
        ViewContentGravity result;
        switch (getTextAlignment()) {
            case Left:
                result = ViewContentGravity.Left;
                break;
            case Right:
                result = ViewContentGravity.Right;
                break;
            case Center:
                result = ViewContentGravity.CenterHorizontal;
                break;
            case Justified:
                result = ViewContentGravity.FillHorizontal;
                break;
            default:
                result = ViewContentGravity.None;
                break;
        }
        return result;
    }

    public void setGravity(ViewContentGravity gravity) {

        if((gravity.value() & ViewContentGravity.Left.value()) == ViewContentGravity.Left.value()) {
            setTextAlignment(NSTextAlignment.Left);
        } else if((gravity.value() & ViewContentGravity.Right.value()) == ViewContentGravity.Right.value()) {
            setTextAlignment(NSTextAlignment.Right);
        } else {
            setTextAlignment(NSTextAlignment.Center);
        }
    }

    public LayoutParams getLayoutParams() {
        return UIViewLayoutUtil.getLayoutParams(this);
    }

    public void setLayoutParams(LayoutParams layoutParams) {
        UIViewLayoutUtil.setLayoutParams(this, layoutParams);
    }

    @Override
    public void onMeasure(LayoutMeasureSpec widthMeasureSpec, LayoutMeasureSpec heightMeasureSpec) {
        LayoutMeasureSpecMode widthMode = widthMeasureSpec.getMode();
        LayoutMeasureSpecMode heightMode = heightMeasureSpec.getMode();

        double widthSize = widthMeasureSpec.getSize();
        double heightSize = heightMeasureSpec.getSize();

        LayoutMeasuredSize measuredSize = new LayoutMeasuredSize();
        measuredSize.getWidth().setState(LayoutMeasuredState.None);
        measuredSize.getHeight().setState(LayoutMeasuredState.None);

        if(widthMode == LayoutMeasureSpecMode.Exactly) {
            measuredSize.getWidth().setSize(widthSize);
        } else {
            CGSize size = NSString.getBoundingRect(getText(), new CGSize(Double.MAX_VALUE, Double.MAX_VALUE),
                    NSStringDrawingOptions.UsesLineFragmentOrigin,
                    new NSAttributedStringAttributes().setFont(getFont()), null).getSize();

            measuredSize.getWidth().setSize(Math.ceil(size.getWidth()));

            if(widthMode == LayoutMeasureSpecMode.AtMost) {
                measuredSize.getWidth().setSize(Math.min(measuredSize.getWidth().getSize(), widthSize));
            }
        }
        CGSize minSize = UIViewLayoutUtil.getMinSize(this);
        measuredSize.getWidth().setSize(Math.max(measuredSize.getWidth().getSize(), minSize.getWidth()));

        if(heightMode == LayoutMeasureSpecMode.Exactly) {
            measuredSize.getHeight().setSize(heightSize);
        } else {
            CGSize size = NSString.getBoundingRect(getText(), new CGSize(measuredSize.getWidth().getSize(), Double.MAX_VALUE),
                    NSStringDrawingOptions.UsesLineFragmentOrigin,
                    new NSAttributedStringAttributes().setFont(getFont()), null).getSize();

            measuredSize.getHeight().setSize(Math.max(Math.ceil(size.getHeight()), getFont().getLineHeight() + getNumberOfLines()));
            if(heightMode == LayoutMeasureSpecMode.AtMost) {
                measuredSize.getHeight().setSize(Math.min(measuredSize.getHeight().getSize(), heightSize));
            }
        }
        measuredSize.getHeight().setSize(Math.max(measuredSize.getHeight().getSize(), minSize.getHeight()));

        UIViewLayoutUtil.setMeasuredDimensionSize(this, measuredSize);
    }

    @Override
    public void onLayout(CGRect frame, boolean changed) {

    }

    @Override
    public void onBackgroundDrawableChanged() {
        final String backgroundDrawableFrameTag = "backgroundDrawableFrame";
        final Drawable drawable = UIViewDrawableUtil.getBackgroundDrawable(this);

        if(drawable != null) {
            drawable.setDelegate(null);
            drawable.setDelegate(new DrawableDelegate() {
                @Override
                public void drawableDidInvalidate(Drawable drawable) {
                    setNeedsDisplay();
                }
            });
            drawable.setState(UIControlState.Normal);
            setBackgroundColor(UIColor.clear());

            if(!NSKeyValueObserverUtil.hasObserver(this, backgroundDrawableFrameTag)) {
                NSKeyValueObserverUtil.addObserver(this, new org.lirazs.robolayout.core.util.NSKeyValueObserver() {
                    @Override
                    public void observeValue(String s, NSObject nsObject, NSKeyValueChangeInfo nsKeyValueChangeInfo) {
                        drawable.setBounds(getBounds());
                        setNeedsDisplay();
                    }
                }, backgroundDrawableFrameTag, Collections.singletonList("frame"), NSKeyValueObservingOptions.New);
            }
        } else {
            NSKeyValueObserverUtil.removeObserver(this, backgroundDrawableFrameTag);
        }
        setNeedsDisplay();
    }

    @Override
    public void drawText(CGRect cgRect) {
        final Drawable drawable = UIViewDrawableUtil.getBackgroundDrawable(this);
        if(drawable != null) {
            CGContext context = UIGraphics.getCurrentContext();
            drawable.setBounds(cgRect);

            drawable.drawInContext(context);
        }

        super.drawText(cgRect);
    }
}
