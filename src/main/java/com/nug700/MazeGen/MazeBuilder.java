package com.nug700.MazeGen;

import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.util.Random;

import org.bukkit.scheduler.BukkitRunnable;

public final class MazeBuilder{
	private enum Direction{
		N ((byte)1),
		W ((byte)2);
		
		private final byte index;

		Direction(byte _index){
			index = _index;
		}
		
		public byte getIndex(){
			return index;
		}
	}
	
	public final class Maze {
		Stack s_stack;
		Random rand;
		
		public int MSizeX;
		public int MSizeY;
		public int MSizeZ;
		public int[] maze_base;
		public byte[][] maze_data;
		
		private int iSmooth;
		
		// --- Generating ---
		public void GenerateMaze(int sizeX, int sizeY, int sizeZ, int seed, int smoothness){
			iSmooth = smoothness;
			MSizeX = sizeX;
			MSizeY = sizeY;
			MSizeZ = sizeZ;
			maze_base = new int[MSizeX * MSizeZ];
			maze_data = new byte[MSizeX][MSizeZ];
			
			s_stack = new Stack();
			rand = new Random(seed);
			
			MazeInit(rand);
			
			cMazeState state = new cMazeState(rand.nextInt(2147483647) % MSizeX, rand.nextInt(2147483647) % MSizeZ, 0);
			analyze_cell(state, rand);
		}
		void analyze_cell(cMazeState s, Random r){
			boolean bEnd = false, found;
			int indexSrc, indexDest, tDir = 0, prevDir = 0;
			
			while(true){
				if (s.dir == 15){
					while (s.dir == 15){
						s = (cMazeState)s_stack.pop();
						if (s == null){
							bEnd = true;
							break;
						}
					}
					if (bEnd == true) break;
				}
				else{
					try {
						do{
							prevDir = tDir;
							tDir = (int)Math.pow(2, r.nextInt(2147483647) % 4);
							
							if ((r.nextInt() % 32) < iSmooth)
								if ((s.dir & prevDir) == 0)
									tDir = prevDir;
							
							if ((s.dir & tDir) != 0)
								found = true;
							else 
								found = false;
							
						} while (found == true && s.dir != 15);
						
						s.dir |= tDir;
						
						indexSrc = cell_index(s.x, s.z);
						
						// Dir W
						if (tDir == 1 && s.x > 0){
							indexDest = cell_index(s.x - 1, s.z);
							if (base_cell(indexSrc) != base_cell(indexDest)){
								merge(indexSrc, indexDest);
								maze_data[s.x][s.z] |= Direction.W.getIndex();
								
								s_stack.push(new cMazeState(s));
								s.x -= 1; s.dir = 0;
							}
						}
						
						// Dir E
						if (tDir == 2 && s.x < MSizeX - 1){
							indexDest = cell_index(s.x + 1, s.z);
							if (base_cell(indexSrc) != base_cell(indexDest)){
								merge(indexSrc, indexDest);
								maze_data[s.x + 1][s.z] |= Direction.W.getIndex();
								
								s_stack.push(new cMazeState(s));
								s.x += 1; s.dir = 0;
							}
						}
						
						// Dir N
						if (tDir == 4 && s.z > 0){
							indexDest = cell_index(s.x, s.z - 1);
							if (base_cell(indexSrc) != base_cell(indexDest)){
								merge(indexSrc, indexDest);
								maze_data[s.x][s.z] |= Direction.N.getIndex();
								
								s_stack.push(new cMazeState(s));
								s.z -= 1; s.dir = 0;
							}
						}
						
						// Dir S
						if (tDir == 8 && s.z < MSizeZ - 1){
							indexDest = cell_index(s.x, s.z + 1);
							if (base_cell(indexSrc) != base_cell(indexDest)){
								merge(indexSrc, indexDest);
								maze_data[s.x][s.z + 1] |= Direction.N.getIndex();
								
								s_stack.push(new cMazeState(s));
								s.z += 1; s.dir = 0;
							}
						}
					} 
					catch (Exception e){
						LogError(e);
						return;
					}
				}
			}
		}
		
