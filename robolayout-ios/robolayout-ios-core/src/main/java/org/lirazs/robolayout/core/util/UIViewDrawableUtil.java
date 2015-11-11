package org.lirazs.robolayout.core.util;

import org.lirazs.robolayout.core.resource.drawable.Drawable;
import org.lirazs.robolayout.core.resource.drawable.DrawableLayer;
import org.lirazs.robolayout.core.view.DrawableBackgroundDelegate;
import org.apache.commons.io.FilenameUtils;
import org.robovm.apple.coreanimation.CALayer;
import org.robovm.apple.coregraphics.*;
import org.robovm.apple.foundation.*;
import org.robovm.apple.uikit.*;
import org.robovm.objc.Selector;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by mac on 7/29/15.
 */
public class UIViewDrawableUtil {
    private static String BACKGROUND_DRAWABLE_KEY = "BACKGROUND_DRAWABLE_KEY";

    public static void setBackgroundDrawable(UIView view, Drawable backgroundDrawable) {
        view.setAssociatedObject(BACKGROUND_DRAWABLE_KEY, NSValue.valueOf(backgroundDrawable));
        if(backgroundDrawable.hasPadding()) {
            UIViewLayoutUtil.setPadding(view, backgroundDrawable.getPadding());
        }
        onBackgroundDrawableChanged(view);
    }
    public static Drawable getBackgroundDrawable(UIView view) {
        NSValue nsValue = (NSValue) view.getAssociatedObject(BACKGROUND_DRAWABLE_KEY);
        return nsValue != null ? (Drawable) nsValue.objectValue() : null;
    }

    public static void onBackgroundDrawableChanged(final UIView view) {
        if(view instanceof DrawableBackgroundDelegate) {
            ((DrawableBackgroundDelegate) view).onBackgroundDrawableChanged();
        } else {
            onDefaultBackgroundDrawableChanged(view);
        }
    }

    private static void onDefaultBackgroundDrawableChanged(final UIView view) {
        DrawableLayer existingBackgroundLayer = null;
        CALayer layer = view.getLayer();

        if (layer.getSublayers() != null) {
            for (CALayer subLayer : layer.getSublayers()) {
                if(subLayer instanceof DrawableLayer) {
                    existingBackgroundLayer = (DrawableLayer) subLayer;
                    break;
                }
            }
        }

        Drawable drawable = getBackgroundDrawable(view);
        drawable.setBounds(view.getBounds());

        String backgroundDrawableFrameTag = "backgroundDrawableFrame";
        String backgroundDrawableStateTag = "backgroundDrawableState";

        if(drawable != null) {
            if(view instanceof UIControl) {
                UIControl control = (UIControl) view;
                drawable.setState(control.getState());
            } else {
                drawable.setState(UIControlState.Normal);
            }

            if(existingBackgroundLayer == null) {
                existingBackgroundLayer = new DrawableLayer();
                view.getLayer().insertSublayerAt(existingBackgroundLayer, 0);
            }

            existingBackgroundLayer.setDrawable(drawable);
            existingBackgroundLayer.setFrame(view.getBounds());
            existingBackgroundLayer.setNeedsDisplay();

            if(!NSKeyValueObserverUtil.hasObserver(view, backgroundDrawableFrameTag)) {
                final DrawableLayer finalExistingBackgroundLayer = existingBackgroundLayer;

                NSKeyValueObserverUtil.addObserver(view, new NSKeyValueObserver() {
                    @Override
                    public void observeValue(String s, NSObject nsObject, NSKeyValueChangeInfo nsKeyValueChangeInfo) {
                        finalExistingBackgroundLayer.setFrame(view.getBounds());
                    }
                }, backgroundDrawableFrameTag, Collections.singletonList("frame"), NSKeyValueObservingOptions.New);

                if(view instanceof  UIControl && !NSKeyValueObserverUtil.hasObserver(view, backgroundDrawableStateTag)) {

                    NSKeyValueObserverUtil.addObserver(view, new NSKeyValueObserver() {
                        @Override
                        public void observeValue(String s, NSObject nsObject, NSKeyValueChangeInfo nsKeyValueChangeInfo) {
                            Drawable backgroundDrawable = getBackgroundDrawable(view);
                            UIControl control = (UIControl) view;
                            backgroundDrawable.setState(control.getState());
                        }
                    }, backgroundDrawableStateTag, Arrays.asList("highlighted", "enabled", "selected"), NSKeyValueObservingOptions.New);
                }
            }
        } else {
            NSKeyValueObserverUtil.removeObserver(view, backgroundDrawableFrameTag);
            NSKeyValueObserverUtil.removeObserver(view, backgroundDrawableStateTag);

            existingBackgroundLayer.removeFromSuperlayer();
        }
    }
}
