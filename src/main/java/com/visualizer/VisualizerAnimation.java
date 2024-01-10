package com.visualizer;

import java.awt.Color;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.JagexColor;
import net.runelite.api.ModelData;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.WorldPoint;

public class VisualizerAnimation
{
	@Inject
	private Client client;
	@Inject
	private RuneliteObjectUtil rlUtils;

	private int gridSize;
	private RuneLiteObject[][] tiles;
	private WorldPoint wallStartPoint;

	private ModelData tileModel1;
	private ModelData tileModel2;

	private static final int TILE_ID_1 = 45510;
	private static final int TILE_ID_2 = 45432;

	public void initialize(int size)
	{
		this.gridSize = size;

		tileModel1 = client.loadModelData(TILE_ID_1);
		tileModel2 = client.loadModelData(TILE_ID_2);

		tiles = new RuneLiteObject[size][size];
		wallStartPoint = VisualizerAnimation.getWallStartPoint(client.getLocalPlayer().getWorldLocation(), size);

		spawnGridTiles();
	}

	public void reset()
	{
		clearTiles();
	}

	public void animateGrid()
	{
		if (tiles == null)
		{
			return;
		}

		animateCircle();
	}

	private void animateCircle()
	{
		int amplitude = 200;
		int offset = 1000;
		double period = 100d;
		double periodToSee = 1.4d;
		double speed = 1 / 50d;
		double t = (System.currentTimeMillis() * speed) % period;

		double minAmplitude = -amplitude - (amplitude / 2d) - offset;
		double maxAmplitude = amplitude - (amplitude / 2d) - offset;
		for (int x = 0; x < gridSize; x++)
		{
			for (int y = 0; y < gridSize; y++)
			{
//				double xWeight = x + y;
				double xWeight = Math.sqrt(Math.pow(x - gridSize / 2f, 2) + Math.pow(y - gridSize / 2f, 2));
//				double xWeight = Math.sqrt(Math.pow(x - 15, 2) + Math.pow(y - 15, 2));
				double xInterval = (periodToSee * period) / gridSize;
				double input = (t + (xWeight * xInterval)) * 2d * Math.PI * (1.0d / period);
				int translateY = (int) (amplitude * Math.sin(input)) - (amplitude / 2) - offset;

//				log.debug(String.valueOf(translateY));
				ModelData tileCopy;

				int scale = (int) mapToRange(translateY, minAmplitude, maxAmplitude, 0, 20);
				tileCopy = tileModel2.shallowCopy().cloneColors().cloneVertices()
					.translate(0, translateY, 0).scale(128 + scale, 128, 128 + scale);
//				if ((x + y) % 2 == 0)
//				{
//					tileCopy = tileModel1.shallowCopy().cloneColors().cloneVertices().translate(0, translateY, 0);
//				}
//				else
//				{
//					tileCopy = tileModel2.shallowCopy().cloneColors().cloneVertices().translate(0, translateY, 0);
//				}

				int rgb = (int) mapToRange(translateY, minAmplitude, maxAmplitude, 40, 230);
				tileCopy.recolor(tileCopy.getFaceColors()[33],
					JagexColor.rgbToHSL(new Color(255 - rgb, rgb, 150).getRGB(), ((x + y) % 2 == 0) ? 1.0d : 1d));
				tiles[x][y].setModel(tileCopy.light());
//				int angle = (int) mapToRange(translateY, minAmplitude, maxAmplitude, 0, 2047 / 4);
				int angle = 0;
				tiles[x][y].setOrientation(angle);
			}
		}
	}

	private void spawnGridTiles()
	{
		int tileObjectId;
		for (int x = 0; x < gridSize; x++)
		{
			for (int y = 0; y < gridSize; y++)
			{
				if ((x + y) % 2 == 0)
				{
					tileObjectId = TILE_ID_1;
				}
				else
				{
					tileObjectId = TILE_ID_2;
				}

				tiles[x][y] = rlUtils.spawnGridTileObject(wallStartPoint.dx(x + 1).dy(-(y + 1)), tileObjectId);
			}
		}
	}

	private void clearTiles()
	{
		if (tiles == null)
		{
			return;
		}

		for (int x = 0; x < gridSize; x++)
		{
			for (int y = 0; y < gridSize; y++)
			{
				if (tiles[x][y] != null)
				{
					tiles[x][y].setActive(false);
				}
			}
		}

		tiles = null;
	}

	private static double mapToRange(double value, double inputMin, double inputMax, double outputMin, double outputMax)
	{
		value = Math.min(Math.max(value, inputMin), inputMax);
		double normalizedValue = (value - inputMin) / (inputMax - inputMin);

		return outputMin + normalizedValue * (outputMax - outputMin);
	}

	private static WorldPoint getWallStartPoint(WorldPoint playerWorldPosition, int gameSize)
	{
		int offset = (int) Math.ceil(gameSize / 2.0f);
		return playerWorldPosition.dx(-offset).dy(offset);
	}
}
