package com.cubes.shapes;

import java.util.List;

import com.chappelle.jcraft.blocks.Torch;
import com.cubes.Block;
import com.cubes.BlockShape;
import com.cubes.BlockSkin_TextureLocation;
import com.cubes.Chunk;
import com.cubes.Vector3Int;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class BlockShape_Torch extends BlockShape
{
    public BlockShape_Torch(){}
    
    @Override
    public void addTo(Chunk chunk, Block block, Vector3Int blockLocation)
    {
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
        Vector3f normalVector = (Vector3f) chunk.getBlockStateValue(blockLocation, Torch.VAR_ORIENTATION);
        if(normalVector == null)
        {
            normalVector = Vector3f.UNIT_Z.clone();
        }
        Block.Face face = Block.Face.fromNormal(normalVector);
        if (face != Block.Face.Top)
        {
            //Apply rotation to offsets   
            vectors.rotate(getRotation(face));
        }
        
        //Set the rotated points at the block location
        Vector3f blockLocation3f = new Vector3f(blockLocation.getX() + 0.5f, blockLocation.getY() + 0.29f, blockLocation.getZ() + 0.5f);
        if (face != Block.Face.Top)
        {
            blockLocation3f.addLocal(0.0f, 0.3f, 0.0f);
            
            //Slide rotated torch up against the block            
            vectors.subtract(normalVector.mult(0.4f));            
        }
        vectors.add(blockLocation3f);


        //Add to mesh
        if (shouldFaceBeAdded(chunk, blockLocation, Block.Face.Top))
        {
            addFaceIndices(indices, positions.size());
            positions.add(faceLoc_Top_BottomLeft);
            positions.add(faceLoc_Top_BottomRight);
            positions.add(faceLoc_Top_TopLeft);
            positions.add(faceLoc_Top_TopRight);
            addSquareNormals(normals, 0, 1, 0);

            addTopTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Top).getTextureLocation());
            addLighting(chunk, blockLocation, Block.Face.Top);            
        }
        if (shouldFaceBeAdded(chunk, blockLocation, Block.Face.Bottom))
        {
            addFaceIndices(indices, positions.size());
            positions.add(faceLoc_Bottom_BottomRight);
            positions.add(faceLoc_Bottom_BottomLeft);
            positions.add(faceLoc_Bottom_TopRight);
            positions.add(faceLoc_Bottom_TopLeft);
            addSquareNormals(normals, 0, -1, 0);
            addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Bottom).getTextureLocation());
            addLighting(chunk, blockLocation, Block.Face.Bottom);
        }
        if (shouldFaceBeAdded(chunk, blockLocation, Block.Face.Left))
        {
            addFaceIndices(indices, positions.size());
            positions.add(faceLoc_Bottom_TopLeft);
            positions.add(faceLoc_Bottom_BottomLeft);
            positions.add(faceLoc_Top_TopLeft);
            positions.add(faceLoc_Top_BottomLeft);
            addSquareNormals(normals, -1, 0, 0);
            addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Left).getTextureLocation());
            addLighting(chunk, blockLocation, Block.Face.Left);
        }
        if (shouldFaceBeAdded(chunk, blockLocation, Block.Face.Right))
        {
            addFaceIndices(indices, positions.size());
            positions.add(faceLoc_Bottom_BottomRight);
            positions.add(faceLoc_Bottom_TopRight);
            positions.add(faceLoc_Top_BottomRight);
            positions.add(faceLoc_Top_TopRight);
            addSquareNormals(normals, 1, 0, 0);
            addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Right).getTextureLocation());
            addLighting(chunk, blockLocation, Block.Face.Right);
        }
        if (shouldFaceBeAdded(chunk, blockLocation, Block.Face.Front))
        {
            addFaceIndices(indices, positions.size());
            positions.add(faceLoc_Bottom_BottomLeft);
            positions.add(faceLoc_Bottom_BottomRight);
            positions.add(faceLoc_Top_BottomLeft);
            positions.add(faceLoc_Top_BottomRight);
            addSquareNormals(normals, 0, 0, 1);
            addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            addLighting(chunk, blockLocation, Block.Face.Front);
        }
        if (shouldFaceBeAdded(chunk, blockLocation, Block.Face.Back))
        {
            addFaceIndices(indices, positions.size());
            positions.add(faceLoc_Bottom_TopRight);
            positions.add(faceLoc_Bottom_TopLeft);
            positions.add(faceLoc_Top_TopRight);
            positions.add(faceLoc_Top_TopLeft);
            addSquareNormals(normals, 0, 0, -1);
            addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Back).getTextureLocation());
            addLighting(chunk, blockLocation, Block.Face.Back);
        }
    }

    private Quaternion getRotation(Block.Face face)
    {
        Quaternion q = new Quaternion();
        float angle = 45.0f;

        if (face == Block.Face.Left)
        {
            q.fromAngleNormalAxis(angle * FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
        }
        else if (face == Block.Face.Right)
        {
            q.fromAngleNormalAxis((360 - angle) * FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
        }
        else if (face == Block.Face.Front)
        {
            q.fromAngleNormalAxis(angle * FastMath.DEG_TO_RAD, Vector3f.UNIT_X);
        }
        else if (face == Block.Face.Back)
        {
            q.fromAngleNormalAxis((360 - angle) * FastMath.DEG_TO_RAD, Vector3f.UNIT_X);
        }
        return q;
    }

    private void addFaceIndices(List<Short> indices, int offset)
    {
        indices.add((short) (offset + 2));
        indices.add((short) (offset + 0));
        indices.add((short) (offset + 1));
        indices.add((short) (offset + 1));
        indices.add((short) (offset + 3));
        indices.add((short) (offset + 2));
    }

    private void addSquareNormals(List<Float> normals, float normalX, float normalY, float normalZ)
    {
        for (int i = 0; i < 4; i++)
        {
            normals.add(normalX);
            normals.add(normalY);
            normals.add(normalZ);
        }
    }

    private void addTextureCoordinates(Chunk chunk, List<Vector2f> textureCoordinates, BlockSkin_TextureLocation textureLocation)
    {
        float xOffset = 14.0f / 32.0f;
        float topYOffset = 12 / 32.0f;

        Vector2f v1 = getTextureCoordinates(chunk, textureLocation, 0 + xOffset, 0);
        textureCoordinates.add(v1);
        Vector2f v2 = getTextureCoordinates(chunk, textureLocation, 1 - xOffset, 0);
        textureCoordinates.add(v2);
        Vector2f v3 = getTextureCoordinates(chunk, textureLocation, 0 + xOffset, 1 - topYOffset);
        textureCoordinates.add(v3);
        Vector2f v4 = getTextureCoordinates(chunk, textureLocation, 1 - xOffset, 1 - topYOffset);
        textureCoordinates.add(v4);
    }

    private void addTopTextureCoordinates(Chunk chunk, List<Vector2f> textureCoordinates, BlockSkin_TextureLocation textureLocation)
    {
        float xOffset = 14.0f / 32.0f;
        float topYOffset = 12 / 32.0f;
        float bottomYOffset = 16 / 32.0f;

        Vector2f v1 = getTextureCoordinates(chunk, textureLocation, 0 + xOffset, 0 + bottomYOffset);
        textureCoordinates.add(v1);
        Vector2f v2 = getTextureCoordinates(chunk, textureLocation, 1 - xOffset, 0 + bottomYOffset);
        textureCoordinates.add(v2);
        Vector2f v3 = getTextureCoordinates(chunk, textureLocation, 0 + xOffset, 1 - topYOffset);
        textureCoordinates.add(v3);
        Vector2f v4 = getTextureCoordinates(chunk, textureLocation, 1 - xOffset, 1 - topYOffset);
        textureCoordinates.add(v4);
    }

    @Override
    protected boolean shouldFaceBeAdded(Chunk chunk, Vector3Int blockLocation, Block.Face face)
    {
        return true;
    }
    
    @Override
    protected boolean canBeMerged(Block.Face face)
    {
        return false;
    }
}
