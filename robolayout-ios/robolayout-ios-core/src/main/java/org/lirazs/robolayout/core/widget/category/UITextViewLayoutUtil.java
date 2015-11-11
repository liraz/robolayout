package org.lirazs.robolayout.core.widget.category;

import org.robovm.apple.uikit.UIEdgeInsets;
import org.robovm.apple.uikit.UITextView;
import org.robovm.apple.uikit.UIView;

/**
 * Created by mac on 7/29/15.
 */
public class UITextViewLayoutUtil {

    public static void setPadding(UITextView view, UIEdgeInsets padding) {
        view.setContentInset(padding);
    }

    public static boolean isViewGroup(UIView view) {
        return false;
    }
}
