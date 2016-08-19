package com.chappelle.jcraft.jme3.appstate;

import java.util.*;

import com.jme3.app.*;
import com.jme3.app.state.*;
import com.jme3.input.controls.*;

/**
 * Helpful for AppStates that need to listen for input. Ensures that it stops listening when disabled
 * and starts when enabled.
 *
 * @param <T> The type of Application
 */
public abstract class BaseInputAppState<T extends Application> extends BaseAppState implements ActionListener
{
	private List<String> actions = new ArrayList<>();
	private T myApplication;
	private int frameCountForNextInput;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void initialize(Application app)
	{
		myApplication = (T)app;
	}

	@Override
	protected void onEnable()
	{
		startListeningForInput();
	}

	@Override
	protected void onDisable()
	{
		stopListeningForInput();
	}

	@Override
	protected void cleanup(Application app)
	{
		myApplication = null;
		actions.clear();
	}

	protected T getMyApplication()
	{
		return myApplication;
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf)
	{
		//Subclasses can override
	}
	
	protected void enableAppState(AppState appState)
	{
		AppStateManager stateManager = getStateManager();
		if(!stateManager.hasState(appState))
		{
			stateManager.attach(appState);
		}
		appState.setEnabled(true);
	}
	
	protected void addMapping(String action, Trigger trigger)
	{
		getApplication().getInputManager().addMapping(action, trigger);
		actions.add(action);
	}
	
	@Override
	public void update(float tpf)
	{
		if(frameCountForNextInput == 0)
		{
			frameCountForNextInput++;
		}
		else if(frameCountForNextInput == 1)
		{
			frameCountForNextInput = -1;
			startListeningForInput();
		}
	}
	
	public void startListeningForInputNextFrame()
	{
		frameCountForNextInput = 0;
	}

	public void startListeningForInput()
	{
		getApplication().getInputManager().addListener(this, actions.toArray(new String[]{}));
	}
	
	public void stopListeningForInput()
	{
		getApplication().getInputManager().removeListener(this);
	}
}
