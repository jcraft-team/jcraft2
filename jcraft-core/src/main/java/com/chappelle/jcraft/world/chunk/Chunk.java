package com.chappelle.jcraft.world.chunk;

import java.io.IOException;
import java.util.*;

import org.apache.commons.lang3.*;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.lighting.*;
import com.chappelle.jcraft.serialization.*;
import com.chappelle.jcraft.util.*;
import com.chappelle.jcraft.util.math.Vector3Int;
import com.chappelle.jcraft.world.World;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.*;

public class Chunk implements BitSerializable
{
	private long lastSavedTime = System.currentTimeMillis();
	private long lastModifiedTime;
	private long lastMeshUpdateTime;
	private int[][] heightMap;
	private int[][][] data = new int[16][256][16];
	private BitField blockTypeField  = new BitField(0xFF000000);
	private BitField blockLightField = new BitField(0x00F00000);
	private BitField skyLightField   = new BitField(0x000F0000);
	private BitField blockStateField = new BitField(0x000000FF);

	public Vector3Int location = new Vector3Int();
    public Vector3Int blockLocation = new Vector3Int();
    
    public World world;
    public boolean isLoaded;
    private boolean isAddedToScene;

	private Mesh pendingOpaqueMesh; 
	private Mesh pendingTransparentMesh; 
	private Geometry geomOpaque; 
	private Geometry geomTransparent; 
	private Node node = new Node();
	public Long id;
	public boolean isLightUpdating;
	
	public Chunk(World world, int x, int z)
	{
		this(world, x, z, new byte[16][256][16]);
	}
	
	public Chunk(World world, int x, int z, int[][][] data)
	{
    	this.world = world;
    	location.set(x, 0, z);
    	blockLocation.set(location.mult(16, 256, 16));
    	this.data = data;
    	byte[][][] blockTypes = new byte[16][256][16];
    	for(int i = 0; i < 16; i++)
    	{
    		for(int j = 0; j < 256; j++)
    		{
    			for(int k = 0; k < 16; k++)
    			{
    				blockTypes[i][j][k] = (byte)blockTypeField.getValue(data[i][j][k]);
    			}
    		}
    	}
    	node.setLocalTranslation(new Vector3f(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ()));
    	this.id = ChunkCoordIntPair.chunkXZ2Int(x, z); 
    	this.heightMap = makeHeightMap(blockTypes);
//    	world.getLightManager().initSunlight(this);
	}
	
    public Chunk(World world, int x, int z, byte[][][] blockTypes)
    {
    	this.world = world;
    	location.set(x, 0, z);
    	blockLocation.set(location.mult(16, 256, 16));
    	this.data = new int[16][256][16];
    	for(int i = 0; i < 16; i++)
    	{
    		for(int j = 0; j < 256; j++)
    		{
    			for(int k = 0; k < 16; k++)
    			{
    				data[i][j][k] = 0;
    				data[i][j][k] = blockTypeField.setValue(data[i][j][k], blockTypes[i][j][k]);
    			}
    		}
    	}
    	node.setLocalTranslation(new Vector3f(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ()));
    	this.id = ChunkCoordIntPair.chunkXZ2Int(x, z); 
    	this.heightMap = makeHeightMap(blockTypes);
//    	world.getLightManager().initSunlight(this);
    }
    
    public boolean hasPendingMesh()
    {
    	return pendingOpaqueMesh != null || pendingTransparentMesh != null;
    }
    
    public boolean isLoadedAndAddedToScene()
    {
    	return isLoaded && isAddedToScene;
    }
    
    public boolean isAddedToScene()
    {
    	return isAddedToScene;
    }
    
    public boolean hasChangedSinceLastSave()
    {
    	return lastSavedTime < lastModifiedTime;
    }
    
    public void setLastSavedTime(long lastSavedTime)
    {
    	this.lastSavedTime = lastSavedTime;
    }
    
    public int[][] getHeightMap()
    {
    	return heightMap;
    }
    
