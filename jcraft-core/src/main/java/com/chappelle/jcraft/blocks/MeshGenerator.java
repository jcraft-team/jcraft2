package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.util.math.Vector3Int;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.scene.*;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

public class MeshGenerator
{
	public static Mesh generateOptimizedMesh(Chunk blockChunk, boolean isTransparent)
	{
		
		MeshData meshData = new MeshData();
		Vector3Int tmpLocation = new Vector3Int();
		for(int x = 0; x < 16; x++)
		{
			for(int y = 0; y < 256; y++)
			{
				for(int z = 0; z < 16; z++)
				{
					tmpLocation.set(x, y, z);
					Block block = blockChunk.getBlock(tmpLocation);
					if(block != null)
					{
						BlockShape blockShape = block.getShape(blockChunk, tmpLocation);
						blockShape.addTo(meshData, blockChunk, block, tmpLocation, isTransparent);
						meshData.vec3Pool.reset();
					}
				}
			}
		}
		return generateMesh(meshData);
	}

	private static Mesh generateMesh(MeshData meshData)
	{
		float[] positions = meshData.positionsList.toArray();
		short[] indices = meshData.indicesList.toArray();
		float[] textureCoordinates = meshData.textureCoordinatesList.toArray();
		float[] normals = meshData.normalsList.toArray();
        float[] color = meshData.colorList.toArray();

		Mesh mesh = new Mesh();
		mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(positions));
		mesh.setBuffer(Type.Index, 1, BufferUtils.createShortBuffer(indices));
		mesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
		mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(textureCoordinates));
		mesh.setBuffer(VertexBuffer.Type.Color, 4, BufferUtils.createFloatBuffer(color));
		mesh.updateBound();
		return mesh;
	}
	
}
