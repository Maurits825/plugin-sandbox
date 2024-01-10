package com.visualizer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("visualizer")
public interface VisualizerConfig extends Config
{
	@ConfigItem(
		keyName = "size",
		name = "Grid size",
		description = "The size of the grid"
	)
	default int size()
	{
		return 2;
	}
}
