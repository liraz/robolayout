package org.lirazs.robolayout.core.widget.category;

import org.lirazs.robolayout.core.resource.ResourceManager;
import org.lirazs.robolayout.core.resource.state.DrawableStateList;
import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.lirazs.robolayout.core.view.LayoutMeasureSpec;
import org.lirazs.robolayout.core.view.LayoutMeasureSpecMode;
import org.lirazs.robolayout.core.view.LayoutMeasuredSize;
import org.lirazs.robolayout.core.view.LayoutMeasuredState;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.*;

import java.util.Map;

/**
 * Created by mac on 7/29/15.
 */
public class UIImageViewLayoutUtil {

    public static void applyAttributes(UIImageView imageView, Map<String, String> attrs, NSObject actionTarget) {
        UIViewLayoutUtil.applyAttributes(imageView, attrs);

        String imageRes = attrs.get("src");
        DrawableStateList drawableStateList = ResourceManager.getCurrent().getDrawableStateList(imageRes);
        if(drawableStateList != null) {
            imageView.setImage(drawableStateList.getImageForControlState(UIControlState.Normal));

            UIImage highlightedImage = drawableStateList.getImageForControlState(UIControlState.Highlighted);
            if(highlightedImage != null) {
                imageView.setImage(highlightedImage);
            }
        }

        String scaleType = attrs.get("scaleType");
        if(scaleType != null) {
            if(scaleType.equals("center")) {
                imageView.setContentMode(UIViewContentMode.Center);

            } else if(scaleType.equals("centerCrop")) {
                imageView.setContentMode(UIViewContentMode.ScaleAspectFill);
                imageView.setClipsToBounds(true);

            } else if(scaleType.equals("centerInside")) {
                imageView.setContentMode(UIViewContentMode.ScaleAspectFit);

            } else if(scaleType.equals("fitXY")) {
                imageView.setContentMode(UIViewContentMode.ScaleToFill);

            } else if(scaleType.equals("top")) {
                imageView.setContentMode(UIViewContentMode.Top);

            } else if(scaleType.equals("topLeft")) {
                imageView.setContentMode(UIViewContentMode.TopLeft);

            } else if(scaleType.equals("topRight")) {
                imageView.setContentMode(UIViewContentMode.TopRight);

            } else if(scaleType.equals("left")) {
                imageView.setContentMode(UIViewContentMode.Left);

            } else if(scaleType.equals("right")) {
                imageView.setContentMode(UIViewContentMode.Right);

            } else if(scaleType.equals("bottom")) {
                imageView.setContentMode(UIViewContentMode.Bottom);

            } else if(scaleType.equals("bottomLeft")) {
                imageView.setContentMode(UIViewContentMode.BottomLeft);

            } else if(scaleType.equals("bottomRight")) {
                imageView.setContentMode(UIViewContentMode.BottomRight);
            }
        }
    }

    public static boolean isImageScaling(UIImageView imageView) {
        return (imageView.getContentMode() == UIViewContentMode.ScaleAspectFill) ||
                (imageView.getContentMode() == UIViewContentMode.ScaleAspectFit) ||
                (imageView.getContentMode() == UIViewContentMode.ScaleToFill);
    }

    public static void measure(UIImageView view, LayoutMeasureSpec widthMeasureSpec, LayoutMeasureSpec heightMeasureSpec) {
        LayoutMeasureSpecMode widthMode = widthMeasureSpec.getMode();
        LayoutMeasureSpecMode heightMode = heightMeasureSpec.getMode();

        double widthSize = widthMeasureSpec.getSize();
        double heightSize = heightMeasureSpec.getSize();

        UIImage image = view.getImage();
        CGSize imageSize = image != null ? image.getSize() : CGSize.Zero();

        LayoutMeasuredSize measuredSize = new LayoutMeasuredSize();
        measuredSize.getWidth().setSize(imageSize.getWidth());
        measuredSize.getWidth().setState(LayoutMeasuredState.None);
        measuredSize.getHeight().setSize(imageSize.getHeight());
        measuredSize.getHeight().setState(LayoutMeasuredState.None);

        switch(widthMode) {
            case Exactly:
                measuredSize.getWidth().setSize(widthSize);
                if(isImageScaling(view)) {
                    if(imageSize.getWidth() <= 0.d) {
                        measuredSize.getHeight().setSize(0);
                    } else {
                        double size = (measuredSize.getWidth().getSize() / imageSize.getWidth()) * imageSize.getHeight();
                        measuredSize.getHeight().setSize(size);
                    }
                }
                break;

            case AtMost:
                if(widthSize < imageSize.getWidth()) {
                    measuredSize.getWidth().setSize(widthSize);

                    if(isImageScaling(view)) {
                        if(imageSize.getWidth() <= 0.d) {
                            measuredSize.getHeight().setSize(0.d);
                        } else {
                            double size = (measuredSize.getWidth().getSize() / imageSize.getWidth()) * imageSize.getHeight();
                            measuredSize.getHeight().setSize(size);
                        }
                    }
                }
                break;

            case Unspecified:
            default:
                    break;
        }

        switch (heightMode) {
            case Exactly:
                measuredSize.getHeight().setSize(heightSize);
                break;

            case AtMost:
                measuredSize.getHeight().setSize(Math.min(heightSize, measuredSize.getHeight().getSize()));
                break;
            case Unspecified:
            default:
                break;
        }

        double size = Math.min(measuredSize.getWidth().getSize(), (imageSize.getHeight() > 0.d ? (measuredSize.getHeight().getSize() / imageSize.getHeight()) * imageSize.getWidth() : 0.d));
        measuredSize.getWidth().setSize(size);

        UIViewLayoutUtil.setMeasuredDimensionSize(view, measuredSize);
    }
}
