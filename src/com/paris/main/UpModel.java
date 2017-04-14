/*
 ����ī�װ����� �� ī�װ����� ��ϵ� ��ǰ�� �� ������ �����ϴ� ��
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
	
	//��� ��������
	public void getList(){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		StringBuffer sql=new StringBuffer();
		sql.append("select s.subcategory_id as subcategory_id, sub_name as ��ǰ��, count(product_id) as �հ�");
		sql.append(" from subcategory s left outer join product p on s.subcategory_id=p.subcategory_id group by s.subcategory_id, sub_name");
		

		try {
			pstmt=con.prepareStatement(sql.toString());
			rs=pstmt.executeQuery();

			//���͵��� �ʱ�ȭ
			columnName.removeAll(columnName);
			data.removeAll(data);		
			
			//�÷�������
			ResultSetMetaData meta=rs.getMetaData();
			for(int i=1;i<=meta.getColumnCount();i++){ //for�� 1���� �����ϴϱ� <= �� ����
				columnName.add(meta.getColumnName(i)); //�ƴϸ� i+1�� �ؾ���
			}
			
			while(rs.next()){
				//���ڵ� 1���� ���Ϳ� �Űܽ���. ���⼭ ���ʹ� DTO������ �Ѵ�. (JTABLE�� ��������̶� DTO��������)
				Vector vec=new Vector();
				vec.add(rs.getString("subcategory_id"));
				vec.add(rs.getString("��ǰ��"));
				vec.add(rs.getString("�հ�"));
				//��������� ���ڵ� �Ѱ�
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