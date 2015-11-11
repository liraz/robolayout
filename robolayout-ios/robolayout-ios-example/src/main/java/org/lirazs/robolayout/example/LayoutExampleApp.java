package org.lirazs.robolayout.example;

import org.lirazs.robolayout.example.view.MainViewController;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.*;

/**
 * Created by mac on 8/7/15.
 */
public class LayoutExampleApp extends UIApplicationDelegateAdapter {
    @Override
    public void didBecomeActive(UIApplication application) {
        super.didBecomeActive(application);

        setWindow(new UIWindow(UIScreen.getMainScreen().getBounds()));

        // Override point for customization after application launch.
        getWindow().setBackgroundColor(UIColor.white());

        MainViewController mainVC = new MainViewController();
        UINavigationController navController = new UINavigationController(mainVC);

        getWindow().setRootViewController(navController);
        getWindow().makeKeyAndVisible();
    }

    public static void main(String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, LayoutExampleApp.class);
        }
    }
}
