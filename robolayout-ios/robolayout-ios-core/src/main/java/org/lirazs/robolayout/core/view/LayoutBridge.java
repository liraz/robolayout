package org.lirazs.robolayout.core.view;

import org.lirazs.robolayout.core.util.UIViewLayoutBridgeUtil;
import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.lirazs.robolayout.core.util.UIViewViewGroupUtil;
import org.lirazs.robolayout.core.view.inflater.LayoutInflater;
import org.apache.commons.io.FilenameUtils;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.*;
import org.robovm.apple.uikit.*;
import org.robovm.objc.Selector;
import org.robovm.objc.annotation.Method;

import java.util.Map;

/**
 * Created on 8/4/2015.
 */
public class LayoutBridge extends ViewGroup implements LayoutViewDelegate, LayoutParamsDelegate {

    private boolean resizeOnKeyboard;
    private boolean scrollToTextField;

    private CGRect lastFrame;

    public LayoutBridge(CGRect frame) {
        super(frame);
    }

    @Override
    public CGSize getSizeThatFits(CGSize cgSize) {
        UIView lastChild = getSubviews().last();

        MarginLayoutParams layoutParams = (MarginLayoutParams) UIViewLayoutUtil.getLayoutParams(lastChild);
        LayoutMeasureSpec widthSpec = new LayoutMeasureSpec(cgSize.getWidth(), LayoutMeasureSpecMode.Unspecified);
        LayoutMeasureSpec heightSpec = new LayoutMeasureSpec(cgSize.getHeight(), LayoutMeasureSpecMode.Unspecified);

        if(layoutParams.getWidth() == LayoutParamsSize.MatchParent.getValue()) {
            widthSpec.setMode(LayoutMeasureSpecMode.Exactly);
        }
        if(layoutParams.getHeight() == LayoutParamsSize.MatchParent.getValue()) {
            heightSpec.setMode(LayoutMeasureSpecMode.Exactly);
        }

        onMeasure(widthSpec, heightSpec);
        return getMeasuredSize();
    }

    public void addSubview(UIView view) {
        for (UIView subView : getSubviews()) {
            subView.removeFromSuperview();
        }
        super.addSubview(view);
    }

    public boolean isResizeOnKeyboard() {
        return resizeOnKeyboard;
    }

    public void setResizeOnKeyboard(boolean resizeOnKeyboard) {
        if(resizeOnKeyboard && !this.resizeOnKeyboard) {
            NSNotificationCenter center = NSNotificationCenter.getDefaultCenter();

            center.addObserver(this, Selector.register("willShowKeyboard:"), UIWindow.KeyboardWillShowNotification(), null);
            center.addObserver(this, Selector.register("didShowKeyboard:"), UIWindow.KeyboardDidShowNotification(), null);
            center.addObserver(this, Selector.register("willHideKeyboard:"), UIWindow.KeyboardWillHideNotification(), null);

        } else if(!resizeOnKeyboard && this.resizeOnKeyboard) {
            NSNotificationCenter center = NSNotificationCenter.getDefaultCenter();

            center.removeObserver(this, UIWindow.KeyboardWillShowNotification(), null);
            center.removeObserver(this, UIWindow.KeyboardDidShowNotification(), null);
            center.removeObserver(this, UIWindow.KeyboardWillHideNotification(), null);
        }
        this.resizeOnKeyboard = resizeOnKeyboard;
    }

    public boolean isScrollToTextField() {
        return scrollToTextField;
    }

    public void setScrollToTextField(boolean scrollToTextField) {
        if(scrollToTextField && !this.scrollToTextField) {
            NSNotificationCenter center = NSNotificationCenter.getDefaultCenter();

            center.addObserver(this, Selector.register("didBeginEditing:"), UITextField.DidBeginEditingNotification(), null);
            center.addObserver(this, Selector.register("didEndEditing:"), UITextField.DidEndEditingNotification(), null);
            center.addObserver(this, Selector.register("didBeginEditing:"), UITextView.DidBeginEditingNotification(), null);
            center.addObserver(this, Selector.register("didEndEditing:"), UITextView.DidEndEditingNotification(), null);

        } else if(!scrollToTextField && this.scrollToTextField) {
            NSNotificationCenter center = NSNotificationCenter.getDefaultCenter();

            center.removeObserver(this, UITextField.DidBeginEditingNotification(), null);
            center.removeObserver(this, UITextField.DidEndEditingNotification(), null);
            center.removeObserver(this, UITextView.DidBeginEditingNotification(), null);
            center.removeObserver(this, UITextView.DidEndEditingNotification(), null);
        }
        this.scrollToTextField = scrollToTextField;
    }

