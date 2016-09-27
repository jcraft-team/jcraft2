package org.terasology.core.world.generator.rasterizers;
import org.terasology.core.world.generator.facets.CaveFacet;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.chunks.ChunkConstants;
import org.terasology.world.generation.*;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.chunk.Chunk;

/**
 * Still need this rasterizer because just changing the density does not provide the correct effect with the default perlin generator
 */
//@RegisterPlugin
public class CaveRasterizer implements WorldRasterizerPlugin {
    String blockUri;

    public CaveRasterizer() {
    }

    public CaveRasterizer(String blockUri) {
        this.blockUri = blockUri;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void generateChunk(Chunk chunk, Region chunkRegion) {
        CaveFacet caveFacet = chunkRegion.getFacet(CaveFacet.class);

        for (Vector3i position : ChunkConstants.CHUNK_REGION) {
            if (caveFacet.get(position)) {
                chunk.removeBlock(position.x, position.y, position.z);
            }
        }
    }
}
