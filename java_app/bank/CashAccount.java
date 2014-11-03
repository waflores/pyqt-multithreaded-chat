import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;

import db2j.b.cu;

@SuppressWarnings("serial")
public abstract class CashAccount extends Account 
{
private double balance; // protected (object)
                        // package   (directory)
public void deposit(double depositAmount) throws IllegalArgumentException
   {
   if (depositAmount < 0 )
	   throw new IllegalArgumentException("Deposit amount must be positive.");// ALSO RETURNS!
   balance += depositAmount;	
   }

public void withdraw(double withdrawAmount) throws OverdraftException,IllegalArgumentException
  {
  if (withdrawAmount < 0 )
      throw new IllegalArgumentException("Withdraw amount must be positive.");// ALSO RETURNS!
  if (withdrawAmount > balance)
	  throw new OverdraftException("Insufficient Funds account " + getAccountNumber());
  balance -= withdrawAmount;	
  }

@Override
public String toString()
   {
   BigDecimal  balanceBD = new BigDecimal(balance,MathContext.DECIMAL32);//set precision to 7 digits
   balanceBD = balanceBD.setScale(2,BigDecimal.ROUND_DOWN);//scale is # of decimal digits (= 2)
   String balanceString = balanceBD.toPlainString();// no exponents	
   return super.toString() + " balance is " + balanceString + " " ;
   }

public CashAccount(String name) throws Exception
    {           // Eclipse stuck this in!
	super(name);// call Mom's constructor that takes a String
    }

public CashAccount(int accountNumber, String customerName, double balance) {
	super(accountNumber, customerName);
	this.balance = balance;
}

public CashAccount() throws Exception
    {
	super(); // compiler sticks this in!	
	}

public double getBalance()
    {
	return balance;
    }
}