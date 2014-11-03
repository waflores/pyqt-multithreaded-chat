/*	Will Flores waflores@ncsu.edu
 * Lab 4 
 * 	ChatRoomClient.java: This program will implement a chat room client which takes a 
 * 		username and server address as inputs from the cmdline
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;


@SuppressWarnings("serial")
public class WebChatApp extends JApplet implements ActionListener, Runnable, WindowListener {
	// Main PrivateChatClient GUI Components
	private JFrame chatWindow = new JFrame("PrivateChatClient");
	private JTextArea inTextArea = new JTextArea();
	private JTextArea outTextArea = new JTextArea();
	private JButton sendButton = new JButton("Send");
	private JScrollPane inScrollPane = new JScrollPane(inTextArea);
	private JScrollPane outScrollPane = new JScrollPane(outTextArea);
	private String newLine = System.getProperty("line.separator");
	private JMenuItem inChatBiggerFont   = new JMenuItem("INPUT  bigger");
	private JMenuItem inChatSmallerFont  = new JMenuItem("INPUT  smaller");
	private JMenuItem outChatBiggerFont  = new JMenuItem("OUTPUT bigger");
	private JMenuItem outChatSmallerFont = new JMenuItem("OUTPUT smaller");
	private int inFontSize = 20;
	private int outFontSize = 20;
	 
	JScrollBar vsb = outScrollPane.getVerticalScrollBar();
	private DataOutputStream dos;
	private DataInputStream dis;
	
	// Join GUI Elements
	private JPanel joinPanel          = new JPanel();
	private JTextField  useridTextField     = new JTextField(16);
	private JTextField  serverTextField     = new JTextField(16);
	private JPasswordField passwordTextField= new JPasswordField(16);
	private JLabel useridLabel       = new JLabel("user id",SwingConstants.CENTER);
	private JLabel passwordLabel     = new JLabel("password",SwingConstants.CENTER);
	private JLabel serverAddressLabel= new JLabel("server address",SwingConstants.CENTER);
	private JLabel      messageTextField    = new JLabel("messages:");
	private String serverAddress; // String instance variable
	private int portNumber = 1234; // default port
	private Socket socket = null;
	/**
	 * @param args
	 */

	private void receive() {
		// receive method
		//System.out.println("receive() method entered");
		try {
			while(true) { // "capture" main thread
				String msg = dis.readUTF();
				outTextArea.append(newLine + msg);
				vsb.setValue(vsb.getMaximum());
				outTextArea.setCaretPosition(outTextArea.getDocument().getLength());
				inTextArea.requestFocus();
			}
		}
		catch(IOException ioe) {
			outTextArea.append(newLine + "Connection lost to the server.");
			outTextArea.append(newLine + "Must restart client to reconnect.");
		}
	}

	public void init() {
		// main calls us here, throw exceptions from this
		// method; we can catch exceptions but only to
		// show the user what happened, this thread runs
		// on borrowed time
		
		/*System.out.println("In the constructor "
				+ "connecting to the chat server at "
				+ serverAddress + " to join " + chatName
				+ " to the chat room.");*/
		// Start building GUI
		// Build the join GUI
		
		// Get Server address
		  serverAddress = getCodeBase().getHost();
		  if ((serverAddress == null) || (serverAddress.length() == 0)) serverAddress = "localhost"; // to run with appletviewer
		  
		  String serverPortNumber = getParameter("PortNumber");
		  if (serverPortNumber != null)//watch for no-find PARAM in HTML
		     {
		     try { 
		         portNumber = Integer.parseInt(serverPortNumber);
		         }
		     catch(NumberFormatException nfe)
		         {
		         messageTextField.setText("Invalid port # specified in HTML. (trying 1234)"); 
		         messageTextField.setBackground(Color.pink);
		         }
		     }
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1,3));
		topPanel.add(serverAddressLabel);
		topPanel.add(useridLabel);
		topPanel.add(passwordLabel);
		
		JPanel middlePanel = new JPanel();
		middlePanel.setLayout(new GridLayout(1,3));
		middlePanel.add(serverTextField);
		middlePanel.add(useridTextField);
		middlePanel.add(passwordTextField);
		 
		serverTextField.setText(serverAddress+","+serverPortNumber);
		passwordTextField.setEnabled(false);
		messageTextField.setEnabled(false);

		serverTextField.addActionListener(this);
		useridTextField.addActionListener(this);
		passwordTextField.addActionListener(this);

		socket = null; // New Socket for server connection
		try {
			socket = new Socket(serverAddress, portNumber); // connect to l'server
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
		}
		catch(Exception e){
			messageTextField.setText("Invalid network address.");
			messageTextField.setBackground(Color.pink);
			return;
		}

	    joinPanel.setLayout(new GridLayout(3,1));//3 rows,1 col
	    joinPanel.add(topPanel);
	    joinPanel.add(middlePanel);
	    joinPanel.add(messageTextField);
	    add(joinPanel); // add joinPanel to browser window!
	}
	public void start() {
		// We have lift off... now to join the party
//		try {
//		dos = new DataOutputStream(socket.getOutputStream());
//		dis = new DataInputStream(socket.getInputStream());
//		}
//		catch(IOException ioe){
//			System.out.println("dos or dis failed, ioe thrown.");
//			return;
//		}
		serverTextField.setEnabled(false);
		useridTextField.setEnabled(true);
		passwordTextField.setEnabled(true);
		useridTextField.requestFocus(); // set cursor in.
		
		// Chat window GUI
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inScrollPane, outScrollPane);
		chatWindow.getContentPane().add(sendButton, "Center");
		chatWindow.getContentPane().add(splitPane, "Center");
		chatWindow.addWindowListener(this);
		sendButton.addActionListener(this); // to button - call if pushed
		sendButton.setMnemonic(KeyEvent.VK_ENTER); // alt enter 
		outTextArea.setEditable(false); // don't edit this
		inTextArea.setEditable(true); // don't edit this... just yet.
		splitPane.setDividerLocation(200); // 200 pixels from left/ top
		chatWindow.setSize(800, 500);
		chatWindow.setVisible(false);
		JMenuBar menuBar = new JMenuBar();
		chatWindow.setJMenuBar(menuBar);
		JMenu fontMenu = new JMenu("Font");
		menuBar.add(fontMenu);
		fontMenu.add(inChatBiggerFont);
		fontMenu.add(inChatSmallerFont);
		fontMenu.add(outChatBiggerFont);
		fontMenu.add(outChatSmallerFont);
		inChatBiggerFont.addActionListener(this);//activate menu item
		inChatSmallerFont.addActionListener(this);//activate menu item
		outChatBiggerFont.addActionListener(this);//activate menu item
		outChatSmallerFont.addActionListener(this);//activate menu item
	}

	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent ae) {
		// GUI objs call here by JVM's event handler thread
		if ((ae.getSource() == useridTextField) || (ae.getSource() == passwordTextField)) {
			String userid   = useridTextField.getText().trim();
			String password = passwordTextField.getText().trim();
			
			if ((userid.length()   == 0) || (password.length() == 0)) {
			   messageTextField.setText("Both user id and password must be provided.");
			   messageTextField.setBackground(Color.pink);
			   return;
		   }
			else if (userid.contains("/") || password.contains("/")) {
				   messageTextField.setText("Don't use a literal slash fields.");
				   messageTextField.setBackground(Color.pink);
				   return;
		   }
			else {
				if (socket.isClosed())// user is rejoining
				   {             // and we must reconnect...
				   try {
					socket   = new Socket(serverAddress, portNumber);
					   dos = new DataOutputStream(socket.getOutputStream());
					   dis = new DataInputStream (socket.getInputStream());
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				   }
				try {
					dos.writeUTF('\u0274' + userid + "/" + password);
					String joinReply = dis.readUTF();
					System.out.println("Join reply from server is: " + joinReply);
					if (joinReply.startsWith("Welcome to the chat room"))
					   {
					   String chatName = joinReply.substring(25);//save name from server
					   chatWindow.setVisible(true);
					   chatWindow.setTitle(chatName + "'s ChatRoom! Use alt-ENTER to send.  Close window to leave chat room.");
					   outTextArea.setText(joinReply);
					   inTextArea.requestFocus();
					   // Start a new thread :)
					   new Thread(this).start();
					   }
					 else // server has rejected the join and closed the connection.
					   {
					   messageTextField.setText(joinReply);	
					   messageTextField.setBackground(Color.pink);
					   Socket socket   = new Socket(serverAddress,1234); // reconnect
					   dos = new DataOutputStream(socket.getOutputStream());
					   dis = new DataInputStream (socket.getInputStream());
					   }
				}
				catch (Exception e) {
					
				}
			}
		}
		
		if (ae.getSource() == sendButton) {
			// handle the sendButton push
			String chat = inTextArea.getText().trim();
			if (chat.length() == 0) return;
			//System.out.println("Sending" + chat); // debug
			inTextArea.setText(" "); // write blank to clear
			inTextArea.requestFocus();
			try {
				dos.writeUTF(chat); // send chat to server
			} catch (IOException ioe) {}
		}
		if (ae.getSource() == inChatBiggerFont) {
			//System.out.println("Make font bigger in inTxtArea");
			inFontSize += 5; // bump
			//System.out.println("New font Size is " + inFontSize);
			inTextArea.setFont(new Font("Default", Font.BOLD, inFontSize));
		}
	
		if (ae.getSource() == inChatSmallerFont) {
			//System.out.println("Make font smaller in inTxtArea");
			if (inFontSize > 5) inFontSize -= 5; // bump down
			else return;
			inTextArea.setFont(new Font("Default", Font.BOLD, inFontSize));
		}
		if (ae.getSource() == outChatBiggerFont) {
			//System.out.println("Make font bigger in outTxtArea");
			outFontSize += 5; // bump up
			outTextArea.setFont(new Font("Default", Font.BOLD, outFontSize));
		}
		if(ae.getSource() == outChatSmallerFont) {
			//System.out.println("Make font smaller in outTxtArea");
			if (outFontSize > 5) outFontSize -= 5; // bump down
			else return;
			outTextArea.setFont(new Font("Default", Font.BOLD, outFontSize));
		}
	}
	// End all methods	

	@Override
	public void run() {
		receive();
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		 try {socket.close();}      // break connection to server
		  catch(Exception w) {} //(also kills receive thread!)
		  //whoWindow.setVisible(false);    // EC lab only
		 // whoNOTWindow.setVisible(false); // EC lab only
		  messageTextField.setText("Hit ENTER in user id or password field to rejoin the chat room.");
		  outTextArea.setText("");// clear old chat for rejoin
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}