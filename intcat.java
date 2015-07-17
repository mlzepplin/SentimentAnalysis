import java.util.*; 
import java.io.*;
import Jama.Matrix;
class intcat{
	public static void main(String args[])throws IOException,ClassNotFoundException{
		
		Nn obj;//theta's initialised to random
		obj = new Nn();
		nlpSA nlpObject = new nlpSA();//nlp object created

		String temp;
		String oneLine;
		int size;
		double pos;
		double neg;
		obj.setAlpha(0.03);
		obj.setLambda(.5);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		BufferedReader br2 = null;
		System.out.println("enter the no. of training examples");
		temp = br.readLine();
		size = Integer.parseInt(temp);
		obj.setM(size);
		for(int i=0 ; i<100 ; i++){
			
			obj.backProp("temp");
			
		}
		System.out.println("training done, now test:");
		System.out.println("enter a sentence and save it in the file userinput ans press enter in terminal");
		br.readLine();

		nlpObject.incArrayBuilder();
		nlpObject.decArrayBuilder();
		nlpObject.invArrayBuilder();

		try {

			double userPos;
			double userNeg;

			FileReader fr = new FileReader("userinput");
			br2 = new BufferedReader(fr);

			while ((oneLine = br2.readLine()) != null){

			
				nlpObject.tagging(oneLine);


				nlpObject.incPolarities();
				nlpObject.decPolarities();
				nlpObject.invPolarities();

				nlpObject.sentencePolarity();

				userPos= nlpObject.effPos;
				
				userNeg = nlpObject.effNeg;

				obj.forwardProp(nlpObject.effPos,nlpObject.effNeg);

		Matrix result ;
		result = obj.getHypothesis();
		if(result.get(0, 0)>result.get(0, 1)&&result.get(0, 0)>result.get(0, 2))
			System.out.println("sentiment: Positive");
		if(result.get(0, 1)>result.get(0, 0)&&result.get(0, 1)>result.get(0, 2))
			System.out.println("sentiment: Negative");
		if(result.get(0, 2)>result.get(0, 0)&&result.get(0, 2)>result.get(0, 1))
			System.out.println("sentiment: Objective");
		//result.print(3, 3);

				

				nlpObject.effPos = 0.0;
				nlpObject.effNeg = 0.0;

			}

			} catch (FileNotFoundException ex) {

			System.out.println("Unable to open file '" + "userinput" + "'");

		} catch (IOException ex) {

			System.out.println("Error reading file '" + "userinput" + "'");
			
		} finally {

			try {
				
				if(br2 != null){
					
					br2.close();
				}
				
				
			} catch (IOException e) {
				
			}
			
		}
		/*System.out.println("enter posScore");
		temp = br.readLine();
		pos = Double.parseDouble(temp);
		System.out.println("enter negScore");
		temp = br.readLine();
		neg = Double.parseDouble(temp);*/
		
		
		
	}
}
class Nn{
	
	private int i,j;//help variables
	private int m;//size of training set
	private double J=0.0;//cost
	private double alpha;//rate for gradient decsent
	private double lambda;//regularization parameter
	private Matrix Y;//the actual result vector
	private double[][] t1;
	private double[][] t2;
	private double[][] t3;
	private Matrix theta1;
	private Matrix theta2;
	private Matrix theta3;
	private Matrix X;//training example vector , to be changed and used for diff training egs 
	private Matrix A2;//1st hidden layer i.e. layer-2
	private Matrix A3;//2nd hidden layer i.e. layer-3
	private Matrix H;//hypothesis vector corr to one training eg.
	private Matrix big_del1;
	private Matrix big_del2;
	private Matrix big_del3;
	private Matrix D1;
	private Matrix D2;
	private Matrix D3;
	
	//theta's initialized to random in constructor
	public Nn(){

		Random r=new Random();
		X=new Matrix(new double[3],1);//vector with two features and a bias term
		Y=new Matrix(new double[3],1);
		
		//allocating theta1
		t1=new double[4][3];
		theta1=new Matrix(t1);
		
		//random initialisation of theta1
		for(i=0;i<4;i++){

			for(j=0;j<3;j++){

				double eg =  r.nextDouble();
				System.out.println(eg);
				theta1.set(i, j, eg);

			}
		}

		System.out.println("theta1_cons");
		theta1.print(3, 3);
		
		for(i=0;i<4;i++){

			for(j=0;j<3;j++){
				
				System.out.print(theta1.get(i,j)+"   ");
		
			}

			System.out.println();

		}
		
		//allocating theta2
		t2=new double[4][5];
		theta2=new Matrix(t2);
		
		//random initialisation of theta2
		for(i=0;i<4;i++){

			for(j=0;j<5;j++){

				theta2.set(i, j, r.nextDouble());

			}

		}
		
		
		//allocating theta3
		t3=new double[3][5];
		theta3=new Matrix(t3);
		
		//random initialisation of theta3
		for(i=0;i<3;i++){

			for(j=0;j<5;j++){

				theta3.set(i, j, r.nextDouble());

			}

		}
		

	}

