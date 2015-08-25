/**
 * 
 */


import java.util.ArrayList;
import java.net.ServerSocket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.net.Socket;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.Date;


/**
 * @author gmbharath
 * 
 * Following is an implementation for Maekawa Mutual Exclusion Algorithm for Distributed Systems.
 * This file act as a Client  
 * References: 
 * ￼A N^1/2 Algorithm for Mutual Exclusion in Decentralized Systems - Paper by MAMORU MAEKAWA
 * Head First Java 2nd Edition
 * Idea taken from http://www.cs.uic.edu/~ajayk/Chapter9.pdf
 * Idea taken from https://github.com/jeremyjohnston/ricart-agrawala
 * 
 */


public class Client 
{
	public static final String request = "REQUEST";
	public static final String cSection = "CRITICAL SECTION";
	public static final String grantRecvd = "GRANT RECEIVED";
	public long latency;
	int clientNumber; 
	int totalNoOfServers = 7;
	Quorum clientMessages;

	BufferedReader clientReader1;
	BufferedReader clientReader2;
	BufferedReader clientReader3;
	BufferedReader clientReader4;
	BufferedReader clientReader5;
	BufferedReader clientReader6;
	BufferedReader clientReader7;

	PrintWriter clientWriter1;
	PrintWriter clientWriter2;
	PrintWriter clientWriter3;
	PrintWriter clientWriter4;
	PrintWriter clientWriter5;
	PrintWriter clientWriter6;
	PrintWriter clientWriter7;



