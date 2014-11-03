
@SuppressWarnings("serial")
public class SavingsAccount extends CashAccount
{
	
	public static SavingsAccount restoreFromDB(int accountNumber, String customerName,double balance) {
		return new SavingsAccount(accountNumber, customerName, balance);
	}

	private SavingsAccount(int accountNumber,String customerName,double balance)
	{
	super(accountNumber, customerName, balance);
	}
	
public SavingsAccount(String name) throws Exception
    {
	super(name);
	}

public SavingsAccount() throws Exception 
	{
	super();
	}

public void addInterest(double interestAmount) 
   {
   deposit(interestAmount);	
   }

@Override
public String toString()
   {
	return super.toString() + " Savings Account " ;
   }
}