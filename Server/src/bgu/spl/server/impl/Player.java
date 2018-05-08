package bgu.spl.server.impl;

import bgu.spl.server.ProtocolCallback;
import bgu.spl.tokenizer.StringMessage;

public class Player {
	private String nick;
	private ProtocolCallback<StringMessage> callback;
	private boolean isResponded;
	private int score;
	private String response;

	public Player (String nick, ProtocolCallback<StringMessage> callback){
		this.nick = nick;
		this.callback = callback;
		isResponded = false;
		score = 0;
		response = null;
	}

	public void setResponse(String resp){
		response = resp;
	}
	
	public String getResponse (){
		return response;
	}
	
	public int getScore(){
		return score;
	}
	
	public void setScore(int points){
		score+=points;
	}
	
	public boolean getIsResponded (){
		return isResponded;
	}
	
	public void setIsResponded(){
		isResponded = !isResponded;
	}

	public String getNick(){
		return nick;
	}

	public ProtocolCallback<StringMessage>  getCallBack(){
		return callback;
	}
}

