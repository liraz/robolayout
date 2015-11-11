package org.lirazs.robolayout.core.widget;

import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.robovm.apple.uikit.UIControlContentVerticalAlignment;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UITextView;

/**
 * Created on 8/4/2015.
 */
public class TextArea extends UITextView {

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
}
