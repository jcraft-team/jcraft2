package com.chappelle.jcraft.shapes;

import java.util.List;

import com.chappelle.jcraft.Block;
import com.chappelle.jcraft.BlockNavigator;
import com.chappelle.jcraft.BlockShape;
import com.chappelle.jcraft.BlockSkin_TextureLocation;
import com.chappelle.jcraft.Chunk;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.blocks.BlockLadder;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class BlockShape_Ladder extends BlockShape
{
	//{top, bottom, left, right, front, back}
	private float[] extents;
	private float[] back_extents = new float[] { 0.5f, 0.5f, 0.5f, 0.5f, 0f, -0.5f };
	private float[] front_extents = new float[] { 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0f };
	private float[] left_extents = new float[] { 0.5f, 0.5f, -0.5f, 0f, 0.5f, 0.5f };
	private float[] right_extents = new float[] { 0.5f, 0.5f, 0f, -0.5f, 0.5f, 0.5f };

    @Override
    public void addTo(Chunk chunk, Block block, Vector3Int blockLocation){
    	Vector3f orientation = (Vector3f) chunk.getBlockStateValue(blockLocation, BlockLadder.VAR_ORIENTATION);
    	Block.Face homeFace = BlockNavigator.getOppositeFace(Block.Face.fromNormal(orientation));
    	if(homeFace == Block.Face.Back)
    	{
    		extents = back_extents;
    	}
    	else if(homeFace == Block.Face.Front)
    	{
    		extents = front_extents;
    	}
    	else if(homeFace == Block.Face.Left)
    	{
    		extents = left_extents;
    	}
    	else if(homeFace == Block.Face.Right)
    	{
    		extents = right_extents;
    	}
        Vector3f blockLocation3f = new Vector3f(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());
        Vector3f faceLoc_Bottom_TopLeft = blockLocation3f.add(new Vector3f((0.5f - extents[2]), (0.5f - extents[1]), (0.5f - extents[5])));
        Vector3f faceLoc_Bottom_TopRight = blockLocation3f.add(new Vector3f((0.5f + extents[3]), (0.5f - extents[1]), (0.5f - extents[5])));
        Vector3f faceLoc_Bottom_BottomLeft = blockLocation3f.add(new Vector3f((0.5f - extents[2]), (0.5f - extents[1]), (0.5f + extents[4])));
        Vector3f faceLoc_Bottom_BottomRight = blockLocation3f.add(new Vector3f((0.5f + extents[3]), (0.5f - extents[1]), (0.5f + extents[4])));
        Vector3f faceLoc_Top_TopLeft = blockLocation3f.add(new Vector3f((0.5f - extents[2]), (0.5f + extents[0]), (0.5f - extents[5])));
        Vector3f faceLoc_Top_TopRight = blockLocation3f.add(new Vector3f((0.5f + extents[3]), (0.5f + extents[0]), (0.5f - extents[5])));
        Vector3f faceLoc_Top_BottomLeft = blockLocation3f.add(new Vector3f((0.5f - extents[2]), (0.5f + extents[0]), (0.5f + extents[4])));
        Vector3f faceLoc_Top_BottomRight = blockLocation3f.add(new Vector3f((0.5f + extents[3]), (0.5f + extents[0]), (0.5f + extents[4])));

        if(homeFace == Block.Face.Left)//FIXME: Ray cast not working
        {
        	addFaceIndices(indices, positions.size());
        	positions.add(faceLoc_Bottom_TopLeft);
        	positions.add(faceLoc_Bottom_BottomLeft);
        	positions.add(faceLoc_Top_TopLeft);
        	positions.add(faceLoc_Top_BottomLeft);
        	addSquareNormals(normals, 1, 0, 0);
        	addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Left).getTextureLocation());
        	addLighting(chunk, blockLocation, Block.Face.Left);
        }
        else if(homeFace == Block.Face.Right)
        {
        	addFaceIndices(indices, positions.size());
        	positions.add(faceLoc_Bottom_BottomRight);
        	positions.add(faceLoc_Bottom_TopRight);
        	positions.add(faceLoc_Top_BottomRight);
        	positions.add(faceLoc_Top_TopRight);
        	addSquareNormals(normals, -1, 0, 0);
        	addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Right).getTextureLocation());
        	addLighting(chunk, blockLocation, Block.Face.Right);
        }
        else if(homeFace == Block.Face.Front)
        {
        	addFaceIndices(indices, positions.size());
        	positions.add(faceLoc_Bottom_BottomLeft);
        	positions.add(faceLoc_Bottom_BottomRight);
        	positions.add(faceLoc_Top_BottomLeft);
        	positions.add(faceLoc_Top_BottomRight);
        	addSquareNormals(normals, 0, 0, -1);
        	addTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
        	addLighting(chunk, blockLocation, Block.Face.Front);
        }
        else if(homeFace == Block.Face.Back)//FIXME: Ray cast not working
        {
        	addFaceIndices(indices, positions.size());
        	positions.add(faceLoc_Bottom_TopRight);
        	positions.add(faceLoc_Bottom_TopLeft);
        	positions.add(faceLoc_Top_TopRight);
        	positions.add(faceLoc_Top_TopLeft);
        	addSquareNormals(normals, 0, 0, 1);//Normal is reversed from what you would normally think of
        	addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Back).getTextureLocation());
        	addLighting(chunk, blockLocation, Block.Face.Back);
        }
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