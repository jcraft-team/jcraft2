package org.terasology.core.world.generator.facetProviders;
import org.terasology.core.world.generator.facets.CaveFacet;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.generation.*;
import org.terasology.world.generation.facets.DensityFacet;

//@RegisterPlugin
@Updates(@Facet(DensityFacet.class))
@Requires(@Facet(CaveFacet.class))
public class CaveToDensityProvider implements FacetProviderPlugin {
    @Override
    public void setSeed(long seed) {
    }

    @Override
    public void process(GeneratingRegion region) {
        CaveFacet caveFacet = region.getRegionFacet(CaveFacet.class);
        DensityFacet densityFacet = region.getRegionFacet(DensityFacet.class);

        for (Vector3i pos : region.getRegion()) {
            if (caveFacet.getWorld(pos)) {
                densityFacet.setWorld(pos, -1f);
            }
        }
    }
}
