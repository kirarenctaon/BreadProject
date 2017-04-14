/*
join문이란? 정규화에 의해 물리적으로 분리된 테이블을 마치 하나의 테이블처럼 보여줄 수 있는 쿼리기법

inner조인 : 조인대상이 되는 테이블간 공통적인 레코드만 가져온다. (우리가 지금까지 사용해왔던 조인)
				주의할점) 공통적인 레코드가 아닌 경우 누락됨
outer조인 : 조인대상이 되는 테이블간 공통된 레코드뿐만 아니라, 지정한 테이블의 레코드는 무조건 다 가져오는 조인 
 */

package com.paris.main;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.naming.spi.DirStateFactory.Result;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import db.DBManager;
import db.SubCategory;
import db.TopCategory;

public class MainWindow extends JFrame implements ItemListener, ActionListener{
	JPanel p_west, p_center, p_east; //전체를 삼등분해서 붙일 패널들
	JPanel p_up, p_down; // 센터에 그리드로 양분해서 붙일 패널들
	JTable table_up, table_down;
	JScrollPane scroll_up, scroll_down;
	
	//서쪽 영역
	Choice ch_top, ch_sub;
	JTextField t_name, t_price;
	Canvas can_west;
	JButton bt_regist;
	
	//동쪽영역
	Canvas can_east;
	JTextField t_id, t_name2, t_price2;
	JButton bt_edit, bt_delete;
	
	DBManager manager;
	Connection con;
	
	ArrayList<TopCategory> topList = new ArrayList<TopCategory>();//상위카테코리 list
	ArrayList<SubCategory> subList = new ArrayList<SubCategory>();//하위카테코리 list
	
	BufferedImage image=null;
	
	//Table 모델 객체들
	UpModel upModel;
	DownModel downModel;
	JFileChooser chooser;
	File file;
	
	
	public MainWindow() {
		/*--------------------------------------  메인
		 -------------------------------------------*/
		p_west=new JPanel();
		p_center=new JPanel();
		p_east=new JPanel();
		p_up=new JPanel();
		p_down=new JPanel();
		table_up=new JTable();
		table_down=new JTable();
		scroll_up=new JScrollPane(table_up);
		scroll_down=new JScrollPane(table_down);
		chooser=new JFileChooser("C:/html_workspace/images/");
		
		/*---------------------------------------  서
		 -------------------------------------------*/
		ch_top=new Choice();
		ch_sub=new Choice();
		t_name=new JTextField(10);
		t_price=new JTextField(10);
		
		//캔버스 생성
		try {
			URL url=this.getClass().getResource("/default.png");
			image=ImageIO.read(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		can_west=new Canvas(){
			@Override
			public void paint(Graphics g) {
				g.drawImage(image, 0, 0, 135, 135, this);
			}
		}; //여기까지만 하면 캔버스사이즈가 지정이 안되서 보이지가 않는다. 	
		can_west.setPreferredSize(new Dimension(135, 135)); //사이즈를 설정하고 나면 보임
		bt_regist=new JButton("등록");	
		
		ch_top.setPreferredSize(new Dimension(135, 40));
		ch_sub.setPreferredSize(new Dimension(135, 40));
		ch_top.add("▼ 상위 카테고리 선택");
		ch_sub.add("▼ 하위 카테고리 선택");
		
		/*----------------------------------------  동
		 -------------------------------------------*/
		can_east=new Canvas(){
			@Override
			public void paint(Graphics g) {
				g.drawImage(image, 0, 0, 135, 135, this);
			}
		};
		can_east.setPreferredSize(new Dimension(135, 135)); 
		
		t_id=new JTextField(10);
		t_name2=new JTextField(10);
		t_price2=new JTextField(10);
		bt_edit=new JButton("수정");
		bt_delete=new JButton("삭제");
		
		
		//서부착
		p_west.add(ch_top); p_west.add(ch_sub); 
		p_west.add(t_name); p_west.add(t_price); 
		p_west.add(can_west); p_west.add(bt_regist); 
		//동부착
		t_id.setEditable(false);//프라이머리키이므로 유저들이 수정하지 못하게하ㅏㅁ
		p_east.add(t_id); 
		p_east.add(t_name2); p_east.add(t_price2); p_east.add(can_east); 
		p_east.add(bt_edit); p_east.add(bt_delete);
		
		//각 패널의 구분을 위해 색상지정
		p_west.setBackground(Color.WHITE);
		p_east.setBackground(Color.WHITE);
		
		//패널들의 크기 지정
		p_west.setPreferredSize(new Dimension(150, 700));
		p_center.setPreferredSize(new Dimension(550, 700));
		p_east.setPreferredSize(new Dimension(150, 700));
		
		//센터의 그리드 적용하고 위아래 구성
		p_center.setLayout(new GridLayout(2, 1));
		p_center.add(p_up);
		p_center.add(p_down);
		
		//스크롤 부착
		p_up.setLayout(new BorderLayout()); //패널의 flow 속성을 없애서 jtable표가 패널에 꽉차게함
		p_down.setLayout(new BorderLayout());  //패널의 flow 속성을 없애서 jtable표가 패널에 꽉차게함
		p_up.add(scroll_up);
		p_down.add(scroll_down);
		
		add(p_west, BorderLayout.WEST);
		add(p_center);
		add(p_east, BorderLayout.EAST);
		
		//초이스와 리스너 연결
		ch_top.addItemListener(this);
		//ch_sub.addItemListener(this); 아래는 선택해도 발생할 사건이 없으므로 빼자
		
		//버튼과 리스너 연결
		bt_regist.addActionListener(this);
		
		//캔버스에 마우스 리스너와 연결
		can_west.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				preView();
			};
		});
		
		
		//다운테이블과 리스너 연결
		table_up.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row=table_up.getSelectedRow();
				int col=0; //id를 구할꺼니까 0으로 고정
				// 내가 선택한 자료를 알려줌, 오브젝트형이니까 스트링으로 형변환하자
				String subcategory_id=(String)table_up.getValueAt(row, col);
				//System.out.println("니가 선택한 모델은 "+subcategory_id);
				
