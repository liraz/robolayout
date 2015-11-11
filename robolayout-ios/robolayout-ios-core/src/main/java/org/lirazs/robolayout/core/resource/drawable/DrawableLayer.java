package org.lirazs.robolayout.core.resource.drawable;

import org.robovm.apple.coreanimation.CAAction;
import org.robovm.apple.coreanimation.CAActionIdentifier;
import org.robovm.apple.coreanimation.CALayer;
import org.robovm.apple.coregraphics.CGContext;
import org.robovm.apple.coregraphics.CGInterpolationQuality;
import org.robovm.apple.foundation.NSCoder;
import org.robovm.apple.foundation.NSNull;
import org.robovm.apple.uikit.UIScreen;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 7/31/2015.
 */
public class DrawableLayer extends CALayer implements DrawableDelegate {

    private Drawable drawable;

    public static boolean needsDisplay(String key) {
        return key.equals("drawables") || CALayer.needsDisplay(key);
    }

    public DrawableLayer() {
        super();
        init();
    }

    @Override
    protected long init(NSCoder nsCoder) {
        long init = super.init(nsCoder);
        init();

        return init;
    }

    @Override
    protected long init(CALayer caLayer) {
        long init = super.init(caLayer);

        setContentsScale(UIScreen.getMainScreen().getScale());

        DrawableLayer l = (DrawableLayer) caLayer;
        this.drawable = l.drawable.copy();
        this.drawable.setDelegate(this);
        this.drawable.setLevel(l.drawable.getLevel());
        this.drawable.setBounds(l.drawable.getBounds());

        return init;
    }

    protected long init() {
        setContentsScale(UIScreen.getMainScreen().getScale());
        setNeedsDisplayOnBoundsChange(true);

        Map newActions = new HashMap<>();
        newActions.put(CAActionIdentifier.OnOrderIn(), NSNull.getNull());
        newActions.put(CAActionIdentifier.OnOrderOut(), NSNull.getNull());
        newActions.put("sublayers", NSNull.getNull());
        newActions.put("contents", NSNull.getNull());

        setActions(newActions);

        return super.init();
    }

    @Override
    public CAAction getAction(String key) {
        return null;
    }

    public void setDrawable(Drawable drawable) {
        if(this.drawable == null || !this.drawable.equals(drawable)) {
            if (this.drawable != null) {
                this.drawable.setDelegate(null);
            }
            this.drawable = drawable;
            this.drawable.setDelegate(this);

            setNeedsDisplay();
        }
    }

    public void drawInContext(CGContext context) {
        context.setAllowsAntialiasing(true);
        context.setShouldAntialias(true);
        context.setInterpolationQuality(CGInterpolationQuality.High);

        this.drawable.drawInContext(context);
    }

    @Override
    public void drawableDidInvalidate(Drawable drawable) {
        setNeedsDisplay();
    }

    @Override
    public void layoutSublayers() {
        super.layoutSublayers();

        this.drawable.setBounds(getBounds());
    }
}
