package com.visualizer;

import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class VisualizerUtils
{
	public static double mapToRange(double value, double inputMin, double inputMax, double outputMin, double outputMax)
	{
		value = Math.min(Math.max(value, inputMin), inputMax);
		double normalizedValue = (value - inputMin) / (inputMax - inputMin);

		return outputMin + normalizedValue * (outputMax - outputMin);
	}

	public static WorldPoint getWallStartPoint(WorldPoint playerWorldPosition, int gameSize)
	{
		int offset = (int) Math.ceil(gameSize / 2.0f);
		return playerWorldPosition.dx(-offset).dy(offset);
	}

	public static LocalPoint getWorldPointLocationInScene(Client client, WorldPoint worldPoint)
	{
		Tile[][][] tiles = client.getScene().getTiles();
		int z = client.getPlane();
		for (int x = 0; x < Constants.SCENE_SIZE; ++x)
		{
			for (int y = 0; y < Constants.SCENE_SIZE; ++y)
			{
				Tile tile = tiles[z][x][y];
				if (tile.getWorldLocation().equals(worldPoint))
				{
					return new LocalPoint(x, y);
				}
			}
		}
		return null;
	}
}