	//Client Constructor to initialize some data
	public Client() throws NumberFormatException, IOException
	{
		clientNumber = getNodeNumber();
//	    System.out.println("Client Number: "+ clientNumber);


		try {
			//To Do. Initialize inside "if" to suppress the warnings.
			Socket socket1 = null;
			Socket socket2 = null;
			Socket socket3 = null;
			Socket socket4 = null;
			Socket socket5 = null;
			Socket socket6 = null;
			Socket socket7 = null;

			//create sockets.
			if(clientNumber == 1)
			{
				socket1 = new Socket("dc11.utdallas.edu", 8001);
				socket2 = new Socket("dc12.utdallas.edu", 8001);
				socket3 = new Socket("dc13.utdallas.edu", 8001);
				socket4 = new Socket("dc14.utdallas.edu", 8001);
				socket5 = new Socket("dc15.utdallas.edu", 8001);
				socket6 = new Socket("dc16.utdallas.edu", 8001);
				socket7 = new Socket("dc17.utdallas.edu", 8001);
			}

			if(clientNumber == 2)
			{
				socket1 = new Socket("dc11.utdallas.edu", 8002);
				socket2 = new Socket("dc12.utdallas.edu", 8002);
				socket3 = new Socket("dc13.utdallas.edu", 8002);
				socket4 = new Socket("dc14.utdallas.edu", 8002);
				socket5 = new Socket("dc15.utdallas.edu", 8002);
				socket6 = new Socket("dc16.utdallas.edu", 8002);
				socket7 = new Socket("dc17.utdallas.edu", 8002);
			}


			if(clientNumber == 3)
			{
				socket1 = new Socket("dc11.utdallas.edu", 8003);
				socket2 = new Socket("dc12.utdallas.edu", 8003);
				socket3 = new Socket("dc13.utdallas.edu", 8003);
				socket4 = new Socket("dc14.utdallas.edu", 8003);
				socket5 = new Socket("dc15.utdallas.edu", 8003);
				socket6 = new Socket("dc16.utdallas.edu", 8003);
				socket7 = new Socket("dc17.utdallas.edu", 8003);
			}

			if(clientNumber == 4)
			{
				socket1 = new Socket("dc11.utdallas.edu", 8004);
				socket2 = new Socket("dc12.utdallas.edu", 8004);
				socket3 = new Socket("dc13.utdallas.edu", 8004);
				socket4 = new Socket("dc14.utdallas.edu", 8004);
				socket5 = new Socket("dc15.utdallas.edu", 8004);
				socket6 = new Socket("dc16.utdallas.edu", 8004);
				socket7 = new Socket("dc17.utdallas.edu", 8004);
			}

			if(clientNumber == 5)
			{
				socket1 = new Socket("dc11.utdallas.edu", 8005);
				socket2 = new Socket("dc12.utdallas.edu", 8005);
				socket3 = new Socket("dc13.utdallas.edu", 8005);
				socket4 = new Socket("dc14.utdallas.edu", 8005);
				socket5 = new Socket("dc15.utdallas.edu", 8005);
				socket6 = new Socket("dc16.utdallas.edu", 8005);
				socket7 = new Socket("dc17.utdallas.edu", 8005);
			}    	   

			//below is the code to write to a file. Used for Data Collection. For demo: printing on the screen. 

			// System.out.println("****************Writing to file************************");
			//  writeToFile();

//		 System.out.println("Sockets are created. Now establishing the connection at every client");


			Thread.sleep(25000);
			clientWriter1 = new PrintWriter(socket1.getOutputStream(),true);
			clientWriter2 = new PrintWriter(socket2.getOutputStream(),true);
			clientWriter3 = new PrintWriter(socket3.getOutputStream(),true);
			clientWriter4 = new PrintWriter(socket4.getOutputStream(),true);
			clientWriter5 = new PrintWriter(socket5.getOutputStream(),true);
			clientWriter6 = new PrintWriter(socket6.getOutputStream(),true);
			clientWriter7 = new PrintWriter(socket7.getOutputStream(),true);

			clientReader1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
			clientReader2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
			clientReader3 = new BufferedReader(new InputStreamReader(socket3.getInputStream()));
			clientReader4 = new BufferedReader(new InputStreamReader(socket4.getInputStream()));
			clientReader5 = new BufferedReader(new InputStreamReader(socket5.getInputStream()));
			clientReader6 = new BufferedReader(new InputStreamReader(socket6.getInputStream()));
			clientReader7 = new BufferedReader(new InputStreamReader(socket7.getInputStream()));

			//  System.out.println("Writer and Reader objects initialzed..");
			  //System.out.println("Connections are now established between client and the server");

			clientMessages = new Quorum(clientNumber, 0);
			clientMessages.writer[1] = clientWriter1;
			clientMessages.writer[2] = clientWriter2;
			clientMessages.writer[3] = clientWriter3;
			clientMessages.writer[4] = clientWriter4;
			clientMessages.writer[5] = clientWriter5;
			clientMessages.writer[6] = clientWriter6;
			clientMessages.writer[7] = clientWriter7;

			Thread.sleep(20000);
		// System.out.println("Creating new threads for eack socket connection.(Client to Server. One to many relationship)");


			//create new thread for each socket connection. Each client will be connected to all the servers
			//Eg,: Client1 to all 7 servers initially. For each connection between client and server new thread is created 
			//On each thread message read and write happens.  

			Thread clientThread1 = new Thread(new MessageHandler(socket1,clientMessages));
			clientThread1.start();
			Thread clientThread2 = new Thread(new MessageHandler(socket2,clientMessages));
			clientThread2.start();
			Thread clientThread3 = new Thread(new MessageHandler(socket3,clientMessages));
			clientThread3.start();
			Thread clientThread4 = new Thread(new MessageHandler(socket4,clientMessages));
			clientThread4.start();
			Thread clientThread5 = new Thread(new MessageHandler(socket5,clientMessages));
			clientThread5.start();
			Thread clientThread6 = new Thread(new MessageHandler(socket6,clientMessages));
			clientThread6.start();
			Thread clientThread7 = new Thread(new MessageHandler(socket7,clientMessages));
			clientThread7.start();


			int count = 0;
			//execute for 20 critical sections.
			while (count < 20)
			{
			    //System.out.println("Requesting Resource");
				boolean enteredCriticalSection = false;
				do
				{
					long startTime = System.currentTimeMillis();
					enteredCriticalSection = clientMessages.requestResource();
					long endTime   = System.currentTimeMillis();
					long elapsedTime = endTime - startTime;
					latency = elapsedTime;
				}
				while (!enteredCriticalSection);
				
				//System.out.println("CS granted");
				
				Timestamp k = getCurrentTimeStamp();

			    System.out.println("Entering CS!!!"+ " Client Number : " + clientNumber + 
					    				  " Current time : " + k);
			    
				//300 millisecs is the time it will be in critical section.
				Thread.sleep(300);
				System.out.println("RELEASE");
				clientMessages.releaseCriticalSection();
				System.out.println("Resources Released");
				count++;
				System.out.println("\n");
				System.out.println("Request " + count);
				System.out.println("\n");
				System.out.println("Latency (elapsed time) --> "+ latency);
				System.out.println("\n");
				System.out.println("Messages sent to access Critical section--> "+ clientMessages.msgSentToAccessCS);
				System.out.println("\n");
				System.out.println("Messages received to access Critical section"+ clientMessages.msgReceived);	
				System.out.println("\n");

				//After CS, client waits for a period of time that is uniformly distributed in the range [5, 10] time units 
				//before trying to enter the critical section
				int time = randomNumber(30,35);
				Thread.sleep(time*100);
			}

			System.out.println("Total messages Sent..."+ clientMessages.totalMessageSent);
			System.out.println("Total messages received..."+ clientMessages.totalMessageReceived);

			//now send a complete message to all the servers.
			clientMessages.sendComplete();

		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			System.out.println("Exception in client side" + e);
		}

	}
	
