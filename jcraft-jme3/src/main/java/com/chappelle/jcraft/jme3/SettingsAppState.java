package com.chappelle.jcraft.jme3;

import com.chappelle.jcraft.GameSettings;
import com.chappelle.jcraft.jme3.appstate.BaseInputAppState;
import com.chappelle.jcraft.jme3.ui.ClickSoundButton;
import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.*;

public class SettingsAppState extends BaseInputAppState<JCraftApplication>
{
	private Container container;
	private Label chunkRenderDistanceValueLabel;
	private Class<? extends AppState> doneAppState = BeginningAppState.class;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void initialize(Application app)
	{
		super.initialize(app);
		
		Container fieldsContainer = new Container();
		fieldsContainer.setInsets(new Insets3f(10, 10, 10, 10));
		fieldsContainer.setLayout(new SpringGridLayout());
		fieldsContainer.addChild(new Label("Settings"), 0, 0);
		Label chunkRenderDistanceLabel = new Label("Chunk Render Distance");
		chunkRenderDistanceLabel.setInsets(new Insets3f(3, 3, 3, 3));
		fieldsContainer.addChild(chunkRenderDistanceLabel, 1, 0);
		RangedValueModel chunkRenderDistanceModel = new DefaultRangedValueModel(3, 20, GameSettings.chunkRenderDistance);
		final Slider chunkRenderDistanceSlider = new Slider(chunkRenderDistanceModel, Axis.X);
		
		chunkRenderDistanceSlider.getIncrementButton().addClickCommands(new Command<Button>()
		{
			@Override
			public void execute(Button source)
			{
				GameSettings.chunkRenderDistance = (int)chunkRenderDistanceSlider.getModel().getValue();
				chunkRenderDistanceValueLabel.setText(Integer.toString(GameSettings.chunkRenderDistance));
			}
		});
		chunkRenderDistanceSlider.getDecrementButton().addClickCommands(new Command<Button>()
		{
			@Override
			public void execute(Button source)
			{
				GameSettings.chunkRenderDistance = (int)chunkRenderDistanceSlider.getModel().getValue();
				chunkRenderDistanceValueLabel.setText(Integer.toString(GameSettings.chunkRenderDistance));
			}
		});
		chunkRenderDistanceSlider.getThumbButton().addClickCommands(new Command<Button>()
		{
			@Override
			public void execute(Button source)
			{
				GameSettings.chunkRenderDistance = (int)chunkRenderDistanceSlider.getModel().getValue();
				chunkRenderDistanceValueLabel.setText(Integer.toString(GameSettings.chunkRenderDistance));
			}
		});
		
		Container sliderWithLabel = new Container();
		sliderWithLabel.setLayout(new BorderLayout());
		fieldsContainer.addChild(sliderWithLabel, 1, 1);
		sliderWithLabel.addChild(chunkRenderDistanceSlider, BorderLayout.Position.West);
		sliderWithLabel.addChild(chunkRenderDistanceValueLabel = new Label(Integer.toString(GameSettings.chunkRenderDistance)), BorderLayout.Position.East);
		container = new Container();
		container.setLayout(new BorderLayout());
		container.addChild(fieldsContainer, BorderLayout.Position.North);
		
		ClickSoundButton doneButton = new ClickSoundButton("Done");
		doneButton.addClickCommands(new Command<Button>()
		{
			@Override
			public void execute(Button source)
			{
				SettingsAppState.this.setEnabled(false);
				enableAppState(getState(doneAppState));
			}
		});
		doneButton.setInsets(new Insets3f(5, 5, 5, 5));
		doneButton.setTextHAlignment(HAlignment.Center);
		doneButton.setTextVAlignment(VAlignment.Center);
		doneButton.setPreferredSize(new Vector3f(150, 35, 0));

		Container buttonContainer = new Container();
		buttonContainer.setLayout(new BorderLayout());
		buttonContainer.addChild(doneButton, BorderLayout.Position.Center);
		container.addChild(buttonContainer, BorderLayout.Position.South);
		
		container.setLocalTranslation(GameSettings.screenWidth/2 - container.getPreferredSize().x/2, GameSettings.screenHeight/2 + container.getPreferredSize().y/2, 0);
	}

	@Override
	protected void onEnable()
	{
		super.onEnable();
		
		getMyApplication().getGuiNode().attachChild(container);
	}

	@Override
	protected void onDisable()
	{
		super.onDisable();

		getMyApplication().getGuiNode().detachChild(container);
		GameSettings.save();
	}

	public void setDoneAppState(Class<? extends AppState> doneAppState)
	{
		this.doneAppState = doneAppState;
	}

}
