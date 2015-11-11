package org.lirazs.robolayout.core.widget;

import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.lirazs.robolayout.core.view.*;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.*;
import org.robovm.objc.Selector;

import java.util.Map;

/**
 * Created on 8/4/2015.
 */
public class TextView extends Label implements LayoutViewDelegate {

    public TextView(CGRect frame) {
        super(frame);
    }

    public TextView(Map<String, String> attrs) {
        super(attrs);
    }

    private UIControlContentVerticalAlignment contentVerticalAlignment;

    public UIControlContentVerticalAlignment getContentVerticalAlignment() {
        return contentVerticalAlignment;
    }

    public void setContentVerticalAlignment(UIControlContentVerticalAlignment contentVerticalAlignment) {
        this.contentVerticalAlignment = contentVerticalAlignment;
        setNeedsDisplay();
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
            CGSize size = null;
            /*if(respondsToSelector(Selector.register("attributedText"))) {
                size = NSStringExtensions.getBoundingRect(
                        getAttributedText().length(),
                        new CGSize(Double.MAX_VALUE, Double.MAX_VALUE),
                        NSStringDrawingOptions.UsesLineFragmentOrigin,
                        null,
                        null
                ).getSize();
            } else {*/
                size = NSString.getSize(getText(), new NSAttributedStringAttributes().setFont(getFont()));
            //}
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
            CGSize size = null;
            CGSize cgSize = new CGSize(measuredSize.getWidth().getSize() - padding.getLeft() - padding.getRight(), Double.MAX_VALUE);

            //TODO: Calling NSStringExtensions.getBoundingRect & respondsToSelector crashes the application
            /*if(respondsToSelector(Selector.register("attributedText"))) {
                size = NSStringExtensions.getBoundingRect(
                        getAttributedText().length(),
                        cgSize,
                        NSStringDrawingOptions.UsesLineFragmentOrigin,
                        null,
                        null
                ).getSize();
            } else {*/
                //TODO: Still waiting for RoboVM community to understand predefined size as well - cgSize
                //size = NSStringExtensions.getSize(getText().length(), getFont(), cgSize);
                //TODO: Calling NSStringExtensions.getBoundingRect & respondsToSelector crashes the application
                /*size = NSStringExtensions.getBoundingRect(
                            getAttributedText().length(),
                            cgSize,
                            NSStringDrawingOptions.UsesLineFragmentOrigin,
                            null,
                            null
                    ).getSize();*/
                size = NSString.getSize(getText(), new NSAttributedStringAttributes().setFont(getFont()));
            //}

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

    public void setGravity(ViewContentGravity gravity) {
        super.setGravity(gravity);

        if((gravity.value() & ViewContentGravity.Top.value()) == ViewContentGravity.Top.value()) {
            setContentVerticalAlignment(UIControlContentVerticalAlignment.Top);
        } else if((gravity.value() & ViewContentGravity.Bottom.value()) == ViewContentGravity.Bottom.value()) {
            setContentVerticalAlignment(UIControlContentVerticalAlignment.Bottom);
        } else if((gravity.value() & ViewContentGravity.FillVertical.value()) == ViewContentGravity.FillVertical.value()) {
            setContentVerticalAlignment(UIControlContentVerticalAlignment.Fill);
        }
    }

    public ViewContentGravity getGravity() {
        ViewContentGravity result = super.getGravity();

        switch (getContentVerticalAlignment()) {
            case Top:
                result.set(ViewContentGravity.Top);
                break;
            case Bottom:
                result.set(ViewContentGravity.Bottom);
                break;
            case Center:
                result.set(ViewContentGravity.CenterVertical);
                break;
            case Fill:
                result.set(ViewContentGravity.FillVertical);
                break;
        }
        return result;
    }

    @Override
    public CGRect getTextRect(CGRect bounds, long numberOfLines) {
        bounds = bounds.inset(UIViewLayoutUtil.getPadding(this));

        CGRect rect = super.getTextRect(bounds, numberOfLines);
        CGRect result = bounds;

        double rectWidth = rect.getSize().getWidth();
        double rectHeight = rect.getSize().getHeight();

        double rectX = rect.getOrigin().getX();

        double boundsY = bounds.getOrigin().getY();
        double boundsHeight = bounds.getSize().getHeight();

        if (contentVerticalAlignment != null) {
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
        }
        return result;
    }

    @Override
    public void drawText(CGRect cgRect) {

        //if(respondsToSelector(Selector.register("sizeThatFits:"))) {
            /*cgRect = cgRect.inset(UIViewLayoutUtil.getPadding(this));

            CGRect result;
            CGSize sizeThatFits = getSizeThatFits(cgRect.getSize());

            double rectWidth = cgRect.getSize().getWidth();
            double rectHeight = cgRect.getSize().getHeight();

            double rectX = cgRect.getOrigin().getX();
            double rectY = cgRect.getOrigin().getY();

            switch (contentVerticalAlignment) {
                case Top:
                    result = new CGRect(rectX, rectY, rectWidth, sizeThatFits.getHeight());
                    break;
                case Bottom:
                    result = new CGRect(rectX, rectY + (rectHeight - sizeThatFits.getHeight()), rectWidth, sizeThatFits.getHeight());
                    break;
                default:
                    result = cgRect;
                    break;
            }

            super.drawText(result);*/
        //} else {
            CGRect r = getTextRect(cgRect, getNumberOfLines());
            super.drawText(r);
        //}
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
    public void setLineBreakMode(NSLineBreakMode nsLineBreakMode) {
        super.setLineBreakMode(nsLineBreakMode);

        UIViewLayoutUtil.requestLayout(this);
    }
}