	//gets the current time stamp
	public static Timestamp getCurrentTimeStamp()
	{
		 java.util.Date date= new java.util.Date();
		 return (new Timestamp(date.getTime()));
	}

	//Write all messages to a file 
	public static void writeToFile() throws IOException
	{
		File fileDescriptor = new File("log.txt");
		FileOutputStream outputStream = new FileOutputStream(fileDescriptor);
		PrintStream printStream = new PrintStream(outputStream);
		System.setOut(printStream);

	}

	//generates random number between two given values
	public static int randomNumber(int x, int y)
	{
		Random rand = new Random();
		int randomNum = rand.nextInt((y-x)+1) + x;
		return randomNum;	 
	}

	/* gets the node number. Made it as private because it shouldn't be accessed by external thread*/
	private int getNodeNumber() throws IOException, NumberFormatException
	{
		File file = new File("clientNode.txt");
		BufferedReader reader = null;
		try {
			//read the file
			reader = new BufferedReader(new FileReader(file));
			String strNodeNumber = null;
			while ((strNodeNumber = reader.readLine()) != null)
			{
				int nodeNumber = Integer.parseInt(strNodeNumber);
				return nodeNumber;
			}
		}
		catch (FileNotFoundException e)
		{
			// handle exception
			e.printStackTrace();
			System.out.println("File Not Found Exception!!");
		}
		return 5;
	}

	// START Client Quorum


	public class Quorum 
	{
		//totalMessageSent is the total number of messages sent to access/release CS
		public int totalMessageSent;
		//totalMessageReceived is the total number of messages received to access/release CS
		public int totalMessageReceived;
		//messages sent to access CS
		public int msgSentToAccessCS;
		//messages received to access CS
		public int msgReceived;

		public int myClientNumber;
		public int totalNumberOfServers = 7;

		public int ourSeqNum;
		public int highSeqNum;

		public boolean[] A;
		public boolean isLocked;
		public PrintWriter[] writer;
		int root = 1;

		public long latency;

		public Quorum(int self, int ourSeqNum)
		{
			this.myClientNumber = self;
			this.ourSeqNum = ourSeqNum;

			//boolean array to keep track of the requests. Initially all will be false. Once the request is made it is made true. 
			A = new boolean[totalNumberOfServers+1];
			//complete = new boolean[totalNumberOfServers+1];	
			isLocked = false;		
			writer = new PrintWriter[totalNumberOfServers+1];
		}

