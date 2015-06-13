package com.chappelle.jcraft.shapes;

import java.util.List;

import com.chappelle.jcraft.Block;
import com.chappelle.jcraft.BlockNavigator;
import com.chappelle.jcraft.BlockShape;
import com.chappelle.jcraft.BlockSkin_TextureLocation;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.blocks.BlockDoor;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class BlockShape_Door extends BlockShape
{
	private static final float DOOR_THICKNESS = 0.05f;
	
	private float[] closedExtents = new float[]{0.5f, 0.5f, 0.5f, 0.5f, DOOR_THICKNESS, DOOR_THICKNESS};
	private float[] openExtents = new float[]{0.5f, 0.5f, DOOR_THICKNESS, DOOR_THICKNESS, 0.5f, 0.5f};
	//{top, bottom, left, right, front, back}
	private float[] extents;

	public BlockShape_Door()
    {
		this.extents = openExtents;
    }

    private boolean isXVector(Vector3f v)
    {
    	return Math.abs(v.getX()) > 0;
    }
    
    @Override
    public void addTo(Chunk chunk, Block block, Vector3Int blockLocation)
    {
        Vector3f orientation = (Vector3f) chunk.getBlockStateValue(blockLocation, BlockDoor.VAR_ORIENTATION);
        
        Block.Face homeFace = BlockNavigator.getOppositeFace(Block.Face.fromNormal(orientation));
        Vector3f offsetVector = null;
        Boolean open = (Boolean)chunk.getBlockStateValue(blockLocation, BlockDoor.VAR_OPEN);
        if(open)
        {
        	if(isXVector(orientation))
        	{
        		extents = closedExtents;
        		orientation = flip(orientation);
        	}
        	else
        	{
        		extents = openExtents;
        		orientation = flip(orientation).negate();
        	}
        }
        else
        {
        	if(isXVector(orientation))
        	{
        		extents = openExtents;
        	}
        	else
        	{
        		extents = closedExtents;
        	}
        }
        offsetVector = orientation.negate().mult(0.5f - DOOR_THICKNESS);
        Vector3f faceLoc_Bottom_TopLeft = new Vector3f((0.5f - extents[2]), (0.5f - extents[1]), (0.5f - extents[5]));
        Vector3f faceLoc_Bottom_TopRight = new Vector3f((0.5f + extents[3]), (0.5f - extents[1]), (0.5f - extents[5]));
        Vector3f faceLoc_Bottom_BottomLeft = new Vector3f((0.5f - extents[2]), (0.5f - extents[1]), (0.5f + extents[4]));
        Vector3f faceLoc_Bottom_BottomRight = new Vector3f((0.5f + extents[3]), (0.5f - extents[1]), (0.5f + extents[4]));
        Vector3f faceLoc_Top_TopLeft = new Vector3f((0.5f - extents[2]), (0.5f + extents[0]), (0.5f - extents[5]));
        Vector3f faceLoc_Top_TopRight = new Vector3f((0.5f + extents[3]), (0.5f + extents[0]), (0.5f - extents[5]));
        Vector3f faceLoc_Top_BottomLeft = new Vector3f((0.5f - extents[2]), (0.5f + extents[0]), (0.5f + extents[4]));
        Vector3f faceLoc_Top_BottomRight = new Vector3f((0.5f + extents[3]), (0.5f + extents[0]), (0.5f + extents[4]));
        ShapeVectors vectors =
                new ShapeVectors(
                faceLoc_Bottom_TopLeft, faceLoc_Bottom_TopRight, faceLoc_Bottom_BottomLeft, faceLoc_Bottom_BottomRight,
                faceLoc_Top_TopLeft, faceLoc_Top_TopRight, faceLoc_Top_BottomLeft, faceLoc_Top_BottomRight);

        Vector3f blockLocation3f = new Vector3f(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());
        vectors.add(blockLocation3f);
        vectors.add(offsetVector);
        
        if(shouldFaceBeAdded(chunk, blockLocation, Block.Face.Top)){
            addFaceIndices(indices, positions.size());
            positions.add(faceLoc_Top_BottomLeft);
            positions.add(faceLoc_Top_BottomRight);
            positions.add(faceLoc_Top_TopLeft);
            positions.add(faceLoc_Top_TopRight);
            addSquareNormals(normals, 0, 1, 0);
            
            addTopBottomTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Top).getTextureLocation());
            addLighting(chunk, blockLocation, Block.Face.Top);
        }
        if(shouldFaceBeAdded(chunk, blockLocation, Block.Face.Bottom)){
            addFaceIndices(indices, positions.size());
            positions.add(faceLoc_Bottom_BottomRight);
            positions.add(faceLoc_Bottom_BottomLeft);
            positions.add(faceLoc_Bottom_TopRight);
            positions.add(faceLoc_Bottom_TopLeft);
            addSquareNormals(normals, 0, -1, 0);
            addTopBottomTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Bottom).getTextureLocation());
            addLighting(chunk, blockLocation, Block.Face.Bottom);
        }
        if(shouldFaceBeAdded(chunk, blockLocation, Block.Face.Left)){
            addFaceIndices(indices, positions.size());
            positions.add(faceLoc_Bottom_TopLeft);
            positions.add(faceLoc_Bottom_BottomLeft);
            positions.add(faceLoc_Top_TopLeft);
            positions.add(faceLoc_Top_BottomLeft);
            addSquareNormals(normals, -1, 0, 0);
            if(open)
            {
            	if(homeFace == Block.Face.Front)
            	{
            		addBackTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Back)
            	{
            		addFrontTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Left)
            	{
            		addHingeTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Right)
            	{
            		addFrameTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            }
            else
            {
            	if(homeFace == Block.Face.Front)
            	{
            		addHingeTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Back)
            	{
            		addFrameTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Left)
            	{
            		addFrontTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Right)
            	{
            		addBackTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            }
            addLighting(chunk, blockLocation, Block.Face.Left);
        }
        if(shouldFaceBeAdded(chunk, blockLocation, Block.Face.Right)){
            addFaceIndices(indices, positions.size());
            positions.add(faceLoc_Bottom_BottomRight);
            positions.add(faceLoc_Bottom_TopRight);
            positions.add(faceLoc_Top_BottomRight);
            positions.add(faceLoc_Top_TopRight);
            addSquareNormals(normals, 1, 0, 0);
            
            if(open)
            {
            	if(homeFace == Block.Face.Front)
            	{
            		addFrontTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Back)
            	{
            		addBackTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Left)
            	{
            		addFrameTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Right)
            	{
            		addHingeTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            }
            else
            {
            	if(homeFace == Block.Face.Front)
            	{
            		addFrameTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Back)
            	{
            		addHingeTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Left)
            	{
            		addBackTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Right)
            	{
            		addFrontTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            }
            addLighting(chunk, blockLocation, Block.Face.Right);
        }
        if(shouldFaceBeAdded(chunk, blockLocation, Block.Face.Front)){
            addFaceIndices(indices, positions.size());
            positions.add(faceLoc_Bottom_BottomLeft);
            positions.add(faceLoc_Bottom_BottomRight);
            positions.add(faceLoc_Top_BottomLeft);
            positions.add(faceLoc_Top_BottomRight);
            addSquareNormals(normals, 0, 0, 1);
            if(open)
            {
            	if(homeFace == Block.Face.Front)
            	{
            		addHingeTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Back)
            	{
            		addFrameTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Left)
            	{
            		addFrontTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Right)
            	{
            		addBackTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            }
            else
            {
            	if(homeFace == Block.Face.Front)
            	{
            		addFrontTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Back)
            	{
            		addBackTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Left)
            	{
            		addFrameTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Right)
            	{
            		addHingeTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            }
            addLighting(chunk, blockLocation, Block.Face.Front);
        }
        if(shouldFaceBeAdded(chunk, blockLocation, Block.Face.Back)){
            addFaceIndices(indices, positions.size());
            positions.add(faceLoc_Bottom_TopRight);
            positions.add(faceLoc_Bottom_TopLeft);
            positions.add(faceLoc_Top_TopRight);
            positions.add(faceLoc_Top_TopLeft);
            addSquareNormals(normals, 0, 0, -1);
            if(open)
            {
            	if(homeFace == Block.Face.Front)
            	{
            		addFrameTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Back)
            	{
            		addHingeTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Left)
            	{
            		addBackTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Right)
            	{
            		addFrontTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            }
            else
            {
            	if(homeFace == Block.Face.Front)
            	{
            		addBackTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Back)
            	{
            		addFrontTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Left)
            	{
            		addHingeTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Block.Face.Right)
            	{
            		addFrameTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            	}
            }
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

    private void addHingeTextureCoordinates(Chunk chunk, List<Vector2f> textureCoordinates, BlockSkin_TextureLocation textureLocation)
    {
        float xOffset = 6.0f / 32.0f;

        Vector2f v1 = getTextureCoordinates(chunk, textureLocation, 0, 0);
        Vector2f v2 = getTextureCoordinates(chunk, textureLocation, xOffset, 0);
        Vector2f v3 = getTextureCoordinates(chunk, textureLocation, 0, 1);
        Vector2f v4 = getTextureCoordinates(chunk, textureLocation, xOffset, 1);

        textureCoordinates.add(v2);
        textureCoordinates.add(v1);
        textureCoordinates.add(v4);
        textureCoordinates.add(v3);
    }

    private void addFrameTextureCoordinates(Chunk chunk, List<Vector2f> textureCoordinates, BlockSkin_TextureLocation textureLocation)
    {
    	float xOffset1 = 4.0f / 32.0f;
    	float xOffset2 = 6.0f / 32.0f;
    	
    	Vector2f v1 = getTextureCoordinates(chunk, textureLocation, xOffset2, 0);
    	Vector2f v2 = getTextureCoordinates(chunk, textureLocation, xOffset1, 0);
    	Vector2f v3 = getTextureCoordinates(chunk, textureLocation, xOffset2, 1);
    	Vector2f v4 = getTextureCoordinates(chunk, textureLocation, xOffset1, 1);
    	
    	textureCoordinates.add(v1);
    	textureCoordinates.add(v2);
    	textureCoordinates.add(v3);
    	textureCoordinates.add(v4);
    }

    private void addTopBottomTextureCoordinates(Chunk chunk, List<Vector2f> textureCoordinates, BlockSkin_TextureLocation textureLocation)
    {
    	float yOffset = 4.0f / 32.0f;
    	
    	Vector2f v1 = getTextureCoordinates(chunk, textureLocation, 0, 1 - yOffset);
    	Vector2f v2 = getTextureCoordinates(chunk, textureLocation, 1, 1 - yOffset);
    	Vector2f v3 = getTextureCoordinates(chunk, textureLocation, 0, 1);
    	Vector2f v4 = getTextureCoordinates(chunk, textureLocation, 1, 1);
    	
    	textureCoordinates.add(v3);
    	textureCoordinates.add(v4);
    	textureCoordinates.add(v1);
    	textureCoordinates.add(v2);
    }

    private void addBackTextureCoordinates(Chunk chunk, List<Vector2f> textureCoordinates, BlockSkin_TextureLocation textureLocation){
        textureCoordinates.add(getTextureCoordinates(chunk, textureLocation, 1, 0));
        textureCoordinates.add(getTextureCoordinates(chunk, textureLocation, 0, 0));
        textureCoordinates.add(getTextureCoordinates(chunk, textureLocation, 1, 1));
        textureCoordinates.add(getTextureCoordinates(chunk, textureLocation, 0, 1));
    }

    private void addFrontTextureCoordinates(Chunk chunk, List<Vector2f> textureCoordinates, BlockSkin_TextureLocation textureLocation){
    	textureCoordinates.add(getTextureCoordinates(chunk, textureLocation, 0, 0));
    	textureCoordinates.add(getTextureCoordinates(chunk, textureLocation, 1, 0));
    	textureCoordinates.add(getTextureCoordinates(chunk, textureLocation, 0, 1));
    	textureCoordinates.add(getTextureCoordinates(chunk, textureLocation, 1, 1));
    }

    @Override
    protected boolean canBeMerged(Block.Face face){
        boolean isAllowed = true;
        Block.Face oppositeFace = BlockNavigator.getOppositeFace(face);
        for(int i=0;i<extents.length;i++){
            if((i != oppositeFace.ordinal()) && (extents[i] != 0.5f)){
                isAllowed = false;
                break;
            }
        }
        return isAllowed;
    }
    
    private Vector3f flip(Vector3f v)
    {
    	float x = v.getX();
    	float z = v.getZ();
    	
    	return new Vector3f(z, v.getY(), x);
    }
}
