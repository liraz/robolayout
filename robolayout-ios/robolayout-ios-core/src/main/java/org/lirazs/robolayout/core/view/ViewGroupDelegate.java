package org.lirazs.robolayout.core.view;

import org.robovm.apple.uikit.UIView;

import java.util.Map;

/**
 * Created on 8/3/2015.
 */
public interface ViewGroupDelegate {

    boolean isViewGroup();

    void onViewRemoved(UIView view);
}
