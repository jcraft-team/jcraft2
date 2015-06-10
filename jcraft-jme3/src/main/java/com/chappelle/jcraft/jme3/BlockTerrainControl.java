package com.chappelle.jcraft.jme3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.chappelle.jcraft.BlockChunkListener;
import com.chappelle.jcraft.Chunk;
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
	private List<BlockChunkControl> chunks = new ArrayList<>();
	private ArrayList<BlockChunkListener> chunkListeners = new ArrayList<BlockChunkListener>();
	private JCraft app;
	public World world;
	private Profiler profiler;

	public BlockTerrainControl(JCraft app, CubesSettings settings, Vector3Int chunksCount)
	{
		this.world = new World(app.getProfiler(), settings, chunksCount, app.getAssetManager(), app.getCamera());
		this.settings = settings;
		this.app = app;
		this.profiler = app.getProfiler();
	}

	public BlockTerrainControl(CubesSettings settings, Vector3Int chunksCount)
	{
		this(null, settings, chunksCount);
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
			app.enqueue(new Callable<Void>()
			{
				@Override
				public Void call() throws Exception
				{
					world.calculateLight();
					updateSpatial();
					return null;
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

	public void updateSpatial()
	{
		profiler.startSection("chunkBuilding");
		Chunk addedChunk = world.addedChunks.poll();
		while(addedChunk != null)
		{
			BlockChunkControl control = new BlockChunkControl(this, addedChunk);
			this.spatial.addControl(control);
			chunks.add(control);
			addedChunk = world.addedChunks.poll();
		}
		for(BlockChunkControl chunk : chunks)
		{
			chunk.updateSpatial();
		}
		profiler.endSection();
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

	@Override
	public BlockTerrainControl clone()
	{
		BlockTerrainControl blockTerrain = new BlockTerrainControl(app, settings, new Vector3Int());
		blockTerrain.world.setBlocksFromTerrain(this.world);
		return blockTerrain;
	}
}
