package org.lirazs.robolayout.core.view.inflater;

import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.UIView;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created on 8/4/2015.
 */
public interface ViewFactory {

    UIView createView(String name, Map<String, String> attributes, NSObject actionTarget) throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException;
}
