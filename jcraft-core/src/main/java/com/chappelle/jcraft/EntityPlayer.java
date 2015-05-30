package com.chappelle.jcraft;

import com.chappelle.jcraft.blocks.PickedBlock;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class EntityPlayer extends Entity
{
	private float yAcceleration = -50.0f;
	private float yVelocity = 0;
	private Vector3f walkDirection = new Vector3f();
	private PlayerCollisionDetector collision;
	private boolean[] arrowKeys = new boolean[4];
	private boolean gravityEnabled = true;
	private BlockHelper blockHelper;
	private CubesSettings cubesSettings;
	private Block selected;
	private Vector3Int currentBlockLocation;

	private Camera cam;
	
	public EntityPlayer(World world, Camera cam, BlockHelper blockHelper)
	{
		super(world);
		
		this.cam = cam;
		this.blockHelper = blockHelper;
		this.cubesSettings = CubesSettings.getInstance();
		this.collision = new PlayerCollisionDetector(world, cubesSettings);

		selectBlock(1);
	}
	
	@Override
	public void update(float tpf)
	{
		currentBlockLocation = getStandingBlockLocation();
		float playerMoveSpeed = ((cubesSettings.getBlockSize() * 6.5f) * tpf);
		Vector3f camDir = cam.getDirection().mult(playerMoveSpeed);
		Vector3f camLeft = cam.getLeft().mult(playerMoveSpeed);
		walkDirection.set(0, 0, 0);
		if(arrowKeys[0])
		{
			walkDirection.addLocal(camDir);
		}
		if(arrowKeys[1])
		{
			walkDirection.addLocal(camLeft.negate());
		}
		if(arrowKeys[2])
		{
			walkDirection.addLocal(camDir.negate());
		}
		if(arrowKeys[3])
		{
			walkDirection.addLocal(camLeft);
		}
		walkDirection.setY(0);
		
		pos.addLocal(walkDirection);

		if(gravityEnabled)
		{
			yVelocity = yVelocity + tpf*yAcceleration;
			float oldY = pos.y;
			float newY = (oldY + tpf*yVelocity);
			float yDiff = Math.max(-2.9f, newY - oldY);
			pos.addLocal(0, yDiff, 0);
		}
		detectCollisions();

		cam.setLocation(pos.add(0, 2, 0));
		Vector3Int newBlockLocation = getStandingBlockLocation();
		if(newBlockLocation.x != currentBlockLocation.x || newBlockLocation.z != currentBlockLocation.z)
		{
			Block block = getStandingBlock();
			if(block != null)
			{
				block.onEntityWalking(world, newBlockLocation);
			}
			currentBlockLocation = newBlockLocation;
		}

	}
	
	private void detectCollisions()
	{
		//Ground block
		collision.calculate(pos);
		if(collision.penetrating)
		{
			pos.addLocal(0, collision.yPen, 0);
			yVelocity = 0;
		}
		//Top block
		collision.calculate(pos.add(0, 2, 0));
		if(collision.penetrating)
		{
			pos.subtractLocal(0, collision.yPen, 0);
		}
		//Front block
		collision.calculate(pos.add(0, 1, 1));
		if(collision.penetrating)
		{
			pos.subtractLocal(0, 0, collision.zPen);
		}
		//Front top block
		collision.calculate(pos.add(0, 2, 1));
		if(collision.penetrating)
		{
			pos.subtractLocal(0, 0, collision.zPen);
		}
		//Back block
		collision.calculate(pos.add(0, 1, -1));
		if(collision.penetrating)
		{
			pos.addLocal(0, 0, collision.zPen);
		}
		//Right block
		collision.calculate(pos.add(1, 1, 0));
		if(collision.penetrating)
		{
			pos.subtractLocal(collision.xPen, 0, 0);
		}
		//left block
		collision.calculate(pos.add(-1, 1, 0));
		if(collision.penetrating)
		{
			pos.addLocal(collision.xPen, 0, 0);
		}
	}
	

	public void jump()
	{
		if(yVelocity == 0)
		{
			yVelocity = 20.0f;
		}
	}
	
    public boolean canPlaceBlock()
    {
    	int blockInteractionRange = 5;
    	float blockSize = cubesSettings.getBlockSize();
        float distanceToDesiredPlacement = pos.distance(BlockHelper.toVector(blockHelper.getPointedBlockLocationInWorldSpace()));
		return distanceToDesiredPlacement > blockSize * 1.25 && distanceToDesiredPlacement < blockSize * blockInteractionRange + 1;
    }

	public void toggleGravity()
	{
		gravityEnabled = !gravityEnabled;
	}

	public void breakBlock()
	{
		PickedBlock pickedBlock = blockHelper.pickBlock();
		if(pickedBlock != null)
		{
			System.out.println("Removing block at " + pickedBlock.getBlockLocation());
			world.removeBlock(pickedBlock.getBlockLocation());
		}
	}
	
	public void placeBlock()
	{
    	PickedBlock pickedBlock = blockHelper.pickBlock();
    	if(pickedBlock != null)
    	{
    		Block block = pickedBlock.getBlock();
			if(block != null && block.isActionBlock())
    		{
    			block.onBlockActivated(world, pickedBlock);
    		}
			else if(canPlaceBlock())
    		{
				pickedBlock = blockHelper.pickNeighborBlock();
    			world.setBlock(pickedBlock, selected);
    			System.out.println("Setting block at " + pickedBlock.getBlockLocation());
//    			System.out.println("blockTerrain.setBlock(" + pickedBlock.getBlockLocation().x + ", " + pickedBlock.getBlockLocation().y + ", " + pickedBlock.getBlockLocation().z + ", Blocks.GRASS);");
    		}
    	}
	}
	
	public void moveUp(boolean isPressed)
	{
		arrowKeys[0] = isPressed;
	}
	
	public void moveRight(boolean isPressed)
	{
		arrowKeys[1] = isPressed;
	}
	
	public void moveDown(boolean isPressed)
	{
		arrowKeys[2] = isPressed;
	}
	
	public void moveLeft(boolean isPressed)
	{
		arrowKeys[3] = isPressed;
	}
	
	public void selectBlock(int index)
	{
		selected = Block.blocksList[index];
		System.out.println("Selected block is " + selected.getClass().getName());
	}
	
	public Block getSelectedBlock()
	{
		return selected;
	}
	
	/**
	 * Returns the Block the player is standing on
	 */
	public Block getStandingBlock()
	{
		Vector3Int location = getStandingBlockLocation();
		if(location != null)
		{
			return world.getBlock(location);
		}
		return null;
	}
	
	public Vector3Int getStandingBlockLocation()
	{
		Vector3Int result = Vector3Int.fromVector3f(pos.divide(CubesSettings.getInstance().getBlockSize()));
		if(result != null)
		{
			result.subtractLocal(0,2,0);
		}
		return result;
	}

}
