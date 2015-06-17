package com.chappelle.jcraft.jme3;

import java.util.HashMap;
import java.util.Map;

import com.chappelle.jcraft.CubesSettings;
import com.chappelle.jcraft.profiler.Profiler;
import com.chappelle.jcraft.world.World;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.chappelle.jcraft.world.chunk.ChunkCoordIntPair;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

public class BlockTerrainControl extends AbstractControl
{
	private CubesSettings settings;
	
	private Map<Long, BlockChunkControl> chunks = new HashMap<Long, BlockChunkControl>();	
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
		world.update(lastTimePerFrame);
		world.calculateLight();
		updateSpatial();
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
		
		//Add chunks
		Chunk addedChunk = world.chunkRenderQueue.poll();
		if(addedChunk != null)//Game runs smoother when we load 1 chunk per frame for some reason
		{
			BlockChunkControl control = new BlockChunkControl(this, addedChunk);
			this.spatial.addControl(control);
			chunks.put(ChunkCoordIntPair.chunkXZ2Int(addedChunk.location.x, addedChunk.location.z), control);
		}
		
		//Remove chunks
		Chunk removedChunk = world.chunkUnloadQueue.poll();
		if(removedChunk != null)
		{
			long chunkKey = ChunkCoordIntPair.chunkXZ2Int(removedChunk.location.x, removedChunk.location.z);
			BlockChunkControl control = chunks.get(chunkKey);
			if(control != null)
			{
				control.detachNode();
				chunks.remove(chunkKey);
			}
		}
		
		//Update meshes
		for(BlockChunkControl chunk : chunks.values())
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
