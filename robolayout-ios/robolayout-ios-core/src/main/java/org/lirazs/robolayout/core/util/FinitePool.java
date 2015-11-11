package org.lirazs.robolayout.core.util;

/**
 * Created by mac on 7/29/15.
 */
public class FinitePool implements Pool {

    private int limit;
    private boolean infinite;
    private int poolCount;

    private PoolableManager manager;
    private Poolable root;

    public FinitePool(PoolableManager manager) {
        this(manager, 0);
    }
    public FinitePool(PoolableManager manager, int limit) {
        this.manager = manager;
        this.limit = limit;
        this.infinite = limit == 0;
    }

    @Override
    public Poolable acquire() {
        Poolable element;

        if(root != null) {
            element = root;
            root = element.getNextPoolable();
            poolCount--;
        } else {
            element = manager.newInstance();
        }

        if(element != null) {
            element.setNextPoolable(null);
            manager.onAcquiredElement(element);
        }

        return element;
    }

    @Override
    public void releaseElement(Poolable element) {
        if(infinite || poolCount < limit) {
            poolCount++;
            element.setNextPoolable(root);
            root = element;
        }
        manager.onReleasedElement(element);
    }
}
