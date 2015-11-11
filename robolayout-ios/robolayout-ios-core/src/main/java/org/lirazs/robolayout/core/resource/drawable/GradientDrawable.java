package org.lirazs.robolayout.core.resource.drawable;

import org.lirazs.robolayout.core.util.DOMUtil;
import org.lirazs.robolayout.core.util.ResourceAttributesUtil;
import org.robovm.apple.coregraphics.*;
import org.robovm.apple.uikit.UIBezierPath;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEdgeInsets;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Map;

/**
 * Created on 7/31/2015.
 */
public class GradientDrawable extends Drawable {

    private GradientDrawableConstantState internalConstantState;

    public GradientDrawable() {
        this(null);
    }
    public GradientDrawable(GradientDrawableConstantState state) {
        this.internalConstantState = new GradientDrawableConstantState(state);
    }

    public void createPathInContext(CGContext context, CGRect rect) {
        GradientDrawableConstantState state = this.internalConstantState;
        GradientDrawableCornerRadius corners = state.getCorners();

        context.beginPath();

        double originX = rect.getOrigin().getX();
        double originY = rect.getOrigin().getY();

        double height = rect.getSize().getHeight();
        double width = rect.getSize().getWidth();

        switch (state.getShape()) {
            case Rectangle:
                context.moveToPoint(originX, originY + corners.getTopLeft());
                context.addLineToPoint(originX, originY + height - corners.getBottomLeft());

                if(corners.getBottomLeft() > 0) {
                    context.addArc(originX + corners.getBottomLeft(),
                            originY + height - corners.getBottomLeft(),
                            corners.getBottomLeft(), Math.PI, Math.PI / 2, 1);
                }
                context.addLineToPoint(originX + width - corners.getBottomRight(), originY + height);

                if(corners.getBottomRight() > 0) {
                    context.addArc(originX + width - corners.getBottomRight(),
                            originY + height - corners.getBottomRight(), corners.getBottomRight(),
                            Math.PI / 2, 0.0, 1);
                }
                context.addLineToPoint(originX + width, originY + corners.getTopRight());

                if(corners.getTopRight() > 0) {
                    context.addArc(originX + width - corners.getTopRight(),
                            originY + corners.getTopRight(), corners.getTopRight(), 0.0, (-Math.PI / 2), 1);
                }
                context.addLineToPoint(originX + corners.getTopLeft(), originY);

                if(corners.getTopLeft() > 0) {
                    context.addArc(originX + corners.getTopLeft(), originY + corners.getTopLeft(),
                            corners.getTopLeft(), (-Math.PI / 2), Math.PI, 1);
                }
                context.closePath();
                break;

            case Oval:
                context.addEllipseInRect(rect);
                break;

            case Ring:
                double thickness = state.getThickness() != -1 ? state.getThickness() : width / state.getThicknessRatio();

                // inner radius
                double radius = state.getInnerRadius() != -1 ? state.getInnerRadius() : width / state.getInnerRadius();
                double x = width / 2.d;
                double y = height / 2.d;
                CGRect innerRect = rect.inset(new UIEdgeInsets(y - radius, x - radius, y - radius, x - radius));
                //rect = UIEdgeInsetsInsetRect(innerRect, UIEdgeInsetsMake(-thickness, -thickness, -thickness, -thickness));

                double halfNegativeThickness = -thickness / 2;
                CGRect r = innerRect.inset(new UIEdgeInsets(halfNegativeThickness, halfNegativeThickness, halfNegativeThickness, halfNegativeThickness));
                context.setLineWidth(thickness);
                context.addEllipseInRect(r);
                context.replacePathWithStrokedPath();
                break;

            case Line:
                double midY = rect.getMidY();
                context.moveToPoint(originX, midY);
                context.addLineToPoint(originX + width, midY);
                break;
            default:
                break;
        }
    }

