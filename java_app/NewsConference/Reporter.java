/*******************************************************************************
 * File: Reporter.java
 * Authors: Will Flores waflores@ncsu.edu
 * 
 * Usage: Implements a Reporter Thread
 * Description: This file contains the 
 * 
 * Environment: Windows 7, x64 build
 * Notes:
 *
 * Revisions: 0.0, Initial Release
 * 
 * Created on April 8, 2012
 *******************************************************************************/

public class Reporter implements Runnable {
	private int reporterNumber;
	private String topicOfInterest;
	private NewsRoom nr;

	public Reporter(int reporterNumber, String topicOfInterest, NewsRoom nr) {
		this.reporterNumber = reporterNumber;
		this.topicOfInterest = topicOfInterest;
		this.nr = nr;
		
		new Thread(this).start(); // Jump to run method
	}

	@Override
	public void run() {
		String story;
		try {
			story = nr.getStory(topicOfInterest);
			
			System.out.println("This is reporter " + reporterNumber + ". My topic of interest is " + topicOfInterest
					+ "\n" + "The president's statement that was of of particular interest to me was: \n" +
					story);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Will Flores waflores");

	}
	
} /* End of Reporter Class Definition */
