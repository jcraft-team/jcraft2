package org.terasology.core.world.generator.facets;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.facets.base.BaseBooleanFieldFacet3D;

public class CaveFacet extends BaseBooleanFieldFacet3D {
    public CaveFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);
    }

    public int getWorldIndex(Vector3i pos) {
        return getWorldIndex(pos.x, pos.y, pos.z);
    }
}
