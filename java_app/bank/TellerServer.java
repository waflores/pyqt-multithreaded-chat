/*******************************************************************************
 * File: TellerServer.java
 * Author: Will Flores waflores@ncsu.edu
 * Usage: Implements Lab 9
 * Description: This file contains...
 * Environment: Windows 7, x64 build
 * Notes:
 * Revisions: 0.0, Initial Release
 * 
 * Created on March 13, 2012
 *******************************************************************************/

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;


@SuppressWarnings("serial")
public class TellerServer extends UnicastRemoteObject implements
		BankTellerServer {

	public TellerServer() throws Exception {
		super();
		Naming.rebind("TellerServices", this);//key,obj to rmiregistry 
		/* The Server is up */
		System.out.println("TellerServices is up at " + InetAddress.getLocalHost().getHostAddress());
	}

	@Override
	public String openNewAccount(String accountType, String customerName) {
		if (customerName.length() != 0) {
			return ("Opening a new " + accountType + " account for "  + customerName);
		}
		else return ("openNewAccount_Error: Please enter Customer's Name.");
	}

	@Override
	public String showAccount(String showType, Integer accountNumber, String customerName) {
		if (showType.equals(Bank.BY_NAME)) {
			if (customerName.length() != 0) {
				return ("Showing all accounts for " + customerName);
			}
			else return ("showAccount_Error: Please enter Customer's Name.");
		 }
		 
		 if (showType.equals(Bank.BY_NUMBER)) {
			 return ("Showing account " + accountNumber);
		 }
		 else return ("showAccount_Error: Nothing to show, check function call again.");
	}

	@Override
	public String processAccount(String processingType, Integer accountNumber, Double amount) {
		 if (processingType.equals(Bank.CLOSE)) {
			 return ("Closing account " + accountNumber);
		 }
		  else {
			  return ("Doing a "     + processingType
                      + " of "         + amount
                      + " on account " + accountNumber);
		  }
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Will Flores waflores\r\n"); // My name
		
		/* Command Line Parameter Check */
		if(args.length != 0) {
			System.out.println("Usage: java TellerServer");
			System.out.println("Don't invoke TellerServer with cmdline parameters.");
		}
		
		/* Boot TellerServer */
		try {
			new TellerServer();
		}
		catch (Exception e){
			/* Server application went down! */
			System.out.println(e.toString());
		}
	}

}