    @Override
    public void drawInContext(CGContext context) {
        CGRect rect = getBounds();
        GradientDrawableConstantState state = this.internalConstantState;
        if(state.getShape() != GradientDrawableShape.Line) {
            if(state.getColors().size() == 1) {
                createPathInContext(context, rect);

                context.setFillColor(state.getColors().get(0).getCGColor());
                context.drawPath(CGPathDrawingMode.Fill);

            } else if(state.getColors().size() > 1) {
                createPathInContext(context, rect);

                context.saveGState();
                context.clip();

                double originX = rect.getOrigin().getX();
                double originY = rect.getOrigin().getY();
                double rectWidth = rect.getSize().getWidth();
                double rectHeight = rect.getSize().getHeight();

                if(state.getGradientType() == GradientDrawableGradientType.Linear) {
                    CGGradient gradient = state.getCurrentGradient();
                    double cos = Math.cos(state.getGradientAngle());
                    double sin = Math.sin(state.getGradientAngle());

                    double halfWidth = rect.getWidth() / 2.d;
                    double halfHeight = rect.getHeight() / 2.d;

                    CGPoint startPoint = new CGPoint(originX + halfWidth - cos * halfWidth, originY + halfHeight + sin * halfHeight);
                    CGPoint endPoint = new CGPoint(originX + halfWidth + cos * halfWidth, originY + halfHeight - sin * halfHeight);

                    context.drawLinearGradient(gradient, startPoint, endPoint, CGGradientDrawingOptions.None);

                } else if(state.getGradientType() == GradientDrawableGradientType.Radial) {
                    CGGradient gradient = state.getCurrentGradient();
                    CGPoint relativeCenterPoint = state.getRelativeGradientCenter();
                    CGPoint centerPoint = new CGPoint(originX + rectWidth * relativeCenterPoint.getX(), originY + rectHeight * relativeCenterPoint.getY());
                    double radius = state.getGradientRadius();

                    if(state.isGradientRadiusIsRelative()) {
                        radius *= Math.min(rectWidth, rectHeight);
                    }
                    context.drawRadialGradient(gradient, centerPoint, 0, centerPoint, radius, CGGradientDrawingOptions.AfterEndLocation);

                } else if(state.getGradientType() == GradientDrawableGradientType.Sweep) {
                    double dim = Math.min(getBounds().getSize().getWidth(), getBounds().getSize().getHeight());
                    int subDiv = 512;
                    double r = dim / 4;
                    double R = dim / 2;

                    double halfinteriorPerim = Math.PI * r;
                    double halfexteriorPerim = Math.PI * R;
                    double smallBase = halfinteriorPerim / subDiv;
                    double largeBase = halfexteriorPerim / subDiv;

                    UIBezierPath cell = new UIBezierPath();
                    context.moveToPoint(-smallBase / 2, r);
                    context.addLineToPoint(smallBase / 2, r);
                    context.addLineToPoint(largeBase / 2, R);
                    context.addLineToPoint(-largeBase / 2, R);
                    context.closePath();

                    double incr = Math.PI / subDiv;
                    context.translateCTM(getBounds().getSize().getWidth() / 2, getBounds().getSize().getHeight() / 2);

                    context.scaleCTM(0.9, 0.9);
                    context.rotateCTM(Math.PI / 2);
                    context.rotateCTM(-incr / 2);

                    for (int i = 0; i < subDiv; i++) {
                        // replace this color with a color extracted from your gradient object
                        cell.fill();
                        cell.stroke();

                        context.rotateCTM(-incr);
                    }
                }
                context.restoreGState();
            }
        }
        boolean drawStroke = state.getStrokeWidth() > 0 && state.getStrokeColor() != null;
        if(drawStroke) {
            createPathInContext(context, rect);
            context.setLineWidth(state.getStrokeWidth());

            if(state.getDashWidth() > 0.d) {
                float lengths[] = new float[] {(float) state.getDashWidth(), (float) state.getDashGap()};
                context.setLineDash(0, lengths, 2);
            }
            context.setStrokeColor(state.getStrokeColor().getCGColor());
            context.strokePath();
        }

        super.drawInContext(context); //OUTLINE_RECT
    }

