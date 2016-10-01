package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.blocks.shapes.BlockShape_Cube;
import com.chappelle.jcraft.util.math.*;
import com.chappelle.jcraft.util.physics.*;
import com.chappelle.jcraft.world.World;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.math.*;

public class Block
{
	public static final Block[] blocksList = new Block[4096];
	
	protected AABB bounds = AABB.getBoundingBox(0, 0, 0, 1, 1, 1);

	public float slipperiness = DEFAULT_SLIPPERINESS;
	
	public float opacity = 1.0f;
	
	/**
	 * Light values are multiplied by each of these color components
	 */
	private ColorRGBA color = new ColorRGBA(1, 1, 1, 1);

	private BlockShape[] shapes = new BlockShape[] { new BlockShape_Cube() };
	private Skin[] skins;

	/** ID of the block. */
	public final byte blockId;
	
	private boolean selectable = true;
	private boolean collidable = true;
	private boolean breakable = true;
	
	public String stepSound;
	
	public static final float DEFAULT_SLIPPERINESS = 0.85F;
	
	public int lightValue;
	private boolean useNeighborLight = true;
	private int blockedSkylight;
	private int stackSize;
	
    /**
     * Returns true if light should pass through this block, false otherwise
     */
	public boolean isTransparent;
	public boolean replacementAllowed;
	public boolean isLiquid;
	
	public boolean isClimbable;
	
	public Block(int blockId, int textureRow, int textureColumn)
	{
		this(blockId, new Skin(textureRow, textureColumn, false));
	}
	
	public Block(int blockId, Skin... skins)
	{
		this.skins = skins;
		this.blockId = (byte)blockId;
		blocksList[blockId] = this;
	}
	
	public Block setShapes(BlockShape... shapes)
	{
		this.shapes = shapes;
		return this;
	}

	public BlockShape getShape(Chunk chunk, Vector3Int location)
	{
		return shapes[getShapeIndex(chunk, location)];
	}

	protected int getShapeIndex(Chunk chunk, Vector3Int location)
	{
		return 0;
	}

	public Skin getSkin(Chunk chunk, Vector3Int location, Face face)
	{
		return skins[getSkinIndex(chunk, location, face)];
	}

	protected int getSkinIndex(Chunk chunk, Vector3Int location, Face face)
	{
		if(skins.length == 6 || face.ordinal() < skins.length - 1)
		{
			return face.ordinal();
		}
		return skins.length - 1;
	}

	public boolean isBreakable()
	{
		return breakable;
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
		return collidable ? bounds.getOffsetBoundingBox(x, y, z) : null; 
	}

	public AABB getSelectedBoundingBox(World world, int x, int y, int z)
	{
		return selectable ? bounds.getOffsetBoundingBox(x, y, z) : null;
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
		return useNeighborLight;
	}
	
	public Block setUseNeighborLight(boolean useNeighborLight)
	{
		this.useNeighborLight = useNeighborLight;
		return this;
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
	
	public Block bounds(AABB bounds)
	{
		this.bounds = bounds;
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
		if(collidable == false)
		{
			return null;
		}
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
		return stackSize;
	}

	public int getBlockedSkylight()
	{
		return blockedSkylight;
	}
	
	public Block setTransparent(boolean transparent)
	{
		this.isTransparent = transparent;
		return this;
	}

	public ColorRGBA getColor()
	{
		return color;
	}

	public Block setColor(ColorRGBA color)
	{
		this.color = color;
		return this;
	}
	
	public Block setBreakable(boolean breakable)
	{
		this.breakable = breakable;
		return this;
	}

	public Block setReplacementAllowed(boolean replacementAllowed)
	{
		this.replacementAllowed = replacementAllowed;
		return this;
	}

	public Block setLiquid(boolean liquid)
	{
		this.isLiquid = liquid;
		return this;
	}
	
	public Block setBlockedSkylight(int blockedSkylight)
	{
		this.blockedSkylight = blockedSkylight;
		return this;
	}
	
	public Block setSelectable(boolean selectable)
	{
		this.selectable = selectable;
		return this;
	}

	public Block setCollidable(boolean collidable)
	{
		this.collidable = collidable;
		return this;
	}
	
	public Block setSlipperiness(float slipperiness)
	{
		this.slipperiness = slipperiness;
		return this;
	}
}
