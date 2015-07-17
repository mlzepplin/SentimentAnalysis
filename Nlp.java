import java.io.*;
import java.util.*;
import java.lang.*;
import java.io.IOException; 
import edu.stanford.nlp.tagger.maxent.*;

public class Nlp{

	public static void main(String args[]) throws IOException,ClassNotFoundException{

		nlpSA nlpObj = new nlpSA();

		String fileIn = "sentences";
		String fileOut = "temp";
		String oneLine = null;
		String temp;
		BufferedReader br = null;
		BufferedWriter br2 = null;
		int i=0;
		char p;

		nlpObj.incArrayBuilder();
		nlpObj.decArrayBuilder();
		nlpObj.invArrayBuilder();

		for(i=0;i<nlpObj.incSize;i++)
			System.out.println(nlpObj.incArray[i]);
				


		try {

			FileReader fr = new FileReader(fileIn);
			br = new BufferedReader(fr);

			FileWriter fr2 = new FileWriter(fileOut);
			br2 = new BufferedWriter(fr2);

			while ((oneLine = br.readLine()) != null){

				p = oneLine.charAt(0);
				nlpObj.tagging(oneLine);


				nlpObj.incPolarities();
				nlpObj.decPolarities();
				nlpObj.invPolarities();

				nlpObj.sentencePolarity();

				temp = ((Double)nlpObj.effPos).toString();
				br2.write(temp,0,temp.length());
				br2.write(" ");
				temp = ((Double)nlpObj.effNeg).toString();
				br2.write(temp,0,temp.length());
				br2.write(" ");

				if(p == 'p'){

					br2.write("1 0 0");
				}
				else if(p == 'n'){

					br2.write("0 1 0");
				}
				else{

					br2.write(" 0 0 1");
				}

				br2.newLine();

				nlpObj.effPos = 0.0;
				nlpObj.effNeg = 0.0;

			}

			} catch (FileNotFoundException ex) {

			System.out.println("Unable to open file '" + fileIn + fileOut+"'");

		} catch (IOException ex) {

			System.out.println("Error reading file '" + fileIn + fileOut+"'");
			
		} finally {

			try {
				
				if(br != null){
					
					br.close();
				}
				
				if(br2 != null){

					br2.close();
				}
			} catch (IOException e) {
				
			}
			
		}




	}
}

class nlpSA{

	class Tokenst
	{
		String word;
		String tag;
		char posid;
		double poss;
		double negs;
	}

	Tokenst[] obttok;
	int[] mems;
	int size;
	int decSize;
	int incSize;
	int invSize;
	String[] decArray;
	String[] incArray;
	String[] invArray;
	double effPos=0.0;
	double effNeg=0.0;
	//Hash table object
	HashTableMaker obj;

	public nlpSA(){
		decSize = 6;
		incSize = 6;
		invSize = 6;
		//hash table init
		obj = new HashTableMaker();
		obj.makeTable("/home/rishabh/Sentiment_Analysis/SentiWordNet.txt");
	}

	void sentencePolarity(){

		int count=0;
		for(int i=0;i<size;i++){

			if((obttok[i].poss!=0.0)||(obttok[i].negs!=0.0) ){

				effPos = effPos+obttok[i].poss;
				effNeg = effNeg+obttok[i].negs;
				count++;
			}

		}
		if(count>0){
		effPos = (effPos/count);
		effNeg = (effNeg/count);
		}
	}
	void displayToken(Tokenst var){

		System.out.println("----------------");
		System.out.println("word: "+var.word);
		System.out.println("tag: "+var.tag);
		System.out.println("pos id: "+var.posid);
		System.out.println("pos score: "+var.poss);
		System.out.println("neg score: "+var.negs);
		System.out.println("----------------");
	}
	
