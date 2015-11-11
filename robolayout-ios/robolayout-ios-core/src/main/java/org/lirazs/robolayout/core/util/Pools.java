package org.lirazs.robolayout.core.util;

/**
 * Created by mac on 7/29/15.
 */
public final class Pools {

    public static Pool simplePoolForPoolableManager(PoolableManager poolableManager) {
        return new FinitePool(poolableManager);
    }

    public static Pool finitePoolWithLimit(int limit, PoolableManager poolableManager) {
        return new FinitePool(poolableManager, limit);
    }

    public static Pool synchronizedPoolForPool(Pool pool) {
        return new SynchronizedPool(pool);
    }

    public static Pool synchronizedPoolForPool(Pool pool, Object lock, boolean takeLockOwnership) {
        return new SynchronizedPool(pool, lock, takeLockOwnership);
    }
}
