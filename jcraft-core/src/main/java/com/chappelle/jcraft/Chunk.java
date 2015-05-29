package com.chappelle.jcraft;

import java.io.IOException;

import com.chappelle.jcraft.lighting.LightMap;
import com.chappelle.jcraft.lighting.LightType;
import com.chappelle.jcraft.network.BitInputStream;
import com.chappelle.jcraft.network.BitOutputStream;
import com.chappelle.jcraft.network.BitSerializable;

public class Chunk implements BitSerializable
{
    public Vector3Int location = new Vector3Int();
    private Vector3Int blockLocation = new Vector3Int();
    private byte[][][] blockTypes;
    private boolean[][][] blocks_IsOnSurface;
    private BlockState[][][] blockState;
    private LightMap lights;
    public boolean needsMeshUpdate;
    public World world;
    
    public Chunk(World world, int x, int y, int z)
    {
    	this.world = world;
    	location.set(x, y, z);
    	blockLocation.set(location.mult(world.getSettings().getChunkSizeX(), world.getSettings().getChunkSizeY(), world.getSettings().getChunkSizeZ()));
    	blockTypes = new byte[world.getSettings().getChunkSizeX()][world.getSettings().getChunkSizeY()][world.getSettings().getChunkSizeZ()];
    	blocks_IsOnSurface = new boolean[world.getSettings().getChunkSizeX()][world.getSettings().getChunkSizeY()][world.getSettings().getChunkSizeZ()];
    	blockState = new BlockState[world.getSettings().getChunkSizeX()][world.getSettings().getChunkSizeY()][world.getSettings().getChunkSizeZ()];
    	lights = new LightMap(new Vector3Int(world.getSettings().getChunkSizeX(), world.getSettings().getChunkSizeY(), world.getSettings().getChunkSizeZ()), location);
    }
    
    public Vector3Int getBlockLocation()
    {
        return blockLocation;
    }

    public void markDirty()
    {
    	this.needsMeshUpdate = true;
    }

    public void setLight(int x, int y, int z, LightType type, int lightVal)
    {
    	lights.setLight(x, y, z, type, lightVal);
    	needsMeshUpdate = true;
    }

    public int getLight(int x, int y, int z, LightType type)
    {
    	return lights.getLight(x, y, z, type);
    }
    
    public LightMap getLights()
    {
    	return lights;
    }

    public Object getBlockStateValue(Vector3Int location, Short key)
    {
        BlockState state = getBlockState(location);
        if(state == null)
        {
            return null;
        }
        else
        {
            return state.get(key);
        }
    }

    public BlockState getBlockState(Vector3Int location)
    {
        BlockState state = blockState[location.getX()][location.getY()][location.getZ()];
        if(state == null)
        {
            state = new BlockState(this);
            blockState[location.getX()][location.getY()][location.getZ()] = state;
        }
        return state;
    }

    public Block getNeighborBlock_Global(Vector3Int location, Block.Face face){
        return world.getBlock(getNeighborBlockGlobalLocation(location, face));
    }

    public Block getBlock(Vector3Int location){
        if(isValidBlockLocation(location)){
            byte blockType = blockTypes[location.getX()][location.getY()][location.getZ()];
            return BlockManager.getBlock(blockType);
        }
        return null;
    }

    public boolean isBlockOnSurface(Vector3Int location)
    {
        return blocks_IsOnSurface[location.getX()][location.getY()][location.getZ()];
    }

    private Vector3Int getNeighborBlockGlobalLocation(Vector3Int location, Block.Face face){
        Vector3Int neighborLocation = BlockNavigator.getNeighborBlockLocalLocation(location, face);
        neighborLocation.addLocal(blockLocation);
        return neighborLocation;
    }

    public void setBlock(Vector3Int location, Block block){
        if(isValidBlockLocation(location)){
            byte blockType = BlockManager.getType(block);
            blockTypes[location.getX()][location.getY()][location.getZ()] = blockType;
            updateBlockState(location);
            needsMeshUpdate = true;
        }
    }

    private void updateBlockState(Vector3Int location){
        updateBlockInformation(location);
        for(int i=0;i<Block.Face.values().length;i++){
            Vector3Int neighborLocation = getNeighborBlockGlobalLocation(location, Block.Face.values()[i]);
            Chunk chunk = world.getChunk(neighborLocation);
            if(chunk != null){
                chunk.updateBlockInformation(neighborLocation.subtract(chunk.getBlockLocation()));
            }
        }
    }
    
    private void updateBlockInformation(Vector3Int location){
        Block neighborBlock_Top = world.getBlock(getNeighborBlockGlobalLocation(location, Block.Face.Top));
        blocks_IsOnSurface[location.getX()][location.getY()][location.getZ()] = (neighborBlock_Top == null || !neighborBlock_Top.smothersBottomBlock());
    }

    private boolean isValidBlockLocation(Vector3Int location){
        return Util.isValidIndex(blockTypes, location);
    }

    public void setBlockState(Vector3Int location, Short key, Object value)
    {
        Block block = getBlock(location);
        if(block != null)
        {
            BlockState state = getBlockState(location);
            state.put(key, value);
        }
    }
    
    public Block getNeighborBlock_Local(Vector3Int location, Block.Face face){
        Vector3Int neighborLocation = BlockNavigator.getNeighborBlockLocalLocation(location, face);
        return getBlock(neighborLocation);
    }
    
    public void removeBlock(Vector3Int location){
        if(isValidBlockLocation(location)){
            blockTypes[location.getX()][location.getY()][location.getZ()] = 0;
            updateBlockState(location);
            needsMeshUpdate = true;
        }
    }
    
    @Override
    public void write(BitOutputStream outputStream){
        for(int x=0;x<blockTypes.length;x++){
            for(int y=0;y<blockTypes[0].length;y++){
                for(int z=0;z<blockTypes[0][0].length;z++){
                    outputStream.writeBits(blockTypes[x][y][z], 8);
                }
            }
        }
    }

    @Override
    public void read(BitInputStream inputStream) throws IOException{
        for(int x=0;x<blockTypes.length;x++){
            for(int y=0;y<blockTypes[0].length;y++){
                for(int z=0;z<blockTypes[0][0].length;z++){
                    blockTypes[x][y][z] = (byte) inputStream.readBits(8);
                }
            }
        }
        Vector3Int tmpLocation = new Vector3Int();
        for(int x=0;x<blockTypes.length;x++){
            for(int y=0;y<blockTypes[0].length;y++){
                for(int z=0;z<blockTypes[0][0].length;z++){
                    tmpLocation.set(x, y, z);
                    updateBlockInformation(tmpLocation);
                }
            }
        }
        needsMeshUpdate = true;
    }
}
