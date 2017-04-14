/* 데이터베이스 계정를 중복해서 기재하지 않기 위함(db연동하는 각각의 클래스 마다)
	1.정보를 한곳에 두기
	2.인스턴스의 갯수를 한개만 둬보기 : 어플리케이션 가동중 생성되는 Connection객체를 하나로 통일하기 위함
	 
*/
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
	static private DBManager instance;
	private String driver="oracle.jdbc.driver.OracleDriver";
	private String url="jdbc:oracle:thin:@localhost:1521:XE";
	private String user="bread";
	private String password="bread";
	private Connection con;
	
	private DBManager(){
		// 1. 드라이버 로드 2.접속 3. 쿼리문 수행 4. 반납 & 해제
		try {
			Class.forName(driver); //Class는 하나의 자료형, 남이 준 바이너리 파일에서 메서드를 추려낼수 있다. 클래스명에 대한 클래스??
			con=DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	static public DBManager getInstance() {
		if(instance==null){
			instance= new DBManager();
		}
		return instance;
	}
	
	public Connection getConnection() {
		return con;
	}
	
	public void disConnect(Connection con){
		if(con!=null){
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
