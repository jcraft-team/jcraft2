package org.terasology.math.geom;
/**
 * TODO Type description
 */
public abstract class BaseRect implements Shape {

    /**
     * The bitmask that indicates that a point lies to the left.
     */
    public static final int OUT_LEFT = 1;

    /**
     * The bitmask that indicates that a point lies above.
     */
    public static final int OUT_TOP = 2;

    /**
     * The bitmask that indicates that a point lies to the right.
     */
    public static final int OUT_RIGHT = 4;

    /**
     * The bitmask that indicates that a point lies below.
     */
    public static final int OUT_BOTTOM = 8;

    @Override
    public boolean contains(BaseVector2f v) {
        return contains(v.getX(), v.getY());
    }

    @Override
    public boolean contains(BaseVector2i v) {
        return contains(v.getX(), v.getY());
    }

    /**
     * Determines where the specified coordinates lie.
     * This method computes a binary OR of the appropriate mask values indicating, for each side,
     * whether or not the specified coordinates are on the same side of the edge.
     * @param x the specified x coordinate
     * @param y the specified y coordinate
     * @return the combination of the appropriate out codes.
     */
    public abstract int outcode(float x, float y);
}
