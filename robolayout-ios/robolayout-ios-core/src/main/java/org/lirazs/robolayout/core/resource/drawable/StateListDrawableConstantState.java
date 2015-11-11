package org.lirazs.robolayout.core.resource.drawable;

import org.robovm.apple.uikit.UIControlState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 7/30/2015.
 */
public class StateListDrawableConstantState extends DrawableContainerConstantState {

    private List<StateListDrawableItem> items;

    public StateListDrawableConstantState(StateListDrawableConstantState state, StateListDrawable owner) {
        super(state, owner);

        if(state != null) {
            int count = Math.min(this.getDrawables().size(), state.getItems().size());
            List<StateListDrawableItem> items = new ArrayList<>(count);

            for (int i = 0; i < count; i++) {
                StateListDrawableItem origItem = state.getItems().get(i);
                StateListDrawableItem item = new StateListDrawableItem();

                item.setDrawable(this.getDrawables().get(i));
                item.setState(origItem.getState());

                items.add(item);
            }
            this.items = items;
        } else {
            this.items = new ArrayList<>(10);
        }
    }

    public List<StateListDrawableItem> getItems() {
        return items;
    }

    public void addDrawable(Drawable drawable, UIControlState state) {
        StateListDrawableItem item = new StateListDrawableItem();
        item.setDrawable(drawable);
        item.setState(state);

        items.add(item);
        addChildDrawable(drawable);
    }
}
