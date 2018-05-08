package json;
import java.io.FileNotFoundException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import bgu.spl.server.ProtocolCallback;
import bgu.spl.server.impl.*;
import bgu.spl.tokenizer.StringMessage;

public class RunBlufferGame extends game {
	private static final String filePath = "bluffer[3].json";
	private int numOfRounds;
	private questionInfo[] randomQuest;
	private int questIdx=0;
	private LinkedList<Player> listOfPlayers;
	private LinkedList<String> randomList;
	private int numOfChoices;

	public RunBlufferGame (Room room) {
		super(room);
		GeneralInfo.getInstance();
		numOfRounds=0;
		randomQuest = new questionInfo[3];
		listOfPlayers = room.getPlayerList();
		randomList = new LinkedList<String>();
		numOfChoices=0;
	}

	public String chooseRandom(LinkedList<String> list)
	{
		int index = new Random().nextInt(list.size());
		String random = list.get(index);
		return random;
	}

	public void run() {
		System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
		try{
			// read the json file
			FileReader reader = new FileReader(filePath);
			JSONParser jsonParser = new JSONParser();

			//read questions from json
			Object obj = (JSONObject) jsonParser.parse(reader);
			JSONObject questionsObj = (JSONObject)obj;

			/**
			// Reading the questions from the json file
			 */

			// get an array from the JSON object
			JSONArray questionsArray= (JSONArray)questionsObj.get("questions");
			questionInfo[] info = new questionInfo[questionsArray.size()];

			// take the elements of the json array
			for(int i=0;i<questionsArray.size();i++){
				JSONObject question = (JSONObject) questionsArray.get(i);
				String questionText = (String) question.get("questionText");
				String ans = (String)question.get("realAnswer");
				info[i] = new questionInfo(questionText,ans);
			}
			//	for(int i=0;i<info.length;i++){
			//		System.out.println("Quest: " + info[i].getQeustion());
			//		System.out.println("Ans: " + info[i].getAnswer() + "\n");
			//	}
			int countQuest = 0;
			int j=(int)(Math.random()*100%info.length);
			randomQuest[countQuest] = info[j];
			countQuest++;
			info[j] = null;
			while (countQuest<3){// choosing 3 random questions
				while (info[j]==null){
					j=(int)(Math.random()*100%info.length);
					//System.out.println(j);
					//System.out.println(info[j]);
				}
				randomQuest[countQuest] = info[j];
				countQuest++;
				info[j] = null;
			}
			round();


		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ParseException ex) {
			ex.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
	}
	public void round(){
		if (numOfRounds==3)
			finishGame();
		else {	
			numOfRounds++;
			for (int i=0; i<listOfPlayers.size(); i++) {
				ProtocolCallback<StringMessage> callback = listOfPlayers.get(i).getCallBack();
				try {
					callback.sendMessage(new StringMessage("ASKTXT " + randomQuest[questIdx].getQeustion()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		// waiting for all the players to respond
		//		System.out.println("WHILLLLLLLLLLLLLLLLLEEEEEEEEEEEEEEE");
		//Counter=0;
		//	for (int h=0; h<listOfPlayers.size(); h++){
		//	if (listOfPlayers.get(h).getIsResponded())
		//	//	Counter++;
		//}
	}
	public void sendChoises(){

		for (int h=0; h<listOfPlayers.size(); h++){// setting the boolean indicating a response back to false
			listOfPlayers.get(h).setIsResponded();
		}

		room.addResponse(randomQuest[questIdx].getAnswer()); //adds the real answer to the response list

		while (room.getResponseList().size()>0) {//shuffling the response list randomly
			String random = chooseRandom(room.getResponseList());
			room.getResponseList().remove(random);
			randomList.add(random);
		}

		String choices = "ASKCHOICES "; // sums up all the choices into one string
		numOfChoices=randomList.size();
		for (int k=0; k<numOfChoices; k++) {
			choices = choices+(k + ". " + randomList.get(k) + " ");
		}
		
		for (int i=0; i<numOfChoices; i++) { // adds ALL the unchosen options to the hashmap of choices
			room.getHashChoiceNum().putIfAbsent(i, new LinkedList<Player>());
		}

		for (int i=0; i<listOfPlayers.size(); i++) {// sends all the possible choices to all the players in the room
			ProtocolCallback<StringMessage> callback = listOfPlayers.get(i).getCallBack();		
			try {
				callback.sendMessage(new StringMessage(choices));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		choices = "";
	}
	//Counter = 0;

	//while (Counter < listOfPlayers.size()) { // waiting for all the player to respond
	//	Counter=0;
	//	for (int h=0; h<listOfPlayers.size(); h++){
	//		if (listOfPlayers.get(h).getIsResponded())
	//			Counter++;
	///	}
	//}
	public void summary () {
		String result=""; 
		int playerScore=0;
		for (int i=0; i<listOfPlayers.size(); i++) { // shows all the players the correct answer
			try {
				listOfPlayers.get(i).getCallBack().sendMessage(new StringMessage("GAMEMSG The correct answer is: "+randomQuest[questIdx].getAnswer()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (int i=0; i<listOfPlayers.size(); i++) {//sum points to the players in this round
			listOfPlayers.get(i).setIsResponded();// setting the boolean indicating a response back to false
			Player player=listOfPlayers.get(i);
			String response=player.getResponse();
			int bluferchoise=0;
			HashMap <Integer ,LinkedList<Player>> hashchoises=room.getHashChoiceNum();
			for(int m=0;m<numOfChoices;m++) {//give 10 points to the players that answer correct
				if (hashchoises.get(m).contains(player)){/////////////////////////////////////////need to fix!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					String playerAns=randomList.get(m);

					if (playerAns==randomQuest[questIdx].getAnswer()) {
						result="Correct! ";
						listOfPlayers.get(i).setScore(10);
						playerScore=playerScore+10;
					}
					else result="Wrong! ";
					break;

				}
				for (int s=0; s<listOfPlayers.size(); s++)	{//give 5 points to the blufers players
					if (listOfPlayers.get(s)!=player){
						for (int b=0;b<randomList.size();b++){
							if (response==randomList.get(b)){//
								bluferchoise=b;
								break;
							}

						}
						if (hashchoises.containsKey(bluferchoise)){
							for (int c=0;c<hashchoises.get(bluferchoise).size();c++){
								player.setScore(5);
								playerScore=playerScore+5;
							}
						}
					}
				}
			}
			try {
				player.getCallBack().sendMessage(new StringMessage("GAMEMSG "+result+playerScore+"+pts"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			playerScore=0;
		}

		randomList.clear();
		room.initializeMap();
		questIdx++;
		round();
	}

	public void finishGame () {
		String summary = "GAMEMSG Summary: "; // sums up all the score info into a string
		for (int i=0; i<listOfPlayers.size(); i++) {
			summary = summary+(listOfPlayers.get(i).getNick()+": "+listOfPlayers.get(i).getScore()+"pts ");
		}

		for (int i=0; i<listOfPlayers.size(); i++) {// sends score summary to all the players
			try {
				listOfPlayers.get(i).getCallBack().sendMessage(new StringMessage(summary));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		room.endGame();
	}
}