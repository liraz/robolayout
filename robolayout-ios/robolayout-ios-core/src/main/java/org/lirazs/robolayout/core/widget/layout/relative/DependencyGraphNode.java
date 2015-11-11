package org.lirazs.robolayout.core.widget.layout.relative;

import org.lirazs.robolayout.core.util.Pool;
import org.lirazs.robolayout.core.util.Poolable;
import org.lirazs.robolayout.core.util.Pools;
import org.robovm.apple.uikit.UIView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created on 8/5/2015.
 */
public class DependencyGraphNode implements Poolable {
    private static int POOL_LIMIT = 100;
    private static Pool pool;

    static public Pool getPool() {
        if(pool == null) {
            SimplePoolableManager poolableManager = new SimplePoolableManager(DependencyGraphNode.class);
            pool = Pools.synchronizedPoolForPool(Pools.finitePoolWithLimit(POOL_LIMIT, poolableManager));
        }
        return pool;
    }

    public static DependencyGraphNode acquireView(UIView view) {
        DependencyGraphNode node = (DependencyGraphNode) DependencyGraphNode.getPool().acquire();
        node.view = view;
        return node;
    }

    private UIView view;
    private Set<DependencyGraphNode> dependents;
    private Map<String, DependencyGraphNode> dependencies;

    Poolable nextPoolable;
    boolean isPooled;

    public DependencyGraphNode() {
        this.dependents = new HashSet<>();
        this.dependencies = new HashMap<>();
    }

    public void releaseNode() {
        this.view = null;

        dependents.clear();
        dependencies.clear();

        getPool().releaseElement(this);
    }

    public UIView getView() {
        return view;
    }

    public Set<DependencyGraphNode> getDependents() {
        return dependents;
    }

    public Map<String, DependencyGraphNode> getDependencies() {
        return dependencies;
    }

    @Override
    public Poolable getNextPoolable() {
        return nextPoolable;
    }

    @Override
    public void setNextPoolable(Poolable poolable) {
        nextPoolable = poolable;
    }

    @Override
    public void setPooled(boolean pooled) {
        isPooled = pooled;
    }

    @Override
    public boolean isPooled() {
        return isPooled;
    }
}