    @Override
    public void inflate(Element element) throws ParseException {
        GradientDrawableConstantState state = this.internalConstantState;
        Map<String, String> attrs = DOMUtil.getAttributesFromElement(element);

        String shape = attrs.get("shape");
        state.setShape(GradientDrawableShape.get(shape));

        if(state.getShape() == GradientDrawableShape.Ring) {
            //TODO: Use the following -> ResourceAttributesUtil.getDimensionValue
            //TODO: Support for dimension resources like in android - ResourceManager.getCurrent().getDimension(identifier)
            //TODO: and checking for valid identifier - ResourceManager.getCurrent().isValidIdentifier(identifier)
            double innerRadius = attrs.containsKey("innerRadius") ? Double.parseDouble(attrs.get("innerRadius")) : -1;
            state.setInnerRadius(innerRadius);

            if(state.getInnerRadius() == -1) {
                double innerRadiusRatio = attrs.containsKey("innerRadiusRatio") ? Double.parseDouble(attrs.get("innerRadiusRatio")) : 3;
                state.setInnerRadiusRatio(innerRadiusRatio);
            }
            double thickness = attrs.containsKey("thickness") ? Double.parseDouble(attrs.get("thickness")) : -1;
            state.setThickness(thickness);

            if(state.getThickness() == -1) {
                double thicknessRatio = attrs.containsKey("thicknessRatio") ? Double.parseDouble(attrs.get("thicknessRatio")) : 9;
                state.setThicknessRatio(thicknessRatio);
            }
        }

        Node child = element.getFirstChild();
        while(child != null) {
            String nodeName = child.getNodeName();
            if(nodeName.equals("gradient")) {
                attrs = DOMUtil.getAttributesFromNode(child, attrs);

                state.setGradientType(GradientDrawableGradientType.get(attrs.get("type")));

                CGPoint gradientCenter = new CGPoint(.5d, .5d);
                String centerX = attrs.get("centerX");
                if(centerX != null) {
                    gradientCenter.setX(Double.parseDouble(centerX));
                }
                String centerY = attrs.get("centerY");
                if(centerY != null) {
                    gradientCenter.setY(Double.parseDouble(centerY));
                }

                if(state.getGradientType() == GradientDrawableGradientType.Radial) {

                    if(!attrs.containsKey("gradientRadius")) {
                        state.setGradientRadius(1);
                        state.setGradientRadiusIsRelative(true);
                    } else {
                        if(ResourceAttributesUtil.isFractionValue(attrs, "gradientRadius")) {
                            double gradientRadius = ResourceAttributesUtil.getFractionValue(attrs, "gradientRadius");

                            state.setGradientRadiusIsRelative(true);
                            state.setGradientRadius(gradientRadius);
                        } else {
                            double gradientRadius = ResourceAttributesUtil.getDimensionValue(attrs, "gradientRadius");

                            state.setGradientRadiusIsRelative(false);
                            state.setGradientRadius(gradientRadius);
                        }
                    }
                } else if(state.getGradientType() == GradientDrawableGradientType.Linear) {
                    double angle = Double.parseDouble(attrs.get("angle"));
                    state.setGradientAngle(angle * Math.PI / 180.d);
                }

                UIColor startColor = ResourceAttributesUtil.getColorValue(attrs, "startColor", UIColor.black());
                UIColor centerColor = ResourceAttributesUtil.getColorValue(attrs, "centerColor");
                UIColor endColor = ResourceAttributesUtil.getColorValue(attrs, "endColor", UIColor.black());

                if(centerColor != null) {
                    state.setColors(Arrays.asList(startColor, centerColor, endColor));
                    if(state.getGradientType() == GradientDrawableGradientType.Linear) {
                        double[] colorPositions = new double[3];
                        colorPositions[0] = 0.d;
                        // Since 0.5f is default value, try to take the one that isn't 0.5f
                        colorPositions[1] = gradientCenter.getX() != .5d ? gradientCenter.getX() : gradientCenter.getY();
                        colorPositions[2] = 1.d;

                        state.setColorPositions(colorPositions);
                    }
                } else {
                    state.setColors(Arrays.asList(startColor, endColor));
                }


            } else if(nodeName.equals("padding")) {
                attrs = DOMUtil.getAttributesFromNode(child, attrs);

                UIEdgeInsets padding = UIEdgeInsets.Zero();
                padding.setLeft(Double.parseDouble(attrs.get("left")));
                padding.setTop(Double.parseDouble(attrs.get("top")));
                padding.setRight(Double.parseDouble(attrs.get("right")));
                padding.setBottom(Double.parseDouble(attrs.get("bottom")));

                state.setPadding(padding);
                state.setHasPadding(true);

            } else if(nodeName.equals("corners")) {
                GradientDrawableCornerRadius cornerRadius = GradientDrawableCornerRadius.zero();
                attrs = DOMUtil.getAttributesFromNode(child, attrs);

                double radius = Double.parseDouble(attrs.get("radius"));
                cornerRadius.setTopLeft(radius);
                cornerRadius.setTopRight(radius);
                cornerRadius.setBottomLeft(radius);
                cornerRadius.setBottomRight(radius);

                String topLeftRadius = attrs.get("topLeftRadius");
                String topRightRadius = attrs.get("topRightRadius");
                String bottomLeftRadius = attrs.get("bottomLeftRadius");
                String bottomRightRadius = attrs.get("bottomRightRadius");

                if(topLeftRadius != null) cornerRadius.setTopLeft(Double.parseDouble(topLeftRadius));
                if(topRightRadius != null) cornerRadius.setTopRight(Double.parseDouble(topRightRadius));
                if(bottomLeftRadius != null) cornerRadius.setBottomLeft(Double.parseDouble(bottomLeftRadius));
                if(bottomRightRadius != null) cornerRadius.setBottomRight(Double.parseDouble(bottomRightRadius));

                state.setCorners(cornerRadius);

            } else if(nodeName.equals("solid")) {
                attrs = DOMUtil.getAttributesFromNode(child, attrs);
                UIColor color = ResourceAttributesUtil.getColorValue(attrs, "color", UIColor.black());
                state.setColors(Arrays.asList(color));

            } else if(nodeName.equals("size")) {
                attrs = DOMUtil.getAttributesFromNode(child, attrs);
                CGSize size = CGSize.Zero();

                size.setWidth(ResourceAttributesUtil.getDimensionValue(attrs, "width", -1.d));
                size.setHeight(ResourceAttributesUtil.getDimensionValue(attrs, "height", -1.d));

                state.setSize(size);

            } else if(nodeName.equals("stroke")) {
                attrs = DOMUtil.getAttributesFromNode(child, attrs);
                state.setStrokeWidth(ResourceAttributesUtil.getDimensionValue(attrs, "width"));
                state.setStrokeColor(ResourceAttributesUtil.getColorValue(attrs, "color"));
                state.setDashWidth(ResourceAttributesUtil.getDimensionValue(attrs, "dashWidth"));

                if(state.getDashWidth() != 0.d) {
                    state.setDashGap(ResourceAttributesUtil.getDimensionValue(attrs, "dashGap"));
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
    public CGSize getIntrinsicSize() {
        return internalConstantState.getSize();
    }

    @Override
    public DrawableConstantState getConstantState() {
        return internalConstantState;
    }

    @Override
    public void onStateChangeToState(UIControlState state) {

    }

    @Override
    public void onBoundsChangeToRect(CGRect bounds) {

    }

    @Override
    public boolean onLevelChangeToLevel(int level) {
        return false;
    }
}