	void tagging( String line)  throws IOException,ClassNotFoundException{

		// Initialize the tagger
		MaxentTagger tagger = new MaxentTagger("taggers/english-bidirectional-distsim.tagger");
		
		// The tagged string
    	String tagged = tagger.tagString(line);

		line = tagged;
		int i,j;
		
		
		
		item saitem=null;

		// Tokenisation begins	
		StringTokenizer st = new StringTokenizer(line,"_' '\t");
		i=0;
		size=st.countTokens()/2;

		mems=new int[size];

		for(j=0;j<size;j++)
		{
			mems[j]=0;
		}

		obttok=new Tokenst[size];
		for(int k=0;k<size;k++){
			obttok[k] = new Tokenst();
		}

		while(st.hasMoreTokens()) {


			obttok[i].word = st.nextToken(); 
			obttok[i].tag = st.nextToken();

			if(obttok[i].word.length()>2)
			{
				if(obttok[i].tag.equals("JJ")||obttok[i].tag.equals("JJR")||obttok[i].tag.equals("JJS"))
					{
						obttok[i].posid='a';
						saitem=obj.findWord(obttok[i].word,obttok[i].posid);
				

						//call find_word with a and key and return + - values
					}
				else if(obttok[i].tag.equals("RB")||obttok[i].tag.equals("RBR")||obttok[i].tag.equals("RBS")||obttok[i].tag.equals("WRB"))
					{
						mems[i]=1;
						obttok[i].posid='r';
						saitem=obj.findWord(obttok[i].word,obttok[i].posid);
						//call find_word with r and key and return + - values
					}
				else if(obttok[i].tag.equals("NN")||obttok[i].tag.equals("NNS")||obttok[i].tag.equals("NNP")||obttok[i].tag.equals("NNPS")||obttok[i].tag.equals("PRP")||obttok[i].tag.equals("PRP$")||obttok[i].tag.equals("WP")||obttok[i].tag.equals("WP$"))
					{
						obttok[i].posid='n';
						saitem=obj.findWord(obttok[i].word,obttok[i].posid);
						//call find_word with n and key and return + - values
					}
				else if(obttok[i].tag.equals("VB")||obttok[i].tag.equals("VBD")||obttok[i].tag.equals("VBG")||obttok[i].tag.equals("VBN")||obttok[i].tag.equals("VBP")||obttok[i].tag.equals("VBZ"))
					{
						obttok[i].posid='v';
						saitem=obj.findWord(obttok[i].word,obttok[i].posid);
						//call find_word with v and key and return + - values
					}
				else
					{
						obttok[i].posid='n';
						saitem=obj.findWord(obttok[i].word,obttok[i].posid);
						//call find_word with n and key and return + - values
					}
				if(saitem!=null)
					{
						obttok[i].poss=saitem.posScore;
						obttok[i].negs=saitem.negScore;
					}
				else if(saitem==null)
					{
						obttok[i].poss=0;
						obttok[i].negs=0;
					}
			}
			displayToken(obttok[i]);

			i++;
		}	
	}



	void incArrayBuilder(){

		String fName = "incrementers";
		String l = null;
		BufferedReader br = null;
		
		incArray = new String[incSize];
		int i=0;

		try {

			FileReader fr = new FileReader(fName);
			double c;
			br = new BufferedReader(fr);

			while ((l = br.readLine()) != null){

				incArray[i] = l;
				i++;

			}

			} catch (FileNotFoundException ex) {

			System.out.println("Unable to open file '" + fName+ "'");

		} catch (IOException ex) {

			System.out.println("Error reading file '" + fName + "'");
			
		} finally {

			try {
				
				if(br != null){
					
					br.close();
				}
				
			} catch (IOException e) {
				
			}
			
		}

		

	}

	void incPolarities(){

		int flag=0;
		//sorting 
		mergeSort(incArray,incSize);

		//indexing

		for(int i=0;i+1<size;i++)
		{
			if(mems[i]==1)
			{
				if(binSearch(incArray,obttok[i].word,incSize)){

					flag=i+1;	
				}
			}

			if(flag!=0)
			{

				obttok[flag].poss=obttok[flag].poss+0.1;
				obttok[flag].negs=obttok[flag].negs+0.1;

				obttok[flag-1].poss=0.0;
				obttok[flag-1].negs=0.0;
				
			}
		
			flag=0;
		}

	}


	void decArrayBuilder(){

		String fName1 = "decrementers";
		String l1 = null;
		BufferedReader br1 = null;
		
		decArray = new String[decSize+1];
		int i=0;

		try {

			FileReader fr1 = new FileReader(fName1);
			double c;
			br1 = new BufferedReader(fr1);

			while ((l1 = br1.readLine()) != null){

				
				decArray[i] = l1;
				i++;

			}

				

		} catch (FileNotFoundException ex) {

			System.out.println("Unable to open file '" + fName1+ "'");

		} catch (IOException ex) {

			System.out.println("Error reading file '" + fName1 + "'");
			
		} finally {

			try {
				
				if(br1 != null){
					
					br1.close();
				}
				
			} catch (IOException e) {
				
			}
			
		}


	}


