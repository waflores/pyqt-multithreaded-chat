
public class StartNewsConference
{ 
public static void main(String[] args)
	{
    NewsRoom ncr = new NewsRoom();
    new Reporter(1,"election"  , ncr); // Specify a topic
    new Reporter(2,"economy"   , ncr); // of interest
    new Reporter(3,"politics"  , ncr); // to the Reporter.
    new Reporter(4,"any topic" , ncr);
    new Reporter(5,"election"  , ncr);
    new Reporter(6,"healthfood", ncr);
    new Reporter(7,"ANY TOPIC" , ncr);
    new Reporter(8,"jobs"      , ncr);
    new Reporter(9,"Afganistan", ncr);
    new WhiteHousePressSecretary(ncr);
	}
}
