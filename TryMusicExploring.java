
package demo;

//package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

//import net.bytebuddy.asm.Advice.This;

public class TryMusicExploring implements Runnable {
	// size of the byte buffer used to read/write the audio stream
	private static final int BUFFER_SIZE = 4096;
	private static String audioFilePath = "E:/MUSIC CITY/prgmMusic/";
	// -----------------------------------------------------------------------------------------------------
	// to store current position
	Long currentFrame;
	Clip clip;

	// current status of clip
	String status;

	AudioInputStream audioInputStream;
	static String filePath;

	// -------------------------------------------------------------------------------------------------------
	static void play(String audioFilePath, long start, long end)
			throws IOException, UnsupportedAudioFileException, LineUnavailableException {
		System.err.println("In side play method");
		// It will open the file format

		List<File[]> files = new ArrayList<File[]>();
		File audioFile = new File(audioFilePath);
		if (audioFile.isDirectory()) {
			files.add(audioFile.listFiles());

		} else {
			File[] f1 = new File[1];
			f1[0] = audioFile;
			files.add(f1);
		}
		for (File audioFileNew : files.get(0)) {
			// ------------------------------------------------

			AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFileNew);

			AudioFormat format = audioStream.getFormat();

			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

			SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);

			audioLine.open(format);

			audioLine.start();

			System.out.println("Playback started.");

			byte[] bytesBuffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;

			while ((bytesRead = audioStream.read(bytesBuffer)) != -1) {
				audioLine.write(bytesBuffer, 0, bytesRead);
			}

			audioLine.drain();
			audioLine.close();
			audioStream.close();

			System.out.println("Playback completed.");

		}
		// run
	}

	@Override
	public void run() {

	}

	// --------------------------------------------------------------------------------------------------
	private void gotoChoice(int c) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
		switch (c) {
		case 1:
			pause();
			break;
		case 2:
			resumeAudio();
			break;
		case 3:
			restart();
			break;
		case 4:
			//stop();
			break;
		}
	}

	public void pause() {

		System.out.println("music paused");
		
		
		  if (status.equals("paused")) 
		{
		  System.out.println("audio is already paused"); return; 
		}
		  this.currentFrame = this.clip.getMicrosecondPosition(); clip.stop();
		  status = "paused";
		  System.out.println("music paused");
		 
	}

	// Method to resume the audio
	public void resumeAudio() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		
		
		  if (status.equals("play")) 
		  { 
	                       System.out.println("Audio is already "+ "being played"); 
		  return; 
		} 
		  clip.close(); resetAudioStream();
		  clip.setMicrosecondPosition(currentFrame); this.run();
		  System.out.println("music is resuming.");
		 
	}

	// Method to restart the audio
	public void restart() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
		
		  clip.stop(); 
		  clip.close(); 
		  resetAudioStream(); 
		  currentFrame = 0L;
		  clip.setMicrosecondPosition(0); 
		  this.run();
		  System.out.println("music restarted.");
	}

	/* Method to stop the audio
	public void stop() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		
		 currentFrame = 0L; 
		 // clip.stop(); 
		 // clip.close();
		 TryMusicExploring player = new TryMusicExploring();
		 player.stop(audioFilePath); player.stop();
		 System.out.println("music stopped.");
		 
	}
	*/

	public void resetAudioStream() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
		clip.open(audioInputStream);
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws ParseException, InterruptedException {
		// for starting the Audio
		Scanner in = new Scanner(System.in);
		System.out.print("Enter your start time in YYYY-MM-DD- HH:MM:SS format ");
		String time = in.nextLine();

		Scanner out = new Scanner(System.in);
		System.out.print("Enter your End time in YYYY-MM-DD- HH:MM:SS format ");
		String endTime = out.nextLine();
		// 2018-12-04 02:25:40
		// 2018-11-15 11:15:00
		// ScheduledExecutorService scheduler =
		// Executors.newSingleThreadScheduledExecutor();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
		long delay = simpleDateFormat.parse(time).getTime() - System.currentTimeMillis();
		long delayInSeconds = TimeUnit.MILLISECONDS.toSeconds(delay);
		// delay = delay / 1000;
		System.out.println(delay + " Delay in Milli Seconds");
		System.out.println(delayInSeconds + " Delay in Seconds");
		System.out.println("Waiting for...");
		ScheduledExecutorService execService = Executors.newScheduledThreadPool(0);
		execService.schedule(new Callable() {
			public Object call() throws Exception {
				play(audioFilePath, TimeUnit.MILLISECONDS.toSeconds(simpleDateFormat.parse(time).getTime()),
						TimeUnit.MILLISECONDS.toSeconds(simpleDateFormat.parse(endTime).getTime()));
						
				execService.shutdown();
				execService.awaitTermination(TimeUnit.MILLISECONDS.toSeconds(simpleDateFormat.parse(endTime).getTime()),
						TimeUnit.SECONDS);
				System.out.println("Executed!");
				return "Called!";
			}
		}, delayInSeconds, TimeUnit.SECONDS);

		// --------------------------------------------------------------------------
		/* final ScheduledExecutorService coordinator =
		 Executors.newSingleThreadScheduledExecutor();
		 //SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd
		 //HH:mm:ss");
		 long stopMusicTime = simpleDateFormat.parse(endtime).getTime() -simpleDateFormat.parse(time).getTime();
		 
		 outDelay = stopMusicTime/1000;
		 ScheduledExecutorService endExecService =
		 Executors.newScheduledThreadPool(1);
		 endExecService.schedule(()->{execService.shutdown(), outDelay,
		 TimeUnit.MILLISECONDS);
		 System.out.println("It will stop the Audio Player");
*/
		 

		try {
			// filePath = "E:/MUSIC CITY/prgmMusic/Aaj_phir.wav";
			// filePath = "https://youtube.com/";
			// SimpleAudioPlayer audioPlayer =
			// new SimpleAudioPlayer();

			// audioPlayer.play();
			// Scanner sc = new Scanner(System.in);
			TryMusicExploring player = new TryMusicExploring();
			// player.run();

			while (true) {
				System.out.println("1. pause");
				System.out.println("2. resume");
				System.out.println("3. restart");
				//System.out.println("4. stop");

				int c = in.nextInt();
				player.gotoChoice(c);
				if (c == 4)
					break;
			}
			in.close();
		}

		catch (Exception ex) {
			System.out.println("Error with playing sound.");
			// ex.printStackTrace();

		}
	}

}
