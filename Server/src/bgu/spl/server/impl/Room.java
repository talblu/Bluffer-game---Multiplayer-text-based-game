package bgu.spl.server.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import json.RunBlufferGame;

public class Room {
	private String name;
	private String game;
	private boolean isStarted;
	private LinkedList<Player> playerList;
	private LinkedList<String> responseList;
	private RunBlufferGame bluffer;
	private HashMap <Integer ,LinkedList<Player>> HashChoiceNum;

	public Room (String name, LinkedList<Player> players) {
		this.name = name;
		game = null;
		isStarted = false;
		playerList = players;
		responseList = new LinkedList<String>();
		bluffer = new RunBlufferGame(this);
		HashChoiceNum = new HashMap<Integer ,LinkedList<Player>>();
	}

	public LinkedList<String> getResponseList () {
		return responseList;
	}

	public void addResponse (String response) {
		if (!responseList.contains(response))
			responseList.add(response);
	}

	public LinkedList<Player> getPlayerList () {
		return playerList;
	}

	public void addPlayer (Player player) {
		playerList.add(player);
	}
	
	public void removePlayer (Player player) {
		playerList.remove(player);
	}

	public String getName () {
		return name;
	}

	public String getGame () {
		return game;
	}

	public void startGame (String gameName) {		
		isStarted = true;
		if (gameName.equals("BLUFFER")){
			System.out.println("rommmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
			bluffer.run();
		}
	}

	public boolean isTheGameStarted(){
		return isStarted;
	}

	public void sendchoises(){
		bluffer.sendChoises();
	}

	public void summary(){
		bluffer.summary();
	}
	
	public void endGame (){
		isStarted = false;
	}
	
	public HashMap<Integer, LinkedList<Player>> getHashChoiceNum() {
		return HashChoiceNum;
	}

	public synchronized int getHashSize () {
		System.out.println("gethashsizekkkkkkkkkkkkkkkkkkkkkkkkk");
		int sum = 0;
		for(Entry<Integer, LinkedList<Player>> Entry : HashChoiceNum.entrySet()) {
			sum+=Entry.getValue().size();
			System.out.println("foorrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
		}
		return sum;
	}

	public void initializeMap (){
		HashChoiceNum.clear();
	}
}
