package com.chappelle.jcraft.blocks.shapes;

import com.chappelle.jcraft.blocks.*;
import com.chappelle.jcraft.util.math.*;
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
    public void addTo(MeshGenContext gen, boolean isTransparent){
    	
    	TFloatList positions = gen.getPositions();
    	TShortList indices = gen.getIndices();
    	TFloatList normals = gen.getNormals();
    	TFloatList textureCoordinates = gen.getTextureCoordinates();
    	Vector3fPool pool = gen.getVector3fPool();
    	Block block = gen.getBlock();
    	Chunk chunk = gen.getChunk();
    	Vector3Int blockLocation = gen.getLocation();
    	
        Vector3f blockLocation3f = new Vector3f(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());
        Vector3f faceLoc_Bottom_TopLeft = pool.add(blockLocation3f, (0.5f - extents[2]), (0.5f - extents[1]), (0.5f - extents[5]));
        Vector3f faceLoc_Bottom_TopRight = pool.add(blockLocation3f, (0.5f + extents[3]), (0.5f - extents[1]), (0.5f - extents[5]));
        Vector3f faceLoc_Bottom_BottomLeft = pool.add(blockLocation3f, (0.5f - extents[2]), (0.5f - extents[1]), (0.5f + extents[4]));
        Vector3f faceLoc_Bottom_BottomRight = pool.add(blockLocation3f, (0.5f + extents[3]), (0.5f - extents[1]), (0.5f + extents[4]));
        Vector3f faceLoc_Top_TopLeft = pool.add(blockLocation3f, (0.5f - extents[2]), (0.5f + extents[0]), (0.5f - extents[5]));
        Vector3f faceLoc_Top_TopRight = pool.add(blockLocation3f, (0.5f + extents[3]), (0.5f + extents[0]), (0.5f - extents[5]));
        Vector3f faceLoc_Top_BottomLeft = pool.add(blockLocation3f, (0.5f - extents[2]), (0.5f + extents[0]), (0.5f + extents[4]));
        Vector3f faceLoc_Top_BottomRight = pool.add(blockLocation3f, (0.5f + extents[3]), (0.5f + extents[0]), (0.5f + extents[4]));
        if(shouldFaceBeAdded(gen, Face.Top, isTransparent)){
            addFaceIndices(indices, positions.size());
            addPositions(positions, faceLoc_Top_BottomLeft);
            addPositions(positions, faceLoc_Top_BottomRight);
            addPositions(positions, faceLoc_Top_TopLeft);
            addPositions(positions, faceLoc_Top_TopRight);
            addSquareNormals(normals, 0, 1, 0);
            addTextureCoordinates(gen, textureCoordinates, block.getSkin(chunk, blockLocation, Face.Top).getTextureLocation());
            addLighting(gen, Face.Top);
        }
        if(shouldFaceBeAdded(gen, Face.Bottom, isTransparent)){
            addFaceIndices(indices, positions.size());
            addPositions(positions, faceLoc_Bottom_BottomRight);
            addPositions(positions, faceLoc_Bottom_BottomLeft);
            addPositions(positions, faceLoc_Bottom_TopRight);
            addPositions(positions, faceLoc_Bottom_TopLeft);
            
            addSquareNormals(normals, 0, -1, 0);
            addTextureCoordinates(gen, textureCoordinates, block.getSkin(chunk, blockLocation, Face.Bottom).getTextureLocation());
            addLighting(gen, Face.Bottom);
        }
        if(shouldFaceBeAdded(gen, Face.Left, isTransparent)){
            addFaceIndices(indices, positions.size());
            addPositions(positions, faceLoc_Bottom_TopLeft);
            addPositions(positions, faceLoc_Bottom_BottomLeft);
            addPositions(positions, faceLoc_Top_TopLeft);
            addPositions(positions, faceLoc_Top_BottomLeft);

            addSquareNormals(normals, -1, 0, 0);
            addTextureCoordinates(gen, textureCoordinates, block.getSkin(chunk, blockLocation, Face.Left).getTextureLocation());
            addLighting(gen, Face.Left);
        }
        if(shouldFaceBeAdded(gen, Face.Right, isTransparent)){
            addFaceIndices(indices, positions.size());
            
            addPositions(positions, faceLoc_Bottom_BottomRight);
            addPositions(positions, faceLoc_Bottom_TopRight);
            addPositions(positions, faceLoc_Top_BottomRight);
            addPositions(positions, faceLoc_Top_TopRight);

            addSquareNormals(normals, 1, 0, 0);
            addTextureCoordinates(gen, textureCoordinates, block.getSkin(chunk, blockLocation, Face.Right).getTextureLocation());
            addLighting(gen, Face.Right);
        }
        if(shouldFaceBeAdded(gen, Face.Front, isTransparent)){
            addFaceIndices(indices, positions.size());
            addPositions(positions, faceLoc_Bottom_BottomLeft);
            addPositions(positions, faceLoc_Bottom_BottomRight);
            addPositions(positions, faceLoc_Top_BottomLeft);
            addPositions(positions, faceLoc_Top_BottomRight);
            
            addSquareNormals(normals, 0, 0, 1);
            addTextureCoordinates(gen, textureCoordinates,block.getSkin(chunk, blockLocation, Face.Front).getTextureLocation());
            addLighting(gen, Face.Front);
        }
        if(shouldFaceBeAdded(gen, Face.Back, isTransparent)){
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