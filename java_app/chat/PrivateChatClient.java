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
import java.io.*;
import java.net.Socket;
import javax.swing.*;


public class PrivateChatClient implements ActionListener, Runnable {
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
	private JFrame      joinWindow          = new JFrame("Enter user id and password to join the chat room.");
	private JTextField  useridTextField     = new JTextField(16);
	private JTextField  serverTextField     = new JTextField(16);
	private JPasswordField passwordTextField= new JPasswordField(16);
	private JLabel useridLabel       = new JLabel("user id",SwingConstants.CENTER);
	private JLabel passwordLabel     = new JLabel("password",SwingConstants.CENTER);
	private JLabel serverAddressLabel= new JLabel("server address",SwingConstants.CENTER);
	private JLabel      messageTextField    = new JLabel("messages:");
	private String serverAddress; // String instance variable
	
	/**
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		// cmd-line loader calls here, from JVM - thread can be
		// held onto for the long haul
		
		// Format keyboard input 
		InputStreamReader inputStr = new InputStreamReader(System.in);
		BufferedReader buf = new BufferedReader(inputStr);
		
		String serverAddress = "localhost";
		System.out.println("Will Flores waflores\r\n"); // My name
		
		if(args.length == 0) {
			System.out.println("Usage: java PrivateChatClient [serverAddress]");
			System.out.println("[serverAddress] = dotted-numeric server ip-address.");
			System.out.println("Ommiting the serverAddress will connect you to localhost");
		}
		else serverAddress = args[0];
		// test connection to server
//		try {
//			Socket socket = new Socket(serverAddress, 1234);
//			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
//			dos.writeUTF("test");
//			DataInputStream dis = new DataInputStream(socket.getInputStream());
//			String testResponse = dis.readUTF();
//		}
//		catch (IOException ioe ){
//			System.out.println("Attempt to connect to \"" + serverAddress + "\" has failed.");
//			System.out.println("This network address may be incorrect for the server,");
//			System.out.println("or the server may not be up. The specific failure is:");
//			System.out.println(ioe.toString());
//			return;
//		}
		
		try {
			new PrivateChatClient(serverAddress);
			//System.out.println("Back to main, crc is up and receive() is gonna get called.");
			//crc.receive(); 
			// branch the main thread to receive() method
		}
		catch (Exception e){
			System.out.println(e.toString());
		}
		
		//System.out.println("Exiting main...");
	}

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

	public PrivateChatClient(String serverAddress) throws Exception {
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
		this.serverAddress = serverAddress; // save parm

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1,3));
		topPanel.add(serverAddressLabel);
		topPanel.add(useridLabel);
		topPanel.add(passwordLabel);
		joinWindow.getContentPane().add(topPanel,"North");
		
		JPanel middlePanel = new JPanel();
		middlePanel.setLayout(new GridLayout(1,3));
		middlePanel.add(serverTextField);
		middlePanel.add(useridTextField);
		middlePanel.add(passwordTextField);
		joinWindow.getContentPane().add(middlePanel,"Center");
		joinWindow.getContentPane().add(messageTextField,"South");
		  
		serverTextField.setText(serverAddress);
		passwordTextField.setEnabled(false);
		messageTextField.setEnabled(false);

		serverTextField.addActionListener(this);
		useridTextField.addActionListener(this);
		passwordTextField.addActionListener(this);

		joinWindow.setSize(600,100);
		joinWindow.setLocation(300,300);
		joinWindow.setVisible(true);
		joinWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Socket socket = null; // New Socket for server connection
		try {
			socket = new Socket(serverAddress, 1234); // connect to l'server
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
		}
		catch(Exception e){
			messageTextField.setText("Invalid network address.");
			messageTextField.setBackground(Color.pink);
			throw e;
		}
		
		// We have lift off... now to join the party
		dos = new DataOutputStream(socket.getOutputStream());
		dis = new DataInputStream(socket.getInputStream());
		serverTextField.setEnabled(false);
		useridTextField.setEnabled(true);
		passwordTextField.setEnabled(true);
		useridTextField.requestFocus(); // set cursor in.
		
		// Chat window GUI
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inScrollPane, outScrollPane);
		chatWindow.getContentPane().add(sendButton, "Center");
		chatWindow.getContentPane().add(splitPane, "Center");
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
		chatWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit prog when exit GUI
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
				try {
					dos.writeUTF('\u0274' + userid + "/" + password);
					String joinReply = dis.readUTF();
					System.out.println("Join reply from server is: " + joinReply);
					if (joinReply.startsWith("Welcome to the chat room"))
					   {
					   String chatName = joinReply.substring(25);//save name from server
					   joinWindow.dispose();
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
}