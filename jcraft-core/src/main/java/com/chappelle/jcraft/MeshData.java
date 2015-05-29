package com.chappelle.jcraft;

import java.util.LinkedList;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class MeshData
{
    public LinkedList<Vector3f> positionsList = new LinkedList<Vector3f>();
    public LinkedList<Short> indicesList = new LinkedList<Short>();
    public LinkedList<Float> normalsList = new LinkedList<Float>();
    public LinkedList<Vector2f> textureCoordinatesList = new LinkedList<Vector2f>();
    public LinkedList<Float> colorList = new LinkedList<>();
}
