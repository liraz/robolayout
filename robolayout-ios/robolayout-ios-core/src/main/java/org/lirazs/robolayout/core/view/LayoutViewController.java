package org.lirazs.robolayout.core.view;

import org.lirazs.robolayout.core.view.inflater.LayoutInflater;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewAutoresizing;
import org.robovm.apple.uikit.UIViewController;

/**
 * Created on 8/4/2015.
 */
public class LayoutViewController extends UIViewController {

    private NSURL layoutUrl;

    public LayoutViewController() {
        this(null, null);
    }

    public LayoutViewController(String layoutName) {
        this(layoutName, null);
    }
    public LayoutViewController(String layoutName, NSBundle layoutBundle) {
        super(null, null);

        if(layoutBundle == null) {
            layoutBundle = NSBundle.getMainBundle();
        }
        if(layoutName != null) {
            this.layoutUrl = layoutBundle.findResourceURL(layoutName, "xml");
        }
    }

    public LayoutViewController(NSURL layoutUrl) {
        this();
        this.layoutUrl = layoutUrl;
    }

    @Override
    public void loadView() {
        LayoutBridge bridge = new LayoutBridge(UIScreen.getMainScreen().getBounds());
        bridge.setResizeOnKeyboard(true);
        bridge.setScrollToTextField(true);
        bridge.setAutoresizingMask(UIViewAutoresizing.FlexibleWidth.set(UIViewAutoresizing.FlexibleHeight));

        if(layoutUrl != null) {
            LayoutInflater inflater = new LayoutInflater();
            inflater.setActionTarget(this);
            inflater.inflate(layoutUrl, bridge, true);
        }
        setView(bridge);
    }

    public LayoutBridge getView() {
        return (LayoutBridge) super.getView();
    }
}
