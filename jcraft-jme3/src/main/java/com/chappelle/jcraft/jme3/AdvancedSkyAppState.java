package com.chappelle.jcraft.jme3;

import com.chappelle.jcraft.world.TimeOfDayProvider;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.*;
import com.jme3.scene.*;

import jme3utilities.*;
import jme3utilities.sky.*;

public class AdvancedSkyAppState extends BaseAppState implements ViewPortListener, TimeOfDayProvider
{
    private static final float timeRate = 100f;
    private JCraftApplication app;
    private SkyControl sky;
    private Spatial cubeMap = null;
    private Node sceneNode;
    private AssetManager assetManager;
    private Camera cam;
    private Node rootNode;
    private ViewPort viewPort;
    private TimeOfDay timeOfDay;

    public AdvancedSkyAppState(float initialTimeOfDay)
    {
        timeOfDay = new TimeOfDay(initialTimeOfDay);
        timeOfDay.setRate(timeRate);
    }
    
	@Override
	protected void initialize(Application app)
	{
        this.app = (JCraftApplication) app;
        this.viewPort = this.app.getViewPort();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.cam = this.app.getCamera();
        this.sceneNode = new Node();

        cubeMap = MyAsset.createStarMap(assetManager, "purple-nebula-complex");
        this.sky = new SkyControl(assetManager, cam, 0.2f, false, true);
        sky.setCloudiness(0.0f);
        sky.setCloudYOffset(0.4f);
        sky.setTopVerticalAngle(1.784f);
        getStateManager().attach(timeOfDay);

        sky.getSunAndStars().setObserverLatitude(0.2f);

        Updater updater = sky.getUpdater();
        updater.addViewPort(viewPort);
    }
    
    public float getTimeOfDay()
    {
    	return timeOfDay.getHour();
    }

    @Override
    public void update(float tpf)
    {
        super.update(tpf);
        float hour = timeOfDay.getHour();
        sky.getSunAndStars().setHour(hour);
        sky.getSunAndStars().orientExternalSky(cubeMap);
    }

   /**
     * Callback when a view port is added, to apply shadows to the viewport.
     *
     * @param viewPort (not null)
     */
    @Override
    public void addViewPort(ViewPort viewPort) {
        assert viewPort != null;
    }

    /**
     * Callback when a view port is removed. Does nothing.
     */
    @Override
    public void removeViewPort(ViewPort unused) {
        /* no action required */
    }

	@Override
	protected void cleanup(Application app)
	{
        this.app = null;
        this.viewPort = null;
        this.rootNode = null;
        this.assetManager = null;
        this.cam = null;
	}

	@Override
	protected void onEnable()
	{
		sceneNode.attachChild(cubeMap);
		sceneNode.addControl(sky);
		rootNode.attachChild(sceneNode);
        sky.setEnabled(true);
	}

	@Override
	protected void onDisable()
	{
		sky.setEnabled(false);
		sceneNode.removeControl(sky);
		sceneNode.detachChild(cubeMap);
		rootNode.detachChild(sceneNode);
		viewPort.setBackgroundColor(ColorRGBA.BlackNoAlpha);
	}

	@Override
	public void setTimeOfDay(float timeOfDay)
	{
		getStateManager().detach(this.timeOfDay);
		this.timeOfDay = new TimeOfDay(timeOfDay);
		getStateManager().attach(this.timeOfDay);
	}
}