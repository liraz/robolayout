package org.lirazs.robolayout.example.view;

import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.lirazs.robolayout.core.view.LayoutViewController;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.objc.annotation.Method;

/**
 * Created on 8/8/2015.
 */
public class LayoutAnimationsViewController extends LayoutViewController {

    public LayoutAnimationsViewController(String layoutName) {
        super(layoutName);
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        UILabel otherLabel = (UILabel) UIViewLayoutUtil.findViewById(getView(), "otherText");
        otherLabel.setContentMode(UIViewContentMode.ScaleToFill);
    }

    @Method(selector = "didPressButton")
    public void didPressButton() {
        UILabel textLabel = (UILabel) UIViewLayoutUtil.findViewById(getView(), "text");
        if(textLabel.getText().equals("Short text")) {
            textLabel.setText("Very long long text");
        } else {
            textLabel.setText("Short text");
        }

        UIView.animate(0.2, new Runnable() {
            @Override
            public void run() {
                getView().layoutIfNeeded();
            }
        });
    }

    @Override
    public boolean shouldAutorotate() {
        return true;
    }
}
