package org.lirazs.robolayout.core.view.inflater;

import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.lirazs.robolayout.core.widget.category.*;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created on 8/4/2015.
 */
public class BaseViewFactory implements ViewFactory {

    public UIButtonType getButtonType(String typeAttribute) {
        UIButtonType result = UIButtonType.Custom;

        if(typeAttribute == null || typeAttribute.equals("custom")) {
            result = UIButtonType.Custom;
        } else if(typeAttribute.equals("roundedRect")) {
            result = UIButtonType.RoundedRect;
        } else if(typeAttribute.equals("detailDisclosure")) {
            result = UIButtonType.DetailDisclosure;
        } else if(typeAttribute.equals("infoLight")) {
            result = UIButtonType.InfoLight;
        } else if(typeAttribute.equals("infoDark")) {
            result = UIButtonType.InfoDark;
        } else if(typeAttribute.equals("contactAdd")) {
            result = UIButtonType.ContactAdd;
        }
        return result;
    }

    public UIButton createUIButton(Map<String, String> attrs, NSObject actionTarget) {
        String type = attrs.get("type");

        UIButtonType buttonType = getButtonType(type);
        UIButton button = UIButton.create(buttonType);

        UIButtonLayoutUtil.applyAttributes(button, attrs, actionTarget);

        return button;
    }

    @Override
    public UIView createView(String name, Map<String, String> attributes, NSObject actionTarget) throws IllegalAccessException,
            InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {

        UIView result = null;

        Class<? extends UIView> viewClass;

        if(name.contains(".")) { // has a full package
            viewClass = (Class<? extends UIView>) Class.forName(name);
        } else { // searching class by simple name
            viewClass = findClassBySimpleName(name);
        }

        // support for UIKit classes
        if(viewClass.equals(UIButton.class)) {
            result = createUIButton(attributes, actionTarget);

        }else if(viewClass.equals(UISwitch.class)) {
            UISwitch view = (UISwitch) viewClass.newInstance();
            UISwitchLayoutUtil.applyAttributes(view, attributes, actionTarget);
            result = view;

        } else if(viewClass.equals(UIControl.class)) {
            UIControl view = (UIControl) viewClass.newInstance();
            UIControlLayoutUtil.applyAttributes(view, attributes, actionTarget);
            result = view;

        } else if(viewClass.equals(UIImageView.class)) {
            UIImageView view = (UIImageView) viewClass.newInstance();
            UIImageViewLayoutUtil.applyAttributes(view, attributes, actionTarget);
            result = view;

        } else if(viewClass.equals(UINavigationBar.class)) {
            UINavigationBar view = (UINavigationBar) viewClass.newInstance();
            UINavigationBarLayoutUtil.applyAttributes(view, attributes, actionTarget);
            result = view;

        } else if(viewClass.equals(UISearchBar.class)) {
            UISearchBar view = (UISearchBar) viewClass.newInstance();
            UISearchBarLayoutUtil.applyAttributes(view, attributes, actionTarget);
            result = view;

        } else if(viewClass.equals(UIToolbar.class)) {
            UIToolbar view = (UIToolbar) viewClass.newInstance();
            UIToolbarLayoutUtil.applyAttributes(view, attributes, actionTarget);
            result = view;

        } else if(viewClass.equals(UIWebView.class)) {
            UIWebView view = (UIWebView) viewClass.newInstance();
            UIWebViewLayoutUtil.applyAttributes(view, attributes, actionTarget);
            result = view;

        } else if(viewClass.equals(UIView.class)) {
            UIView view = viewClass.newInstance();
            UIViewLayoutUtil.applyAttributes(view, attributes);
            result = view;

        } else { // we have a custom class
            Class[] cArgWithMap = new Class[1];
            cArgWithMap[0] = Map.class; // first argument must be a map of attributes

            Class[] cArgWithMapAndAction = new Class[2];
            cArgWithMapAndAction[0] = Map.class; // first argument must be a map of attributes
            cArgWithMapAndAction[1] = NSObject.class; // second argument must be a target object

            // look for the constructor that supports action target as well
            try {
                result = (UIView) viewClass.getDeclaredConstructor(cArgWithMapAndAction).newInstance(attributes, actionTarget);
            } catch (NoSuchMethodException | SecurityException ignored) {
            }

            if (result == null) {
                result = (UIView) viewClass.getDeclaredConstructor(cArgWithMap).newInstance(new Object[]{attributes});
            }
        }

        return result;
    }

    public static final String[] searchPackages = {
            "org.robovm.apple.uikit",
            "org.lirazs.robolayout.core.widget",
            "org.lirazs.robolayout.core.widget.layout",
            "org.lirazs.robolayout.core.widget.layout.linear",
            "org.lirazs.robolayout.core.widget.layout.relative",
    };

    public Class<? extends UIView> findClassBySimpleName(String name) {
        for(int i=0; i < searchPackages.length; i++){
            try{
                return (Class<? extends UIView>) Class.forName(searchPackages[i] + "." + name);
            } catch (ClassNotFoundException e){
                //not in this package, try another
            }
        }
        //nothing found: return null or throw ClassNotFoundException
        return null;
    }
}
