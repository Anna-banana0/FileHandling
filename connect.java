import java.sql.*;
public class connect {
	Connection c;
	Statement s;
	public connect() {
		try {
			String url= "jdbc:mysql://localhost:3306/bankdb";
			String username="bank";
			String password="securepassword";
			Class.forName("com.mysql.jdbc.Driver");
			c= DriverManager.getConnection(url,username,password);
			s= c.createStatement();
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
}
