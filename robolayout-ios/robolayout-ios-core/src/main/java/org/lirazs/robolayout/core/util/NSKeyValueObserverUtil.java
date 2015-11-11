package org.lirazs.robolayout.core.util;

import org.robovm.apple.foundation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mac on 7/29/15.
 */
public class NSKeyValueObserverUtil {
    private static String KEY_VALUE_OBSERVERS_KEY = "KEY_VALUE_OBSERVERS_KEY";

    public static void addObserver(NSObject object, NSKeyValueObserver observer, String identifier,
                            List<String> keyPaths, NSKeyValueObservingOptions options) {

        Map<String, NSKeyValueObserver> keyValueObserverMap = getKeyValueObserverMap(object);

        for (String keyPath : keyPaths) {
            object.addKeyValueObserver(keyPath, observer, options);
        }

        keyValueObserverMap.put(identifier, observer);
    }

    public static void removeObserver(NSObject object, String identifier) {
        Map<String, NSKeyValueObserver> keyValueObserverMap = getKeyValueObserverMap(object);
        keyValueObserverMap.remove(identifier);
    }

    public static boolean hasObserver(NSObject object, String identifier) {
        Map<String, NSKeyValueObserver> keyValueObserverMap = getKeyValueObserverMap(object);
        return keyValueObserverMap.containsKey(identifier);
    }

    private static Map<String, NSKeyValueObserver> getKeyValueObserverMap(NSObject object) {
        NSDictionary<NSString, NSKeyValueObserver> nsDictionary = (NSDictionary<NSString, NSKeyValueObserver>) object.getAssociatedObject(KEY_VALUE_OBSERVERS_KEY);

        if(nsDictionary == null) {
            nsDictionary = new NSDictionary<>();
            object.setAssociatedObject(KEY_VALUE_OBSERVERS_KEY, nsDictionary);
        }
        return nsDictionary.asStringMap();
    }
}
