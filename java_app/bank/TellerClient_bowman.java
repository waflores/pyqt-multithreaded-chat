/** 2-29-12 for Lab 9 RMI Client-Server 
 *  This version has the GUI (but no edits) and calls
 *  the server which echos the parameters.
 */

import java.awt.event.*;
import java.awt.*;
import java.rmi.*;

import javax.swing.*;
import java.io.*;
import java.net.*;


public class TellerClient implements ActionListener
{
public static void main(String[] args)
 {
 try {
     if (args.length == 0)
        {
    	System.out.println("Server address may be provided as a command line parameter."); 
	    new TellerClient("localhost");
        }
     if (args.length == 1)
		 new TellerClient(args[0]);
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
private JFrame     mainWindow            = new JFrame("Teller Station");	
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
private JLabel     accountLabel          = new JLabel("Account #:",SwingConstants.RIGHT); 
private JLabel     amountLabel           = new JLabel("Amount:;",SwingConstants.RIGHT); 
private JLabel     customerNameLabel     = new JLabel("CustomerName Last,First:;",SwingConstants.RIGHT); 
private JTextArea  displayTextArea       = new JTextArea(10,60); 
private JScrollPane displayScrollPane    = new JScrollPane(displayTextArea);
private JPanel     topPanel              = new JPanel();
private JPanel     bottomPanel           = new JPanel();

private BankTellerServer server;

//=========================================================
public TellerClient(String serverAddress) throws Exception // CONSTRUCTOR
    {
	mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
	mainWindow.getContentPane().add(topPanel,BorderLayout.NORTH);
	
	mainWindow.getContentPane().add(displayScrollPane,BorderLayout.CENTER);
	
	bottomPanel.setLayout(new GridLayout(1,6));
	bottomPanel.add(clearButton);
	bottomPanel.add(customerNameLabel);
	bottomPanel.add(customerNameTextField);
	bottomPanel.add(showByNameButton);
	bottomPanel.add(openNewSavingsButton);
	bottomPanel.add(openNewCheckingButton);
	mainWindow.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	
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
	mainWindow.setSize(1000,500); // across, down
	mainWindow.setVisible(true);
	
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
     if (ae.getSource() == openNewSavingsButton) openNewAccount(Bank.SAVINGS);
     if (ae.getSource() == openNewCheckingButton)openNewAccount(Bank.CHECKING);
     }
 catch(RemoteException re)
     {
	 displayTextArea.setText(re.toString());
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
 String customerName = customerNameTextField.getText();
 String accountNumber = accountTextField.getText();
 System.out.println("Opening a new " + accountType
		          + " account for "  + customerName);
 displayTextArea.setText(server.openNewAccount(accountType, customerName));
 }


//======================================================
private void processAccount(String processType) throws NumberFormatException, RemoteException
 {
 String accountNumber = accountTextField.getText();
 String amount        = amountTextField.getText();
 if (processType.equals(Bank.CLOSE))
    {
	System.out.println("Closing account " + accountNumber);
	displayTextArea.setText(server.processAccount(processType, 
			                                      Integer.parseInt(accountNumber),
			                                      null));
    }
  else
    {  
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
 String accountNumber = accountTextField.getText();
 String customerName = customerNameTextField.getText();
 if (showType.equals(Bank.BY_NAME))
    {
    System.out.println("Showing all accounts for " + customerName);
    displayTextArea.setText(server.showAccount(showType,
    		                                   null,
    		                                   customerName));
    }
 if (showType.equals(Bank.BY_NUMBER))
    {
    System.out.println("Showing account " + accountNumber);
    displayTextArea.setText(server.showAccount(showType,
                                               Integer.parseInt(accountNumber),
                                               null));
    }
 }
}
