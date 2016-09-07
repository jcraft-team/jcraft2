package org.terasology.math.geom;
/**
 * An immutable implementation of BaseVector2f, which is a point or vector in 2D space with float components.
 * This type is intended for use for constants, or any time you want a BaseVector2f that is guaranteed immutable.
 *
 * @author auto-generated
 */
public final class ImmutableVector2f extends BaseVector2f {

    private final float x;
    private final float y;

    /**
    * @param x the x component
    * @param y the y component
     */
    public ImmutableVector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Copy constructor
     * @param other The BaseVector2f to copy.
     */
    public ImmutableVector2f(BaseVector2f other) {
        this(other.getX(), other.getY());
    }

    /**
     * Returns an immutable version of the provided vector.
     * @param other the vector to use
     */
    public static ImmutableVector2f createOrUse(BaseVector2f other) {
        if (other instanceof ImmutableVector2f) {
            return (ImmutableVector2f) other;
        }

        return new ImmutableVector2f(other);
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }


    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }


    /**
     * Adds a point to this point
     *
     * @param valueX the value to add to the x component
     * @param valueY the value to add to the y component
     * @return a new instance
     */
    public ImmutableVector2f add(float valueX, float valueY) {
        float nx = x + valueX;
        float ny = y + valueY;
        return new ImmutableVector2f(nx, ny);
    }

    /**
     * Adds a point to this point
     *
     * @param other the point
     * @return a new instance
     */
    public ImmutableVector2f add(BaseVector2f other) {
        float nx = x + other.getX();
        float ny = y + other.getY();
        return new ImmutableVector2f(nx, ny);
    }

    /**
     * Subtracts a point from this point
     *
     * @param valueX the value to subtract from the x component
     * @param valueY the value to subtract from the y component
     * @return a new instance
     */
    public ImmutableVector2f sub(float valueX, float valueY) {
        float nx = x - valueX;
        float ny = y - valueY;
        return new ImmutableVector2f(nx, ny);
    }

    /**
     * Subtracts a point from this point
     *
     * @param other the point
     * @return a new instance
     */
    public ImmutableVector2f sub(BaseVector2f other) {
        float nx = x - other.getX();
        float ny = y - other.getY();
        return new ImmutableVector2f(nx, ny);
    }

    /**
     * Multiplies this with a scalar value
     *
     * @param value a scalar value
     * @return a new instance
     */
    public ImmutableVector2f scale(float value) {
        float nx = x * value;
        float ny = y * value;
        return new ImmutableVector2f(nx, ny);
    }

}
