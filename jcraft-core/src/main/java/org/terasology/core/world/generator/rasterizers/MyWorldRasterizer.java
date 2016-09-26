package org.terasology.core.world.generator.rasterizers;
import org.terasology.math.ChunkMath;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.generation.*;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

import com.chappelle.jcraft.blocks.*;
import com.chappelle.jcraft.world.chunk.Chunk;

public class MyWorldRasterizer implements WorldRasterizer
{
    private Block grass;

    @Override
    public void initialize()
    {
        grass = Blocks.grass;
    }

    @Override
    public void generateChunk(Chunk chunk, Region chunkRegion) {
        SurfaceHeightFacet surfaceHeightFacet = chunkRegion.getFacet(SurfaceHeightFacet.class);
        for (Vector3i position : chunkRegion.getRegion()) {
            float surfaceHeight = surfaceHeightFacet.getWorld(position.x, position.z);
            if (position.y < surfaceHeight) {
                Vector3i pos = ChunkMath.calcBlockPos(position);
				chunk.setBlock(Math.abs(pos.x), Math.abs(pos.y), Math.abs(pos.z), grass);
            }
        }
    }
}
