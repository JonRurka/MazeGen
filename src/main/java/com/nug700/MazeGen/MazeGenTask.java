package com.nug700.MazeGen;

import java.util.Random;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.scheduler.*;

public class MazeGenTask extends BukkitRunnable {
	private final MazeGen plugin;
	private int xSize;
	private int ySize;
	private int zSize;
	private int smoothness;
	private int cellSize;
	private int seed;
	private Material FloorMaterial;
	private Material WallMaterial;
	private Location blockLocation;
	private BukkitScheduler scheduler;
	private MazeBuilder builder;

	public MazeGenTask(MazeGen _plugin, Location _loc, int _xSize, int _ySize, int _zSize){
		plugin = _plugin;
		scheduler = plugin.getServer().getScheduler();
		blockLocation = _loc;
		xSize = _xSize;
		ySize = _ySize;
		zSize = _zSize;
		smoothness = 1;
		cellSize = 2;
		seed = 0;
		FloorMaterial = Material.MOSSY_COBBLESTONE;
		WallMaterial = Material.SANDSTONE;
	}
	
	public void setSmoothness(int _smooth){
		smoothness = _smooth;
	}
	
	public void setCellSize(int _size){
		cellSize = _size;
	}

	public void SetSeed(int _seed){
		seed = _seed;
	}

	public void SetRandomSeed(){
		seed = new Random(System.currentTimeMillis()).nextInt();
	}
	
	public void SetFloor(Material _floor){
		FloorMaterial = _floor;
	}
	
	public void SetWall(Material _wall){
		WallMaterial = _wall;
	}
	
	@Override
	public void run() {
		builder = new MazeBuilder(xSize, ySize, zSize, smoothness, cellSize, seed);
		scheduler.runTask(plugin, new BukkitRunnable () {
			@Override
			public void run() {
				runWhenDone();
			}});
	}
	
	public void runWhenDone(){
		plugin.getServer().broadcastMessage("Maze Data Generated.");
		World world = blockLocation.getWorld();
		int xStart = blockLocation.getBlockX();
		int yStart = blockLocation.getBlockY();
		int zStart = blockLocation.getBlockZ();
		
		int xEnd = xStart + (xSize * cellSize) + 1;
		int yEnd = yStart + ySize;
		int zEnd = zStart + (zSize * cellSize) + 1;
		
		Material CurrentMaterial = Material.AIR;
		Material Air = Material.AIR;

		
		for (int xP = xStart, xL = 0; xP < xEnd; xP++, xL++){
			for (int yP = yStart, yL = 0; yP < yEnd; yP++, yL++){
				for (int zP = zStart, zL = 0; zP < zEnd; zP++, zL++){
					Block currentBlock = world.getBlockAt(xP, yP, zP);
					
					if (yL == 0){
						CurrentMaterial = FloorMaterial;
					}
					else if (builder.getValue(xL, yL, zL)){
						CurrentMaterial = WallMaterial;
					}
					else{
						CurrentMaterial = Air;
						if (xP == xEnd - 1 || zP == zEnd - 1){
							CurrentMaterial = WallMaterial;
						}
					}
					
					currentBlock.setType(CurrentMaterial);
				}
			}
		}
		plugin.getServer().broadcastMessage("Maze Created.");
		
	}
}




