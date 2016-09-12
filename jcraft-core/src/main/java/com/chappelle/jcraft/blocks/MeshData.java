package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.util.Vector3fPool;

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
}
