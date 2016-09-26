package com.chappelle.jcraft.blocks.shapes;

import com.chappelle.jcraft.blocks.*;
import com.chappelle.jcraft.util.math.*;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.math.Vector3f;

import gnu.trove.list.*;

public class BlockShape_Ladder extends BlockShape
{
	//{top, bottom, left, right, front, back}
	private float[] extents;
	private float[] back_extents = new float[] { 0.5f, 0.5f, 0.5f, 0.5f, 0f, -0.5f };
	private float[] front_extents = new float[] { 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0f };
	private float[] left_extents = new float[] { 0.5f, 0.5f, -0.5f, 0f, 0.5f, 0.5f };
	private float[] right_extents = new float[] { 0.5f, 0.5f, 0f, -0.5f, 0.5f, 0.5f };

	public BlockShape_Ladder()
	{
		for(Block.Face face : Block.Face.values())
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

    	byte blockState = chunk.getBlockState(blockLocation);
    	Vector3f orientation = BlockLadder.getOrientation(blockState).normal;
    	Block.Face homeFace = Block.Face.getOppositeFace(Block.Face.fromNormal(orientation));
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
        	addPositions(positions, faceLoc_Bottom_TopLeft);
        	addPositions(positions, faceLoc_Bottom_BottomLeft);
        	addPositions(positions, faceLoc_Top_TopLeft);
        	addPositions(positions, faceLoc_Top_BottomLeft);
        	addSquareNormals(normals, 1, 0, 0);
        	addTextureCoordinates(gen, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Left).getTextureLocation());
        	addLighting(gen, Block.Face.Left);
        }
        else if(homeFace == Block.Face.Right)
        {
        	addFaceIndices(indices, positions.size());
        	addPositions(positions, faceLoc_Bottom_BottomRight);
        	addPositions(positions, faceLoc_Bottom_TopRight);
        	addPositions(positions, faceLoc_Top_BottomRight);
        	addPositions(positions, faceLoc_Top_TopRight);
        	addSquareNormals(normals, -1, 0, 0);
        	addTextureCoordinates(gen, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Right).getTextureLocation());
        	addLighting(gen, Block.Face.Right);
        }
        else if(homeFace == Block.Face.Front)
        {
        	addFaceIndices(indices, positions.size());
        	addPositions(positions, faceLoc_Bottom_BottomLeft);
        	addPositions(positions, faceLoc_Bottom_BottomRight);
        	addPositions(positions, faceLoc_Top_BottomLeft);
        	addPositions(positions, faceLoc_Top_BottomRight);
        	addSquareNormals(normals, 0, 0, -1);
        	addTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
        	addLighting(gen, Block.Face.Front);
        }
        else if(homeFace == Block.Face.Back)//FIXME: Ray cast not working
        {
        	addFaceIndices(indices, positions.size());
        	addPositions(positions, faceLoc_Bottom_TopRight);
        	addPositions(positions, faceLoc_Bottom_TopLeft);
        	addPositions(positions, faceLoc_Top_TopRight);
        	addPositions(positions, faceLoc_Top_TopLeft);
        	addSquareNormals(normals, 0, 0, 1);//Normal is reversed from what you would normally think of
        	addTextureCoordinates(gen, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Back).getTextureLocation());
        	addLighting(gen, Block.Face.Back);
        }
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

    private void addTextureCoordinates(MeshGenContext gen, TFloatList textureCoordinates, BlockSkin_TextureLocation textureLocation){
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