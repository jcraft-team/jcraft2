package com.chappelle.jcraft.blocks.shapes;

import com.chappelle.jcraft.blocks.*;
import com.chappelle.jcraft.util.math.Vector3Int;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.math.Vector3f;

import gnu.trove.list.*;

public class BlockShape_Pyramid extends BlockShape{

	public BlockShape_Pyramid()
	{
		for(Face face : Face.values())
		{
			fullSide.put(face, Boolean.FALSE);
		}
		fullSide.put(Face.Bottom, Boolean.TRUE);
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

        Vector3f blockLocation3f = new Vector3f(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());
        Vector3f faceLoc_Bottom_TopLeft = blockLocation3f.add(new Vector3f(0, 0, 0));
        Vector3f faceLoc_Bottom_TopRight = blockLocation3f.add(new Vector3f(1, 0, 0));
        Vector3f faceLoc_Bottom_BottomLeft = blockLocation3f.add(new Vector3f(0, 0, 1));
        Vector3f faceLoc_Bottom_BottomRight = blockLocation3f.add(new Vector3f(1, 0, 1));
        Vector3f faceLoc_Top = blockLocation3f.add(new Vector3f(0.5f, 1, 0.5f));
        int indexOffset = positions.size()/3;
        if(shouldFaceBeAdded(gen, Face.Bottom, isTransparent)){
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
            TextureLocation textureLocationBottom = block.getSkin(chunk, blockLocation, Face.Bottom).getTextureLocation();
            textureCoordinates.add(getTextureCoordinatesX(gen, textureLocationBottom, 0, 0));
            textureCoordinates.add(getTextureCoordinatesY(gen, textureLocationBottom, 0, 0));
            
            textureCoordinates.add(getTextureCoordinatesX(gen, textureLocationBottom, 1, 0));
            textureCoordinates.add(getTextureCoordinatesY(gen, textureLocationBottom, 1, 0));
            
            textureCoordinates.add(getTextureCoordinatesX(gen, textureLocationBottom, 0, 1));
            textureCoordinates.add(getTextureCoordinatesY(gen, textureLocationBottom, 0, 1));
            
            textureCoordinates.add(getTextureCoordinatesX(gen, textureLocationBottom, 1, 1));
            textureCoordinates.add(getTextureCoordinatesY(gen, textureLocationBottom, 1, 1));
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
        addTextureCoordinates_Side(gen, textureCoordinates, block, chunk, blockLocation, Face.Front);
        //Left
        addPositions(positions, faceLoc_Bottom_TopLeft);
        addPositions(positions, faceLoc_Bottom_BottomLeft);
        addPositions(positions, faceLoc_Top);
        indices.add((short) (indexOffset + 0));
        indices.add((short) (indexOffset + 1));
        indices.add((short) (indexOffset + 2));
        indexOffset += 3;
        addTriangleNormals(normals, -1, 0, 0);
        addTextureCoordinates_Side(gen, textureCoordinates, block, chunk, blockLocation, Face.Left);
        //Back
        addPositions(positions, faceLoc_Bottom_TopRight);
        addPositions(positions, faceLoc_Bottom_TopLeft);
        addPositions(positions, faceLoc_Top);
        indices.add((short) (indexOffset + 0));
        indices.add((short) (indexOffset + 1));
        indices.add((short) (indexOffset + 2));
        indexOffset += 3;
        addTriangleNormals(normals, 0, 0, -1);
        addTextureCoordinates_Side(gen, textureCoordinates, block, chunk, blockLocation, Face.Back);
        //Right
        addPositions(positions, faceLoc_Bottom_BottomRight);
        addPositions(positions, faceLoc_Bottom_TopRight);
        addPositions(positions, faceLoc_Top);
        indices.add((short) (indexOffset + 0));
        indices.add((short) (indexOffset + 1));
        indices.add((short) (indexOffset + 2));
        indexOffset += 3;
        addTriangleNormals(normals, 1, 0, 0);
        addTextureCoordinates_Side(gen, textureCoordinates, block, chunk, blockLocation, Face.Right);
    }
    
    private void addTriangleNormals(TFloatList normals, float x, float y, float z){
        for(int i=0;i<3;i++){
            normals.add(x);
            normals.add(y);
            normals.add(z);
        }
    }
    
    private void addTextureCoordinates_Side(MeshGenContext gen, TFloatList textureCoordinates, Block block, Chunk chunk, Vector3Int blockLocation, Face face){
        TextureLocation textureLocation = block.getSkin(chunk, blockLocation, face).getTextureLocation();
        textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 0, 0));
        textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 0, 0));
        
        textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 1, 0));
        textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 1, 0));
        
        textureCoordinates.add(getTextureCoordinatesX(gen, textureLocation, 0.5f, 1));
        textureCoordinates.add(getTextureCoordinatesY(gen, textureLocation, 0.5f, 1));
    }
}
