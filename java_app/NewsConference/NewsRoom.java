/*******************************************************************************
 * File: NewsRoom.java
 * Authors: Will Flores waflores@ncsu.edu
 * 
 * Usage: Implements a NewRoom Object
 * Description: This file contains the 
 * 
 * Environment: Windows 7, x64 build
 * Notes:
 *
 * Revisions: 0.0, Initial Release
 * 
 * Created on April 8, 2012
 *******************************************************************************/
public class NewsRoom {
	private String story = "";
	
	public synchronized String getStory(String topicOfInterest) throws Exception {
		while (true) {
			this.wait(); // wait for the story
			
			if (topicOfInterest.equalsIgnoreCase("any topic") || story.contains(topicOfInterest)){
				return story;
			}
		}
	}
	
	public synchronized void heresTheStory(String statement) {
		story = statement;
		notifyAll();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Will Flores waflores");
	}

} /* End of NewsRoom Class Definition */
