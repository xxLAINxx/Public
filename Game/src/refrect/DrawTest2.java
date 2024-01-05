package refrect;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DrawTest2 extends JPanel implements MouseListener, MouseMotionListener {
	Image offImage;		//バックグラウンド描写用（未使用）
	static JFrame frame;
	double moveX;	//発射角　マウスのｘ軸移動量で増減
	double moveY;	//マウスのy軸移動で増減　moveXに加算
	double p_moveX;	//マウス移動量を求めるための前moveX値　previous_moveX
	double p_moveY;
	int posX = 200;	//始点座標・自機座標　マウスドラッグで増減
	int posY = 200;
	int p_posX = 200; //移動量計算用の前座標
	int p_posY = 200;
	double outer_startX;	//発射角と円との交点
	double outer_startY;
	double cross_lX;	//発射角に対しての原点からの垂線の交点座標
	double cross_lY;
	double cross_rX;	//反射角に対しての原点からの垂線の交点座標
	double cross_rY;
	double outer_endX;	//反射線の円の交点座標
	double outer_endY;
	
	int offset = 200;

	boolean mouseClick;	//マウスドラッグ処理用
	int speed = 50;		//マウス移動量を角度に変換する際の減算割合
	
	Point[] sample = {new Point(100,0),new Point(0,100),new Point(100,100)};
	

	public DrawTest2() {
		frame = new JFrame();

		frame.setTitle("お絵描きテスト2");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setPreferredSize(new Dimension(400, 400));
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		addMouseMotionListener(this);
		addMouseListener(this);
		frame.setVisible(true);
		offImage = createImage(400, 400);
	}

	public static void main(String[] args) {
		DrawTest2 dt = new DrawTest2();
		frame.add(dt);
	}

	public void paint(Graphics g) { // paint() method
		g.clearRect(0, 0, 400, 400);
		g.drawOval(50, 50, 300, 300);
		g.setColor(Color.RED);
		g.drawLine(posX, posY, (int) (posX + 400 * Math.cos(moveX)), (int) (posY + 400 * Math.sin(moveX)));
		calcCross(posX,posY,moveX);
		g.fillOval((int)outer_startX+offset-2, (int)outer_startY+offset-2, 4, 4);
		g.fillOval((int)cross_lX+offset-2, (int)cross_lY+offset-2, 4, 4);
		g.fillOval((int)cross_rX+offset-2, (int)cross_rY+offset-2, 4, 4);
		g.fillOval((int)outer_endX+offset-2, (int)outer_endY+offset-2, 4, 4);
		for(int i = 0;i < sample.length;i++) {
			Point line_end = calcDist(sample[i]);
			g.drawLine(sample[i].x+offset, sample[i].y+offset, line_end.x+offset, line_end.y+offset);
		}

		g.setColor(Color.GREEN);
		g.drawLine(offset,offset,(int)outer_startX + offset,(int)outer_startY + offset);
		g.setColor(Color.BLUE);
		g.drawLine(offset + (int)outer_startX, offset + (int)outer_startY, offset + (int)outer_endX, offset + (int)outer_endY);
//		System.out.printf("cX=%d cY=%d%n",outer_startX,outer_startY);
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		posX += e.getX() - p_posX;
		posY += e.getY() - p_posY;
		if (posX < 50)
			posX = 50;
		if (posX > 350)
			posX = 350;
		if (posY < 50)
			posY = 50;
		if (posY > 350)
			posY = 350;
		p_posX = e.getX();
		p_posY = e.getY();
//		System.out.printf("DG:pX=%d pY=%d X=%d Y=%d%n", p_posX, p_posY, posX, posY);
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(mouseClick) {					//マウスドラッグ中の発射角のキープ
			p_moveX = ((double) e.getX() / speed) - p_moveX;
			p_moveY = ((double) e.getY() / speed) - p_moveY;
		}else {
		moveX += ((double) e.getX() / speed) - p_moveX;
		moveY += ((double) e.getY() / speed) - p_moveY;
		moveX += ((double) e.getY() / speed) - p_moveY;
		p_moveX = ((double) e.getX() / speed);
		p_moveY = ((double) e.getY() / speed);
		}
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseClick = true;
		p_posX = e.getX();
		p_posY = e.getY();
//		System.out.printf("MC:pX=%d pY=%d X=%d Y=%d%n", p_posX, p_posY, posX, posY);

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseClick = false;
		p_moveX = ((double) e.getX() / speed);
		p_moveY = ((double) e.getY() / speed);

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
	
	void calcCross(int x,int y,double rad) {
		double seg;
		double dist;
		double arcLength;

		if (Math.pow(x-offset,2) + Math.pow(y-offset,2) < Math.pow(150,2)) {
			seg = (y-offset) - (x-offset) * Math.tan(rad);
			dist = -seg * Math.cos(rad);
			cross_lX = dist * Math.sin(rad);
			cross_lY = -dist * Math.cos(rad);
			arcLength =Math.sqrt(Math.pow(150,2) - Math.pow(dist,2));
			outer_startX =cross_lX + arcLength * Math.cos(rad);
			outer_startY =cross_lY + arcLength * Math.sin(rad);
			calcRefrect(cross_lX, cross_lY, dist, arcLength);
		}
		
	}
	
	void calcRefrect(double x,double y,double dist,double length) {
		double rad = Math.atan(length/dist);
		cross_rX = x * Math.cos(2*rad) - y * Math.sin(2*rad);
		cross_rY = x * Math.sin(2*rad) + y * Math.cos(2*rad);
		outer_endX = 2 * cross_rX - outer_startX;
		outer_endY = 2 * cross_rY - outer_startY;

	}
	Point calcDist(Point po) {
		double rad = Math.atan(cross_rY/cross_rX);	//(反射線分の原点からの垂線)の角度
		rad = cross_rX < 0 ? rad + Math.PI : rad;
		Point dist_vector = new Point();
		
		dist_vector.x = (int)(cross_rX + Math.sin(rad)*Math.sin(rad)*po.x
										- Math.cos(rad)*Math.sin(rad)*po.y
							);
		dist_vector.y = (int)(cross_rY - Math.cos(rad)*Math.sin(rad)*po.x
										+ Math.cos(rad)*Math.cos(rad)*po.y
							);

		return dist_vector;
	}

}
