package com.cubes;

import java.util.Iterator;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

public class MeshGenerator
{
	public static Mesh generateMesh(Block block)
	{
		MeshData meshData = new MeshData();
		BlockShape blockShape = block.getShape(null, null);
		blockShape.prepare(false, meshData);
		blockShape.addTo(null, block, new Vector3Int());
		return generateMesh(meshData, 3);//TODO:
	}
	
	public static Mesh generateOptimizedMesh(BlockChunkControl blockChunk, boolean isTransparent)
	{
		MeshData meshData = new MeshData();
		BlockTerrainControl blockTerrain = blockChunk.getTerrain();
		Vector3Int tmpLocation = new Vector3Int();
		for(int x = 0; x < blockTerrain.getSettings().getChunkSizeX(); x++)
		{
			for(int y = 0; y < blockTerrain.getSettings().getChunkSizeY(); y++)
			{
				for(int z = 0; z < blockTerrain.getSettings().getChunkSizeZ(); z++)
				{
					tmpLocation.set(x, y, z);
					Block block = blockChunk.getBlock(tmpLocation);
					if(block != null)
					{
						BlockShape blockShape = block.getShape(blockChunk, tmpLocation);
						blockShape.prepare(isTransparent, meshData);
						blockShape.addTo(blockChunk, block, tmpLocation);
					}
				}
			}
		}
		return generateMesh(meshData, blockTerrain.getSettings().getBlockSize());
	}

	private static Mesh generateMesh(MeshData meshData, float blockSize)
	{
		Vector3f[] positions = new Vector3f[meshData.positionsList.size()];
		Iterator<Vector3f> positionsIterator = meshData.positionsList.iterator();
		for(int i = 0; positionsIterator.hasNext(); i++)
		{
			positions[i] = positionsIterator.next().mult(blockSize);
		}
		short[] indices = new short[meshData.indicesList.size()];
		Iterator<Short> indicesIterator = meshData.indicesList.iterator();
		for(int i = 0; indicesIterator.hasNext(); i++)
		{
			indices[i] = indicesIterator.next();
		}
		Vector2f[] textureCoordinates = meshData.textureCoordinatesList.toArray(new Vector2f[0]);
		float[] normals = new float[meshData.normalsList.size()];
		Iterator<Float> normalsIterator = meshData.normalsList.iterator();
		for(int i = 0; normalsIterator.hasNext(); i++)
		{
			normals[i] = normalsIterator.next();
		}
        float[] color = new float[meshData.colorList.size()];
        Iterator<Float> colorIterator = meshData.colorList.iterator();
        for(int i=0;colorIterator.hasNext();i++){
        	color[i] = colorIterator.next();
        }

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
