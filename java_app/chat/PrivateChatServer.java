/* 
 * File:   ChatRoomServer.java
 * Author: Will Flores waflores@ncsu.edu
 *
 * This file implements a Robust Chat Room Server.
 *
 * Created on February 6, 2012
 */


import java.io.*;
import java.util.Collection;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.net.*;

public class PrivateChatServer implements Runnable {
	// clients key is chat name of WHO IS NOW IN THE CHAT ROOM.
	// So always start with this collection EMPTY.
	// (So this collection need not be saved to disk.)
	private ConcurrentHashMap<String,DataOutputStream> clients = 
	    new ConcurrentHashMap<String,DataOutputStream>();

	// authorizedClients key is userid, object is chat name.
	// This collection is always initialized from a prepared
	// .ser file, and is NOT updated by the server. 
	// (So this collection need not be saved to disk.)
	private TreeMap<String,String> authorizedClients;

	// passwords key is userid, associated object is password.
	// This collection is always restored from disk.
	// This collection is updated by 1st-time joiners,
	// and it must be saved to disk whenever it is changed.
	private ConcurrentHashMap<String,String> passwords;
	
	private int portNumber = 1234;
	private ServerSocket ss;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PrivateChatServer() throws Exception {
		// Constructor
		ss = new ServerSocket(portNumber);
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream("ChatRoomAuthorizedClients.ser"));
		authorizedClients = (TreeMap) ois.readObject();
		ois.close();
		System.out.println(authorizedClients.size()
				   + " students are authorized to join the chat room:");
				System.out.println(authorizedClients);
		try {
			ois = new ObjectInputStream(new FileInputStream("ChatRoomPasswords.ser"));
	        passwords = (ConcurrentHashMap) ois.readObject();
	        ois.close();
        }
	    catch(FileNotFoundException fnfe) {
	    	System.out.println("Input file ChatRoomPasswords is not found, so an empty passwords collection is being created.");
			passwords = new ConcurrentHashMap();
		}		
				
		System.out.println("ChatRoomServer is up at " + InetAddress.getLocalHost().getHostAddress()
								+ " on port " +  ss.getLocalPort());
		new Thread(this).start(); // create a thread and start running
	}

	@Override
	public void run() {
		// Each client goes here!
		Socket s = null;
		DataOutputStream dos = null;
		DataInputStream dis = null;
		String clientName = "";
		String userid = null;
		String password = null;
		
		try {
			s = ss.accept(); // wait for a client
			new Thread(this).start(); // make a thread to connect the NEXT client - somewhat recursive
			dis = new DataInputStream(s.getInputStream());
			String firstMsg = dis.readUTF();
			dos = new DataOutputStream(s.getOutputStream());
			
			// validate connection 
			if (firstMsg.charAt(0) == '\u0274') { // char startsWith()
				firstMsg = firstMsg.substring(1); // drop unicode char
				int slashOffset = firstMsg.indexOf("/");
				userid = firstMsg.substring(0, slashOffset).toLowerCase();
				password = firstMsg.substring(slashOffset + 1);
				System.out.println(userid + " is attempting to join with password " + password);
			}
			else {
				dos.writeUTF("Invalid protocol. " + "Are you calling the right address and port?");
				dos.close(); // close connection
				System.out.println("Invalid 1st msg received: " + firstMsg);
				return; // kill this client thread
			}
			
			// validate username then get chatName from this collection: 
			if (authorizedClients.containsKey(userid)) clientName = authorizedClients.get(userid);
			  else { //The submitted userid is not in the collection...
				  dos.writeUTF("user id " + userid + " is not authorized to join the PrivateChatRoom.");
				  System.out.println(userid + " is not authorized to join.");
				  dos.close(); // hang up.
				  return;      // and kill this client thread
			   }
			if (clients.containsKey(clientName)) {
				   dos.writeUTF(clientName + " is already in the chat room. Cannot be joined from two locations concurrently.");
				   System.out.println("Received join request for a userid (" + userid + "=" + clientName + " that is already in the chat room." );
				   dos.close(); // hang up.
				   return;      // and kill this client thread
			}
			else { // process this request
				System.out.println(clientName + " is joining."); // trace
				sendToAllClients("Welcome to " + clientName + " who has just joined the chat room!"); // let everyone know you're flossing
				clients.put(clientName, dos); // add to collection
			}
			ObjectOutputStream oos = null;
			if (!passwords.containsKey(userid)) // stored password not found
					   { // That's OK! This is just a first-time join!
					   passwords.put(userid, password);//add to collection
					   //And save the updated passwords collection to disk:
					   oos= new ObjectOutputStream(new FileOutputStream("ChatRoomPasswords.ser"));
					   oos.writeObject(passwords);
					   oos.close();
					   }
					 else // a stored password for this userid is found
					   {
					   String storedPassword = passwords.get(userid);
					   if(!storedPassword.equals(password))
					     {
					     dos.writeUTF("Join rejected. The submitted password does not match the password stored for this user id.");
					     return;      // and kill this client's thread.
					     }
					   }
					dos.writeUTF("Welcome to the chat room " + clientName + "!");	
		} 
		catch (Exception e) {
			System.out.println("Client connection failure: " + e);
			if (s.isConnected()) {
				try {
					s.close(); // hang up
				}
				catch (IOException ioe){
					// Already hung up
				}
			} // end if
		} // end catch
		
		// Send Receive loop
		try {
			while (true) {
				String inChat = dis.readUTF();
				String outChat = clientName + " says: " + inChat;
				System.out.println(outChat); // trace
				sendToAllClients(outChat); // send it to everyone
			}
		}
		catch (IOException ioe){ // leave processing
			System.out.println(clientName + " is leaving."); // trace
			sendToAllClients("Goodbye to " + clientName + " who has just left the chat room!"); // let everyone know you're leaving
			clients.remove(clientName); // remove from collection
			return; // kill thread since we're done now for w/e reason
		}
	} 
		
	private synchronized void sendToAllClients(String message) {
		Collection<DataOutputStream> dosList = clients.values();
		
		// time to send everyone a message
		for (DataOutputStream clientDOS : dosList) {
			try {
				clientDOS.writeUTF(message);
			} 
			catch (IOException e) {
				// don't do anything
			}
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Will Flores waflores\r\n"); // My name
		
		if(args.length != 0) {
			System.out.println("Usage: java ChatRoomServer");
			System.out.println("Don't invoke ChatRoomServer with cmdline parameters.");
		}
		
		// run the chatroom server
		try {
			new PrivateChatServer();
		}
		catch (Exception e){
			System.out.println(e.toString()); // server app died
		}
	}

}
