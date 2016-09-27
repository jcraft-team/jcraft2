package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.blocks.shapes.BlockShape_Cube;
import com.chappelle.jcraft.util.math.*;
import com.chappelle.jcraft.util.physics.*;
import com.chappelle.jcraft.world.World;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.math.*;
import com.jme3.scene.Geometry;

public class Block
{
	public static final Block[] blocksList = new Block[4096];
	
	protected AABB bounds;
	/** minimum X for the block bounds (local coordinates) */
//	protected double minX;

	/** minimum Y for the block bounds (local coordinates) */
//	protected double minY;

	/** minimum Z for the block bounds (local coordinates) */
//	protected double minZ;

	/** maximum X for the block bounds (local coordinates) */
//	protected double maxX;

	/** maximum Y for the block bounds (local coordinates) */
//	protected double maxY;

	/** maximum Z for the block bounds (local coordinates) */
//	protected double maxZ;

	public float slipperiness;
	
	public float opacity = 1.0f;
	
	
	/**
	 * Light values are multiplied by each of these color components
	 */
	public ColorRGBA color = new ColorRGBA(1, 1, 1, 1);

	private BlockShape[] shapes = new BlockShape[] { new BlockShape_Cube() };
	private BlockSkin[] skins;

	/** ID of the block. */
	public final byte blockId;
	
	public String stepSound;
	
	public static final float DEFAULT_SLIPPERINESS = 0.85F;
	
	public int lightValue;
	private Sprite sprite;
	
    /**
     * Returns true if light should pass through this block, false otherwise
     */
	public boolean isTransparent;
	public boolean replacementAllowed;
	public boolean isLiquid;
	
	public boolean isClimbable;
	
	public Block(int blockId, BlockSkin... skins)
	{
		this.skins = skins;
		
		if(blockId > 255)
		{
			throw new RuntimeException("Only block ids less than 256 are supported!");
		}
		this.blockId = (byte)blockId;
		this.slipperiness = DEFAULT_SLIPPERINESS;
		blocksList[blockId] = this;
		bounds = AABB.getBoundingBox(0, 0, 0, 1, 1, 1);
//		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		
        int skinIndex = 0;
        if(skins.length < 5)
        {
            skinIndex = 0;
        }
        else
        {
            skinIndex = 4;
        }
        BlockSkin_TextureLocation textureLocation = skins[skinIndex].getTextureLocation();
        sprite = new Sprite(textureLocation.getColumn()*32, textureLocation.getRow()*32, 32, 32);
	}
	
	/**
	 * Sets the bounds of the block. minX, minY, minZ, maxX, maxY, maxZ
	 */
//	public final void setBlockBounds(float par1, float par2, float par3, float par4, float par5, float par6)
//	{
//		this.minX = (double) par1;
//		this.minY = (double) par2;
//		this.minZ = (double) par3;
//		this.maxX = (double) par4;
//		this.maxY = (double) par5;
//		this.maxZ = (double) par6;
//	}

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

	public boolean isBreakable()
	{
		return true;
	}
	
	public void onBlockPlaced(World world, Vector3Int location, Face face, Vector3f cameraDirectionAsUnitVector)
	{
		// TODO Auto-generated method stub
		
	}

	public void onBlockRemoved(World world, Vector3Int location)
	{
		// TODO Auto-generated method stub
		
	}

	public void onBlockActivated(World world, int x, int y, int z)
	{
		// TODO Auto-generated method stub
		
	}

	public void onNeighborRemoved(World world, Vector3Int location, Vector3Int neighborLocation)
	{
		// TODO Auto-generated method stub
		
	}

	public void onEntityWalking(World world, int x, int y, int z)
	{
		world.playSound(SoundConstants.STEP_GRASS_4);
	}
	
	public AABB getCollisionBoundingBox(World world, int x, int y, int z)
	{
		return bounds.getOffsetBoundingBox(x, y, z);
//		return AABB.getAABBPool().getAABB((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ, (double) x + this.maxX, (double) y + this.maxY, (double) z + this.maxZ);
	}

	public AABB getSelectedBoundingBox(World world, int x, int y, int z)
	{
		return getCollisionBoundingBox(world, x, y, z);
	}

	public boolean isOpaqueCube()
	{
		return true;
	}
	
