package com.chappelle.jcraft.shapes;

import java.util.List;

import com.chappelle.jcraft.Block;
import com.chappelle.jcraft.BlockShape;
import com.chappelle.jcraft.BlockSkin_TextureLocation;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class BlockShape_Flower extends BlockShape
{
    @Override
    public void addTo(Chunk chunk, Block block, Vector3Int blockLocation)
    {
        Vector3f blockLocation3f = new Vector3f(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());

        float halfWidth = 0.25f;
        float height = 0.7f;
        
    	addFaceIndices(indices, positions.size());
        positions.add(blockLocation3f.add(new Vector3f(0.5f - halfWidth, 0, 0.5f - halfWidth)));
        positions.add(blockLocation3f.add(new Vector3f(0.5f + halfWidth, 0, 0.5f + halfWidth)));
        positions.add(blockLocation3f.add(new Vector3f(0.5f - halfWidth, height, 0.5f - halfWidth)));
        positions.add(blockLocation3f.add(new Vector3f(0.5f + halfWidth, height, 0.5f + halfWidth)));
        addSquareNormals(normals, -1, 0, -1);
        addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Left).getTextureLocation());
        addLighting(chunk, blockLocation, Block.Face.Left);

        addFaceIndices(indices, positions.size());
        positions.add(blockLocation3f.add(new Vector3f(0.5f + halfWidth, 0, 0.5f + halfWidth)));
        positions.add(blockLocation3f.add(new Vector3f(0.5f - halfWidth, 0, 0.5f - halfWidth)));
        positions.add(blockLocation3f.add(new Vector3f(0.5f + halfWidth, height, 0.5f + halfWidth)));
        positions.add(blockLocation3f.add(new Vector3f(0.5f - halfWidth, height, 0.5f - halfWidth)));
        addSquareNormals(normals, 1, 0, -1);
        addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Right).getTextureLocation());
        addLighting(chunk, blockLocation, Block.Face.Right);

        addFaceIndices(indices, positions.size());
        positions.add(blockLocation3f.add(new Vector3f(0.5f - halfWidth, 0, 0.5f + halfWidth)));
        positions.add(blockLocation3f.add(new Vector3f(0.5f + halfWidth, 0, 0.5f - halfWidth)));
        positions.add(blockLocation3f.add(new Vector3f(0.5f - halfWidth, height, 0.5f + halfWidth)));
        positions.add(blockLocation3f.add(new Vector3f(0.5f + halfWidth, height, 0.5f - halfWidth)));
        addSquareNormals(normals, 1, 0, 1);
        addTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
        addLighting(chunk, blockLocation, Block.Face.Front);

        addFaceIndices(indices, positions.size());
        positions.add(blockLocation3f.add(new Vector3f(0.5f + halfWidth, 0, 0.5f - halfWidth)));
        positions.add(blockLocation3f.add(new Vector3f(0.5f - halfWidth, 0, 0.5f + halfWidth)));
        positions.add(blockLocation3f.add(new Vector3f(0.5f + halfWidth, height, 0.5f - halfWidth)));
        positions.add(blockLocation3f.add(new Vector3f(0.5f - halfWidth, height, 0.5f + halfWidth)));
        addSquareNormals(normals, -1, 0, 1);
        addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Back).getTextureLocation());
        addLighting(chunk, blockLocation, Block.Face.Back);
    }

    private void addFaceIndices(List<Short> indices, int offset){
        indices.add((short) (offset + 2));
        indices.add((short) (offset + 0));
        indices.add((short) (offset + 1));
        indices.add((short) (offset + 1));
        indices.add((short) (offset + 3));
        indices.add((short) (offset + 2));
    }
    
    private void addSquareNormals(List<Float> normals, float normalX, float normalY, float normalZ){
        for(int i=0;i<4;i++){
            normals.add(normalX);
            normals.add(normalY);
            normals.add(normalZ);
        }
    }

    private void addTextureCoordinates(Chunk chunk, List<Vector2f> textureCoordinates, BlockSkin_TextureLocation textureLocation){
        textureCoordinates.add(getTextureCoordinates(chunk, textureLocation, 0, 0));
        textureCoordinates.add(getTextureCoordinates(chunk, textureLocation, 1, 0));
        textureCoordinates.add(getTextureCoordinates(chunk, textureLocation, 0, 1));
        textureCoordinates.add(getTextureCoordinates(chunk, textureLocation, 1, 1));
    }

    @Override
    protected boolean canBeMerged(Block.Face face){
    	return false;
    }
    
}