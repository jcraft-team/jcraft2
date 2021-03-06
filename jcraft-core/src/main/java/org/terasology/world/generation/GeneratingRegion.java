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
package org.terasology.world.generation;

import org.terasology.math.Region3i;

/**
 */
public interface GeneratingRegion {

    Region3i getRegion();

    <T extends WorldFacet> T getRegionFacet(Class<T> type);

    <T extends WorldFacet> void setRegionFacet(Class<T> type, T facet);

    Border3D getBorderForFacet(Class<? extends WorldFacet> type);
}