	public void setBlock(int x, int y, int z, Block block)
    {
		//NOTE: Kind of a hack to check for transparency here. For now it keeps torches flowers and doors from covering the ground
    	if(heightMap[x][z] < y && !block.isTransparent)
    	{
    		heightMap[x][z] = y;
    	}
    	
    	data[x][y][z] = blockTypeField.setValue(data[x][y][z], block.blockId);
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
            int blockType = blockTypeField.getValue(data[x][y][z]);
            return Block.blocksList[blockType];
        }
        return null;
    }
    
    public void removeBlock(int x, int y, int z)
    {
    	data[x][y][z] = blockTypeField.setValue(data[x][y][z], 0);
    	if(heightMap[x][z] == y)
    	{
    		heightMap[x][z] = findHeight(x, z, y);
    	}
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
    
    public void setBlockState(int x, int y, int z, byte value)
    {
		data[x][y][z] = blockStateField.setValue(data[x][y][z], value);
    	markDirty();
    }

    public byte getBlockState(Vector3Int location)
    {
    	return (byte)getBlockState(location.x, location.y, location.z);
    }
    
    
    private byte getBlockState(int x, int y, int z)
    {
    	return (byte)blockStateField.getValue(data[x][y][z]);
    }

    public void addToScene(Node parent)
    {
    	parent.attachChild(node);
    	isAddedToScene = true;
    }
    
    public void removeFromScene()
    {
    	node.getParent().detachChild(node);
    	isAddedToScene = false;
    }

    public void setPendingMesh(Mesh opaqueMesh, Mesh transparentMesh)
    {
    	this.pendingOpaqueMesh = opaqueMesh;
    	this.pendingTransparentMesh = transparentMesh;
    	lastMeshUpdateTime = System.currentTimeMillis();
    }
    
    public void setMeshFromPending()
    {
    	if(hasPendingMesh())
    	{
    		setMesh(pendingOpaqueMesh, pendingTransparentMesh);
    	}
		pendingOpaqueMesh = null;
		pendingTransparentMesh = null;
    }
    
    /**
     * Updates the mesh. NEVER CALL THIS FROM A THREAD OTHER THAN THE MAIN THREAD!
     * @param opaqueMesh
     * @param transparentMesh
     */
    public void setMesh(Mesh opaqueMesh, Mesh transparentMesh)
    {
		if(geomOpaque == null)
		{
			geomOpaque = new Geometry("");
			geomOpaque.setQueueBucket(Bucket.Opaque);
			node.attachChild(geomOpaque);
			geomOpaque.setMaterial(world.getBlockMaterial());
		}
		if(geomTransparent == null)
		{
			geomTransparent = new Geometry("");
			geomTransparent.setQueueBucket(Bucket.Transparent);
			node.attachChild(geomTransparent);
			geomTransparent.setMaterial(world.getBlockMaterial());
		}
		geomOpaque.setMesh(opaqueMesh);
		geomTransparent.setMesh(transparentMesh);
		isLoaded = true;
    }
    
	public void setDayNightLighting(float dayNightLighting)
	{
		if(geomOpaque != null && geomTransparent != null)
		{
			this.geomOpaque.getMaterial().setFloat("dayNightLighting", dayNightLighting);
			this.geomTransparent.getMaterial().setFloat("dayNightLighting", dayNightLighting);
		}
	}
	
    public Vector3Int getBlockLocation()
    {
        return blockLocation;
    }

    public boolean isDirty()
    {
    	return lastMeshUpdateTime < lastModifiedTime;
    }
    
    public void markDirty()
    {
    	this.lastModifiedTime = System.currentTimeMillis();
    }

	public void setLight(int x, int y, int z, LightType type, int lightVal)
	{
		if(x >= 0 && x < 16 && z >= 0 && z < 16)//sometimes get ArrayIndexOutOfBoundsExceptions. I think it's only when we are in negative land.
		{
			if(type == LightType.BLOCK)
			{
				data[x][y][z] = blockLightField.setValue(data[x][y][z], lightVal);
			}
			else
			{
				data[x][y][z] = skyLightField.setValue(data[x][y][z], lightVal);
			}
			markDirty();
		}
	}

	private int getSunlight(int x, int y, int z)
	{
		return skyLightField.getValue(data[x][y][z]);
	}
	
	public int[][][] getData()
	{
		return data;
	}
	private int getBlocklight(int x, int y, int z)
	{
		return blockLightField.getValue(data[x][y][z]);
	}

	public int getLight(int x, int y, int z)
	{
		int blocklight = getBlocklight(x, y, z);
		int sunlight = getSunlight(x, y, z); 
		return Math.max(blocklight, sunlight);
	}
    
	public int getLight(int x, int y, int z, LightType lightType)
	{
		if(lightType == LightType.BLOCK)
		{
			return getBlocklight(x, y, z);
		}
		return getSunlight(x, y, z);
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

    public boolean isBlockExposedToDirectSunlight(int x, int y, int z)
    {
    	return heightMap[x][z] <= y;
    }

    public boolean isBlockOnSurface(Vector3Int location)
    {
    	return heightMap[location.x][location.z] == location.y;
    }

    private Vector3Int getNeighborBlockGlobalLocation(Vector3Int location, Block.Face face)
    {
        Vector3Int neighborLocation = Block.Face.getNeighborBlockLocalLocation(location, face);
        neighborLocation.addLocal(blockLocation);
        return neighborLocation;
    }
    
    private boolean isValidBlockLocation(int x, int y, int z)
    {
        return Util.isValidIndex(x, y, z);
    }

    public Block getNeighborBlock_Local(Vector3Int location, Block.Face face)
    {
        Vector3Int neighborLocation = Block.Face.getNeighborBlockLocalLocation(location, face);
        return getBlock(neighborLocation);
    }

	@Override
	public void write(BitOutputStream outputStream)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int y = 0; y < 256; y++)
			{
				for(int z = 0; z < 16; z++)
				{
					outputStream.writeBits(data[x][y][z], 8);
				}
			}
		}
	}

	@Override
	public void read(BitInputStream inputStream) throws IOException
	{
		for(int x = 0; x < 16; x++)
		{
			for(int y = 0; y < 256; y++)
			{
				for(int z = 0; z < 16; z++)
				{
					data[x][y][z] = inputStream.readBits(32);
				}
			}
		}
		markDirty();
	}
	
    public Chunk clone()
    {
    	Chunk chunk = new Chunk(world, location.x, location.z);
    	chunk.data = this.data.clone();
    	return chunk;
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
