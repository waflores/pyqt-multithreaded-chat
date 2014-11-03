/*******************************************************************************
 * File: TellerClient.java
 * Author: Will Flores waflores@ncsu.edu
 * Usage: Implements Lab 9
 * Description: This file contains...
 * Environment: Windows 7, x64 build
 * Notes:
 * Revisions: 0.0, Initial Release
 * 
 * Created on March 13, 2012
 *******************************************************************************/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.io.*;
import java.math.RoundingMode;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.text.DecimalFormat;

import javax.swing.JFrame;

@SuppressWarnings("unused")
public class TellerClient implements ActionListener {
	/* Main Teller Window */
	private JFrame tellerWindow = new JFrame("TellerClient");
	private JPanel topPanel = new JPanel();
	private JPanel midPanel = new JPanel();
	private JPanel botPanel = new JPanel();
	
	private JTextArea displayTextArea = new JTextArea(30, 50); // Server Output in Teller Window
	private JScrollPane outScrollPane = new JScrollPane(displayTextArea);
	
	private JButton showByNumberButton = new JButton("Show By Number");
	private JButton showByNameButton = new JButton("Show By Name");
	private JButton depositButton = new JButton("Deposit");
	private JButton withdrawButton = new JButton("Withdraw");
	private JButton closeButton = new JButton("Close");
	private JButton clearButton = new JButton("Clear");
	private JButton openCheckingButton = new JButton("Open New Checking");
	private JButton openSavingsButton = new JButton("Open New Savings");
	
	private JTextField accountTextField = new JTextField(16);
	private JTextField amountTextField = new JTextField(16);
	private JTextField nameTextField = new JTextField(16);
	private JLabel accountTextLabel = new JLabel("Account: ");
	private JLabel amountTextLabel = new JLabel("Amount: ");
	private JLabel nameTextLabel = new JLabel("Customer Name: ");
	private BankTellerServer server = null;
	
	// close account windows
	private JDialog closeAccountWindow = new JDialog(tellerWindow, true);
	private JLabel closeLabel = new JLabel();
	private JButton confirmClose = new JButton("Confirm Close");
	private JButton cancelClose = new JButton("Cancel Close");
	
	public  TellerClient(String serverAddress) throws Exception {
		topPanel.add(accountTextLabel);
		topPanel.add(accountTextField);
		topPanel.add(showByNumberButton);
		topPanel.add(amountTextLabel);
		topPanel.add(amountTextField);
		topPanel.add(depositButton);
		topPanel.add(withdrawButton);
		topPanel.add(closeButton);
		displayTextArea.setEditable(true);
		midPanel.add(displayTextArea);
		botPanel.add(clearButton);
		botPanel.add(nameTextLabel);
		botPanel.add(nameTextField);
		botPanel.add(showByNameButton);
		botPanel.add(openCheckingButton);
		botPanel.add(openSavingsButton);
		
		showByNumberButton.addActionListener(this);
		showByNameButton.addActionListener(this);
		depositButton.addActionListener(this);
		withdrawButton.addActionListener(this);
		closeButton.addActionListener(this);
		openSavingsButton.addActionListener(this);
		openCheckingButton.addActionListener(this);
		clearButton.addActionListener(this);
		
		tellerWindow.getContentPane().add(topPanel, "North");
		tellerWindow.getContentPane().add(midPanel, "Center");
		tellerWindow.getContentPane().add(botPanel, "South");
		
		tellerWindow.setSize(800, 500);
		tellerWindow.setVisible(true);

		// Close the account dialog
		closeAccountWindow.setTitle("Account Close Request");
		closeAccountWindow.add(closeLabel, "Center");
		closeAccountWindow.add(confirmClose, "North");
		closeAccountWindow.add(cancelClose, "South");
		closeAccountWindow.setSize(30, 50);
		closeAccountWindow.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		tellerWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		server = (BankTellerServer) Naming.lookup("TellerServices");
		displayTextArea.setText("Connected to the TellerServer!");
	}
	
