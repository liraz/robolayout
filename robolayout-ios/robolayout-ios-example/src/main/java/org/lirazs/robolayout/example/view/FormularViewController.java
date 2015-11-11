package org.lirazs.robolayout.example.view;

import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.lirazs.robolayout.core.view.LayoutParams;
import org.lirazs.robolayout.core.view.LayoutViewController;
import org.lirazs.robolayout.core.view.ViewContentGravity;
import org.lirazs.robolayout.core.view.ViewVisibility;
import org.lirazs.robolayout.core.widget.layout.linear.LinearLayoutLayoutParams;
import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.Method;

/**
 * Created on 8/8/2015.
 */
public class FormularViewController extends LayoutViewController {

    public FormularViewController(String layoutName) {
        super(layoutName);
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        updateAndroidStatus();
    }

    @Method(selector = "didPressSubmitButton")
    public void didPressSubmitButton() {
        UIButton submitButton = (UIButton) UIViewLayoutUtil.findViewById(getView(), "submitButton");
        submitButton.setSelected(true);

        UILabel username = (UILabel) UIViewLayoutUtil.findViewById(getView(), "username");
        UILabel password = (UILabel) UIViewLayoutUtil.findViewById(getView(), "password");
        UITextView freeText = (UITextView) UIViewLayoutUtil.findViewById(getView(), "freeText");

        username.resignFirstResponder();
        password.resignFirstResponder();
        freeText.resignFirstResponder();

        String message = String.format("Username: %s\nPassword: %s\nText: %s", username.getText(), password.getText(), freeText.getText());

        UIAlertView alertView = new UIAlertView("", message, null, "OK");
        alertView.show();
    }

    @Method(selector = "didPressToggleButton")
    public void didPressToggleButton() {
        UIView androidView = UIViewLayoutUtil.findViewById(getView(), "android");

        ViewVisibility visibility = UIViewLayoutUtil.getVisibility(androidView);
        if(visibility == ViewVisibility.Visible) {
            LinearLayoutLayoutParams lp = (LinearLayoutLayoutParams) UIViewLayoutUtil.getLayoutParams(androidView);

            if(lp.getGravity().equals(ViewContentGravity.Left)) {
                lp.setGravity(ViewContentGravity.CenterHorizontal);
            } else if(lp.getGravity().equals(ViewContentGravity.CenterHorizontal)) {
                lp.setGravity(ViewContentGravity.Right);
            } else {
                lp.setGravity(ViewContentGravity.Left);
                UIViewLayoutUtil.setVisibility(androidView, ViewVisibility.Invisible);
            }
            UIViewLayoutUtil.setLayoutParams(androidView, lp);

        } else if(visibility == ViewVisibility.Invisible) {
            UIViewLayoutUtil.setVisibility(androidView, ViewVisibility.Gone);
        } else {
            UIViewLayoutUtil.setVisibility(androidView, ViewVisibility.Visible);
        }

        UIView.animate(0.5, new Runnable() {
            @Override
            public void run() {
                getView().layoutIfNeeded();
            }
        });
        updateAndroidStatus();
    }

    private void updateAndroidStatus() {
        UIView androidView = UIViewLayoutUtil.findViewById(getView(), "android");
        UILabel label = (UILabel) UIViewLayoutUtil.findViewById(getView(), "androidStatus");

        String visibility = null;
        switch(UIViewLayoutUtil.getVisibility(androidView)) {
            case Visible:
                visibility = "visible";
                break;
            case Invisible:
                visibility = "invisible";
                break;
            case Gone:
                visibility = "gone";
                break;
        }

        String gravity;
        LinearLayoutLayoutParams lp = (LinearLayoutLayoutParams) UIViewLayoutUtil.getLayoutParams(androidView);

        if(lp.getGravity().equals(ViewContentGravity.Left)) {
            gravity = "left";
        } else if(lp.getGravity().equals(ViewContentGravity.CenterHorizontal)) {
            gravity = "center_horizontal";
        } else if(lp.getGravity().equals(ViewContentGravity.Right)) {
            gravity = "right";
        } else {
            gravity = "unknown";
        }

        label.setText(String.format("andrdoid[visibility=%s,layout_gravity=%s]", visibility, gravity));
    }

    @Override
    public boolean shouldAutorotate() {
        return true;
    }
}