		public boolean requestResource() throws InterruptedException
		{
			msgSentToAccessCS = 0;
			msgReceived = 0;

			for (int i=1; i <= totalNumberOfServers ; i++)
			{
				//initially all false
				A[i] = false;
			}

			//dummy placeholder not used for any thing. Acts as an sentinel
			//Server number 
			A[0] = true;

			// clock value
			ourSeqNum = highSeqNum + 1;

			for (int i=1;  i<=totalNumberOfServers; i++)
			{
				totalMessageSent++;
				msgSentToAccessCS++;
				writer[i].println("REQUEST," +ourSeqNum+ "," + myClientNumber + "," + i);
			}

			//To check for Deadlock
			//if the quorum() is false, then there's a deadlock. Less possibility for deadlock to happen in Maekawa for the 
			//given requirements.
			for (int i = 0; i<50; i++)
			{
				Thread.sleep(50);
				if (quorum(root))
				{
					System.out.println("NO Deadlock");

					return true;
				}
			}
			System.out.println("Deadlock");
			return false;

		}


		//exits from the critical section. releases all resources.
		public void releaseCriticalSection()
		{
			//System.out.println("Releasing CS");
			for (int i=1; i<=totalNumberOfServers; i++)
			{
				totalMessageSent++;
				writer[i].println("RELEASE," +ourSeqNum+ "," + myClientNumber + "," + i);
			}
		}


		//Client receives GRANT message from the server. 
		public void receiveGrant(int serverNumber)
		{
			msgReceived++;
			totalMessageReceived++;
			//System.out.println("Received Grant from the server: " + serverNumber + " and making it true");
			A[serverNumber] = true;
		}

		//complete message is sent to the server after executing CS for 20 times.
		public void sendComplete()
		{
			//System.out.println("Sending complete message to the Server");
			writer[1].println("COMPLETE," + myClientNumber);
		}


		// algorithm tries to construct quorums in a way that each quorum
		// represents any path from the root to a leaf
		public void getQuorum(int root)
		{
			int i = root;
			int leftSubtree = 2*i;
			int rightSubtree = (2*i)+1;
			if(root == 0)
			{
				System.out.println("Empty tree");
			}	
			
			  A[0] = true;
			 
			  /* it's a leaf, so print the path that led to here  */
			  if (leftSubtree == 0  && rightSubtree == 0) 
			  {
				  System.out.println("Unsuccessful in forming a quorum");
			  }
			  else
			  {
			    /* otherwise try both subtrees */
			    getQuorum(leftSubtree);
			    getQuorum(rightSubtree);
			  }

		}

		//check the quorum if it's valid or not
		public boolean quorum(int root)
		{
			//A[1] is a root of the binary tree and A[2], A[3] left and right child of the root node respectively
			if (root > ((totalNumberOfServers-1)/2))
			{
				return A[root];
			}
			//In a binary tree given a position of the node, say 'i', parent will be at i/2 position
			//left child 2*i and right child (2*i + 1)
			if ((quorum((2*root)) && quorum((2*root)+1)) || (A[root] && quorum((2*root))) || (quorum((2*root)+1) && A[root]))
			{
				return true;
			}
			else
			{
				return false;
			}	
		}
	}

	// END Quorum**************




	//start Client Message Handler
	public class MessageHandler implements Runnable 
	{
		Socket socket;
		BufferedReader reader;
		PrintWriter writer;
		Quorum client;

		public MessageHandler(Socket s, Quorum client)
		{
			try
			{
				this.client  = client;
				socket = s;
				InputStreamReader iReader = new InputStreamReader(socket.getInputStream());
				reader = new BufferedReader(iReader);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}	

		public void run() 
		{
			String msg;

			try {
				while ((msg = reader.readLine()) != null)
				{								
					// Read Message from Channel
					//System.out.println("Message received from the server... Parse the message and check if its \"GRANT or EXIT\" "+msg);
					String message[] = msg.split(",");
					//System.out.println(message[0]);

					if (message[0].equals("GRANT"))
					{
						//System.out.println("Received Grant from Server " + message[2]);
						client.receiveGrant(Integer.parseInt(message[2]));
					}

					if(message[0].equals("EXIT"))
					{
						//System.out.println("Client---->\"Exit message received from the server 1 \" ");
						System.exit(0);
					}
				}
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//END Message Handler


	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{

		//initialize client
		try 
		{
			new Client();

		} catch (Exception e)
		{
			// handle exception
			System.out.println("Exception in Initializing Client!");

		}
	}
}
