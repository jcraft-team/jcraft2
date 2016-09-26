package org.terasology.core.world.generator.facetProviders;

import org.terasology.entitySystem.Component;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.rendering.nui.properties.Range;
import org.terasology.world.generation.*;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

@Produces(SurfaceHeightFacet.class)
public class MySurfaceProvider implements ConfigurableFacetProvider
{
    private MySurfaceProviderConfiguration configuration = new MySurfaceProviderConfiguration();

    @Override
    public void setSeed(long seed)
    {

    }

    @Override
    public void process(GeneratingRegion region)
    {
        Border3D border = region.getBorderForFacet(SurfaceHeightFacet.class);
        SurfaceHeightFacet facet = new SurfaceHeightFacet(region.getRegion(), border);

        Rect2i processRegion = facet.getWorldRegion();
        for(BaseVector2i position : processRegion.contents()){
            facet.setWorld(position, configuration.surfaceHeight);
        }

        region.setRegionFacet(SurfaceHeightFacet.class, facet);
    }

    @Override
    public String getConfigurationName()
    {
        return "My Surface Configuration";
    }

    @Override
    public Component getConfiguration()
    {
        return configuration;
    }

    @Override
    public void setConfiguration(Component configuration)
    {
        this.configuration = (MySurfaceProviderConfiguration)configuration;
    }

    private static class MySurfaceProviderConfiguration implements Component
    {
        @Range(min = 1, max = 100f, increment = 1.0f, precision = 1, description = "My Surface Height")
        public int surfaceHeight = 10;
    }
}
