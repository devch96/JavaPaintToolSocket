
// JavaObjClientView.java ObjecStram 기반 Client
//실질적인 채팅 창
import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.ImageObserver;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.JToggleButton;
import javax.swing.JList;
import java.awt.Canvas;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet.ColorAttribute;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.sun.tools.javac.Main;


import javax.swing.Icon;

public class JavaPaintToolClientView extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtInput;
	private String UserName;
	private JButton btnSend;
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	private Socket socket; // 연결소켓
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;

	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	private JLabel lblUserName;
	// private JTextArea textArea;
	private JTextPane textArea;

	private Frame frame;
	private FileDialog fd;
	private JButton imgBtn;

	JPanel panel;
	private JLabel lblMouseEvent;
	private Graphics gc;
	private Graphics2D gc3 = null;
	private int pen_size = 2; // minimum 2
	// 그려진 Image를 보관하는 용도, paint() 함수에서 이용한다.
	private Image panelImage = null; 
	private Image tmpImage = null;
	private Graphics2D gc2 = null;
	private Image background = new ImageIcon(JavaPaintToolClientView.class.getResource("/img/background.jpg")).getImage();
	private ImageIcon exitImage = new ImageIcon(JavaPaintToolClientView.class.getResource("/img/image_exitButton.jpg"));
	private ImageIcon BlackCrayon = new ImageIcon(JavaPaintToolClientView.class.getResource("/img/black_crayon.png"));
	private ImageIcon BlueCrayon = new ImageIcon(JavaPaintToolClientView.class.getResource("/img/blue_crayon.png"));
	private ImageIcon GreenCrayon = new ImageIcon(JavaPaintToolClientView.class.getResource("/img/green_crayon.png"));
	private ImageIcon RedCrayon = new ImageIcon(JavaPaintToolClientView.class.getResource("/img/red_crayon.png"));
	private ImageIcon Eraser = new ImageIcon(JavaPaintToolClientView.class.getResource("/img/Eraser.png"));
	private ImageIcon FreeDraw = new ImageIcon(JavaPaintToolClientView.class.getResource("/img/freedraw.png"));
	private ImageIcon StraightLine = new ImageIcon(JavaPaintToolClientView.class.getResource("/img/straightline.png"));
	private ImageIcon Rectangle = new ImageIcon(JavaPaintToolClientView.class.getResource("/img/rec.jpg"));
	private ImageIcon FillRec = new ImageIcon(JavaPaintToolClientView.class.getResource("/img/fillrec.png"));
	private ImageIcon Circle = new ImageIcon(JavaPaintToolClientView.class.getResource("/img/circle.png"));
	private ImageIcon FillCir = new ImageIcon(JavaPaintToolClientView.class.getResource("/img/fillcircle.png"));
	private ImageIcon Emoji = new ImageIcon(JavaPaintToolClientView.class.getResource("/img/emoji.jpg"));
	private Image emo = Emoji.getImage();
	private int ox , oy, nx ,ny;
	private Color c;
	private String line = "freedraw";
	private int width, height;
 
	/**
	 * Create the frame.
	 * @throws BadLocationException 
	 */
	public JavaPaintToolClientView(String username, String ip_addr, String port_no)  {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1055, 660);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(728, 10, 277, 471);
		contentPane.add(scrollPane);

		textArea = new JTextPane();
		textArea.setEditable(true);
		textArea.setFont(new Font("굴림체", Font.PLAIN, 14));
		scrollPane.setViewportView(textArea);

		txtInput = new JTextField();
		txtInput.setBounds(737, 515, 209, 40);
		contentPane.add(txtInput);
		txtInput.setColumns(10);

		btnSend = new JButton("Send");
		btnSend.setFont(new Font("굴림", Font.PLAIN, 14));
		btnSend.setBounds(958, 514, 69, 40);
		contentPane.add(btnSend);

		lblUserName = new JLabel("Name");
		lblUserName.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName.setBackground(Color.WHITE);
		lblUserName.setFont(new Font("굴림", Font.BOLD, 14));
		lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName.setBounds(505, 570, 62, 40);
		contentPane.add(lblUserName);
		setVisible(true);

		AppendText("User " + username + " connecting " + ip_addr + " " + port_no);
		UserName = username;
		lblUserName.setText(username);

		imgBtn = new JButton("+");
		imgBtn.setFont(new Font("굴림", Font.PLAIN, 16));
		imgBtn.setBounds(823, 569, 50, 40);
		contentPane.add(imgBtn);
		
		JButton btnNewButton = new JButton(exitImage);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "400", "Bye");
				SendObject(msg);
				System.exit(0);
			}
		});
		btnNewButton.setBounds(936, 570, 75, 40);
		contentPane.add(btnNewButton);

		panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(22, 10, 550, 550);
		contentPane.add(panel);
		gc = panel.getGraphics();
		
		// Image 영역 보관용. paint() 에서 이용한다.
		panelImage = createImage(panel.getWidth(), panel.getHeight());
		tmpImage = createImage(panel.getWidth(), panel.getHeight());
		
		gc2 = (Graphics2D) panelImage.getGraphics();
		gc2.setColor(panel.getBackground());
		gc2.fillRect(0,0, panel.getWidth(),  panel.getHeight());
		gc2.setColor(Color.BLACK);
		gc2.drawRect(0,0, panel.getWidth()-1,  panel.getHeight()-1);
		gc2.drawImage(background,0,0,null);
		
		gc3 = (Graphics2D) tmpImage.getGraphics();
		gc3.setColor(panel.getBackground());
		gc3.fillRect(0,0, panel.getWidth(),  panel.getHeight());
		gc3.setColor(Color.BLACK);
		gc3.drawRect(0,0, panel.getWidth()-1,  panel.getHeight()-1);
		gc3.drawImage(background,0,0,null);
		
		lblMouseEvent = new JLabel("<dynamic>");
		lblMouseEvent.setHorizontalAlignment(SwingConstants.CENTER);
		lblMouseEvent.setFont(new Font("굴림", Font.BOLD, 14));
		lblMouseEvent.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblMouseEvent.setBackground(Color.WHITE);
		lblMouseEvent.setBounds(65, 570, 400, 40);
		contentPane.add(lblMouseEvent);
		

		JButton btnColorBlack = new JButton(BlackCrayon);
		JButton btnColorBlue = new JButton(BlueCrayon);
		JButton btnColorGreen = new JButton(GreenCrayon);
		JButton btnColorRed = new JButton(RedCrayon);
		JButton btnErase = new JButton(Eraser);
		JButton btnEmoticon = new JButton(Emoji);
		btnColorBlack.setBackground(Color.BLACK);
		btnColorBlack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnColorGreen.setBackground(Color.WHITE);
				btnColorBlack.setBackground(Color.BLACK);
				btnColorBlue.setBackground(Color.WHITE);
				btnColorRed.setBackground(Color.WHITE);
				btnErase.setBackground(Color.WHITE);
				c = new Color(0,0,0);
				gc2.setColor(c);
			}
		});
		btnColorBlack.setForeground(Color.BLACK);
		btnColorBlack.setBounds(595, 45, 100, 40);
		contentPane.add(btnColorBlack);
		
		btnColorBlue.setBackground(Color.WHITE);
		btnColorBlue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnColorGreen.setBackground(Color.WHITE);
				btnColorBlack.setBackground(Color.WHITE);
				btnColorBlue.setBackground(Color.BLACK);
				btnColorRed.setBackground(Color.WHITE);
				btnErase.setBackground(Color.WHITE);
				c = new Color(0,0,255);
				gc2.setColor(c);
			}
		});
		btnColorBlue.setBounds(595, 116, 100, 40);
		contentPane.add(btnColorBlue);
		
		btnColorGreen.setBackground(Color.WHITE);
		btnColorGreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnColorGreen.setBackground(Color.BLACK);
				btnColorBlack.setBackground(Color.WHITE);
				btnColorBlue.setBackground(Color.WHITE);
				btnColorRed.setBackground(Color.WHITE);
				btnErase.setBackground(Color.WHITE);
				c = new Color(0,255,0);
				gc2.setColor(c);
			}
		});
		btnColorGreen.setBounds(595, 185, 100, 40);
		contentPane.add(btnColorGreen);
		
		btnErase.setBackground(Color.WHITE);
		btnErase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnColorGreen.setBackground(Color.WHITE);
				btnColorBlack.setBackground(Color.WHITE);
				btnColorBlue.setBackground(Color.WHITE);
				btnColorRed.setBackground(Color.WHITE);
				btnErase.setBackground(Color.BLACK);
				c = new Color (255, 255, 255);
				gc2.setColor(c);
			}
		});
		btnErase.setBounds(595, 320, 100, 40);
		contentPane.add(btnErase);
		
		btnColorRed.setBackground(Color.WHITE);
		btnColorRed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnColorGreen.setBackground(Color.WHITE);
				btnColorBlack.setBackground(Color.WHITE);
				btnColorBlue.setBackground(Color.WHITE);
				btnColorRed.setBackground(Color.BLACK);
				btnErase.setBackground(Color.WHITE);
				c = new Color (255, 0, 0);
				gc2.setColor(c);
			}
		});
		btnColorRed.setBounds(595, 254, 100, 40);
		contentPane.add(btnColorRed);
		
		JButton btnClear = new JButton("Clear");
		btnClear.setBackground(Color.WHITE);
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gc2 = (Graphics2D) panelImage.getGraphics();
				gc2.setColor(panel.getBackground());
				gc2.fillRect(0,0, panel.getWidth(),  panel.getHeight());
				gc2.setColor(Color.BLACK);
				gc2.drawRect(0,0, panel.getWidth()-1,  panel.getHeight()-1);
				gc2.drawImage(background,0,0,null);
				gc.drawImage(panelImage, 0, 0, panel);
			}
		});
		btnClear.setBounds(598, 388, 97, 23);
		contentPane.add(btnClear);
		
		JButton btnfreedraw = new JButton(FreeDraw);
		JButton btnstraightline = new JButton(StraightLine);
		JButton btnrec = new JButton(Rectangle);
		JButton btnfillrec = new JButton(FillRec);
		JButton btncir = new JButton(Circle);
		JButton btnfillcir = new JButton(FillCir);
		btnfreedraw.setBounds(603, 433, 28, 25);
		btnfreedraw.setBackground(Color.BLACK);
		contentPane.add(btnfreedraw);
		btnfreedraw.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnfreedraw.setBackground(Color.BLACK);
				btnstraightline.setBackground(Color.WHITE);
				btnrec.setBackground(Color.WHITE);
				btnfillrec.setBackground(Color.WHITE);
				btncir.setBackground(Color.WHITE);
				btnfillcir.setBackground(Color.WHITE);
				btnEmoticon.setBackground(Color.WHITE);
				line = "freedraw";
			}
		});
		
		btnstraightline.setBackground(Color.WHITE);
		btnstraightline.setBounds(643, 433, 28, 25);
		contentPane.add(btnstraightline);
		btnstraightline.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnfreedraw.setBackground(Color.WHITE);
				btnstraightline.setBackground(Color.BLACK);
				btnrec.setBackground(Color.WHITE);
				btnfillrec.setBackground(Color.WHITE);
				btncir.setBackground(Color.WHITE);
				btnfillcir.setBackground(Color.WHITE);
				btnEmoticon.setBackground(Color.WHITE);
				line = "drawline";
			}
		});
		
		btnrec.setBackground(Color.WHITE);
		btnrec.setBounds(603, 472, 28, 25);
		contentPane.add(btnrec);
		btnrec.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnfreedraw.setBackground(Color.WHITE);
				btnstraightline.setBackground(Color.WHITE);
				btnrec.setBackground(Color.BLACK);
				btnfillrec.setBackground(Color.WHITE);
				btncir.setBackground(Color.WHITE);
				btnfillcir.setBackground(Color.WHITE);
				btnEmoticon.setBackground(Color.WHITE);
				line = "rectangle";
			}
		});

		
		
		btnfillrec.setBackground(Color.WHITE);
		btnfillrec.setBounds(643, 472, 28, 25);
		contentPane.add(btnfillrec);
		btnfillrec.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnfreedraw.setBackground(Color.WHITE);
				btnstraightline.setBackground(Color.WHITE);
				btnrec.setBackground(Color.WHITE);
				btnfillrec.setBackground(Color.BLACK);
				btncir.setBackground(Color.WHITE);
				btnfillcir.setBackground(Color.WHITE);
				btnEmoticon.setBackground(Color.WHITE);
				line = "fillrec";
			}
		});
		
		
		btncir.setBackground(Color.WHITE);
		btncir.setBounds(603, 509, 28, 25);
		contentPane.add(btncir);
		btncir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnfreedraw.setBackground(Color.WHITE);
				btnstraightline.setBackground(Color.WHITE);
				btnrec.setBackground(Color.WHITE);
				btnfillrec.setBackground(Color.WHITE);
				btncir.setBackground(Color.BLACK);
				btnfillcir.setBackground(Color.WHITE);
				btnEmoticon.setBackground(Color.WHITE);
				line = "circle";
			}
		});
		
		
		btnfillcir.setBackground(Color.WHITE);
		btnfillcir.setBounds(643, 509, 28, 25);
		contentPane.add(btnfillcir);
		btnfillcir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnfreedraw.setBackground(Color.WHITE);
				btnstraightline.setBackground(Color.WHITE);
				btnrec.setBackground(Color.WHITE);
				btnfillrec.setBackground(Color.WHITE);
				btncir.setBackground(Color.WHITE);
				btnfillcir.setBackground(Color.BLACK);
				btnEmoticon.setBackground(Color.WHITE);
				line = "fillcir";
			}
		});
		
		btnEmoticon.setBackground(Color.WHITE);
		btnEmoticon.setBounds(614, 556, 50, 40);
		contentPane.add(btnEmoticon);
		btnEmoticon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					btnfreedraw.setBackground(Color.WHITE);
					btnstraightline.setBackground(Color.WHITE);
					btnrec.setBackground(Color.WHITE);
					btnfillrec.setBackground(Color.WHITE);
					btncir.setBackground(Color.WHITE);
					btnfillcir.setBackground(Color.WHITE);
					btnEmoticon.setBackground(Color.BLACK);
					line = "Emoticon";
					}
		});

		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));
