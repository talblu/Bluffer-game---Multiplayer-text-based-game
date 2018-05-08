package json;

public class questionInfo {
	private String question;
	private String answer;
	
	public questionInfo (String question, String answer) {
		this.question = question;
		this.answer = answer;
	}
	
	public String getQeustion () {
		return question;
	}
	
	public String getAnswer () {
		return answer;
	}
}