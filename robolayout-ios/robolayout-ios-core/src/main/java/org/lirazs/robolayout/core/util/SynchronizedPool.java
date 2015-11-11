package org.lirazs.robolayout.core.util;

/**
 * Created by mac on 7/29/15.
 */
public class SynchronizedPool implements Pool {

    private boolean hasBlockOwnership;
    private Pool pool;
    private final Object lock;

    public SynchronizedPool(Pool pool) {
        this(pool, null, false);
    }
    public SynchronizedPool(Pool pool, Object lock, boolean takeLockOwnership) {
        this.pool = pool;
        this.lock = lock != null ? lock : this;
        this.hasBlockOwnership = takeLockOwnership;
    }

    public boolean isHasBlockOwnership() {
        return hasBlockOwnership;
    }

    @Override
    public Poolable acquire() {
        synchronized (lock) {
            return pool.acquire();
        }
    }

    @Override
    public void releaseElement(Poolable element) {
        synchronized (lock) {
            pool.releaseElement(element);
        }
    }
}
