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

	float[] lighting = new float[]
			{
			0.1f, //12am 
			0.1f, //1am
			0.1f, //2am
			0.1f, //3am
			0.2f, //4am
			0.4f, //5am
			0.5f, //6am
			0.65f, //7am
			0.75f,//8am
			0.85f,//9am
			1.0f,//10am
			1.0f,//11am
			1.0f,//12pm
			1.0f,//1pm
			1.0f,//2pm
			1.0f,//3pm
			1.0f,//4pm
			0.9f,//5pm
			0.8f,//6pm
			0.7f,//7pm
			0.5f,//8pm
			0.1f,//9pm
			0.1f,//10pm
			0.1f};//11pm
	
	private float calculateDayNightLighting(float hour)
	{
		int morningStart = 6;
		int morningEnd = 8;
		int nightStart = 17;
		int nightEnd = 19;
		float darkest = 0.2f;
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
	
	public void updateSpatial()
	{
		profiler.startSection("ChunkSpatial");
		
		EnvironmentAppState env = app.getStateManager().getState(EnvironmentAppState.class);
		if(env != null)
		{
			float hour = env.getTimeOfDay().getHour();
			float dayNightLighting = calculateDayNightLighting(hour);
			for(BlockChunkControl chunk : chunks.values())
			{
				chunk.setDayNightLighting(dayNightLighting);
			}
		}
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
