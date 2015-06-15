package com.chappelle.jcraft.blocks.shapes;

import java.util.List;

import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.blocks.BlockShape;
import com.chappelle.jcraft.blocks.BlockSkin_TextureLocation;
import com.chappelle.jcraft.blocks.MeshData;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class BlockShape_Pyramid extends BlockShape{

    @Override
    public void addTo(MeshData meshData, Chunk chunk, Block block, Vector3Int blockLocation, boolean isTransparent)
    {
    	List<Vector3f> positions = meshData.positionsList;
    	List<Short> indices = meshData.indicesList;
    	List<Float> normals = meshData.normalsList;
    	List<Vector2f> textureCoordinates = meshData.textureCoordinatesList;

        Vector3f blockLocation3f = new Vector3f(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());
        Vector3f faceLoc_Bottom_TopLeft = blockLocation3f.add(new Vector3f(0, 0, 0));
        Vector3f faceLoc_Bottom_TopRight = blockLocation3f.add(new Vector3f(1, 0, 0));
        Vector3f faceLoc_Bottom_BottomLeft = blockLocation3f.add(new Vector3f(0, 0, 1));
        Vector3f faceLoc_Bottom_BottomRight = blockLocation3f.add(new Vector3f(1, 0, 1));
        Vector3f faceLoc_Top = blockLocation3f.add(new Vector3f(0.5f, 1, 0.5f));
        int indexOffset = positions.size();
        if(shouldFaceBeAdded(chunk, blockLocation, Block.Face.Bottom, isTransparent)){
            positions.add(faceLoc_Bottom_BottomRight);
            positions.add(faceLoc_Bottom_BottomLeft);
            positions.add(faceLoc_Bottom_TopRight);
            positions.add(faceLoc_Bottom_TopLeft);
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
            textureCoordinates.add(getTextureCoordinates(chunk, textureLocationBottom, 0, 0));
            textureCoordinates.add(getTextureCoordinates(chunk, textureLocationBottom, 1, 0));
            textureCoordinates.add(getTextureCoordinates(chunk, textureLocationBottom, 0, 1));
            textureCoordinates.add(getTextureCoordinates(chunk, textureLocationBottom, 1, 1));
        }
        //Front
        positions.add(faceLoc_Bottom_BottomLeft);
        positions.add(faceLoc_Bottom_BottomRight);
        positions.add(faceLoc_Top);
        indices.add((short) (indexOffset + 0));
        indices.add((short) (indexOffset + 1));
        indices.add((short) (indexOffset + 2));
        indexOffset += 3;
        addTriangleNormals(normals, 0, 0, 1);
        addTextureCoordinates_Side(textureCoordinates, block, chunk, blockLocation, Block.Face.Front);
        //Left
        positions.add(faceLoc_Bottom_TopLeft);
        positions.add(faceLoc_Bottom_BottomLeft);
        positions.add(faceLoc_Top);
        indices.add((short) (indexOffset + 0));
        indices.add((short) (indexOffset + 1));
        indices.add((short) (indexOffset + 2));
        indexOffset += 3;
        addTriangleNormals(normals, -1, 0, 0);
        addTextureCoordinates_Side(textureCoordinates, block, chunk, blockLocation, Block.Face.Left);
        //Back
        positions.add(faceLoc_Bottom_TopRight);
        positions.add(faceLoc_Bottom_TopLeft);
        positions.add(faceLoc_Top);
        indices.add((short) (indexOffset + 0));
        indices.add((short) (indexOffset + 1));
        indices.add((short) (indexOffset + 2));
        indexOffset += 3;
        addTriangleNormals(normals, 0, 0, -1);
        addTextureCoordinates_Side(textureCoordinates, block, chunk, blockLocation, Block.Face.Back);
        //Right
        positions.add(faceLoc_Bottom_BottomRight);
        positions.add(faceLoc_Bottom_TopRight);
        positions.add(faceLoc_Top);
        indices.add((short) (indexOffset + 0));
        indices.add((short) (indexOffset + 1));
        indices.add((short) (indexOffset + 2));
        indexOffset += 3;
        addTriangleNormals(normals, 1, 0, 0);
        addTextureCoordinates_Side(textureCoordinates, block, chunk, blockLocation, Block.Face.Right);
    }
    
    private void addTriangleNormals(List<Float> normals, float x, float y, float z){
        for(int i=0;i<3;i++){
            normals.add(x);
            normals.add(y);
            normals.add(z);
        }
    }
    
    private void addTextureCoordinates_Side(List<Vector2f> textureCoordinates, Block block, Chunk chunk, Vector3Int blockLocation, Block.Face face){
        BlockSkin_TextureLocation textureLocation = block.getSkin(chunk, blockLocation, face).getTextureLocation();
        textureCoordinates.add(getTextureCoordinates(chunk, textureLocation, 0, 0));
        textureCoordinates.add(getTextureCoordinates(chunk, textureLocation, 1, 0));
        textureCoordinates.add(getTextureCoordinates(chunk, textureLocation, 0.5f, 1));
    }

    @Override
    protected boolean canBeMerged(Block.Face face){
        return (face == Block.Face.Bottom);
    }
}
