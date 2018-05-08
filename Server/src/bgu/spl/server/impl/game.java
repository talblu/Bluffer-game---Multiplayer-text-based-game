package bgu.spl.server.impl;

import java.util.LinkedList;

public abstract class game {
	protected Room room;

	public game(Room room){
		this.room=room;
	}
	
	public abstract void run();
	public abstract void round();
	public abstract void sendChoises();
	public abstract void summary ();
	public abstract void finishGame ();
	
}
