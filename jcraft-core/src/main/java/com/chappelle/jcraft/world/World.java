package com.chappelle.jcraft.world;

import java.util.*;
import java.util.concurrent.*;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.blocks.*;
import com.chappelle.jcraft.lighting.*;
import com.chappelle.jcraft.serialization.VoxelWorldSave;
import com.chappelle.jcraft.util.*;
import com.chappelle.jcraft.util.math.*;
import com.chappelle.jcraft.util.physics.*;
import com.chappelle.jcraft.world.chunk.*;
import com.jme3.app.Application;
import com.jme3.audio.*;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

public class World
{
	private CubesSettings settings;

	private final String name;
	private List<Entity> entities = new ArrayList<Entity>();
	private EntityPlayer player;
	private TimeOfDayProvider timeOfDayProvider = new FixedTimeOfDayProvider(12);
	
	private Application app;
	public Node node = new Node("world");
	private ChunkManager chunkManager;
	private List<WorldListener> listeners = new ArrayList<>();
	private long seed;
	private Node parent;
	private VoxelWorldSave voxelWorldSave;
	private List<Enemy> enemies = new ArrayList<>();
	private LightManager lightManager;
	private final Context context;
	
	public World(Application app, Context context, String name, long seed)
	{
		this.context = context;
		this.context.put(World.class, this);
		this.seed = seed;
		this.app = app;
		this.name = name;
		this.settings = context.get(CubesSettings.class);
        this.voxelWorldSave = context.get(VoxelWorldSave.class);
        
        this.chunkManager = new ConcurrentChunkManager(context);
        this.context.put(ChunkManager.class, chunkManager);

        this.lightManager = new FloodFillLightManager(this);
//        this.lightManager = new RecursiveFloodFillLightManager(this);
        this.context.put(LightManager.class, lightManager);
	}
	
	public LightManager getLightManager()
	{
		return lightManager;
	}
	
	public void addEnemy(Enemy enemy)
	{
		this.enemies.add(enemy);
	}
	
	public void spawnPlayer(EntityPlayer player)
	{
		Camera camera = player.cam;
		camera.setFrustumPerspective(GameSettings.fieldOfView, (float) camera.getWidth() / camera.getHeight(), 0.1f, 1000f);
		Vector3f lookAt = (Vector3f)voxelWorldSave.getGameData("playerLookDirection");
		if(lookAt == null)
		{
			lookAt = new Vector3f(1, 0, 1);
		}
		camera.lookAtDirection(lookAt, Vector3f.UNIT_Y);
		
		player.preparePlayerToSpawn();
	}
	
	public void spawnEnemy(double x, double y, double z)
	{
		Enemy enemy = new Enemy(this);
		enemy.setPosition(x, y, z);
		enemies.add(enemy);
		parent.addControl(new EnemyControl(app.getAssetManager(), enemy));
	}
	
	public VoxelWorldSave getVoxelWorldSave()
	{
		return voxelWorldSave;
	}
	
	public long getSeed()
	{
		return seed;
	}

	public EntityPlayer getPlayer()
	{
		return player;
	}
	
	public TimeOfDayProvider getTimeOfDayProvider()
	{
		return timeOfDayProvider;
	}
	
	public void addListener(WorldListener listener)
	{
		this.listeners.add(listener);
	}
	
	public void removeListener(WorldListener listener)
	{
		this.listeners.remove(listener);
	}
	
	private void fireOnBlockAdded(Block block, Chunk chunk, int x, int y, int z)
	{
		for(WorldListener listener : listeners)
		{
			listener.onBlockAdded(block, chunk, x, y, z);
		}
	}

	private void fireOnBlockRemoved(Block block, Chunk chunk, int x, int y, int z)
	{
		for(WorldListener listener : listeners)
		{
			listener.onBlockAdded(block, chunk, x, y, z);
		}
	}
	
