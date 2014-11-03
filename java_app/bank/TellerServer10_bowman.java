/** 2-29-12 for Lab 9 RMI Client-Server
 *  This version just echos the parameters back to
 *  the client.
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;


public class TellerServer10_bowman extends    UnicastRemoteObject 
                            implements BankTellerServer
{
public static void main(String[] args)
 {
 try {
	 new TellerServer10_bowman();
	 }
 catch (Exception e)
     {
	 System.out.println(e);
	 }
 }

private ConcurrentHashMap<Integer,CashAccount> accounts =
   new  ConcurrentHashMap<Integer,CashAccount>();

//========================================================
public TellerServer10_bowman() throws Exception
  {
  super(); // call UnicastRemoteObject constructor
  Naming.rebind("TellerServices",this);//register with rmiregistry
  try {
      ObjectInputStream ois = new ObjectInputStream(
                              new FileInputStream("accounts.ser"));
      accounts = (ConcurrentHashMap<Integer,CashAccount>)ois.readObject();
      ois.close();
      System.out.println(accounts);
      }
  catch(FileNotFoundException fnfe)
      {
	  System.out.println("accounts.ser file is not found. Starting with empty accounts collection.");
      }
  System.out.println("TellerServices is up at "
		           + InetAddress.getLocalHost().getHostAddress());
  }


//========================================================
public String openNewAccount(String accountType,
		                     String customerName)
 {
 CashAccount ca;
 try {
     if (accountType.equals(Bank.CHECKING))
        ca = new CheckingAccount(customerName);
else if (accountType.equals(Bank.SAVINGS))
        ca = new SavingsAccount(customerName);
else return "ERROR: account type " + accountType
         + " is not recognized by the server."
         + " Call the IT department!";
     }
 catch(Exception ioe)
     {
	 return "ERROR: server cannot create new accounts. "
	      + ioe.toString();
     }
 accounts.put(ca.getAccountNumber(),ca);
 try {
     saveAccounts(); // save accounts collection on disk
     }
 catch (IOException ioe)
     {
	 return "ERROR: server is unable to save new accounts on disk. "
	      + ioe.toString();
     }
 return ca.toString();
	
 // return "Server says: opening a new " + accountType
 //      + " account for "               + customerName;
 }

//========================================================
public String showAccount(String showType,
		                  Integer accountNumber,
	                      String customerName) 
 {
      if (showType.equals(Bank.BY_NUMBER))
         {
    	 CashAccount ca = accounts.get(accountNumber);
    	 if (ca == null)
    		 return "Account " + accountNumber + " not found.";
    	  else
    		 return ca.toString(); // let account introduce itself! 
	     // return "Server says: showing account " + accountNumber;
         }
 else if (showType.equals(Bank.BY_NAME))
         {
	     Collection<CashAccount>listOfAccounts=accounts.values();
         TreeSet<String> theHitList = new TreeSet<String>();
         String hitString = "";
         String newLine = System.getProperty("line.separator");
         boolean showingAllAccounts;
	     if (customerName.length() == 0)
             showingAllAccounts = true;
	      else
	    	 showingAllAccounts = false;
	     String enteredName = customerName.toUpperCase();
	     for (CashAccount ca : listOfAccounts)
	    	 {
	    	 String accountCustomerName = ca.getCustomerName().toUpperCase();
	    	 if (accountCustomerName.startsWith(enteredName)
	    	  || showingAllAccounts)
	    		 theHitList.add(ca.toString());
	    	 }
	     if (theHitList.isEmpty())
	    	 return "No accounts found that start with " + customerName;
	     for (String account : theHitList)
	    	  hitString += newLine + account;
	     return hitString;
	     //return "Server says: showing all accounts for " + customerName;
         }
 else     return "Server says: showType of " + showType
               + " is not recognized by the server.";
 }

//=========================================================
public String processAccount(String processingType,
		                     Integer accountNumber,
	                         Double amount)
  {
  CashAccount ca = accounts.get(accountNumber);
  if (ca == null)
      return "ERROR: Account " + accountNumber + " not found.";
  try {
      if (processingType.equals(Bank.CLOSE))
         {
	     if (ca.getBalance() == 0)
	    	 return ca.getCustomerName();
	     else
	    	 return "ERROR: Cannot close account with non-zero balance.";
    	 //if (accountNumber%2 == 0) // even number 
       	 //    return "Smith,Bubba";	 
    	 // else // odd account number
	     //    return "ERROR: account " + accountNumber + " does not have a zero balance and cannot be closed.";
         }
 else if (processingType.equals(Bank.CONFIRM))
         {
	     accounts.remove(accountNumber);
	     saveAccounts();
         return "Account " + accountNumber + " has been closed.";
         }
 else if (processingType.equals(Bank.DEPOSIT))
         {
	     ca.deposit(amount);
	     saveAccounts();
	     return ca.toString();
         }
 else if (processingType.equals(Bank.WITHDRAW))
         {
	     try {
	    	 ca.withdraw(amount);
	         }
	     catch(OverdraftException oe)
	         {
	    	 return ("Insufficient Funds");
	         }
	     saveAccounts();
	     return ca.toString();
         }
 else    return "Server says: processingType of " + processingType
              + " is not recognized by the server.";
      }// bottom of try
  catch(IOException ioe)
      {
      return "ERROR: server is unable to save updated  accounts on disk. "
           + ioe.toString();
      }
      //return "Server says: Doing a "      + processingType 
      //     + " of "         + amount
      //     + " on account " + accountNumber;
  }
  

//=========================================================
private synchronized void saveAccounts() throws IOException
  {
  ObjectOutputStream oos = new ObjectOutputStream(
		                   new FileOutputStream("accounts.ser"));
  oos.writeObject(accounts);
  oos.close();
  }
}