package com.chappelle.jcraft;

import com.chappelle.jcraft.blocks.PickedBlock;
import com.chappelle.jcraft.shapes.BlockShape_Cube;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

public class Block
{
	public static enum Face
	{
		Top, Bottom, Left, Right, Front, Back;

		public static Block.Face fromNormal(Vector3f normal)
		{
			return fromNormal(Vector3Int.fromVector3f(normal));
		}

		public static Block.Face fromNormal(Vector3Int normal)
		{
			int x = normal.getX();
			int y = normal.getY();
			int z = normal.getZ();
			if(x != 0)
			{
				if(x > 0)
				{
					return Block.Face.Right;
				}
				else
				{
					return Block.Face.Left;
				}
			}
			else if(y != 0)
			{
				if(y > 0)
				{
					return Block.Face.Top;
				}
				else
				{
					return Block.Face.Bottom;
				}
			}
			else if(z != 0)
			{
				if(z > 0)
				{
					return Block.Face.Front;
				}
				else
				{
					return Block.Face.Back;
				}
			}
			return null;
		}

	};

	protected final World terrainMgr;
	private BlockShape[] shapes = new BlockShape[] { new BlockShape_Cube() };
	private BlockSkin[] skins;

	public Block(World blockTerrainManager, BlockSkin... skins)
	{
		this.skins = skins;
		this.terrainMgr = blockTerrainManager;
	}

	public Block(BlockSkin... skins)
	{
		this.skins = skins;
		this.terrainMgr = null;
	}
	
	protected void setShapes(BlockShape... shapes)
	{
		this.shapes = shapes;
	}

	public BlockShape getShape(Chunk chunk, Vector3Int location)
	{
		return shapes[getShapeIndex(chunk, location)];
	}

	protected int getShapeIndex(Chunk chunk, Vector3Int location)
	{
		return 0;
	}

	public BlockSkin getSkin(Chunk chunk, Vector3Int location, Face face)
	{
		return skins[getSkinIndex(chunk, location, face)];
	}

	protected int getSkinIndex(Chunk chunk, Vector3Int location, Face face)
	{
		if(skins.length == 6)
		{
			return face.ordinal();
		}
		return 0;
	}

	public boolean isRemovable()
	{
		return true;
	}
	
	public boolean isAffectedByGravity()
	{
		return false;
	}
	
	public Geometry makeBlockGeometry()
	{
		return null;
	}

	public boolean isValidPlacementFace(Face face)
	{
		return true;
	}

	public void onBlockPlaced(Vector3Int location, Vector3f contactNormal, Vector3f cameraDirectionAsUnitVector)
	{
		// TODO Auto-generated method stub
		
	}

	public void onAction(PickedBlock pickedBlock)
	{
		// TODO Auto-generated method stub
		
	}

	public void onNeighborRemoved(Vector3Int location, Vector3Int neighborLocation)
	{
		// TODO Auto-generated method stub
		
	}
	
    public boolean smothersBottomBlock()
    {
        return true;
    }
    
    public boolean isSolid()
    {
    	return true;
    }

	public boolean isTransparent()
	{
		return false;
	}

	public int getBlockLightValue()
	{
		return 0;
	}
}
