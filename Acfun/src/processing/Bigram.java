package processing;

public class Bigram {
	String bigram = null;
	int[] category = {0, 0, 0, 0, 0, 0, 0};
	int occurance = 0;
	double[] freq = {0, 0, 0, 0, 0, 0, 0};
	
	Bigram(String b, int[] ca) {
		bigram = b;
		for (int i = 0; i < 7; i++){
			category[i] = ca[i];
		}
	}
	
	public void add(Bigram b) {
		for (int i = 0;i < 7; i++){
			this.category[i] += b.category[i];
		}
	}
	
	@Override
	public String toString(){
		String str = this.bigram;
		for (int i = 0;i < 7;i++){
			str = str + ", " + this.category[i]; 
		}
		for (int i = 0;i < 7;i++){
			str = str + ", " + this.freq[i]; 
		}
		return str;
	}
	
	public void calc(){
		for (int i = 0; i < 7;i++){
			occurance += category[i];
		}
		
		for (int i = 0; i < 7;i++){
			if (category[i] == 0){
				freq[i] = 0.0001;
			} else {
				freq[i] = (double) category[i] / (double) occurance;
			}
		}
		
		for (int i = 0;i < 7;i++){
			freq[i] = Math.log(freq[i]);
		}
//		System.out.println(this);
	}
}
