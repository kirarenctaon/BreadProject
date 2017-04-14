//하위카테고리에 등록된 상품정보제공 모델
package com.paris.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class DownModel extends AbstractTableModel {
	Vector<String> columnName=new Vector<>();
	Vector<Vector> data=new Vector<Vector>();
	Connection con;
	
	public DownModel(Connection con) {
		this.con=con;
		
		/*  jtable에서 getColumnCount는 table이 태어날때 호출된다. 
		db 테이블은 원래컬럼은 고정적이다.
		컬럼이 고정되지 않으면 getValueat이 작동하지 않는다.  
		생성자에서 컬럼이름을 고정하자 		 */
		
		columnName.add("product_id");
		columnName.add("subcategory_id");
		columnName.add("product_name");
		columnName.add("price");
		columnName.add("img");
	}
	
	//마우스로 유저가 클릭할때마다 id값이 바뀌므로, 아래의 메서드를 그때마다 호출하자!
	public void getList(int subcategory_id){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="select * from product where subcategory_id=?";
		
		try {
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, subcategory_id); //쿼리문 수행전 바인드 변수 먼저 받자	
			rs=pstmt.executeQuery();
			
			/* 컬럼이름은 고정적으로 주자  
			columnName.removeAll(columnName);		
			ResultSetMetaData meta=rs.getMetaData();
			for(int i=1;i<=meta.getColumnCount();i++){
				columnName.add(meta.getColumnName(i));
			}
			System.out.println("getList 컬럼크기는"+columnName.size());  */

			data.removeAll(data); //add로 누적되는 데이터 초기화
			while(rs.next()){
				Vector vec=new Vector();
				//vec.add(rs.getInt("product_id")); //boxing 
				vec.add(rs.getString("product_id")); // getInt가 정석이나 jtable이 자료형을 안가리니 getString으로 하자
				vec.add(rs.getString("subcategory_id"));
				vec.add(rs.getString("product_name"));
				vec.add(rs.getString("price"));
				vec.add(rs.getString("img"));
				data.add(vec);
			}
			
			System.out.println("getList 레코드는"+data.size());
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} //finally
	}
	
	@Override
	public String getColumnName(int col) {
		return columnName.get(col);
	}
	
	@Override
	public int getColumnCount() {
		System.out.println("컬럼 갯수는 "+columnName.size());
		return columnName.size();
	}

	@Override
	public int getRowCount() {
		System.out.println("레코드 갯수는 "+data.size());
		return data.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		Object value=data.get(row).get(col);
		System.out.println("getValueAt 호출 : "+value);
		//value는 Object형이라 자동으로 toString()이 동작해 출력할 수 있다. 
		return value;
	}

}
