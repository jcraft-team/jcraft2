package com.chappelle.jcraft.jme3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.chappelle.jcraft.CubesSettings;
import com.chappelle.jcraft.profiler.Profiler;
import com.chappelle.jcraft.world.World;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

public class BlockTerrainControl extends AbstractControl
{
	private CubesSettings settings;
	private List<BlockChunkControl> chunks = new ArrayList<>();
	private JCraft app;
	public World world;
	private Profiler profiler;

	public BlockTerrainControl(JCraft app, CubesSettings settings, World world)
	{
		this.world = world;
		this.settings = settings;
		this.app = app;
		this.profiler = app.getProfiler();
	}

	@Override
	protected void controlUpdate(final float lastTimePerFrame)
	{
		app.enqueue(new Callable<Void>()
		{
			@Override
			public Void call() throws Exception
			{
				world.update(lastTimePerFrame);
				world.calculateLight();
				updateSpatial();
				return null;
			}
		});
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
		profiler.startSection("ChunkSpatial");
		Chunk addedChunk = world.chunkRenderQueue.poll();
		while(addedChunk != null)
		{
			BlockChunkControl control = new BlockChunkControl(this, addedChunk);
			this.spatial.addControl(control);
			chunks.add(control);
			addedChunk = world.chunkRenderQueue.poll();
		}
		for(BlockChunkControl chunk : chunks)
		{
			chunk.updateSpatial();
		}
		profiler.endSection();
	}

	public CubesSettings getSettings()
	{
		return settings;
	}

	@Override
	public BlockTerrainControl clone()
	{
		BlockTerrainControl blockTerrain = new BlockTerrainControl(app, settings, this.world);
		blockTerrain.world.setBlocksFromTerrain(this.world);
		return blockTerrain;
	}
}
