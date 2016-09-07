package org.terasology.math.geom;

/**
 * Interface for all kinds of 2D shapes
 * @author Martin Steiger
 */
public interface Shape {

    /**
     * The exact definition of <i>insideness</i> depends on the implementation
     * @param v the position coordinates
     * @return true if the polygon contains the point
     */
    boolean contains(BaseVector2f v);

    /**
     * The exact definition of <i>insideness</i> depends on the implementation
     * @param v the position coordinates
     * @return true if the polygon contains the point
     */
    boolean contains(BaseVector2i v);

    /**
     * The exact definition of <i>insideness</i> depends on the implementation
     * @param x the x coord
     * @param y the y coord
     * @return true if the polygon contains the point
     */
    boolean contains(float x, float y);

    /**
     * @return the bounding box of the shape
     */
    Rect2f getBounds();
}
