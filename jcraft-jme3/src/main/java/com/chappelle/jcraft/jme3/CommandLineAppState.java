package com.chappelle.jcraft.jme3;

import java.util.*;

import com.chappelle.jcraft.GameSettings;
import com.chappelle.jcraft.commands.CommandHandler;
import com.chappelle.jcraft.debug.DebugAppState;
import com.chappelle.jcraft.jme3.appstate.BaseInputAppState;
import com.jme3.app.*;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.TextEntryComponent;
import com.simsilica.lemur.event.*;

public class CommandLineAppState extends BaseInputAppState<JCraftApplication>
{
	private Container commandLineContainer;
	private Container messagesContainer;
	private JCraftApplication application;
	private CommandHandler commandHandler;
	private TextField textField;
	private List<String> messages = new ArrayList<String>();
	
	public CommandLineAppState(CommandHandler commandHandler)
	{
		this.commandHandler = commandHandler;
	}
	
	@Override
	protected void initialize(Application app)
	{
		super.initialize(app);
		
		this.application = (JCraftApplication)app;
		
		messagesContainer = new Container();
		messagesContainer.setInsets(new Insets3f(5, 5, 5, 5));
		commandLineContainer = new Container();
		commandLineContainer.addChild(messagesContainer);
		commandLineContainer.addChild(new Label("Command"));
		textField = new TextField("");
		textField.setPreferredWidth(GameSettings.screenWidth - 10);
		textField.getActionMap().put(new KeyAction(KeyInput.KEY_RETURN), new KeyActionListener()
		{
			@Override
			public void keyAction(TextEntryComponent source, KeyAction key)
			{
				String message = commandHandler.handleCommand(source.getText());
				if(message != null && message.trim().length() > 0)
				{
					messages.add(message);
				}
			}
		});
		
		commandLineContainer.addChild(textField);
		commandLineContainer.setLocalTranslation(0, commandLineContainer.getPreferredSize().y, 0);
		addMapping("exit", new KeyTrigger(KeyInput.KEY_ESCAPE));
	}

	@Override
	public void update(float tpf)
	{
		super.update(tpf);
		
		if(!messages.isEmpty())
		{
			for(String message : messages)
			{
				Label label = new Label(message);
				messagesContainer.addChild(label);
				commandLineContainer.setLocalTranslation(0, commandLineContainer.getPreferredSize().y, 0);
			}
			messages.clear();
		}
	}

	@Override
	public void onAction(String name, boolean isPressed, float lastTimePerFrame)
	{
		if("exit".equals(name) && !isPressed)
		{
			setEnabled(false);
		}
	}
	
	@Override
	protected void onEnable()
	{
		super.onEnable();
		
		getState(DebugAppState.class).setEnabled(false);
		getState(GameRunningAppState.class).stopListeningForInput();
		getState(GameRunningAppState.class).setPlayerEnabled(false);
		application.getGuiNode().attachChild(commandLineContainer);
		GuiGlobals.getInstance().requestFocus(textField);
	}

	@Override
	protected void onDisable()
	{
		super.onDisable();
		
		getState(DebugAppState.class).setEnabled(GameSettings.debugEnabled);
		application.getGuiNode().detachChild(commandLineContainer);
		getState(GameRunningAppState.class).setPlayerEnabled(true);
		getState(GameRunningAppState.class).startListeningForInputNextFrame();
		GuiGlobals.getInstance().requestFocus(getMyApplication().getRootNode());
		textField.setText("");
	}
}