		// --- get voxel data ---
		public boolean[][][] GetMaze(int xS, int zS, int cellSize){
			int i, j;
			
			xS *= cellSize;
			zS *= cellSize;
			
			int xSize = xS / MSizeX;
			int zSize = zS / MSizeZ;
			
			boolean[][][] VoxelMaze = new boolean[xS + 1][MSizeY][zS + 1];
			for (i = 0; i < MSizeX; i++){
				for (j = 0; j < MSizeZ; j++){
					if ((maze_data[i][j] & Direction.N.getIndex()) == 0){
						// draw voxel vertical line.
						int startingPointX = xSize * i;
						int EndingPointX = xSize * (i + 1);
						int zPos = zSize * j;
						
						for (int _x = startingPointX; _x < EndingPointX; _x++){
							for (int _y = 0; _y < MSizeY; _y++){
								VoxelMaze[_x][_y][zPos] = true;
							}
						}
						
					}
					
					if ((maze_data[i][j] & Direction.W.getIndex()) == 0){
						// draw voxel horizontal line.
						int startingPointZ = zSize * j;
						int endingPointZ = zSize * (j + 1);
						int xPos = xSize * i;
						
						for(int _y = 0; _y < MSizeY; _y++){
							for (int _z = startingPointZ; _z < endingPointZ; _z++){
								VoxelMaze[xPos][_y][_z] = true;
							}
						}
					}
				}
			}
			
			return VoxelMaze;
		}
		
		// --- Cell functions ---
		int cell_index(int x, int y){
			return MSizeX * y + x;
		}
		int base_cell(int tIndex){
			int index = tIndex;
			while(maze_base[index] >= 0){
				index = maze_base[index];
			}
			return index;
		}
		void merge (int index1, int index2){
			int base1 = base_cell(index1);
			int base2 = base_cell(index2);
			maze_base[base2] = base1;
		}
		
		// --- Maze Init ---
		void MazeInit(Random r){
			int i, j;
			
			for (i = 0; i < MSizeX; i++){
				for (j = 0; j < MSizeZ; j++){
					maze_base[cell_index(i, j)] = -1;
					maze_data[i][j] = 0;
				}
			}
		}

		void LogError(final Exception e){
			MazeGen.getInstance().getServer().getScheduler().runTask(MazeGen.getInstance(), new BukkitRunnable () {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String message = e.getClass().toString() + ": " + e.getMessage();
					StackTraceElement[] traceArray = e.getStackTrace();
					String trace = "";
					for (StackTraceElement element : traceArray){
						trace += element.toString() + "\n";
					}
					
					MazeGen.getInstance().getServer().broadcastMessage(message + "\n" + trace);
				}});
		}
	}

	private Maze myMaze;
	private boolean[][][] Voxels;
	
	public MazeBuilder() { this(2, 2, 2, 0, 2, 1); }
	
	public MazeBuilder(int mazeSizeX, int mazeSizeY, int mazeSizeZ, int seed, int cellSize, int smooth) {
		myMaze = new Maze();
		myMaze.GenerateMaze(mazeSizeX, mazeSizeY, mazeSizeZ, seed, smooth);
		Voxels = myMaze.GetMaze(mazeSizeX, mazeSizeZ, cellSize);
	}
	
	public boolean getValue(int x, int y, int z){
		if (isInBounds(x,y,z))
			return Voxels[x][y][z];
		else
			return false;
	}
	
	public boolean isInBounds(int x, int y, int z){
		return ((x <= Voxels.length - 1) && (y <= Voxels[0].length - 1) && (z <= Voxels[0][0].length - 1) && x >= 0 && y >= 0 && z >= 0);
	}
}
