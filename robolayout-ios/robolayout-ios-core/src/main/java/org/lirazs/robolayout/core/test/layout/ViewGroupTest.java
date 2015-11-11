package org.lirazs.robolayout.core.test.layout;

import org.lirazs.robolayout.core.test.ViewAssertsTest;
import org.lirazs.robolayout.core.view.LayoutBridge;
import org.lirazs.robolayout.core.view.LayoutParamsSize;
import org.lirazs.robolayout.core.view.ViewGroup;
import org.lirazs.robolayout.core.view.inflater.LayoutInflater;
import org.lirazs.robolayout.core.widget.TextView;
import org.lirazs.robolayout.core.widget.layout.linear.LinearLayoutLayoutParams;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.uikit.UIView;

/**
 * Created on 8/7/2015.
 */
public class ViewGroupTest extends ViewAssertsTest {

    private UIView rootView;
    private ViewGroup group;

    @Before
    public void setUp() {
        rootView = new LayoutBridge(new CGRect(0, 0, 100, 100));

        LayoutInflater inflater = new LayoutInflater();
        inflater.inflate("test/viewgroupchildren.xml", rootView, true);

        group = (ViewGroup) rootView.getSubviews().last();
    }

    @After
    public void tearDown() {
        rootView = null;
        group = null;
    }

    @Test
    public void testAddChild() throws Exception {
        UIView view = createViewWithText("1");
        group.addView(view);

        Assert.assertEquals("Wrong number of children", 1, group.getSubviews().size());
    }

    @Test
    public void testAddChildAtFront() throws Exception {
        for (int i = 0; i < 24; i++) {
            UIView view = createViewWithText(String.valueOf(i + 1));
            group.addView(view);
        }

        UIView view = createViewWithText("X");
        group.addView(view, 0);

        Assert.assertEquals("Wrong number of children", 25, group.getSubviews().size());
        Assert.assertEquals("View has not been added at front", view, group.getSubviews().get(0));
    }

    @Test
    public void testAddChildInMiddle() throws Exception {
        for (int i = 0; i < 24; i++) {
            UIView view = createViewWithText(String.valueOf(i + 1));
            group.addView(view);
        }

        UIView view = createViewWithText("X");
        group.addView(view, 12);

        Assert.assertEquals("Wrong number of children", 25, group.getSubviews().size());
        Assert.assertEquals("View has not been added in the middle", view, group.getSubviews().get(12));
    }

    @Test
    public void testAddChildren() throws Exception {
        for (int i = 0; i < 24; i++) {
            UIView view = createViewWithText(String.valueOf(i + 1));
            group.addView(view);
        }

        Assert.assertEquals("Wrong number of children", 24, group.getSubviews().size());
    }

    @Test
    public void testRemoveChild() throws Exception {
        UIView view = createViewWithText("1");
        group.addView(view);

        group.removeView(view);

        assertGroupNotContainsChild(group, view);

        Assert.assertEquals("Wrong number of children", 0, group.getSubviews().size());
        Assert.assertNull("Superview of remove view is not null", view.getSuperview());
    }

    @Test
    public void testRemoveChildren() throws Exception {
        NSMutableArray<UIView> views = new NSMutableArray<>(24);

        for (int i = 0; i < 24; i++) {
            UIView view = createViewWithText(String.valueOf(i + 1));
            views.add(view);
            group.addView(view);
        }

        for (int i = views.size() - 1; i >= 0; i--) {
            UIView v = views.get(i);
            group.removeView(i);

            assertGroupNotContainsChild(group, v);

            Assert.assertNull("Removed view still has a parent", v.getSuperview());
        }
        Assert.assertEquals("ViewGroup still has subviews", 0, group.getSubviews().size());
    }

    @Test
    public void testRemoveChildAtFront() throws Exception {
        NSMutableArray<UIView> views = new NSMutableArray<>(24);

        for (int i = 0; i < 24; i++) {
            UIView view = createViewWithText(String.valueOf(i + 1));
            views.add(view);
            group.addView(view);
        }

        UIView v = views.get(0);
        group.removeView(0);

        assertGroupNotContainsChild(group, v);
        Assert.assertNull("View still has a superview", v.getSuperview());

        Assert.assertEquals("ViewGroup has the wrong number of subviews", views.size() - 1, group.getSubviews().size());
    }

    @Test
    public void testRemoveChildInMiddle() throws Exception {
        NSMutableArray<UIView> views = new NSMutableArray<>(24);

        for (int i = 0; i < 24; i++) {
            UIView view = createViewWithText(String.valueOf(i + 1));
            views.add(view);
            group.addView(view);
        }

        UIView v = views.get(12);
        group.removeView(12);

        assertGroupNotContainsChild(group, v);
        Assert.assertNull("View still has a superview", v.getSuperview());

        Assert.assertEquals("ViewGroup has the wrong number of subviews", views.size() - 1, group.getSubviews().size());
    }

    private TextView createViewWithText(String text) {
        TextView view = new TextView(CGRect.Zero());
        view.setText(text);

        LinearLayoutLayoutParams layoutParams = new LinearLayoutLayoutParams(LayoutParamsSize.MatchParent, LayoutParamsSize.WrapContent);
        view.setLayoutParams(layoutParams);

        return view;
    }
}
