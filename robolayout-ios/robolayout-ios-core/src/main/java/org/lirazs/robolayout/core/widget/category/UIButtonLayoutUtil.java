package org.lirazs.robolayout.core.widget.category;

import org.lirazs.robolayout.core.resource.ResourceManager;
import org.lirazs.robolayout.core.resource.state.ColorStateList;
import org.lirazs.robolayout.core.resource.state.DrawableStateItem;
import org.lirazs.robolayout.core.resource.state.DrawableStateList;
import org.lirazs.robolayout.core.resource.state.ResourceStateItem;
import org.lirazs.robolayout.core.util.ColorParser;
import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.lirazs.robolayout.core.view.*;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.*;

import java.util.Map;

/**
 * Created by mac on 7/29/15.
 */
public class UIButtonLayoutUtil {

    public static void applyAttributes(UIButton button, Map<String, String> attrs, NSObject actionTarget) {
        /*NSString *backgroundString = [attrs objectForKey:@"background"];
        if (backgroundString != nil) {
            NSMutableDictionary *mutableAttrs = [NSMutableDictionary dictionaryWithDictionary:attrs];
            [mutableAttrs removeObjectForKey:@"background"];
            attrs = mutableAttrs;
        }*/

        UIControlLayoutUtil.applyAttributes(button, attrs, actionTarget);

        String text = attrs.get("text");

        if(ResourceManager.getCurrent().isValidIdentifier(text)) {
            String title = ResourceManager.getCurrent().getString(text);
            button.setTitle(title, UIControlState.Normal);
        } else {
            button.setTitle(text, UIControlState.Normal);
        }

        String textColor = attrs.get("textColor");
        if(textColor != null && !textColor.isEmpty()) {
            ColorStateList colorStateList = ResourceManager.getCurrent().getColorStateList(textColor);
            if(colorStateList != null) {
                for (ResourceStateItem item : colorStateList.getItems()) {
                    button.setTitleColor(item.getColor(), item.getControlState());
                }
            } else {
                UIColor color = ColorParser.getColorFromColorString(textColor);
                if(color != null) {
                    button.setTitleColor(color, UIControlState.Normal);
                }
            }
        }

        String fontName = attrs.get("font");
        String textSize = attrs.get("textSize");
        if(fontName != null) {
            double size = button.getTitleLabel().getFont().getPointSize();
            if(textSize != null) {
                size = Double.parseDouble(textSize);
            }
            button.getTitleLabel().setFont(UIFont.getFont(fontName, size));

        } else if(textSize != null) {
            double size = Double.parseDouble(textSize);
            button.getTitleLabel().setFont(UIFont.getSystemFont(size));
        }

        /*if ([backgroundString length] > 0) {
        IDLDrawableStateList *drawableStateList = [[IDLResourceManager currentResourceManager] drawableStateListForIdentifier:backgroundString];
        if (drawableStateList != nil) {
            for (NSInteger i=[drawableStateList.items count]-1; i>=0; i--) {
                IDLDrawableStateItem *item = [drawableStateList.items objectAtIndex:i];
                [self setBackgroundImage:item.image forState:item.controlState];
            }
        } else {
            UIColor *color = [UIColor colorFromIDLColorString:backgroundString];
            if (color != nil) {
                UIImage *image = [UIImage idl_imageFromColor:color withSize:CGSizeMake(1, 1)];
                [self setBackgroundImage:image forState:UIControlStateNormal];
            }
        }
        }*/

        String imageString = attrs.get("image");
        if(imageString != null && !imageString.isEmpty()) {
            DrawableStateList drawableStateList = ResourceManager.getCurrent().getDrawableStateList(imageString);
            if(drawableStateList != null) {
                for (ResourceStateItem item : drawableStateList.getItems()) {
                    DrawableStateItem drawableItem = (DrawableStateItem) item;
                    button.setBackgroundImage(drawableItem.getImage(), drawableItem.getControlState());
                }
            }
        }
    }

