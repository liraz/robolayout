package org.lirazs.robolayout.core.resource.drawable;

import org.robovm.apple.uikit.UIEdgeInsets;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 7/31/2015.
 */
public class LayerDrawableConstantState extends DrawableConstantState {

    private List<LayerDrawableItem> items;

    private boolean paddingComputed;
    private boolean hasPadding;
    private UIEdgeInsets padding;

    public LayerDrawableConstantState(LayerDrawableConstantState state, LayerDrawable owner) {
        super();

        if(state != null) {
            List<LayerDrawableItem> items = new ArrayList<>(state.items.size());

            for (LayerDrawableItem origItem : state.items) {
                LayerDrawableItem item = new LayerDrawableItem();
                Drawable drawable = origItem.getDrawable().copy();
                drawable.setDelegate(owner);
                item.setDrawable(drawable);
                item.setInsets(origItem.getInsets());

                items.add(item);
            }
            this.items = items;
        } else {
            this.items = new ArrayList<>(10);
        }
    }

    public void addLayer(Drawable drawable, UIEdgeInsets insets, LayerDrawable owner) {
        LayerDrawableItem item = new LayerDrawableItem();
        item.setDrawable(drawable);
        item.setInsets(insets);

        items.add(item);
        this.paddingComputed = false;
    }

    public void computePadding() {
        UIEdgeInsets padding = UIEdgeInsets.Zero();
        boolean hasPadding = false;

        for (LayerDrawableItem item : this.items) {
            Drawable drawable = item.getDrawable();
            if(drawable.hasPadding()) {
                hasPadding = true;

                UIEdgeInsets childPadding = drawable.getPadding();
                padding.setLeft(Math.max(padding.getLeft(), childPadding.getLeft()));
                padding.setRight(Math.max(padding.getRight(), childPadding.getRight()));
                padding.setTop(Math.max(padding.getTop(), childPadding.getTop()));
                padding.setBottom(Math.max(padding.getBottom(), childPadding.getBottom()));
            }
        }

        this.padding = padding;
        this.hasPadding = hasPadding;
        this.paddingComputed = true;
    }

    public boolean isPaddingComputed() {
        return paddingComputed;
    }

    public UIEdgeInsets getPadding() {
        UIEdgeInsets padding = UIEdgeInsets.Zero();
        if(!this.isPaddingComputed()) {
            computePadding();
        }
        this.padding = padding;
        return padding;
    }

    public boolean hasPadding() {
        if(!this.isPaddingComputed()) {
            computePadding();
        }
        return this.hasPadding;
    }

    public List<LayerDrawableItem> getItems() {
        return items;
    }
}
