package com.chappelle.jcraft.world;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import com.chappelle.jcraft.BlockState;
import com.chappelle.jcraft.CubesSettings;
import com.chappelle.jcraft.Direction;
import com.chappelle.jcraft.Entity;
import com.chappelle.jcraft.EntityPlayer;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.blocks.SoundConstants;
import com.chappelle.jcraft.lighting.FloodFillLightManager;
import com.chappelle.jcraft.lighting.LightManager;
import com.chappelle.jcraft.network.BitInputStream;
import com.chappelle.jcraft.network.BitOutputStream;
import com.chappelle.jcraft.network.BitSerializable;
import com.chappelle.jcraft.network.CubesSerializer;
import com.chappelle.jcraft.profiler.Profiler;
import com.chappelle.jcraft.util.AABB;
import com.chappelle.jcraft.util.BlockNavigator;
import com.chappelle.jcraft.util.MathUtils;
import com.chappelle.jcraft.util.RayTrace;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.chappelle.jcraft.world.chunk.ChunkProvider;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class World implements BitSerializable
{
	private long seed;
	
	private CubesSettings settings;
	
	private ChunkProvider chunkProvider;
	
	/** Chunks added here will be rendered in the following tick(one per tick)*/ 
	public Queue<Chunk> chunkRenderQueue = new LinkedList<Chunk>();

	/** Chunks generated this tick. We queue them up and copy them to the render queue after the lighting data is ready*/
	private List<Chunk> generatedChunks = new ArrayList<Chunk>();
	
	private LightManager lightMgr;
	public Profiler profiler;
	private AssetManager assetManager;
	private Random random = new Random();
	private AudioNode music;
	private Camera cam;
	private List<Entity> entities = new ArrayList<Entity>();
	private EntityPlayer player;
	private Vector3Int playerLocTemp = new Vector3Int();
	private int chunkGenTicks;
	private ChunkGenerationRunnable gen = new ChunkGenerationRunnable();
	
	public World(ChunkProvider chunkProvider, Profiler profiler, CubesSettings settings, AssetManager assetManager, Camera cam, long seed)
	{
		this.chunkProvider = chunkProvider;
		this.chunkProvider.setWorld(this);
		this.profiler = profiler;
		this.settings = settings;
		this.assetManager = assetManager;
		this.lightMgr = new FloodFillLightManager(this);
		this.cam = cam;
		this.seed = seed;
		
        music = new AudioNode(assetManager, SoundConstants.MUSIC_CALM1);
        music.setReverbEnabled(false);
        music.setPositional(false);
        music.setLooping(true);
	}

	private class ChunkGenerationRunnable implements Runnable
	{
		public boolean isRunning;
		
		public void run()
		{
			isRunning = true;
			generateNearbyChunks();
			for(Chunk chunk : generatedChunks)
			{
				lightMgr.initChunkSunlight(chunk);
//				for(Direction dir : Direction.values())
//				{
//					Chunk neighbor = getChunkNeighbor(chunk, dir);
//					if(neighbor != null)
//					{
//						lightMgr.rebuildSunlight(neighbor);
//					}
//				}
			}
			chunkRenderQueue.addAll(generatedChunks);
			generatedChunks.clear();
			isRunning = false;
		}
	}
	
	public void update(float tpf)
	{
		if(chunkGenTicks > 30)
		{
			if(!gen.isRunning)
			{
				chunkGenTicks = 0;
				new Thread(gen).start();
			}
		}
		chunkGenTicks++;
	}

	/**
	 * Generates a radius of chunks around the player
	 */
	public void generateNearbyChunks()
	{
		int radius = 5;
		int x = (int)player.posX - 16*radius;
		int z = (int)player.posZ - 16*radius;
		for(int i = 0; i < radius*2; i++)
		{
			for(int j = 0; j < radius*2; j++)
			{
				playerLocTemp.set(x + 16*i, 0, z + 16*j);
				if(!playerLocTemp.hasNegativeCoordinate())
				{
					getChunk(playerLocTemp, true);
				}
			}
		}
	}
	
	public void setPlayer(EntityPlayer player)
	{
		this.player = player;
		getChunk(new Vector3Int(MathUtils.floor_double(player.posX), MathUtils.floor_double(player.posY), MathUtils.floor_double(player.posZ)), true);
	}
	
	public void addEntity(Entity entity)
	{
		entities.add(entity);
	}
	
	/**
	 * Plays a sound given the full path to the sound file.
	 * Sound file paths can be found in {@code SoundConstants}.
	 * @param name The full path of the sound file
	 */
    public void playSound(String name)
    {
    	makeAudio(name).play();//TODO: Could be a lot of object creation. May want to somehow pool these nodes or something in the future
    }

    /**
     * Plays a random sound given the base path of the file and the number
     * of existing sound variants. 
     * @param name The base name of the sound file.
     * @param fileVariants The number of different file variants
     */
    public void playSound(String name, int fileVariants)
    {
		playSound(name + (random.nextInt(3) + 1) + ".ogg");
    }

    protected AudioNode makeAudio(String location)
    {
        AudioNode result = new AudioNode(assetManager, location);
        result.setReverbEnabled(false);
        result.setVolume(.3f);
        return result;
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

    public void setBlock(RayTrace rayTrace, Block blockToPlace)
    {
        Vector3Int location = new Vector3Int(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ);
        Vector3Int newBlockLocation = BlockNavigator.getNeighborBlockLocalLocation(location, rayTrace.sideHit);
        Block.Face placementFace = rayTrace.sideHit;
        if (blockToPlace.isValidPlacementFace(placementFace) && blockToPlace.canPlaceBlockAt(this, newBlockLocation.x, newBlockLocation.y, newBlockLocation.z))
        {
            setBlock(newBlockLocation, blockToPlace);
            blockToPlace.onBlockPlaced(this, newBlockLocation, placementFace, getCameraDirectionAsUnitVector(cam.getDirection()));
        }
    }

	public void setBlock(int x, int y, int z, Block block)
	{
		setBlock(new Vector3Int(x, y, z), block);
	}

	public void setBlock(Vector3Int location, Block block)
	{
		BlockTerrain_LocalBlockState localBlockState = getLocalBlockState(location, true);
		if(localBlockState != null)
		{
			lightMgr.setBlockLight(location, block.lightValue);
			lightMgr.removeSunlight(location);
			
			localBlockState.setBlock(block);
		}
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
				int blockHeight = (((int) (((((row[z] - gridMinimum) * 100) / gridLargestDifference) / 100) * size.getY())) + 1);
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


	public void removeBlock(Vector3Int location)
	{
		BlockTerrain_LocalBlockState localBlockState = getLocalBlockState(location);
		if(localBlockState != null)
		{
			Block block = localBlockState.getBlock();
			if(block.isBreakable())
			{
				lightMgr.removeBlockLight(location);
				lightMgr.rebuildSunlight(localBlockState.getChunk());
				localBlockState.removeBlock();
				block.onBlockRemoved(this, location);
				
	            //Notify neighbors of block removal
	            for (Block.Face face : Block.Face.values())
	            {
	                Vector3Int neighborLocation = BlockNavigator.getNeighborBlockLocalLocation(location, face);
	                Block neighbor = getBlock(neighborLocation);
	                if (neighbor != null)
	                {
	                    neighbor.onNeighborRemoved(this, location, neighborLocation);
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
		return getLocalBlockState(blockLocation, false);
	}
	
	public BlockTerrain_LocalBlockState getLocalBlockState(Vector3Int blockLocation, boolean generateIfNeeded)
	{
		if(blockLocation.hasNegativeCoordinate())
		{
			return null;
		}
		Chunk chunk = getChunk(blockLocation, generateIfNeeded);
		if(chunk != null)
		{
			Vector3Int localBlockLocation = getLocalBlockLocation(blockLocation, chunk);
			return new BlockTerrain_LocalBlockState(chunk, localBlockLocation);
		}
		return null;
	}

	public Chunk getChunk(Vector3Int blockLocation)
	{
		return getChunk(blockLocation, false);
	}
	
	public Chunk getChunk(Vector3Int blockLocation, boolean generateIfNeeded)
	{
		if(blockLocation.hasNegativeCoordinate())
		{
			return null;
		}
		Vector3Int chunkLocation = getChunkLocation(blockLocation);
		Chunk cachedChunk = chunkProvider.getChunk(chunkLocation.x, chunkLocation.z);
		if(!generateIfNeeded || cachedChunk != null)
		{
			return cachedChunk;
		}
		else
		{
			Chunk chunk = chunkProvider.generateChunk(chunkLocation.x, chunkLocation.z);
			onChunkGenerated(chunk);
			return chunk;
		}
	}

	private void onChunkGenerated(Chunk chunk)
	{
		generatedChunks.add(chunk);
		for(Direction dir : Direction.values())
		{
			Chunk neighbor = getChunkNeighbor(chunk, dir);
			if(neighbor != null)
			{
				neighbor.markDirty();
			}
		}
	}
	
	public Chunk getChunkNeighbor(Chunk chunk, Direction direction)
	{
		Vector3Int chunkLocation = chunk.location.add(direction.getVector());
		return chunkProvider.getChunk(chunkLocation.getX(), chunkLocation.getZ());
	}
	
	private Vector3Int getChunkLocation(Vector3Int blockLocation)
	{
		Vector3Int chunkLocation = new Vector3Int();
		int chunkX = (blockLocation.getX() / 16);
		int chunkY = (blockLocation.getY() / 256);
		int chunkZ = (blockLocation.getZ() / 16);
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

	public CubesSettings getSettings()
	{
		return settings;
	}


	public void setBlocksFromTerrain(World world)
	{
		CubesSerializer.readFromBytes(this, CubesSerializer.writeToBytes(world));
	}

	@Override
	public void write(BitOutputStream outputStream)
	{
//		outputStream.writeInteger(chunks.length);
//		outputStream.writeInteger(chunks[0].length);
//		outputStream.writeInteger(chunks[0][0].length);
//		for(int x = 0; x < chunks.length; x++)
//		{
//			for(int y = 0; y < chunks[0].length; y++)
//			{
//				for(int z = 0; z < chunks[0][0].length; z++)
//				{
//					chunks[x][y][z].write(outputStream);
//				}
//			}
//		}
	}

	@Override
	public void read(BitInputStream inputStream) throws IOException
	{
//		int chunksCountX = inputStream.readInteger();
//		int chunksCountY = inputStream.readInteger();
//		int chunksCountZ = inputStream.readInteger();
//		initializeChunks(new Vector3Int(chunksCountX, chunksCountY, chunksCountZ));
//		for(int x = 0; x < chunksCountX; x++)
//		{
//			for(int y = 0; y < chunksCountY; y++)
//			{
//				for(int z = 0; z < chunksCountZ; z++)
//				{
//					chunks[x][y][z].read(inputStream);
//				}
//			}
//		}
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
    	profiler.startSection("light");
    	
    	lightMgr.calculateLight();
    	
    	profiler.endSection();
    }

    public List<AABB> getCollidingBoundingBoxes(Entity entity, AABB boundingBox)
    {
    	List<AABB> boundingBoxes = new ArrayList<AABB>();
    	
		int minX = MathUtils.floor_double(boundingBox.minX);
		int maxX = MathUtils.floor_double(boundingBox.maxX + 1.0D);
		int minY = MathUtils.floor_double(boundingBox.minY);
		int maxY = MathUtils.floor_double(boundingBox.maxY + 1.0D);
		int minZ = MathUtils.floor_double(boundingBox.minZ);
		int maxZ = MathUtils.floor_double(boundingBox.maxZ + 1.0D);

		for (int x = minX; x < maxX; ++x)
		{
			for (int z = minZ; z < maxZ; ++z)
			{
				for (int y = minY - 1; y < maxY; ++y)
				{
					Block block = getBlock(x, y, z);
					if (block != null)
					{
						AABB blockBoundingBox = block.getCollisionBoundingBox(this, x, y, z);
						if(blockBoundingBox != null && blockBoundingBox.intersectsWith(boundingBox))
						{
							boundingBoxes.add(blockBoundingBox);
						}
					}
				}
			}
		}
		return boundingBoxes;
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
    
    public RayTrace rayTraceBlocks(Vector3f position, Vector3f collisionEndPoint)
    {
		if (!Double.isNaN(position.x) && !Double.isNaN(position.y) && !Double.isNaN(position.z))
		{
			if (!Double.isNaN(collisionEndPoint.x) && !Double.isNaN(collisionEndPoint.y) && !Double.isNaN(collisionEndPoint.z))
			{
				int collisionX = MathUtils.floor_double(collisionEndPoint.x);
				int collisionY = MathUtils.floor_double(collisionEndPoint.y);
				int collisionZ = MathUtils.floor_double(collisionEndPoint.z);
				int posX = MathUtils.floor_double(position.x);
				int posY = MathUtils.floor_double(position.y);
				int posZ = MathUtils.floor_double(position.z);
				Block block = getBlock(posX, posY, posZ);

				if (block != null)
				{
					RayTrace movingobjectposition = block.collisionRayTrace(this, posX, posY, posZ, position, collisionEndPoint);

					if (movingobjectposition != null)
					{
						return movingobjectposition;
					}
				}
				int maxLoops = 200;
				while (maxLoops-- >= 0)
				{
					if (Double.isNaN(position.x) || Double.isNaN(position.y) || Double.isNaN(position.z))
					{
						return null;
					}

					if (posX == collisionX && posY == collisionY && posZ == collisionZ)
					{
						return null;
					}

					boolean xFinished = true;
					boolean yFinished = true;
					boolean zFinished = true;
					double xStep = 999.0D;
					double yStep = 999.0D;
					double zStep = 999.0D;

					if (collisionX > posX)
					{
						xStep = (double) posX + 1.0D;
					} 
					else if (collisionX < posX)
					{
						xStep = (double) posX + 0.0D;
					} 
					else
					{
						xFinished = false;
					}

					if (collisionY > posY)
					{
						yStep = (double) posY + 1.0D;
					} 
					else if (collisionY < posY)
					{
						yStep = (double) posY + 0.0D;
					} 
					else
					{
						yFinished = false;
					}

					if (collisionZ > posZ)
					{
						zStep = (double) posZ + 1.0D;
					} 
					else if (collisionZ < posZ)
					{
						zStep = (double) posZ + 0.0D;
					} 
					else
					{
						zFinished = false;
					}

					double x = 999.0D;
					double y = 999.0D;
					double z = 999.0D;
					double xDist = collisionEndPoint.x - position.x;
					double yDiff = collisionEndPoint.y - position.y;
					double zDiff = collisionEndPoint.z - position.z;

					if (xFinished)
					{
						x = (xStep - position.x) / xDist;
					}

					if (yFinished)
					{
						y = (yStep - position.y) / yDiff;
					}

					if (zFinished)
					{
						z = (zStep - position.z) / zDiff;
					}

					byte sideHit;

					if (x < y && x < z)
					{
						if (collisionX > posX)
						{
							sideHit = 4;
						} 
						else
						{
							sideHit = 5;
						}

						position.x = (float)xStep;
						position.y += yDiff * x;
						position.z += zDiff * x;
					} 
					else if (y < z)
					{
						if (collisionY > posY)
						{
							sideHit = 0;
						} 
						else
						{
							sideHit = 1;
						}

						position.x += xDist * y;
						position.y = (float)yStep;
						position.z += zDiff * y;
					} 
					else
					{
						if (collisionZ > posZ)
						{
							sideHit = 2;
						} 
						else
						{
							sideHit = 3;
						}

						position.x += xDist * z;
						position.y += yDiff * z;
						position.z = (float)zStep;
					}

					Vector3f newVecPos = new Vector3f(position.x, position.y, position.z);
					posX = (int) (newVecPos.x = (float) MathUtils.floor_double(position.x));

					if (sideHit == 5)
					{
						--posX;
						++newVecPos.x;
					}

					posY = (int) (newVecPos.y = (float) MathUtils.floor_double(position.y));

					if (sideHit == 1)
					{
						--posY;
						++newVecPos.y;
					}

					posZ = (int) (newVecPos.z = (float) MathUtils.floor_double(position.z));

					if (sideHit == 3)
					{
						--posZ;
						++newVecPos.z;
					}

					Block block1 = getBlock(posX, posY, posZ);

					if (block1 != null)
					{
						RayTrace rayTrace = block1.collisionRayTrace(this, posX, posY, posZ, position, collisionEndPoint);

						if (rayTrace != null)
						{
							return rayTrace;
						}
					}
				}
				return null;
			}
			else
			{
				return null;
			}
		}
		return null;
    }
    

    public long getSeed()
    {
    	return seed;
    }
}
