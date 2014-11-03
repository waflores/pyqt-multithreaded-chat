import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class ShowAccountsTable
{
public static void main(String[] args) throws Exception
	{
	new ShowAccountsTable();
	}
	
private JFrame window       = new JFrame("BANK_ACCOUNT Table Contents");
private JPanel displayPanel = new JPanel();

public ShowAccountsTable() throws Exception
       {
	   Class.forName("com.ibm.db2j.jdbc.DB2jDriver");
	   System.out.println("Driver loaded!");
	   Connection connection = DriverManager.getConnection(
	 	    			"jdbc:db2j:C:\\database\\QuoteDB");
	   System.out.println("Connected to database!");
	  
       Statement selectStatement = connection.createStatement();
	   ResultSet rs = selectStatement.executeQuery("SELECT * FROM BANK_ACCOUNTS");
	  
	   System.out.println("Results from table BANK_ACCOUNTS");
	  
	   ResultSetMetaData md = rs.getMetaData();
	   System.out.print("( " + md.getColumnCount() + " columns: ");
	   displayPanel.setLayout(new GridLayout(0,md.getColumnCount()));

	   // Put Column Names from MetaData in top row of GUI grid
	   for( int i = 1; i <= md.getColumnCount(); i++)
		  {
	      System.out.print(md.getColumnName(i) + ", ");
	      displayPanel.add(new JTextField(md.getColumnName(i)));
		  }
	   System.out.println(" )");
	  
	   // Fill GUI with data from table
	   while(rs.next())
			{
			for (int n = 1; n <= md.getColumnCount(); n++)
				{
			    displayPanel.add(new JLabel(rs.getString(n)));
			    System.out.print(rs.getString(n) + "  ");
				}
			System.out.println(" ");
			}
	  
	  // Build GUI
	  window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  window.getContentPane().add(displayPanel, BorderLayout.CENTER);
	  window.setSize(400,400);
	  window.setVisible(true);
	  }
}
