package project;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GolfGame extends JFrame {
	
	Runnable guideline, move1, move2, power_gauge, collision, reset;
	ArrayList<Stage_Info> stage_info = new ArrayList<Stage_Info>();
	
	int x = 210, y = 600, ball_x = 30, ball_y = 580, scene_x = 0;
	int scene_number = 0, bsign = 0, power = 0, try_count = 0;;
	int press_check = 0, check = 0, reset_check = 0, goal_check = 0, bounce_check = 0;;
	int collision_x = 0, collision_y = 0, background2_x = 0, background2_y = 0;

	
	float vx, vy, a, b, c, speed_x, speed_y;	//코사인x, 사인y, 밑변, 높이, 빗변, x,y 초기속도
	float [] stop_check = new float[3];	
	
	//---------------------------------------------------------
	public GolfGame() {
		
		//스테이지 충돌판정 정보 읽어오기
		try {
			
			FileReader fr = new FileReader("C:\\Users\\a\\Desktop\\stage_info.txt");
			BufferedReader br = new BufferedReader(fr);
			
			String st = br.readLine();	//텍스트파일 한줄 읽어오기
			StringTokenizer tk = new StringTokenizer(st);	//읽어온 줄 분리
			
			while(st != null) {
				
				//----------------------------------------------------------------------------------
				//스테이지 정보 저장
				while(tk.hasMoreTokens())
					stage_info.add(new Stage_Info(
							Integer.parseInt(tk.nextToken()), Integer.parseInt(tk.nextToken()),
							Integer.parseInt(tk.nextToken()), Integer.parseInt(tk.nextToken()),
							Integer.parseInt(tk.nextToken()), Integer.parseInt(tk.nextToken()),
							Integer.parseInt(tk.nextToken()), Integer.parseInt(tk.nextToken())));
				//----------------------------------------------------------------------------------
				
				st = br.readLine();
				tk = new StringTokenizer(st);
				
			}	//end while
			br.close();
		}	catch(Exception e) {}
		//------------------------------------------------------------------------------------------
		setResizable(false);
		add(new GameBoard());
					
		//프레임 설정
		setSize(1200, 800);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	//---------------------------------------------------------
	//스테이지 정보
		class Stage_Info {
			
			int hall_right_x1, hall_right_x2, hall_left_x1, hall_left_x2, hall_y,
			goal_x1, goal_x2, goal_y;
			
			public Stage_Info(int hall_right_x1, int hall_right_x2,int hall_left_x1,
					int hall_left_x2, int hall_y, int goal_x1, int goal_x2, int goal_y) {
				
				this.hall_right_x1 = hall_right_x1;
				this.hall_right_x2 = hall_right_x2;
				this.hall_left_x1 = hall_left_x1;
				this.hall_left_x2 = hall_left_x2;
				this.hall_y = hall_y;
				
				this.goal_x1 = goal_x1;
				this.goal_x2 = goal_x2;
				this.goal_y = goal_y;
			}
		}
		//-----------------------------------------------------
	class GameBoard extends JPanel {

		//화면그리기
		public void paintComponent(Graphics g) {
			
			requestFocus();
			setFocusable(true);
	
			addKeyListener(new Key_Manager());
			addMouseListener(new Mouse_Manager());
			
			super.paintComponent(g);
			new Scene_Manager().draw(g);
			
		}	//end paintComponent
	}	//end GameBoard
	//---------------------------------------------------------
	class Scene_Manager {
		
		//배경이미지 불러오기--------------------------------------------------------------------------
		ImageIcon background = new ImageIcon("C:\\Users\\a\\Desktop\\background.png");
		ImageIcon background2 = new ImageIcon("C:\\Users\\a\\Desktop\\goal.png");
		
		Image bg = background.getImage();
		Image bg2 = background2.getImage();
		//---------------------------------------------------------------------------------------
		
		//리셋버튼 이미지 불러오기----------------------------------------------------------------------
		ImageIcon reset_button = new ImageIcon("C:\\Users\\a\\Desktop\\button.png");
		ImageIcon reset_button2 = new ImageIcon("C:\\Users\\a\\Desktop\\button_pressed.png");
		
		Image rb = reset_button.getImage();
		Image rb2 = reset_button2.getImage();
		//---------------------------------------------------------------------------------------
		
		public void draw(Graphics g) {
			
			new Task_Manager().Guideline();
			Font f;
			
			//홀 근처 바닥 그리기--------------------------------------------
			switch(scene_number) {
			case 1:
				background2_x = 957;	background2_y = 599;	break;
				
			case 2:
				background2_x = 960;	background2_y = 339;	break;
				
			case 3:
				background2_x = 654;	background2_y = 195;	break;
				
			}	//end switch-------------------------------------------
			
			//스테이지별 화면 그리기
			switch(scene_number) {
			case 0:	//시작화면
				g.drawImage(bg, scene_x, 0, null);
				break;	//end case 0
				
			case 4:	//종료화면
				g.drawImage(bg, -1200, 0, null);
				
				g.setColor(Color.black);
				f = new Font("Dialog", Font.PLAIN, 80);
				g.setFont(f);
				
				g.drawString("Game Clear!", 350, 100);
				
				f = new Font("Dialog", Font.PLAIN, 50);
				g.setFont(f);
				
				g.drawString("Total Try: " + try_count, 450, 150);
				g.drawString("Press Enter to Retry", 350, 200);
				
				break;	//end case 4
				
			case 1:	//1스테이지
			case 2:	//2스테이지
			case 3:	//3스테이지
				
				//배경 그리기
				scene_x = scene_number * -1200;
				g.drawImage(bg, scene_x, 0, null);
				
				//리셋버튼 그리기--------------------------------------------------------
				switch(reset_check) {
				case 1:
					
					g.drawImage(rb, 0, 0, null);
					repaint();
					break;	//end case 1
				case 2:
					
					g.drawImage(rb2, 0, 0, null);
					repaint();
					break;	//end case 2
				}	//end reset_check switch-----------------------------------------
				
				g.drawLine(50, 600, x, y);	//가이드라인	
				
				//공 그리기------------------------------------------------------------
				g.setColor(Color.white);
				g.fillOval(ball_x, ball_y, 20, 20);
				g.setColor(Color.black);
				//-------------------------------------------------------------------
				
				//파워게이지------------------------------------------------------------
				g.setColor(Color.black);
				g.drawRect(20, 460, 40, 10);
				g.setColor(Color.red);
				g.fillRect(20, 460, power * 1, 10);
				//-------------------------------------------------------------------
				
				g.drawImage(bg2, background2_x, background2_y, null);	//배경2
				
				
				//실행조건에 제한이 없으면 계속 실행 = sleep안함
				switch(check) {
				case 0:
					new Thread(guideline).start();
					check = 1;
					break;
				}	//end check switch
				
				//다음스테이지 이동---------------------------------------------
				switch(goal_check) {
				case 1:
					g.setColor(Color.black);
					f = new Font("Dialog", Font.PLAIN, 80);
					g.setFont(f);
					g.drawString("Hall In One!", 375, 100);
					f = new Font("Dialog", Font.PLAIN, 50);
					g.setFont(f);
					g.drawString("Press Enter To Next Stage", 275, 175);
					break;
				}//end goal_check switch----------------------------------
				
				break;	//end case 1 ~ 3
				
			}	//end scene_number switch
		}	//end draw
	}	//end Scene_Manager
	//---------------------------------------------------------
	class Task_Manager {
		
		public void Guideline() {
			guideline = () -> {
				int g_check = 0;
				
				while(press_check != 1) {
					try {
						switch(g_check) {
						case 0:
							x -= 3;
							y -= 3;
							
							if(x >= 120)
								x -= 2;
							
							repaint();
							break;	//end case 0
							
						case 1:
							x += 3;
							y += 3;
							
							if(x >= 120)
								x += 2;
							
							repaint();
							break;	//end case 1
						}	//end switch
						
						if(x <= 60)
							g_check = 1;
						
						if(x >= 210)
							g_check = 0;
						
						Thread.sleep(15);
					}	catch(InterruptedException e) {}
				}	//end while
			};
		}
		//------------------------------------------------------
		public void Power_gauge() {
			power_gauge = () -> {
				int p_check = 0;
				
				while(press_check == 1) {
					try {
						
						if(p_check == 0)
							power += 1;
						
						if(p_check == 1)
							power -= 1;
						
						repaint();
						
						if(power >= 40)
							p_check = 1;
						
						else if(power <= 0)
							p_check = 0;
						
						Thread.sleep(10);
					}	catch(InterruptedException err) {}
				}	//end while
			};
		}
		//------------------------------------------------------
		public void Move1() {
			
			new Task_Manager().Move2();
			new Task_Manager().Collision();
			
			move1 = () -> {
				System.out.println("move1 start");
				while(true) {
					
					if(bsign == 1)
						break;
					
					try {
						
						new Thread(collision).join();	//충돌판정 대기
						
						ball_x += speed_x;
						ball_y -= speed_y;
						repaint();
						
						speed_y -= 0.98;
						
						new Thread(collision).join();	//충돌판정 대기

						if(collision_y == 1) {
							
							speed_y = Math.abs((float)speed_y) / 2;
							collision_y = 0;
							break;
						}
						
						Thread.sleep(10);
						
					}	catch(InterruptedException e) {}
				}	//end while
				
				System.out.println("move1 end");
				new Thread(move2).start();
			};
		}
		//------------------------------------------------------
		public void Move2() {
			
			new Task_Manager().Collision();
			new Task_Manager().Reset();
			
			move2 = () -> {
				
				int [] m2_check = new int[3];
				int count = 0;
				
				System.out.println("move2 start");
				
				while(true) {
					
					if(bsign == 1)
						break;
					
					try {
						
						new Thread(collision).join();	//충돌판정 대기
						
						ball_x += speed_x;
						ball_y -= speed_y;
						repaint();

						speed_y -= 0.98;
						speed_x *= 0.98;	//감쇠계수
						
						//정지판정
						switch(count) {
						
						case 0:
							
							m2_check[0] = ball_x;
							//---------------------------------------------------------------
							if((m2_check[0] == m2_check[1] && m2_check[0] == m2_check[2]) ||
									Math.abs(speed_x) < 0.3)
								bsign = 1;
							//---------------------------------------------------------------
							count ++;
							break;	//end case 0
							
						case 1:
							
							m2_check[1] = ball_x;
							//---------------------------------------------------------------
							if((m2_check[0] == m2_check[1] && m2_check[1] == m2_check[2]) ||
									Math.abs(speed_x) < 0.3)
								bsign = 1;
							//---------------------------------------------------------------
							count ++;
							break;	//end case 1
							
						case 2:
							
							m2_check[2] = ball_x;
							//---------------------------------------------------------------
							if((m2_check[0] == m2_check[2] && m2_check[1] == m2_check[2]) ||
									Math.abs(speed_x) < 0.3)
								bsign = 1;
							//---------------------------------------------------------------
							count = 0;
							break;	//end case 2
						}	//end 정지판정 switch */
						
						
						if(collision_y == 1) {	//비탄성 충돌
							switch(bounce_check) {
							case 0:
								
								speed_y = Math.abs((float)speed_y) / 2;
								collision_y = 0;
								bounce_check ++;
								break;	//end case 0
								
							case 1:
								
								speed_y = speed_y / 2;
								collision_y = 0;	
								break;	//end case 1
							}
						}	//end if
						
						Thread.sleep(10);
						
					}	catch(InterruptedException e) {}
				}	//end while
				
				System.out.println("move2 end");
				new Thread(reset).start();
			};
		}
		//---------------------------------------------------------------
		public void Collision() {
			collision = () -> {
				
				System.out.println("collision start");
				
				while(true) {
					
					try {
						
						new Thread(move1).join();
						new Thread(move2).join();
						
						//중복되는 충돌판정들
						switch(scene_number) {
						case 1:
						case 2:
						case 3:
							
							//장외 판정
							if(ball_x < -30 || ball_x > 1220)
								bsign = 1;
							
							//홀 오른쪽 벽
							if((ball_x >= stage_info.get(scene_number).hall_right_x1 &&
									ball_x <= stage_info.get(scene_number).hall_right_x2) && 
									ball_y >= stage_info.get(scene_number).hall_y) {
								
								switch(collision_x) {
								case 0:
									
									speed_x = Math.abs(speed_x) / 2;
									speed_x = -speed_x;
									collision_x = 1;
									break;
								}	//end switch
							}	//end if
							
							//홀 왼쪽벽
							if(collision_x == 1 &&
									((ball_x >= stage_info.get(scene_number).hall_left_x1 && 
									ball_x <= stage_info.get(scene_number).hall_left_x2) &&
											ball_y >= stage_info.get(scene_number).hall_y)) {
								
								switch(collision_x) {
								case 1:
									
									speed_x = Math.abs(speed_x) / 2;
									collision_x = 0;
									break;
								}	//end switch
							}	//end if
							
							//goal 판정
							if((ball_x >= stage_info.get(scene_number).goal_x1 &&
									ball_x <= stage_info.get(scene_number).goal_x2) &&
									(ball_y >= stage_info.get(scene_number).goal_y)) {
								
								bsign = 1;
								goal_check = 1;
							}	//end if
							
						}	//end 중복판정 switch
						
						//개별적인 충돌판정
						switch(scene_number) {

						case 1:	//스테이지1 충돌판정-----------------------------------------------
							
							//땅
							if(!((ball_x >= 960 && ball_x <= 1010))&& ball_y >= 581) {
								
								ball_y = 580;
								collision_y = 1;
							}	//end if
							break;	//end case 1----------------------------------------------
							
						case 2:	//스테이지2 충돌판정-----------------------------------------------
							
							//1층 땅
							if(ball_y >= 581) {
								
								ball_y = 580;
								collision_y = 1;
							}	//end if
							
							//2층 땅
							if(!(ball_x >= 976 && ball_x <= 1021) && (ball_x >= 693)
									&& (ball_y >= 321 && ball_y <= 350)) {
								
								ball_y = 320;
								collision_y = 1;
							}	//end if
							
							//1층 오른쪽 벽
							if((ball_x >= 673 && ball_x <= 700) &&(ball_y >= 320 && ball_y <= 620)) {
								switch(collision_x) {
								case 0:
									
									speed_x = Math.abs(speed_x) / 2;
									speed_x = -speed_x;
									collision_x = 1;
									break;
								}	//end switch
							}	//end if
							
							//벽 뚫는 경우 방지
							if((ball_x >= 701 && ball_x <= 745) && (ball_y >= 350 && ball_y <= 620)) {
								switch(collision_x) {
								case 0:
									
									speed_x = Math.abs(speed_x) / 2;
									speed_x = -speed_x;
									collision_x = 1;
									break;
								}	//end switch
							}	//end if
							
							break;	//end case 2-----------------------------------------------
							
						case 3:	//스테이지3 충돌판정------------------------------------------------
							
							//1층 땅
							if(ball_y >= 581) {
								
								ball_y = 580;
								collision_y = 1;
							}	//end if
							
							//1층 오른쪽 벽
							if((ball_x >= 400 && ball_x <= 430) &&(ball_y >= 419 && ball_y <= 581)) {
								switch(collision_x) {
								case 0:

									speed_x = Math.abs(speed_x) / 2;
									speed_x = -speed_x;
									collision_x = 1;
									break;
								}
							}
							
							//2층 땅
							if((ball_x >=430 && ball_x <= 958) && ball_y >= 419) {
								
								ball_y = 419;
								collision_y = 1;
							}	//end if
							
							//2층 오른쪽 벽
							if((ball_x >= 487 && ball_x <= 517) &&(ball_y >= 296 && ball_y <= 448)) {
								switch(collision_x) {
								case 0:

									speed_x = Math.abs(speed_x) / 2;
									speed_x = -speed_x;
									collision_x = 1;
									break;
								}
							}
							
							//3층 땅
							if(!(ball_x >=670 && ball_x <= 715) && 
									(ball_x >=517 && ball_x <= 880) && ball_y >= 296) {
								
								ball_y = 296;
								collision_y = 1;
							}	//end if
							
							//3층 오른쪽 벽
							if((ball_x >= 554 && ball_x <= 584) &&(ball_y >= 177 && ball_y <= 315)) {
								switch(collision_x) {
								case 0:
									
									speed_x = Math.abs(speed_x) / 2;
									speed_x = -speed_x;
									collision_x = 1;
									break;
								}
							}
							
							//4층 땅
							if(!(ball_x >=670 && ball_x <= 715) && (ball_x >= 584 && ball_x <= 811)
									&& (ball_y >= 177 && ball_y <= 196)) {
								
								ball_y = 177;
								collision_y = 1;
							}	//end if
							
							break;	//end case 3
						}	//end switch
						
						
						//정지신호감지
						if(bsign == 1)
							break;
						
					}	catch(Exception e) {}
				}	//end while
				System.out.println("collision end");
			};
		}
		//-----------------------------------------------------
		public void Reset() {
			reset = () -> {
				System.out.println("리셋시작");
				
				while(bsign != 0 && goal_check != 1) {
					try {
						PointerInfo pt = MouseInfo.getPointerInfo();
						
						if((pt.getLocation().x >= 1075 && pt.getLocation().x <= 1155)
								&& (pt.getLocation().y >= 65 && pt.getLocation().y <= 130)) {
							reset_check = 2;
							repaint();
						}	else {
							reset_check = 1;
							repaint();
							}
							
						Thread.sleep(10);
						
					}	catch(Exception e) { }
				}
				System.out.println("리셋 종료");
			};
		}
		//-----------------------------------------------------

		
	}	//end Task_Manager
	//---------------------------------------------------------
	class Key_Manager implements KeyListener {
		public void keyTyped(KeyEvent e) {}
		
		public void keyPressed(KeyEvent e) {
			
			new Task_Manager().Power_gauge();
			
			switch(scene_number) {
			case 0:	//시작화면
				
				scene_number ++;
				repaint();
				break;	//end case 0
				
			case 4:	//종료화면
				press_check = 1;	// case3에서 바로 넘어가는거 방지
				break;	//end case 4
				
			case 1:
			case 2:
			case 3:
				
				int keycode = e.getKeyCode();
				
				if(keycode == KeyEvent.VK_SPACE && press_check == 0) {
					power = 0;	bsign = 0;	press_check = 1;
					new Thread(power_gauge).start();
				}
				
				break;	//end case 1 ~ 3

			}	//end switch
		}	//end keyPressed

		public void keyReleased(KeyEvent e) {
			
			new Task_Manager().Move1();
			new Task_Manager().Collision();
			
			int keycode = e.getKeyCode();
			
			switch(scene_number) {
			case 1:
			case 2:
			case 3:
				
				if(keycode == KeyEvent.VK_SPACE && press_check == 1) {
					
					a = x - 50;	//밑변
					b = 600 - y;	//높이
					c = (float)Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));	//빗변
					
					vx = a/c;	//코사인x
					vy = b/c;	//사인y
					
					speed_x = vx * power;	//x축 초기속도 = cos_x * power
					speed_y = vy * power;	//y축 초기속도 = sin_y * power
					
					press_check = 2;
					new Thread(collision).start();
					new Thread(move1).start();
					
				}	//end if
				
				//스테이지 넘어가기
				if(goal_check == 1 && keycode == KeyEvent.VK_ENTER) {
					
					power = 0;	scene_number ++;	try_count ++;
					ball_x = 30;	ball_y = 580;	collision_x = 0;	collision_y = 0;
					check = 0;	press_check = 0;	goal_check = 0;
					
					repaint();
				}
				break;	//end case 3
			case 4:
				if(press_check == 1 && keycode == KeyEvent.VK_ENTER) {
					
					scene_number = 1;	try_count = 0;
					press_check = 0;	bounce_check = 0;
					repaint();
				}
				
				break;	//end case 4:
				
			}	//end scene_number switch
			
			
		}	//end KeyReleased
	}	//end GUI_Manager
	//---------------------------------------------------------
	class Mouse_Manager implements MouseListener {

		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}

		public void mouseReleased(MouseEvent e) {
			
			switch(reset_check) {	//리셋버튼 누르면 초기화
			case 2:
				bsign = 0;		power = 0;	try_count ++;
				ball_x = 30;	ball_y = 580;	collision_x = 0;	collision_y = 0;
				check = 0;	press_check = 0;	reset_check = 0;	bounce_check = 0;
				break;
			}	//end reset_check switch
		}	//end mouseReleased
	}	//end Mouse_Manager
	//---------------------------------------------------------
	public static void main(String[] args) {
		GolfGame game = new GolfGame();
	}
}