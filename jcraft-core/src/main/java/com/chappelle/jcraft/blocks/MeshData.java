package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.util.math.Vector3fPool;
import com.jme3.scene.*;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import gnu.trove.list.*;
import gnu.trove.list.array.*;

public class MeshData
{
    public TFloatList positionsList = new TFloatArrayList();
    public TShortList indicesList = new TShortArrayList();
    public TFloatList normalsList = new TFloatArrayList();
    public TFloatList textureCoordinatesList = new TFloatArrayList();
    public TFloatList colorList = new TFloatArrayList();
    public Vector3fPool vec3Pool = new Vector3fPool();
    
	public Mesh toMesh()
	{
		float[] positions = positionsList.toArray();
		short[] indices = indicesList.toArray();
		float[] textureCoordinates = textureCoordinatesList.toArray();
		float[] normals = normalsList.toArray();
        float[] color = colorList.toArray();

		Mesh mesh = new Mesh();
		mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(positions));
		mesh.setBuffer(Type.Index, 1, BufferUtils.createShortBuffer(indices));
		mesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
		mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(textureCoordinates));
		mesh.setBuffer(VertexBuffer.Type.Color, 4, BufferUtils.createFloatBuffer(color));
		mesh.updateBound();
		return mesh;
	}
    
}
