package com.chappelle.jcraft.debug;

import com.jme3.app.*;
import com.jme3.app.state.*;
import com.jme3.font.*;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.*;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;

/**
 * Displays debug information in the corner when enabled
 */
public class DebugAppState extends AbstractAppState
{
	private static final int DEBUG_WIDTH = 375;
	private SimpleApplication app;
	protected DebugView debugView;
	protected boolean showSettings = true;
	private boolean showFps = false;
	private boolean showStats = true;
	private boolean darkenBehind = true;

	protected Node guiNode;
	protected float secondCounter = 0.0f;
	protected int frameCounter = 0;
	protected BitmapText fpsText;
	protected BitmapFont guiFont;
	protected Geometry darkenFps;
	protected Geometry darkenStats;
	private float width;
	private float height;
	private AppSettings settings;
	private StatsAppState stats;
	private DebugDataProvider debugDataProvider;
	
	public DebugAppState(AppSettings appSettings, DebugDataProvider debugDataProvider)
	{
		this.debugDataProvider = debugDataProvider;
		this.settings = appSettings;
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

		if(!(app instanceof SimpleApplication))
		{
			throw new IllegalArgumentException("DebugAppState is only compatible with a SimpleApplication");
		}

		this.app = (SimpleApplication) app;
		this.stats = stateManager.getState(StatsAppState.class);
		this.width = settings.getWidth();
		this.height = settings.getHeight();

		if(guiNode == null)
		{
			guiNode = this.app.getGuiNode();
		}
		guiFont = app.getAssetManager().loadFont("Interface/Fonts/Default.fnt");

		if(guiNode == null)
		{
			throw new RuntimeException("No guiNode specific and cannot be automatically determined.");
		}

		loadFpsText();
		loadDebugView();
		loadDarken();
		positionElements();
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

		fpsText.setText("Frames per second");
		fpsText.setCullHint(showFps ? CullHint.Never : CullHint.Always);
		guiNode.attachChild(fpsText);

	}

	private void positionElements()
	{
		fpsText.setLocalTranslation(0, fpsText.getLineHeight() + getHeightOffset(), 0);
		// move it up so it appears above fps text
		debugView.setLocalTranslation(0, fpsText.getLineHeight() + getHeightOffset(), 0);
		darkenFps.setLocalTranslation(0, 0 + getHeightOffset(), -1);
		darkenStats.setLocalTranslation(0, fpsText.getHeight() + getHeightOffset(), -1);
	}
	
	/**
	 * Attaches Debug View to guiNode and displays it on the screen above FPS
	 * statistics line.
	 *
	 */
	public void loadDebugView()
	{
		debugView = new DebugView("Debug View", app.getAssetManager(), debugDataProvider);
		debugView.setEnabled(showStats);
		debugView.setCullHint(showStats ? CullHint.Never : CullHint.Always);
		guiNode.attachChild(debugView);
	}

	public void loadDarken()
	{
		Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", new ColorRGBA(0, 0, 0, 0.5f));
		mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);

		darkenFps = new Geometry("StatsDarken", new Quad(DEBUG_WIDTH, fpsText.getLineHeight()));
		darkenFps.setMaterial(mat);
		darkenFps.setCullHint(showFps && darkenBehind ? CullHint.Never : CullHint.Always);
		guiNode.attachChild(darkenFps);

		darkenStats = new Geometry("StatsDarken", new Quad(DEBUG_WIDTH, debugView.getHeight()));
		darkenStats.setMaterial(mat);
		darkenStats.setCullHint(showStats && darkenBehind ? CullHint.Never : CullHint.Always);
		guiNode.attachChild(darkenStats);
	}
	
	private float getHeightOffset()
	{
		return height - (fpsText.getLineHeight() + debugView.getHeight());
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);

		setStatsEnabled(enabled);
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

	private void setStatsEnabled(boolean enabled)
	{
		if(stats != null)
		{
			stats.setEnabled(enabled);
		}
	}

	@Override
	public void update(float tpf)
	{
		if(settings.getWidth() != width || settings.getHeight() != height)//May have toggled fullscreen mode
		{
			width = settings.getWidth();
			height = settings.getHeight();
			positionElements();
		}
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