				//구해진 id를 아래의 모델에 적용하자.
				downModel.getList(Integer.parseInt(subcategory_id));
				table_down.updateUI();
				
				/*  jtable에서 getColumnCount는 table이 태어날때 호출된다. 
				db에서 원래 테이블은 컬럼은 고정적이다.
				컬럼이 고정되지 않으면 getValueAt이 작동하지 않는다.  
				그래서 컬럼 고정안시키고 getList()하면 updateUI 작동하지 않는다.  */
				//downModel.fireTableStructureChanged();
			}
		});
		
		table_down.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//굳이 db연동할 필요없음 
				int row=table_down.getSelectedRow();
				
				//이차원벡터에 들어 잇는 벡터를 얻어오자. 이 벡터, 이 한줄이 바로 레코드 한건
				downModel.data.get(row);
				Vector vec=downModel.data.get(row);
				getDetail(vec);
			}
		});
		
		setSize(850, 700);
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		init();
		getTop();
		getUpList();//위쪽 테이블처리
		getDownList();//아래쪽 테이블 처리
	}
	
	//데이터베이스 커넥션 얻기
	public void init(){
		manager=manager.getInstance();
		con=manager.getConnection();
		
		//이 시점에서 제대로 접속된건가 확인하려면 sysout에서 null이 아니면된다.  
		//System.out.println(con);
	}
	
	//최상위 카테고리 얻기
	public void getTop(){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="select * from topcategory order by topcategory_id asc";
		
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			while(rs.next()){
				TopCategory dto = new TopCategory();// 인스턴스 한개 생성
				
				dto.setTopcategory_id(rs.getInt("topcategory_id"));
				dto.setTop_name(rs.getString("top_name"));
				
				topList.add(dto); //리스트에 탑재
				ch_top.add(dto.getTop_name()); //초이스 컴포넌트에 탑재
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
	
	//위쪽 테이블 데이터 처리
	public void getUpList(){
		table_up.setModel(upModel=new UpModel(con));
		table_up.updateUI();
	}
	
	public void getDownList(){
		table_down.setModel(downModel=new DownModel(con));
		table_down.updateUI(); //하지만 지금 뭐 변경할 ui가 없을 거임
	}
	
	//하위 카테고리 구하기 : 바인드 변수 이용 
	public void getSub(){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		String sql="select * from subcategory where topcategory_id=?";
		
		try {
			pstmt=con.prepareStatement(sql);
			int index=ch_top.getSelectedIndex(); //유저가 선택한 탑카테고리
			
			if(index-1>=0){ //ch_top.add("▼ 상위 카테고리 선택"); 때문에 조건문 넣은
				TopCategory dto=topList.get(index-1);
				pstmt.setInt(1, dto.getTopcategory_id()); //첫번째 발견된 바인드 변수의 아이디를 구해라
				//첫번째 발견된 물음표에 유저가 선택한 카테고리의 i값이 들어가라, 여기까지가 쿼리문 준비
				
				rs=pstmt.executeQuery();
				
				//담기 전에 지우기
				subList.removeAll(subList); //메모리 지우기
				ch_sub.removeAll();//디자인 지우기
				
				//하위카테고리 지우기
				while(rs.next()){
					SubCategory vo = new SubCategory();
					
					vo.setSubcategory_id(rs.getInt("subcategory_id"));
					vo.setTopcategory_id(rs.getInt("topcategory_id"));
					vo.setSub_name(rs.getString("sub_name"));
					
					subList.add(vo);
					ch_sub.add(vo.getSub_name());
				}
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
	public void itemStateChanged(ItemEvent e) {
		getSub();
	}

	// 상품등록
	public void regist(){
		PreparedStatement pstmt=null;
		Result rs=null;
		
		String sql="insert into product(product_id, subcategory_id, product_name, price, img)";
		sql+=" values(seq_product.nextval,?,?,?,?)";
		
		//System.out.println(sql);
		
		try {
			pstmt=con.prepareStatement(sql);
			
			/*바인드 변수에 들어갈 값 결정
			pstmt.setInt(1, 서브id);
			pstmt.setString(2, 상품명);
			pstmt.setInt(3, 가격);
			pstmt.setString(4, 이미지명);*/
			
			//ArrayList(subList)에 있는 SubCategory dto를 추출하여 pk값(getSubcategory_id)을 넣어주자
			int index= ch_sub.getSelectedIndex(); //내가 화면에서 선택한 인덱스
			SubCategory vo=subList.get(index); //그 인덱스에 해당하는 제품하나에 대한 vo를 받아옴	
		
			pstmt.setInt(1, vo.getSubcategory_id());
			pstmt.setString(2, t_name.getText());
			pstmt.setInt(3, Integer.parseInt(t_price.getText()));
			pstmt.setString(4, file.getName());
			
			//executeUpdate 메서드는 쿼리문 수행 후 반영된 레코드의 갯수를 반환해 준다
			//따라서 insert문 경우 언제나 성공했다면 1, update 1건 이상, delete 1건 이상
			//결론) insert시 반환값이 0이라면 insert 실패!!
			int result=pstmt.executeUpdate();
			
			if(result!=0){
				JOptionPane.showMessageDialog(this, "등록성공");
				upModel.getList();//이차원벡터등이 변경된 값을 DB로 부터 얻어옴
				table_up.updateUI();//테이블갱신도하고
				copy();//이미지복사도 할꺼야
			} else {
				JOptionPane.showMessageDialog(this, "등록실패");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} //finally
	}
	
	//캔버스에 이미지 반영하기
	public void preView(){
		int result=chooser.showOpenDialog(this);
		if(result==JFileChooser.APPROVE_OPTION){
			//캔버스에 이미지 그리자!!
			file=chooser.getSelectedFile();
			
			//얻어진 파일을 기존의 이미지로 대체하여 다시 그리기
			try {
				image=ImageIO.read(file);
				can_west.repaint();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	//복사 메서드 정의
	public void copy(){
		FileInputStream fis=null;
		FileOutputStream fos=null;
		
		try {
			fis=new FileInputStream(file);
			fos=new FileOutputStream("C:/java_workspace/BreadProject/data/"+file.getName());
			
			byte[] b=new byte[1024];
			
			int flag; //-1인지 여부 판단
			while(true){
				flag=fis.read(b); //실제 데이터는 b에 들어 있음
				if(flag==-1)break;
				fos.write(b);
			}
			JOptionPane.showMessageDialog(this, "복사완료");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		}//finally
		
	}
	
	//선택한 제품의 상세정보 보여주기
	public void getDetail(Vector vec){
		t_id.setText(vec.get(0).toString());
		t_name2.setText(vec.get(2).toString());
		t_price2.setText(vec.get(3).toString());
		
		try {
			image=ImageIO.read(new File("C:/java_workspace/BreadProject/data/"+vec.get(4)));
			can_east.repaint();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		regist();
	}

	public static void main(String[] args) {
		new MainWindow();
	}

}
