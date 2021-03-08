import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
public class DBService {
	
		private static Connection con;
		private Properties props;
		public DBService() {
			String url= "jdbc:mysql://localhost:3306/bankdb";
			String username="bank";
			String password="securepassword";
		Connection con;
		try {
			con = DriverManager.getConnection(url, username, password);
			DBService.con = con;
			System.out.println("************** Connected to Database! *************************");
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			con = null;
			e.printStackTrace();
		}
	}
		
		{
			props = new Properties();
			try {
				props.load(new FileReader("C:\\Users\\RDRIL\\eclipse-workspace\\CalendarDemo\\src\\TableContent.properties"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
/*********************************************************************************************************/	
		void tableCreation(File file, Map<String, String> valids, List<String> validKeys) {
			
			System.out.println("Creating table");
			try {
				 String tableName = file.getName().split("\\.")[0];
				//System.out.println(tableName);
				con.setAutoCommit(false);
				Statement statement = con.createStatement();
				String addTranSql = "CREATE TABLE "+tableName+" ("+props.getProperty("Column1")+" VARCHAR(5));";
				System.out.println(addTranSql);
				statement.executeUpdate(addTranSql);
				System.out.println("Table Created!");
				 
				if(valids.size()>1) {
					for(int i=1; i<valids.size(); i++) {
						String sql = "ALTER TABLE "+tableName+" ADD "+validKeys.get(i)+" VARCHAR(10);";
						System.out.println(sql);
			            statement.executeUpdate(sql);
					}
					System.out.println("more columns added");
				}
				con.commit();
			}
			catch(SQLException ex)
			{
				try {
					con.rollback();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Error !"+ex.getMessage());
			}
			
		}
/************************************************************************************************************/
		void fieldAddition(File file, Map<String, String> valids, List<String> validKeys) {
			
			try {
				String tableName = file.getName().split("\\.")[0];
				System.out.println("adding the fields of file into table****"+tableName);
				con.setAutoCommit(false);
				Statement statement = con.createStatement();
					String s1 = "INSERT INTO ";
					s1 = s1 + tableName + " VALUES(" ;
					for(String s : validKeys) {
						s1= s1 +"'"+ valids.get(s) +"'"+ ",";
					}
					s1 = s1.substring(0,s1.length()-1);
					s1 = s1 + ");";
					//INSERT INTO <tablename> VALUES('value1','value2','value3');
				System.out.println(s1);
				 statement.executeUpdate(s1);
				 System.out.println("Rows inserted!!");
				
			}catch(SQLException ex)
			{
				try {
					con.rollback();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Error !"+ex.getMessage());
			}
		}
		
}