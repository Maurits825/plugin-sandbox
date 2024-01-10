package com.visualizer;

import java.awt.Color;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.JagexColor;
import net.runelite.api.ModelData;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
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
	private LocalPoint gridStartInScene;
	private PhysicBody[][] physicBodies;

	private ModelData tileModel1;
	private ModelData tileModel2;

	private long lastTime;

	private static final int TILE_ID_1 = 45510;
	private static final int TILE_ID_2 = 45432;

	public void initialize(int size)
	{
		this.gridSize = size;

		tileModel1 = client.loadModelData(TILE_ID_1);
		tileModel2 = client.loadModelData(TILE_ID_2);

		lastTime = System.nanoTime();

		tiles = new RuneLiteObject[size][size];
		physicBodies = new PhysicBody[size][size];

		for (int x = 0; x < gridSize; x++)
		{
			for (int y = 0; y < gridSize; y++)
			{
				physicBodies[x][y] = new PhysicBody(Vector.zero(), Vector.zero());
			}
		}
		wallStartPoint = VisualizerUtils.getWallStartPoint(client.getLocalPlayer().getWorldLocation(), size);
		gridStartInScene = VisualizerUtils.getWorldPointLocationInScene(client, wallStartPoint.dx(1).dy(-1));

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

		animatePhysics();
//		animateCircle();
	}

	private void animatePhysics()
	{
		double deltaTime = (System.nanoTime() - lastTime) / 1000_000_000D;
		lastTime = System.nanoTime();

		double gravity = -50;

		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
		int playerX = playerLocation.getX() - wallStartPoint.getX() - 1;
		int playerY = wallStartPoint.getY() - playerLocation.getY() - 1;

		for (int x = 0; x < gridSize; x++)
		{
			for (int y = 0; y < gridSize; y++)
			{
//				double forceY = 800D / (Math.sqrt(Math.pow(x - playerX, 4) + Math.pow(y - playerY, 4)) + 1);
				double forceY = (playerX == x && playerY == y) ? 500 : 0;
				physicBodies[x][y].applyForce(new Vector(0, forceY + gravity, 0), deltaTime);

				int tileHeight = (int) physicBodies[x][y].getPosition().y;
				ModelData tileCopy = tileModel2.shallowCopy().cloneColors().cloneVertices()
					.translate(0, -tileHeight, 0);
				int rgb = (int) VisualizerUtils.mapToRange(tileHeight, 0, 500, 20, 220);
				tileCopy.recolor(tileCopy.getFaceColors()[33],
					JagexColor.rgbToHSL(new Color(255 - rgb, rgb, 200).getRGB(), ((x + y) % 2 == 0) ? 1.0d : 1.0d));
				tiles[x][y].setModel(tileCopy.light());
			}
		}
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

				int scale = (int) VisualizerUtils.mapToRange(translateY, minAmplitude, maxAmplitude, 0, 20);
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

				int rgb = (int) VisualizerUtils.mapToRange(translateY, minAmplitude, maxAmplitude, 40, 230);
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
}
