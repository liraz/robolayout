package org.lirazs.robolayout.core.view;

import org.robovm.apple.coregraphics.CGRect;

/**
 * Created on 8/3/2015.
 */
public interface LayoutViewDelegate {

    void onLayout(CGRect frame, boolean changed);

    void onMeasure(LayoutMeasureSpec widthMeasureSpec, LayoutMeasureSpec heightMeasureSpec);
}
