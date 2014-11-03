import java.rmi.Remote;
import java.rmi.RemoteException;


public interface BankTellerServer extends Remote
{
public String openNewAccount(String  accountType,
                             String  customerName)
                             throws  RemoteException;

public String showAccount   (String  showType,
		                     Integer accountNumber,
                             String  customerName)
                             throws  RemoteException;

public String processAccount(String  processingType,
                             Integer accountNumber,
                             Double  amount)
                             throws  RemoteException;
}