	public ChunkManager getChunkManager()
	{
		return chunkManager;
	}
	
	public void update(float tpf)
	{
		updateTimeOfDay();

		lightManager.propagateLight();

		chunkManager.update();
	}
	
	private void updateTimeOfDay()
	{
		if(timeOfDayProvider != null)
		{
			float hour = timeOfDayProvider.getTimeOfDay();
			float dayNightLighting = calculateDayNightLighting(hour);
			for(Chunk chunk : chunkManager.getLoadedChunks())
			{
				chunk.setDayNightLighting(dayNightLighting);
			}
		}
	}

	public float calculateDayNightLighting(float hour)
	{
		int morningStart = 6;
		int morningEnd = 8;
		int nightStart = 17;
		int nightEnd = 19;
		float darkest = 0.14f;
		if(hour < morningEnd && hour > morningStart)//Transition to daylight between 6 and 8
		{
			float denominator = morningEnd - morningStart;
			float progress = hour - morningStart;
			return Math.max(progress/denominator, darkest);
		}
		else if(hour > nightStart && hour < nightEnd)//Transition to night
		{
			float denominator = nightEnd - nightStart;
			float progress = hour - nightStart;
			return Math.max(1.0f-progress/denominator, darkest);
		}
		else if(hour > morningEnd && hour < nightStart)
		{
			return 1.0f;
		}
		else
		{
			return darkest;
		}
	}

	public <V> Future<V> enqueue(Callable<V> callable) 
	{
		return app.enqueue(callable);
	}
	
	public void addToScene(Node parent)
	{
		this.parent = parent;
		parent.attachChild(node);
	}
	
	public void setTimeOfDayProvider(TimeOfDayProvider timeOfDayProvider)
	{
		if(timeOfDayProvider != null)
		{
			this.timeOfDayProvider = timeOfDayProvider;
		}
	}
	
	public int getLoadedChunkCount()
	{
		return chunkManager.getLoadedChunks().size();
	}
	
	public List<Chunk> getNearbyChunks(int radius)
	{
		return getChunkNeighborhood(getPlayerChunk(), radius);
	}

    /**
     * Returns the chunk neighbors with a radius of r excluding the current chunk. This is a Moore Neighborhood as in http://mathworld.wolfram.com/MooreNeighborhood.html.
     * The number of chunks in the grid will be (2*r+1)^2. So if r=10, then then it will return 441 chunks.
     * @param r The radius
     * @return A list of neighboring Chunks with a radius of r
     */
    public List<Chunk> getChunkNeighborhood(Chunk referenceChunk, int r)
    {
    	List<Chunk> result = new ArrayList<Chunk>();
		//Iterates starting in top left corner, then down, then across to the right
		int x = referenceChunk.location.x - r;
		int z = referenceChunk.location.z + r;
		int gridWidth = r*2 + 1;
		for(int xMod = 0; xMod < gridWidth; xMod++)
		{
			for(int zMod = 0; zMod < gridWidth; zMod++)
			{
				int chunkX = x+xMod;
				int chunkZ = z-zMod;
				if(!(chunkX == referenceChunk.location.x && chunkZ == referenceChunk.location.z))//Exclude this chunk from result
				{
					Chunk chunk = getChunkFromChunkCoordinates(chunkX, chunkZ);
					if(chunk != null)
					{
						result.add(chunk);
					}
				}
			}
		}
    	return result;
    }

	public Chunk getPlayerChunk()
	{
		return getChunkFromBlockCoordinates(MathUtils.floor_double(player.posX), MathUtils.floor_double(player.posZ));
	}
	
	public void setPlayer(EntityPlayer player)
	{
		this.player = player;
//		chunkMgr.generateTerrain(MathUtils.floor_double(player.posX)/16, MathUtils.floor_double(player.posZ)/16);
//		chunkMgr.updateNow();
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
//    	makeAudio(name).play();//TODO: Could be a lot of object creation. May want to somehow pool these nodes or something in the future
    }

