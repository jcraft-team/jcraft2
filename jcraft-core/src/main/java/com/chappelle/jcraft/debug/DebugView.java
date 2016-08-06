package com.chappelle.jcraft.debug;

import java.util.Map;

import com.jme3.asset.AssetManager;
import com.jme3.font.*;
import com.jme3.renderer.*;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.*;
import com.jme3.scene.control.Control;

public class DebugView extends Node implements Control
{
	private BitmapText[] labels;
	private DebugDataProvider debugDataProvider;

	private String[] debugLabels;

	private boolean enabled = true;

	private final StringBuilder stringBuilder = new StringBuilder();

	public DebugView(String name, AssetManager manager, DebugDataProvider debugDataProvider)
	{
		super(name);

		setQueueBucket(Bucket.Gui);
		setCullHint(CullHint.Never);

		this.debugDataProvider = debugDataProvider;

		debugLabels = debugDataProvider.getLabels();
		labels = new BitmapText[debugLabels.length];

		BitmapFont font = manager.loadFont("Interface/Fonts/Console.fnt");
		for(int i = 0; i < labels.length; i++)
		{
			labels[i] = new BitmapText(font);
			labels[i].setLocalTranslation(0, labels[i].getLineHeight() * (i + 1), 0);
			attachChild(labels[i]);
		}

		addControl(this);
	}

	public float getHeight()
	{
		return labels[0].getLineHeight() * debugLabels.length;
	}

	public void update(float tpf)
	{
		if(isEnabled())
		{
			Map<String, String> data = debugDataProvider.getData();
			for(int i = 0; i < labels.length; i++)
			{
				stringBuilder.setLength(0);
				stringBuilder.append(debugLabels[i]).append(": ").append(data.get(debugLabels[i]));
				labels[i].setText(stringBuilder);
			}
		}
	}

	public Control cloneForSpatial(Spatial spatial)
	{
		return (Control) spatial;
	}

	public void setSpatial(Spatial spatial)
	{
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void render(RenderManager rm, ViewPort vp)
	{
	}

}
