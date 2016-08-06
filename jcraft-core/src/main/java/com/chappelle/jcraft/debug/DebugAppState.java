package com.chappelle.jcraft.debug;

import com.chappelle.jcraft.BlockApplication;
import com.jme3.app.*;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Quad;

/**
 * Displays debug information in the corner when enabled
 */
public class DebugAppState extends AbstractAppState
{
	private BlockApplication app;
	protected DebugView debugView;
	protected boolean showSettings = true;
	private boolean showFps = true;
	private boolean showStats = true;
	private boolean darkenBehind = true;

	protected Node guiNode;
	protected float secondCounter = 0.0f;
	protected int frameCounter = 0;
	protected BitmapText fpsText;
	protected BitmapFont guiFont;
	protected Geometry darkenFps;
	protected Geometry darkenStats;

	public DebugAppState()
	{
	}

	public DebugAppState(Node guiNode, BitmapFont guiFont)
	{
		this.guiNode = guiNode;
		this.guiFont = guiFont;
	}

	/**
	 * Called by SimpleApplication to provide an early font so that the fpsText
	 * can be created before init. This is because several applications expect
	 * to directly access fpsText... unfortunately.
	 */
	public void setFont(BitmapFont guiFont)
	{
		this.guiFont = guiFont;
		this.fpsText = new BitmapText(guiFont, false);
	}

	public BitmapText getFpsText()
	{
		return fpsText;
	}

	public DebugView getStatsView()
	{
		return debugView;
	}

	public float getSecondCounter()
	{
		return secondCounter;
	}

	public void toggleStats()
	{
		setDisplayFps(!showFps);
		setDisplayStatView(!showStats);
	}

	public void setDisplayFps(boolean show)
	{
		showFps = show;
		if(fpsText != null)
		{
			fpsText.setCullHint(show ? CullHint.Never : CullHint.Always);
			if(darkenFps != null)
			{
				darkenFps.setCullHint(showFps && darkenBehind ? CullHint.Never : CullHint.Always);
			}

		}
	}

	public void setDisplayStatView(boolean show)
	{
		showStats = show;
		if(debugView != null)
		{
			debugView.setEnabled(show);
			debugView.setCullHint(show ? CullHint.Never : CullHint.Always);
			if(darkenStats != null)
			{
				darkenStats.setCullHint(showStats && darkenBehind ? CullHint.Never : CullHint.Always);
			}
		}
	}

	public void setDarkenBehind(boolean darkenBehind)
	{
		this.darkenBehind = darkenBehind;
		setEnabled(isEnabled());
	}

	public boolean isDarkenBehind()
	{
		return darkenBehind;
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app)
	{
		super.initialize(stateManager, app);

		if(!(app instanceof BlockApplication))
		{
			throw new IllegalArgumentException("DebugAppState is only compatible with a BlockApplication");
		}

		disableStatsAppState(stateManager);

		this.app = (BlockApplication) app;

		if(guiNode == null)
		{
			guiNode = this.app.getGuiNode();
		}
		if(guiFont == null)
		{
			guiFont = this.app.getGuiFont();
		}

		if(guiNode == null)
		{
			throw new RuntimeException("No guiNode specific and cannot be automatically determined.");
		}

		if(guiFont == null)
		{
			guiFont = app.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
		}

		loadFpsText();
		loadDebugView();
		loadDarken();
	}

	private void disableStatsAppState(AppStateManager stateManager)
	{
		StatsAppState statsAppState = stateManager.getState(StatsAppState.class);
		if(statsAppState != null)
		{
			statsAppState.setEnabled(false);
		}
	}

	/**
	 * Attaches FPS statistics to guiNode and displays it on the screen.
	 *
	 */
	public void loadFpsText()
	{
		if(fpsText == null)
		{
			fpsText = new BitmapText(guiFont, false);
		}

		fpsText.setLocalTranslation(0, fpsText.getLineHeight(), 0);
		fpsText.setText("Frames per second");
		fpsText.setCullHint(showFps ? CullHint.Never : CullHint.Always);
		guiNode.attachChild(fpsText);

	}

	/**
	 * Attaches Debug View to guiNode and displays it on the screen above FPS
	 * statistics line.
	 *
	 */
	public void loadDebugView()
	{
		debugView = new DebugView("Debug View", app.getAssetManager(), app.getDebugDataProvider());
		// move it up so it appears above fps text
		debugView.setLocalTranslation(0, fpsText.getLineHeight(), 0);
		debugView.setEnabled(showStats);
		debugView.setCullHint(showStats ? CullHint.Never : CullHint.Always);
		guiNode.attachChild(debugView);
	}

	public void loadDarken()
	{
		Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", new ColorRGBA(0, 0, 0, 0.5f));
		mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);

		darkenFps = new Geometry("StatsDarken", new Quad(200, fpsText.getLineHeight()));
		darkenFps.setMaterial(mat);
		darkenFps.setLocalTranslation(0, 0, -1);
		darkenFps.setCullHint(showFps && darkenBehind ? CullHint.Never : CullHint.Always);
		guiNode.attachChild(darkenFps);

		darkenStats = new Geometry("StatsDarken", new Quad(200, debugView.getHeight()));
		darkenStats.setMaterial(mat);
		darkenStats.setLocalTranslation(0, fpsText.getHeight(), -1);
		darkenStats.setCullHint(showStats && darkenBehind ? CullHint.Never : CullHint.Always);
		guiNode.attachChild(darkenStats);
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);

		if(enabled)
		{
			fpsText.setCullHint(showFps ? CullHint.Never : CullHint.Always);
			darkenFps.setCullHint(showFps && darkenBehind ? CullHint.Never : CullHint.Always);
			debugView.setEnabled(showStats);
			debugView.setCullHint(showStats ? CullHint.Never : CullHint.Always);
			darkenStats.setCullHint(showStats && darkenBehind ? CullHint.Never : CullHint.Always);
		}
		else
		{
			fpsText.setCullHint(CullHint.Always);
			darkenFps.setCullHint(CullHint.Always);
			debugView.setEnabled(false);
			debugView.setCullHint(CullHint.Always);
			darkenStats.setCullHint(CullHint.Always);
		}
	}

	@Override
	public void update(float tpf)
	{
		if(showFps)
		{
			secondCounter += app.getTimer().getTimePerFrame();
			frameCounter++;
			if(secondCounter >= 1.0f)
			{
				int fps = (int) (frameCounter / secondCounter);
				fpsText.setText("Frames per second: " + fps);
				secondCounter = 0.0f;
				frameCounter = 0;
			}
		}
	}

	@Override
	public void cleanup()
	{
		super.cleanup();

		guiNode.detachChild(debugView);
		guiNode.detachChild(fpsText);
		guiNode.detachChild(darkenFps);
		guiNode.detachChild(darkenStats);
	}

}
