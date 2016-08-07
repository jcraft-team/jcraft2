package com.chappelle.jcraft.world.chunk;

import java.io.IOException;
import java.util.*;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.lighting.*;
import com.chappelle.jcraft.serialization.*;
import com.chappelle.jcraft.util.*;
import com.chappelle.jcraft.world.World;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.*;

public class Chunk implements BitSerializable
{
    public Vector3Int location = new Vector3Int();
    public Vector3Int blockLocation = new Vector3Int();
    private byte[][][] blockTypes;
    private int[][] heightMap;
    private BlockState[][][] blockState;
    private LightMap lights;
    private boolean needsMeshUpdate = true;
    public World world;
    private NibbleArray metadata;
    public boolean isLoaded;
    public boolean updateInProgress;

	private Geometry optimizedGeometry_Opaque; 
	private Geometry optimizedGeometry_Transparent; 
	private Node node = new Node();
	public Long id;
	public LightManager lightMgr;
	public boolean isLightUpdating;
	
	public Chunk(World world, int x, int z)
	{
		this(world, x, z, new byte[16][256][16]);
	}
	
    public Chunk(World world, int x, int z, byte[][][] blockTypes)
    {
    	this(world, x, z, blockTypes, new byte[256][16][16]);
    }
    
    public Chunk(World world, int x, int z, byte[][][] blockTypes, byte[][][] light)
    {
    	this.world = world;
    	location.set(x, 0, z);
    	blockLocation.set(location.mult(16, 256, 16));
    	this.blockTypes = blockTypes;
    	lights = new LightMap();
    	this.metadata = new NibbleArray(16*256*16);
    	node.setLocalTranslation(new Vector3f(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ()));
    	this.id = ChunkCoordIntPair.chunkXZ2Int(x, z); 
    	this.lightMgr = new FloodFillLightManager(this);
    	this.heightMap = makeHeightMap(blockTypes);
    }
    
    public int[][] getHeightMap()
    {
    	return heightMap;
    }
    
	public void setBlock(int x, int y, int z, Block block)
    {
    	if(heightMap[x][z] < y)
    	{
    		heightMap[x][z] = y;
    	}
    	blockTypes[x][y][z] = block.blockId;
    	markDirty();
    }
    
	public Block getBlock(Vector3Int location)
	{
		return getBlock(location.x, location.y, location.z);
	}
	
    public Block getBlock(int x, int y, int z)
    {
        if(isValidBlockLocation(x, y, z))
        {
            int blockType = blockTypes[x][y][z];
            return Block.blocksList[blockType];
        }
        return null;
    }
    
    public void removeBlock(int x, int y, int z)
    {
    	if(heightMap[x][z] == y)
    	{
    		heightMap[x][z] = findHeight(x, z, y);
    	}
    	blockTypes[x][y][z] = 0;
    	markDirty();
    }

    private int[][] makeHeightMap(byte[][][] blockTypes)
	{
    	int[][] heightMap = new int[16][16];
    	for(int x = 0; x < 16; x++)
    	{
    		for(int z = 0; z < 16; z++)
    		{
    			heightMap[x][z] = findHeight(x, z);
    		}
    	}
    	return heightMap;
	}


    private int findHeight(int x, int z)
    {
    	return findHeight(x, z, 255);
    }
    
