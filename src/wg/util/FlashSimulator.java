package wg.util;

public class FlashSimulator {

	static long startTime;

	public static String Capabilities_serverString = "A=t&SA=t&SV=t&EV=t&MP3=t&AE=t&VE=t&ACC=f&PR=t&SP=t&SB=f&DEB=t&V=WIN%208%2C0%2C0%2C0&M=Macromedia%20Windows&R=1600x1200&DP=72&COL=color&AR=1.0&OS=Windows%20XP&L=en&PT=External&AVD=f&LFD=f&WD=f";

	public static void startFlash() {
		startTime = System.currentTimeMillis();
	}

	public static long getTimer() {
		return System.currentTimeMillis() - startTime;
	}
}
