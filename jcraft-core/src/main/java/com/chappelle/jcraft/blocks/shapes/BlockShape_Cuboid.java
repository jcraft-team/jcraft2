package com.chappelle.jcraft.blocks.shapes;

import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.blocks.*;
import com.chappelle.jcraft.util.Vector3fPool;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.math.Vector3f;

import gnu.trove.list.*;

public class BlockShape_Cuboid extends BlockShape
{
	//{top, bottom, left, right, front, back}
	private float[] extents;

    public BlockShape_Cuboid(float[] extents)
    {
        this.extents = extents;
    }

    @Override
    public void addTo(MeshData meshData, Chunk chunk, Block block, Vector3Int blockLocation, boolean isTransparent){
    	
    	TFloatList positions = meshData.positionsList;
    	TShortList indices = meshData.indicesList;
    	TFloatList normals = meshData.normalsList;
    	TFloatList colors = meshData.colorList;
    	TFloatList textureCoordinates = meshData.textureCoordinatesList;
    	Vector3fPool pool = meshData.vec3Pool;
    	
        Vector3f blockLocation3f = new Vector3f(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());
        Vector3f faceLoc_Bottom_TopLeft = pool.add(blockLocation3f, (0.5f - extents[2]), (0.5f - extents[1]), (0.5f - extents[5]));
        Vector3f faceLoc_Bottom_TopRight = pool.add(blockLocation3f, (0.5f + extents[3]), (0.5f - extents[1]), (0.5f - extents[5]));
        Vector3f faceLoc_Bottom_BottomLeft = pool.add(blockLocation3f, (0.5f - extents[2]), (0.5f - extents[1]), (0.5f + extents[4]));
        Vector3f faceLoc_Bottom_BottomRight = pool.add(blockLocation3f, (0.5f + extents[3]), (0.5f - extents[1]), (0.5f + extents[4]));
        Vector3f faceLoc_Top_TopLeft = pool.add(blockLocation3f, (0.5f - extents[2]), (0.5f + extents[0]), (0.5f - extents[5]));
        Vector3f faceLoc_Top_TopRight = pool.add(blockLocation3f, (0.5f + extents[3]), (0.5f + extents[0]), (0.5f - extents[5]));
        Vector3f faceLoc_Top_BottomLeft = pool.add(blockLocation3f, (0.5f - extents[2]), (0.5f + extents[0]), (0.5f + extents[4]));
        Vector3f faceLoc_Top_BottomRight = pool.add(blockLocation3f, (0.5f + extents[3]), (0.5f + extents[0]), (0.5f + extents[4]));
//        Vector3f blockLocation3f = new Vector3f(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());
//        Vector3f faceLoc_Bottom_TopLeft = blockLocation3f.add((0.5f - extents[2]), (0.5f - extents[1]), (0.5f - extents[5]));
//        Vector3f faceLoc_Bottom_TopRight = blockLocation3f.add((0.5f + extents[3]), (0.5f - extents[1]), (0.5f - extents[5]));
//        Vector3f faceLoc_Bottom_BottomLeft = blockLocation3f.add((0.5f - extents[2]), (0.5f - extents[1]), (0.5f + extents[4]));
//        Vector3f faceLoc_Bottom_BottomRight = blockLocation3f.add((0.5f + extents[3]), (0.5f - extents[1]), (0.5f + extents[4]));
//        Vector3f faceLoc_Top_TopLeft = blockLocation3f.add((0.5f - extents[2]), (0.5f + extents[0]), (0.5f - extents[5]));
//        Vector3f faceLoc_Top_TopRight = blockLocation3f.add((0.5f + extents[3]), (0.5f + extents[0]), (0.5f - extents[5]));
//        Vector3f faceLoc_Top_BottomLeft = blockLocation3f.add((0.5f - extents[2]), (0.5f + extents[0]), (0.5f + extents[4]));
//        Vector3f faceLoc_Top_BottomRight = blockLocation3f.add((0.5f + extents[3]), (0.5f + extents[0]), (0.5f + extents[4]));
        if(shouldFaceBeAdded(chunk, blockLocation, Block.Face.Top, isTransparent)){
            addFaceIndices(indices, positions.size());
            addPositions(positions, faceLoc_Top_BottomLeft);
            addPositions(positions, faceLoc_Top_BottomRight);
            addPositions(positions, faceLoc_Top_TopLeft);
            addPositions(positions, faceLoc_Top_TopRight);
//            positions.add(faceLoc_Top_BottomLeft);
//            positions.add(faceLoc_Top_BottomRight);
//            positions.add(faceLoc_Top_TopLeft);
//            positions.add(faceLoc_Top_TopRight);
            addSquareNormals(normals, 0, 1, 0);
            addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Top).getTextureLocation());
            addLighting(colors, chunk, blockLocation, Block.Face.Top);
        }
        if(shouldFaceBeAdded(chunk, blockLocation, Block.Face.Bottom, isTransparent)){
            addFaceIndices(indices, positions.size());
            addPositions(positions, faceLoc_Bottom_BottomRight);
            addPositions(positions, faceLoc_Bottom_BottomLeft);
            addPositions(positions, faceLoc_Bottom_TopRight);
            addPositions(positions, faceLoc_Bottom_TopLeft);
            
//            positions.add(faceLoc_Bottom_BottomRight);
//            positions.add(faceLoc_Bottom_BottomLeft);
//            positions.add(faceLoc_Bottom_TopRight);
//            positions.add(faceLoc_Bottom_TopLeft);
            addSquareNormals(normals, 0, -1, 0);
            addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Bottom).getTextureLocation());
            addLighting(colors, chunk, blockLocation, Block.Face.Bottom);
        }
        if(shouldFaceBeAdded(chunk, blockLocation, Block.Face.Left, isTransparent)){
            addFaceIndices(indices, positions.size());
            addPositions(positions, faceLoc_Bottom_TopLeft);
            addPositions(positions, faceLoc_Bottom_BottomLeft);
            addPositions(positions, faceLoc_Top_TopLeft);
            addPositions(positions, faceLoc_Top_BottomLeft);

//            positions.add(faceLoc_Bottom_TopLeft);
//            positions.add(faceLoc_Bottom_BottomLeft);
//            positions.add(faceLoc_Top_TopLeft);
//            positions.add(faceLoc_Top_BottomLeft);
            addSquareNormals(normals, -1, 0, 0);
            addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Left).getTextureLocation());
            addLighting(colors, chunk, blockLocation, Block.Face.Left);
        }
        if(shouldFaceBeAdded(chunk, blockLocation, Block.Face.Right, isTransparent)){
            addFaceIndices(indices, positions.size());
            
            addPositions(positions, faceLoc_Bottom_BottomRight);
            addPositions(positions, faceLoc_Bottom_TopRight);
            addPositions(positions, faceLoc_Top_BottomRight);
            addPositions(positions, faceLoc_Top_TopRight);

//            positions.add(faceLoc_Bottom_BottomRight);
//            positions.add(faceLoc_Bottom_TopRight);
//            positions.add(faceLoc_Top_BottomRight);
//            positions.add(faceLoc_Top_TopRight);
            addSquareNormals(normals, 1, 0, 0);
            addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Right).getTextureLocation());
            addLighting(colors, chunk, blockLocation, Block.Face.Right);
        }
        if(shouldFaceBeAdded(chunk, blockLocation, Block.Face.Front, isTransparent)){
            addFaceIndices(indices, positions.size());
            addPositions(positions, faceLoc_Bottom_BottomLeft);
            addPositions(positions, faceLoc_Bottom_BottomRight);
            addPositions(positions, faceLoc_Top_BottomLeft);
            addPositions(positions, faceLoc_Top_BottomRight);
            
//            positions.add(faceLoc_Bottom_BottomLeft);
//            positions.add(faceLoc_Bottom_BottomRight);
//            positions.add(faceLoc_Top_BottomLeft);
//            positions.add(faceLoc_Top_BottomRight);
            addSquareNormals(normals, 0, 0, 1);
            addTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
            addLighting(colors, chunk, blockLocation, Block.Face.Front);
        }
        if(shouldFaceBeAdded(chunk, blockLocation, Block.Face.Back, isTransparent)){
            addFaceIndices(indices, positions.size());
            addPositions(positions, faceLoc_Bottom_TopRight);
            addPositions(positions, faceLoc_Bottom_TopLeft);
            addPositions(positions, faceLoc_Top_TopRight);
            addPositions(positions, faceLoc_Top_TopLeft);
            
//            positions.add(faceLoc_Bottom_TopRight);
//            positions.add(faceLoc_Bottom_TopLeft);
//            positions.add(faceLoc_Top_TopRight);
//            positions.add(faceLoc_Top_TopLeft);
            addSquareNormals(normals, 0, 0, -1);
            addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Back).getTextureLocation());
            addLighting(colors, chunk, blockLocation, Block.Face.Back);
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