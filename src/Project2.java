package Project2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Project2 {
	public static void main(String args[]) throws FileNotFoundException{
		int m = 113;
		int power = 0;
		long[] words; //linked list 
		double a = (Math.sqrt(5) - 1) / 2;
		String[] filenames = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",	"S", "T", "U", "V", "W", "XYZ"};		
		boolean conflict = true;
		
		while(conflict){
			conflict = false;
			m = getNextM(m);
			
			System.out.println(m);
			
			words = new long[m];
			
			for(int i = 0; i < filenames.length && !conflict; i++){
				Scanner in = new Scanner(new File("words\\" + filenames[i] + ".txt"));
				
				while(in.hasNext()){
					String word = in.next();
					
					if(word.length() != 3 && word.length() != 4 && word.length() != 7 && word.length() != 10){
						continue;
					}
					
					long k = Long.parseLong(wordToNumber(word));
					double k2 = (k * a) % 1;
					int k3 = (int) (m * k2);
					
					if(words[k3] == 0){
						words[k3] = k;
					}
					
					//linear hashing
					else if(words[k3] != k){
						conflict = true;
						break;
					}
				}
			}
		}
		
//		for(int i = 0; i < words.length; i++){
//			System.out.println(words[i] + " ");
//		}
		
		System.out.println(m);
	}
	
	//make access function
	
	public static int getNextM(int m){
		m = m * 2;
		
		if(m % 2 == 0){
			m++;
		}
		
		boolean prime = false;
		
		while(!prime){
			prime = false;
			
			for(int i = 1; i < m / 2; i = i + 2){
				if(m % i == 0){
					prime = true;
					break;
				}
			}
			
			m = m + 2;
		}
		
		return m;
	}

	
	public static String wordToNumber(String word){
		String numWord = "";
		
		for(int i = 0; i < word.length(); i++){
			char c = word.charAt(i);
			
			if(c == 'a' || c == 'b' || c == 'c'){
				numWord = numWord + 2;
			}
			
			else if(c == 'd' || c == 'e' || c == 'f'){
				numWord = numWord + 3;
			}
			
			else if(c == 'g' || c == 'h' || c == 'i'){
				numWord = numWord + 4;
			}
			
			else if(c == 'j' || c == 'k' || c == 'l'){
				numWord = numWord + 5;
			}
			
			else if(c == 'm' || c == 'n' || c == 'o'){
				numWord = numWord + 6;
			}
			
			else if(c == 'p' || c == 'q' || c == 'r' || c == 's'){
				numWord = numWord + 7;
			}
			
			else if(c == 't' || c == 'u' || c == 'v'){
				numWord = numWord + 8;
			}
			
			else if(c == 'w' || c == 'x' || c == 'y' || c == 'z'){
				numWord = numWord + 9;
			}
		}
		
		return numWord;
	}
}
