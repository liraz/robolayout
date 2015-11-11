package org.lirazs.robolayout.core.resource.drawable;

import org.lirazs.robolayout.core.resource.ResourceManager;
import org.lirazs.robolayout.core.resource.XMLCache;
import org.robovm.apple.coregraphics.CGContext;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.foundation.NSZone;
import org.robovm.apple.uikit.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created on 7/30/2015.
 */
public abstract class Drawable extends NSObject {
    public static int MAX_LEVEL = 10000;

    private CGRect bounds = CGRect.Zero();
    private UIControlState state;
    private int level;
    private DrawableConstantState constantState;

    protected DrawableDelegate delegate;

    public static Drawable create(Element element) {
        Drawable drawable = null;
        String tagName = element.getTagName();

        Class<? extends Drawable> drawableClass = null;

        if(tagName.equals("selector")) {
            drawableClass = StateListDrawable.class;
        } else if(tagName.equals("layer-list")) {
            drawableClass = LayerDrawable.class;
        } else if(tagName.equals("color")) {
            drawableClass = ColorDrawable.class;
        } else if(tagName.equals("bitmap")) {
            drawableClass = BitmapDrawable.class;
        } else if(tagName.equals("inset")) {
            drawableClass = InsetDrawable.class;
        } else if(tagName.equals("nine-patch")) {
            drawableClass = NinePatchDrawable.class;
        } else if(tagName.equals("shape")) {
            drawableClass = GradientDrawable.class;
        } else if(tagName.equals("clip")) {
            drawableClass = ClipDrawable.class;
        } else if(tagName.equals("rotate")) {
            drawableClass = RotateDrawable.class;
        } else if(tagName.equals("shadow")) {
            drawableClass = ShadowDrawable.class;
        } else {
            try {
                drawableClass = (Class<? extends Drawable>) Class.forName(tagName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if(drawableClass != null) {
            try {
                drawable = drawableClass.newInstance();
                drawable.inflate(element);
            } catch (InstantiationException | IllegalAccessException | ParseException e) {
                e.printStackTrace();
            }
        }
        return drawable;
    }

    public static Drawable create(NSData data) {
        Drawable drawable = null;

        if(data == null)
            return null;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        byte[] bytes = data.getBytes();
        if(bytes != null) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

            try {
                DocumentBuilder db = factory.newDocumentBuilder();
                InputSource inputSource = new InputSource(inputStream);
                Document document = db.parse(inputSource);

                drawable = create(document.getDocumentElement());
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }
        return drawable;
    }

    public static Drawable create(NSURL url) {
        Drawable drawable = null;

        ResourceManager resourceManager = ResourceManager.getCurrent();
        XMLCache cache = resourceManager.getXmlCache();

        try {
            Document xml = cache.getXML(url);
            drawable = create(xml.getDocumentElement());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return drawable;
    }

    public Drawable() {
    }

    public Drawable(DrawableConstantState constantState) {
        this.constantState = constantState;
    }

    public CGRect getBounds() {
        return bounds;
    }

    public UIControlState getState() {
        return state;
    }

    public int getLevel() {
        return level;
    }

    public void setDelegate(DrawableDelegate delegate) {
        this.delegate = delegate;
    }

    public abstract void inflate(Element element) throws ParseException;

    public abstract void onStateChangeToState(UIControlState state);

    public abstract void onBoundsChangeToRect(CGRect bounds);

    public abstract boolean onLevelChangeToLevel(int level);

    public boolean isStateful() {
        return false;
    }

    public boolean hasPadding() {
        return false;
    }

    public UIEdgeInsets getPadding() {
        return UIEdgeInsets.Zero();
    }

    public void invalidateSelf() {
        if(delegate != null)
            delegate.drawableDidInvalidate(this);
    }

    public Drawable getCurrent() {
        return this;
    }

    public CGSize getIntrinsicSize() {
        return new CGSize(-1, -1);
    }

    public CGSize getMinimumSize() {
        CGSize size = getIntrinsicSize();

        size.setWidth(Math.max(size.getWidth(), 0));
        size.setHeight(Math.max(size.getHeight(), 0));

        return size;
    }

    public void drawInContext(CGContext context) {
        context.saveGState();
        context.setStrokeColor(UIColor.red().getCGColor());
        context.setLineWidth(1);

        context.strokeRect(bounds);
        context.restoreGState();
    }

    public UIImage renderToImage() {
        UIGraphics.beginImageContext(bounds.getSize());

        CGContext context = UIGraphics.getCurrentContext();
        drawInContext(context);

        UIImage image = UIGraphics.getImageFromCurrentImageContext();
        UIGraphics.endImageContext();

        return image;
    }

    public void setBounds(CGRect bounds) {
        if(!bounds.equalsTo(this.bounds)) {
            this.bounds = bounds;
            onBoundsChangeToRect(bounds);
        }
    }

    public void setState(UIControlState state) {
        if(!state.equals(this.state)) {
            this.state = state;
            onStateChangeToState(state);
        }
    }

    public boolean setLevel(int level) {
        boolean result = false;

        if(this.level != level) {
            this.level = level;
            result = onLevelChangeToLevel(level);
        }
        return result;
    }

    public DrawableConstantState getConstantState() {
        return constantState;
    }

    public void setConstantState(DrawableConstantState constantState) {
        this.constantState = constantState;
    }

    public Drawable copy() {
        Object clone = null;
        try {
            clone = this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return (Drawable) clone;
    }
}
