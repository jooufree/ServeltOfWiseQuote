import java.sql.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class CitesManager extends HttpServlet {
	private static final String DB_URL = "jdbc:sqlite:C:/tomcat/webapps/ROOT/WEB-INF/classes/Cites.db";
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html; charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		// выводим заголовок страницы
		out.println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' />");
		out.println(new String("<title>Менеджер цитат</title></head><body>".getBytes("CP1251"), "UTF-8"));
		out.println(new String("<h3>Цитаты в базе</h3>".getBytes("CP1251"), "UTF-8"));
		// выводим форму с таблицей состоящей из чекбоксов для пометки на удаление и текста цитат
		out.println("<form method='POST'><input type='hidden' name='act' id='act' value='del' />");
		out.println(new String("<table border=1><tr><th>Пометить</th><th>Текст</th></tr>".getBytes("CP1251"), "UTF-8"));

		Connection conn = null;
		Statement stm = null;
		ResultSet rs = null;
		String cite = null;
		String nc = null;
		String param = null;
		String[] delstr = null;
		int id = 0;
		
		try {
			// объявление класса драйвера, создание подключения
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(DB_URL);
			stm = conn.createStatement();

			param = req.getParameter("act");
			if ( "add".equals(param) ) {
				// получаем из запроса текст новой цитаты
				nc = req.getParameter("newcite");
				// проверяем, что передана не пустая трока
				if ( ! ( "".equals(nc) ) ) {
					nc = new String(nc.getBytes("ISO-8859-1"), "CP1251");
					// отправляем в БД запрос на вставку цитаты
					stm.executeUpdate("INSERT INTO Cites (Content) VALUES ('" + nc + "')");
				}
			}
			else if ( "del".equals(param) ) {
				// получаем параметр - массив строк, содержащих id цитат для удаления
				delstr = req.getParameterValues("remid");
				if ( delstr != null ) {
					// для каждого индекса отправляем в БД запрос на удаление соответствующей цитаты
					for ( String s : delstr )
						stm.executeUpdate("DELETE FROM Cites WHERE Id=" + Integer.parseInt(s));
				}
			}
			
			// отправляем запрос на все цитаты вместе с id
			rs = stm.executeQuery("SELECT Id, Content FROM Cites");
			
			// для каждой строки с результатом выводим строку таблицы, задавая для чекбокса id цитаты
			while ( rs.next() ) {
				id = rs.getInt("Id");
				cite = new String(rs.getString("Content").getBytes("CP1251"), "UTF-8");
				out.println("<tr><td><input type='checkbox' name='remid' id='remid' value='" + id + "' /></td>");
				out.println("<td>" + cite + "</td></tr>");
			}
			// закрываем таблицу, выводим кнопку для удаления, закрываем форму
			out.println(new String("</table><input type='submit' value=' Удалить ' /></form>".getBytes("CP1251"), "UTF-8"));
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
		
		out.println(new String("<h3>Добавить цитату</h3>".getBytes("CP1251"), "UTF-8"));
		out.println("<form method='POST'><input type='hidden' name='act' id='act' value='add' /><input type='text' name='newcite' id='newcite' size='120' /><br />");
		out.println(new String("<input type='submit' value=' Добавить ' /></form>".getBytes("CP1251"), "UTF-8"));
		out.println(new String("<br /><a href='http://localhost:8080/Cites'>Случайная цитата</a>".getBytes("CP1251"), "UTF-8"));
		// закрываем вывод документа
		out.println("</body></html>");
	}
	
	// на запрос GET выводим форму с двумя кнопками - переход к случайной цитате, или редактирование цитат
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html; charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		PrintWriter out = resp.getWriter();
		
		out.println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' />");
		out.println(new String("<title>Менеджер цитат</title></head><body>".getBytes("CP1251"), "UTF-8"));
		out.println(new String("<form method='POST'><input type='hidden' name='act' id='act' value='show' /><input type='button' value=' Случайная цитата ' onclick='location.replace(\'http://localhost:8080/Cites\');' /><input type='submit' value=' Управлять цитатами ' /></form>".getBytes("CP1251"), "UTF-8"));
		out.println("</body></html>");
	}
}