	public void setM(int x){
		m=x;
	}

	public int getM(){
		return m;
	}
	
	public void setAlpha(double x){
		alpha=x;
	}

	public void setLambda(double x){
		lambda=x;
	}

	public Matrix getHypothesis(){
		return H;
	}


	//a helper function to return a new copy of a matrix, not the referance
	public Matrix matrixCopy(Matrix B){
		
		double t[][] = new double[B.getRowDimension()][B.getColumnDimension()];
		Matrix temp = new Matrix(t);

		for(int i=0;i<B.getRowDimension();i++){

			for(int j=0;j<B.getColumnDimension();j++){

				temp.set(i, j, B.get(i, j));
			}
		}
		return temp;
		
	}

	public void forwardProp(double posScore,double negScore){
	
		//feeding the training example into X and Y of obj
		X.set(0, 0, 1.0);
		X.set(0,1,posScore);
		X.set(0, 2,negScore);
		System.out.println("X:");
		X.print(3, 3);
		
		Matrix thetacopy1 = matrixCopy(theta1);
		Matrix thetacopy2 = matrixCopy(theta2);
		Matrix thetacopy3 = matrixCopy(theta3);

		
		//WORKING FOR A2 i.e. layer-2
		A2=new Matrix(new double[5],1);
		Matrix a2=X.times(thetacopy1.transpose());
		System.out.println("a2:");
		a2.print(3, 3);
		
		for(i=1,j=0;i<5;i++,j++){

			A2.set(0,i,sigmoid(a2.get(0,j)));
		}
		
		//setting the bias term
		A2.set(0, 0, 1);

		System.out.println("A2:");
		A2.print(3, 3);

		
		//WORKING FOR A3
		A3=new Matrix(new double[5],1);
		Matrix a3=A2.times(thetacopy2.transpose());
		
		for(i=1,j=0;i<5;i++,j++){

			A3.set(0,i,sigmoid(a3.get(0,j)));
		}
		
		//setting the bias term
		A3.set(0, 0, 1);
		

		//WORKING FOR H
		H=A3.times(thetacopy3.transpose());
		
		for(i=0;i<3;i++) {

			H.set(0, i,sigmoid(H.get(0, i)));
		}
		
		//System.out.println("H:");
		//H.print(3, 3);
		
	}
	
