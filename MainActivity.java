package com.example.mic_client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends Activity {
    private Button startButton, stopButton ,talkButton;
    private EditText name;
    public byte[] buffer;
    public static DatagramSocket socket;
    //private int port = 50000;
    AudioRecord recorder;

    private int sampleRate = 8000;
    @SuppressWarnings("deprecation")
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    private boolean status = true;

    int bufferSizeInBytes;
    int bufferSizeInShorts;
    int shortsRead;
    short audioBuffer[];
   
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.start_button);
        stopButton = (Button) findViewById(R.id.stop_button);
        talkButton = (Button) findViewById(R.id.talk_button);
        name = (EditText) findViewById(R.id.editText);
        System.out.println("start");   
        
        talkButton.setOnClickListener(new View.OnClickListener() {//talk
            public void onClick(View v) {
            	getusername();
            }
        });

        
        startButton.setOnClickListener(new View.OnClickListener() {//start
            public void onClick(View v) {
            	//flag server allow
                status = true;
                startStreaming();
            }

        });

        stopButton.setOnClickListener(new View.OnClickListener() {//stop
            public void onClick(View v) {
                status = false;
                recorder.release();
                Log.d("VS", "Recorder released");

            }

        });

        minBufSize += 5120;
        System.out.println("minBufSize: " + minBufSize);
    }


    public void startStreaming() {

        Thread streamThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    DatagramSocket socket = new DatagramSocket();
                    Log.d("VS", "Socket Created");

                    byte[] buffer = new byte[minBufSize];

                    Log.d("VS", "Buffer created of size " + minBufSize);
                    DatagramPacket packet;
                    //machine's IP
                    final InetAddress destination = InetAddress
                            .getByName("192.168.0.3");//163.21.245.164 //10.1.12.164
                    Log.d("VS", "Address retrieved");

                    recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                            sampleRate, channelConfig, audioFormat,
                            minBufSize * 10);
                    Log.d("VS", "Recorder initialized");

                    recorder.startRecording();

                    while (status == true) {

                        // reading data from MIC into buffer
                        minBufSize = recorder.read(buffer, 0, buffer.length);

                        // putting buffer in the packet
                        packet = new DatagramPacket(buffer, buffer.length,
                                destination, 50000);

                        socket.send(packet);
                        System.out.println("MinBufferSize: " + minBufSize);

                    }

                } catch (UnknownHostException e) {
                    Log.e("VS", "UnknownHostException");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("VS", "IOException");
                }
            }

        });
        streamThread.start();
    }
    public void getusername() {
    	 Thread nameThread = new Thread(new Runnable() {

             @Override
             public void run() {
                 try {
                	 DatagramSocket clientSocket = new DatagramSocket();
                	 
                	 byte[] sendStr = new byte[15];
                	 String sendname =name.getText().toString();
                	 sendStr = sendname.getBytes();
                	 
                	 
                	 final InetAddress destination = InetAddress.getByName("192.168.0.3");
                	 DatagramPacket sendPacket =new DatagramPacket(sendStr, sendStr.length, destination,55000);
                	 clientSocket.send(sendPacket);
                	
                    
                 
                 } catch (IOException e) {
                     e.printStackTrace();
                     Log.e("VS", "IOException");
                 }
             }

         });
         	nameThread.start();
     }
    
    	
    }

