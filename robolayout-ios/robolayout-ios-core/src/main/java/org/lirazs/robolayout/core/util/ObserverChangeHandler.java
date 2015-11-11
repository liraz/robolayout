package org.lirazs.robolayout.core.util;

import org.robovm.apple.foundation.NSKeyValueChangeInfo;

import java.util.Map;

/**
 * Created by mac on 7/29/15.
 */
public interface ObserverChangeHandler {

    void onChange(String keyPath, Object object, NSKeyValueChangeInfo change);
}
