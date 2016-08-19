package com.chappelle.jcraft.jme3;

import com.chappelle.jcraft.world.TimeOfDayProvider;
import com.jme3.app.Application;
import com.jme3.app.state.*;
import com.jme3.asset.AssetManager;
import com.jme3.light.*;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.*;
import com.jme3.scene.*;
import com.jme3.shadow.*;
import com.jme3.texture.*;

import jme3utilities.*;
import jme3utilities.sky.*;

public class AdvancedSkyAppState extends BaseAppState implements ViewPortListener, TimeOfDayProvider
{
    private static final float timeRate = 100f;
    /**
     * width and height of rendered shadow maps (pixels per side, &gt;0)
     */
    final private static int shadowMapSize = 512;
    /**
     * number of shadow map splits (&gt;0)
     */
    final private static int shadowMapSplits = 3;
    private JCraftApplication app;
    private SkyControl sky;
    private Spatial cubeMap = null;
    private Node sceneNode;
    private AssetManager assetManager;
    private Camera cam;
    private Node rootNode;
    private ViewPort viewPort;
    private TimeOfDay timeOfDay;

    private AmbientLight ambientLight = null;
    private DirectionalLight mainLight = null;

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

        initializeLights();
        cubeMap = MyAsset.createStarMap(assetManager, "purple-nebula-complex");
        this.sky = new SkyControl(assetManager, cam, 0.9f, true, true);
//        sceneNode.attachChild(cubeMap);
//        sceneNode.addControl(sky);
//        rootNode.attachChild(sceneNode);
        sky.clearStarMaps();
        sky.setCloudiness(0.8f);
        sky.setCloudModulation(true);

        Texture moonTexture = MyAsset.loadTexture(assetManager, "Textures/skies/moon/clementine.png");
        Material moonMaterial = MyAsset.createShadedMaterial(assetManager, moonTexture);
        int equatorSamples = 12;
        int meridianSamples = 24;
        int resolution = 512;
        GlobeRenderer moonRenderer = new GlobeRenderer(moonMaterial,Image.Format.Luminance8Alpha8, equatorSamples, meridianSamples, resolution);
        getStateManager().attach(moonRenderer);
        sky.setMoonRenderer(moonRenderer);

        getStateManager().attach(timeOfDay);

        for (Light light : rootNode.getLocalLightList())
        {
            if (light.getName().equals("ambient"))
            {
                sky.getUpdater().setAmbientLight((AmbientLight) light);
            }
            else if (light.getName().equals("main"))
            {
                sky.getUpdater().setMainLight((DirectionalLight) light);
            }
        }
        sky.getSunAndStars().setObserverLatitude(0.2f);


        Updater updater = sky.getUpdater();

        updater.addViewPort(viewPort);
        updater.setAmbientLight(ambientLight);
        updater.setMainLight(mainLight);
    }
    
    public float getTimeOfDay()
    {
    	return timeOfDay.getHour();
    }

    private void initializeLights()
    {
        mainLight = new DirectionalLight();
        mainLight.setName("main");

        ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White.mult(1.3f));
        ambientLight.setName("ambient");
    }

    @Override
    public void update(float tpf)
    {
        super.update(tpf);
        float hour = timeOfDay.getHour();
        sky.getSunAndStars().setHour(hour);
        sky.getSunAndStars().orientExternalSky(cubeMap);
    }

   private void addShadows(ViewPort viewPort)
   {
        boolean shadowFilter = false;


        Updater updater = sky.getUpdater();
        if (shadowFilter)
        {
            DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, shadowMapSize, shadowMapSplits);
            dlsf.setEdgeFilteringMode(EdgeFilteringMode.PCF8);
            dlsf.setLight(mainLight);
            Misc.getFpp(viewPort, assetManager).addFilter(dlsf);
            updater.addShadowFilter(dlsf);

        }
        else
        {
            DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, shadowMapSize, shadowMapSplits);
            dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCF8);
            dlsr.setLight(mainLight);
            updater.addShadowRenderer(dlsr);
            viewPort.addProcessor(dlsr);
        }
    }
   /**
     * Callback when a view port is added, to apply shadows to the viewport.
     *
     * @param viewPort (not null)
     */
    @Override
    public void addViewPort(ViewPort viewPort) {
        assert viewPort != null;
        addShadows(viewPort);
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
	}

	@Override
	public void setTimeOfDay(float timeOfDay)
	{
		getStateManager().detach(this.timeOfDay);
		this.timeOfDay = new TimeOfDay(timeOfDay);
		getStateManager().attach(this.timeOfDay);
	}
}