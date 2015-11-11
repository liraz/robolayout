package org.lirazs.robolayout.core.util;

/**
 * Created by mac on 7/29/15.
 */
public interface Poolable {

    Poolable getNextPoolable();

    void setNextPoolable(Poolable poolable);

    void setPooled(boolean pooled);

    boolean isPooled();
}
