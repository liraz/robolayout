package org.lirazs.robolayout.core.util;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIScrollView;
import org.robovm.apple.uikit.UIView;

import java.util.Map;

/**
 * Created by mac on 7/29/15.
 */
public class UIViewLayoutBridgeUtil {


    public static UIView findAndScrollToFirstResponder(UIView view) {
        UIView result = null;
        if(view.isFirstResponder()) {
            result = view;
        }

        for (UIView subView : view.getSubviews()) {
            UIView firstResponder = findAndScrollToFirstResponder(subView);
            if(firstResponder != null) {
                if(view instanceof UIScrollView) {
                    UIScrollView sv = (UIScrollView) view;
                    CGRect r = view.convertRectFromView(firstResponder.getFrame(), firstResponder);
                    sv.scrollRectToVisible(r, false);

                    result = view;
                } else {
                    result = firstResponder;
                }
                break;
            }
        }

        return result;
    }
}
