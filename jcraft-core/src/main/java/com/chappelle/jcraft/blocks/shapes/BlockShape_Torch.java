package com.chappelle.jcraft.blocks.shapes;

import com.chappelle.jcraft.blocks.*;
import com.chappelle.jcraft.util.math.Vector3Int;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.math.*;

import gnu.trove.list.*;

public class BlockShape_Torch extends BlockShape
{
    public BlockShape_Torch()
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

        //This is so that when we add to the blockLocation origin, the rotation is centered
        float height = 0.58f;
        float width = 0.1f;
        float halfHeight = height / 2;
        float halfWidth = width / 2;

        //Calculate offsets from block origin
        Vector3f faceLoc_Bottom_TopLeft = new Vector3f(-halfWidth, -halfHeight, -halfWidth);
        Vector3f faceLoc_Bottom_TopRight = new Vector3f(halfWidth, -halfHeight, -halfWidth);
        Vector3f faceLoc_Bottom_BottomLeft = new Vector3f(-halfWidth, -halfHeight, halfWidth);
        Vector3f faceLoc_Bottom_BottomRight = new Vector3f(halfWidth, -halfHeight, halfWidth);
        Vector3f faceLoc_Top_TopLeft = new Vector3f(-halfWidth, halfHeight, -halfWidth);
        Vector3f faceLoc_Top_TopRight = new Vector3f(halfWidth, halfHeight, -halfWidth);
        Vector3f faceLoc_Top_BottomLeft = new Vector3f(-halfWidth, halfHeight, halfWidth);
        Vector3f faceLoc_Top_BottomRight = new Vector3f(halfWidth, halfHeight, halfWidth);
        ShapeVectors vectors =
                new ShapeVectors(
                faceLoc_Bottom_TopLeft, faceLoc_Bottom_TopRight, faceLoc_Bottom_BottomLeft, faceLoc_Bottom_BottomRight,
                faceLoc_Top_TopLeft, faceLoc_Top_TopRight, faceLoc_Top_BottomLeft, faceLoc_Top_BottomRight);

        //Get orientation from the block state
        byte blockState = chunk.getBlockState(blockLocation);
        Face face = BlockTorch.getOrientation(blockState);
        Vector3f normalVector = face.getNormal();
        if(normalVector == null)
        {
            normalVector = Vector3f.UNIT_Z.clone();
        }
        if (face != Face.Top)
        {
            //Apply rotation to offsets   
            vectors.rotate(getRotation(face));
        }
        
        //Set the rotated points at the block location
        Vector3f blockLocation3f = new Vector3f(blockLocation.getX() + 0.5f, blockLocation.getY() + 0.29f, blockLocation.getZ() + 0.5f);
        if (face != Face.Top)
        {
            blockLocation3f.addLocal(0.0f, 0.3f, 0.0f);
            
            //Slide rotated torch up against the block            
            vectors.subtract(normalVector.mult(0.4f));            
        }
        vectors.add(blockLocation3f);


