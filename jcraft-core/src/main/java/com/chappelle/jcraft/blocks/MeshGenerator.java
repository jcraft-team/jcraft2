package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.util.math.Vector3Int;
import com.chappelle.jcraft.world.World;
import com.chappelle.jcraft.world.chunk.*;

public class MeshGenerator
{
	public static ChunkMesh generateMesh(World world, Chunk chunk)
	{
		MeshGenContextImpl meshGenContext = new MeshGenContextImpl(world, chunk);
		MeshData opaqueMeshData = new MeshData();
		MeshData transparentMeshData = new MeshData();
		Vector3Int tmpLocation = new Vector3Int();
		for(int x = 0; x < 16; x++)
		{
			for(int y = 0; y < 256; y++)
			{
				for(int z = 0; z < 16; z++)
				{
					tmpLocation.set(x, y, z);
					Block block = chunk.getBlock(tmpLocation);
					if(block != null)
					{
						meshGenContext.block = block;
						meshGenContext.location = tmpLocation;
						
						BlockShape shape = block.getShape(chunk, tmpLocation);
						
						meshGenContext.meshData = opaqueMeshData;
						shape.addTo(meshGenContext, false);
						
						meshGenContext.meshData = transparentMeshData;
						shape.addTo(meshGenContext, true);
						
						meshGenContext.vec3Pool.reset();
					}
				}
			}
		}
		return new ChunkMesh(opaqueMeshData.toMesh(), transparentMeshData.toMesh(), world.getBlockMaterial());
	}
}
