package org.lirazs.robolayout.core.widget;

import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.lirazs.robolayout.core.util.UIViewViewGroupUtil;
import org.lirazs.robolayout.core.view.*;
import org.lirazs.robolayout.core.widget.category.UIControlLayoutUtil;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.*;

import java.util.Map;

/**
 * Created on 8/4/2015.
 */
public class EditText extends UITextField implements LayoutViewDelegate {

    private UIControlContentVerticalAlignment contentVerticalAlignment;

    public EditText(Map<String, String> attrs, NSObject actionTarget) {
        super();
        UIControlLayoutUtil.applyAttributes(this, attrs, actionTarget);
    }

    @Override
    public UIControlContentVerticalAlignment getContentVerticalAlignment() {
        return contentVerticalAlignment;
    }

    @Override
    public void setContentVerticalAlignment(UIControlContentVerticalAlignment contentVerticalAlignment) {
        //super.setContentVerticalAlignment(contentVerticalAlignment);

        this.contentVerticalAlignment = contentVerticalAlignment;
        setNeedsDisplay();
    }

    public CGRect getTextRect(CGRect bounds) {
        bounds = bounds.inset(UIViewLayoutUtil.getPadding(this));

        CGRect rect = super.getTextRect(bounds);
        CGRect result = null;

        double rectWidth = rect.getSize().getWidth();
        double rectHeight = rect.getSize().getHeight();

        double rectX = rect.getOrigin().getX();

        double boundsY = bounds.getOrigin().getY();
        double boundsHeight = bounds.getSize().getHeight();

        switch (contentVerticalAlignment) {
            case Top:
                result = new CGRect(rectX, boundsY, rectWidth, rectHeight);
                break;

            case Center:
                result = new CGRect(rectX,
                        boundsY + (boundsHeight - rectHeight) / 2, rectWidth, rectHeight);
                break;

            case Bottom:
                result = new CGRect(rectX, boundsY + (boundsHeight - rectHeight), rectWidth, rectHeight);
                break;

            default:
                result = bounds;
                break;
        }
        return result;
    }

    @Override
    public CGRect getEditingRect(CGRect bounds) {
        return getTextRect(bounds);
    }

    @Override
    public CGRect getPlaceholderRect(CGRect bounds) {
        return getTextRect(bounds);
    }

    @Override
    public void drawText(CGRect cgRect) {
        CGRect r = getTextRect(cgRect);
        super.drawText(r);
    }

    @Override
    public void drawPlaceholder(CGRect cgRect) {
        CGRect r = getPlaceholderRect(cgRect);
        super.drawPlaceholder(r);
    }

    @Override
    public void setText(String s) {
        super.setText(s);
        UIViewLayoutUtil.requestLayout(this);
    }

    @Override
    public void setFont(UIFont uiFont) {
        super.setFont(uiFont);
        UIViewLayoutUtil.requestLayout(this);
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

        UIEdgeInsets padding = UIViewLayoutUtil.getPadding(this);

        if(widthMode == LayoutMeasureSpecMode.Exactly) {
            measuredSize.getWidth().setSize(widthSize);
        } else {
            CGSize size = NSString.getSize(getText(), new NSAttributedStringAttributes().setFont(getFont()));
            measuredSize.getWidth().setSize(Math.ceil(size.getWidth()) + padding.getLeft() + padding.getRight());

            if(widthMode == LayoutMeasureSpecMode.AtMost) {
                measuredSize.getWidth().setSize(Math.min(measuredSize.getWidth().getSize(), widthSize));
            }
        }
        CGSize minSize = UIViewLayoutUtil.getMinSize(this);
        measuredSize.getWidth().setSize(Math.max(measuredSize.getWidth().getSize(), minSize.getWidth()));

        if(heightMode == LayoutMeasureSpecMode.Exactly) {
            measuredSize.getHeight().setSize(heightSize);
        } else {
            CGSize cgSize = new CGSize(measuredSize.getWidth().getSize() - padding.getLeft() - padding.getRight(), Double.MAX_VALUE);
            //CGSize size = NSStringExtensions.getSize(getText().length(), getFont(), cgSize);
            //TODO: Still waiting for RoboVM community to understand size as well - cgSize
            CGSize size = NSString.getSize(getText(), new NSAttributedStringAttributes().setFont(getFont()));

            measuredSize.getHeight().setSize(Math.max(Math.ceil(size.getHeight()), getFont().getLineHeight()) + padding.getTop() + padding.getBottom());
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
}
