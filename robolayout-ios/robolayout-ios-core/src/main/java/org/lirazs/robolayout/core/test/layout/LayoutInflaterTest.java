package org.lirazs.robolayout.core.test.layout;

import org.lirazs.robolayout.core.test.layout.view.CustomTestView;
import org.lirazs.robolayout.core.view.LayoutBridge;
import org.lirazs.robolayout.core.view.inflater.LayoutInflater;
import org.lirazs.robolayout.core.widget.layout.linear.LinearLayout;
import junit.framework.Assert;
import org.junit.Test;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UIView;

/**
 * Created on 8/7/2015.
 */
public class LayoutInflaterTest {

    @Test
    public void testInflateURL() throws Exception {
        NSURL url = NSBundle.getMainBundle().findResourceURL("test/testLayout1", "xml");
        LayoutInflater inflater = new LayoutInflater();

        LayoutBridge rootView = new LayoutBridge(new CGRect(0, 0, 100, 100));
        UIView view = inflater.inflate(url, rootView, false);

        Assert.assertNotNull("Inflater returned null when inflating simple view", view);
    }

    @Test
    public void testInflateAttachToRootTrue() throws Exception {
        NSURL url = NSBundle.getMainBundle().findResourceURL("test/testLayout1", "xml");
        LayoutInflater inflater = new LayoutInflater();

        LayoutBridge rootView = new LayoutBridge(new CGRect(0, 0, 100, 100));
        UIView view = inflater.inflate(url, rootView, true);

        Assert.assertEquals("Inflater did not return rootView", view, rootView);
        Assert.assertEquals("Inflater did not attach inflated view to rootView", 1, rootView.getSubviews().size());
    }

    @Test
    public void testInflateAttachToRootFalse() throws Exception {
        NSURL url = NSBundle.getMainBundle().findResourceURL("test/testLayout1", "xml");
        LayoutInflater inflater = new LayoutInflater();

        LayoutBridge rootView = new LayoutBridge(new CGRect(0, 0, 100, 100));
        UIView view = inflater.inflate(url, rootView, false);

        Assert.assertNull("Inflater attached inflated view to rootView", view.getSuperview());
    }

    @Test
    public void testInflateCustomView() throws Exception {
        NSURL url = NSBundle.getMainBundle().findResourceURL("test/testLayout2", "xml");
        LayoutInflater inflater = new LayoutInflater();

        LayoutBridge rootView = new LayoutBridge(new CGRect(0, 0, 100, 100));
        UIView view = inflater.inflate(url, rootView, false);

        Assert.assertEquals("Inflater inflated the wrong view type", view.getClass(), CustomTestView.class);
    }

    @Test
    public void testInflatePrefixedViews() throws Exception {
        NSURL url = NSBundle.getMainBundle().findResourceURL("test/testLayout3", "xml");
        LayoutInflater inflater = new LayoutInflater();

        LayoutBridge rootView = new LayoutBridge(new CGRect(0, 0, 100, 100));
        UIView view = inflater.inflate(url, rootView, false);

        Assert.assertEquals("Inflater didn't resolve the non-prefixed view name to a prefixed class name", view.getClass(), CustomTestView.class);
    }

    @Test
    public void testInflateSubviews() throws Exception {
        NSURL url = NSBundle.getMainBundle().findResourceURL("test/testLayout4", "xml");
        LayoutInflater inflater = new LayoutInflater();

        LayoutBridge rootView = new LayoutBridge(new CGRect(0, 0, 100, 100));
        UIView view = inflater.inflate(url, rootView, false);

        Assert.assertEquals("Inflater did not inflate the LinearLayout root view", view.getClass(), LinearLayout.class);
        Assert.assertEquals("Inflater inflated the wrong number of subviews", 2, view.getSubviews().size());
    }


}
