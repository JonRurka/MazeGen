package com.nug700.MazeGen;

public final class cMazeState {
	public int x, z, dir;
	public cMazeState(int tx, int tz, int td) {x = tx; z = tz; dir = td;}
	public cMazeState(cMazeState s) {x = s.x; z = s.z; dir = s.dir;}
}
