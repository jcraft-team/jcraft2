package com.chappelle.jcraft.blocks.shapes;

import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.blocks.*;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.math.Vector3f;

import gnu.trove.list.*;

public class BlockShape_Pyramid extends BlockShape{

	public BlockShape_Pyramid()
	{
		for(Block.Face face : Block.Face.values())
		{
			fullSide.put(face, Boolean.FALSE);
		}
		fullSide.put(Block.Face.Bottom, Boolean.TRUE);
	}

    @Override
    public void addTo(MeshData meshData, Chunk chunk, Block block, Vector3Int blockLocation, boolean isTransparent)
    {
    	TFloatList positions = meshData.positionsList;
    	TShortList indices = meshData.indicesList;
    	TFloatList normals = meshData.normalsList;
    	TFloatList textureCoordinates = meshData.textureCoordinatesList;

        Vector3f blockLocation3f = new Vector3f(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());
        Vector3f faceLoc_Bottom_TopLeft = blockLocation3f.add(new Vector3f(0, 0, 0));
        Vector3f faceLoc_Bottom_TopRight = blockLocation3f.add(new Vector3f(1, 0, 0));
        Vector3f faceLoc_Bottom_BottomLeft = blockLocation3f.add(new Vector3f(0, 0, 1));
        Vector3f faceLoc_Bottom_BottomRight = blockLocation3f.add(new Vector3f(1, 0, 1));
        Vector3f faceLoc_Top = blockLocation3f.add(new Vector3f(0.5f, 1, 0.5f));
        int indexOffset = positions.size()/3;
        if(shouldFaceBeAdded(chunk, blockLocation, Block.Face.Bottom, isTransparent)){
        	addPositions(positions, faceLoc_Bottom_BottomRight);
        	addPositions(positions, faceLoc_Bottom_BottomLeft);
        	addPositions(positions, faceLoc_Bottom_TopRight);
        	addPositions(positions, faceLoc_Bottom_TopLeft);
            indices.add((short) (indexOffset + 2));
            indices.add((short) (indexOffset + 0));
            indices.add((short) (indexOffset + 1));
            indices.add((short) (indexOffset + 1));
            indices.add((short) (indexOffset + 3));
            indices.add((short) (indexOffset + 2));
            indexOffset += 4;
            for(int i=0;i<4;i++){
                normals.add(0f);
                normals.add(-1f);
                normals.add(0f);
            }
            BlockSkin_TextureLocation textureLocationBottom = block.getSkin(chunk, blockLocation, Block.Face.Bottom).getTextureLocation();
            textureCoordinates.add(getTextureCoordinatesX(chunk, textureLocationBottom, 0, 0));
            textureCoordinates.add(getTextureCoordinatesY(chunk, textureLocationBottom, 0, 0));
            
            textureCoordinates.add(getTextureCoordinatesX(chunk, textureLocationBottom, 1, 0));
            textureCoordinates.add(getTextureCoordinatesY(chunk, textureLocationBottom, 1, 0));
            
            textureCoordinates.add(getTextureCoordinatesX(chunk, textureLocationBottom, 0, 1));
            textureCoordinates.add(getTextureCoordinatesY(chunk, textureLocationBottom, 0, 1));
            
            textureCoordinates.add(getTextureCoordinatesX(chunk, textureLocationBottom, 1, 1));
            textureCoordinates.add(getTextureCoordinatesY(chunk, textureLocationBottom, 1, 1));
        }
        //Front
        addPositions(positions, faceLoc_Bottom_BottomLeft);
        addPositions(positions, faceLoc_Bottom_BottomRight);
        addPositions(positions, faceLoc_Top);
        indices.add((short) (indexOffset + 0));
        indices.add((short) (indexOffset + 1));
        indices.add((short) (indexOffset + 2));
        indexOffset += 3;
        addTriangleNormals(normals, 0, 0, 1);
        addTextureCoordinates_Side(textureCoordinates, block, chunk, blockLocation, Block.Face.Front);
        //Left
        addPositions(positions, faceLoc_Bottom_TopLeft);
        addPositions(positions, faceLoc_Bottom_BottomLeft);
        addPositions(positions, faceLoc_Top);
        indices.add((short) (indexOffset + 0));
        indices.add((short) (indexOffset + 1));
        indices.add((short) (indexOffset + 2));
        indexOffset += 3;
        addTriangleNormals(normals, -1, 0, 0);
        addTextureCoordinates_Side(textureCoordinates, block, chunk, blockLocation, Block.Face.Left);
        //Back
        addPositions(positions, faceLoc_Bottom_TopRight);
        addPositions(positions, faceLoc_Bottom_TopLeft);
        addPositions(positions, faceLoc_Top);
        indices.add((short) (indexOffset + 0));
        indices.add((short) (indexOffset + 1));
        indices.add((short) (indexOffset + 2));
        indexOffset += 3;
        addTriangleNormals(normals, 0, 0, -1);
        addTextureCoordinates_Side(textureCoordinates, block, chunk, blockLocation, Block.Face.Back);
        //Right
        addPositions(positions, faceLoc_Bottom_BottomRight);
        addPositions(positions, faceLoc_Bottom_TopRight);
        addPositions(positions, faceLoc_Top);
        indices.add((short) (indexOffset + 0));
        indices.add((short) (indexOffset + 1));
        indices.add((short) (indexOffset + 2));
        indexOffset += 3;
        addTriangleNormals(normals, 1, 0, 0);
        addTextureCoordinates_Side(textureCoordinates, block, chunk, blockLocation, Block.Face.Right);
    }
    
    private void addTriangleNormals(TFloatList normals, float x, float y, float z){
        for(int i=0;i<3;i++){
            normals.add(x);
            normals.add(y);
            normals.add(z);
        }
    }
    
    private void addTextureCoordinates_Side(TFloatList textureCoordinates, Block block, Chunk chunk, Vector3Int blockLocation, Block.Face face){
        BlockSkin_TextureLocation textureLocation = block.getSkin(chunk, blockLocation, face).getTextureLocation();
        textureCoordinates.add(getTextureCoordinatesX(chunk, textureLocation, 0, 0));
        textureCoordinates.add(getTextureCoordinatesY(chunk, textureLocation, 0, 0));
        
        textureCoordinates.add(getTextureCoordinatesX(chunk, textureLocation, 1, 0));
        textureCoordinates.add(getTextureCoordinatesY(chunk, textureLocation, 1, 0));
        
        textureCoordinates.add(getTextureCoordinatesX(chunk, textureLocation, 0.5f, 1));
        textureCoordinates.add(getTextureCoordinatesY(chunk, textureLocation, 0.5f, 1));
    }
}
