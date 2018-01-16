import java.sql.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
public class Cites extends HttpServlet {
	private static final String DB_URL = "jdbc:sqlite:C:/tomcat/webapps/ROOT/WEB-INF/classes/Cites.db";
	private Random rand;
	public void init() throws ServletException {
		rand = new Random();
	}
	// Обработка запроса GET
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html; charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		out.println(new String("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /><title>Мудрые цитаты</title></head><body>".getBytes("CP1251"), "UTF-8"));
		Connection conn = null;
		Statement stm = null;
		ResultSet rs = null;
		String cite = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(DB_URL);
			stm = conn.createStatement();
			rs = stm.executeQuery("SELECT Content FROM Cites");
			// динамический массив строк под цитаты
			List<String> all = new ArrayList<String>();
			while ( rs.next() ) {
				cite = new String(rs.getString("Content").getBytes("CP1251"), "UTF-8");
				all.add(cite);
			}
			cite = all.get(rand.nextInt(all.size()));
			out.println(cite + "<br />");
			out.println(new String("<form method='POST' action='CitesManager'><input type=button value=' Другая цитата ' onclick='location.reload();' /><input type='submit' value=' Управлять цитатами ' /></form>".getBytes("CP1251"), "UTF-8"));
		}
		catch(ClassNotFoundException cnfe) {
			throw new ServletException("Can't find SQLite class: " + cnfe.getMessage());
		}
		catch(SQLException sqle) {
			throw new ServletException("SQL Error: " + sqle.getMessage());
		}
		finally {
			try {
				if ( rs != null )
					rs.close();
				if ( stm != null )
					stm.close();
				if ( conn != null )
					conn.close();
			}
			catch(SQLException sqle2) {
				throw new ServletException("SQL Error: " + sqle2.getMessage());
			}
		}
		
		out.println("</body></html>");
	}
}
