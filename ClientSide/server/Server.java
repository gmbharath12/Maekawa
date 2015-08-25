/**
 * 
 */
package server;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author gmbharath
 * 
 * Following is an implementation for Maekawa Mutual Exclusion Algorithm for Distributed Systems.
 * This File act as a Server 
 * 
 * References: 
 * ￼A N^1/2 Algorithm for Mutual Exclusion in Decentralized Systems - Paper by MAMORU MAEKAWA
 * Head First Java 2nd Edition
 * http://www.cs.uic.edu/~ajayk/Chapter9.pdf
 * https://github.com/jeremyjohnston/ricart-agrawala
 * 
 */


public class Server 
{
	public static final String grant = "GRANT";
	public static final String complete = "Complete";
	public static final String requestReceived = "REQUEST";
    ServerMessages serverMsg;

	int numberOfPorts = 5;
	int serverNumber;
	
    PrintWriter serverWriter1;
    PrintWriter serverWriter2;
    PrintWriter serverWriter3;
    PrintWriter serverWriter4;
    PrintWriter serverWriter5;
    
    BufferedReader serverReader1;
    BufferedReader serverReader2;
    BufferedReader serverReader3;
    BufferedReader serverReader4;
    BufferedReader serverReader5;
    
	
	Server() throws FileNotFoundException, NumberFormatException, IOException, InterruptedException
	{
		//when server sends a GRANT message we need to identify the server. serverNumber will have that info.
		serverNumber = getServerNumber();
		System.out.println("Server Side.....");
		System.out.println("Server NO: ---> "+ serverNumber);

		ServerSocket serverSocket1;
		ServerSocket serverSocket2;
		ServerSocket serverSocket3;
		ServerSocket serverSocket4;
		ServerSocket serverSocket5;

		try {
			//creates socket at port 8001..8005
			serverSocket1 = new ServerSocket(8001);
			serverSocket2 = new ServerSocket(8002);
			serverSocket3 = new ServerSocket(8003);
			serverSocket4 = new ServerSocket(8004);
			serverSocket5 = new ServerSocket(8005);

			Socket clientSocket1 = null;
			Socket clientSocket2 = null;
			Socket clientSocket3 = null;
			Socket clientSocket4 = null;
			Socket clientSocket5 = null;

			//ask servers to accept from the client
			clientSocket1 = serverSocket1.accept();
			clientSocket2 = serverSocket2.accept();
			clientSocket3 = serverSocket3.accept();
			clientSocket4 = serverSocket4.accept();
			clientSocket5 = serverSocket5.accept();

			System.out.println("Server sockets created. Start accepting from clients");
			Thread.sleep(20);
			System.out.println("sleeping...");


			serverWriter1 = new PrintWriter(clientSocket1.getOutputStream(),true);
			serverWriter2 = new PrintWriter(clientSocket2.getOutputStream(),true);
			serverWriter3 = new PrintWriter(clientSocket3.getOutputStream(),true);
			serverWriter4 = new PrintWriter(clientSocket4.getOutputStream(),true);
			serverWriter5 = new PrintWriter(clientSocket5.getOutputStream(),true);


			serverReader1 = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));
			serverReader2 = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));
			serverReader3 = new BufferedReader(new InputStreamReader(clientSocket3.getInputStream()));
			serverReader4 = new BufferedReader(new InputStreamReader(clientSocket4.getInputStream()));
			serverReader5 = new BufferedReader(new InputStreamReader(clientSocket5.getInputStream()));

			System.out.println("Servers listening to clients.");

			serverMsg = new ServerMessages(serverNumber, 0);
			serverMsg.writer[1] = serverWriter1;
			serverMsg.writer[2] = serverWriter2;
			serverMsg.writer[3] = serverWriter3;
			serverMsg.writer[4] = serverWriter4;
			serverMsg.writer[5] = serverWriter5;

			System.out.println("Start Threads. Message read and write happens on that thread ");

			Thread serverThread1 = new Thread(new MessageHandler(clientSocket1,serverMsg));
			serverThread1.start();
			Thread serverThread2 = new Thread(new MessageHandler(clientSocket2,serverMsg));
			serverThread2.start();
			Thread serverThread3 = new Thread(new MessageHandler(clientSocket3,serverMsg));
			serverThread3.start();
			Thread serverThread4 = new Thread(new MessageHandler(clientSocket4,serverMsg));
			serverThread4.start();
			Thread serverThread5 = new Thread(new MessageHandler(clientSocket5,serverMsg));
			serverThread5.start();

			System.out.println("Every Thread Started");
		} 
		catch (Exception e) 
		{
			// handle exception
			e.printStackTrace();
			System.out.println("Exception"+e);
		}
	}
	
	//returns server number from a file.
	private int getServerNumber() throws FileNotFoundException, NumberFormatException, IOException
	{

		File file = new File("fileServer.txt");
		BufferedReader bufferedReader = null;
		try {
			//read from the file, parse the number from a file. if exception is thrown, return 1;
			bufferedReader = new BufferedReader(new FileReader(file));
			String stringServerNumber = null;
			while ((stringServerNumber = bufferedReader.readLine()) != null) 
			{
				int sNumber = Integer.parseInt(stringServerNumber);   
				return sNumber;
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			bufferedReader.close();
		}
		
		return 1;

	}
	
	//class that handle all messages. 
	public class ServerMessages 
	{

		//server number, self identification 
		public int self;
		
		//total number of servers
		public int N = 5;

		//to keep track of logical clock
		public int ourSeqNum;
		
		//boolean array to keep track as to which clients sent completion notification 
		public boolean[] complete;
		
		LinkedList priorityQueue = new LinkedList(); 

		//to indicate if a resource is locked or not by a client
		public boolean isLocked;

		// Other Variables
		public PrintWriter[] writer;

		//these messages although implemented here not required. we only need to calculate for clients
		public int totalMsgSent;
		public int totalMsgReceived;
		public int msgSent;
		public int msgReceived;
		public int lockedBy;

		ServerMessages(int self, int ourSeqNum)
		{
			this.self = self;
			this.ourSeqNum = ourSeqNum;
			isLocked = false;
			writer = new PrintWriter[N+1];
			complete = new boolean[N+1];
		}

		/*
		 * If the server is Locked by that client, the server checks its queue. If there is a REQUEST in its queue,
		 * the server dequeues the REQUEST at the head of the queue, 
		 * sends a GRANT to the corresponding client and stays in the Locked state. 
		 * Otherwise (queue is empty), the server becomes unlocked
		 */

		public void receiveRequest(int seqNum, int clientNumber)
		{
			System.out.println("Received Request with from Client" + clientNumber);
			priorityQueue.remove((Integer) clientNumber);
			if (!isLocked)
			{
				System.out.println("Not using the resource");
				sendGrant(clientNumber);
			}
			else
			{
				System.out.println("Currently resource is busy. Enqueue the requests");
				//enqueue the request
				priorityQueue.add((Integer)clientNumber);
				System.out.println("\n The queue after Request is + " +priorityQueue);
			}
			return;
		}

		//sends a GRANT message to the Client.  When client receives GRANT message, it enters
		// critical section
		public void sendGrant(int clientNumber)
		{
			//lock should be true since this server gets locked to the specific client
			isLocked = true;
			lockedBy = clientNumber;
			System.out.println("Sending GRANT message to Client "+ clientNumber);
			writer[clientNumber].println("GRANT," +ourSeqNum+ "," + self + "," + clientNumber);
			return;
		}

		//handles complete message from the client
		public void handleCompleteMessage(int i)
		{
			System.out.println(" COMPLETE message received from the client");
			//complete[0] always true. acts as a sentinel 
			complete[0] = true;
			complete[i] = true; 
			for(i = 1; i <= N; i ++)
			{
				if(complete[i] == false)
				{
					return;
				}
			}
			exit();
		}

		//exit function
		public void exit()
		{
			System.out.println("Exiting out.....");
			
			/*
			 * S1 brings the entire distributed computation to an end once 
			 * its has received completion notification from all the clients
			 */
			
			//this is written only when all the clients have executed CS.
				writer[1].println("EXIT");
				writer[2].println("EXIT");
				writer[3].println("EXIT");
				writer[4].println("EXIT");
				writer[5].println("EXIT");
		}

		//when server receive release message from client, it checks its queue if there are any pending requests in its queue 
		// and sends a GRANT message to the head of the queue. 
		public void receiveRelease(int clientNumber)
		{
			System.out.println("Received RELEASE message from the Client ---" + clientNumber);
			if(lockedBy == clientNumber)
			{
				System.out.println("Remaining requests in a queue " + priorityQueue.size() );
				if(priorityQueue.size()!=0)
				{
					//when received Release message, dequeue the request at the head and serve the next set of requests
					int x = (Integer) priorityQueue.removeFirst();
					System.out.println("Dequeued element from queue is " + x);
					sendGrant(x);
				}
				else
				{
					isLocked = false;
					lockedBy = 0;
				}
			}
			else
			{
				priorityQueue.remove((Integer)clientNumber);
			}
			return;
		}
	}
	
	
	
	//A thread class which handles Messages "REQUEST", "RELEASE", "COMPLETE"
	public class MessageHandler implements Runnable 
	{
		PrintWriter writer;
		BufferedReader reader;
		Socket socket;
		ServerMessages server;

		public MessageHandler(Socket s, ServerMessages server)
		{
			try{
				this.server  = server;
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

			try 
			{
				while ((msg = reader.readLine()) != null)
				{							
					// Read Message from Channel
					System.out.println("Server "+ server.self + "Got this message ----->" + msg);
					String text[] = msg.split(",");
					System.out.println(text[0]);

					// Request Message Received
					if (text[0].equals("REQUEST"))
					{											
						System.out.println("Treating Request Resources");
						server.receiveRequest(Integer.parseInt(text[1]),Integer.parseInt(text[2]));
						server.totalMsgReceived++;
					}
					
					// Complete Message Received
					else if (text[0].equals("COMPLETE"))
					{									
						System.out.println("Server Received Complete message from the client in Message handler");
						server.handleCompleteMessage(Integer.parseInt(text[1]));
					}

					// RELEASE Message Received from the client
					else if (text[0].equals("RELEASE"))
					{										
						System.out.println("Treating Release Resources");
						server.receiveRelease(Integer.parseInt(text[2]));
						server.totalMsgReceived++;
						server.msgReceived++;
					}
				}
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		try {
			 new Server();
			
		} catch (Exception e) {
			// handle exception
			System.out.println("Exception in initializing the server!!");
		}
	}

}
