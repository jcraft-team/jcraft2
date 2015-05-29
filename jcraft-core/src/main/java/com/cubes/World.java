package com.cubes;

import java.io.IOException;
import java.util.ArrayList;

import com.chappelle.jcraft.blocks.PickedBlock;
import com.cubes.lighting.FloodFillLightManager;
import com.cubes.lighting.LightManager;
import com.cubes.network.BitInputStream;
import com.cubes.network.BitOutputStream;
import com.cubes.network.BitSerializable;
import com.cubes.network.CubesSerializer;
import com.jme3.math.Vector3f;

public class World implements BitSerializable
{
	private CubesSettings settings;
	public Chunk[][][] chunks;
	private ArrayList<BlockChunkListener> chunkListeners = new ArrayList<BlockChunkListener>();
	private LightManager lightMgr;

	public World(CubesSettings settings, Vector3Int chunksCount)
	{
		this.settings = settings;
		this.lightMgr = new FloodFillLightManager(this);
		initializeChunks(chunksCount);
	}

	private void initializeChunks(Vector3Int chunksCount)
	{
		chunks = new Chunk[chunksCount.getX()][chunksCount.getY()][chunksCount.getZ()];
		for(int x = 0; x < chunks.length; x++)
		{
			for(int y = 0; y < chunks[0].length; y++)
			{
				for(int z = 0; z < chunks[0][0].length; z++)
				{
					Chunk chunk = new Chunk(this, x, y, z);
					chunks[x][y][z] = chunk;
					lightMgr.initChunkSunlight(chunk);
				}
			}
		}
	}

	public Block getBlock(int x, int y, int z)
	{
		return getBlock(new Vector3Int(x, y, z));
	}

	public Block getBlock(Vector3Int location)
	{
		BlockTerrain_LocalBlockState localBlockState = getLocalBlockState(location);
		if(localBlockState != null)
		{
			return localBlockState.getBlock();
		}
		return null;
	}

	public void setBlockArea(Vector3Int location, Vector3Int size, Block block)
	{
		Vector3Int tmpLocation = new Vector3Int();
		for(int x = 0; x < size.getX(); x++)
		{
			for(int y = 0; y < size.getY(); y++)
			{
				for(int z = 0; z < size.getZ(); z++)
				{
					tmpLocation.set(location.getX() + x, location.getY() + y, location.getZ() + z);
					setBlock(tmpLocation, block);
				}
			}
		}
	}

	public void setBlock(int x, int y, int z, Block block)
	{
		setBlock(new Vector3Int(x, y, z), block);
	}

	public void setBlock(Vector3Int location, Block block)
	{
		BlockTerrain_LocalBlockState localBlockState = getLocalBlockState(location);
		if(localBlockState != null)
		{
			lightMgr.setBlockLight(location, block.getBlockLightValue());
        	lightMgr.removeSunlight(location);
			
			localBlockState.setBlock(block);
		}
	}

	public void removeBlock(Vector3Int location)
	{
		BlockTerrain_LocalBlockState localBlockState = getLocalBlockState(location);
		if(localBlockState != null)
		{
			Block block = localBlockState.getBlock();
			if(block.isRemovable())
			{
				lightMgr.removeBlockLight(location);
				lightMgr.rebuildSunlight(localBlockState.getChunk());
				localBlockState.removeBlock();
				
	            //Notify neighbors of block removal
	            for (Block.Face face : Block.Face.values())
	            {
	                Vector3Int neighborLocation = BlockNavigator.getNeighborBlockLocalLocation(location, face);
	                Block neighbor = getBlock(neighborLocation);
	                if (neighbor != null)
	                {
	                    neighbor.onNeighborRemoved(location, neighborLocation);
	                }
	            }
			}
		}
	}

	public void removeBlockArea(Vector3Int location, Vector3Int size)
	{
		Vector3Int tmpLocation = new Vector3Int();
		for(int x = 0; x < size.getX(); x++)
		{
			for(int y = 0; y < size.getY(); y++)
			{
				for(int z = 0; z < size.getZ(); z++)
				{
					tmpLocation.set(location.getX() + x, location.getY() + y, location.getZ() + z);
					removeBlock(tmpLocation);
				}
			}
		}
	}

	public void removeBlock(int x, int y, int z)
	{
		removeBlock(new Vector3Int(x, y, z));
	}

	public BlockTerrain_LocalBlockState getLocalBlockState(Vector3Int blockLocation)
	{
		if(blockLocation.hasNegativeCoordinate())
		{
			return null;
		}
		Chunk chunk = getChunk(blockLocation);
		if(chunk != null)
		{
			Vector3Int localBlockLocation = getLocalBlockLocation(blockLocation, chunk);
			return new BlockTerrain_LocalBlockState(chunk, localBlockLocation);
		}
		return null;
	}

