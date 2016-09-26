package com.chappelle.jcraft.world.chunk;

import com.jme3.material.Material;
import com.jme3.scene.Mesh;

public class ChunkMesh
{
	private final Mesh opaqueMesh;
	private final Mesh transparentMesh;
	private final Material chunkMaterial;
	
	public ChunkMesh(Mesh opaqueMesh, Mesh transparentMesh, Material chunkMaterial)
	{
		this.opaqueMesh = opaqueMesh;
		this.transparentMesh = transparentMesh;
		this.chunkMaterial = chunkMaterial;
	}

	public Mesh getOpaqueMesh()
	{
		return opaqueMesh;
	}

	public Mesh getTransparentMesh()
	{
		return transparentMesh;
	}

	public Material getChunkMaterial()
	{
		return chunkMaterial;
	}
}
