/** 3-7-12 for Lab 10 RMI Client-Server 
 *  This version has the GUI (now with edits) and calls
 *  the server which now uses the Bank Java Beans.
 */

import java.awt.event.*;
import java.awt.*;
import java.rmi.*;

import javax.swing.*;

import java.io.*;
import java.net.*;


public class TellerClientDB implements ActionListener
{
public static void main(String[] args)
 {
 try {
     if (args.length == 0)
        {
    	System.out.println("Server address may be provided as a command line parameter."); 
	    new TellerClientDB("localhost");
        }
     if (args.length == 1)
		 new TellerClientDB(args[0]);
     if (args.length > 1)
        {
 	    System.out.println("Only server address may be provided as a single command line parameter."); 
	    return;
        }
     }
 catch(Exception e)
     {
     System.out.println(e);
	 }
 }


//OBJECT STUFF --------------------------------------------------------	
private JFrame     tellerWindow          = new JFrame("Teller Station");	
private JButton    depositButton         = new JButton("Deposit");
private JButton    withdrawButton        = new JButton("Withdraw");
private JButton    closeButton           = new JButton("Close");
private JButton    clearButton           = new JButton("Clear");
private JButton    showByNumberButton    = new JButton("ShowByNumber");
private JButton    showByNameButton      = new JButton("ShowByName");
private JButton    openNewSavingsButton  = new JButton("OpenNewSavings");
private JButton    openNewCheckingButton = new JButton("OpenNewChecking");
private JTextField accountTextField      = new JTextField(16);
private JTextField amountTextField       = new JTextField(16);
private JTextField customerNameTextField = new JTextField(16);
private JLabel     accountLabel          = new JLabel("Account # =>",SwingConstants.RIGHT); 
private JLabel     amountLabel           = new JLabel("Amount =>",SwingConstants.RIGHT); 
private JLabel     customerNameLabel     = new JLabel("CustomerName Last,First =>",SwingConstants.RIGHT); 
private JTextArea  displayTextArea       = new JTextArea(10,60); 
private JScrollPane displayScrollPane    = new JScrollPane(displayTextArea);
private JPanel     topPanel              = new JPanel();
private JPanel     bottomPanel           = new JPanel();

private JDialog    closeAccountWindow    = new JDialog(tellerWindow,true);
private JLabel     closeLabel            = new JLabel();
private JButton    confirmCloseButton    = new JButton("Confirm Close");
private JButton    cancelCloseButton     = new JButton("Cancel Close");

private BankTellerServer server;

//=========================================================
public TellerClientDB(String serverAddress) throws Exception // CONSTRUCTOR
    {
	tellerWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // Build GUI (layout)
	topPanel.setLayout(new GridLayout(1,8));
	topPanel.add(accountLabel);
	topPanel.add(accountTextField);
	topPanel.add(showByNumberButton);
	topPanel.add(amountLabel);
	topPanel.add(amountTextField);
	topPanel.add(depositButton);
	topPanel.add(withdrawButton);
	topPanel.add(closeButton);
	tellerWindow.getContentPane().add(topPanel,BorderLayout.NORTH);
	
	tellerWindow.getContentPane().add(displayScrollPane,BorderLayout.CENTER);
	
	bottomPanel.setLayout(new GridLayout(1,6));
	bottomPanel.add(clearButton);
	bottomPanel.add(customerNameLabel);
	bottomPanel.add(customerNameTextField);
	bottomPanel.add(showByNameButton);
	bottomPanel.add(openNewSavingsButton);
	bottomPanel.add(openNewCheckingButton);
	tellerWindow.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	
	// set GUI attributes
	displayTextArea.setEditable(false); // keep cursor out!
	displayTextArea.setFont(new Font("default", Font.BOLD, 20));
	accountTextField.setFont(new Font("default", Font.BOLD, 20));
	amountTextField.setFont(new Font("default", Font.BOLD, 20));
	customerNameTextField.setFont(new Font("default", Font.BOLD, 20));

    showByNumberButton.setBackground(Color.cyan);
	showByNameButton.setBackground(Color.cyan);
	clearButton.setBackground(Color.cyan);
	closeButton.setBackground(Color.red);
	openNewSavingsButton.setBackground(Color.green);
	openNewCheckingButton.setBackground(Color.green);
	depositButton.setBackground(Color.yellow);
	withdrawButton.setBackground(Color.yellow);
	
	// Request event notification (CALL ME!)
	showByNumberButton.addActionListener(this);
	showByNameButton.addActionListener(this);
	depositButton.addActionListener(this);
	withdrawButton.addActionListener(this);
	openNewCheckingButton.addActionListener(this);
	openNewSavingsButton.addActionListener(this);
	clearButton.addActionListener(this);
	closeButton.addActionListener(this);
	
	// show window
	tellerWindow.setSize(1000,500); // across, down
	tellerWindow.setVisible(true);
	
	// pop-up
    confirmCloseButton.setBackground(Color.green);
    cancelCloseButton.setBackground(Color.red);
	cancelCloseButton.addActionListener(this);
	confirmCloseButton.addActionListener(this);
	closeLabel.setFont(new Font("default", Font.BOLD, 20));
	closeAccountWindow.setTitle("Account Close Request");
	closeAccountWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	closeAccountWindow.getContentPane().add(confirmCloseButton,"North");
	closeAccountWindow.getContentPane().add(closeLabel,"Center");
	closeAccountWindow.getContentPane().add(cancelCloseButton,"South");
    closeAccountWindow.setSize(200,150);
    closeAccountWindow.setLocation(500,300);
	
	// Connect to RMI TellerServer
	server = (BankTellerServer) Naming.lookup(
		     "rmi://"+serverAddress+"/TellerServices");
	displayTextArea.setText("Connected to TellerServices");
    }

 
//========================================================
public void actionPerformed(ActionEvent ae)
 {
 try {	
     if (ae.getSource() == showByNameButton)     showAccount   (Bank.BY_NAME);
     if (ae.getSource() == showByNumberButton)   showAccount   (Bank.BY_NUMBER);
     if (ae.getSource() == depositButton)        processAccount(Bank.DEPOSIT);
     if (ae.getSource() == withdrawButton)       processAccount(Bank.WITHDRAW);
     if (ae.getSource() == closeButton)          processAccount(Bank.CLOSE);
     if (ae.getSource() == cancelCloseButton)    processAccount(Bank.CANCEL);
     if (ae.getSource() == confirmCloseButton)   processAccount(Bank.CONFIRM);
     if (ae.getSource() == openNewSavingsButton) openNewAccount(Bank.SAVINGS);
     if (ae.getSource() == openNewCheckingButton)openNewAccount(Bank.CHECKING);
     }
 catch(RemoteException re)
     {
	 displayTextArea.setText(re.toString());
     }
 catch(IllegalArgumentException iae)
     {
     displayTextArea.setText(iae.getMessage());
     }
 if (ae.getSource() == clearButton)
    {
	accountTextField.setText("");
	amountTextField.setText("");
	customerNameTextField.setText("");
	displayTextArea.setText("");
    }
 }


//========================================================
private void openNewAccount(String accountType) throws RemoteException
 {
 String customerName = getCustomerName();
 System.out.println("Opening a new " + accountType
		          + " account for "  + customerName);
 displayTextArea.setText(server.openNewAccount(accountType, customerName));
 }


//======================================================
private void processAccount(String processType) throws NumberFormatException, RemoteException
 {
 String accountNumber = getAccountNumber();
 if (processType.equals(Bank.CLOSE))
    {
	String customerName = customerNameTextField.getText().trim();
	if (customerName.length() != 0)
		throw new IllegalArgumentException("Customer name should not be provided for account close.");
	String amount = amountTextField.getText().trim();
	if (amount.length() != 0)
		throw new IllegalArgumentException("Amount should not be provided for account close.");
	System.out.println("Closing account " + accountNumber);
    String returnedCustomerName = server.processAccount(
    		                      processType,
    		                      Integer.parseInt(accountNumber),
    		                      null); // amount is N/A 
    if (returnedCustomerName.startsWith("ERROR:"))
       {
       displayTextArea.setText(returnedCustomerName); // really is an err msg
       return;
       }
    displayTextArea.setText("");//erase any previous err msg
    closeLabel.setText(returnedCustomerName);
    closeAccountWindow.setVisible(true);//show pop-up
    }
 else if (processType.equals(Bank.CANCEL))
    {
	System.out.println("Canceling Close Account.");
	closeLabel.setText("");
	closeAccountWindow.setVisible(false);
    }
 else if (processType.equals(Bank.CONFIRM))
    {
	String account = accountTextField.getText().trim();
	System.out.println("Confirming Close of account " + account);
	closeLabel.setText("");
	closeAccountWindow.setVisible(false);
    displayTextArea.setText(server.processAccount(
    		                processType,
    		                Integer.parseInt(account),
    	                 	null)); // amount is N/A 
    }
 else // DEPOSIT or WITHDRAW
    { 
	String amount = getAmount();
    System.out.println("Doing a "     + processType
	                 + " of "         + amount
		             + " on account " + accountNumber);
    displayTextArea.setText(server.processAccount(processType,
                                                  Integer.parseInt(accountNumber),
                                                  Double.parseDouble(amount)));
    }
 }

//========================================================
private void showAccount(String showType) throws NumberFormatException, RemoteException
 {
 if (showType.equals(Bank.BY_NAME))
    {
    String customerName = customerNameTextField.getText().trim();
    // zero-length customer name is allowed and requests
    // show of ALL accounts from the server.
    System.out.println("Showing all accounts for " + customerName);
    displayTextArea.setText(server.showAccount(showType,
    		                                   null,
    		                                   customerName));
    }
 if (showType.equals(Bank.BY_NUMBER))
    {
    String accountNumber = getAccountNumber();
    System.out.println("Showing account " + accountNumber);
    displayTextArea.setText(server.showAccount(showType,
                                               Integer.parseInt(accountNumber),
                                               null));
    }
 }

//===============================================================
private String getAccountNumber() throws IllegalArgumentException
  {
  String accountNumber = accountTextField.getText().trim();
  if (accountNumber.length() == 0)
	  throw new IllegalArgumentException("Account Number required");
  try {
      int account = Integer.parseInt(accountNumber);
      if (account < 1)
 		 throw new IllegalArgumentException("Account number must be positive.");
      }
  catch(NumberFormatException nfe)
      {
	  throw new IllegalArgumentException("Invalid Account Number");
      }
  return accountNumber; 	
  }
	

//===============================================================
private String getAmount() throws IllegalArgumentException
  {
  String amountNumber = amountTextField.getText().trim();
  if (amountNumber.length() == 0)
	  throw new IllegalArgumentException("Amount required.");
  if (amountNumber.startsWith("$"))
      amountNumber = amountNumber.substring(1).trim();
  try {
      double amount =  Double.parseDouble(amountNumber);
      if (amount < .01)
	      throw new IllegalArgumentException("Amount must be positive.");
	  }
  catch(NumberFormatException nfe)
      {
	  throw new IllegalArgumentException("Invalid amount");
      }
  if (amountNumber.contains("."))
     {
     int periodOffset = amountNumber.indexOf(".");
	 // String wholeNumberPortion=amountNumber.substring(0,periodOffset);
	 String decimalPortion=amountNumber.substring(periodOffset+1);
	 if (decimalPortion.length() != 2)
         throw new IllegalArgumentException("A decimal point must be followed by 2 decimal digits.");
     }
  return amountNumber; 	
  }

//===============================================================
private String getCustomerName() throws IllegalArgumentException
  {
  String customerName = customerNameTextField.getText().trim();
  if (customerName.length() == 0)
      throw new IllegalArgumentException("Customer Name required.");
  if (customerName.contains(" "))
      throw new IllegalArgumentException("Customer Name must not contain blanks.");
  if (!customerName.contains(",")
   ||  customerName.startsWith(",")
   ||  customerName.endsWith(","))
	 throw new IllegalArgumentException("Customer Name must contain a single, imdedded comma.");
  int firstCommaOffset = customerName.indexOf(",");
  int secondCommaOffset = customerName.indexOf(",",firstCommaOffset+1);
  if (secondCommaOffset >= 0) // same as != -1
      throw new IllegalArgumentException("Customer Name must contain only a single, imdedded comma.");
  return customerName;
  }

}