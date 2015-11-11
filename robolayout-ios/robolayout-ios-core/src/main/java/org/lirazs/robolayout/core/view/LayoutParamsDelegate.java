package org.lirazs.robolayout.core.view;

import java.util.Map;

/**
 * Created on 8/3/2015.
 */
public interface LayoutParamsDelegate {

    LayoutParams generateDefaultLayoutParams();

    LayoutParams generateLayoutParams(LayoutParams params);

    LayoutParams generateLayoutParams(Map<String, String> attrs);

    boolean checkLayoutParams(LayoutParams layoutParams);
}
