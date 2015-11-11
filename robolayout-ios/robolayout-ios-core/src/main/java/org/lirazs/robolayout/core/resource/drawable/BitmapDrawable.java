package org.lirazs.robolayout.core.resource.drawable;

import org.lirazs.robolayout.core.resource.ResourceManager;
import org.lirazs.robolayout.core.util.DOMUtil;
import org.lirazs.robolayout.core.util.UIImageUtil;
import org.lirazs.robolayout.core.view.Gravity;
import org.lirazs.robolayout.core.view.ViewContentGravity;
import org.robovm.apple.coregraphics.CGBlendMode;
import org.robovm.apple.coregraphics.CGContext;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSNotification;
import org.robovm.apple.foundation.NSNotificationCenter;
import org.robovm.apple.foundation.NSOperationQueue;
import org.robovm.apple.uikit.*;
import org.robovm.objc.Selector;
import org.robovm.objc.annotation.Method;
import org.robovm.objc.block.VoidBlock1;
import org.w3c.dom.Element;

import java.text.ParseException;
import java.util.Map;

/**
 * Created on 7/31/2015.
 */
public class BitmapDrawable extends Drawable {

    private BitmapDrawableConstantState internalConstantState;
    private UIImage scaledImageCache;

    public BitmapDrawable() {
        this(null, null);
    }

    public BitmapDrawable(UIImage image) {
        this(null, image);
    }

    public BitmapDrawable(BitmapDrawableConstantState state) {
        this(state, null);
    }

    public BitmapDrawable(BitmapDrawableConstantState state, UIImage image) {
        super();

        NSNotificationCenter defaultCenter = NSNotificationCenter.getDefaultCenter();
        defaultCenter.addObserver(this, Selector.register("didReceiveMemoryWarning:"), UIApplication.DidReceiveMemoryWarningNotification(), null);

        BitmapDrawableConstantState internalState = new BitmapDrawableConstantState(state);
        internalState.setImage(image);
        this.internalConstantState = internalState;
    }

    public UIImage getImage() {
        return internalConstantState.getImage();
    }

    public UIImage resizeImage(UIImage image, double width, double height) {
        CGSize size = new CGSize(width, height);
        UIGraphics.beginImageContext(size, false, 0);

        CGContext context = UIGraphics.getCurrentContext();

        // Draw the original image to the context
        context.setBlendMode(CGBlendMode.Copy);
        image.draw(new CGRect(0, 0, width, height));

        // Retrieve the UIImage from the current context
        UIImage imageOut = UIGraphics.getImageFromCurrentImageContext();

        UIGraphics.endImageContext();

        return imageOut;
    }

    public void drawInContext(CGContext context) {
        BitmapDrawableConstantState state = this.internalConstantState;
        CGRect containerRect = this.getBounds();
        CGRect dstRect = CGRect.Zero();

        UIImage image = state.getImage();

        Gravity.applyGravity(state.getGravity(), image.getSize().getWidth(), image.getSize().getHeight(), containerRect, dstRect);

        if(scaledImageCache == null) {
            //self.scaledImageCache = [self resizeImage:image toWidth:dstRect.size.width height:dstRect.size.height];
        }
        UIGraphics.pushContext(context);
        //[self.scaledImageCache drawInRect:dstRect];
        image.draw(dstRect);

        UIGraphics.popContext();
    }

    public CGSize getIntrinsicSize() {
        return this.internalConstantState.getImage().getSize();
    }

    @Override
    public void inflate(Element element) throws ParseException {
        BitmapDrawableConstantState state = this.internalConstantState;

        Map<String, String> attributesFromElement = DOMUtil.getAttributesFromElement(element);
        String bitmapIdentifier = attributesFromElement.get("src");

        if(bitmapIdentifier != null) {
            ResourceManager resMgr = ResourceManager.getCurrent();
            UIImage image = resMgr.getImage(bitmapIdentifier);
            state.setImage(image);
        } else {
            throw new ParseException("<bitmap> requires a valid src attribute", 0);
        }

        String gravityValue = attributesFromElement.get("gravity");
        if(gravityValue != null) {
            state.setGravity(ViewContentGravity.getFromAttribute(gravityValue));
        }
    }

    @Override
    public CGSize getMinimumSize() {
        BitmapDrawableConstantState state = this.internalConstantState;
        //if(state.getImage().respondsToSelector(Selector.register("capInsets"))) {
            UIEdgeInsets insets = state.getImage().getCapInsets();

            if(insets.equalsTo(UIEdgeInsets.Zero())) {
                return new CGSize(insets.getLeft() + insets.getRight(), insets.getTop() + insets.getBottom());
            }
        //}

        return super.getMinimumSize();
    }

    @Override
    public boolean hasPadding() {
        return UIImageUtil.hasNinePatchPaddings(this.internalConstantState.getImage());
    }

    @Override
    public UIEdgeInsets getPadding() {
        return UIImageUtil.getNinePatchPaddings(this.internalConstantState.getImage());
    }

    @Override
    public DrawableConstantState getConstantState() {
        return this.internalConstantState;
    }

    @Override
    public void onStateChangeToState(UIControlState state) {

    }

    @Override
    public void onBoundsChangeToRect(CGRect bounds) {
        this.scaledImageCache = null;
    }

    @Override
    public boolean onLevelChangeToLevel(int level) {
        return false;
    }

    @Method(selector = "didReceiveMemoryWarning:")
    private void didReceiveMemoryWarning(NSNotification nsNotification) {
        this.scaledImageCache = null;
    }
}
