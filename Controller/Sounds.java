import java.io.File;
import java.lang.Math;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class Sounds
{
	private static final Clip invaderkilled = loadFromName("invaderkilled.wav");
	private static final Clip explosion	= loadFromName("explosion.wav");
	private static final Clip failure  	= loadFromName("failure.wav");
	private static final Clip start    	= loadFromName("start.wav");
	private static final Clip shoot    	= loadFromName("shoot.wav");
	private static final Clip success  	= loadFromName("success.wav");
	private static final Clip thrust  	= loadFromName("thrust.wav");
	private static final Clip ufohigh	= loadFromName("ufo_highpitch.wav");
	private static final Clip ufolow	= loadFromName("ufo_lowpitch.wav");
	private static final Clip[] invader = {
											loadFromName("fastinvader1.wav"),
											loadFromName("fastinvader2.wav"),
											loadFromName("fastinvader3.wav"),
											loadFromName("fastinvader4.wav")
										};

	private static Clip loadFromName(String name)
	{
		AudioInputStream sound = SpaceInvader.audioStreamForName(name);
		Clip clip = null;
		try
		{
			DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat());
			clip = (Clip)AudioSystem.getLine(info);
			clip.open(sound);
		}
		catch (Exception e) { e.printStackTrace(); }
		return clip;
	}

	public static void invaderkilled()	{ new Audio(Sounds.invaderkilled).start(); }
	public static void explosion()		{ new Audio(Sounds.explosion).start(); }
	public static void failure()		{ new Audio(Sounds.failure).start(); }
	public static void start()			{ new Audio(Sounds.start).start(); }
	public static void shoot() 			{ new Audio(Sounds.shoot).start(); }
	public static void success() 		{ new Audio(Sounds.success).start(); }
	public static void thrust() 		{ new Audio(Sounds.thrust).start(); }
	public static void ufohigh() 		{ new Audio(Sounds.ufohigh).start(); }
	public static void ufolow() 		{ new Audio(Sounds.ufolow).start(); }
	public static void invader()		{ new Audio(Sounds.invader[(int)(Math.random()*Sounds.invader.length)]).start(); }
	
	private static class Audio extends Thread
	{
		private Clip sound;
		public Audio(Clip clip)
		{
			this.sound = clip;
		}
		
		public void run()
		{
			sound.setMicrosecondPosition(0);
			sound.start();
		}
	}
}