    //TODO: For some reason this method does not exist in RoboVM - so cannot call super!!
    public void setValue(Object value, String key) {
        if(key.equals("layouts")) {
            String stringValue = String.valueOf(value);

            String extension = FilenameUtils.getExtension(stringValue);
            if(extension.isEmpty()) {
                extension = "xml";
            }
            NSBundle bundle = NSBundle.getMainBundle();
            NSURL url = bundle.findResourceURL(stringValue.substring(0, stringValue.indexOf('.')), extension);
            if(url != null) {
                LayoutInflater inflater = new LayoutInflater();
                inflater.inflate(url, this, true);
            }
        } else {
            //[super setValue:value forUndefinedKey:key];
        }
    }

    @Override
    public void onLayout(CGRect frame, boolean changed) {
        UIView firstChild = getSubviews().last();
        if(firstChild != null) {
            CGSize size = UIViewLayoutUtil.getMeasuredSize(firstChild);
            MarginLayoutParams lp = (MarginLayoutParams) UIViewLayoutUtil.getLayoutParams(firstChild);
            UIEdgeInsets margin = lp.getMargin();

            UIViewLayoutUtil.layout(firstChild,
                    new CGRect(margin.getLeft(), margin.getTop(), size.getWidth(), size.getHeight()));
        }
    }

    @Override
    public void onMeasure(LayoutMeasureSpec widthMeasureSpec, LayoutMeasureSpec heightMeasureSpec) {
        /*for (UIView subView : getSubviews()) {
            if(UIViewLayoutUtil.getVisibility(subView) != ViewVisibility.Gone) {
                UIViewViewGroupUtil.measureChild(this, subView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            }
        }*/

        CGSize lastChildSize = CGSize.Zero();
        UIView lastChild = getSubviews().last();

        if(UIViewLayoutUtil.getVisibility(lastChild) != ViewVisibility.Gone) {
            UIViewViewGroupUtil.measureChild(this, lastChild, widthMeasureSpec, 0, heightMeasureSpec, 0);

            lastChildSize = UIViewLayoutUtil.getMeasuredSize(lastChild);
            LayoutParams layoutParams = UIViewLayoutUtil.getLayoutParams(lastChild);

            if(layoutParams instanceof MarginLayoutParams) {
                MarginLayoutParams marginParams = (MarginLayoutParams) layoutParams;
                lastChildSize.setWidth(lastChildSize.getWidth() + marginParams.getMargin().getLeft() + marginParams.getMargin().getRight());
                lastChildSize.setHeight(lastChildSize.getHeight() + marginParams.getMargin().getTop() + marginParams.getMargin().getBottom());
            }
        }

        double widthSize = lastChildSize.getWidth() + getPadding().getLeft() + getPadding().getRight();
        double heightSize = lastChildSize.getHeight() + getPadding().getTop() + getPadding().getBottom();

        LayoutMeasuredDimension width = new LayoutMeasuredDimension(widthSize, LayoutMeasuredState.None);
        LayoutMeasuredDimension height = new LayoutMeasuredDimension(heightSize, LayoutMeasuredState.None);

        setMeasuredDimensionSize(new LayoutMeasuredSize(width, height));
    }

    @Override
    public boolean checkLayoutParams(LayoutParams layoutParams) {
        return layoutParams != null;
    }

    @Override
    public LayoutParams generateDefaultLayoutParams() {
        LayoutBridgeLayoutParams lp = new LayoutBridgeLayoutParams(LayoutParamsSize.MatchParent, LayoutParamsSize.MatchParent);
        lp.setWidth(LayoutParamsSize.MatchParent.getValue());
        lp.setHeight(LayoutParamsSize.MatchParent.getValue());
        return lp;
    }

