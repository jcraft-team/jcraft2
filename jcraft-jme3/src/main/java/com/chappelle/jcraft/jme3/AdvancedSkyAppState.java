package com.chappelle.jcraft.jme3;

import jme3utilities.Misc;
import jme3utilities.MyAsset;
import jme3utilities.TimeOfDay;
import jme3utilities.ViewPortListener;
import jme3utilities.sky.GlobeRenderer;
import jme3utilities.sky.SkyControl;
import jme3utilities.sky.Updater;

import com.chappelle.jcraft.world.TimeOfDayProvider;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;

public class AdvancedSkyAppState extends AbstractAppState implements ViewPortListener, TimeOfDayProvider
{
    private float initialTimeOfDay = 6;
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
    	this.initialTimeOfDay = initialTimeOfDay;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app)
    {
        super.initialize(stateManager, app);

        this.app = (JCraftApplication) app;
        this.viewPort = this.app.getViewPort();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.cam = this.app.getCamera();
        this.sceneNode = new Node();

        initializeLights();
        cubeMap = MyAsset.createStarMap(assetManager, "purple-nebula-complex");
        sceneNode.attachChild(cubeMap);
        this.sky = new SkyControl(assetManager, cam, 0.9f, true, true);
        sceneNode.addControl(sky);
        rootNode.attachChild(sceneNode);
        sky.clearStarMaps();
        sky.setCloudiness(0.8f);
        sky.setCloudModulation(true);

        Texture moonTexture = MyAsset.loadTexture(assetManager, "Textures/skies/moon/clementine.png");
        Material moonMaterial = MyAsset.createShadedMaterial(assetManager, moonTexture);
        int equatorSamples = 12;
        int meridianSamples = 24;
        int resolution = 512;
        GlobeRenderer moonRenderer = new GlobeRenderer(moonMaterial,Image.Format.Luminance8Alpha8, equatorSamples, meridianSamples, resolution);
        stateManager.attach(moonRenderer);
        sky.setMoonRenderer(moonRenderer);

        timeOfDay = new TimeOfDay(initialTimeOfDay);
        stateManager.attach(timeOfDay);
        timeOfDay.setRate(timeRate);

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
        this.sky.setEnabled(true);
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
    }}