	void decPolarities(){

		int flag1=0;
		//sorting 
		mergeSort(decArray,decSize);

		//indexing

		for(int i=0;i+1<size;i++)
		{
			if(mems[i]==1)
			{
				if(binSearch(decArray,obttok[i].word,decSize)){

					flag1=i+1;	
				}
			}

			if(flag1!=0)
			{
				obttok[flag1].poss=obttok[flag1].poss+0.1;
				obttok[flag1].negs=obttok[flag1].negs+0.1;
				obttok[flag1-1].poss=0.0;
				obttok[flag1-1].negs=0.0;
				
			}
		
			flag1=0;
		}

		
	}


	void invArrayBuilder(){

		String fName2 = "inverters";
		String l2 = null;
		BufferedReader br2 = null;
		
		invArray = new String[invSize];
		int i=0;

		try {

			FileReader fr2 = new FileReader(fName2);
			double c;
			br2 = new BufferedReader(fr2);

			while ((l2 = br2.readLine()) != null){

				invArray[i] = l2;
				i++;

			}

				

		} catch (FileNotFoundException ex) {

			System.out.println("Unable to open file '" + fName2 + "'");

		} catch (IOException ex) {

			System.out.println("Error reading file '" + fName2 + "'");
			
		} finally {

			try {
				
				if(br2 != null){
					
					br2.close();
				}
				
			} catch (IOException e) {
				
			}
			
		}

	}



	void invPolarities(){

		int flag2=0;
		//sorting 
		mergeSort(invArray,invSize);

		//indexing

		for(int i=0;i+1<size;i++)
		{
			if(mems[i]==1)
			{
				if(binSearch(invArray,obttok[i].word,invSize)){

					flag2=i+1;	
				}
			}

			if(flag2!=0)
			{

				obttok[flag2-1].poss=0.0;
				obttok[flag2-1].negs=0.0;	
					
				double c=obttok[flag2].poss;
				obttok[flag2].poss=obttok[flag2].negs;
				obttok[flag2].negs=c;
			
				/*if(obttok[flag2-1].poss<obttok[flag2-1].negs&&obttok[flag2].poss<obttok[flag2].negs)
				{
					//c=obttok[flag2-1].poss;
						}
				/*else if(obttok[flag2-1].poss<obttok[flag2-1].negs&&obttok[flag2].poss>obttok[flag2].negs)
				{
					c=obttok[flag2].poss;
					obttok[flag2].poss=obttok[flag2].negs;
					obttok[flag2].negs=c;
				}
				/*else if(obttok[flag2-1].poss>obttok[flag2-1].negs&&obttok[flag2].poss<obttok[flag2].negs)
				{
					c=obttok[flag2-1].poss;
					obttok[flag2-1].poss=obttok[flag2-1].negs;
					obttok[flag2-1].negs=c;
				}*/
			}
		
			flag2=0;
		}
	
	}


	void mergeSort(String arr[],int size){

		partition(arr,0,size-1);

	}

	void partition(String arr[],int low,int high){

		if(high-low+1>1){

			int mid=low+(high-low)/2;
			partition(arr,low,mid);
			partition(arr,mid+1,high);
			mergeSub(arr,low,high,mid);
		}

	return;
	}

	void mergeSub(String arr[],int low,int high,int mid){
	
		int i=low;
		int k=0;
		int j=mid+1;
		String[] out = new String[high-low+1];
		while(i!=mid+1 && j!=high+1){

			if(arr[i].compareTo(arr[j])<0)//ar[i]<ar[j]
			{
				
				out[k]=arr[i];
				i++;
				k++;
			}
			else{

				out[k]=arr[j];
				j++;
				k++;
			}
		}

		while (i <= mid)
		{
			out[k] = arr[i];
			k++;
			i++;
  	  	}

    	while (j <= high)
   	 	{
        	out[k] = arr[j];
			k++;
			j++;
		}

		for(i=low,j=0;i<(low+k) && j<k;i++,j++){

			arr[i]=out[j];
		}

	}

	public boolean binSearch(String arr[],String key,int size) 
   {
         int low = 0;
         int high = size - 1;
          
         while(high >= low) {
            int middle = (low + high) / 2;
             if(arr[middle].equals(key)) {
                 return true;
             }
             if(arr[middle].compareTo(key)<0) {
                 low = middle + 1;
             }
             if(arr[middle].compareTo(key)>0) {
                 high = middle - 1;
             }
        }
        return false;
   }

	

}
