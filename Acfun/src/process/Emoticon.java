package process;

public class Emoticon {
	public String content;
	public int type;
	public int happiness;
	public int sadness;
	public int fear;
	public int disgust;
	public int anger;
	public int surprise;
	public int love;
	public int occurrence;
	
	Emoticon() {}
	
	Emoticon(String content) {
		this.content = content;
	}
	
	public Emoticon combine(Emoticon e) {
		this.happiness += e.happiness;
		this.sadness += e.sadness;
		this.fear += e.fear;
		this.disgust += e.disgust;
		this.anger += e.anger;
		this.surprise += e.surprise;
		this.love += e.love;
		this.occurrence = this.happiness + this.sadness
				+ this.fear + this.disgust
				+ this.anger + this.surprise
				+ this.love;
		return this;
	}
}
