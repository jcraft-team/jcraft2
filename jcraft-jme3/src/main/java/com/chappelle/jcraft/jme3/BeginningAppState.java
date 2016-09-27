package com.chappelle.jcraft.jme3;

import java.util.*;

import org.slf4j.*;
import org.terasology.core.world.generator.facetProviders.*;
import org.terasology.core.world.generator.rasterizers.*;
import org.terasology.world.generation.*;
import org.terasology.world.generator.plugin.WorldGeneratorPluginLibrary;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.jme3.appstate.BaseInputAppState;
import com.chappelle.jcraft.jme3.ui.ClickSoundButton;
import com.chappelle.jcraft.serialization.VoxelWorldSave;
import com.chappelle.jcraft.util.Context;
import com.chappelle.jcraft.world.World;
import com.chappelle.jcraft.world.gen.*;
import com.google.common.collect.*;
import com.jme3.app.*;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.*;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.SpringGridLayout;

public class BeginningAppState extends BaseInputAppState<JCraftApplication>
{
	private final static Logger log = LoggerFactory.getLogger(BeginningAppState.class.getName());

	boolean isGuiShowing = false;
	private Container beginningOptionsContainer;
	
	public World world;
	protected EntityPlayer player;
	
	private SettingsAppState settingsAppState;
	private Context context;
	private WorldGeneratorPluginLibrary pluginLibrary;
	private final List<WorldRasterizer> rasterizers = Lists.newArrayList();
	private final List<FacetProvider> providersList = Lists.newArrayList();
	private final Set<Class<? extends WorldFacet>> facetCalculationInProgress = Sets.newHashSet();

