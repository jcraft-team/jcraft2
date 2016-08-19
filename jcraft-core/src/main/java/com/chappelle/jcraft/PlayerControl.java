package com.chappelle.jcraft;

import com.jme3.app.SimpleApplication;
import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

public class PlayerControl extends NodeControl implements ActionListener
{
	private static final int JUMP_TIME_INTERVAL = 200;
	
	/**
	 * Used for enabling flying by double pressing space
	 */
	private long lastJumpPressed;

	private Node playerNode = new Node("player");
	private Camera cam;
	private EntityPlayer player;
	private Vector3f position = new Vector3f();
	private InputManager inputManager;

	public PlayerControl(SimpleApplication app, EntityPlayer player)
	{
		this.inputManager = app.getInputManager();
		
		cam = app.getCamera();
		
		this.player = player;
		this.player.initInventory();
		
		addMapping("move_left", new KeyTrigger(KeyInput.KEY_A));
		addMapping("move_right", new KeyTrigger(KeyInput.KEY_D));
		addMapping("move_up", new KeyTrigger(KeyInput.KEY_W));
		addMapping("move_down", new KeyTrigger(KeyInput.KEY_S));
		addMapping("jump", new KeyTrigger(KeyInput.KEY_SPACE));
		addMapping("1", new KeyTrigger(KeyInput.KEY_1));
		addMapping("2", new KeyTrigger(KeyInput.KEY_2));
		addMapping("3", new KeyTrigger(KeyInput.KEY_3));
		addMapping("4", new KeyTrigger(KeyInput.KEY_4));
		addMapping("5", new KeyTrigger(KeyInput.KEY_5));
		addMapping("6", new KeyTrigger(KeyInput.KEY_6));
		addMapping("7", new KeyTrigger(KeyInput.KEY_7));
		addMapping("8", new KeyTrigger(KeyInput.KEY_8));
		addMapping("9", new KeyTrigger(KeyInput.KEY_9));
		addMapping("0", new KeyTrigger(KeyInput.KEY_0));
		addMapping("g", new KeyTrigger(KeyInput.KEY_G));
		addMapping("RightClick", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		addMapping("LeftClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		addMapping("lshift", new KeyTrigger(KeyInput.KEY_LSHIFT));
		addMapping("lctrl", new KeyTrigger(KeyInput.KEY_LCONTROL));
		
	}
	
	private void addMapping(String action, Trigger trigger)
	{
		inputManager.addMapping(action, trigger);
		inputManager.addListener(this, action);
	}
	
	@Override
	protected void attach()
	{
		getNode().attachChild(playerNode);
	}

	@Override
	protected void detach()
	{
		getNode().detachChild(playerNode);
	}

	@Override
	protected void controlUpdate(float tpf)
	{
		player.update(tpf);
		
		position.set((float)player.posX, (float)player.posY, (float)player.posZ);
		playerNode.setLocalTranslation(position);
		cam.setLocation(playerNode.getLocalTranslation());
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf)
	{
		if(name.equals("move_up"))
		{
			player.moveForward(isPressed);
		}
		else if(name.equals("move_right"))
		{
			player.moveRight(isPressed);
		}
		else if(name.equals("move_left"))
		{
			player.moveLeft(isPressed);
		}
		else if(name.equals("move_down"))
		{
			player.moveBackward(isPressed);
		}
		else if(name.equals("jump"))
		{
			long currentTime = System.currentTimeMillis();
			long timeSinceLastPressed = currentTime - lastJumpPressed;
			if(!isPressed && timeSinceLastPressed > 0 && timeSinceLastPressed < JUMP_TIME_INTERVAL)
			{
				player.toggleFlying();
			}
			else
			{
				player.jump();
			}
			player.moveUp(isPressed);
			if(!isPressed)
			{
				lastJumpPressed = System.currentTimeMillis();
			}
		}
		else if(name.equals("lshift"))
		{
			player.moveDown(isPressed);
		}
		else if(name.equals("lctrl"))
		{
			player.setFastFlying(isPressed);
		}
		else if(name.equals("RightClick") && !isPressed)
		{
			player.placeBlock();
		}
		else if(name.equals("LeftClick") && !isPressed)
		{
			player.breakBlock();
		}
		else if(name.length() == 1 && Character.isDigit(name.charAt(0)) && !isPressed)
		{
			player.selectBlock(Integer.valueOf(name));
		}
		else if("g".equals(name) && !isPressed)
		{
			player.toggleGravity();
		}
	}
}