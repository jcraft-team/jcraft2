package com.chappelle.jcraft.jme3;

import com.chappelle.jcraft.EntityPlayer;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

public class PlayerControl extends NodeControl
{
	private Node playerNode = new Node("player");
	private Camera cam;
	private EntityPlayer player;
	private Vector3f position = new Vector3f();

	public PlayerControl(JCraft app, EntityPlayer player)
	{
		cam = app.getCamera();
		
		this.player = player;
	}
	
	@Override
	protected void setNode(Node node)
	{
		node.attachChild(playerNode);
	}

	@Override
	protected void controlUpdate(float tpf)
	{
		player.update(tpf);
		
		position.set((float)player.posX, (float)player.posY, (float)player.posZ);
		playerNode.setLocalTranslation(position);
		cam.setLocation(playerNode.getLocalTranslation());
	}
}