    public static void measure(UIButton view, LayoutMeasureSpec widthMeasureSpec, LayoutMeasureSpec heightMeasureSpec) {
        LayoutMeasureSpecMode widthMode = widthMeasureSpec.getMode();
        LayoutMeasureSpecMode heightMode = heightMeasureSpec.getMode();

        double widthSize = widthMeasureSpec.getSize();
        double heightSize = heightMeasureSpec.getSize();

        LayoutMeasuredSize measuredSize = new LayoutMeasuredSize();
        measuredSize.getWidth().setState(LayoutMeasuredState.None);
        measuredSize.getHeight().setState(LayoutMeasuredState.None);

        UIEdgeInsets padding = UIViewLayoutUtil.getPadding(view);

        if(widthMode == LayoutMeasureSpecMode.Exactly) {
            measuredSize.getWidth().setSize(widthSize);
        } else {
            //CGSize size = NSStringExtensions.getSize(view.getCurrentTitle().length(), view.getTitleLabel().getFont());
            CGSize size = NSString.getSize(view.getCurrentTitle(), new NSAttributedStringAttributes().setFont(view.getTitleLabel().getFont()));
            measuredSize.getWidth().setSize(Math.ceil(size.getWidth()) + padding.getLeft() + padding.getRight());

            if(widthMode == LayoutMeasureSpecMode.AtMost) {
                measuredSize.getWidth().setSize(Math.min(measuredSize.getWidth().getSize(), widthSize));
            }
        }
        CGSize minSize = UIViewLayoutUtil.getMinSize(view);
        measuredSize.getWidth().setSize(Math.max(measuredSize.getWidth().getSize(), minSize.getWidth()));

        if(heightMode == LayoutMeasureSpecMode.Exactly) {
            measuredSize.getHeight().setSize(heightSize);
        } else {
            CGSize cgSize = new CGSize(measuredSize.getWidth().getSize() - padding.getLeft() - padding.getRight(), Double.MAX_VALUE);
            //CGSize size = NSStringExtensions.getSize(view.getCurrentTitle().length(), view.getTitleLabel().getFont(), cgSize, view.getTitleLabel().getLineBreakMode());

            //TODO: Calling NSStringExtensions.getBoundingRect crashes the application
            /*CGSize size = NSStringExtensions.getBoundingRect(
                    view.getCurrentTitle().length(),
                    cgSize,
                    NSStringDrawingOptions.UsesLineFragmentOrigin,
                    null,
                    null
            ).getSize();*/
            CGSize size = NSString.getSize(view.getCurrentTitle(), new NSAttributedStringAttributes().setFont(view.getTitleLabel().getFont()));

            measuredSize.getHeight().setSize(Math.ceil(size.getHeight()) + padding.getTop() + padding.getBottom());
            if(heightMode == LayoutMeasureSpecMode.AtMost) {
                measuredSize.getHeight().setSize(Math.min(measuredSize.getHeight().getSize(), heightSize));
            }
        }
        measuredSize.getHeight().setSize(Math.max(measuredSize.getHeight().getSize(), minSize.getHeight()));

        UIViewLayoutUtil.setMeasuredDimensionSize(view, measuredSize);
    }

    public static void setGravity(UIButton button, ViewContentGravity gravity) {
        UIControlLayoutUtil.setGravity(button, gravity);

        if((gravity.value() & ViewContentGravity.Top.value()) == ViewContentGravity.Top.value()) {
            button.setContentVerticalAlignment(UIControlContentVerticalAlignment.Top);
        } else if((gravity.value() & ViewContentGravity.Bottom.value()) == ViewContentGravity.Bottom.value()) {
            button.setContentVerticalAlignment(UIControlContentVerticalAlignment.Bottom);
        } else if((gravity.value() & ViewContentGravity.FillVertical.value()) == ViewContentGravity.FillVertical.value()) {
            button.setContentVerticalAlignment(UIControlContentVerticalAlignment.Fill);
        }
    }

    public static void setPadding(UIButton button, UIEdgeInsets padding) {
        button.setContentEdgeInsets(padding);
    }
}