    /**
     * Plays a random sound given the base path of the file and the number
     * of existing sound variants. 
     * @param name The base name of the sound file.
     * @param fileVariants The number of different file variants
     */
    public void playSound(String name, int fileVariants)
    {
//		playSound(name + (random.nextInt(3) + 1) + ".ogg");
    }

    protected AudioNode makeAudio(String location)
    {
        AudioNode result = new AudioNode(app.getAssetManager(), location, AudioData.DataType.Buffer);
        result.setReverbEnabled(false);
        result.setVolume(4.0f);
        return result;
    }
    
    public boolean isOpaqueBlockPresent(int x, int y, int z)
    {
    	if(y > 255 || y < 0)
    	{
    		return false;
    	}
    	Block block = getBlock(new Vector3Int(x, y, z));
		return block != null && !block.isTransparent;
    }

    public Block getBlock(int x, int y, int z)
	{
		return getBlock(new Vector3Int(x, y, z));
	}

	public Block getBlock(Vector3Int location)
	{
		ChunkLocation localBlockState = getLocalBlockState(location);
		if(localBlockState != null)
		{
			return localBlockState.getBlock();
		}
		return null;
	}

    public void setBlock(RayTrace rayTrace, Vector3f cameraDirectionUnitVector, Block blockToPlace)
    {
        Vector3Int location = new Vector3Int(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ);
        Vector3Int newBlockLocation = Face.getNeighborBlockLocalLocation(location, rayTrace.sideHit);
        Face placementFace = rayTrace.sideHit;
        if (blockToPlace.canPlaceBlockOn(this, location.x, location.y, location.z, rayTrace.sideHit) && blockToPlace.canPlaceBlockAt(this, newBlockLocation.x, newBlockLocation.y, newBlockLocation.z))
        {
            setBlock(newBlockLocation, blockToPlace);
            blockToPlace.onBlockPlaced(this, newBlockLocation, placementFace, cameraDirectionUnitVector);
        }
    }

	public void setBlock(int x, int y, int z, Block block)
	{
		setBlock(new Vector3Int(x, y, z), block);
	}

