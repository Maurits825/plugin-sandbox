package com.visualizer;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Visualizer"
)
public class VisualizerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private VisualizerConfig config;

	@Inject
	private VisualizerAnimation visualizerAnimation;

	@Override
	protected void startUp() throws Exception
	{
	}

	@Override
	protected void shutDown() throws Exception
	{
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			visualizerAnimation.initialize(config.size());
		}
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		visualizerAnimation.animateGrid();
	}

	@Provides
	VisualizerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(VisualizerConfig.class);
	}
}
