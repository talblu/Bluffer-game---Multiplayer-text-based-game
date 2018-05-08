package bgu.spl.server.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

public class GeneralInfo {
	private LinkedList<String> nickList;
	private HashMap <String ,Room> HashRooms;
	private LinkedList<String> gamesList;
	//private HashMap <String ,LinkedList<Player>> HashResponse;

	private boolean allPlayerRespond;

	private static class GeneralInfoHolder {
		private static GeneralInfo instance = new GeneralInfo();
	}

	private GeneralInfo() {
		nickList = new LinkedList<String>();
		HashRooms = new HashMap <String, Room>();
		gamesList = new LinkedList<String>();
		gamesList.add("BLUFFER");
		//HashResponse = new HashMap<String ,LinkedList<Player>>();
		allPlayerRespond=false;
	}

	public static GeneralInfo getInstance() {
		return GeneralInfoHolder.instance;
	}

	public boolean addNick (String nick) {
		if (nickList.contains(nick))
			return false;
		else {
			nickList.addFirst(nick);
			return true;
		}
	}
	
	public void removeNick (String nick) {
		nickList.remove(nick);
	}
	//public HashMap<String ,LinkedList<Player>>  getHashResponse(){
	//	return  HashResponse;

	//}

	public HashMap<String, Room> getHashRooms (){
		return HashRooms;
	}

	public LinkedList<String> getGameList () {
		return gamesList;
	}


	public boolean addPlayer(Player player,String roomName){
		//	Room room = new Room (roomName);
		for(Entry<String, Room> Entry : HashRooms.entrySet()) {//make sure that there isn't a show of the specific player in another room
			if (Entry.getValue().getPlayerList().contains(player)){
				if (Entry.getValue().isTheGameStarted())
					return false;//the player is already playing in another room that started
				Entry.getValue().getPlayerList().remove(player);
				break;
			}
		}

		if (HashRooms.containsKey(roomName)){
			if (!HashRooms.get(roomName).isTheGameStarted()){
				HashRooms.get(roomName).addPlayer(player);
				return true;
			}
			else
				return false;//the game has started,no one can enter now
		}
		else{//the room dont exist yet
			LinkedList<Player> playerList =new LinkedList <Player>();
			playerList.add(player);
			HashRooms.put(roomName, new Room (roomName, playerList));	
			return true;
		}
	}
	
	public boolean removePlayer(Player player, Room room){
		if(room.isTheGameStarted()){
			return false;
		}
		else{
			room.removePlayer(player);
			//callbacksANDNames.remove(players.get(playerName).getCallback());
			return true;
		}
	}
	
	public boolean getAllPlayerRespond(){
		return allPlayerRespond;
	}
	public void setgetAllPlayerRespond(){
		allPlayerRespond=(!allPlayerRespond);
	}
}