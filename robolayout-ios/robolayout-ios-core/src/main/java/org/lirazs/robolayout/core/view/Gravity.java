package org.lirazs.robolayout.core.view;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;

/**
 * Created on 8/3/2015.
 */
public class Gravity {

    /**
     * Apply a gravity constant to an object.
     *
     * @param gravity The desired placement of the object, as defined by the
     *                constants in this class.
     * @param width The horizontal size of the object.
     * @param height The vertical size of the object.
     * @param containerCGRect The frame of the containing space, in which the object
     *                  will be placed.  Should be large enough to contain the
     *                  width and height of the object.
     * @param xAdj Offset to apply to the X axis.  If gravity is LEFT this
     *             pushes it to the right; if gravity is RIGHT it pushes it to
     *             the left; if gravity is CENTER_HORIZONTAL it pushes it to the
     *             right or left; otherwise it is ignored.
     * @param yAdj Offset to apply to the Y axis.  If gravity is TOP this pushes
     *             it down; if gravity is BOTTOM it pushes it up; if gravity is
     *             CENTER_VERTICAL it pushes it down or up; otherwise it is
     *             ignored.
     * @param outCGRect Receives the computed frame of the object in its
     *                container.
     */
    public static void applyGravity(ViewContentGravity gravity, double width, double height,
                                    CGRect containerCGRect, double xAdj, double yAdj, CGRect outCGRect) {

        Frame container = new Frame(
                containerCGRect.getOrigin().getY(),
                containerCGRect.getOrigin().getX(),
                containerCGRect.getOrigin().getY() + containerCGRect.getSize().getHeight(),
                containerCGRect.getOrigin().getX() + containerCGRect.getSize().getWidth()
        );
        Frame outRect = new Frame(
                outCGRect.getOrigin().getY(),
                outCGRect.getOrigin().getX(),
                outCGRect.getOrigin().getY() + outCGRect.getSize().getHeight(),
                outCGRect.getOrigin().getX() + outCGRect.getSize().getWidth()
        );

        long gravityValueWithAxis = gravity.value() & ((ViewContentGravity.GravityAxis.AXIS_PULL_BEFORE | ViewContentGravity.GravityAxis.AXIS_PULL_AFTER) << ViewContentGravity.GravityAxis.AXIS_X_SHIFT);
        if(gravityValueWithAxis == 0) {
            outRect.setLeft(container.getLeft() + ((container.getRight() - container.getLeft() - width) / 2) + xAdj);
            outRect.setRight(outRect.getLeft() + width);

            if((gravity.value() & (ViewContentGravity.GravityAxis.AXIS_CLIP << ViewContentGravity.GravityAxis.AXIS_X_SHIFT))
                    == (ViewContentGravity.GravityAxis.AXIS_CLIP << ViewContentGravity.GravityAxis.AXIS_X_SHIFT)) {

                if(outRect.getLeft() < container.getLeft()) {
                    outRect.setLeft(container.getLeft());
                }
                if(outRect.getRight() < container.getRight()) {
                    outRect.setRight(container.getRight());
                }
            }

        } else if(gravityValueWithAxis == (ViewContentGravity.GravityAxis.AXIS_PULL_BEFORE << ViewContentGravity.GravityAxis.AXIS_X_SHIFT)) {
            outRect.setLeft(container.getLeft() + xAdj);
            outRect.setRight(outRect.getLeft() + width);

            if ((gravity.value() & (ViewContentGravity.GravityAxis.AXIS_CLIP << ViewContentGravity.GravityAxis.AXIS_X_SHIFT))
                    == (ViewContentGravity.GravityAxis.AXIS_CLIP << ViewContentGravity.GravityAxis.AXIS_X_SHIFT)) {

                if(outRect.getRight() > container.getRight()) {
                    outRect.setRight(container.getRight());
                }
            }

        } else if(gravityValueWithAxis == (ViewContentGravity.GravityAxis.AXIS_PULL_AFTER << ViewContentGravity.GravityAxis.AXIS_X_SHIFT)) {
            outRect.setRight(container.getRight() - xAdj);
            outRect.setLeft(outRect.getRight() - width);

            if((gravity.value() & (ViewContentGravity.GravityAxis.AXIS_CLIP << ViewContentGravity.GravityAxis.AXIS_X_SHIFT))
                    == (ViewContentGravity.GravityAxis.AXIS_CLIP << ViewContentGravity.GravityAxis.AXIS_X_SHIFT)) {

                if(outRect.getLeft() < container.getLeft()) {
                    outRect.setLeft(container.getLeft());
                }
            }
        } else {
            outRect.setLeft(container.getLeft() + xAdj);
            outRect.setRight(container.getRight() + xAdj);
        }


        gravityValueWithAxis = gravity.value() &
                ((ViewContentGravity.GravityAxis.AXIS_PULL_BEFORE | ViewContentGravity.GravityAxis.AXIS_PULL_AFTER) << ViewContentGravity.GravityAxis.AXIS_Y_SHIFT);

        if(gravityValueWithAxis == 0) {
            outRect.setTop(container.getTop() + ((container.getBottom() - container.getTop() - height) / 2) + yAdj);
            outRect.setBottom(outRect.getTop() + height);

            if((gravity.value() & (ViewContentGravity.GravityAxis.AXIS_CLIP << ViewContentGravity.GravityAxis.AXIS_Y_SHIFT))
                    == (ViewContentGravity.GravityAxis.AXIS_CLIP << ViewContentGravity.GravityAxis.AXIS_Y_SHIFT)) {

                if(outRect.getTop() < container.getTop()) {
                    outRect.setTop(container.getTop());
                }
                if(outRect.getBottom() > container.getBottom()) {
                    outRect.setBottom(container.getBottom());
                }
            }

        } else if(gravityValueWithAxis == (ViewContentGravity.GravityAxis.AXIS_PULL_BEFORE << ViewContentGravity.GravityAxis.AXIS_Y_SHIFT)) {
            outRect.setTop(container.getTop() + yAdj);
            outRect.setBottom(outRect.getTop() + height);

            if ((gravity.value() & (ViewContentGravity.GravityAxis.AXIS_CLIP << ViewContentGravity.GravityAxis.AXIS_Y_SHIFT))
                    == (ViewContentGravity.GravityAxis.AXIS_CLIP << ViewContentGravity.GravityAxis.AXIS_Y_SHIFT)) {

                if(outRect.getBottom() > container.getBottom()) {
                    outRect.setBottom(container.getBottom());
                }
            }

        } else if(gravityValueWithAxis == (ViewContentGravity.GravityAxis.AXIS_PULL_AFTER << ViewContentGravity.GravityAxis.AXIS_Y_SHIFT)) {
            outRect.setBottom(container.getBottom() - yAdj);
            outRect.setTop(outRect.getBottom() - height);

            if ((gravity.value() & (ViewContentGravity.GravityAxis.AXIS_CLIP << ViewContentGravity.GravityAxis.AXIS_Y_SHIFT))
                    == (ViewContentGravity.GravityAxis.AXIS_CLIP << ViewContentGravity.GravityAxis.AXIS_Y_SHIFT)) {

                if (outRect.getTop() < container.getTop()) {
                    outRect.setTop(container.getTop());
                }
            }
        } else {
            outRect.setTop(container.getTop() + yAdj);
            outRect.setBottom(container.getBottom() + yAdj);
        }

        outCGRect.setOrigin(new CGPoint(outRect.getLeft(), outRect.getTop()));
        outCGRect.setSize(new CGSize(outRect.getRight() - outRect.getLeft(), outRect.getBottom() - outRect.getTop()));
        containerCGRect.setOrigin(new CGPoint(container.getLeft(), container.getTop()));
        containerCGRect.setSize(new CGSize(container.getRight() - container.getLeft(), container.getBottom() - container.getTop()));
    }

    /**
     * Apply a gravity constant to an object. This suppose that the layout direction is LTR.
     *
     * @param gravity The desired placement of the object, as defined by the
     *                constants in this class.
     * @param width The horizontal size of the object.
     * @param height The vertical size of the object.
     * @param containerCGRect The frame of the containing space, in which the object
     *                  will be placed.  Should be large enough to contain the
     *                  width and height of the object.
     * @param outCGRect Receives the computed frame of the object in its
     *                container.
     */
    public static void applyGravity(ViewContentGravity gravity, double width, double height,
                                    CGRect containerCGRect, CGRect outCGRect) {

        applyGravity(gravity, width, height, containerCGRect, 0.d, 0.d, outCGRect);
    }
}
