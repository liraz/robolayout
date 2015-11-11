package org.lirazs.robolayout.core.widget.layout.relative;

/**
 * Created on 8/5/2015.
 */
public enum RelativeLayoutRule {

    /**
     * Rule that aligns a child's right edge with another child's left edge.
     */
    LeftOf (0),
    /**
     * Rule that aligns a child's left edge with another child's right edge.
     */
    RightOf (1),
    /**
     * Rule that aligns a child's bottom edge with another child's top edge.
     */
    Above (2),
    /**
     * Rule that aligns a child's top edge with another child's bottom edge.
     */
    Below (3),

    /**
     * Rule that aligns a child's baseline with another child's baseline.
     */
    AlignBaseline (4),
    /**
     * Rule that aligns a child's left edge with another child's left edge.
     */
    AlignLeft (5),
    /**
     * Rule that aligns a child's top edge with another child's top edge.
     */
    AlignTop (6),
    /**
     * Rule that aligns a child's right edge with another child's right edge.
     */
    AlignRight (7),
    /**
     * Rule that aligns a child's bottom edge with another child's bottom edge.
     */
    AlignBottom (8),

    /**
     * Rule that aligns the child's left edge with its RelativeLayout
     * parent's left edge.
     */
    AlignParentLeft (9),
    /**
     * Rule that aligns the child's top edge with its RelativeLayout
     * parent's top edge.
     */
    AlignParentTop (10),
    /**
     * Rule that aligns the child's right edge with its RelativeLayout
     * parent's right edge.
     */
    AlignParentRight (11),
    /**
     * Rule that aligns the child's bottom edge with its RelativeLayout
     * parent's bottom edge.
     */
    AlignParentBottom (12),

    /**
     * Rule that centers the child with respect to the bounds of its
     * RelativeLayout parent.
     */
    CenterInParent (13),
    /**
     * Rule that centers the child horizontally with respect to the
     * bounds of its RelativeLayout parent.
     */
    CenterHorizontal (14),
    /**
     * Rule that centers the child vertically with respect to the
     * bounds of its RelativeLayout parent.
     */
    CenterVertical (15);
    
    public static RelativeLayoutRule valueOf(int value) {
        for (RelativeLayoutRule relativeLayoutRule : RelativeLayoutRule.values()) {
            if(relativeLayoutRule.getValue() == value)
                return relativeLayoutRule;
        }
        return null;
    }

    private int value;

    RelativeLayoutRule(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
