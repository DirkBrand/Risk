package risk.commonObjects;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

	public static final int MINIMAL = 10;
	public static final int REPLAY = 20;
	public static final int DEBUG_IO = 30;
	public static final int DEBUG_METHODS = 40;
	public static final int DEBUG_ALL = 50;
	
	private int debugLevel;

	BufferedWriter out;
	FileWriter writer;

	String filename = "gameLog.txt";
	String line = null;

	public Logger() {
		this(0);
	}

	public Logger(int level) {
		try {
			writer = new FileWriter(filename);
			out = new BufferedWriter(writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		setDebugLevel(level);
	}

	public void log(int level, String message) {
		if (level <= getDebugLevel()) {
			try {
				String s = message + "\n";
				out.write(s);
				endLog();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void endLog() {
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		String temp = "";
		try {
			FileReader reader = new FileReader(filename);
			BufferedReader ins = new BufferedReader(reader);
			while ((line = ins.readLine()) != null)
				temp += line;

			ins.close();
			return temp;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp;
	}

	public int getDebugLevel() {
		return debugLevel;
	}

	public void setDebugLevel(int debugLevel) {
		this.debugLevel = debugLevel;
	}
}
