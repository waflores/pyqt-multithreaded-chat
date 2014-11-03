import java.io.*;

/*******************************************************************************
 * File: WhiteHousePressSecretary.java
 * Authors: Will Flores waflores@ncsu.edu
 * 
 * Usage: Implements a WhiteHousePressSecretary Object
 * Description: This file contains the 
 * 
 * Environment: Windows 7, x64 build
 * Notes:
 *
 * Revisions: 0.0, Initial Release
 * 
 * Created on April 8, 2012
 *******************************************************************************/

public class WhiteHousePressSecretary implements Runnable {
	private NewsRoom nr; 
	// you need to read from the console in order to tell reporters stuff ;
	
	
	public WhiteHousePressSecretary(NewsRoom nr) {
		// TODO Auto-generated constructor stub
		this.nr = nr;
		new Thread(this).start();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Will Flores waflores");
	}

	@Override
	
	public void run() {
		String EXIT = "This concludes the news conference.";
		InputStreamReader inputStr = new InputStreamReader(System.in);
		BufferedReader buffr = new BufferedReader(inputStr);
		
		while (true) {
			String input;
			// Since IOException can be thrown - use a try/catch
			try {
				System.out.println("What do you want to talk about?");
				input = buffr.readLine().trim();
				if (input.equalsIgnoreCase(EXIT)) System.exit(0); // Terminate the JVM
				nr.heresTheStory(input);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}// End try/catch
		} // End while loop
	} // End run Method

} /* End of WhiteHousePressSecretary Class Definition */
