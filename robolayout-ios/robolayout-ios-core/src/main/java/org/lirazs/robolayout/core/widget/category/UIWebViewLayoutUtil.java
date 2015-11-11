package org.lirazs.robolayout.core.widget.category;

import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.foundation.NSURLRequest;
import org.robovm.apple.uikit.UIWebView;

import java.util.Map;

/**
 * Created by mac on 7/29/15.
 */
public class UIWebViewLayoutUtil {

    public static void applyAttributes(UIWebView webview, Map<String, String> attrs, NSObject actionTarget) {
        UIViewLayoutUtil.applyAttributes(webview, attrs);

        String src = attrs.get("src");
        if(src != null) {
            NSURL url = new NSURL(src);
            webview.loadRequest(new NSURLRequest(url));
        }
    }

}
