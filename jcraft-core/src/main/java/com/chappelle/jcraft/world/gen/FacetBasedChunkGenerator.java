package com.chappelle.jcraft.world.gen;

import java.util.*;

import org.terasology.world.generation.*;

import com.chappelle.jcraft.world.chunk.Chunk;
import com.google.common.collect.ListMultimap;

public class FacetBasedChunkGenerator implements ChunkGenerator
{
	private final ListMultimap<Class<? extends WorldFacet>, FacetProvider> facetProviderChains;
	private final List<WorldRasterizer> worldRasterizers;
    private final Map<Class<? extends WorldFacet>, Border3D> borders;
	
    public FacetBasedChunkGenerator(ListMultimap<Class<? extends WorldFacet>, FacetProvider> facetProviderChains, List<WorldRasterizer> worldRasterizers, Map<Class<? extends WorldFacet>, Border3D> borders)
    {
    	this.facetProviderChains = facetProviderChains;
    	this.worldRasterizers = worldRasterizers;
    	this.borders = borders;
    }
    
	@Override
	public Chunk generate(int x, int z)
	{
		Chunk chunk = new Chunk(x, z);
		Region region = new RegionImpl(chunk.getRegion(), facetProviderChains, borders);
		for(WorldRasterizer rasterizer : worldRasterizers)
		{
			rasterizer.generateChunk(chunk, region);
		}
		return chunk;
	}
	

	@Override
	public void initialize()
	{
        Collection<FacetProvider> facetProviders = new LinkedHashSet<>(facetProviderChains.values());

        facetProviders.forEach(FacetProvider::initialize);

        worldRasterizers.forEach(WorldRasterizer::initialize);
	}
}