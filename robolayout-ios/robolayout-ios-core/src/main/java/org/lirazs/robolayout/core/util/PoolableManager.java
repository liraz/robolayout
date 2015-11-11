package org.lirazs.robolayout.core.util;

/**
 * Created by mac on 7/29/15.
 */
public interface PoolableManager {

    Poolable newInstance();

    void onAcquiredElement(Poolable element);

    void onReleasedElement(Poolable element);
}
