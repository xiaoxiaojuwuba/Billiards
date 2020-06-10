package study.learning;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;//���������õ���
import java.awt.Graphics;//��һ����������2Dͼ����뵼���java�����ṩ��ͼ��ͼ������أ���ɫ�Ļ��ơ�
import java.awt.Graphics2D;
import java.awt.Image;//�ṩ�������޸�ͼ��ĸ����ࡣ
import java.awt.Panel;//����һ���м���������Container�����࣬Applet�ĳ��࣬�������awt���
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;//��������£��ɿ������룬�뿪
import java.awt.event.MouseMotionListener;//�˶����뿪
import java.awt.event.WindowAdapter;//���մ����¼��ĳ����������ࡣ�����еķ���Ϊ�ա�������ڵ�Ŀ���Ƿ��㴴������������WindowAdapter�ǳ����࣬ʵ�������е�WindowListener������ֻ���������ڲ����ǿյġ�
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;//Image��һ�������࣬BufferedImage����ʵ���࣬��һ����������ͼ���࣬��Ҫ�����ǽ�һ��ͼƬ���ص��ڴ���

public class JavaBilliards extends Panel implements Runnable, MouseListener,//extends�̳��࣬implementsʵ�ֽӿ�
        MouseMotionListener {

	
    private static final long serialVersionUID = -5019885590391523441L;

    // ��
    private double _ball;

    // �����ñ���
    private Image _screen;

    // ����
    private Graphics _graphics;

    // ̨��
    private Image _table;
    private double xLeftBound;
    private double xRightBound;
    private double xInLeftBound;
    private double xInMiddle;
    private double xInRightBound;
    private double yTopBound;
    private double yBottomBound;
    private double yInTopBound;
    private double yInBottomBound;

    private int countBall;

    private int remainBallNum;

    private double xPosition[];//������¼һ����ֹʱ������λ��

    private double yPosition[];

    private double xSpeed[];

    private double ySpeed[];

    private double ballCenterX[];//������ʱ��¼һ���˶��е����λ��

    private double ballCenterY[];

    private double xSpeedAfterCrash[];

    private double ySpeedAfterCrash[];

    private boolean exist[];

    private double ballRound;

    private int state;

    private int beforeDraggedX;

    private int beforeDraggedY;

    private int nowMouseX;

    private int nowMouseY;

    private boolean mouseIsPressed;

    private int armLength;

    private int A;//�ò�����Ϊ���ø����߿���ȥ�������������ǰ�ʱ�������ı���
	
    /**
     * ��ʼ��
     * 
     */
    public JavaBilliards() {//���췽��
        state = 0;//��Ϸ״̬��0�����ʼ��1�������������ֹ��2�����������˶���3�����������Ѵ���
        ballRound = 15d;//��İ뾶
        armLength = 300;//��˳������
        A = 0;
        //setBounds(50, 50, 700, 350);
        Frame frame = new Frame("ϲ��̨��");
        frame.add(this);
        frame.setBounds(0, 0, 1400, 760);//ǰ�����Ǵ������꣬���������ڴ�С
        frame.setResizable(false);//��Ļ�Ƿ������
        frame.setVisible(true);//���ڿɼ�
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });//�����������������ڱ��ر�ʱ�˳�����
        requestFocus();//�����뽹����ڵ�����������Ŀؼ���
        initialize();
    }

    public void initialize() {
        // ��������
        base();
        // ע��ʵ��
        immit();
        // ���汳��
        _screen = new BufferedImage(this.getWidth(), this.getHeight(), 1);
        // ����ͼ��
        _graphics = _screen.getGraphics();
        // ����̨��
        makeTable();
        // ���ü���
        addMouseListener(this);//����initialize����ʱһ����һ������this�ʹ���������󣬴˴�����JavaBilliards��
        addMouseMotionListener(this);
        // ����
        new Thread(this).start();//thread�ཨ���̶߳���start�Ǹö���ķ�������ʾ���߳̿�ʼִ��
    }

    /**
     * ��ʼ������
     * 
     */
    public void base() {

        // ����
        _ball = 16D;//���İ뾶
        xLeftBound=40D;//������߽������X����
        xRightBound=getWidth()-40;//������߽��ұ���X����
        yTopBound=xLeftBound;//������߽��ϱ���y����
        yBottomBound=(double) getHeight() - xLeftBound;//������߽��±���y����
        xInLeftBound=xLeftBound + 20D;//�����ڱ߽������X����
        xInRightBound= xRightBound - 20D;//�����ڱ߽��ұ���X����
        xInMiddle=(xInLeftBound+ xInRightBound)/2;//�����ڱ߽�����x����
        yInTopBound=yTopBound + 20D;//�����ڱ߽��ϱ���y����
        yInBottomBound=yBottomBound - 20D ;//�����ڱ߽��±���y����
    }

    /**
     * ע��ʵ��
     * 
     */
    public void immit() {
        countBall = 16;//��ĸ���
        xPosition = new double[countBall];
        yPosition = new double[countBall];
        xSpeed = new double[countBall];
        ySpeed = new double[countBall];
        ballCenterX = new double[countBall];
        ballCenterY = new double[countBall];
        xSpeedAfterCrash = new double[countBall];
        ySpeedAfterCrash = new double[countBall];
        exist = new boolean[countBall];
        // ��ʼ���������
        hitObject();
        // ��ʼ���������
        hitBall();
    }

    //���ð����״̬��λ��
    public void hitBall() {
        xPosition[0] = (1.0D * (xInRightBound - xInLeftBound)) / 3D;//���X����
        yPosition[0] = this.getHeight() / 2;//���������
        xSpeed[0] = 0.0D;//��ĺ����ٶ�
        ySpeed[0] = 0.0D;//��������ٶ�
        exist[0] = true;//0����ĸ�򣬻����Ǹ����Ƿ��ڳ�
    }
 
    //��ʼ�����ñ������
    public void hitObject() {
        int il = 1;
        remainBallNum = countBall - 1;//��ʣ������
        // ��ƽ����
        double dl = Math.sqrt(4D);
        for (int j1 = 0; j1 < 5; j1++) {
            double d2 = ((double) getWidth() * 2D) / 3D + (double) j1 * dl * ballRound;//��ĺ�������ڿ�ȵ�2/3���������������˰뾶�˸���3.5
            double d3 = (double) (getHeight() / 2) - (double) j1 * ballRound*1.2;//�����������ڸ߶ȵ�1/2��ȥ�������������˰뾶
            for (int k1 = 0; k1 <= j1; k1++) {
                xPosition[il] = d2;
                yPosition[il] = d3;
                xSpeed[il] = 0.0D;
                ySpeed[il] = 0.0D;
                exist[il] = true;
                d3 += 2.2D * ballRound;//ͬһ�ŵ������ÿ���ĸ߶ȵ�����һ���ĸ߶ȼ�2���뾶
               
                il++;
            }

        }
        //exist[12]=true;

    }
    
    //������
    public void makeTable() {
        _table = new BufferedImage(this.getWidth(), this.getHeight(), 1);
        Graphics g = _table.getGraphics();
        g.setColor(Color.GRAY);//���ⱳ��ɫ
        g.fillRect(0, 0, getWidth(), getHeight());//Ϳɫ�����������Ͻ����꼰���
        g.setColor((new Color(200, 100, 250)).darker());//���������߿���ɫ
 
        g.fill3DRect((int) xLeftBound, (int) yTopBound, (int) (xRightBound - xLeftBound),
                (int) (yBottomBound - yTopBound),true);
        g.setColor(Color.GREEN.darker());//��������ɫ
        g.fill3DRect((int) xInLeftBound, (int) yInTopBound, (int) (xInRightBound - xInLeftBound),
                (int) (yInBottomBound - yInTopBound), false);
        g.setColor(Color.GREEN.darker());//ĸ������ɫ
        g.drawLine((int) ((1.0D * (xInRightBound - xInLeftBound)) / 3D), (int) yInTopBound,
                (int) ((1.0D * (xInRightBound - xInLeftBound)) / 3D), (int) yInBottomBound);
        g.fillOval((int) ((1.0D * (xInRightBound - xInLeftBound)) / 3D) - 2,
                (int) ((yInBottomBound + yInTopBound) / 2D) - 2, 4, 4);
        g.drawArc((int) ((1.0D * (xInRightBound - xInLeftBound)) / 3D) - 20,
                (int) ((yInBottomBound + yInTopBound) / 2D) - 20, 40, 40, 90, 180);
        g.setColor(Color.BLACK);
        double d1 = _ball - 2D;
        //���滭6�����
        g.fillOval((int) (xInLeftBound - d1), (int) (yInTopBound - d1),
                (int) (2D * d1), (int) (2D * d1));
        g.fillOval((int) (xInLeftBound - d1), (int) (yInBottomBound - d1),
                (int) (2D * d1), (int) (2D * d1));
        g.fillOval((int) (xInMiddle - d1), (int) (yInTopBound - d1),
                (int) (2D * d1), (int) (2D * d1));
        g.fillOval((int) (xInMiddle - d1), (int) (yInBottomBound - d1),
                (int) (2D * d1), (int) (2D * d1));
        g.fillOval((int) (xInRightBound - d1), (int) (yInTopBound - d1),
                (int) (2D * d1), (int) (2D * d1));
        g.fillOval((int) (xInRightBound - d1), (int) (yInBottomBound - d1),
                (int) (2D * d1), (int) (2D * d1));
       
    }

    /**
     * �̴߳���
     */
    public void run() {//��дrunable�ӿڵ�run�������������state
        long timeStart;
        timeStart = System.currentTimeMillis();//��ȡϵͳʱ��
        // ��ѭ����������
        for (;;) {
            long timeEnd = System.currentTimeMillis();
            switch (state) {
            default:
                break;

            case 1://���������ֹ
                // ����ʱ�任���˶��켣
                conversion(timeEnd - timeStart);
                // ���̴���
                course();
                break;

            case 2://�������˶�
                conversion(timeEnd - timeStart);
                // ���̴���
                course();
                boolean flag = true;
                for (int i1 = 0; flag && i1 < countBall; i1++)
                    flag = xSpeed[i1] == 0.0D && ySpeed[i1] == 0.0D;

                if (flag) {
                    state = 1;
                    // ����
                    if (!exist[0]) {
                        hitBall();//���ð���
                    }
                }
                if (remainBallNum == 0)
                    state = 3;
                break;

            case 3://��ȫ���������
                hitObject();
                hitBall();
                state = 0;
                break;
            }

            repaint();//ִ��paint����
            timeStart = timeEnd;
            try {
                Thread.sleep(10L);//�߳�����10ms
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

    }

    public void course() {
        // ��������
        limit();
        // �������
        pocket();
        // �˶�����
        play();
        for (int i1 = 0; i1 < countBall; i1++)
            if (exist[i1]) {
                xPosition[i1] = ballCenterX[i1];
                yPosition[i1] = ballCenterY[i1];//�����λ�ô洢��position��
            }
    }
    /**
     * �任ʱ��Ϊ��������
     * 
     * @param value
     */
    public void conversion(long value) {//�����˶�����ײ
        double d1 = (double) value / 1000D;
        for (int i1 = 0; i1 < countBall; i1++)
            if (exist[i1]) {
                ballCenterX[i1] = xPosition[i1] + xSpeed[i1] * d1;
                ballCenterY[i1] = yPosition[i1] + ySpeed[i1] * d1;
                xSpeed[i1] *= 0.99D;//�ٶȰ�ʱ��˥������
                ySpeed[i1] *= 0.99D;
                if (Math.abs(Math.hypot(xSpeed[i1], ySpeed[i1])) < 2D) {//�ٶ�С��2ʱǿ��ֹͣ
                    xSpeed[i1] = 0.0D;
                    ySpeed[i1] = 0.0D;
                }
            }

    }

    public void pocket() {//�ֱ�����6���������
        for (int i1 = 0; i1 < countBall; i1++)
            if (exist[i1]) {
            	if (Math.hypot(xInLeftBound - xPosition[i1], yInTopBound - yPosition[i1]) < _ball) {//���������ϴ�����С�ڴ��뾶
                    exist[i1] = false;
                    if (i1 != 0) {
                        remainBallNum--;
                    }
                    xSpeed[i1] = 0.0D;
                    ySpeed[i1] = 0.0D;
                }
            	if (Math.hypot(xInLeftBound - xPosition[i1], yInBottomBound - yPosition[i1]) < _ball) {
                    exist[i1] = false;
                    if (i1 != 0) {
                        remainBallNum--;
                    }
                    xSpeed[i1] = 0.0D;
                    ySpeed[i1] = 0.0D;
                }
            	if (Math.hypot(xInMiddle - xPosition[i1], yInTopBound - yPosition[i1]) < _ball) {
                    exist[i1] = false;
                    if (i1 != 0) {
                        remainBallNum--;
                    }
                    xSpeed[i1] = 0.0D;
                    ySpeed[i1] = 0.0D;
                }
            	if (Math.hypot(xInMiddle - xPosition[i1], yInBottomBound - yPosition[i1]) < _ball) {
                    exist[i1] = false;
                    if (i1 != 0) {
                        remainBallNum--;
                    }
                    xSpeed[i1] = 0.0D;
                    ySpeed[i1] = 0.0D;
                }
            	if (Math.hypot(xInRightBound - xPosition[i1], yInTopBound - yPosition[i1]) < _ball) {
                    exist[i1] = false;
                    if (i1 != 0) {
                        remainBallNum--;
                    }
                    xSpeed[i1] = 0.0D;
                    ySpeed[i1] = 0.0D;
                }
            	if (Math.hypot(xInRightBound - xPosition[i1], yInBottomBound - yPosition[i1]) < _ball) {
                    exist[i1] = false;
                    if (i1 != 0) {
                        remainBallNum--;
                    }
                    xSpeed[i1] = 0.0D;
                    ySpeed[i1] = 0.0D;
                }

            }

    }

    public void play() {
        for (int i1 = 0; i1 < countBall; i1++)
            if (exist[i1]) {
                for (int j1 = i1 + 1; j1 < countBall; j1++) {
                    boolean flag;
                    if (exist[j1] && (flag = isCrashed(i1, j1))) {
                        for (int k1 = 0; k1 < 10 && flag; k1++) {
                            ballCenterX[i1] = (ballCenterX[i1] + xPosition[i1]) / 2D;
                            ballCenterY[i1] = (ballCenterY[i1] + yPosition[i1]) / 2D;
                            ballCenterX[j1] = (ballCenterX[j1] + xPosition[j1]) / 2D;
                            ballCenterY[j1] = (ballCenterY[j1] + yPosition[j1]) / 2D;
                            flag = isCrashed(i1, j1);
                        }//������ײ�ˣ�����ʮ�������ϴ��˶����Ķ��ֵ㣬�鿴�Ƿ���ײ��ֱ��ĳ�β���Ϊֹ��

                        if (flag) {//��1024�ֵ���Ȼ��������Ϊ�ϴ��˶�����㴦���Ѿ����ˡ���ĳһ�㲻���ˣ�����Ϊ��һ�����ĵ㴦���ġ�
                            ballCenterX[i1] = xPosition[i1];
                            ballCenterY[i1] = yPosition[i1];
                            ballCenterX[j1] = xPosition[j1];
                            ballCenterY[j1] = yPosition[j1];
                        }
                        double d1 = ballCenterX[j1] - ballCenterX[i1];//������ײʱX�����
                        double d2 = ballCenterY[j1] - ballCenterY[i1];
                        double d3 = Math.hypot(ballCenterX[i1] - ballCenterX[j1], ballCenterY[i1] - ballCenterY[j1]);//������ײʱ����
                        double d4 = d1 / d3;//X�߱�б��
                        double d5 = d2 / d3;//Y��б
                        xSpeedAfterCrash[j1] = xSpeed[j1] - xSpeed[j1] * d4 * d4;
                        xSpeedAfterCrash[j1] -= ySpeed[j1] * d4 * d5;
                        xSpeedAfterCrash[j1] += xSpeed[i1] * d4 * d4;
                        xSpeedAfterCrash[j1] += ySpeed[i1] * d4 * d5;
                        ySpeedAfterCrash[j1] = ySpeed[j1] - ySpeed[j1] * d5 * d5;
                        ySpeedAfterCrash[j1] -= xSpeed[j1] * d4 * d5;
                        ySpeedAfterCrash[j1] += xSpeed[i1] * d4 * d5;
                        ySpeedAfterCrash[j1] += ySpeed[i1] * d5 * d5;
                        xSpeedAfterCrash[i1] = xSpeed[i1] - xSpeed[i1] * d4 * d4;
                        xSpeedAfterCrash[i1] -= ySpeed[i1] * d4 * d5;
                        xSpeedAfterCrash[i1] += xSpeed[j1] * d4 * d4;
                        xSpeedAfterCrash[i1] += ySpeed[j1] * d4 * d5;
                        ySpeedAfterCrash[i1] = ySpeed[i1] - ySpeed[i1] * d5 * d5;
                        ySpeedAfterCrash[i1] -= xSpeed[i1] * d4 * d5;
                        ySpeedAfterCrash[i1] += xSpeed[j1] * d4 * d5;
                        ySpeedAfterCrash[i1] += ySpeed[j1] * d5 * d5;
                        xSpeed[i1] = xSpeedAfterCrash[i1];
                        ySpeed[i1] = ySpeedAfterCrash[i1];
                        xSpeed[j1] = xSpeedAfterCrash[j1];
                        ySpeed[j1] = ySpeedAfterCrash[j1];
                    }
                }

            }

    }

    public boolean isCrashed(int i1, int j1) {
        // �鿴 �����Ƿ���ײ
        return Math.hypot(ballCenterX[i1] - ballCenterX[j1], ballCenterY[i1] - ballCenterY[j1]) < 2D * ballRound;
    }

    /**
     * ��������
     * 
     */
    public void limit() {
        for (int i = 0; i < countBall; i++)
            if (exist[i]) {
                if (ballCenterX[i] - ballRound < xInLeftBound) {
                    ballCenterX[i] = xInLeftBound + ballRound;
                    xSpeed[i] *= -1D;
                } else if (ballCenterX[i] + ballRound > xInRightBound) {
                    ballCenterX[i] = xInRightBound - ballRound;
                    xSpeed[i] *= -1D;
                }
                if (ballCenterY[i] - ballRound < yInTopBound) {
                    ballCenterY[i] = yInTopBound + ballRound;
                    ySpeed[i] *= -1D;
                } else if (ballCenterY[i] + ballRound > yInBottomBound) {
                    ballCenterY[i] = yInBottomBound - ballRound;
                    ySpeed[i] *= -1D;
                }
            }

    }//����ȫ�ٷ���

    public void makeScreen(Graphics screenGraphics) {

        screenGraphics.drawImage(_table, 0, 0, null);
        if (exist[0]) {
            _graphics.setColor(Color.WHITE);//�����ڳ���ĸ��ֻ�ǻ���
            _graphics.fillOval((int) (xPosition[0] - ballRound), (int) (yPosition[0] - ballRound),
                    (int) (ballRound * 2D), (int) (ballRound * 2D));
        }
        
        screenGraphics.setColor(Color.RED);//�����ڳ��ı������
        for (int i1 = 1; i1 < countBall; i1++)
            if (exist[i1])
            { screenGraphics.fillOval((int) (xPosition[i1] - ballRound), (int) (yPosition[i1] - ballRound),
                        (int) (ballRound * 2D), (int) (ballRound * 2D));
            screenGraphics.setColor(Color.BLACK);
            screenGraphics.setFont(new Font("����", Font.PLAIN, 16));
            screenGraphics.drawString(i1+"", (int)(xPosition[i1]-3),(int)(yPosition[i1])+3);
            screenGraphics.setColor(Color.RED);
            }
        
/*
        screenGraphics.setColor(Color.BLACK);//���������������
        for (int j1 = 0; j1 < countBall; j1++)
            if (exist[j1]) {
                screenGraphics.drawOval((int) (xPosition[j1] - ballRound), (int) (yPosition[j1] - ballRound),
                        (int) (ballRound * 2D), (int) (ballRound * 2D));
            }
*/
        if (state == 1){
            makeHelper(screenGraphics);
        }
        if (state == 0) {
            int k1 = getWidth() / 2 - 85;
            int l1 = getHeight() / 2;
            screenGraphics.setColor(Color.BLACK);
            screenGraphics.drawString("������濪ʼ", k1 + 2, l1 + 2);
           
                screenGraphics.setColor(Color.YELLOW);
     
            screenGraphics.drawString("������濪ʼ", k1, l1);
        }//���ﻹ���˸����ֱ任��ɫ��ʾ
    }

    /**
     * ������˼�������
     * 
     * @param screenGraphics
     */
    public void makeHelper(Graphics screenGraphics) {
        double d1 = Math.hypot(xPosition[0] - (double) beforeDraggedX, yPosition[0] - (double) beforeDraggedY);//ĸ������קǰλ�þ���
        double d2 = ((double) beforeDraggedX - xPosition[0]) / d1;//X��������б��
        double d3 = ((double) beforeDraggedY - yPosition[0]) / d1;
        double d4 = mouseIsPressed ? n() / 10D : 1.0D;
        double d5 = xPosition[0] + d2 * (ballRound + d4);
        double d6 = xPosition[0] + d2 * (ballRound + (double) armLength + d4);
        double d7 = yPosition[0] + d3 * (ballRound + d4);
        double d8 = yPosition[0] + d3 * (ballRound + (double) armLength + d4);
        screenGraphics.setColor(Color.ORANGE.darker());
        Graphics2D g2 = (Graphics2D)screenGraphics;
        g2.setStroke(new BasicStroke(6.0f));//��ϸ
        g2.drawLine((int) d5, (int) d7, (int) d6, (int) d8);//�����
        g2.setStroke(new BasicStroke(0.5f));
        int i1 = 0;
        int j1 = mouseIsPressed ? (int) (150D * (d4 / 1000D)) : 15;
        double d9=30D;//������������ľ���
        double d10 = d9 * d2;
        double d11 = d9 * d3;
        double d12 = xPosition[0] + (double) A * d2;//�����������ĺ�����
        double d13 = yPosition[0] + (double) A * d3;//������������������
        A++;
        A %= d9;//����ı�A��ֵ��ʵ�������ø����߿���ȥ����������ʱ��ı�ÿ���������
        screenGraphics.setColor(Color.WHITE);
        for (; i1 < j1; i1++) {
            if (d12 < xInLeftBound) {
                d12 = xInLeftBound - d12;
                d12 = xInLeftBound + d12;
                d10 *= -1D;
            } else if (d12 > xInRightBound) {
                d12 -= xInRightBound;
                d12 = xInRightBound - d12;
                d10 *= -1D;
            }
            if (d13 < yInTopBound) {
                d13 = yInTopBound - d13;
                d13 = yInTopBound + d13;
                d11 *= -1D;
            } else if (d13 > yInBottomBound) {
                d13 -= yInBottomBound;
                d13 = yInBottomBound - d13;
                d11 *= -1D;
            }
            screenGraphics.fillOval((int) d12 - 2, (int) d13 - 2, 4,4);//�������ߣ�������ÿ����ʵ�����Ǹ�СԲ
            d12 -= d10;
            d13 -= d11;
        }

    }

    public double n() {
        if (mouseIsPressed) {
            return Math.min(1000D, 10D * Math.hypot(xPosition[0] - (double) nowMouseX, yPosition[0]
                    - (double) nowMouseY));
        } else {
            return Math.min(1000D, 10D * Math.hypot(beforeDraggedX - nowMouseX, beforeDraggedY - nowMouseY));
        }
    }

    public void update(Graphics g) {
        paint(g);
    }

    public void paint(Graphics g) {
        makeScreen(_graphics);
        g.drawImage(_screen, 0, 0, null);
    }

    public void mousePressed(MouseEvent mouseevent) {//��ס���ɿ�
        mouseIsPressed = true;
    }

    public void mouseReleased(MouseEvent mouseevent) {//���������������ͷţ��������ק�ɿ�Ҳ�ᴥ������ͷ�
        if (state == 1) {
            double d1 = Math.hypot(xPosition[0] - (double) beforeDraggedX, yPosition[0] - (double) beforeDraggedY);//ĸ�����ľ���
            double d2 = (xPosition[0] - (double) beforeDraggedX) / d1;//ĸ�������λ�ú������
            double d3 = (yPosition[0] - (double) beforeDraggedY) / d1;
            double d4;
            if ((d4 = n()) > 0.0D) {
                state = 2;
                xSpeed[0] = d4 * d2*3;
                ySpeed[0] = d4 * d3*3;
            }
        }
        mouseIsPressed = false;
    }

    public void mouseClicked(MouseEvent mouseevent) {
        if (state == 0)
            state = 1;
    }//�����

    public void mouseEntered(MouseEvent mouseevent) {
   
    }//�����봰��

    public void mouseExited(MouseEvent mouseevent) {

    }//����뿪����

    public void mouseMoved(MouseEvent mouseevent) {
        nowMouseX = mouseevent.getX();
        nowMouseY = mouseevent.getY();
        beforeDraggedX = nowMouseX;
        beforeDraggedY = nowMouseY;    
    }

    public void mouseDragged(MouseEvent mouseevent) {//����϶�
        nowMouseX = mouseevent.getX();
        nowMouseY = mouseevent.getY();       
    }

    public static void main(String args[]) {
        new JavaBilliards();
    }

}