	public Chunk getChunk(Vector3Int blockLocation)
	{
		if(blockLocation.hasNegativeCoordinate())
		{
			return null;
		}
		Vector3Int chunkLocation = getChunkLocation(blockLocation);
		if(isValidChunkLocation(chunkLocation))
		{
			return chunks[chunkLocation.getX()][chunkLocation.getY()][chunkLocation.getZ()];
		}
		return null;
	}

	public Chunk getChunkNeighbor(Chunk chunk, Direction direction)
	{
		Vector3Int chunkLocation = chunk.location.add(direction.getVector());
		if(isValidChunkLocation(chunkLocation))
		{
			return chunks[chunkLocation.getX()][chunkLocation.getY()][chunkLocation.getZ()];
		}
		return null;
	}
	
	private boolean isValidChunkLocation(Vector3Int location)
	{
		return Util.isValidIndex(chunks, location);
	}

	private Vector3Int getChunkLocation(Vector3Int blockLocation)
	{
		Vector3Int chunkLocation = new Vector3Int();
		int chunkX = (blockLocation.getX() / settings.getChunkSizeX());
		int chunkY = (blockLocation.getY() / settings.getChunkSizeY());
		int chunkZ = (blockLocation.getZ() / settings.getChunkSizeZ());
		chunkLocation.set(chunkX, chunkY, chunkZ);
		return chunkLocation;
	}

	public Vector3Int getLocalBlockLocation(Vector3Int blockLocation, Chunk chunk)
	{
		Vector3Int localLocation = new Vector3Int();
		int localX = (blockLocation.getX() - chunk.getBlockLocation().getX());
		int localY = (blockLocation.getY() - chunk.getBlockLocation().getY());
		int localZ = (blockLocation.getZ() - chunk.getBlockLocation().getZ());
		localLocation.set(localX, localY, localZ);
		return localLocation;
	}

	public void addChunkListener(BlockChunkListener blockChunkListener)
	{
		chunkListeners.add(blockChunkListener);
	}

	public void removeChunkListener(BlockChunkListener blockChunkListener)
	{
		chunkListeners.remove(blockChunkListener);
	}

	public CubesSettings getSettings()
	{
		return settings;
	}

	public Chunk[][][] getChunks()
	{
		return chunks;
	}


	public void setBlocksFromHeightmap(Vector3Int location, int[][] heightmap, Block block)
	{
		Vector3Int tmpLocation = new Vector3Int();
		Vector3Int tmpSize = new Vector3Int();
		for(int x = 0; x < heightmap.length; x++)
		{
			for(int z = 0; z < heightmap[0].length; z++)
			{
				tmpLocation.set(location.getX() + x, location.getY(), location.getZ() + z);
				tmpSize.set(1, heightmap[x][z], 1);
				setBlockArea(tmpLocation, tmpSize, block);
			}
		}
	}

	public void setBlocksFromNoise(Vector3Int location, Vector3Int size, float roughness, Block block)
	{
		Noise noise = new Noise(null, roughness, size.getX(), size.getZ());
		noise.initialise();
		float gridMinimum = noise.getMinimum();
		float gridLargestDifference = (noise.getMaximum() - gridMinimum);
		float[][] grid = noise.getGrid();
		for(int x = 0; x < grid.length; x++)
		{
			float[] row = grid[x];
			for(int z = 0; z < row.length; z++)
			{
				/*---Calculation of block height has been summarized to minimize the java heap---
				float gridGroundHeight = (row[z] - gridMinimum);
				float blockHeightInPercents = ((gridGroundHeight * 100) / gridLargestDifference);
				int blockHeight = ((int) ((blockHeightInPercents / 100) * size.getY())) + 1;
				---*/
				int blockHeight = (((int) (((((row[z] - gridMinimum) * 100) / gridLargestDifference) / 100) * size
						.getY())) + 1);
				Vector3Int tmpLocation = new Vector3Int();
				for(int y = 0; y < blockHeight; y++)
				{
					tmpLocation.set(location.getX() + x, location.getY() + y, location.getZ() + z);
					setBlock(tmpLocation, block);
				}
			}
		}
	}

	public void setBlocksForMaximumFaces(Vector3Int location, Vector3Int size, Block block)
	{
		Vector3Int tmpLocation = new Vector3Int();
		for(int x = 0; x < size.getX(); x++)
		{
			for(int y = 0; y < size.getY(); y++)
			{
				for(int z = 0; z < size.getZ(); z++)
				{
					if(((x ^ y ^ z) & 1) == 1)
					{
						tmpLocation.set(location.getX() + x, location.getY() + y, location.getZ() + z);
						setBlock(tmpLocation, block);
					}
				}
			}
		}
	}

