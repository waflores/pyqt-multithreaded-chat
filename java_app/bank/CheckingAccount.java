
@SuppressWarnings("serial")
public class CheckingAccount extends CashAccount
{
	public static CheckingAccount restoreFromDB(int accountNumber, String customerName,double balance) {
		return new CheckingAccount(accountNumber, customerName, balance);
	}
	private CheckingAccount(int accountNumber,String customerName,double balance)
	{
	super(accountNumber, customerName, balance);
	}
public CheckingAccount(String name) throws Exception
    {
	super(name);
	}

public CheckingAccount() throws Exception 
	{
	super();
	}

public void chargeFee(double feeAmount) throws OverdraftException
   {
   withdraw(feeAmount);	
   }

@Override
public String toString()
   {
	return  super.toString() + " Checking Account ";
   }
}