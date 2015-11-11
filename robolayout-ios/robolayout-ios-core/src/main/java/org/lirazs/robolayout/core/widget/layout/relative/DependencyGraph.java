package org.lirazs.robolayout.core.widget.layout.relative;

import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.robovm.apple.uikit.UIView;

import java.util.*;

/**
 * Created on 8/5/2015.
 */
public class DependencyGraph {

    private Map<String, DependencyGraphNode> keyNodes;
    private List<DependencyGraphNode> nodes;
    private List<DependencyGraphNode> roots;

    public DependencyGraph() {
        keyNodes = new HashMap<>();
        nodes = new ArrayList<>();
        roots = new ArrayList<>();
    }

    public Map<String, DependencyGraphNode> getKeyNodes() {
        return keyNodes;
    }

    public void clear() {
        for (DependencyGraphNode node : nodes) {
            node.releaseNode();
        }

        nodes.clear();
        keyNodes.clear();
        roots.clear();
    }

    public void addView(UIView view) {
        String identifier = UIViewLayoutUtil.getIdentifier(view);
        DependencyGraphNode node = DependencyGraphNode.acquireView(view);

        if(identifier != null) {
            keyNodes.put(identifier, node);
        }

        nodes.add(node);
    }

    public List<DependencyGraphNode> findRootsWithRules(int[] rulesFilter) {
        Map<String, DependencyGraphNode> keyNodes = this.keyNodes;
        List<DependencyGraphNode> nodes = this.nodes;

        // Find roots can be invoked several times, so make sure to clear
        // all dependents and dependencies before running the algorithm
        for (DependencyGraphNode node : nodes) {
            node.getDependents().clear();
            node.getDependencies().clear();
        }

        // Builds up the dependents and dependencies for each node of the graph
        for (DependencyGraphNode node : nodes) {
            RelativeLayoutLayoutParams layoutParams = (RelativeLayoutLayoutParams) UIViewLayoutUtil.getLayoutParams(node.getView());
            String[] rules = layoutParams.getRules();

            // Look only the the rules passed in parameter, this way we build only the
            // dependencies for a specific set of rules
            for (int ruleId : rulesFilter) {
                String rule = rules[ruleId];
                if(rule != null) {
                    // The node this node depends on
                    DependencyGraphNode dependency = keyNodes.get(rule);
                    // Skip unknowns and self dependencies
                    if (dependency == null || dependency == node) {
                        continue;
                    }
                    // Add the current node as a dependent
                    dependency.getDependents().add(node);
                    // Add a dependency to the current node
                    node.getDependencies().put(rule, dependency);
                }
            }
        }

        List<DependencyGraphNode> roots = this.roots;
        roots.clear();

        // Finds all the roots in the graph: all nodes with no dependencies
        for (DependencyGraphNode node : nodes) {
            if(node.getDependencies().size() == 0) {
                roots.add(node);
            }
        }
        return roots;
    }

    /**
     * Builds a sorted list of views. The sorting order depends on the dependencies
     * between the view. For instance, if view C needs view A to be processed first
     * and view A needs view B to be processed first, the dependency graph
     * is: B -> A -> C. The sorted array will contain views B, A and C in this order.
     *
     * @param sorted The sorted list of views. The length of this array must
     *        be equal to getChildCount().
     * @param rulesIndexes The list of rules to take into account.
     */
    public void getSortedViews(List<UIView> sorted, int[] rulesIndexes) {
        List<DependencyGraphNode> roots = findRootsWithRules(rulesIndexes);
        int index = 0;

        while(roots.size() > 0) {
            DependencyGraphNode node = roots.get(0);
            roots.remove(0);

            UIView view = node.getView();
            String key = UIViewLayoutUtil.getIdentifier(view);

            sorted.add(index++, view);

            Set<DependencyGraphNode> dependents = node.getDependents();
            for (DependencyGraphNode dependent : dependents) {
                Map<String, DependencyGraphNode> dependencies = dependent.getDependencies();

                dependencies.remove(key);
                if(dependencies.size() == 0) {
                    roots.add(dependent);
                }
            }
        }

        if(index < sorted.size()) {
            throw new IllegalStateException("Circular dependencies cannot exist in RelativeLayout");
        }
    }
}
