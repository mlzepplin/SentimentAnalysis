import java.io.*;
import java.util.*;
import java.lang.*;

public class Hash{
	public static void main(String args[]) throws java.io.IOException{

		String tempstring;
		char pos;
		HashTableMaker ht = new HashTableMaker();
		item get = null;
		ht.makeTable("/home/rishabh/Sentiment_Analysis/SentiWordNet.txt");
		///home/rishabh/Sentiment_Analysis/SentiWordNet_3.0.0_20130122.txt
		//ht.printTable();
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		System.out.println("pos?");
		pos=br.readLine().charAt(0);
		System.out.println("input?");
		tempstring=br.readLine();
		while(!tempstring.equals("exit")){
			get = ht.findWord(tempstring,pos);
			ht.foundWord(get);
			System.out.println("pos?");
			pos=br.readLine().charAt(0);
			System.out.println("input?");
			tempstring=br.readLine();
			
		}
		//int t = ht.hashFunction("unable");
		//ht.printItemsInIndex(t);
		
		
	}
}


class item{

		public char pos;
		public long id;
		public double posScore;
		public double negScore;
		public String word;
		public item next;
	}

class HashTableMaker{

	private int tableSize = 100;//number  of buckets

	private item hashTable[]= new item[tableSize];

	public HashTableMaker(){

		for(int i=0;i<tableSize;i++){

			hashTable[i] = new item();
			hashTable[i].pos = 'd';
			hashTable[i].id  = 0;
			hashTable[i].posScore = 0.0;
			hashTable[i].negScore = 0.0;
			hashTable[i].word = "empty";
			hashTable[i].next=null;
		}

	}

	public void makeTable(String input){

		//String fileName = "/home/rishabh/Desktop/testfile";
		String fileName = input;
		String line = null;
		BufferedReader br = null;
		item temp = new item();
		try{
			
			FileReader fileReader = new FileReader(fileName);
			br= new BufferedReader(fileReader);

			while((line = br.readLine()) != null){

				StringTokenizer st = new StringTokenizer(line);
				String s;

				

				s = st.nextToken();
				temp.pos = s.charAt(0);
				if(!st.hasMoreTokens()){
				System.out.println(line);	
				}
				s = st.nextToken();
				temp.id = Long.parseLong(s);
				s = st.nextToken();
				temp.posScore = Double.parseDouble(s);
				s = st.nextToken();
				temp.negScore = Double.parseDouble(s);

				s = st.nextToken();

				temp.word = s;

				while((temp.word.charAt(temp.word.length()-3) == '#'||temp.word.charAt(temp.word.length()-2)== '#')){
					String trimmed;
					if(temp.word.charAt(temp.word.length()-3) == '#'){
						trimmed=temp.word.substring(0,temp.word.length()-3);
						addItem(temp.pos,temp.id,temp.posScore,temp.negScore,trimmed);
					}
					else if(temp.word.charAt(temp.word.length()-2) == '#'){
						trimmed=temp.word.substring(0,temp.word.length()-2);
						addItem(temp.pos,temp.id,temp.posScore,temp.negScore,trimmed);
					}
					temp.word = st.nextToken();
					if(temp.word.length()<3)
						break;
				}
				
				//addItem(temp.pos,temp.id,temp.posScore,temp.negScore,temp.word);



	
				
			
				//System.out.println(line);

			}
		}catch(FileNotFoundException ex){
			System.out.println("unable to open file " + fileName );
		}
		catch(IOException ex){
			System.out.println("Error reading file" + fileName);
		}
		finally{

			try{

				if(br != null){
					br.close();
				}
			}catch(IOException e){

			}
		}

	}

