package com.nug700.MazeGen;

import java.util.List;
import java.util.Random;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class MazeGen extends JavaPlugin {
	
	private static MazeGen instance; 
	MazeGenTask task;
	
	@Override
	public void onEnable(){
		instance = this;
	}
	
	@Override
	public void onDisable(){
		HandlerList.unregisterAll(this);
		if (task != null)
			task.cancel();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (cmd.getName().equalsIgnoreCase("MazeGen")){
			if (sender instanceof Player){
				if (args.length >= 3){
					try {
						Player target = (Player)sender;
						int xSize = Integer.parseInt(args[0]);
						int ySize = Integer.parseInt(args[1]);
						int zSize = Integer.parseInt(args[2]);
						int smoothness = 1;
						int cellSize = 2;
						int seed = new Random(System.currentTimeMillis()).nextInt();
						Material floorMat = Material.COBBLESTONE;
						Material wallMat = Material.SANDSTONE;
						Block targetBlock = target.getTargetBlock(null, 100);
						Location blockLocation = targetBlock.getLocation();
						if (task != null)
							task.cancel();
						task = new MazeGenTask(this, blockLocation, xSize, ySize, zSize);
						for (String arg : args){
							if (arg.contains("=")){
								String[] parts = arg.split("=");
								String key = parts[0];
								String value = parts[1];
								
								switch(key.toLowerCase()){
								case "smooth":
									smoothness = Integer.parseInt(value);
									break;
								case "cellsize":
									cellSize = Integer.parseInt(value);
									break;
								case "seed":
									seed = Integer.parseInt(value);
									break;
								case "walltype":
									wallMat = Material.getMaterial(value);
									break;
								case "floortype":
									floorMat = Material.getMaterial(value);
									break;
								default:
									break;
								}
							}
						}
						task.setSmoothness(smoothness);
						task.setCellSize(cellSize);
						task.SetSeed(seed);
						task.SetWall(wallMat);
						task.SetFloor(floorMat);
						task.runTaskAsynchronously(this);
						sender.sendMessage("Generating maze...");
					}
					catch(Exception e){
						sender.sendMessage(e.getMessage());
						return false;
					}
					return true;
				}
				else{
					sender.sendMessage("Not enough perameters");
				}
			}
			else{
				sender.sendMessage("This command cannot be called by the console.");
			}
		}
		
		return false;
	}
	
	/*public void GenerateMaze(Location loc, int xSize, int ySize, int zSize, int smoothness, int cellSize, int seed){
		BukkitTask task = new MazeGenTask(this, loc, xSize, ySize, zSize, smoothness, cellSize, seed).runTaskAsynchronously(this);
	}*/
	
	public static MazeGen getInstance(){
		return instance;
	}
}
