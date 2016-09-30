package com.chappelle.jcraft.blocks.shapes;

import com.chappelle.jcraft.blocks.*;
import com.chappelle.jcraft.util.math.Vector3Int;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.math.Vector3f;

import gnu.trove.list.*;

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
		
		for(Face face : Face.values())
		{
			fullSide.put(face, Boolean.FALSE);
		}
    }

    private boolean isXVector(Vector3f v)
    {
    	return Math.abs(v.getX()) > 0;
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
    	Face orientationFace = BlockDoor.getOrientation(blockState);
        Vector3f orientation = orientationFace.getNormal();
        
        Face homeFace = Face.getOppositeFace(Face.fromNormal(orientation));
        Vector3f offsetVector = null;
        Boolean open = BlockDoor.isOpen(blockState);
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
        
        if(shouldFaceBeAdded(gen, Face.Top, isTransparent)){
            addFaceIndices(indices, positions.size());
            addPositions(positions, faceLoc_Top_BottomLeft);
            addPositions(positions, faceLoc_Top_BottomRight);
            addPositions(positions, faceLoc_Top_TopLeft);
            addPositions(positions, faceLoc_Top_TopRight);
            addSquareNormals(normals, 0, 1, 0);
            
            addTopBottomTextureCoordinates(gen, textureCoordinates, block.getSkin(chunk, blockLocation, Face.Top).getTextureLocation());
            addLighting(gen, Face.Top);
        }
        if(shouldFaceBeAdded(gen, Face.Bottom, isTransparent)){
            addFaceIndices(indices, positions.size());
            addPositions(positions, faceLoc_Bottom_BottomRight);
            addPositions(positions, faceLoc_Bottom_BottomLeft);
            addPositions(positions, faceLoc_Bottom_TopRight);
            addPositions(positions, faceLoc_Bottom_TopLeft);
            addSquareNormals(normals, 0, -1, 0);
            addTopBottomTextureCoordinates(gen, textureCoordinates, block.getSkin(chunk, blockLocation, Face.Bottom).getTextureLocation());
            addLighting(gen, Face.Bottom);
        }
        if(shouldFaceBeAdded(gen, Face.Left, isTransparent)){
            addFaceIndices(indices, positions.size());
            addPositions(positions, faceLoc_Bottom_TopLeft);
            addPositions(positions, faceLoc_Bottom_BottomLeft);
            addPositions(positions, faceLoc_Top_TopLeft);
            addPositions(positions, faceLoc_Top_BottomLeft);
            addSquareNormals(normals, -1, 0, 0);
            if(open)
            {
            	if(homeFace == Face.Front)
            	{
            		addBackTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Back)
            	{
            		addFrontTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Left)
            	{
            		addHingeTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Right)
            	{
            		addFrameTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            }
            else
            {
            	if(homeFace == Face.Front)
            	{
            		addHingeTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Back)
            	{
            		addFrameTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Left)
            	{
            		addFrontTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Right)
            	{
            		addBackTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            }
            addLighting(gen, Face.Left);
        }
        if(shouldFaceBeAdded(gen, Face.Right, isTransparent)){
            addFaceIndices(indices, positions.size());
            addPositions(positions, faceLoc_Bottom_BottomRight);
            addPositions(positions, faceLoc_Bottom_TopRight);
            addPositions(positions, faceLoc_Top_BottomRight);
            addPositions(positions, faceLoc_Top_TopRight);
            addSquareNormals(normals, 1, 0, 0);
            
            if(open)
            {
            	if(homeFace == Face.Front)
            	{
            		addFrontTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Back)
            	{
            		addBackTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Left)
            	{
            		addFrameTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Right)
            	{
            		addHingeTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            }
            else
            {
            	if(homeFace == Face.Front)
            	{
            		addFrameTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Back)
            	{
            		addHingeTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Left)
            	{
            		addBackTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Right)
            	{
            		addFrontTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            }
            addLighting(gen, Face.Right);
        }
        if(shouldFaceBeAdded(gen, Face.Front, isTransparent)){
            addFaceIndices(indices, positions.size());
            addPositions(positions, faceLoc_Bottom_BottomLeft);
            addPositions(positions, faceLoc_Bottom_BottomRight);
            addPositions(positions, faceLoc_Top_BottomLeft);
            addPositions(positions, faceLoc_Top_BottomRight);
            addSquareNormals(normals, 0, 0, 1);
            if(open)
            {
            	if(homeFace == Face.Front)
            	{
            		addHingeTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Back)
            	{
            		addFrameTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Left)
            	{
            		addFrontTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Right)
            	{
            		addBackTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            }
            else
            {
            	if(homeFace == Face.Front)
            	{
            		addFrontTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Back)
            	{
            		addBackTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Left)
            	{
            		addFrameTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Right)
            	{
            		addHingeTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            }
            addLighting(gen, Face.Front);
        }
        if(shouldFaceBeAdded(gen, Face.Back, isTransparent)){
            addFaceIndices(indices, positions.size());
            addPositions(positions, faceLoc_Bottom_TopRight);
            addPositions(positions, faceLoc_Bottom_TopLeft);
            addPositions(positions, faceLoc_Top_TopRight);
            addPositions(positions, faceLoc_Top_TopLeft);
            addSquareNormals(normals, 0, 0, -1);
            if(open)
            {
            	if(homeFace == Face.Front)
            	{
            		addFrameTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Back)
            	{
            		addHingeTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Left)
            	{
            		addBackTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Right)
            	{
            		addFrontTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            }
            else
            {
            	if(homeFace == Face.Front)
            	{
            		addBackTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Back)
            	{
            		addFrontTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Left)
            	{
            		addHingeTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            	else if(homeFace == Face.Right)
            	{
            		addFrameTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            	}
            }
            addLighting(gen, Face.Back);
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

    private void addHingeTextureCoordinates(MeshGenContext gen, TFloatList textureCoordinates, TextureLocation textureLocation)
    {
        float xOffset = 6.0f / 32.0f;

        float v1X = getTextureCoordinatesX(gen, textureLocation, 0, 0);
        float v1Y = getTextureCoordinatesY(gen, textureLocation, 0, 0);
        float v2X = getTextureCoordinatesX(gen, textureLocation, xOffset, 0);
        float v2Y = getTextureCoordinatesY(gen, textureLocation, xOffset, 0);
        float v3X = getTextureCoordinatesX(gen, textureLocation, 0, 1);
        float v3Y = getTextureCoordinatesY(gen, textureLocation, 0, 1);
        float v4X = getTextureCoordinatesX(gen, textureLocation, xOffset, 1);
        float v4Y = getTextureCoordinatesY(gen, textureLocation, xOffset, 1);

        textureCoordinates.add(v2X);
        textureCoordinates.add(v2Y);
        textureCoordinates.add(v1X);
        textureCoordinates.add(v1Y);
        textureCoordinates.add(v4X);
        textureCoordinates.add(v4Y);
        textureCoordinates.add(v3X);
        textureCoordinates.add(v3Y);
    }

    private void addFrameTextureCoordinates(MeshGenContext gen, TFloatList textureCoordinates, TextureLocation textureLocation)
    {
    	float xOffset1 = 4.0f / 32.0f;
    	float xOffset2 = 6.0f / 32.0f;
    	
    	float v1X = getTextureCoordinatesX(gen, textureLocation, xOffset2, 0);
    	float v1Y = getTextureCoordinatesY(gen, textureLocation, xOffset2, 0);
    	float v2X = getTextureCoordinatesX(gen, textureLocation, xOffset1, 0);
    	float v2Y = getTextureCoordinatesY(gen, textureLocation, xOffset1, 0);
    	float v3X = getTextureCoordinatesX(gen, textureLocation, xOffset2, 1);
    	float v3Y = getTextureCoordinatesY(gen, textureLocation, xOffset2, 1);
    	float v4X = getTextureCoordinatesX(gen, textureLocation, xOffset1, 1);
    	float v4Y = getTextureCoordinatesY(gen, textureLocation, xOffset1, 1);
    	
    	textureCoordinates.add(v1X);
    	textureCoordinates.add(v1Y);
    	textureCoordinates.add(v2X);
    	textureCoordinates.add(v2Y);
    	textureCoordinates.add(v3X);
    	textureCoordinates.add(v3Y);
    	textureCoordinates.add(v4X);
    	textureCoordinates.add(v4Y);
    }

    private void addTopBottomTextureCoordinates(MeshGenContext gen, TFloatList textureCoordinates, TextureLocation textureLocation)
    {
    	float yOffset = 4.0f / 32.0f;
    	
    	float v1X = getTextureCoordinatesX(gen, textureLocation, 0, 1 - yOffset);
    	float v1Y = getTextureCoordinatesY(gen, textureLocation, 0, 1 - yOffset);
    	float v2X = getTextureCoordinatesX(gen, textureLocation, 1, 1 - yOffset);
    	float v2Y = getTextureCoordinatesY(gen, textureLocation, 1, 1 - yOffset);
    	float v3X = getTextureCoordinatesX(gen, textureLocation, 0, 1);
    	float v3Y = getTextureCoordinatesY(gen, textureLocation, 0, 1);
    	float v4X = getTextureCoordinatesX(gen, textureLocation, 1, 1);
    	float v4Y = getTextureCoordinatesY(gen, textureLocation, 1, 1);
    	
    	textureCoordinates.add(v3X);
    	textureCoordinates.add(v3Y);
    	textureCoordinates.add(v4X);
    	textureCoordinates.add(v4Y);
    	textureCoordinates.add(v1X);
    	textureCoordinates.add(v1Y);
    	textureCoordinates.add(v2X);
    	textureCoordinates.add(v2Y);
    }

    private void addBackTextureCoordinates(MeshGenContext gen, TFloatList textureCoordinates, TextureLocation textureLocation){
        textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 1, 0));
        textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 1, 0));
        
        textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 0, 0));
        textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 0, 0));
        
        textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 1, 1));
        textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 1, 1));
        
        textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 0, 1));
        textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 0, 1));
    }

    private void addFrontTextureCoordinates(MeshGenContext gen, TFloatList textureCoordinates, TextureLocation textureLocation){
    	textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 0, 0));
    	textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 0, 0));
    	
    	textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 1, 0));
    	textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 1, 0));
    	
    	textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 0, 1));
    	textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 0, 1));
    	
    	textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 1, 1));
    	textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 1, 1));
    }

    private Vector3f flip(Vector3f v)
    {
    	float x = v.getX();
    	float z = v.getZ();
    	
    	return new Vector3f(z, v.getY(), x);
    }
}
