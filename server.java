package mic_server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

class Server implements ActionListener{
	AudioInputStream audioInputStream;
	static AudioInputStream ais;
	static AudioFormat format;
	static boolean status = true;
	//static int port = 50000;
	static int sampleRate = 8000;
	JButton [] b = new JButton[6];
	JPanel jpanel;
	JLabel jl,jl2;
	static byte[] username=new byte[15];
	static int user_c=0;//user number
	static String name;
	
	
	public static void main(String args[]) throws Exception {
		
		new Server();

		DatagramSocket servervo = new DatagramSocket(50000);//voice stream
		
		DatagramSocket clientname = new DatagramSocket(55000);//username from client
		
		DatagramPacket getname = new DatagramPacket(username,username.length);//get username from client
		clientname.receive(getname); //error
		name = new String(getname.getData());
		System.out.println(name);
		
		/**for lag = (byte_size/sample_rate)*2 Byte size 9728 will
		 * produce ~ 0.45 seconds of lag. Voice slightly broken. Byte size 1400
		 * will produce ~ 0.06 seconds of lag. Voice extremely broken. Byte size
		 * 4000 will produce ~ 0.18 seconds of lag. Voice slightly more broken
		 * then 9728.
		 */

		byte[] receiveData = new byte[5000];
		format = new AudioFormat(sampleRate, 16, 1, true, false);

		while (status == true) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
			servervo.receive(receivePacket);
			ByteArrayInputStream baiss = new ByteArrayInputStream(
					receivePacket.getData());
			ais = new AudioInputStream(baiss, format, receivePacket.getLength());
			toSpeaker(receivePacket.getData());

		}
	}

	public static void toSpeaker(byte soundbytes[]) {
		try {

			DataLine.Info dataLineInfo = new DataLine.Info(
					SourceDataLine.class, format);
			SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem
					.getLine(dataLineInfo);

			sourceDataLine.open(format);

			FloatControl volumeControl = (FloatControl) sourceDataLine
					.getControl(FloatControl.Type.MASTER_GAIN);
			volumeControl.setValue(6.0206f);

			sourceDataLine.start();
			sourceDataLine.open(format);

			sourceDataLine.start();

			 System.out.println("format? :" + sourceDataLine.getFormat());

			 sourceDataLine.write(soundbytes, 0, soundbytes.length);
			 System.out.println(soundbytes.toString());
			sourceDataLine.drain();
			sourceDataLine.close();
		} catch (Exception e) {
			System.out.println("Not working in speakers...");
			e.printStackTrace();
		}
	}
	
	public Server() { //layout
		JFrame jf = new JFrame("");
		jl=new JLabel("--------------List---------------");
		jl2=new JLabel("speaker");	
		jpanel = new JPanel();  //new
		jpanel.setLayout(null);				
		b[0]= new  JButton("check file");
		b[1] = new JButton("clean");
		b[2]= new  JButton("...");
		b[3]= new  JButton("username");
		b[4]= new  JButton("username");	
		b[5]= new  JButton("username");	
		jf.getContentPane().add(jpanel);  //add	
		jpanel.add(jl).setBounds(100,150,200,50);
		jpanel.add(jl2).setBounds(100,80,200,50);	
		jpanel.add(b[0]).setBounds(250,500,100,50);
		jpanel.add(b[1]).setBounds(100,500,100,50);
		jpanel.add(b[2]).setBounds(250,80,100,50);
		jpanel.add(b[3]).setBounds(100,200,100,50);
		jpanel.add(b[4]).setBounds(100,250,100,50);
		jpanel.add(b[5]).setBounds(100,300,100,50);
		b[3].setVisible(false);
		b[4].setVisible(false);
		b[5].setVisible(false);
		b[0].addActionListener(this);
		b[1].addActionListener(this);
		b[2].addActionListener(this);
		b[3].addActionListener(this);	
		b[4].addActionListener(this);
		b[5].addActionListener(this);	
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(500,700);	
		jf.setTitle("mic_server");
		jf.setVisible(true);		
	}	
	public void actionPerformed(ActionEvent e){
		 
		JButton s = (JButton)e.getSource();
		JButton [] list = new JButton[10];
		/*if(username!=null){
			b[3].setVisible(true);
			b[3].setText(name+"");
			
		}*/
		
		if(s==b[1]){//clean
			b[3].setVisible(false);
			b[4].setVisible(false);
			b[5].setVisible(false);
		}												
		if(s==b[2]){ //interrupt window
			Object[] options = { "OK", "CANCEL" };	
			int response =JOptionPane.showOptionDialog(null, "   Are you sure to interrupt speaker ?", "Confirm",
					JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,null, options, options[0]);
			if (response == JOptionPane.YES_OPTION) {
			    	jl2.setText("");	
			    	}			
		}
	
		if(s==b[3]||s==b[4]||s==b[5]){ //allow talking window
			Object[] options = { "ACCEPT", "REJECT" };	
			int response =JOptionPane.showOptionDialog(null, "   Do you allow the user to talk ?", "Confirm",
					JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,null, options, options[0]);
			if (response == JOptionPane.NO_OPTION) {
					b[3].setText("");
			    	}		
		}
	
	}	
}