	/*******************************************************************************
	 * Purpose: This method bootstraps TellerClient with the correct server address 
	 * 			for communication with TellerServer.
	 * Passed: No arguments passed.
	 * Locals: No local variables used.
	 * Returned: No values returned.
	 * Author: Will Flores waflores@ncsu.edu
	 *******************************************************************************/
	public static void main(String[] args) throws Exception {	
		String serverAddress = "localhost"; // Default server address
		String connectionAddress = null;
		System.out.println("Will Flores waflores\r\n"); // My name
		
		/* Print the command line arguments for debug trace in Eclipse */
		System.out.print("java TellerClient ");
		if (args.length > 0) {
			for (String cmdLineArg : args) {
				System.out.print(cmdLineArg + " ");
			}
			System.out.println();
		}
		
		/* Command Line Parameter Check */
		if (args.length != 1) {
			/* We didn't have correct amount of parameters on the command line - use localhost */
			System.out.println("Usage: java TellerClient [serverAddress]");
			System.out.println("[serverAddress] = dotted-numeric server ip-address.");
			System.out.println("Ommiting the serverAddress will connect you to localhost.");
		}
		else serverAddress = args[0]; // Use provided server Address
		connectionAddress = "rmi://" + serverAddress + "/TellerServices";
		
		try {
			new TellerClient(connectionAddress);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public void actionPerformed(ActionEvent ae) {
		try {
			  if (ae.getSource() == showByNumberButton) showAccount   (Bank.BY_NUMBER);
			  if (ae.getSource() == showByNameButton)   showAccount   (Bank.BY_NAME);
			  if (ae.getSource() == depositButton)      processAccount(Bank.DEPOSIT);
			  if (ae.getSource() == withdrawButton)     processAccount(Bank.WITHDRAW);
			  if (ae.getSource() == closeButton)        processAccount(Bank.CLOSE);
			  if (ae.getSource() == openSavingsButton)  openNewAccount(Bank.SAVINGS);
			  if (ae.getSource() == openCheckingButton) openNewAccount(Bank.CHECKING);
		}
		catch (RemoteException re) {
			displayTextArea.setText(re.toString());
		}
		catch (IllegalArgumentException iae) {
			displayTextArea.setText(iae.getMessage());
		}
		
		if (ae.getSource() == clearButton) {
			displayTextArea.setText("");
			accountTextField.setText("");
			amountTextField.setText("");
			nameTextField.setText("");
		}
	}

	private void openNewAccount(String accountType) throws RemoteException, IllegalArgumentException {
		String customerName  = getCustomerName();
		String accountNumber = getAccount();
		
		System.out.println("Opening a new " + accountType + " account for "  + customerName);
		displayTextArea.setText(server.openNewAccount(accountType, customerName));
	}

	private void showAccount(String showType) throws RemoteException, IllegalArgumentException {
		 String ActNumCheck = getAccount();
		 int accountNumber = -1;
		 
		 // Account number parsing
		 if (ActNumCheck.length() != 0){
			 try {
				 accountNumber  = Integer.parseInt(ActNumCheck);
			 }
			 catch(NumberFormatException nfe) {
				 throw new IllegalArgumentException("Account number must be numeric.");
			 }
		 }
		 else throw new IllegalArgumentException("Account number must be provided.");
		 
		 // Account number checking
		 if (accountNumber < 0) throw new IllegalArgumentException("Account number must be positive.");
		 if (accountNumber > Integer.MAX_VALUE) {
			 throw new IllegalArgumentException("Account number must not exceed maximum value.");
		 }
		 
		 String customerName = nameTextField.getText().trim();
		 
		 if (showType.equals(Bank.BY_NAME)) {
			 System.out.println("Showing all accounts for " + customerName);
			 displayTextArea.setText(server.showAccount(showType, accountNumber, customerName));
		 }
		 
		 if (showType.equals(Bank.BY_NUMBER)) {
			 System.out.println("Showing account " + accountNumber);
			 displayTextArea.setText(server.showAccount(showType, accountNumber, customerName));
		 }
	 }


	private void processAccount(String processingType) throws RemoteException, IllegalArgumentException {
		 String ActNumCheck = getAccount();
		 int accountNumber = -1;
		 
		 // Account number parsing
		 if (ActNumCheck.length() != 0){
			 try {
				 accountNumber  = Integer.parseInt(ActNumCheck);
			 }
			 catch(NumberFormatException nfe) {
				 throw new IllegalArgumentException("Account number must be numeric.");
			 }
		 }
		 else throw new IllegalArgumentException("Account number must be provided.");
		 
		 // Account number checking
		 if (accountNumber < 0) throw new IllegalArgumentException("Account number must be positive.");
		 if (accountNumber > Integer.MAX_VALUE) {
			 throw new IllegalArgumentException("Account number must not exceed maximum value.");
		 }
		 
		 String AmtNumCheck = getAmount();
		 double amount = -1;
		 // Amount parsing
		 if (AmtNumCheck.length() != 0){
			 try{
				 amount = Double.parseDouble(AmtNumCheck);
			 }
			 catch (NumberFormatException nfe) {
				 throw new IllegalArgumentException("Amount is not numeric.");
			 }
		 }
		 else throw new IllegalArgumentException("Amount must be provided.");
		 // amount checking 
		 if (amount < 0 ) throw new IllegalArgumentException("Amount must be positive.");
		 
		 
		 if (processingType.equals(Bank.CLOSE)) {
			 System.out.println("Closing account " + accountNumber);
			 displayTextArea.setText(server.processAccount(processingType, accountNumber, amount));
		 }
		  else {
			  System.out.println("Doing a "     + processingType
                      + " of "         + amount
                      + " on account " + accountNumber);
			  displayTextArea.setText(server.processAccount(processingType, accountNumber, amount));
		  }
     }
	
	// getter functions
	private String getAccount() throws IllegalArgumentException {
		return accountTextField.getText().trim();
	}
	private String getAmount() throws IllegalArgumentException {
		return amountTextField.getText().trim();
	}
	private String getCustomerName() throws IllegalArgumentException {
		String nameStr = nameTextField.getText().trim();
		if (nameStr.charAt(0) == ',')throw new IllegalArgumentException("Last name must be included.");
		if (nameStr.endsWith(",")) throw new IllegalArgumentException("First name must be included.");
		
		return null;
	}
} /* End of TellerClient class definition */