	public void addItem(char pos,long id,double posScore,double negScore,String word){
		
		int index = hashFunction(word);

		if(hashTable[index].word == "empty"){

			hashTable[index].pos = pos;
			hashTable[index].id  = id;
			hashTable[index].posScore = posScore;
			hashTable[index].negScore = negScore;
			hashTable[index].word = word;
		}
		else{

			item ref = hashTable[index];
			item newRef = new item();
			newRef.pos = pos;
			newRef.id = id;
			newRef.posScore = posScore;
			newRef.negScore = negScore;
			newRef.word = word;
			newRef.next = null;

			while(ref.next != null){

				ref = ref.next;

			}

			ref.next = newRef;

		}
	}

	public int numberOfItemsInIndex(int index){

		int count=0;
	
		if(hashTable[index].word == "empty"){

			return count;
		}
		else{

			count++;
			item ref = hashTable[index];

			while(ref.next != null){

				count++;
				ref = ref.next;
			}
			return count;
		}

	}

	public void printTable(){

		int number;

		for(int i=0;i<tableSize;i++){

			number = numberOfItemsInIndex(i);
			System.out.println("-------------------");
			System.out.println("index = " +i );
			System.out.print( hashTable[i].pos+"  ");
			System.out.print( hashTable[i].id+"  ");
			System.out.print( hashTable[i].posScore+"  ");
			System.out.print( hashTable[i].negScore+"  ");
			System.out.print( hashTable[i].word+"  ");
			System.out.println( "# of items = "+number );
		}

	}

	public void printItemsInIndex(int index){

		item ref = hashTable[index];

		if(ref.word == "empty"){

			System.out.println(" index = "+index + "is empty" );

		}
		else{

			System.out.println("index " + index + " contains the following items");
		

			while(ref != null){


				System.out.println("-------------------");
				System.out.println( ref.pos);
				System.out.println( ref.id);
				System.out.println( ref.posScore);
				System.out.println( ref.negScore);
				System.out.println( ref.word);
				System.out.println( ref.word.length());
				System.out.println( ref.next);
				System.out.println("-------------------");

				ref=ref.next;
			}
		}
	}

	public item findWord(String word,char pos){

		int index = hashFunction(word);
		boolean foundWord = false;

		item ref = hashTable[index];


		if(ref.pos == 'd'){

			System.out.println(word+"'s inf was not found in the hashtable");
			return null;
		}
		else if(ref.word.equals(word) && (pos == ref.pos)){
			foundWord = true;
			//foundWord(ref);
			return ref;
		}
		while(ref.next != null){

			if(ref.word.equals(word) && (ref.pos == pos)){

				foundWord = true;
				break;
			}
			ref = ref.next;

		}
		if(foundWord == true){

			//foundWord(ref);
			return ref;
		}
		else{
			

			//System.out.println("-------------------");
			System.out.println(word+"'s info was not found in the hashtable");
			return null;
		}

	}

	public void foundWord(item ref){

		System.out.println("found = "+ref.word);
			System.out.println(ref.pos);
			System.out.println(ref.id);
			System.out.println(ref.posScore);
			System.out.println(ref.negScore);
			System.out.println("-------------------");
	}

	public int hashFunction(String key){

		int hash = 0;
		int index;

		for(int i=0;i<key.length();i++){

			hash=hash+(int)key.charAt(i);		//adding up the ascii values of all characters in the string
		}

		index = hash % tableSize;


		return index; 

	}
}
	/*public void readLineInFile(String fileName){
		//String fileName = "SentiWordNet_3.0.0_20130122";
		String line = null;
		BufferedReader br = null;
		
		try{
			
			FileReader fileReader = new FileReader(fileName);
			br= new BufferedReader(fileReader);

			//while((line = br.readLine()) != null){
			line = br.readLine();
				System.out.println(line);
			//}
		}catch(FileNotFoundException ex){
			System.out.println("unable to open file " + fileName );
		}
		catch(IOException ex){
			System.out.println("Error reading file" + fileName);
		}
		finally{

			try{

				if(br != null){
					br.close();
				}
			}catch(IOException e){

			}
		}
	}*/
