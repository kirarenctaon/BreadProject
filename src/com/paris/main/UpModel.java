/*
 상위카테고리와 그 카테고리에 등록된 상품의 수 정보를 제공하는 모델
  */
package com.paris.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class UpModel extends AbstractTableModel{
	Vector<String> columnName = new Vector<String>();
	Vector<Vector> data=new Vector<Vector>();
	Connection con;
	
	public UpModel(Connection con) {
		this.con=con;
		getList();
	}
	
	//목록 가져오기
	public void getList(){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		StringBuffer sql=new StringBuffer();
		sql.append("select s.subcategory_id as subcategory_id, sub_name as 제품명, count(product_id) as 합계");
		sql.append(" from subcategory s left outer join product p on s.subcategory_id=p.subcategory_id group by s.subcategory_id, sub_name");
		

		try {
			pstmt=con.prepareStatement(sql.toString());
			rs=pstmt.executeQuery();

			//벡터들을 초기화
			columnName.removeAll(columnName);
			data.removeAll(data);		
			
			//컬럼명추출
			ResultSetMetaData meta=rs.getMetaData();
			for(int i=1;i<=meta.getColumnCount();i++){ //for를 1부터 시작하니까 <= 로 하자
				columnName.add(meta.getColumnName(i)); //아니면 i+1로 해야함
			}
			
			while(rs.next()){
				//레코드 1건을 벡터에 옮겨심자. 여기서 벡터는 DTO역할을 한다. (JTABLE이 옛날기술이라 DTO지원안함)
				Vector vec=new Vector();
				vec.add(rs.getString("subcategory_id"));
				vec.add(rs.getString("제품명"));
				vec.add(rs.getString("합계"));
				//여기까지가 레코드 한건
				data.add(vec);
			}	
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
		}//finally
	}
	
	@Override
	public String getColumnName(int col) {
		return columnName.get(col);
	}
	
	@Override
	public int getColumnCount() {
		return columnName.size();
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		return data.get(row).get(col);
	}

}
