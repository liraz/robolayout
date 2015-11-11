package org.lirazs.robolayout.core.widget.layout;

import org.lirazs.robolayout.core.view.*;
import org.lirazs.robolayout.core.widget.View;
import org.robovm.apple.coregraphics.CGRect;

import java.util.Map;

/**
 * Created on 8/5/2015.
 */
public class Space extends View implements LayoutViewDelegate {

    public Space(Map<String, String> attrs) {
        super(attrs);

        if(getVisibility() == ViewVisibility.Visible) {
            setVisibility(ViewVisibility.Invisible);
        }
    }


    @Override
    public void onMeasure(LayoutMeasureSpec widthMeasureSpec, LayoutMeasureSpec heightMeasureSpec) {
        LayoutMeasuredDimension defaultWidthDimension = getDefaultSize(getSuggestedMinimumSize().getWidth(), widthMeasureSpec);
        LayoutMeasuredDimension defaultHeightDimension = getDefaultSize(getSuggestedMinimumSize().getHeight(), heightMeasureSpec);

        setMeasuredDimensionSize(new LayoutMeasuredSize(defaultWidthDimension, defaultHeightDimension));
    }

    private LayoutMeasuredDimension getDefaultSize(double size, LayoutMeasureSpec measureSpec) {
        LayoutMeasuredDimension result = new LayoutMeasuredDimension(size, LayoutMeasuredState.None);

        LayoutMeasureSpecMode specMode = measureSpec.getMode();
        double specSize = measureSpec.getSize();

        switch (specMode) {
            case Unspecified:
                result.setSize(size);
                break;
            case AtMost:
                result.setSize(Math.min(size, specSize));
                break;
            case Exactly:
                result.setSize(specSize);
                break;
        }
        return result;
    }


    @Override
    public void onLayout(CGRect frame, boolean changed) {

    }

    @Override
    public void draw(CGRect cgRect) {
         // don't draw!
    }
}