        //Add to mesh
        if (shouldFaceBeAdded(gen, Face.Top, isTransparent))
        {
            addFaceIndices(indices, positions.size());
            addPositions(positions, faceLoc_Top_BottomLeft);
            addPositions(positions, faceLoc_Top_BottomRight);
            addPositions(positions, faceLoc_Top_TopLeft);
            addPositions(positions, faceLoc_Top_TopRight);
            addSquareNormals(normals, 0, 1, 0);

            addTopTextureCoordinates(gen, textureCoordinates, block.getSkin(chunk, blockLocation, Face.Top).getTextureLocation());
            addLighting(gen, Face.Top);            
        }
        if (shouldFaceBeAdded(gen, Face.Bottom, isTransparent))
        {
            addFaceIndices(indices, positions.size());
            addPositions(positions, faceLoc_Bottom_BottomRight);
            addPositions(positions, faceLoc_Bottom_BottomLeft);
            addPositions(positions, faceLoc_Bottom_TopRight);
            addPositions(positions, faceLoc_Bottom_TopLeft);
            addSquareNormals(normals, 0, -1, 0);
            addTextureCoordinates(gen, textureCoordinates, block.getSkin(chunk, blockLocation, Face.Bottom).getTextureLocation());
            addLighting(gen, Face.Bottom);
        }
        if (shouldFaceBeAdded(gen, Face.Left, isTransparent))
        {
            addFaceIndices(indices, positions.size());
            addPositions(positions, faceLoc_Bottom_TopLeft);
            addPositions(positions, faceLoc_Bottom_BottomLeft);
            addPositions(positions, faceLoc_Top_TopLeft);
            addPositions(positions, faceLoc_Top_BottomLeft);
            addSquareNormals(normals, -1, 0, 0);
            addTextureCoordinates(gen, textureCoordinates, block.getSkin(chunk, blockLocation, Face.Left).getTextureLocation());
            addLighting(gen, Face.Left);
        }
        if (shouldFaceBeAdded(gen, Face.Right, isTransparent))
        {
            addFaceIndices(indices, positions.size());
            addPositions(positions, faceLoc_Bottom_BottomRight);
            addPositions(positions, faceLoc_Bottom_TopRight);
            addPositions(positions, faceLoc_Top_BottomRight);
            addPositions(positions, faceLoc_Top_TopRight);
            addSquareNormals(normals, 1, 0, 0);
            addTextureCoordinates(gen, textureCoordinates, block.getSkin(chunk, blockLocation, Face.Right).getTextureLocation());
            addLighting(gen, Face.Right);
        }
        if (shouldFaceBeAdded(gen, Face.Front, isTransparent))
        {
            addFaceIndices(indices, positions.size());
            addPositions(positions, faceLoc_Bottom_BottomLeft);
            addPositions(positions, faceLoc_Bottom_BottomRight);
            addPositions(positions, faceLoc_Top_BottomLeft);
            addPositions(positions, faceLoc_Top_BottomRight);
            addSquareNormals(normals, 0, 0, 1);
            addTextureCoordinates(gen, textureCoordinates, block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            addLighting(gen, Face.Front);
        }
        if (shouldFaceBeAdded(gen, Face.Back, isTransparent))
        {
            addFaceIndices(indices, positions.size());
            addPositions(positions, faceLoc_Bottom_TopRight);
            addPositions(positions, faceLoc_Bottom_TopLeft);
            addPositions(positions, faceLoc_Top_TopRight);
            addPositions(positions, faceLoc_Top_TopLeft);
            addSquareNormals(normals, 0, 0, -1);
            addTextureCoordinates(gen, textureCoordinates, block.getSkin(chunk, blockLocation, Face.Back).getTextureLocation());
            addLighting(gen, Face.Back);
        }
    }

    private Quaternion getRotation(Face face)
    {
        Quaternion q = new Quaternion();
        float angle = 45.0f;

        if (face == Face.Left)
        {
            q.fromAngleNormalAxis(angle * FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
        }
        else if (face == Face.Right)
        {
            q.fromAngleNormalAxis((360 - angle) * FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
        }
        else if (face == Face.Front)
        {
            q.fromAngleNormalAxis(angle * FastMath.DEG_TO_RAD, Vector3f.UNIT_X);
        }
        else if (face == Face.Back)
        {
            q.fromAngleNormalAxis((360 - angle) * FastMath.DEG_TO_RAD, Vector3f.UNIT_X);
        }
        return q;
    }

    private void addFaceIndices(TShortList indices, int offset)
    {
    	offset = offset/3;
        indices.add((short) (offset + 2));
        indices.add((short) (offset + 0));
        indices.add((short) (offset + 1));
        indices.add((short) (offset + 1));
        indices.add((short) (offset + 3));
        indices.add((short) (offset + 2));
    }

    private void addSquareNormals(TFloatList normals, float normalX, float normalY, float normalZ)
    {
        for (int i = 0; i < 4; i++)
        {
            normals.add(normalX);
            normals.add(normalY);
            normals.add(normalZ);
        }
    }

    private void addTextureCoordinates(MeshGenContext gen, TFloatList textureCoordinates, BlockSkin_TextureLocation textureLocation)
    {
        float xOffset = 14.0f / 32.0f;
        float topYOffset = 12 / 32.0f;

        textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 0 + xOffset, 0));
        textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 0 + xOffset, 0));
        
        textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 1 - xOffset, 0));
        textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 1 - xOffset, 0));
        
        textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 0 + xOffset, 1 - topYOffset));
        textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 0 + xOffset, 1 - topYOffset));
        
        textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 1 - xOffset, 1 - topYOffset));
        textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 1 - xOffset, 1 - topYOffset));
    }

    private void addTopTextureCoordinates(MeshGenContext gen, TFloatList textureCoordinates, BlockSkin_TextureLocation textureLocation)
    {
        float xOffset = 14.0f / 32.0f;
        float topYOffset = 12 / 32.0f;
        float bottomYOffset = 16 / 32.0f;

        textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 0 + xOffset, 0 + bottomYOffset));
        textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 0 + xOffset, 0 + bottomYOffset));
        
        textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 1 - xOffset, 0 + bottomYOffset));
        textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 1 - xOffset, 0 + bottomYOffset));
        
        textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 0 + xOffset, 1 - topYOffset));
        textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 0 + xOffset, 1 - topYOffset));
        
        textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 1 - xOffset, 1 - topYOffset));
        textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 1 - xOffset, 1 - topYOffset));
    }

    @Override
    protected boolean shouldFaceBeAdded(MeshGenContext gen, Face face, boolean isTransparent)
    {
        return true;
    }
}
