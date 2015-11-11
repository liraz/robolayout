package org.lirazs.robolayout.core.test;

import org.junit.internal.TextListener;
import org.junit.runner.Computer;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSException;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegate;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;

/**
 * Created on 8/7/2015.
 */
public abstract class XCTestRunner extends UIApplicationDelegateAdapter {

    @Override
    public void didBecomeActive(UIApplication application) {
        super.didBecomeActive(application);

        NSException.registerDefaultJavaUncaughtExceptionHandler();

        Computer computer = new Computer();

        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.addListener(new TextListener(System.out));
        jUnitCore.run(computer, getTestClasses());
    }

    public abstract Class[] getTestClasses();
}
