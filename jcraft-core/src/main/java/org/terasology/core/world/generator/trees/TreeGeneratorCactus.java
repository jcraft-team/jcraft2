/*
 * Copyright 2013 MovingBlocks
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
package org.terasology.core.world.generator.trees;

import org.terasology.utilities.random.Random;

import com.chappelle.jcraft.blocks.*;
import com.chappelle.jcraft.world.chunk.Chunk;

/**
 * Cactus generator.
 *
 */
public class TreeGeneratorCactus extends AbstractTreeGenerator {

	private Block cactusType = Blocks.cactus;
	
    @Override
    public void generate(Chunk view, Random rand, int posX, int posY, int posZ) {
        for (int y = posY; y < posY + 3; y++) {
            safelySetBlock(view, posX, y, posZ, cactusType);
        }
    }

    public TreeGenerator setTrunkType(Block b) {
        cactusType = b;
        return this;
    }
}
