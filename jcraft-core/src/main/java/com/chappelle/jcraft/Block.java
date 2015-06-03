package com.chappelle.jcraft;

import com.chappelle.jcraft.blocks.BlockIce;
import com.chappelle.jcraft.blocks.BlockStone;
import com.chappelle.jcraft.blocks.BlockDoor;
import com.chappelle.jcraft.blocks.BlockGlass;
import com.chappelle.jcraft.blocks.BlockGrass;
import com.chappelle.jcraft.blocks.PickedBlock;
import com.chappelle.jcraft.blocks.BlockTorch;
import com.chappelle.jcraft.shapes.BlockShape_Cube;
import com.chappelle.jcraft.util.AABB;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

public class Block
{
	public static final Block[] blocksList = new Block[4096];
	
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

	/** minimum X for the block bounds (local coordinates) */
	protected double minX;

	/** minimum Y for the block bounds (local coordinates) */
	protected double minY;

	/** minimum Z for the block bounds (local coordinates) */
	protected double minZ;

	/** maximum X for the block bounds (local coordinates) */
	protected double maxX;

	/** maximum Y for the block bounds (local coordinates) */
	protected double maxY;

	/** maximum Z for the block bounds (local coordinates) */
	protected double maxZ;

	public float slipperiness;
	
	public static final Block grass = new BlockGrass(1);
	public static final Block glass = new BlockGlass(2);
	public static final Block door = new BlockDoor(3, true);
	public static final Block torch = new BlockTorch(4);
	public static final Block stone = new BlockStone(5);
	public static final Block ice = new BlockIce(6);
	
	private BlockShape[] shapes = new BlockShape[] { new BlockShape_Cube() };
	private BlockSkin[] skins;

	/** ID of the block. */
	public final int blockId;
	
	public Block(int blockId, BlockSkin... skins)
	{
		this.skins = skins;
		this.blockId = blockId;
		this.slipperiness = 0.79F;
		blocksList[blockId] = this;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}
	
	/**
	 * Sets the bounds of the block. minX, minY, minZ, maxX, maxY, maxZ
	 */
	public final void setBlockBounds(float par1, float par2, float par3, float par4, float par5, float par6)
	{
		this.minX = (double) par1;
		this.minY = (double) par2;
		this.minZ = (double) par3;
		this.maxX = (double) par4;
		this.maxY = (double) par5;
		this.maxZ = (double) par6;
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

	public void onBlockPlaced(World world, Vector3Int location, Vector3f contactNormal, Vector3f cameraDirectionAsUnitVector)
	{
		// TODO Auto-generated method stub
		
	}

	public void onBlockRemoved(World world, Vector3Int location)
	{
		// TODO Auto-generated method stub
		
	}

	public void onBlockActivated(World world, PickedBlock pickedBlock)
	{
		// TODO Auto-generated method stub
		
	}

	public void onNeighborRemoved(World world, Vector3Int location, Vector3Int neighborLocation)
	{
		// TODO Auto-generated method stub
		
	}

	public void onEntityWalking(World world, Vector3Int location)
	{
		// TODO Auto-generated method stub
		
	}
	
	public AABB getCollisionBoundingBox(World world, int x, int y, int z)
	{
		return AABB.getAABBPool().getAABB((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ, (double) x + this.maxX, (double) y + this.maxY, (double) z + this.maxZ);
	}

	public AABB getSelectedBoundingBox(World world, int x, int y, int z)
	{
		return AABB.getAABBPool().getAABB((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ, (double) x + this.maxX, (double) y + this.maxY, (double) z + this.maxZ);
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
	
	public boolean isActionBlock()
	{
		return false;
	}
	
	public boolean useNeighborLight()
	{
		return true;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
	
	
}