//			is = socket.getInputStream();
//			dis = new DataInputStream(is);
//			os = socket.getOutputStream();
//			dos = new DataOutputStream(os);

			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			// SendMessage("/login " + UserName);
			ChatMsg obcm = new ChatMsg(UserName, "100", "Hello");
			SendObject(obcm);

			ListenNetwork net = new ListenNetwork();
			net.start();
			TextSendAction action = new TextSendAction();
			btnSend.addActionListener(action);
			txtInput.addActionListener(action);
			txtInput.requestFocus();
			ImageSendAction action2 = new ImageSendAction();
			imgBtn.addActionListener(action2);
			MyMouseEvent mouse = new MyMouseEvent();
			panel.addMouseMotionListener(mouse);
			panel.addMouseListener(mouse);
			MyMouseWheelEvent wheel = new MyMouseWheelEvent();
			panel.addMouseWheelListener(wheel);


		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AppendText("connect error");
		}

	}

	public void paint(Graphics g) {
		super.paint(g);
		// Image 영역이 가려졌다 다시 나타날 때 그려준다.
		gc.drawImage(panelImage, 0, 0, this);
	}
	
	// Server Message를 수신해서 화면에 표시
	class ListenNetwork extends Thread {
		public void run() {
			while (true) {
				try {

					Object obcm = null;
					String msg = null;
					ChatMsg cm;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						msg = String.format("[%s]\n%s", cm.UserName, cm.data);
					} else
						continue;
					switch (cm.code) {
					case "200": // chat message
						if (cm.UserName.equals(UserName))
							AppendTextR(msg); // 내 메세지는 우측에
						else
							AppendText(msg);
						break;
					case "300": // Image 첨부
						if (cm.UserName.equals(UserName))
							AppendTextR("[" + cm.UserName + "]");
						else
							AppendText("[" + cm.UserName + "]");
						AppendImage(cm.img);
						break;
					case "500": // Mouse Event 수신
						DoMouseEvent(cm);
						break;
					case "600": // EMOTICON 수신
						DoMouseEmoticon(cm);
						break;
					}
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
//						dos.close();
//						dis.close();
						ois.close();
						oos.close();
						socket.close();

						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝

			}
		}
	}

	// Mouse Event 수신 처리
	public void DoMouseEvent(ChatMsg cm) {	
		if (cm.line.equals("freedraw")) {
			if (cm.mouse_e.getID() == MouseEvent.MOUSE_PRESSED) {
				ox = cm.mouse_e.getX();
				oy = cm.mouse_e.getY();
			}
			else  {
				nx = cm.mouse_e.getX();
				ny = cm.mouse_e.getY();
				gc2.setColor(cm.color);
				gc2.setStroke(new BasicStroke (cm.pen_size));
				gc2.drawLine(ox,oy,nx,ny);
				ox = nx;
				oy = ny;
				gc.drawImage(panelImage, 0,0, panel);
			}
		}
		if (cm.line.equals("drawline")) {
			if (cm.mouse_e.getID() == MouseEvent.MOUSE_RELEASED) {
			ox = cm.ox;
			oy = cm.oy;
			nx = cm.mouse_e.getX();
			ny = cm.mouse_e.getY();
			gc2.setColor(cm.color);
			gc2.setStroke(new BasicStroke (cm.pen_size));
			gc2.drawLine(ox,oy,nx,ny);
			gc.drawImage(panelImage,0,0,panel);
			}
		}
		if (cm.line.equals("rectangle")) {
			if (cm.mouse_e.getID() == MouseEvent.MOUSE_RELEASED){
				ox = cm.ox;
				oy = cm.oy;
				nx = cm.nx;
				ny = cm.ny;
				width = Math.abs(ox - cm.mouse_e.getX());
				height = Math.abs(oy - cm.mouse_e.getY());
				gc2.setColor(cm.color);
				gc2.setStroke(new BasicStroke (cm.pen_size));
				gc2.drawRect(nx,ny,width,height);
				gc.drawImage(panelImage,0,0,panel);
			}
		}
		if (cm.line.equals("fillrec")) {		
			if (cm.mouse_e.getID() == MouseEvent.MOUSE_RELEASED){
				ox = cm.ox;
				oy = cm.oy;
				nx = cm.nx;
				ny = cm.ny;
				width = Math.abs(ox - cm.mouse_e.getX());
				height = Math.abs(oy - cm.mouse_e.getY());
				gc2.setColor(cm.color);
				gc2.fillRect(nx,ny,width,height);
				gc.drawImage(panelImage,0,0,panel);
			}
		}
		if (cm.line.equals("circle")) {
			if (cm.mouse_e.getID() == MouseEvent.MOUSE_RELEASED){
				ox = cm.ox;
				oy = cm.oy;
				nx = cm.nx;
				ny = cm.ny;
				width = Math.abs(ox - cm.mouse_e.getX());
				height = Math.abs(oy - cm.mouse_e.getY());
				gc2.setColor(cm.color);
				gc2.setStroke(new BasicStroke (cm.pen_size));
				gc2.drawOval(nx,ny,width,height);
				gc.drawImage(panelImage,0,0,panel);
			}
		}
		if (cm.line.equals("fillcir")) {
			if (cm.mouse_e.getID() == MouseEvent.MOUSE_RELEASED){
				ox = cm.ox;
				oy = cm.oy;
				nx = cm.nx;
				ny = cm.ny;
				width = Math.abs(ox - cm.mouse_e.getX());
				height = Math.abs(oy - cm.mouse_e.getY());
				gc2.setColor(cm.color);
				gc2.fillOval(nx,ny,width,height);
				gc.drawImage(panelImage,0,0,panel);
			}
		}
	}
	public void DoMouseEmoticon(ChatMsg cm) {
		if (cm.code.matches("600") && cm.line.equals("Emoticon")){
			gc2.drawImage(emo,cm.mouse_e.getX(),cm.mouse_e.getY(), panel);
			gc.drawImage(panelImage,0,0, panel);
		}
	}
	public void SendMouseEvent(MouseEvent e) {
		ChatMsg cm = new ChatMsg(UserName, "500", "MOUSE");
		cm.mouse_e = e;
		cm.pen_size = pen_size;
		cm.color = c;
		cm.line = line;
		cm.ox = ox;
		cm.oy = oy;
		cm.nx = nx;
		cm.ny = ny;
		SendObject(cm);
	}
	public void SendMouseEmoticon(MouseEvent e) {
		ChatMsg cm = new ChatMsg(UserName, "600", "EMOTICON");
		cm.mouse_e = e;
		cm.pen_size = pen_size;
		cm.line = line;
		SendObject(cm);
	}

	class MyMouseWheelEvent implements MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			// TODO Auto-generated method stub
			if (e.getWheelRotation() < 0) { // 위로 올리는 경우 pen_size 증가
				if (pen_size < 20)
					pen_size++;
			} else {
				if (pen_size > 2)
					pen_size--;
			}
			lblMouseEvent.setText("mouseWheelMoved Rotation=" + e.getWheelRotation() 
				+ " pen_size = " + pen_size + " " + e.getX() + "," + e.getY());

		}
		
	}


	// Mouse Event Handler
	class MyMouseEvent implements MouseListener, MouseMotionListener {
		@Override
		public void mouseMoved(MouseEvent e) {
			lblMouseEvent.setText(e.getButton() + " mouseMoved " + e.getX() + "," + e.getY());
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			lblMouseEvent.setText(e.getButton() + " mouseClicked " + e.getX() + "," + e.getY());
			SendMouseEmoticon(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			lblMouseEvent.setText(e.getButton() + " mouseEntered " + e.getX() + "," + e.getY());
			// panel.setBackground(Color.YELLOW);

		}

		@Override
		public void mouseExited(MouseEvent e) {
			lblMouseEvent.setText(e.getButton() + " mouseExited " + e.getX() + "," + e.getY());
			// panel.setBackground(Color.CYAN);

		}

		@Override
		public void mousePressed(MouseEvent e) {
			lblMouseEvent.setText(e.getButton() + " mousePressed " + e.getX() + "," + e.getY());
			gc3.drawImage(panelImage,0,0,panel);
			ox = e.getX();
			oy = e.getY();	
			SendMouseEvent(e);
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			lblMouseEvent.setText(e.getButton() + " mouseDragged " + e.getX() + "," + e.getY());// 좌표출력가능
			if (line.equals("drawline")) {
				nx = e.getX();
				ny = e.getY();
				gc.drawImage(tmpImage,0,0,panel);
				gc2.drawImage(tmpImage,0,0,panel);
				gc2.drawLine(ox,oy,nx,ny);
				gc2.setColor(c);
				gc2.setStroke(new BasicStroke (pen_size));
				gc.drawImage(panelImage,0,0,panel);
			}
			if (line.equals("rectangle"))  {
				if (ox < e.getX() && oy < e.getY()) {
					nx = ox;
					ny = oy;
					width = Math.abs(ox - e.getX());
					height = Math.abs(oy - e.getY());
					gc.drawImage(tmpImage,0,0,panel);
					gc2.drawImage(tmpImage,0,0,panel);
					gc2.drawRect(nx,ny,width,height);
					gc2.setColor(c);
					gc2.setStroke(new BasicStroke (pen_size));
					gc.drawImage(panelImage,0,0,panel);
				}
				if ( ox < e.getX() && oy > e.getY()) {
					nx = ox;
					ny = e.getY();
					width = Math.abs(ox - e.getX());
					height = Math.abs(oy - e.getY());
					gc.drawImage(tmpImage,0,0,panel);
					gc2.drawImage(tmpImage,0,0,panel);
					gc2.drawRect(nx,ny,width,height);
					gc2.setColor(c);
					gc2.setStroke(new BasicStroke (pen_size));
					gc.drawImage(panelImage,0,0,panel);
				}
				if (ox > e.getX() && oy < e.getY()) {
					nx = e.getX();
					ny = oy;
					width = Math.abs(ox - e.getX());
					height = Math.abs(oy - e.getY());
					gc.drawImage(tmpImage,0,0,panel);
					gc2.drawImage(tmpImage,0,0,panel);
					gc2.drawRect(nx,ny,width,height);
					gc2.setColor(c);
					gc2.setStroke(new BasicStroke (pen_size));
					gc.drawImage(panelImage,0,0,panel);
				}
				if (ox > e.getX() && oy > e.getY()) {
					nx = e.getX();
					ny = e.getY();
					width = Math.abs(ox - e.getX());
					height = Math.abs(oy - e.getY());
					gc.drawImage(tmpImage,0,0,panel);
					gc2.drawImage(tmpImage,0,0,panel);
					gc2.drawRect(nx,ny,width,height);
					gc2.setColor(c);
					gc2.setStroke(new BasicStroke (pen_size));
					gc.drawImage(panelImage,0,0,panel);
				}	
			}
			if (line.equals("fillrec")){
				if (ox < e.getX() && oy < e.getY()) {
					nx = ox;
					ny = oy;
					width = Math.abs(ox - e.getX());
					height = Math.abs(oy - e.getY());
					gc.drawImage(tmpImage,0,0,panel);
					gc2.drawImage(tmpImage,0,0,panel);
					gc2.fillRect(nx,ny,width,height);
					gc2.setColor(c);
					gc2.setStroke(new BasicStroke (pen_size));
					gc.drawImage(panelImage,0,0,panel);
				}
				if ( ox < e.getX() && oy > e.getY()) {
					nx = ox;
					ny = e.getY();
					width = Math.abs(ox - e.getX());
					height = Math.abs(oy - e.getY());
					gc.drawImage(tmpImage,0,0,panel);
					gc2.drawImage(tmpImage,0,0,panel);
					gc2.fillRect(nx,ny,width,height);
					gc2.setColor(c);
					gc2.setStroke(new BasicStroke (pen_size));
					gc.drawImage(panelImage,0,0,panel);
				}
				if (ox > e.getX() && oy < e.getY()) {
					nx = e.getX();
					ny = oy;
					width = Math.abs(ox - e.getX());
					height = Math.abs(oy - e.getY());
					gc.drawImage(tmpImage,0,0,panel);
					gc2.drawImage(tmpImage,0,0,panel);
					gc2.fillRect(nx,ny,width,height);
					gc2.setColor(c);
					gc2.setStroke(new BasicStroke (pen_size));
					gc.drawImage(panelImage,0,0,panel);
				}
				if (ox > e.getX() && oy > e.getY()) {
					nx = e.getX();
					ny = e.getY();
					width = Math.abs(ox - e.getX());
					height = Math.abs(oy - e.getY());
					gc.drawImage(tmpImage,0,0,panel);
					gc2.drawImage(tmpImage,0,0,panel);
					gc2.fillRect(nx,ny,width,height);
					gc2.setColor(c);
					gc2.setStroke(new BasicStroke (pen_size));
					gc.drawImage(panelImage,0,0,panel);
				}	
			}
			if (line.equals("circle")) {
				if (ox < e.getX() && oy < e.getY()) {
					nx = ox;
					ny = oy;
					width = Math.abs(ox - e.getX());
					height = Math.abs(oy - e.getY());
					gc.drawImage(tmpImage,0,0,panel);
					gc2.drawImage(tmpImage,0,0,panel);
					gc2.drawOval(nx,ny,width,height);
					gc2.setColor(c);
					gc2.setStroke(new BasicStroke (pen_size));
					gc.drawImage(panelImage,0,0,panel);
				}
				if ( ox < e.getX() && oy > e.getY()) {
					nx = ox;
					ny = e.getY();
					width = Math.abs(ox - e.getX());
					height = Math.abs(oy - e.getY());
					gc.drawImage(tmpImage,0,0,panel);
					gc2.drawImage(tmpImage,0,0,panel);
					gc2.drawOval(nx,ny,width,height);
					gc2.setColor(c);
					gc2.setStroke(new BasicStroke (pen_size));
					gc.drawImage(panelImage,0,0,panel);
				}
				if (ox > e.getX() && oy < e.getY()) {
					nx = e.getX();
					ny = oy;
					width = Math.abs(ox - e.getX());
					height = Math.abs(oy - e.getY());
					gc.drawImage(tmpImage,0,0,panel);
					gc2.drawImage(tmpImage,0,0,panel);
					gc2.drawOval(nx,ny,width,height);
					gc2.setColor(c);
					gc2.setStroke(new BasicStroke (pen_size));
					gc.drawImage(panelImage,0,0,panel);
				}
				if (ox > e.getX() && oy > e.getY()) {
					nx = e.getX();
					ny = e.getY();
					width = Math.abs(ox - e.getX());
					height = Math.abs(oy - e.getY());
					gc.drawImage(tmpImage,0,0,panel);
					gc2.drawImage(tmpImage,0,0,panel);
					gc2.drawOval(nx,ny,width,height);
					gc2.setColor(c);
					gc2.setStroke(new BasicStroke (pen_size));
					gc.drawImage(panelImage,0,0,panel);
				}
			}
			if (line.equals("fillcir")) {
				if (ox < e.getX() && oy < e.getY()) {
					nx = ox;
					ny = oy;
					width = Math.abs(ox - e.getX());
					height = Math.abs(oy - e.getY());
					gc.drawImage(tmpImage,0,0,panel);
					gc2.drawImage(tmpImage,0,0,panel);
					gc2.fillOval(nx,ny,width,height);
					gc2.setColor(c);
					gc2.setStroke(new BasicStroke (pen_size));
					gc.drawImage(panelImage,0,0,panel);
				}
				if ( ox < e.getX() && oy > e.getY()) {
					nx = ox;
					ny = e.getY();
					width = Math.abs(ox - e.getX());
					height = Math.abs(oy - e.getY());
					gc.drawImage(tmpImage,0,0,panel);
					gc2.drawImage(tmpImage,0,0,panel);
					gc2.fillOval(nx,ny,width,height);
					gc2.setColor(c);
					gc2.setStroke(new BasicStroke (pen_size));
					gc.drawImage(panelImage,0,0,panel);
				}
				if (ox > e.getX() && oy < e.getY()) {
					nx = e.getX();
					ny = oy;
					width = Math.abs(ox - e.getX());
					height = Math.abs(oy - e.getY());
					gc.drawImage(tmpImage,0,0,panel);
					gc2.drawImage(tmpImage,0,0,panel);
					gc2.fillOval(nx,ny,width,height);
					gc2.setColor(c);
					gc2.setStroke(new BasicStroke (pen_size));
					gc.drawImage(panelImage,0,0,panel);
				}
				if (ox > e.getX() && oy > e.getY()) {
					nx = e.getX();
					ny = e.getY();
					width = Math.abs(ox - e.getX());
					height = Math.abs(oy - e.getY());
					gc.drawImage(tmpImage,0,0,panel);
					gc2.drawImage(tmpImage,0,0,panel);
					gc2.fillOval(nx,ny,width,height);
					gc2.setColor(c);
					gc2.setStroke(new BasicStroke (pen_size));
					gc.drawImage(panelImage,0,0,panel);
				}
			}
			SendMouseEvent(e);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			lblMouseEvent.setText(e.getButton() + " mouseReleased " + e.getX() + "," + e.getY());
			SendMouseEvent(e);
			// 드래그중 멈출시 보임
		}
	}

	// keyboard enter key 치면 서버로 전송
	class TextSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button을 누르거나 메시지 입력하고 Enter key 치면
			if (e.getSource() == btnSend || e.getSource() == txtInput) {
				String msg = null;
				// msg = String.format("[%s] %s\n", UserName, txtInput.getText());
				msg = txtInput.getText();
				SendMessage(msg);
				txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
				txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
				if (msg.contains("/exit")) // 종료 처리
					System.exit(0);
			}
		}
	}
	
	class ImageSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// 액션 이벤트가 sendBtn일때 또는 textField 에세 Enter key 치면
			if (e.getSource() == imgBtn) {
				frame = new Frame("이미지첨부");
				fd = new FileDialog(frame, "이미지 선택", FileDialog.LOAD);
				// frame.setVisible(true);
				// fd.setDirectory(".\\");
				fd.setVisible(true);
				// System.out.println(fd.getDirectory() + fd.getFile());
				if (fd.getDirectory().length() > 0 && fd.getFile().length() > 0) {
					ChatMsg obcm = new ChatMsg(UserName, "300", "IMG");
					ImageIcon img = new ImageIcon(fd.getDirectory() + fd.getFile());
					obcm.img = img;
					SendObject(obcm);
				}
			}
		}
	}


	public void AppendIcon(ImageIcon icon) {
		int len = textArea.getDocument().getLength();
		// 끝으로 이동
		textArea.setCaretPosition(len);
		textArea.insertIcon(icon);
	}

	// 화면에 출력
	public void AppendText(String msg) {
		// textArea.append(msg + "\n");
		// AppendIcon(icon1);
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
		//textArea.setCaretPosition(len);
		//textArea.replaceSelection(msg + "\n");
		
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(left, Color.BLACK);
	    doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		try {
			doc.insertString(doc.getLength(), msg+"\n", left );
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		//textArea.replaceSelection("\n");


	}
	// 화면 우측에 출력
	public void AppendTextR(String msg) {
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.	
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet right = new SimpleAttributeSet();
		StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
		StyleConstants.setForeground(right, Color.BLUE);	
	    doc.setParagraphAttributes(doc.getLength(), 1, right, false);
		try {
			doc.insertString(doc.getLength(),msg+"\n", right );
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		//textArea.replaceSelection("\n");

	}
	
	public void AppendImage(ImageIcon ori_icon) {
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len); // place caret at the end (with no selection)
		Image ori_img = ori_icon.getImage();
		Image new_img;
		ImageIcon new_icon;
		int width, height;
		double ratio;
		width = ori_icon.getIconWidth();
		height = ori_icon.getIconHeight();
		// Image가 너무 크면 최대 가로 또는 세로 200 기준으로 축소시킨다.
		if (width > 200 || height > 200) {
			if (width > height) { // 가로 사진
				ratio = (double) height / width;
				width = 200;
				height = (int) (width * ratio);
			} else { // 세로 사진
				ratio = (double) width / height;
				height = 200;
				width = (int) (height * ratio);
			}
			new_img = ori_img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			new_icon = new ImageIcon(new_img);
			textArea.insertIcon(new_icon);
		} else {
			textArea.insertIcon(ori_icon);
			new_img = ori_img;
		}
		len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		textArea.replaceSelection("\n");
		// ImageViewAction viewaction = new ImageViewAction();
		// new_icon.addActionListener(viewaction); // 내부클래스로 액션 리스너를 상속받은 클래스로
		// panelImage = ori_img.getScaledInstance(panel.getWidth(), panel.getHeight(), Image.SCALE_DEFAULT);

		gc2.drawImage(ori_img,  0,  0, panel.getWidth(), panel.getHeight(), panel);
		gc.drawImage(panelImage, 0, 0, panel.getWidth(), panel.getHeight(), panel);
	}

	// Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
	public byte[] MakePacket(String msg) {
		byte[] packet = new byte[BUF_LEN];
		byte[] bb = null;
		int i;
		for (i = 0; i < BUF_LEN; i++)
			packet[i] = 0;
		try {
			bb = msg.getBytes("euc-kr");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		for (i = 0; i < bb.length; i++)
			packet[i] = bb[i];
		return packet;
	}

	// Server에게 network으로 전송
	public void SendMessage(String msg) {
		try {
			// dos.writeUTF(msg);
//			byte[] bb;
//			bb = MakePacket(msg);
//			dos.write(bb, 0, bb.length);
			ChatMsg obcm = new ChatMsg(UserName, "200", msg);
			oos.writeObject(obcm);
		} catch (IOException e) {
			// AppendText("dos.write() error");
			AppendText("oos.writeObject() error");
			try {
//				dos.close();
//				dis.close();
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(0);
			}
		}
	}

	public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			// textArea.append("메세지 송신 에러!!\n");
			AppendText("SendObject Error");
		}
	}
}
