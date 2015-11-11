package org.lirazs.robolayout.core.view.inflater;

import org.lirazs.robolayout.core.resource.ResourceManager;
import org.lirazs.robolayout.core.resource.values.Style;
import org.lirazs.robolayout.core.util.DOMUtil;
import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.lirazs.robolayout.core.util.UIViewViewGroupUtil;
import org.lirazs.robolayout.core.view.LayoutParams;
import org.lirazs.robolayout.core.view.LayoutParamsDelegate;
import org.lirazs.robolayout.core.view.ViewVisibility;
import org.apache.commons.io.FilenameUtils;
import org.robovm.apple.foundation.*;
import org.robovm.apple.uikit.UIView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created on 8/4/2015.
 */
public class LayoutInflater {
    public static String ViewAttributeActionTarget = "__actionTarget";

    public static String TAG_MERGE = "merge";
    public static String TAG_INCLUDE = "include";
    public static String INCLUDE_ATTRIBUTE_LAYOUT = "layouts";

    public Map<String, String> getAttributes(Element element, Map<String, String> reuseMap) {
        Map<String, String> attrs = DOMUtil.getAttributesFromElement(element, reuseMap);
        //Apply style
        String styleAttribute = attrs.get("style");
        if(styleAttribute != null && !styleAttribute.isEmpty()) {
            Style style = ResourceManager.getCurrent().getStyle(styleAttribute);
            if(style != null) {
                Map<String, String> styleAttributes = style.getAttributes();
                for (String name : styleAttributes.keySet()) {
                    if(!attrs.containsKey(name)) {

                    }
                }
            }
            attrs.remove("style");
        }
        return attrs;
    }

    private ViewFactory viewFactory;
    private NSObject actionTarget;

