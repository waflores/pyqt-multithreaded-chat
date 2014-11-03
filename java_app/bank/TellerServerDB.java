/** 2-29-12 for Lab 9 RMI Client-Server
 *  This version just echos the parameters back to
 *  the client.
 */

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.Collection;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;


@SuppressWarnings("serial")
public class TellerServerDB extends    UnicastRemoteObject 
                            implements BankTellerServer
{
private ConcurrentHashMap<Integer,CashAccount> accounts =
			new  ConcurrentHashMap<Integer,CashAccount>();

private Connection connection;
private PreparedStatement insertStatement;
private PreparedStatement updateStatement;
private PreparedStatement deleteStatement;
private Statement selectAllStatement;

public static void main(String[] args) throws Exception
 {
 try {
	 new TellerServerDB();
	 }
 catch (Exception e)
     {
	 System.out.println(e);
	 }
 }

//========================================================
public TellerServerDB() throws Exception
  {
	super(); // call UnicastRemoteObject constructor
	Naming.rebind("TellerServices",this);//register with rmiregistry
	
	// connect to database
	Class.forName("com.ibm.db2j.jdbc.DB2jDriver");
    System.out.println("DB Driver loaded!");
    connection = DriverManager.getConnection(
                      "jdbc:db2j:C:\\database\\QuoteDB");
    System.out.println("Connection made to Data Base!");
    
    insertStatement = connection.prepareStatement("INSERT INTO BANK_ACCOUNTS "
    		+ "(ACCOUNT_NUMBER, ACCOUNT_TYPE, CUSTOMER_NAME, BALANCE) "
    		+"VALUES (?,?,?,?)");
    
    updateStatement = connection.prepareStatement(
            "UPDATE BANK_ACCOUNTS "
          + "SET BALANCE = ? "
          + "WHERE ACCOUNT_NUMBER = ?");
    
    deleteStatement = connection.prepareStatement(
            "DELETE FROM BANK_ACCOUNTS "
          + "WHERE ACCOUNT_NUMBER = ?");
    
    selectAllStatement = connection.createStatement();
    
    ResultSet rs = selectAllStatement.executeQuery(
            "SELECT * FROM BANK_ACCOUNTS");
    
    while (rs.next())
    {
	    // get the column values for this row
	    int    accountNumber = rs.getInt   ("ACCOUNT_NUMBER");
	    String accountType   = rs.getString("ACCOUNT_TYPE");
	    String customerName  = rs.getString("CUSTOMER_NAME");
	    double balance       = rs.getDouble("BALANCE");
	    
	    System.out.println(" acct#="    + accountNumber
	            + " acctType=" + accountType
	            + " custName=" + customerName
	            + " balance="  + balance);
	    if(accountType.equals(Bank.CHECKING))
	    {
	    	CheckingAccount ca = CheckingAccount.restoreFromDB(accountNumber, customerName, balance);
	    	accounts.put(accountNumber, ca);
	    }
	    else if(accountType.equals(Bank.SAVINGS))
	    {
	    	SavingsAccount sa = SavingsAccount.restoreFromDB(accountNumber, customerName, balance);
	    	accounts.put(accountNumber, sa);
	    }
	    else System.out.println("Invalid account type");
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
	    insertStatement.setInt   (1, ca.getAccountNumber());
	    insertStatement.setString(2, accountType);
	    insertStatement.setString(3, customerName);
	    insertStatement.setDouble(4, 0); // initial balance
	    insertStatement.executeUpdate();
	    }
	catch(SQLException sqle)
	    {
	    return "ERROR: Unable to add new account to the data base."
	          + sqle.toString();
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
	     try {
	    	    deleteStatement.setInt(1, accountNumber);
	    	    deleteStatement.executeUpdate();
	    	    }
	    	catch(SQLException sqle)
	    	    {
	    	    return "ERROR: Server is unable to delete account from the data base."
	    	         + sqle.toString();
	    	    }
         return "Account " + accountNumber + " has been closed.";
         }
 else if (processingType.equals(Bank.DEPOSIT))
         {
	     ca.deposit(amount);
	     try {
	    	    updateStatement.setDouble (1, ca.getBalance());
	    	    updateStatement.setInt    (2, ca.getAccountNumber());
	    	    updateStatement.executeUpdate();
    	    }
    	catch(SQLException sqle)
	    {
    		return "ERROR: Server is unable to update account in the data base."
    	         + sqle.toString();
	    }
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
	     try {
	    	    updateStatement.setDouble (1, ca.getBalance());
	    	    updateStatement.setInt    (2, ca.getAccountNumber());
	    	    updateStatement.executeUpdate();
 	    	}
	     catch(SQLException sqle)
	     	{
	    	 return "ERROR: Server is unable to update account in the data base."
	    			 + sqle.toString();
	     	}
	     return ca.toString();
         }
 else    return "Server says: processingType of " + processingType
              + " is not recognized by the server.";
      }// bottom of try
  catch(Exception ioe) 
      {
      return "ERROR: server is unable to save updated  accounts on disk. "
           + ioe.toString();
      }
      //return "Server says: Doing a "      + processingType 
      //     + " of "         + amount
      //     + " on account " + accountNumber;
  }

}