    private int findHeight(int x, int z, int startingHeight)
    {
		for(int y = startingHeight; y >= 0; y--)
		{
			Block block = getBlock(x, y, z);
			if(block != null && !block.isTransparent)
			{
				return y;
			}
		}
		return 0;
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

    public BlockState getBlockState(Vector3Int location)
    {
        BlockState state = getBlockState(location.x, location.y, location.z);
        if(state == null)
        {
            state = new BlockState(this);
            blockState[location.getX()][location.getY()][location.getZ()] = state;
        }
        return state;
    }
    
    
    private BlockState getBlockState(int x, int y, int z)
    {
    	if(blockState == null)
    	{
    		blockState = new BlockState[16][256][16];
    	}
    	return blockState[x][y][z];
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

    public void addToScene(Node parent)
    {
    	parent.attachChild(node);
    }
    
    public void removeFromScene()
    {
    	node.getParent().detachChild(node);
    }

    /**
     * Updates the mesh. NEVER CALL THIS FROM A THREAD OTHER THAN THE MAIN THREAD!
     * @param opaqueMesh
     * @param transparentMesh
     */
    public void setMesh(Mesh opaqueMesh, Mesh transparentMesh)
    {
		if(optimizedGeometry_Opaque == null)
		{
			optimizedGeometry_Opaque = new Geometry("");
			optimizedGeometry_Opaque.setQueueBucket(Bucket.Opaque);
			node.attachChild(optimizedGeometry_Opaque);
			optimizedGeometry_Opaque.setMaterial(world.getBlockMaterial());
		}
		if(optimizedGeometry_Transparent == null)
		{
			optimizedGeometry_Transparent = new Geometry("");
			optimizedGeometry_Transparent.setQueueBucket(Bucket.Transparent);
			node.attachChild(optimizedGeometry_Transparent);
			optimizedGeometry_Transparent.setMaterial(world.getBlockMaterial());
		}
		optimizedGeometry_Opaque.setMesh(opaqueMesh);
		optimizedGeometry_Transparent.setMesh(transparentMesh);
		isLoaded = true;
		needsMeshUpdate = false;
		updateInProgress = false;
    }
    
	public void setDayNightLighting(float dayNightLighting)
	{
		if(optimizedGeometry_Opaque != null && optimizedGeometry_Transparent != null)
		{
			this.optimizedGeometry_Opaque.getMaterial().setFloat("dayNightLighting", dayNightLighting);
			this.optimizedGeometry_Transparent.getMaterial().setFloat("dayNightLighting", dayNightLighting);
		}
	}
	

    public byte getMetadata(int x, int y, int z)
    {
    	int index = y + (z * 256) + (x * 16 * 16);
    	return metadata.get(index);
    }

    public void setMetadata(int x, int y, int z, byte value)
    {
    	int index = y + (z * 256) + (x * 16 * 16);
    	metadata.set(index, value);
    	markDirty();
    }
    
    public Vector3Int getBlockLocation()
    {
        return blockLocation;
    }

    public boolean isDirty()
    {
    	return needsMeshUpdate;
    }
    
    public void markDirty()
    {
    	this.needsMeshUpdate = true;
    }

    public void setLight(int x, int y, int z, LightType type, int lightVal)
    {
    	lights.setLight(x, y, z, type, lightVal);
    	markDirty();
    }

    public int getLight(int x, int y, int z, LightType type)
    {
    	return lights.getLight(x, y, z, type);
    }
    
    public LightMap getLights()
    {
    	return lights;
    }

    public Block getNeighborBlock_Global(Vector3Int location, Block.Face face)
    {
        return world.getBlock(getNeighborBlockGlobalLocation(location, face));
    }

    public Chunk getChunkNeighbor(Direction dir)
    {
    	return world.getChunkNeighbor(this, dir);
    }
    
    /**
     * Returns the chunk neighbors with a radius of r excluding the current chunk. This is a Moore Neighborhood as in http://mathworld.wolfram.com/MooreNeighborhood.html.
     * The number of chunks in the grid will be (2*r+1)^2. So if r=10, then then it will return 441 chunks.
     * @param r The radius
     * @return A list of neighboring Chunks with a radius of r
     */
    public List<Chunk> getChunkNeighborhood(int r)
    {
    	List<Chunk> result = new ArrayList<Chunk>();
		//Iterates starting in top left corner, then down, then across to the right
		int x = location.x - r;
		int z = location.z + r;
		int gridWidth = r*2 + 1;
		for(int xMod = 0; xMod < gridWidth; xMod++)
		{
			for(int zMod = 0; zMod < gridWidth; zMod++)
			{
				int chunkX = x+xMod;
				int chunkZ = z-zMod;
				if(!(chunkX == location.x && chunkZ == location.z))//Exclude this chunk from result
				{
					Chunk chunk = world.getChunkFromChunkCoordinates(chunkX, chunkZ);
					if(chunk != null)
					{
						result.add(chunk);
					}
				}
			}
		}
    	return result;
    }

    public List<Vector3Int> getMissingChunkNeighborhoodLocations(int r)
    {
    	List<Vector3Int> result = new ArrayList<Vector3Int>();
    	//Iterates starting in top left corner, then down, then across to the right
    	int x = location.x - r;
    	int z = location.z + r;
    	int gridWidth = r*2 + 1;
    	for(int xMod = 0; xMod < gridWidth; xMod++)
    	{
    		for(int zMod = 0; zMod < gridWidth; zMod++)
    		{
    			int chunkX = x+xMod;
    			int chunkZ = z-zMod;
    			if(!(chunkX == location.x && chunkZ == location.z))//Exclude this chunk from result
    			{
    				Chunk chunk = world.getChunkFromChunkCoordinates(chunkX, chunkZ);
    				if(chunk == null)
    				{
    					result.add(new Vector3Int(chunkX, 0, chunkZ));
    				}
    			}
    		}
    	}
    	return result;
    }
    
    public boolean isBlockExposedToDirectSunlight(int x, int y, int z)
    {
    	return heightMap[x][z] <= y;
    }

    public boolean isBlockOnSurface(Vector3Int location)
    {
    	if(location.y == 255)
    	{
    		return true;
    	}
    	Block topBlock = getBlock(location.add(0, 1, 0));
		return topBlock == null || !topBlock.isOpaqueCube();
    }

    private Vector3Int getNeighborBlockGlobalLocation(Vector3Int location, Block.Face face)
    {
        Vector3Int neighborLocation = BlockNavigator.getNeighborBlockLocalLocation(location, face);
        neighborLocation.addLocal(blockLocation);
        return neighborLocation;
    }
    
    private boolean isValidBlockLocation(int x, int y, int z)
    {
        return Util.isValidIndex(blockTypes, x, y, z);
    }

    public Block getNeighborBlock_Local(Vector3Int location, Block.Face face)
    {
        Vector3Int neighborLocation = BlockNavigator.getNeighborBlockLocalLocation(location, face);
        return getBlock(neighborLocation);
    }

	@Override
	public void write(BitOutputStream outputStream)
	{
		for(int x = 0; x < blockTypes.length; x++)
		{
			for(int y = 0; y < blockTypes[0].length; y++)
			{
				for(int z = 0; z < blockTypes[0][0].length; z++)
				{
					outputStream.writeBits(blockTypes[x][y][z], 8);
					outputStream.writeBits(lights.light[y][x][z], 8);
				}
			}
		}
	}

	@Override
	public void read(BitInputStream inputStream) throws IOException
	{
		for(int x = 0; x < blockTypes.length; x++)
		{
			for(int y = 0; y < blockTypes[0].length; y++)
			{
				for(int z = 0; z < blockTypes[0][0].length; z++)
				{
					blockTypes[x][y][z] = (byte) inputStream.readBits(8);
					lights.light[y][x][z] = (byte) inputStream.readBits(8);
				}
			}
		}
		markDirty();
	}
	
    public Chunk clone()
    {
    	return new Chunk(world, location.x, location.z, blockTypes.clone());
    }

    @Override
    public String toString()
    {
    	return "Chunk " + location;
    }

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Chunk other = (Chunk) obj;
		if(location == null)
		{
			if(other.location != null)
				return false;
		}
		else if(!location.equals(other.location))
			return false;
		return true;
	}

}
