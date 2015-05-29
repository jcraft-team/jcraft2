package com.chappelle.jcraft.jme3;

import com.chappelle.jcraft.Block;
import com.chappelle.jcraft.BlockHelper;
import com.chappelle.jcraft.Blocks;
import com.chappelle.jcraft.CubesSettings;
import com.chappelle.jcraft.World;
import com.chappelle.jcraft.blocks.PickedBlock;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

public class PlayerControl extends NodeControl
{
	private Node playerNode = new Node("player");
	private float yAcceleration = -50.0f;
	private float yVelocity = 0;
	private Vector3f walkDirection = new Vector3f();
	private PlayerCollisionDetector collision;
	private boolean[] arrowKeys = new boolean[4];
	private boolean gravityEnabled = true;
	private BlockHelper blockHelper;
	private CubesSettings cubesSettings;
	private Block selected;
	private Camera cam;
	private World world;

	public PlayerControl(JCraft app)
	{
		this.cubesSettings = app.getCubesSettings();
		this.blockHelper = app.getBlockHelper();
		cam = app.getCamera();
		this.world = app.world;
		collision = new PlayerCollisionDetector(app.world, cubesSettings);
		selectBlock(1);
	}
	
	@Override
	protected void setNode(Node node)
	{
		node.attachChild(playerNode);
	}

	@Override
	protected void controlUpdate(float tpf)
	{
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
		
		playerNode.setLocalTranslation(playerNode.getLocalTranslation().add(walkDirection));

		if(gravityEnabled)
		{
			yVelocity = yVelocity + tpf*yAcceleration;
			float oldY = playerNode.getLocalTranslation().y;
			float newY = (oldY + tpf*yVelocity);
			float yDiff = Math.max(-2.9f, newY - oldY);
			playerNode.setLocalTranslation(playerNode.getLocalTranslation().add(0, yDiff, 0));
		}
		detectCollisions();

		cam.setLocation(playerNode.getLocalTranslation().add(0, 2, 0));
	}
	
	private void detectCollisions()
	{
		Vector3f playerLocation = playerNode.getLocalTranslation();

		//Ground block
		collision.calculate(playerLocation);
		if(collision.penetrating)
		{
			playerNode.setLocalTranslation(playerLocation.add(0, collision.yPen, 0));
			yVelocity = 0;
		}
		//Top block
		collision.calculate(playerLocation.add(0, 2, 0));
		if(collision.penetrating)
		{
			playerNode.setLocalTranslation(playerLocation.subtract(0, collision.yPen, 0));
		}
		//Front block
		collision.calculate(playerLocation.add(0, 1, 1));
		if(collision.penetrating)
		{
			playerNode.setLocalTranslation(playerLocation.subtract(0, 0, collision.zPen));
		}
		//Front top block
		collision.calculate(playerLocation.add(0, 2, 1));
		if(collision.penetrating)
		{
			playerNode.setLocalTranslation(playerLocation.subtract(0, 0, collision.zPen));
		}
		//Back block
		collision.calculate(playerLocation.add(0, 1, -1));
		if(collision.penetrating)
		{
			playerNode.setLocalTranslation(playerLocation.add(0, 0, collision.zPen));
		}
		//Right block
		collision.calculate(playerLocation.add(1, 1, 0));
		if(collision.penetrating)
		{
			playerNode.setLocalTranslation(playerLocation.subtract(collision.xPen, 0, 0));
		}
		//left block
		collision.calculate(playerLocation.add(-1, 1, 0));
		if(collision.penetrating)
		{
			playerNode.setLocalTranslation(playerLocation.add(collision.xPen, 0, 0));
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
        float distanceToDesiredPlacement = playerNode.getLocalTranslation().distance(BlockHelper.toVector(blockHelper.getPointedBlockLocationInWorldSpace()));
		return distanceToDesiredPlacement > blockSize * 1.25 && distanceToDesiredPlacement < blockSize * blockInteractionRange + 1;
    }

	public Vector3f getLocalTranslation()
	{
		return playerNode.getLocalTranslation();
	}
	
	public void setLocalTranslation(Vector3f v)
	{
		playerNode.setLocalTranslation(v);
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
    	PickedBlock pickedBlock = blockHelper.pickNeighborBlock();
    	if(pickedBlock != null)
    	{
    		if(canPlaceBlock())
    		{
    			world.setBlock(pickedBlock, selected);
//    			System.out.println("Setting block at " + pickedBlock.getBlockLocation());
    			System.out.println("blockTerrain.setBlock(" + pickedBlock.getBlockLocation().x + ", " + pickedBlock.getBlockLocation().y + ", " + pickedBlock.getBlockLocation().z + ", Blocks.GRASS);");
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
		selected = Blocks.blocks[index-1];
		System.out.println("Selected block is " + selected);
	}
	
	public Block getSelectedBlock()
	{
		return selected;
	}
}
