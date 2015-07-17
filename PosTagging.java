import java.io.*;
import java.util.*;
//import java.lang.*;

public class PosTagging{
	class Tokenst
	{
		String word;
		String tag;
		char posid;
		double poss;
		double negs;
	}

	public static void main(String[] args)   throws IOException,ClassNotFoundException {

        BufferedReader bufferedReader = null;
		try {

			String fileName = "sentence";
			FileReader fileReader = new FileReader(fileName);
			bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {
		

				// Initialize the tagger
        		MaxentTagger tagger = new MaxentTagger("taggers/english-bidirectional-distsim.tagger");


        		// The tagged string
	        	String tagged = tagger.tagString(sample);


				String line = tagged;
				int size,i,j;
				int[] mems;
				Tokenst[] obttok;
				double effPos=0.0;
				double effNeg=0.0;
				item saitem=null;

				// Tokenisation begins	
				StringTokenizer st = new StringTokenizer(line,"_' '");
				i=0;
				size=st.countTokens()/2;

				mems=new int[size];

				for(j=0;j<size;j++)
				{
					mems[j]=0;
				}

				obttok=new Tokenst[size];

				while(st.hasMoreTokens()) {


					obttok[i].word = st.nextToken(); 
					obttok[i].tag = st.nextToken();

					if(obttok[i].word.length()>2)
					{
						if(obttok[i].tag=="JJ"||obttok[i].tag=="JJR"||obttok[i].tag=="JJS")
							{
								obttok[i].posid='a';
								saitem=findWord(obttok[i].word,obttok[i].posid);
								//call find_word with a and key and return + - values
							}
						else if(obttok[i].tag=="RB"||obttok[i].tag=="RBR"||obttok[i].tag=="RBS"||obttok[i].tag=="WRB")
							{
								mems[i]=1;
								obttok[i].posid='r';
								saitem=findWord(obttok[i].word,obttok[i].posid);
								//call find_word with r and key and return + - values
							}
						else if(obttok[i].tag=="NN"||obttok[i].tag=="NNS"||obttok[i].tag=="NNP"||obttok[i].tag=="NNPS"||obttok[i].tag=="PRP"||obttok[i].tag=="PRP$"||obttok[i].tag=="WP"||obttok[i].tag=="WP$")
							{
								obttok[i].posid='n';
								saitem=findWord(obttok[i].word,obttok[i].posid);
								//call find_word with n and key and return + - values
							}
						else if(obttok[i].tag=="VB"||obttok[i].tag=="VBD"||obttok[i].tag=="VBG"||obttok[i].tag=="VBN"||obttok[i].tag=="VBP"||obttok[i].tag=="VBZ")
							{
								obttok[i].posid='v';
								saitem=findWord(obttok[i].word,obttok[i].posid);
								//call find_word with v and key and return + - values
							}
						else
							{
								obttok[i].posid='n';
								saitem=findWord(obttok[i].word,obttok[i].posid);
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

					i++;
				}
			}
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");

		} catch (IOException ex) {

			System.out.println("Error reading file '" + fileName + "'");
		} finally {

			try {
				if(bufferedReader != null){

					bufferedReader.close();

				}	
			} catch (IOException e) {
				//////////
			}
		}
		
		String fName = "incrementers";
		String l = null;
		BufferedReader br = null;
		int flag=0;
		try {

			FileReader fr = new FileReader(fName);
			br = new BufferedReader(fr);

			while ((l = br.readLine()) != null) {
			
				for(i=0;i+1<size;i++)
				{
					if(mems[i]==1&&obttok[i].word.equals(l))
					{
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
				//System.out.println(l);
			}

		} catch (FileNotFoundException ex) {

			System.out.println("Unable to open file '" + fName + "'");

		} catch (IOException ex) {

			System.out.println("Error reading file '" + fName + "'");
			
		} finally {

			try {
				
				if(br != null){
					
					br.close();
				}
				
			} catch (IOException e) {
				////////
			}
			
		}

		String fName1 = "decrementers";
		String l1 = null;
		BufferedReader br1 = null;
		int flag1=0;

		try {

			FileReader fr1 = new FileReader(fName1);
			br1 = new BufferedReader(fr1);

			while ((l1 = br1.readLine()) != null) {
			
				for(i=0;i+1<size;i++)
				{
					if(mems[i]==1&&obttok[i].word.equals(l1))
					{
						flag1=i+1;
					}
				}
			
				if(flag1!=0)
				{
					obttok[flag1].poss=obttok[flag1].poss-0.1;
					obttok[flag1].negs=obttok[flag1].negs-0.1;
					obttok[flag-1].poss=0.0;
					obttok[flag-1].negs=0.0;

				}
			
				flag1=0;
				//System.out.println(l);
			}

		} catch (FileNotFoundException ex) {

			System.out.println("Unable to open file '" + fName1 + "'");

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

		String fName2 = "inverters";
		String l2 = null;
		BufferedReader br2 = null;
		int flag2=0;

		try {

			FileReader fr2 = new FileReader(fName2);
			double c;
			br2 = new BufferedReader(fr2);

			while ((l2 = br2.readLine()) != null) {
			
				for(i=0;i+1<size;i++)
				{
					if(mems[i]==1&&obttok[i].word.equals(l2))
					{
						flag2=i+1;
					}
				}
			
				if(flag2!=0)
				{

					obttok[flag2-1].poss=0.0;
					obttok[flag2-1].negs=0.0;	
						
					c=obttok[flag2].poss;
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
				//System.out.println(l);
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

		int numOfValidWords = 0;

		for(int i=0;i<size;i++){

			if(obttok[i].poss!=0.0 || obttok[i].negs!=0.0){

				effPos += obttok[i].poss;
				effNeg += obttok[i].neg;
				numOfValidWords++;

			}
		}



	}
}