	public BeginningAppState(Context context)
	{
		this.context = context;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void initialize(Application app)
	{
		super.initialize(app);
		
		settingsAppState = new SettingsAppState();
		app.getStateManager().attach(new OptionPanelState());

		beginningOptionsContainer = new Container();
		beginningOptionsContainer.setLayout(new SpringGridLayout());
		
		beginningOptionsContainer.addChild(new Label("JCraft"));
		Button startGame = beginningOptionsContainer.addChild(new ClickSoundButton("Start Game"));
		startGame.setInsets(new Insets3f(5, 5, 5, 5));
		startGame.setTextHAlignment(HAlignment.Center);
		startGame.setTextVAlignment(VAlignment.Center);
		startGame.setPreferredSize(new Vector3f(500, 35, 0));
		startGame.addClickCommands(new Command<Button>()
		{
			@Override
			public void execute(Button source)
			{
				startGame();
			}
		});
		Button settingsButton = beginningOptionsContainer.addChild(new ClickSoundButton("Settings"));
		settingsButton.setInsets(new Insets3f(5, 5, 5, 5));
		settingsButton.setTextHAlignment(HAlignment.Center);
		settingsButton.setTextVAlignment(VAlignment.Center);
		settingsButton.setPreferredSize(new Vector3f(500, 35, 0));
		settingsButton.addClickCommands(new Command<Button>()
		{
			@Override
			public void execute(Button source)
			{
				showSettings();
			}
		});
		Button exitGame = beginningOptionsContainer.addChild(new ClickSoundButton("Quit Game"));
		exitGame.setInsets(new Insets3f(5, 5, 5, 5));
		exitGame.setTextHAlignment(HAlignment.Center);
		exitGame.setTextVAlignment(VAlignment.Center);
		exitGame.setPreferredSize(new Vector3f(500, 35, 0));
		exitGame.addClickCommands(new Command<Button>()
		{
			@Override
			public void execute(Button source)
			{
				getMyApplication().stop();
			}
		});
		beginningOptionsContainer.setLocalTranslation(GameSettings.screenWidth/2 - beginningOptionsContainer.getPreferredSize().x/2, GameSettings.screenHeight/2 + beginningOptionsContainer.getPreferredSize().y/2, 0);		
	}
	
    protected void showSettings()
	{
    	setEnabled(false);
    	
    	settingsAppState.setDoneAppState(BeginningAppState.class);
    	safeSetEnableAppState(settingsAppState, true);
	}

	protected AudioNode makeAudio(String location)
    {
        AudioNode result = new AudioNode(getApplication().getAssetManager(), location, AudioData.DataType.Buffer);
        result.setReverbEnabled(false);
        result.setVolume(2.0f);
        result.setPositional(false);
        return result;
    }


	private void startGame()
	{
		long seed = getSeed();
		
		providersList.add(new SeaLevelProvider(40));
		providersList.add(new PerlinHumidityProvider());
		providersList.add(new PerlinSurfaceTemperatureProvider());
		providersList.add(new PerlinBaseSurfaceProvider());
		providersList.add(new PerlinRiverProvider());
		providersList.add(new PerlinOceanProvider());
		providersList.add(new PerlinHillsAndMountainsProvider());
		providersList.add(new BiomeProvider());
		providersList.add(new SurfaceToDensityProvider());
		providersList.add(new DefaultFloraProvider());
		providersList.add(new DefaultTreeProvider());
		providersList.add(new MySurfaceProvider());
		rasterizers.add(new SolidRasterizer());
		rasterizers.add(new FloraRasterizer());
		rasterizers.add(new TreeRasterizer());
		rasterizers.add(new BedrockRasterizer());
		for(FacetProvider provider : providersList)
		{
			provider.setSeed(seed);
		}
		ListMultimap<Class<? extends WorldFacet>, FacetProvider> providerChains = determineProviderChains();
		for(WorldRasterizer rasterizer : rasterizers)
		{
			rasterizer.initialize();
		}
		context.put(ChunkGenerator.class, new FacetBasedChunkGenerator(providerChains, rasterizers, determineBorders(providerChains)));
//		context.put(ChunkGenerator.class, new ChunkGeneratorImpl(seed, getFeatures()));

		log.info("Using world seed: " + seed);
		log.info("Creating new world...get ready!");
		world = new World(getApplication(), context, "JCraftWorld", seed);

		Camera camera = getMyApplication().getCamera();
		player = new EntityPlayer(world, camera);
		// Setup player
		Vector3f playerLocation = (Vector3f)context.get(VoxelWorldSave.class).getGameData("playerLocation");
		if(playerLocation != null)
		{
			player.setPosition(playerLocation.x, playerLocation.y, playerLocation.z);
		}

		
		AppStateManager stateManager = getMyApplication().getStateManager();
		BeginningAppState.this.setEnabled(false);
		stateManager.attach(new LoadingAppState(new WorldLoadingCallable(world, player, getMyApplication().getSettings())));
	}

    private ListMultimap<Class<? extends WorldFacet>, FacetProvider> determineProviderChains() {
        ListMultimap<Class<? extends WorldFacet>, FacetProvider> result = ArrayListMultimap.create();
        Set<Class<? extends WorldFacet>> facets = new LinkedHashSet<>();
        for (FacetProvider provider : providersList) {
            Produces produces = provider.getClass().getAnnotation(Produces.class);
            if (produces != null) {
                facets.addAll(Arrays.asList(produces.value()));
            }
            Updates updates = provider.getClass().getAnnotation(Updates.class);
            if (updates != null) {
                for (Facet facet : updates.value()) {
                    facets.add(facet.value());
                }
            }
        }
        for (Class<? extends WorldFacet> facet : facets) {
            determineProviderChainFor(facet, result);
            if (log.isDebugEnabled()) {
                StringBuilder text = new StringBuilder(facet.getSimpleName());
                text.append(" --> ");
                Iterator<FacetProvider> it = result.get(facet).iterator();
                while (it.hasNext()) {
                    text.append(it.next().getClass().getSimpleName());
                    if (it.hasNext()) {
                        text.append(", ");
                    }
                }
                log.debug(text.toString());
            }
        }

        return result;
    }

    private Map<Class<? extends WorldFacet>, Border3D> determineBorders(ListMultimap<Class<? extends WorldFacet>, FacetProvider> providerChains) {
        Map<Class<? extends WorldFacet>, Border3D> borders = Maps.newHashMap();

        for (Class<? extends WorldFacet> facet : providerChains.keySet()) {
            ensureBorderCalculatedForFacet(facet, providerChains, borders);
        }

        return borders;
    }

    private void ensureBorderCalculatedForFacet(Class<? extends WorldFacet> facet, ListMultimap<Class<? extends WorldFacet>, FacetProvider> providerChains,
                                                Map<Class<? extends WorldFacet>, Border3D> borders) {

        if (!borders.containsKey(facet)) {

            Border3D border = new Border3D(0, 0, 0);
            int maxSide = 0;
            int maxTop = 0;
            int maxBottom = 0;
            for (FacetProvider facetProvider : providerChains.values()) {
                // Find all facets that require it
                Requires requires = facetProvider.getClass().getAnnotation(Requires.class);
                Produces produces = facetProvider.getClass().getAnnotation(Produces.class);
                Updates updates = facetProvider.getClass().getAnnotation(Updates.class);
                if (requires != null) {
                    for (Facet requiredFacet : requires.value()) {
                        if (requiredFacet.value() == facet) {


                            FacetBorder requiredBorder = requiredFacet.border();

                            if (produces != null) {
                                for (Class<? extends WorldFacet> producedFacet : produces.value()) {
                                    ensureBorderCalculatedForFacet(producedFacet, providerChains, borders);
                                    Border3D borderForProducedFacet = borders.get(producedFacet);
                                    border = border.maxWith(
                                            borderForProducedFacet.getTop() + requiredBorder.top(),
                                            borderForProducedFacet.getBottom() + requiredBorder.bottom(),
                                            borderForProducedFacet.getSides() + requiredBorder.sides());
                                }
                            }
                            if (updates != null) {
                                for (Facet producedFacetAnnotation : updates.value()) {
                                    Class<? extends WorldFacet> producedFacet = producedFacetAnnotation.value();
                                    FacetBorder borderForFacetAnnotation = producedFacetAnnotation.border();
                                    ensureBorderCalculatedForFacet(producedFacet, providerChains, borders);
                                    Border3D borderForProducedFacet = borders.get(producedFacet);
                                    border = border.maxWith(
                                            borderForProducedFacet.getTop() + requiredBorder.top() + borderForFacetAnnotation.top(),
                                            borderForProducedFacet.getBottom() + requiredBorder.bottom() + borderForFacetAnnotation.bottom(),
                                            borderForProducedFacet.getSides() + requiredBorder.sides() + borderForFacetAnnotation.sides());
                                }
                            }
                        }
                    }
                }
//Get biggest border for facet?! Create an array of borders and search for maximum.
// Check if there are update annotation for facet, if there are search for biggest border requested from providers and replace value
                if(updates != null) {
                    for (Facet producedFacetAnnotation : updates.value()) {
                        if (producedFacetAnnotation.value() == facet) {

                            FacetBorder borderForFacetAnnotation = producedFacetAnnotation.border();
                            if (maxSide < borderForFacetAnnotation.sides()) {
                                maxSide = borderForFacetAnnotation.sides();
                            }
                            if (maxTop < borderForFacetAnnotation.top()) {
                                maxTop = borderForFacetAnnotation.top();
                            }
                            if (maxBottom < borderForFacetAnnotation.bottom()) {
                                maxBottom = borderForFacetAnnotation.bottom();
                            }

                        }

                    }

                    border = border.maxWith(maxTop, maxBottom, maxSide);
                }
            }
            borders.put(facet, border);
        }
    }

    private void determineProviderChainFor(Class<? extends WorldFacet> facet, ListMultimap<Class<? extends WorldFacet>, FacetProvider> result) {
        if (result.containsKey(facet)) {
            return;
        }
        if (!facetCalculationInProgress.add(facet)) {
            throw new RuntimeException("Circular dependency detected when calculating facet provider ordering for " + facet);
        }
        Set<FacetProvider> orderedProviders = Sets.newLinkedHashSet();

        // first add all @Produces facet providers
        FacetProvider producer = null;
        for (FacetProvider provider : providersList) {
            if (producesFacet(provider, facet)) {
                if (producer != null) {
                    log.warn("Facet already produced by {} and overwritten by {}", producer, provider);
                }
                // add all required facets for producing provider
                for (Facet requirement : requiredFacets(provider)) {
                    determineProviderChainFor(requirement.value(), result);
                    orderedProviders.addAll(result.get(requirement.value()));
                }
                // add all updated facets for producing provider
                for (Facet updated : updatedFacets(provider)) {
                    determineProviderChainFor(updated.value(), result);
                    orderedProviders.addAll(result.get(updated.value()));
                }
                orderedProviders.add(provider);
                producer = provider;
            }
        }

        if (producer == null) {
            log.warn("No facet provider found that produces {}", facet);
        }

        // then add all @Updates facet providers
        providersList.stream().filter(provider -> updatesFacet(provider, facet)).forEach(provider -> {
            // add all required facets for updating provider
            for (Facet requirement : requiredFacets(provider)) {
                determineProviderChainFor(requirement.value(), result);
                orderedProviders.addAll(result.get(requirement.value()));
            }
            // the provider updates this and other facets
            // just add producers for the other facets
            for (Facet updated : updatedFacets(provider)) {
                for (FacetProvider fp : providersList) {
                    // only add @Produces providers to avoid infinite recursion
                    if (producesFacet(fp, updated.value())) {
                        orderedProviders.add(fp);
                    }
                }
            }
            orderedProviders.add(provider);
        });
        result.putAll(facet, orderedProviders);
        facetCalculationInProgress.remove(facet);
    }

    private Facet[] requiredFacets(FacetProvider provider) {
        Requires requirements = provider.getClass().getAnnotation(Requires.class);
        if (requirements != null) {
            return requirements.value();
        }
        return new Facet[0];
    }

    private Facet[] updatedFacets(FacetProvider provider) {
        Updates updates = provider.getClass().getAnnotation(Updates.class);
        if (updates != null) {
            return updates.value();
        }
        return new Facet[0];
    }

    private boolean producesFacet(FacetProvider provider, Class<? extends WorldFacet> facet) {
        Produces produces = provider.getClass().getAnnotation(Produces.class);
        if (produces != null && Arrays.asList(produces.value()).contains(facet)) {
            return true;
        }
        return false;
    }

    private boolean updatesFacet(FacetProvider provider, Class<? extends WorldFacet> facet) {
        Updates updates = provider.getClass().getAnnotation(Updates.class);
        if (updates != null) {
            for (Facet updatedFacet : updates.value()) {
                if (updatedFacet.value() == facet) {
                    return true;
                }
            }
        }
        return false;
    }

	private long getSeed()
	{
		long seed = System.currentTimeMillis();
		String seedStr = System.getProperty("seed");
		if(seedStr != null)
		{
			seed = Long.parseLong(seedStr);
		}
		return seed;
	}

	private List<Feature> getFeatures()
	{
		String featureProviderId = System.getProperty("featureProvider");
		if(featureProviderId == null)
		{
			featureProviderId = "core:default";
		}
		FeatureProvider featureProvider = getFeatureProviders().get(featureProviderId);
		if(featureProvider == null)
		{
			throw new IllegalStateException("No featureProvider with id=" + featureProviderId);
		}
		List<Feature> result = new ArrayList<>();
		result.addAll(featureProvider.getFeatures());
		return result;
	}

	private Map<String, FeatureProvider> getFeatureProviders()
	{
		Map<String, FeatureProvider> result = new HashMap<>();
		Iterator<FeatureProvider> featureProviderIter = ServiceLoader.load(FeatureProvider.class).iterator();
		while(featureProviderIter.hasNext())
		{
			FeatureProvider featureProvider = featureProviderIter.next();
			
			result.put(featureProvider.getId(), featureProvider);
		}		
		return result;
	}
	
	@Override
	protected void onEnable()
	{
		super.onEnable();
		
		disableAppState(getState(StatsAppState.class));
		
		getMyApplication().getGuiNode().attachChild(beginningOptionsContainer);
		
	}

	@Override
	protected void onDisable()
	{
		super.onDisable();
		
		getMyApplication().getGuiNode().detachChild(beginningOptionsContainer);
	}
}