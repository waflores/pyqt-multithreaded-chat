
@SuppressWarnings("serial")
public class OverdraftException extends Exception 
{ // The exception MUST BE CAUGHT because I extended Exception
  // (extend RuntimeException to make an exception that NEED NOT be caught)
public OverdraftException() 
    {
	super();
	}

public OverdraftException(String errorMessage)
    {
	super(errorMessage);
	}

public OverdraftException(Throwable exception)
    {
	super(exception);
	}

public OverdraftException(String errorMessage, Throwable exception)
    {
	super(errorMessage, exception);
	}

}