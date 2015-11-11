package org.lirazs.robolayout.core.test.layout;

import org.lirazs.robolayout.core.test.ViewAssertsTest;
import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.lirazs.robolayout.core.view.LayoutBridge;
import org.lirazs.robolayout.core.view.inflater.LayoutInflater;
import org.junit.Before;
import org.junit.Test;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIView;

/**
 * Created on 8/7/2015.
 */
public class FrameLayoutGravityTest extends ViewAssertsTest {

    private UIView parent;
    private UIView leftView;
    private UIView rightView;
    private UIView centerHorizontalView;
    private UIView leftCenterVerticalView;
    private UIView rightCenterVerticalView;
    private UIView centerView;
    private UIView leftBottomView;
    private UIView rightBottomView;
    private UIView centerHorizontalBottomView;

    @Before
    public void setUp() {
        LayoutBridge bridge = new LayoutBridge(new CGRect(0, 0, 100, 100));

        LayoutInflater inflater = new LayoutInflater();
        UIView inflatedView = inflater.inflate("test/framelayout_gravity.xml", bridge, true);

        parent = UIViewLayoutUtil.findViewById(inflatedView, "parent");

        leftView = UIViewLayoutUtil.findViewById(inflatedView, "left");
        rightView = UIViewLayoutUtil.findViewById(inflatedView, "right");
        centerHorizontalView = UIViewLayoutUtil.findViewById(inflatedView, "center_horizontal");

        leftCenterVerticalView = UIViewLayoutUtil.findViewById(inflatedView, "left_center_vertical");
        rightCenterVerticalView = UIViewLayoutUtil.findViewById(inflatedView, "right_center_vertical");
        centerView = UIViewLayoutUtil.findViewById(inflatedView, "center");

        leftBottomView = UIViewLayoutUtil.findViewById(inflatedView, "left_bottom");
        rightBottomView = UIViewLayoutUtil.findViewById(inflatedView, "right_bottom");
        centerHorizontalBottomView = UIViewLayoutUtil.findViewById(inflatedView, "center_horizontal_bottom");
    }

    @Test
    public void testLeftTopAligned() throws Exception {
        assertViewIsLeftAlignedToView(parent, leftView);
        assertViewIsTopAlignedToView(parent, leftView);
    }

    @Test
    public void testRightTopAligned() throws Exception {
        assertViewIsRightAlignedToView(parent, rightView);
        assertViewIsTopAlignedToView(parent, rightView);
    }
}
