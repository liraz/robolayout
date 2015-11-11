package org.lirazs.robolayout.core.widget.layout.relative;

import org.lirazs.robolayout.core.util.Poolable;
import org.lirazs.robolayout.core.util.PoolableManager;

/**
 * Created on 8/5/2015.
 */
public class SimplePoolableManager implements PoolableManager {

    private Class<Poolable> clazz;

    public SimplePoolableManager(Class clazz) {
        this.clazz = clazz;
    }

    public Poolable newInstance() {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onAcquiredElement(Poolable element) {

    }

    @Override
    public void onReleasedElement(Poolable element) {

    }
}
