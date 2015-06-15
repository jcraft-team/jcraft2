package com.chappelle.jcraft.jme3;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.blocks.MeshGenerator;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

public class BlockChunkControl extends AbstractControl
{
	public BlockTerrainControl terrain;
	private Node node = new Node();
	private Geometry optimizedGeometry_Opaque;
	private Geometry optimizedGeometry_Transparent;
	public Chunk chunk;
	private MeshBuilder opaqueMeshBuilder;
	private MeshBuilder transparentMeshBuilder;
	private Future<Mesh> opaqueMeshFuture;
	private Future<Mesh> transparentMeshFuture;
	
	public BlockChunkControl(BlockTerrainControl terrain, Chunk chunk)
	{
		this.chunk = chunk;
		this.opaqueMeshBuilder = new MeshBuilder(chunk, false);
		this.transparentMeshBuilder = new MeshBuilder(chunk, true);
		this.terrain = terrain;
		Vector3Int blockLocation = chunk.getBlockLocation();
		node.setLocalTranslation(new Vector3f(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ()));
	}

	public boolean updateSpatial()
	{
		if(chunk.needsMeshUpdate)
		{
			if(opaqueMeshFuture == null && transparentMeshFuture == null)
			{
				opaqueMeshFuture = JCraft.getInstance().executor.submit(opaqueMeshBuilder);
				transparentMeshFuture = JCraft.getInstance().executor.submit(transparentMeshBuilder);
			}
			else
			{
				if(opaqueMeshFuture.isDone() && transparentMeshFuture.isDone())
				{
					if(optimizedGeometry_Opaque == null)
					{
						optimizedGeometry_Opaque = new Geometry("");
						optimizedGeometry_Opaque.setQueueBucket(Bucket.Opaque);
						node.attachChild(optimizedGeometry_Opaque);
						optimizedGeometry_Opaque.setMaterial(terrain.getSettings().getBlockMaterial());
					}
					if(optimizedGeometry_Transparent == null)
					{
						optimizedGeometry_Transparent = new Geometry("");
						optimizedGeometry_Transparent.setQueueBucket(Bucket.Transparent);
						node.attachChild(optimizedGeometry_Transparent);
						optimizedGeometry_Transparent.setMaterial(terrain.getSettings().getBlockMaterial());
					}
					try
					{
						optimizedGeometry_Opaque.setMesh(opaqueMeshFuture.get());
						optimizedGeometry_Transparent.setMesh(transparentMeshFuture.get());
					}
					catch(Exception e)
					{
						e.printStackTrace();
						//Try to generate it non threaded
						optimizedGeometry_Opaque.setMesh(MeshGenerator.generateOptimizedMesh(chunk, false));
						optimizedGeometry_Transparent.setMesh(MeshGenerator.generateOptimizedMesh(chunk, true));
					}
					opaqueMeshFuture = null;
					transparentMeshFuture = null;
					chunk.needsMeshUpdate = false;
				}
			}
			return true;
		}
		return false;
	}

	private class MeshBuilder implements Callable<Mesh>
	{
		private final Chunk chunk;
		private final boolean isTransparent;
		
		public MeshBuilder(Chunk chunk, boolean isTransparent)
		{
			this.chunk = chunk;
			this.isTransparent = isTransparent;
		}
		
		@Override
		public Mesh call() throws Exception
		{
			return MeshGenerator.generateOptimizedMesh(this.chunk, this.isTransparent);
		}
	}
	@Override
	public void setSpatial(Spatial spatial)
	{
		Spatial oldSpatial = this.spatial;
		super.setSpatial(spatial);
		if(spatial instanceof Node)
		{
			Node parentNode = (Node) spatial;
			parentNode.attachChild(node);
		}
		else if(oldSpatial instanceof Node)
		{
			Node oldNode = (Node) oldSpatial;
			oldNode.detachChild(node);
		}
	}

	@Override
	protected void controlUpdate(float lastTimePerFrame)
	{
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
}
