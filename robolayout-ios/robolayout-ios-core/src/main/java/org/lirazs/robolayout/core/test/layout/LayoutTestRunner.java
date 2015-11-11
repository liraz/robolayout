package org.lirazs.robolayout.core.test.layout;

import org.lirazs.robolayout.core.test.XCTestRunner;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

/**
 * Created by mac on 8/7/15.
 */
public class LayoutTestRunner extends XCTestRunner {
    @Override
    public Class[] getTestClasses() {
        return new Class[] {ViewGroupTest.class, LayoutInflaterTest.class, FrameLayoutGravityTest.class};
    }

    public static void main(String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, LayoutTestRunner.class);
        }
    }
}