    @Override
    public LayoutParams generateLayoutParams(LayoutParams params) {
        return new LayoutBridgeLayoutParams(params);
    }

    @Override
    public LayoutParams generateLayoutParams(Map<String, String> attrs) {
        return new LayoutBridgeLayoutParams(attrs);
    }

    @Override
    public boolean isViewGroup() {
        //return false;
        return true;
    }

    @Override
    public void onViewRemoved(UIView view) {

    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();

        if(lastFrame == null || !getFrame().equalsTo(lastFrame) || UIViewLayoutUtil.isLayoutRequested(this)) {
            NSDate methodStart = NSDate.now();

            lastFrame = this.getFrame();

            final LayoutMeasureSpec widthMeasureSpec = new LayoutMeasureSpec();
            final LayoutMeasureSpec heightMeasureSpec = new LayoutMeasureSpec();

            widthMeasureSpec.setSize(getFrame().getSize().getWidth());
            heightMeasureSpec.setSize(getFrame().getSize().getHeight());

            widthMeasureSpec.setMode(LayoutMeasureSpecMode.Exactly);
            heightMeasureSpec.setMode(LayoutMeasureSpecMode.Exactly);

            UIViewLayoutUtil.measure(this, widthMeasureSpec, heightMeasureSpec);
            UIViewLayoutUtil.layout(this, getFrame());

            NSDate methodFinish = NSDate.now();
            double timeIntervalSince = methodFinish.getTimeIntervalSince(methodStart);

            Foundation.log(String.format("Relayout took %.2fms", timeIntervalSince * 1000));
        }
    }

    @Method(selector = "willShowKeyboard:")
    public void willShowKeyboard(NSNotification notification) {
        NSValue uiKeyboardFrameEndUserInfoKey = (NSValue) notification.getUserInfo().get(new NSString("UIKeyboardFrameEndUserInfoKey"));
        CGRect keyboardFrame = uiKeyboardFrameEndUserInfoKey.rectValue();
        CGRect kbLocalFrame = convertRectFromView(keyboardFrame, getWindow());

        Foundation.log(String.format("Show: %s", kbLocalFrame.toString()));

        CGRect f = this.getFrame();
        f.getSize().setHeight(kbLocalFrame.getOrigin().getY());
        setFrame(f);

        double duration = notification.getUserInfo().getDouble(new NSString("UIKeyboardAnimationDurationUserInfoKey"));
        UIView.animate(duration, new Runnable() {
            @Override
            public void run() {
                layoutIfNeeded();
            }
        });
    }
    @Method(selector = "didShowKeyboard:")
    public void didShowKeyboard(NSNotification notification) {

    }
    @Method(selector = "willHideKeyboard:")
    public void willHideKeyboard(NSNotification notification) {
        NSValue uiKeyboardFrameEndUserInfoKey = (NSValue) notification.getUserInfo().get(new NSString("UIKeyboardFrameEndUserInfoKey"));
        CGRect keyboardFrame = uiKeyboardFrameEndUserInfoKey.rectValue();
        CGRect kbLocalFrame = convertRectFromView(keyboardFrame, getWindow());

        Foundation.log(String.format("Hide: %s", kbLocalFrame.toString()));

        CGRect f = this.getFrame();
        f.getSize().setHeight(kbLocalFrame.getOrigin().getY());
        setFrame(f);

        double duration = notification.getUserInfo().getDouble(new NSString("UIKeyboardAnimationDurationUserInfoKey"));
        UIView.animate(duration, new Runnable() {
            @Override
            public void run() {
                layoutIfNeeded();
            }
        });
    }

    @Method(selector = "didBeginEditing:")
    public void didBeginEditing(NSNotification notification) {
        UIView.animate(0.3, new Runnable() {
            @Override
            public void run() {
                UIViewLayoutBridgeUtil.findAndScrollToFirstResponder(LayoutBridge.this);
            }
        });
    }

    @Method(selector = "didEndEditing:")
    public void didEndEditing(NSNotification notification) {

    }
}
