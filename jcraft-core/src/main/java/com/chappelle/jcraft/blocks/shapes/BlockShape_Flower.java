package com.chappelle.jcraft.blocks.shapes;

import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.blocks.*;
import com.chappelle.jcraft.util.Vector3fPool;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.math.Vector3f;

import gnu.trove.list.*;

public class BlockShape_Flower extends BlockShape
{
	public BlockShape_Flower()
	{
		for(Block.Face face : Block.Face.values())
		{
			fullSide.put(face, Boolean.FALSE);
		}
	}
	
    @Override
    public void addTo(MeshData meshData, Chunk chunk, Block block, Vector3Int blockLocation, boolean isTransparent)
    {
    	TFloatList positions = meshData.positionsList;
    	TShortList indices = meshData.indicesList;
    	TFloatList normals = meshData.normalsList;
    	TFloatList colors = meshData.colorList;
    	TFloatList textureCoordinates = meshData.textureCoordinatesList;
    	Vector3fPool pool = meshData.vec3Pool;

        Vector3f blockLocation3f = pool.get(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());

        float halfWidth = 0.25f;
        float height = 0.7f;
        
    	addFaceIndices(indices, positions.size());
        addPositions(positions, blockLocation3f.add(new Vector3f(0.5f - halfWidth, 0, 0.5f - halfWidth)));
        addPositions(positions, blockLocation3f.add(new Vector3f(0.5f + halfWidth, 0, 0.5f + halfWidth)));
        addPositions(positions, blockLocation3f.add(new Vector3f(0.5f - halfWidth, height, 0.5f - halfWidth)));
        addPositions(positions, blockLocation3f.add(new Vector3f(0.5f + halfWidth, height, 0.5f + halfWidth)));
        addSquareNormals(normals, -1, 0, -1);
        addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Left).getTextureLocation());
        addLighting(colors, chunk, blockLocation, Block.Face.Left);

        addFaceIndices(indices, positions.size());
        addPositions(positions, blockLocation3f.add(new Vector3f(0.5f + halfWidth, 0, 0.5f + halfWidth)));
        addPositions(positions, blockLocation3f.add(new Vector3f(0.5f - halfWidth, 0, 0.5f - halfWidth)));
        addPositions(positions, blockLocation3f.add(new Vector3f(0.5f + halfWidth, height, 0.5f + halfWidth)));
        addPositions(positions, blockLocation3f.add(new Vector3f(0.5f - halfWidth, height, 0.5f - halfWidth)));
        addSquareNormals(normals, 1, 0, -1);
        addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Right).getTextureLocation());
        addLighting(colors, chunk, blockLocation, Block.Face.Right);

        addFaceIndices(indices, positions.size());
        addPositions(positions, blockLocation3f.add(new Vector3f(0.5f - halfWidth, 0, 0.5f + halfWidth)));
        addPositions(positions, blockLocation3f.add(new Vector3f(0.5f + halfWidth, 0, 0.5f - halfWidth)));
        addPositions(positions, blockLocation3f.add(new Vector3f(0.5f - halfWidth, height, 0.5f + halfWidth)));
        addPositions(positions, blockLocation3f.add(new Vector3f(0.5f + halfWidth, height, 0.5f - halfWidth)));
        addSquareNormals(normals, 1, 0, 1);
        addTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
        addLighting(colors, chunk, blockLocation, Block.Face.Front);

        addFaceIndices(indices, positions.size());
        addPositions(positions, blockLocation3f.add(new Vector3f(0.5f + halfWidth, 0, 0.5f - halfWidth)));
        addPositions(positions, blockLocation3f.add(new Vector3f(0.5f - halfWidth, 0, 0.5f + halfWidth)));
        addPositions(positions, blockLocation3f.add(new Vector3f(0.5f + halfWidth, height, 0.5f - halfWidth)));
        addPositions(positions, blockLocation3f.add(new Vector3f(0.5f - halfWidth, height, 0.5f + halfWidth)));
        addSquareNormals(normals, -1, 0, 1);
        addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Back).getTextureLocation());
        addLighting(colors, chunk, blockLocation, Block.Face.Back);
    }

    private void addFaceIndices(TShortList indices, int offset){
    	offset = offset/3;
        indices.add((short) (offset + 2));
        indices.add((short) (offset + 0));
        indices.add((short) (offset + 1));
        indices.add((short) (offset + 1));
        indices.add((short) (offset + 3));
        indices.add((short) (offset + 2));
    }
    
    private void addSquareNormals(TFloatList normals, float normalX, float normalY, float normalZ){
        for(int i=0;i<4;i++){
            normals.add(normalX);
            normals.add(normalY);
            normals.add(normalZ);
        }
    }

    private void addTextureCoordinates(Chunk chunk, TFloatList textureCoordinates, BlockSkin_TextureLocation textureLocation){
        textureCoordinates.add(getTextureCoordinatesX(chunk, textureLocation, 0, 0));
        textureCoordinates.add(getTextureCoordinatesY(chunk, textureLocation, 0, 0));
        
        textureCoordinates.add(getTextureCoordinatesX(chunk, textureLocation, 1, 0));
        textureCoordinates.add(getTextureCoordinatesY(chunk, textureLocation, 1, 0));
        
        textureCoordinates.add(getTextureCoordinatesX(chunk, textureLocation, 0, 1));
        textureCoordinates.add(getTextureCoordinatesY(chunk, textureLocation, 0, 1));
        
        textureCoordinates.add(getTextureCoordinatesX(chunk, textureLocation, 1, 1));
        textureCoordinates.add(getTextureCoordinatesY(chunk, textureLocation, 1, 1));
    }
}