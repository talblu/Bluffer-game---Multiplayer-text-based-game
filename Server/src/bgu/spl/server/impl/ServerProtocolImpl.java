package bgu.spl.server.impl;

import java.io.IOException;
import java.util.LinkedList;

import bgu.spl.server.AsyncServerProtocol;
import bgu.spl.server.ProtocolCallback;
import bgu.spl.server.ServerProtocol;
import bgu.spl.tokenizer.StringMessage;

public class ServerProtocolImpl implements AsyncServerProtocol<StringMessage> {
	private boolean _shouldClose;
	private GeneralInfo info;
	private String nick;
	private Player player;
	private Room room;

	public ServerProtocolImpl () {
		_shouldClose = false;
		info = GeneralInfo.getInstance();
		nick = null;
		player = null;
		room = null;
	}


	public boolean isEnd(StringMessage msg ) { // Was T before we changed to String
		return msg.getMessage().equals("QUIT");
	}


	public void processMessage (StringMessage msg, ProtocolCallback <StringMessage> callback) { // 	Was T before we changed to String
		boolean isValid = false;
		if (isEnd(msg))
			_shouldClose = true;

		if (msg.getMessage().startsWith("NICK")) {
			if (msg.getMessage().length()==4) {//check if there is a nick after "NICK"
				try {
					callback.sendMessage(new StringMessage("SYSMSG NICK REJECTED"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else{
				nick = msg.getMessage().substring(4);

				if (!info.addNick(nick)){
					try {
						callback.sendMessage(new StringMessage("SYSMSG NICK REJECTED"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else
					try {
						callback.sendMessage(new StringMessage("SYSMSG NICK ACCEPTED"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				isValid = true;
			}
		}

		if (msg.getMessage().startsWith("JOIN")){
			if (msg.getMessage().length()==4) {//check if there is a room after "JOIN"
				try {
					callback.sendMessage(new StringMessage("SYSMSG NICK REJECTED"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else{
				String roomName = msg.getMessage().substring(4);
				if (nick!=null && !roomName.equals("")){//the player not register yet!
					player=new Player(nick,callback);
					if (!info.addPlayer(player, roomName)){
						try {
							callback.sendMessage(new StringMessage("SYSMSG JOIN REJECTED"));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else{
						room = info.getHashRooms().get(roomName);
						try {
							callback.sendMessage(new StringMessage("SYSMSG JOIN ACCEPTED"));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				} else
					try {
						callback.sendMessage(new StringMessage("SYSMSG JOIN REJECTED"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				isValid = true;
			}
		}

		if (msg.getMessage().startsWith("MSG")) {
			if (msg.getMessage().length()==3) {//check if there is a massage after "MSG"
				try {
					callback.sendMessage(new StringMessage("SYSMSG NICK REJECTED"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else{
				if (room != null) {
					String message = msg.getMessage().substring(4);
					LinkedList<Player> playerList = info.getHashRooms().get(room.getName()).getPlayerList();
					for (int i=0; i<playerList.size(); i++) {
						if (playerList.get(i).getNick()!=nick){
							try {
								playerList.get(i).getCallBack().sendMessage(new StringMessage("USRMSG"+nick+": " +message));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					try {
						callback.sendMessage(new StringMessage("SYSMSG MSG ACCEPTED"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else
					try {
						callback.sendMessage(new StringMessage("SYSMSG MSG REJECTED"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				isValid = true;
			}
		}

		if (msg.getMessage().startsWith("LISTGAMES")) {
			
			try {
				callback.sendMessage(new StringMessage("SYSMSG LISTGAMES ACCEPTED"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (int i=0; i<info.getGameList().size(); i++)
				try {
					callback.sendMessage(new StringMessage(info.getGameList().get(i)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			isValid = true;
		}

		if (msg.getMessage().startsWith("STARTGAME")) {
			if (msg.getMessage().length()==9) {//check if there is a game after "STARTGAME"
				try {
					callback.sendMessage(new StringMessage("SYSMSG NICK REJECTED"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else{
			String game = msg.getMessage().substring(10);
			if (!info.getGameList().contains(game) || room.isTheGameStarted()) {
				try {
					callback.sendMessage(new StringMessage("SYSMSG STARTGAME REJECTED"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				if (game.equals("BLUFFER")) {// maybe we need to create a hashmap with all the games and their start threads
					try {
						callback.sendMessage(new StringMessage("SYSMSG STARTGAME ACCEPTED"));
						room.startGame(game);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			isValid = true;
			}
		}

		if (msg.getMessage().startsWith("TXTRESP")) {// we made sure the this command is valid (in c++)
			if (msg.getMessage().length()==7) {//check if there is a response after "TXTRESP"
				try {
					callback.sendMessage(new StringMessage("SYSMSG NICK REJECTED"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else{
			String response = msg.getMessage().substring(9);
			try {
				room.addResponse(response);
				callback.sendMessage(new StringMessage("SYSMSG TXTRESP ACCEPTED"));
				if (room.getPlayerList().size() == room.getResponseList().size()) {
					room.sendchoises();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			isValid = true;
			}
		}

		if (msg.getMessage().startsWith("SELECTRESP")) {
			if (msg.getMessage().length()==10) {//check if there is a response after "SELECTRESP"
				try {
					callback.sendMessage(new StringMessage("SYSMSG NICK REJECTED"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else{
			try {
				int choiceNum = Integer.parseInt(msg.getMessage().substring(11));
				room.getHashChoiceNum().putIfAbsent(choiceNum, new LinkedList<Player>());
				room.getHashChoiceNum().get(choiceNum).add(player);
				callback.sendMessage(new StringMessage("SYSMSG SELECTRESP ACCEPTED"));
				if (room.getPlayerList().size() == room.getHashSize()) {
					room.summary();
				}
				isValid = true;
			
			} catch (NumberFormatException e) {	
			} catch (IOException e) {
			} catch (StringIndexOutOfBoundsException e) {}
			}
		}
		

		if (msg.getMessage().startsWith("QUIT")){				
			if(player!=null){
				if(msg.getMessage().equals("QUIT")){
					if(room!=null){
						boolean ans = info.removePlayer(player, room);	
						if (ans) {
							try {
								callback.sendMessage(new StringMessage("SYSMSG QUIT ACCEPTED"));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							_shouldClose=true;
						}
						else{
							try {
								callback.sendMessage(new StringMessage("SYSMSG QUIT REJECTED - A game is still in progress in your room"));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	
						}
					}
					else{
						try {
							callback.sendMessage(new StringMessage("SYSMSG QUIT ACCEPTED"));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						info.removeNick(nick);
						_shouldClose=true;
					}
				}
				else{
					try {
						callback.sendMessage(new StringMessage("SYSMSG QUIT REJECTED - Invalid command"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			else{
				try {
					callback.sendMessage(new StringMessage("SYSMSG QUIT REJECTED - You didn't sign in"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			isValid = true;
		}

		if (!isValid) {// checks is the message received from the client is a valid message
			try {
				callback.sendMessage(new StringMessage("SYSMSG " + msg + " UNIDENTIFIED"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		}
	

	public boolean shouldClose() {
		return _shouldClose;
	}

	public void connectionTerminated() {
		_shouldClose = true;
	}
}
