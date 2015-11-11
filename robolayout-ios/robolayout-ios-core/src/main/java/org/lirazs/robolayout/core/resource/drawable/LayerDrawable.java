package org.lirazs.robolayout.core.resource.drawable;

import org.lirazs.robolayout.core.resource.ResourceManager;
import org.lirazs.robolayout.core.util.DOMUtil;
import org.robovm.apple.coregraphics.CGContext;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEdgeInsets;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.text.ParseException;
import java.util.Map;

/**
 * Created on 7/31/2015.
 */
public class LayerDrawable extends Drawable implements DrawableDelegate {

    private LayerDrawableConstantState internalConstantState;

    public LayerDrawable() {
        this(null);
    }
    public LayerDrawable(LayerDrawableConstantState state) {
        this.internalConstantState = new LayerDrawableConstantState(state, this);
    }

    @Override
    public void inflate(Element element) throws ParseException {

        Map<String, String> attrs = null;
        Node child = element.getFirstChild();

        while(child != null) {
            String tagName = child.getNodeName();
            if(tagName.equals("item")) {
                attrs = DOMUtil.getAttributesFromNode(child, attrs);

                UIEdgeInsets insets = UIEdgeInsets.Zero();
                insets.setLeft(Double.parseDouble(attrs.get("left")));
                insets.setTop(Double.parseDouble(attrs.get("top")));
                insets.setRight(Double.parseDouble(attrs.get("right")));
                insets.setBottom(Double.parseDouble(attrs.get("bottom")));

                String drawableResId = attrs.get("drawables");
                Drawable drawable = null;

                Element firstElementChild = DOMUtil.getFirstElementChild((Element) child);

                if(drawableResId != null) {
                    drawable = ResourceManager.getCurrent().getDrawable(drawableResId);
                } else if(firstElementChild != null) {
                    drawable = Drawable.create(firstElementChild);
                }
                if(drawable != null) {
                    internalConstantState.addLayer(drawable, insets, this);
                }
            }

            child = child.getNextSibling();
        }
    }

    @Override
    public UIEdgeInsets getPadding() {
        return internalConstantState.getPadding();
    }

    @Override
    public boolean hasPadding() {
        return internalConstantState.hasPadding();
    }

    @Override
    public DrawableConstantState getConstantState() {
        return internalConstantState;
    }

    @Override
    public CGSize getIntrinsicSize() {
        CGSize size = new CGSize(-1, -1);
        for (LayerDrawableItem item : internalConstantState.getItems()) {
            UIEdgeInsets insets = item.getInsets();
            CGSize s = item.getDrawable().getIntrinsicSize();

            s.setWidth(s.getWidth() + insets.getLeft() + insets.getRight());
            s.setHeight(s.getHeight() + insets.getTop() + insets.getBottom());

            if(s.getWidth() > size.getWidth()) {
                size.setWidth(s.getWidth());
            }
            if(s.getHeight() > size.getHeight()) {
                size.setHeight(s.getHeight());
            }
        }

        return size;
    }

    @Override
    public void onStateChangeToState(UIControlState state) {
        for (LayerDrawableItem item : internalConstantState.getItems()) {
            item.getDrawable().setState(this.getState());
        }
    }

    @Override
    public void onBoundsChangeToRect(CGRect bounds) {
        for (LayerDrawableItem item : internalConstantState.getItems()) {
            CGRect insetRect = getBounds().inset(item.getInsets());
            item.getDrawable().setBounds(insetRect);
        }
    }

    @Override
    public boolean onLevelChangeToLevel(int level) {
        LayerDrawableConstantState state = this.internalConstantState;
        boolean changed = false;

        for (LayerDrawableItem item : state.getItems()) {
            if(item.getDrawable().setLevel(level)) {
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public void drawInContext(CGContext context) {
        for (LayerDrawableItem item : internalConstantState.getItems()) {
            context.saveGState();
            item.getDrawable().drawInContext(context);
            context.restoreGState();
        }

        super.drawInContext(context);
    }

    @Override
    public void drawableDidInvalidate(Drawable drawable) {
        if(delegate != null) delegate.drawableDidInvalidate(drawable);
    }
}