	public void setBlock(Vector3Int location, Block block)
	{
		ChunkLocation localBlockState = getLocalBlockState(location);
		if(localBlockState != null)
		{
			Chunk chunk = localBlockState.getChunk();
			Vector3Int chunkBlockLocation = localBlockState.getLocalBlockLocation();
			if(!block.isTransparent)
			{
				lightManager.removeBlockLight(chunk, chunkBlockLocation);
			}
			lightManager.setBlockLight(chunk, chunkBlockLocation, block.lightValue);
			if(!block.isTransparent)
			{
				lightManager.removeSunlight(chunk, chunkBlockLocation);
			}
			
			//Here we mark neighbor chunks dirty if we broke a block on the border. This is needed
			//since we don't render block faces that are covered therefore breaking a block could
			//expose what looks like a hole in the world. Rebuilding the neighbor fixes it
			if(isChunkBorder(localBlockState.getLocalBlockLocation()))
			{
				markNeighborChunksDirty(chunk, localBlockState.getLocalBlockLocation());
			}
			localBlockState.setBlock(block);
			fireOnBlockAdded(block, chunk, chunkBlockLocation.x, chunkBlockLocation.y, chunkBlockLocation.z);
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
	
	public void removeBlock(Vector3Int location)
	{
		ChunkLocation localBlockState = getLocalBlockState(location);
		if(localBlockState != null)
		{
			Block block = localBlockState.getBlock();
			if(block != null && block.isBreakable())
			{
				Chunk chunk = localBlockState.getChunk();
				//Here we mark neighbor chunks dirty if we broke a block on the border. This is needed
				//since we don't render block faces that are covered therefore breaking a block could
				//expose what looks like a hole in the world. Rebuilding the neighbor fixes it
				Vector3Int chunkBlockLocation = localBlockState.getLocalBlockLocation();
				if(isChunkBorder(chunkBlockLocation))
				{
					markNeighborChunksDirty(chunk, chunkBlockLocation);
				}
				localBlockState.removeBlock();
				block.onBlockRemoved(this, location);
				lightManager.removeBlockLight(chunk, chunkBlockLocation);
				lightManager.restoreSunlight(chunk, chunkBlockLocation);
				
	            //Notify neighbors of block removal
	            for (Face face : Face.values())
	            {
	                Vector3Int neighborLocation = Face.getNeighborBlockLocalLocation(location, face);
	                Block neighbor = getBlock(neighborLocation);
	                if (neighbor != null)
	                {
	                    neighbor.onNeighborRemoved(this, location, neighborLocation);
	                }
	            }
	            fireOnBlockRemoved(block, chunk, chunkBlockLocation.x, chunkBlockLocation.y, chunkBlockLocation.z);
			}
		}
	}

	private void markNeighborChunksDirty(Chunk chunk, Vector3Int location)
	{
		if(location.x == 0)
		{
			Chunk neighbor = getChunkNeighbor(chunk, Direction.LEFT);
			if(neighbor != null)
			{
				neighbor.markDirty();
			}
		}
		else if(location.x == 15)
		{
			Chunk neighbor = getChunkNeighbor(chunk, Direction.RIGHT);
			if(neighbor != null)
			{
				neighbor.markDirty();
			}
		}
		if(location.z == 0)
		{
			Chunk neighbor = getChunkNeighbor(chunk, Direction.BACK);
			if(neighbor != null)
			{
				neighbor.markDirty();
			}
		}
		else if(location.z == 15)
		{
			Chunk neighbor = getChunkNeighbor(chunk, Direction.FRONT);
			if(neighbor != null)
			{
				neighbor.markDirty();
			}
		}
	}

	private boolean isChunkBorder(Vector3Int location)
	{
		return location.x == 15 || location.x == 0 ||
				location.z == 15 || location.z == 0;
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

	public ChunkLocation getLocalBlockState(Vector3Int blockLocation)
	{
		Chunk chunk = getChunkFromBlockCoordinates(blockLocation.x, blockLocation.z);
		if(chunk != null)
		{
			Vector3Int localBlockLocation = getLocalBlockLocation(blockLocation, chunk);
			return new ChunkLocation(chunk, localBlockLocation);
		}
		return null;
	}

	public Chunk getChunkFromBlockCoordinates(int blockX, int blockZ)
	{
		Vector3Int chunkLocation = getChunkFromBlockLocation(blockX, blockZ);
		return getChunkFromChunkCoordinates(chunkLocation.x, chunkLocation.z);
	}
	
	public Chunk getChunkFromChunkCoordinates(int chunkX, int chunkZ)
	{
		return chunkManager.getChunk(chunkX, chunkZ);
	}

	public Chunk getChunkNeighbor(Chunk chunk, Direction direction)
	{
		if(direction == Direction.UP || direction == Direction.DOWN)
		{
			return null;
		}
		Vector3Int chunkLocation = chunk.location.add(direction.getVector());
		return chunkManager.getChunk(chunkLocation.getX(), chunkLocation.getZ());
	}
	
	/**
	 * Returns the Chunk location given the world block x and z
	 * @param x
	 * @param z
	 * @return
	 */
	private Vector3Int getChunkFromBlockLocation(int x, int z)
	{
		Vector3Int chunkLocation = new Vector3Int();
		int chunkX = (x / 16);
		int chunkZ = (z / 16);
		if(x < 0)
		{
			chunkX -= 1;
		}
		if(z < 0)
		{
			chunkZ -= 1;
		}
		chunkLocation.set(chunkX, 0, chunkZ);
		return chunkLocation;
	}

//	public Vector3Int getLocalBlockLocation(Vector3Int blockLocation, Chunk chunk)
//	{
//		Vector3Int localLocation = new Vector3Int();
//		int localX = (blockLocation.getX() - chunk.getBlockLocation().getX());
//		int localY = (blockLocation.getY() - chunk.getBlockLocation().getY());
//		int localZ = (blockLocation.getZ() - chunk.getBlockLocation().getZ());
//		localLocation.set(localX, localY, localZ);
//		return localLocation;
//	}

	public Vector3Int getLocalBlockLocation(Vector3Int blockLocation, Chunk chunk)
	{
		Vector3Int localLocation = new Vector3Int();
		int localX = (blockLocation.getX() - chunk.getBlockLocation().getX());
//		if(localX == 16)
//		{
//			if(blockLocation.x < 0)
//			{
//				localX = 15;
//			}
//			else
//			{
//				localX = 0;
//			}
//		}
		int localY = (blockLocation.getY() - chunk.getBlockLocation().getY());
		int localZ = (blockLocation.getZ() - chunk.getBlockLocation().getZ());
//		if(localZ == 16)
//		{
//			if(blockLocation.z < 0)
//			{
//				localZ = 15;
//			}
//			else
//			{
//				localZ = 0;
//			}
//		}
		localLocation.set(localX, localY, localZ);
		return localLocation;
	}

	public int getLight(Vector3Int blockLocation)
	{
		return getLight(blockLocation, null);
	}
	
	public int getLight(Vector3Int blockLocation, LightType lightType)
	{
        if (blockLocation.hasNegativeCoordinate())
        {
            return -1;
        }
        Chunk chunk = getChunkFromBlockCoordinates(blockLocation.x, blockLocation.z);
        if (chunk != null)
        {
            Vector3Int localBlockLocation = getLocalBlockLocation(blockLocation, chunk);
            if(lightType == null)
            {
            	return chunk.getLight(localBlockLocation.x, localBlockLocation.y, localBlockLocation.z);
            }
            else
            {
            	return chunk.getLight(localBlockLocation.x, localBlockLocation.y, localBlockLocation.z, lightType);
            }
        }
		return -1;
	}
	
    public byte getBlockState(Vector3Int location)
    {
        ChunkLocation localState = getLocalBlockState(location);
        Chunk chunk = localState.getChunk();
        return chunk.getBlockState(localState.getLocalBlockLocation());
    }
    
	public void setBlockState(int x, int y, int z, byte blockState)
	{
		ChunkLocation localState = getLocalBlockState(new Vector3Int(x, y, z));
		Chunk chunk = localState.getChunk();
		Vector3Int chunkLocalLocation = localState.getLocalBlockLocation();
		chunk.setBlockState(chunkLocalLocation.x, chunkLocalLocation.y, chunkLocalLocation.z, blockState);
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

		for (int x = minX; x <= maxX; ++x)
		{
			for (int z = minZ; z <= maxZ; ++z)
			{
				for (int y = minY - 1; y <= maxY; ++y)
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
    
    public RayTrace rayTraceEntities(Vector3f position, Vector3f collisionEndPoint)
    {
    	for(Enemy enemy : enemies)
    	{
    		if(enemy.boundingBox.isVectorInside(position.subtract(collisionEndPoint)))
    		{
    			return new RayTrace(enemy);
    		}
    	}
    	return null;
    }
    
    public RayTrace rayTraceBlocks(Vector3f position, Vector3f collisionEndPoint)
    {
		if (Util.isValidVector3f(position))
		{
			if (Util.isValidVector3f(collisionEndPoint))
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
					if (!Util.isValidVector3f(position))
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
    

	public Material getBlockMaterial()
	{
		return settings.getBlockMaterial();
	}

	public void destroy()
	{
		chunkManager.destroy();
		parent.detachChild(node);
		voxelWorldSave.closeDB();
	}

	public String getName()
	{
		return name;
	}
}