	public Block setLightValue(int lightValue)
	{
		this.lightValue = lightValue;
		return this;
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
	
	public Block setStepSound(String stepSound)
	{
		this.stepSound = stepSound;
		return this;
	}
	
	public void setBlockBoundsBasedOnState(World world, int x, int y, int z)
	{
	}
	
	/**
	 * Ray traces through the blocks collision from start vector to end vector
	 * returning a ray trace hit.
	 */
	public RayTrace collisionRayTrace(World world, int x, int y, int z, Vector3f startVec, Vector3f endVec)
	{
		this.setBlockBoundsBasedOnState(world, x, y, z);
		startVec = startVec.add((-x), (-y), (-z));
		endVec = endVec.add((-x), (-y), (-z));
		Vector3f minXVec = MathUtils.getIntermediateWithXValue(startVec, endVec, bounds.minX);
		Vector3f maxXVec = MathUtils.getIntermediateWithXValue(startVec, endVec, bounds.maxX);
		Vector3f minYVec = MathUtils.getIntermediateWithYValue(startVec, endVec, bounds.minY);
		Vector3f maxYVec = MathUtils.getIntermediateWithYValue(startVec, endVec, bounds.maxY);
		Vector3f minZVec = MathUtils.getIntermediateWithZValue(startVec, endVec, bounds.minZ);
		Vector3f maxZVec = MathUtils.getIntermediateWithZValue(startVec, endVec, bounds.maxZ);

		if (!bounds.isVecInsideYZBounds(minXVec))
		{
			minXVec = null;
		}

		if (!bounds.isVecInsideYZBounds(maxXVec))
		{
			maxXVec = null;
		}

		if (!bounds.isVecInsideXZBounds(minYVec))
		{
			minYVec = null;
		}

		if (!bounds.isVecInsideXZBounds(maxYVec))
		{
			maxYVec = null;
		}

		if (!bounds.isVecInsideXYBounds(minZVec))
		{
			minZVec = null;
		}

		if (!bounds.isVecInsideXYBounds(maxZVec))
		{
			maxZVec = null;
		}

		Vector3f hitVec = null;

		if (minXVec != null && (hitVec == null || MathUtils.squareDistanceTo(startVec, minXVec) < MathUtils.squareDistanceTo(startVec, hitVec)))
		{
			hitVec = minXVec;
		}

		if (maxXVec != null && (hitVec == null || MathUtils.squareDistanceTo(startVec, maxXVec) < MathUtils.squareDistanceTo(startVec, hitVec)))
		{
			hitVec = maxXVec;
		}

		if (minYVec != null && (hitVec == null || MathUtils.squareDistanceTo(startVec, minYVec) < MathUtils.squareDistanceTo(startVec, hitVec)))
		{
			hitVec = minYVec;
		}

		if (maxYVec != null && (hitVec == null || MathUtils.squareDistanceTo(startVec, maxYVec) < MathUtils.squareDistanceTo(startVec, hitVec)))
		{
			hitVec = maxYVec;
		}

		if (minZVec != null && (hitVec == null || MathUtils.squareDistanceTo(startVec, minZVec) < MathUtils.squareDistanceTo(startVec, hitVec)))
		{
			hitVec = minZVec;
		}

		if (maxZVec != null && (hitVec == null || MathUtils.squareDistanceTo(startVec, maxZVec) < MathUtils.squareDistanceTo(startVec, hitVec)))
		{
			hitVec = maxZVec;
		}

		if (hitVec == null)
		{
			return null;
		} 
		else
		{
			Face sideHit = null;
			if (hitVec == minXVec)
			{
				sideHit = Face.Left;
			}

			if (hitVec == maxXVec)
			{
				sideHit = Face.Right;
			}

			if (hitVec == minYVec)
			{
				sideHit = Face.Bottom;
			}

			if (hitVec == maxYVec)
			{
				sideHit = Face.Top;
			}

			if (hitVec == minZVec)
			{
				sideHit = Face.Back;
			}

			if (hitVec == maxZVec)
			{
				sideHit = Face.Front;
			}

			return new RayTrace(x, y, z, sideHit, hitVec.add((float)x, (float)y, (float)z));
		}
	}
	
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
	{
		Block block = world.getBlock(x, y, z);
		return block == null || block.replacementAllowed;
	}
	
	public boolean canPlaceBlockOn(World world, int x, int y, int z, Face face)
	{
		Block block = world.getBlock(x, y, z);
		if(block == null)
		{
			return false;
		}
		return isValidPlacementFace(face) && block.isOpaqueCube();
	}
	
	public boolean isValidPlacementFace(Face face)
	{
		return true;
	}
	
	public int getStackSize()
	{
		return 64;
	}

	public Sprite getSprite()
	{
		return sprite;
	}
	
	public int getBlockedSkylight()
	{
		return 0;
	}
}