    public LayoutInflater() {
        this.viewFactory = new BaseViewFactory();
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    public void setActionTarget(NSObject actionTarget) {
        this.actionTarget = actionTarget;
    }

    public UIView createView(String name, Map<String, String> attrs, UIView parent) {
        if(name.equals("view")) {
            name = attrs.get("class");
        }
        UIView result = null;
        try {
            result = viewFactory.createView(name, attrs, this.actionTarget);
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | NoSuchMethodException| InvocationTargetException e) {
            e.printStackTrace();

            Foundation.log(String.format("Warning!!!!! Could not initialize class for view with name %s. Creating UIView instead: %s", name, e));
            try {
                result = viewFactory.createView("UIView", attrs, this.actionTarget);
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | NoSuchMethodException| InvocationTargetException e1) {
                e1.printStackTrace();
            }
        }
        return result;
    }

    public void parseInclude(Element element, UIView parentView, Map<String, String> attrs) {
        if(!UIViewViewGroupUtil.isViewGroup(parentView)) {
            Foundation.log("<include /> can only be used in view groups");
            return;
        }

        String layoutToInclude = attrs.get(INCLUDE_ATTRIBUTE_LAYOUT);
        if(layoutToInclude == null) {
            Foundation.log("You must specify a layout in the include tag: <include layout=\\\"@layout/layoutName\\\" />");
        } else {
            NSURL url = ResourceManager.getCurrent().getLayoutURL(layoutToInclude);
            if(url == null) {
                Foundation.log(String.format("You must specify a valid layout reference. The layout ID %s is not valid.", layoutToInclude));
            } else {
                try {
                    Document xml = ResourceManager.getCurrent().getXmlCache().getXML(url);
                    Element rootElement = xml.getDocumentElement();
                    String elementName = rootElement.getTagName();

                    Map<String, String> childAttrs = getAttributes(rootElement, null);
                    Element firstElementChild = DOMUtil.getFirstElementChild(rootElement);

                    if(elementName.equals(TAG_MERGE) && firstElementChild != null) {
                        recursiveInflate(firstElementChild, parentView, childAttrs, true);
                    } else {
                        UIView temp = createView(elementName, childAttrs, parentView);

                        // We try to load the layout params set in the <include /> tag. If
                        // they don't exist, we will rely on the layout params set in the
                        // included XML file.
                        // During a layoutparams generation, a runtime exception is thrown
                        // if either layout_width or layout_height is missing. We catch
                        // this exception and set localParams accordingly: true means we
                        // successfully loaded layout params from the <include /> tag,
                        // false means we need to rely on the included layout params.
                        LayoutParams layoutParams = UIViewLayoutUtil.generateLayoutParams(parentView, attrs);
                        boolean validLayoutParams = UIViewLayoutUtil.checkLayoutParams(parentView, layoutParams);

                        if(!validLayoutParams && parentView instanceof LayoutParamsDelegate) {
                            layoutParams = UIViewLayoutUtil.generateLayoutParams(parentView, childAttrs);
                        } else if(!validLayoutParams) {
                            layoutParams = UIViewLayoutUtil.generateDefaultLayoutParams(parentView);
                        }
                        UIViewLayoutUtil.setLayoutParams(temp, layoutParams);

                        // Inflate all children
                        if(firstElementChild != null) {
                            recursiveInflate(firstElementChild, temp, childAttrs, true);
                        }

                        // Attempt to override the included layout's id with the
                        // one set on the <include /> tag itself.
                        String overwriteIdentifier = attrs.get("id");
                        if(overwriteIdentifier != null) {
                            UIViewLayoutUtil.setIdentifier(temp, overwriteIdentifier);
                        }

                        // While we're at it, let's try to override visibility.
                        String overwriteVisibility = attrs.get("visibility");
                        if(overwriteVisibility != null) {
                            UIViewLayoutUtil.setVisibility(temp, ViewVisibility.get(overwriteVisibility));
                        }
                        parentView.addSubview(temp);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Foundation.log(String.format("Cannot include layout %s: %s %s", layoutToInclude, e.getLocalizedMessage(), e.getCause()));
                }
            }
        }
    }

    protected void recursiveInflate(Element element, UIView parentView, Map<String, String> attrs, boolean finishInflate) {
        Node siblingNode = element;

        do {
            if (siblingNode.getNodeType() == Node.ELEMENT_NODE) {
                Element siblingElement = (Element) siblingNode;

                String tagName = siblingElement.getTagName();
                Map<String, String> childAttrs = getAttributes(siblingElement, attrs);
                if(tagName.equals(TAG_INCLUDE)) {
                    // Include other resource
                    parseInclude(siblingElement, parentView, childAttrs);
                } else {
                    // Create view from element and attach to parent
                    UIView view = createView(tagName, childAttrs, parentView);
                    LayoutParams layoutParams = null;

                    if(parentView instanceof LayoutParamsDelegate) {
                        layoutParams = UIViewLayoutUtil.generateLayoutParams(parentView, attrs);
                    } else {
                        layoutParams = UIViewLayoutUtil.generateDefaultLayoutParams(parentView);
                    }
                    UIViewLayoutUtil.setLayoutParams(view, layoutParams);

                    Element firstElementChild = DOMUtil.getFirstElementChild(siblingElement);

                    if(firstElementChild != null) {
                        recursiveInflate(firstElementChild, view, attrs, true);
                    }
                    UIViewViewGroupUtil.addView(parentView, view);
                }
            }

        } while((siblingNode = siblingNode.getNextSibling()) != null);

        if(finishInflate) {
            UIViewLayoutUtil.onFinishInflate(parentView);
        }
    }

    public UIView inflate(Document document, UIView rootView, boolean attachRoot) {
        UIView result = null;
        if(rootView != null && !UIViewViewGroupUtil.isViewGroup(rootView)) {
            Foundation.log("rootView must be ViewGroup");
            return null;
        }

        Element rootElement = document.getDocumentElement();
        String elementName = rootElement.getTagName();

        Map<String, String> attrs = getAttributes(rootElement, null);
        if(elementName.equals(TAG_MERGE)) {

            Element firstElementChild = DOMUtil.getFirstElementChild(rootElement);

            if(rootView == null || !attachRoot) {
                Foundation.log("<merge /> can be used only with a valid ViewGroup root and attachToRoot=true");
                return null;

            } else if(firstElementChild != null) {
                recursiveInflate(firstElementChild, rootView, attrs, true);
            }
            result = rootView;
        } else {
            UIView temp = createView(elementName, attrs, rootView);

            if(rootView != null) {
                LayoutParams layoutParams = null;
                if(rootView instanceof LayoutParamsDelegate) {
                    layoutParams = UIViewLayoutUtil.generateLayoutParams(rootView, attrs);
                } else {
                    layoutParams = UIViewLayoutUtil.generateDefaultLayoutParams(rootView);
                }
                UIViewLayoutUtil.setLayoutParams(temp, layoutParams);
            }

            Element firstElementChild = DOMUtil.getFirstElementChild(rootElement);
            if(firstElementChild != null) {
                recursiveInflate(firstElementChild, temp, attrs, true);
            }
            if(attachRoot && rootView != null) {
                rootView.addSubview(temp);
                result = rootView;
            } else {
                result = temp;
            }
        }
        return result;
    }

    public UIView inflate(NSURL url, UIView rootView, boolean attachToRoot) {
        UIView result = null;
        NSDate methodStart = new NSDate();

        try {
            Document xml = ResourceManager.getCurrent().getXmlCache().getXML(url);
            result = inflate(xml, rootView, attachToRoot);

            double timeIntervalSince = NSDate.now().getTimeIntervalSince(methodStart);
            Foundation.log(String.format("Inflation of %s took %.2fms", url.getAbsoluteString(), timeIntervalSince));

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public UIView inflate(String resource, UIView rootView, boolean attachToRoot) {
        NSBundle bundle = NSBundle.getMainBundle();

        String extension = FilenameUtils.getExtension(resource);

        if(extension.isEmpty()) {
            extension = "xml";
        }
        String fileName = resource.substring(0, resource.indexOf('.'));
        NSURL url = bundle.findResourceURL(fileName, extension);

        return inflate(url, rootView, attachToRoot);
    }


}
