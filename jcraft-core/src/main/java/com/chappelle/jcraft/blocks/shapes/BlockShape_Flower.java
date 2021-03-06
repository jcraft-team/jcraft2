package com.chappelle.jcraft.blocks.shapes;

import com.chappelle.jcraft.blocks.*;
import com.chappelle.jcraft.util.math.*;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.math.Vector3f;

import gnu.trove.list.*;

public class BlockShape_Flower extends BlockShape
{
	public BlockShape_Flower()
	{
		for(Face face : Face.values())
		{
			fullSide.put(face, Boolean.FALSE);
		}
	}
	
    @Override
    public void addTo(MeshGenContext gen, boolean isTransparent)
    {
    	Chunk chunk = gen.getChunk();
    	Block block = gen.getBlock();
    	Vector3Int blockLocation = gen.getLocation();
    	TFloatList positions = gen.getPositions();
    	TShortList indices = gen.getIndices();
    	TFloatList normals = gen.getNormals();
    	TFloatList textureCoordinates = gen.getTextureCoordinates();
    	Vector3fPool pool = gen.getVector3fPool();
    	
        Vector3f blockLocation3f = pool.get(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());

        float halfWidth = 0.25f;
        float height = 0.7f;
        
    	addFaceIndices(indices, positions.size());
        addPositions(positions, blockLocation3f.add(0.5f - halfWidth, 0, 0.5f - halfWidth));
        addPositions(positions, blockLocation3f.add(0.5f + halfWidth, 0, 0.5f + halfWidth));
        addPositions(positions, blockLocation3f.add(0.5f - halfWidth, height, 0.5f - halfWidth));
        addPositions(positions, blockLocation3f.add(0.5f + halfWidth, height, 0.5f + halfWidth));
        addSquareNormals(normals, -1, 0, -1);
        addTextureCoordinates(gen, textureCoordinates, block.getSkin(chunk, blockLocation, Face.Left).getTextureLocation());
        addLighting(gen, Face.Left);

        addFaceIndices(indices, positions.size());
        addPositions(positions, blockLocation3f.add(0.5f + halfWidth, 0, 0.5f + halfWidth));
        addPositions(positions, blockLocation3f.add(0.5f - halfWidth, 0, 0.5f - halfWidth));
        addPositions(positions, blockLocation3f.add(0.5f + halfWidth, height, 0.5f + halfWidth));
        addPositions(positions, blockLocation3f.add(0.5f - halfWidth, height, 0.5f - halfWidth));
        addSquareNormals(normals, 1, 0, -1);
        addTextureCoordinates(gen, textureCoordinates, block.getSkin(chunk, blockLocation, Face.Right).getTextureLocation());
        addLighting(gen, Face.Right);

        addFaceIndices(indices, positions.size());
        addPositions(positions, blockLocation3f.add(0.5f - halfWidth, 0, 0.5f + halfWidth));
        addPositions(positions, blockLocation3f.add(0.5f + halfWidth, 0, 0.5f - halfWidth));
        addPositions(positions, blockLocation3f.add(0.5f - halfWidth, height, 0.5f + halfWidth));
        addPositions(positions, blockLocation3f.add(0.5f + halfWidth, height, 0.5f - halfWidth));
        addSquareNormals(normals, 1, 0, 1);
        addTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
        addLighting(gen, Face.Front);

        addFaceIndices(indices, positions.size());
        addPositions(positions, blockLocation3f.add(0.5f + halfWidth, 0, 0.5f - halfWidth));
        addPositions(positions, blockLocation3f.add(0.5f - halfWidth, 0, 0.5f + halfWidth));
        addPositions(positions, blockLocation3f.add(0.5f + halfWidth, height, 0.5f - halfWidth));
        addPositions(positions, blockLocation3f.add(0.5f - halfWidth, height, 0.5f + halfWidth));
        addSquareNormals(normals, -1, 0, 1);
        addTextureCoordinates(gen, textureCoordinates, block.getSkin(chunk, blockLocation, Face.Back).getTextureLocation());
        addLighting(gen, Face.Back);
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

    private void addTextureCoordinates(MeshGenContext gen, TFloatList textureCoordinates, TextureLocation textureLocation){
        textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 0, 0));
        textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 0, 0));
        
        textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 1, 0));
        textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 1, 0));
        
        textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 0, 1));
        textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 0, 1));
        
        textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 1, 1));
        textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 1, 1));
    }
}