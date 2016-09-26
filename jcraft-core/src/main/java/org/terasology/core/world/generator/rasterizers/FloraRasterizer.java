/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.core.world.generator.rasterizers;

import java.util.*;

import org.terasology.core.world.generator.facets.FloraFacet;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.utilities.procedural.WhiteNoise;
import org.terasology.world.generation.*;

import com.chappelle.jcraft.blocks.*;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.google.common.collect.*;

/**
 */
public class FloraRasterizer implements WorldRasterizer {

    private final Map<FloraType, List<Block>> flora = Maps.newEnumMap(FloraType.class);

    @Override
    public void initialize() {
//        BlockManager blockManager = CoreRegistry.get(BlockManager.class);
//        air = blockManager.getBlock(BlockManager.AIR_ID);
//
        flora.put(FloraType.GRASS, ImmutableList.<Block>of(
                Blocks.tallGrass));

        flora.put(FloraType.FLOWER, ImmutableList.<Block>of(
        		Blocks.plantRed,Blocks.plantYellow));

        flora.put(FloraType.MUSHROOM, ImmutableList.<Block>of(
                Blocks.mushroomBrown, Blocks.mushroomRed));
    }

    @Override
    public void generateChunk(Chunk chunk, Region chunkRegion) {
        FloraFacet facet = chunkRegion.getFacet(FloraFacet.class);

        WhiteNoise noise = new WhiteNoise(chunk.location.hashCode());

        Map<BaseVector3i, FloraType> entries = facet.getRelativeEntries();
        // check if some other rasterizer has already placed something here
        entries.keySet().stream().filter(pos -> chunk.getBlock(pos) == null).forEach(pos -> {

            FloraType type = entries.get(pos);
            List<Block> list = flora.get(type);
            int blockIdx = Math.abs(noise.intNoise(pos.x(), pos.y(), pos.z())) % list.size();
            Block block = list.get(blockIdx);
            chunk.setBlock(pos, block);
        });
    }
}
