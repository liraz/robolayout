package org.lirazs.robolayout.core.util;

/**
 * Created by mac on 7/29/15.
 */
public interface Pool {

    Poolable acquire();

    void releaseElement(Poolable element);
}
