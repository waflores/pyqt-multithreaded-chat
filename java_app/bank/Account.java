import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


@SuppressWarnings("serial")
public class Account implements Serializable

{//  "abstract" is the OPPOSITE of "final"
// To develop a "bean" :
// 1. declare data fields (private)
// 2. provide getter/setter methods (public)
// 3. consider adding a constructor with parms
// 4. be able to "introduce myself" (toString())
// 5. be "Serializeable" (OK to save data on disk)
//    (use "transient" to keep a field from being written out)	
	
private static int lastAccountNumber;

private static synchronized int getNextAccountNumber() throws Exception
  {
  if (lastAccountNumber == 0) // only do this ONCE for
	 {                        // the 1st acct since JVM up.
	 //  read lastAccountNumber from disk DB
     ObjectInputStream ois = new ObjectInputStream(
				             new FileInputStream("LastAccountNumber.ser"));
	 lastAccountNumber = (Integer)ois.readObject();
	 ois.close();
	 } 
  lastAccountNumber ++;
  // write updated nextAccountNumber to disk DB
  ObjectOutputStream oos = new ObjectOutputStream(
			               new FileOutputStream("LastAccountNumber.ser"));
  oos.writeObject(lastAccountNumber);
  oos.close();
  return lastAccountNumber;
  }
	
private int    accountNumber;	
private String customerName;//A 4-byte pointer field to a String kind of object

public Account(String name) throws Exception // My constructor method!
   {                        // A "group setter"
   this(); // call my other constructor (the one with no parms)
   setCustomerName(name); // call my own setter!
   }

public Account() throws Exception // The "default" constructor
   {             // The constructor method is "overloaded"
   accountNumber = getNextAccountNumber();
   }

//This constructor restores objects with field values from the data base        
//Note this constructor does NOT call the no-arg constructor!
public Account(int accountNumber, String customerName) 
{              
this.accountNumber = accountNumber;               
setCustomerName(customerName);                 
}        

public String toString() // to "introduce myself"
  {                      // A "group getter"
  return getCustomerName() + " account #" + accountNumber;	
  }

public String  getCustomerName()
   {
	return customerName;
   }

public void  setCustomerName(String name)
   {
	customerName = name;
   }

public int getAccountNumber() 
   {
	return accountNumber;
   }
}