package com.chappelle.jcraft.jme3;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import com.chappelle.jcraft.BlockChunkListener;
import com.chappelle.jcraft.CubesSettings;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.World;
import com.chappelle.jcraft.profiler.Profiler;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

public class BlockTerrainControl extends AbstractControl
{
	private CubesSettings settings;
	private BlockChunkControl[][][] chunks;
	private ArrayList<BlockChunkListener> chunkListeners = new ArrayList<BlockChunkListener>();
	private JCraft app;
	public World world;
	private Profiler profiler;

	public BlockTerrainControl(JCraft app, CubesSettings settings, Vector3Int chunksCount)
	{
		this.world = new World(app.getProfiler(), settings, chunksCount);
		this.settings = settings;
		this.app = app;
		this.profiler = app.getProfiler();
		initializeChunks(world, chunksCount);
	}

	public BlockTerrainControl(CubesSettings settings, Vector3Int chunksCount)
	{
		this(null, settings, chunksCount);
	}

	private void initializeChunks(World world, Vector3Int chunksCount)
	{
		chunks = new BlockChunkControl[chunksCount.getX()][chunksCount.getY()][chunksCount.getZ()];
		for(int x = 0; x < chunks.length; x++)
		{
			for(int y = 0; y < chunks[0].length; y++)
			{
				for(int z = 0; z < chunks[0][0].length; z++)
				{
					chunks[x][y][z] = new BlockChunkControl(this, world.chunks[x][y][z]);
				}
			}
		}
	}

	@Override
	public void setSpatial(Spatial spatial)
	{
		Spatial oldSpatial = this.spatial;
		super.setSpatial(spatial);
		for(int x = 0; x < chunks.length; x++)
		{
			for(int y = 0; y < chunks[0].length; y++)
			{
				for(int z = 0; z < chunks[0][0].length; z++)
				{
					if(spatial == null)
					{
						oldSpatial.removeControl(chunks[x][y][z]);
					}
					else
					{
						spatial.addControl(chunks[x][y][z]);
					}
				}
			}
		}
	}

	@Override
	protected void controlUpdate(float lastTimePerFrame)
	{
		if(app == null)
		{
			world.calculateLight();
			updateSpatial();
		}
		else
		{
			// Runs in a separate thread
			app.enqueue(new Callable<Boolean>()
			{
				@Override
				public Boolean call() throws Exception
				{
					world.calculateLight();
					return updateSpatial();
				}
			});
		}
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort)
	{

	}

	@Override
	public Control cloneForSpatial(Spatial spatial)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean updateSpatial()
	{
		profiler.startSection("chunkBuilding");
		boolean wasUpdatedNeeded = false;
		for(int x = 0; x < chunks.length; x++)
		{
			for(int y = 0; y < chunks[0].length; y++)
			{
				for(int z = 0; z < chunks[0][0].length; z++)
				{
					BlockChunkControl chunkControl = chunks[x][y][z];
					if(chunkControl.updateSpatial())
					{
						wasUpdatedNeeded = true;
						for(int i = 0; i < chunkListeners.size(); i++)
						{
							BlockChunkListener blockTerrainListener = chunkListeners.get(i);
							blockTerrainListener.onSpatialUpdated(chunkControl.chunk);
						}
					}
				}
			}
		}
		profiler.endSection();
		return wasUpdatedNeeded;
	}

	public void updateBlockMaterial()
	{
		for(int x = 0; x < chunks.length; x++)
		{
			for(int y = 0; y < chunks[0].length; y++)
			{
				for(int z = 0; z < chunks[0][0].length; z++)
				{
					chunks[x][y][z].updateBlockMaterial();
				}
			}
		}
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

	public BlockChunkControl[][][] getChunks()
	{
		return chunks;
	}

	@Override
	public BlockTerrainControl clone()
	{
		BlockTerrainControl blockTerrain = new BlockTerrainControl(app, settings, new Vector3Int());
		blockTerrain.world.setBlocksFromTerrain(this.world);
		return blockTerrain;
	}
}
