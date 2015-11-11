package org.lirazs.robolayout.core.resource.drawable;

import org.lirazs.robolayout.core.resource.ResourceManager;
import org.lirazs.robolayout.core.resource.state.ColorStateList;
import org.lirazs.robolayout.core.resource.state.ResourceStateItem;
import org.lirazs.robolayout.core.util.DOMUtil;
import org.lirazs.robolayout.core.util.ResourceAttributesUtil;
import org.robovm.apple.uikit.UIControlState;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.text.ParseException;
import java.util.Map;

/**
 * Created on 7/30/2015.
 */
public class StateListDrawable extends DrawableContainer {

    private StateListDrawableConstantState internalConstantState;

    public StateListDrawable() {
        this.internalConstantState = new StateListDrawableConstantState(null, this);
    }
    public StateListDrawable(StateListDrawableConstantState state) {
        super();

        this.internalConstantState = new StateListDrawableConstantState(state, this);
    }
    public StateListDrawable(ColorStateList state) {
        super();

        for (ResourceStateItem item : state.getItems()) {
            ColorDrawable colorDrawable = new ColorDrawable(item.getColor());
            internalConstantState.addDrawable(colorDrawable, item.getControlState());
        }
    }

    @Override
    public void inflate(Element element) throws ParseException {
        super.inflate(element);

        Map<String, String> attrs = DOMUtil.getAttributesFromElement(element);

        internalConstantState.setConstantSize(ResourceAttributesUtil.getBooleanValue(attrs, "constantSize"));

        Node child = element.getFirstChild();
        while(child != null) {
            String tagName = child.getNodeName();

            if(tagName.equals("item")) {
                attrs = DOMUtil.getAttributesFromNode(child, attrs);

                long state = UIControlState.Normal.value();
                for (String attributeName : attrs.keySet()) {
                    boolean value = ResourceAttributesUtil.getBooleanValue(attrs, attributeName);
                    if(value) {
                        state |= getControlState(attributeName).value();
                    }
                }

                String drawableResId = attrs.get("drawables");
                Drawable drawable = null;

                Element firstElementChild = DOMUtil.getFirstElementChild((Element) child);

                if(drawableResId != null) {
                    drawable = ResourceManager.getCurrent().getDrawable(drawableResId);
                } else if(firstElementChild != null) {
                    drawable = Drawable.create(firstElementChild);
                } else {
                    throw new ParseException("<item> tag requires a 'drawable' attribute or child tag defining a drawable", 0);
                }

                if(drawable != null) {
                    internalConstantState.addDrawable(drawable, new UIControlState(state));
                }
            }
            child = child.getNextSibling();
        }
    }

    public int getIndexOfState(UIControlState state) {
        int result = -1;
        int count = internalConstantState.getItems().size();

        for (int i = 0; i < count; i++) {
            StateListDrawableItem item = internalConstantState.getItems().get(i);
            if((item.getState().value() & state.value()) == item.getState().value()) {
                result = i;
                break;
            }
        }
        return result;
    }

    @Override
    public void onStateChangeToState(UIControlState state) {
        int idx = getIndexOfState(this.getState());

        if(!selectDrawableAtIndex(idx)) {
            super.onStateChangeToState(state);
        }
    }

    @Override
    public boolean isStateful() {
        return true;
    }

    public UIControlState getControlState(String attributeName) {
        UIControlState controlState = UIControlState.Normal;

        if(attributeName.equals("state_disabled")) {
            controlState = UIControlState.Disabled;

        } else if(attributeName.equals("state_highlighted") || attributeName.equals("state_pressed")
                || attributeName.equals("state_focused")) {
            controlState = UIControlState.Highlighted;

        } else if(attributeName.equals("state_selected")) {
            controlState = UIControlState.Selected;
        }
        return controlState;
    }


}
