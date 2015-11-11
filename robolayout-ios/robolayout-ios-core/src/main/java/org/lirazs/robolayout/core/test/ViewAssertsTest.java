package org.lirazs.robolayout.core.test;

import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.junit.Assert;
import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.uikit.UIView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 8/7/2015.
 */
public abstract class ViewAssertsTest {

    /**
     * Assert that the specified group contains a specific child once and only once.
     *
     * @param parent The group
     * @param child The child that should belong to group
     */
    protected void assertGroupContainsChild(UIView parent, UIView child) {
        boolean found = false;

        for (UIView childView : parent.getSubviews()) {
            if(childView.isEqual(child)) {
                if(!found) {
                    found = true;
                } else {
                    Assert.fail(String.format("child %s is duplicated in parent", child));
                }
            }
        }
        Assert.assertTrue(String.format("group does not contain %s", child), found);
    }

    /**
     * Assert that the specified group does not contain a specific child.
     *
     * @param parent The group
     * @param child The child that should not belong to group
     */
    protected void assertGroupNotContainsChild(UIView parent, UIView child) {

        for (UIView childView : parent.getSubviews()) {
            if(childView.isEqual(child)) {
                Assert.fail(String.format("child %s is found in parent", child));
            }
        }
    }

    /**
     * Finds the most common ancestor of two views.
     */
    protected UIView findMostCommonAncestorOfView(UIView view1, UIView view2) {
        NSMutableArray<UIView> path1 = new NSMutableArray<>();
        NSMutableArray<UIView> path2 = new NSMutableArray<>();

        UIView n1 = view1;
        while(n1 != null) {
            path1.add(n1);
            n1 = n1.getSuperview();
        }

        UIView n2 = view2;
        while(n2 != null) {
            path2.add(n2);
            n2 = n2.getSuperview();
        }

        UIView result = null;
        while(path1.last() != null && path1.last().isEqual(path2.last())) {
            result = path1.last();

            path1.remove(path1.size() - 1);
            path2.remove(path2.size() - 1);
        }
        return result;
    }

    /**
     * Assert that two views are left aligned, that is that their left edges
     * are on the same x location.
     *
     * @param first The first view
     * @param second The second view
     */
    protected void assertViewIsLeftAlignedToView(UIView first, UIView second) {
        UIView commonAncestor = findMostCommonAncestorOfView(first, second);
        if(commonAncestor == null) {
            Assert.fail("Views can't be aligned because they don't have a common ancestor");
        }

        CGPoint origin1 = commonAncestor.convertPointFromView(first.getFrame().getOrigin(), first);
        CGPoint origin2 = commonAncestor.convertPointFromView(second.getFrame().getOrigin(), second);

        Assert.assertEquals("views are not left aligned", origin1.getX(), origin2.getX(), 0);
    }

    /**
     * Assert that two views are right aligned, that is that their right edges
     * are on the same x location.
     *
     * @param first The first view
     * @param second The second view
     */
    protected void assertViewIsRightAlignedToView(UIView first, UIView second) {
        UIView commonAncestor = findMostCommonAncestorOfView(first, second);
        if(commonAncestor == null) {
            Assert.fail("Views can't be aligned because they don't have a common ancestor");
        }

        CGPoint origin1 = commonAncestor.convertPointFromView(first.getFrame().getOrigin(), first);
        CGPoint origin2 = commonAncestor.convertPointFromView(second.getFrame().getOrigin(), second);

        CGSize firstMeasuredSize = UIViewLayoutUtil.getMeasuredSize(first);
        CGSize secondMeasuredSize = UIViewLayoutUtil.getMeasuredSize(second);

        Assert.assertEquals("views are not right aligned", origin1.getX() + firstMeasuredSize.getWidth(), origin2.getX() + secondMeasuredSize.getWidth(), 0);
    }

    /**
     * Assert that two views are top aligned, that is that their top edges
     * are on the same y location.
     *
     * @param first The first view
     * @param second The second view
     */
    protected void assertViewIsTopAlignedToView(UIView first, UIView second) {
        UIView commonAncestor = findMostCommonAncestorOfView(first, second);
        if(commonAncestor == null) {
            Assert.fail("Views can't be aligned because they don't have a common ancestor");
        }

        CGPoint origin1 = commonAncestor.convertPointFromView(first.getFrame().getOrigin(), first);
        CGPoint origin2 = commonAncestor.convertPointFromView(second.getFrame().getOrigin(), second);

        Assert.assertEquals("views are not top aligned", origin1.getY(), origin2.getY(), 0);
    }

    /**
     * Assert that two views are bottom aligned, that is that their bottom edges
     * are on the same y location.
     *
     * @param first The first view
     * @param second The second view
     */
    protected void assertViewIsBottomAlignedToView(UIView first, UIView second) {
        UIView commonAncestor = findMostCommonAncestorOfView(first, second);
        if(commonAncestor == null) {
            Assert.fail("Views can't be aligned because they don't have a common ancestor");
        }

        CGPoint origin1 = commonAncestor.convertPointFromView(first.getFrame().getOrigin(), first);
        CGPoint origin2 = commonAncestor.convertPointFromView(second.getFrame().getOrigin(), second);

        CGSize firstMeasuredSize = UIViewLayoutUtil.getMeasuredSize(first);
        CGSize secondMeasuredSize = UIViewLayoutUtil.getMeasuredSize(second);

        Assert.assertEquals("views are not bottom aligned", origin1.getY() + firstMeasuredSize.getHeight(), origin2.getY() + secondMeasuredSize.getHeight(), 0);
    }
}