	public void setBlocksFromTerrain(World world)
	{
		CubesSerializer.readFromBytes(this, CubesSerializer.writeToBytes(world));
	}

	@Override
	public void write(BitOutputStream outputStream)
	{
		outputStream.writeInteger(chunks.length);
		outputStream.writeInteger(chunks[0].length);
		outputStream.writeInteger(chunks[0][0].length);
		for(int x = 0; x < chunks.length; x++)
		{
			for(int y = 0; y < chunks[0].length; y++)
			{
				for(int z = 0; z < chunks[0][0].length; z++)
				{
					chunks[x][y][z].write(outputStream);
				}
			}
		}
	}

	@Override
	public void read(BitInputStream inputStream) throws IOException
	{
		int chunksCountX = inputStream.readInteger();
		int chunksCountY = inputStream.readInteger();
		int chunksCountZ = inputStream.readInteger();
		initializeChunks(new Vector3Int(chunksCountX, chunksCountY, chunksCountZ));
		for(int x = 0; x < chunksCountX; x++)
		{
			for(int y = 0; y < chunksCountY; y++)
			{
				for(int z = 0; z < chunksCountZ; z++)
				{
					chunks[x][y][z].read(inputStream);
				}
			}
		}
	}
	
	public int getLight(Vector3Int blockLocation)
	{
        if (blockLocation.hasNegativeCoordinate())
        {
            return -1;
        }
        Chunk chunk = getChunk(blockLocation);
        if (chunk != null)
        {
            Vector3Int localBlockLocation = getLocalBlockLocation(blockLocation, chunk);
            return chunk.getLights().getLight(localBlockLocation.x, localBlockLocation.y, localBlockLocation.z);
        }
		return 0;
	}
	
    public BlockState getBlockState(Vector3Int location)
    {
        BlockTerrain_LocalBlockState localState = getLocalBlockState(location);
        Chunk chunk = localState.getChunk();
        return chunk.getBlockState(localState.getLocalBlockLocation());
    }


    public void calculateLight()
    {
    	lightMgr.calculateLight();
    }

    public void setBlock(PickedBlock pickedBlock, Block blockToPlace)
    {
        pickedBlock.setBlock(blockToPlace);//Kind of a hack, the pickedBlock has a null block at this point

        Vector3Int location = pickedBlock.getBlockLocation();
        Block.Face face = Block.Face.fromNormal(pickedBlock.getContactNormal());
        if (blockToPlace.isValidPlacementFace(face))
        {
            Block bottomBlock = getBlock(location.subtract(0, 1, 0));
            if (blockToPlace.isAffectedByGravity() && bottomBlock == null)
            {
//                Geometry geometry = blockToPlace.makeBlockGeometry();
//                if(geometry != null)
//                {
//                	geometry.setName("active block");
//                	Vector3f placementLocation = location.mult((int) cubeSettings.getBlockSize()).toVector3f();
//                	geometry.setLocalTranslation(placementLocation);
//                	node.attachChild(geometry);
//                	
//                	FallingBlocks fallingBlocks = new FallingBlocks(getFloorGeometry(location), location);
//                	fallingBlocks.add(blockToPlace, geometry);
//                	fallingBlocksList.add(fallingBlocks);
//                }
            }
            else
            {
                setBlock(location, blockToPlace);
                blockToPlace.onBlockPlaced(location, pickedBlock.getContactNormal(), getCameraDirectionAsUnitVector(pickedBlock.getCameraDirection()));
            }
        }
    }
    
    private Vector3f getCameraDirectionAsUnitVector(Vector3f cameraDirection)
    {
    	cameraDirection = cameraDirection.normalize();
		float xPos = cameraDirection.angleBetween(Vector3f.UNIT_X);
    	float xNeg = cameraDirection.angleBetween(Vector3f.UNIT_X.negate());
    	float zPos = cameraDirection.angleBetween(Vector3f.UNIT_Z);
    	float zNeg = cameraDirection.angleBetween(Vector3f.UNIT_Z.negate());
    	if(isFirstArgMin(xPos, xNeg, zPos, zNeg))
    	{
    		return Vector3f.UNIT_X;
    	}
    	else if(isFirstArgMin(xNeg, xPos, zPos, zNeg))
    	{
    		return Vector3f.UNIT_X.negate();
    	}
    	else if(isFirstArgMin(zPos, xPos, xNeg, zNeg))
    	{
    		return Vector3f.UNIT_Z;
    	}
		return Vector3f.UNIT_Z.negate();
    }

    private boolean isFirstArgMin(float a, float b, float c, float d)
    {
    	if(a < b && a < c && a < d)
    	{
    		return true;
    	}
    	return false;
    }
}