	//implementing cost function
	public void regCostFunction(){
		Matrix temp = H;
		Matrix temp2 = Y;
		Matrix temp3 = H;
		for(i=0;i<H.getColumnDimension();i++){
		
			temp.set(0, i, Math.log(H.get(0, i)));
			
			}
		
		temp.times(Y.transpose());
		//first half done
		
		for(i=0;i<Y.getColumnDimension();i++){
			
			temp2.set(0, i, 1-Y.get(0, i));
		}
		
		for(i=0;i<H.getColumnDimension();i++){
			
			temp3.set(0, i, Math.log(1-H.get(0, i)));
		}
		
		temp3.times(temp2.transpose());
		//second half done
		
		temp.plusEquals(temp3);
		
		
		//calculating the regularised term
		double acc=0.0;
		for(i=0;i<theta1.getRowDimension();i++){

				for(j=1;j<theta1.getColumnDimension();j++){

					acc+=(theta1.get(i, j)*theta1.get(i, j));

				}

		}
		
		for(i=0;i<theta2.getRowDimension();i++){

				for(j=1;j<theta2.getColumnDimension();j++){

					acc+=(theta2.get(i, j)*theta2.get(i, j));

				}
		}
		
		for(i=0;i<theta3.getRowDimension();i++){

				for(j=1;j<theta3.getColumnDimension();j++){

					acc+=(theta3.get(i, j)*theta3.get(i, j));

				}
		}
		//calculating and updating the cost function
		J+=(-1/m)*temp.get(0, 0)+(lambda/(2*m))*acc;
		
	}
		
	
	//BACK PROP
	public void backProp(String input)throws IOException{
		
		//initialising big_dels
		
		double [][]b1=new double[4][3];
		big_del1= new Matrix(b1);
				
		for(i=0;i<theta1.getRowDimension();i++){

			for(j=0;j<theta1.getColumnDimension();j++){

				big_del1.set(i, j, 0);
			}
		}
				
		//big_del2=new Matrix(t2);
		double [][]b2=new double[4][5];
		big_del2= new Matrix(b2);
		
		for(i=0;i<theta2.getRowDimension();i++){

			for(j=0;j<theta2.getColumnDimension();j++){

				big_del2.set(i, j, 0);
			}
		}
				
		double [][]b3=new double[3][5];
		big_del3= new Matrix(b3);
				
		for(i=0;i<theta3.getRowDimension();i++){

			for(j=0;j<theta3.getColumnDimension();j++){

				big_del3.set(i, j, 0);
			}
		}
		
		String fileName = input;
		String line = null;
		BufferedReader br = null;
		try{
			
			FileReader fileReader = new FileReader(fileName);
			br= new BufferedReader(fileReader);

			for(int iter=1;iter<=m;iter++){
				line = br.readLine();
				StringTokenizer st = new StringTokenizer(line);
				String s;

				double p;double  neg;double a;double b;double c;
				

				s = st.nextToken();
				p = Double.parseDouble(s);
				s = st.nextToken();
				neg= Double.parseDouble(s);
				s = st.nextToken();
				a = Double.parseDouble(s);
				s = st.nextToken();
				b = Double.parseDouble(s);
				s = st.nextToken();
				c = Double.parseDouble(s);
				
				forwardProp(p,neg);
				//initialising Y
				Y.set(0, 0, a);
				Y.set(0, 1, b);
				Y.set(0, 2, c);
				
				//DELTA_4
				Matrix delta_4=new Matrix(new double[3],1);
				for(i=0;i<3;i++) {

					delta_4.set(0, i, 0);
				}
			
				delta_4=H.minus(Y);
			
			
				//DELTA_3
				Matrix delta_3=new Matrix(new double[5],1);
				delta_3=delta_4.times(theta3);
				
				Matrix temp=new Matrix(new double[A3.getColumnDimension()],1);
				for(i=0;i<A3.getColumnDimension();i++)	{

					temp.set(0, i, 1);
				}
				
				temp.minusEquals(A3);
				
				for(i=0;i<temp.getColumnDimension();i++) {

					temp.set(0, i, A3.get(0, i)*temp.get(0,i));
				}
				
				for(i=0;i<temp.getColumnDimension();i++) {

					delta_3.set(0, i, delta_3.get(0,i)*temp.get(0, i));
				}
				
				//DELTA_2
				Matrix delta_2=new Matrix(new double[5],1);
				
				//shedded delta_3->delta_3_s
				Matrix delta_3_s=new Matrix(new double[delta_3.getColumnDimension()-1],1);
				for(i=0,j=1;i<delta_3_s.getColumnDimension();i++,j++){

					delta_3_s.set(0,i,delta_3.get(0,j));
				}

				delta_2=delta_3_s.times(theta2);
				
				Matrix temp2=new Matrix(new double[A2.getColumnDimension()],1);
				for(i=0;i<A2.getColumnDimension();i++){

					temp2.set(0, i, 1);
				}

				temp2.minusEquals(A2);
				
				for(i=0;i<temp.getColumnDimension();i++){

					temp2.set(0, i, A2.get(0, i)*temp2.get(0,i));
				}
				
				for(i=0;i<temp.getColumnDimension();i++){

					delta_2.set(0, i, delta_2.get(0,i)*temp2.get(0, i));
				}

				//shedding delta_2->delta_2_s	
				Matrix delta_2_s=new Matrix(new double[delta_2.getColumnDimension()-1],1);
				for(i=0,j=1;i<delta_2_s.getColumnDimension();i++,j++){

					delta_2_s.set(0,i,delta_2.get(0,j));
				}
				//delta's without bias done
			
				//updating big dels
			
				//i.e.GETTING THE GRADIENTS
				big_del1.plusEquals((delta_2_s.transpose()).times(X));
				big_del2.plusEquals((delta_3_s.transpose()).times(A2));
				big_del3.plusEquals((delta_4.transpose()).times(A3));
			
			}
			
			D1 = big_del1;
			D2 = big_del2;
			D3 = big_del3;
			
			D1.timesEquals(1/m);
			D2.timesEquals(1/m);
			D3.timesEquals(1/m);
			
			//regularising the gradients
			for(i=0;i<big_del1.getRowDimension();i++){

				for(j=1;j<big_del1.getColumnDimension();j++){

					D1.set(i, j,big_del1.get(i, j)+(lambda/m)*theta1.get(i, j));
				}
			}

			for(i=0;i<big_del2.getRowDimension();i++){

				for(j=1;j<big_del2.getColumnDimension();j++){

					D2.set(i, j,big_del2.get(i, j)+(lambda/m)*theta2.get(i, j));
				}
			}

			for(i=0;i<big_del3.getRowDimension();i++){

				for(j=1;j<big_del3.getColumnDimension();j++){

					D3.set(i, j,big_del3.get(i, j)+(lambda/m)*theta3.get(i, j));
				}
			}
			
			gradientDescent();
			
			System.out.println("theta1");
			theta1.print(3,3);

				

			
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
	
	public void gradientDescent(){
		//updating theta matrices
		theta1 = theta1.minus(D1.timesEquals((-1*alpha)));
		theta2 = theta2.minus(D2.timesEquals((-1*alpha)));
		theta3 = theta3.minus(D3.timesEquals((-1*alpha)));
	}
	
	//printing gradients
	//big_del1.print(big_del1.getColumnDimension(),3 );
	
	//gradient descent
	//theta1.minusEquals(big_del1);
	//theta2.minusEquals(big_del2);
	//theta3.minusEquals(big_del3);
	public double sigmoid(double input){
		double a = 1 / (1 + Math.exp(-input));
		return a;
	}
}
