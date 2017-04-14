//����ī�װ��� ��ϵ� ��ǰ�������� ��
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
		
		/*  jtable���� getColumnCount�� table�� �¾�� ȣ��ȴ�. 
		db ���̺��� �����÷��� �������̴�.
		�÷��� �������� ������ getValueat�� �۵����� �ʴ´�.  
		�����ڿ��� �÷��̸��� �������� 		 */
		
		columnName.add("product_id");
		columnName.add("subcategory_id");
		columnName.add("product_name");
		columnName.add("price");
		columnName.add("img");
	}
	
	//���콺�� ������ Ŭ���Ҷ����� id���� �ٲ�Ƿ�, �Ʒ��� �޼��带 �׶����� ȣ������!
	public void getList(int subcategory_id){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="select * from product where subcategory_id=?";
		
		try {
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, subcategory_id); //������ ������ ���ε� ���� ���� ����	
			rs=pstmt.executeQuery();
			
			/* �÷��̸��� ���������� ����  
			columnName.removeAll(columnName);		
			ResultSetMetaData meta=rs.getMetaData();
			for(int i=1;i<=meta.getColumnCount();i++){
				columnName.add(meta.getColumnName(i));
			}
			System.out.println("getList �÷�ũ���"+columnName.size());  */

			data.removeAll(data); //add�� �����Ǵ� ������ �ʱ�ȭ
			while(rs.next()){
				Vector vec=new Vector();
				//vec.add(rs.getInt("product_id")); //boxing 
				vec.add(rs.getString("product_id")); // getInt�� �����̳� jtable�� �ڷ����� �Ȱ����� getString���� ����
				vec.add(rs.getString("subcategory_id"));
				vec.add(rs.getString("product_name"));
				vec.add(rs.getString("price"));
				vec.add(rs.getString("img"));
				data.add(vec);
			}
			
			System.out.println("getList ���ڵ��"+data.size());
			
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
		System.out.println("�÷� ������ "+columnName.size());
		return columnName.size();
	}

	@Override
	public int getRowCount() {
		System.out.println("���ڵ� ������ "+data.size());
		return data.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		Object value=data.get(row).get(col);
		System.out.println("getValueAt ȣ�� : "+value);
		//value�� Object���̶� �ڵ����� toString()�� ������ ����� �� �ִ�. 
		return value;
	}

}
