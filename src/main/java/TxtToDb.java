import java.io.*;
import java.sql.*;

public class TxtToDb {
	public static void main(String[] args) {
		try {
			// Объявление класса драйвера SQLite
			Class.forName("org.sqlite.JDBC");
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
			System.exit(1);
		}
		try (BufferedReader reader = new BufferedReader(new FileReader("cites.txt"));
			Connection con = DriverManager.getConnection("jdbc:sqlite:cites.db");) {
			Statement stm = null;
			PreparedStatement pr = null;
			stm = con.createStatement();
			String sql = "DROP TABLE IF EXISTS Cites";
			stm.executeUpdate(sql);
			sql = "CREATE TABLE Cites (Id INTEGER PRIMARY KEY AUTOINCREMENT, Content TEXT NOT NULL)";
			stm.executeUpdate(sql);
			String str = null;
			while ( ( str = reader.readLine() ) != null ) {
				sql = "INSERT INTO Cites (Content) VALUES ('" + str + "')";
				stm.executeUpdate(sql);
			}
			stm.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Database created.");
	}
}
