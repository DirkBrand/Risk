package risk.server.facilitator;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;

import risk.commonObjects.ConnectedPlayer;
import risk.commonObjects.Logger;
import risk.server.controller.ControllerLogic;

public class Facilitator {

	int maxConnections = 0;
	int idCounter = 0;

	String address = "localhost";
	int startPort = 3000;

	FacilitatorProtocolManager pm;
	Logger log;

	private static ServerSocket server = null;
	
	FacilitatorGui gui;

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Facilitator();
	}

	public Facilitator() {
		log = new Logger(Logger.REPLAY);
		pm = new FacilitatorProtocolManager(log, this);

        // TODO: Fix this terrible hack - path updated for Gradle deployment 20141030
		this.readAIOpponents("./src/main/java/risk/aiplayers");
		this.readAIOpponents("./src/main/java/risk/aiplayers/EMMPlayers");
		this.readAIOpponents("./src/main/java/risk/aiplayers/MCTSPlayers");

		new Thread(new startServer()).start();
		
		gui = new FacilitatorGui();
		printToGui("Facilitator started up");
	}
	
	public void printToGui (String message) {
		gui.printToArea(message);
	}

	private void readAIOpponents(String path) {
		String file;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				file = listOfFiles[i].getName();
				if (file.endsWith("AI.java")) {
					ConnectedPlayer temp = new ConnectedPlayer(idCounter++,
							file.substring(0, file.length() - 5)); /* TODO */
					temp.setAI(true);
					temp.setPath(path + "/");

					pm.addAIClient(temp);
				}
			}
		}
	}

	public void launchAI(String name) {
		
		Thread t = new Thread(new aiRunThread(name));
		t.setDaemon(true);
		t.start();
	}

	public void connectPlayer(int id, Socket client) {
		pm.startPlayer(id, client);
	}

	public void disconnectPlayer(int id) {
		pm.closeConnection(id);
	}

	public void startGame(ConnectedPlayer[] cPairs, String mapName, Logger log, int port) {
		new ControllerLogic(cPairs[0], cPairs[1],
				getMapLocation(mapName), log, pm.getMesID(), port);
	}

	private String getMapLocation(String mapName) {
		String mapLoc = "./MapFiles/";

		File folder = new File("./MapFiles");
		File[] listOfFiles = folder.listFiles();

		int ind = 0;

		for (int i = 0; i < listOfFiles.length; i++) {
			String temp = "";
			if (listOfFiles[i].isFile()) {
				temp = listOfFiles[i].getName();
				if (temp.startsWith(mapName)) {
					mapLoc += temp;
					break;				}
			}
		}

		return mapLoc;
	}

	// Thread to wait for new clients to connect
	public class startServer implements Runnable {
		@Override
		public void run() {
			try {
				server = new ServerSocket(startPort);

				while (idCounter < maxConnections || maxConnections == 0) {
					Socket client = server.accept();

					connectPlayer(idCounter++, client);
				}

			} catch (IOException e) {
				System.out.println("An error occured.");
				e.printStackTrace();
			}

		}
	}

	public class aiRunThread implements Runnable {
		
		String name;
		
		public aiRunThread(String name) {
			this.name = name;
		}

		@Override
		public void run() {
			String args = name;
			String optional = "";
			String location = "risk.aiplayers.";
			if (name.startsWith("MCTS")) {
				optional += "2000";
				location += "MCTSPlayers."+ name;
			}
			if (name.startsWith("EMM")) {
				optional += "3";
				location += "EMMPlayers."+ name;
			}
			try {
				ProcessBuilder broker = null;
				if (System.getProperty("os.name").startsWith("Windows")) {
					broker = new ProcessBuilder("java.exe", "-cp", "bin",
							location, args,optional);
				} else if (System.getProperty("os.name").equalsIgnoreCase("linux")) {
					broker = new ProcessBuilder(
							"java",
							"-cp",						
							"bin", location, args,optional);

				}
				Process runAI = broker.start();

				Reader reader = new InputStreamReader(runAI.getInputStream());
				Reader readerErr = new InputStreamReader(runAI.getErrorStream());
				Thread t1 = new Thread(new aiReadThread(reader, 0));
				t1.setDaemon(true);
				t1.start();
				Thread t2 = new Thread(new aiReadThread(readerErr, 1));
				t2.setDaemon(true);
				t2.start();

				runAI.waitFor();
				runAI.destroy();
				
			} catch (Throwable e) {
				e.printStackTrace();
			}
			
			System.out.println("AI thread closed");		
		}
		

	}
	
	public class aiReadThread implements Runnable {
		Reader read;
		int type;

		public aiReadThread(Reader read, int type) {
			this.read = read;
			this.type = type;
		}

		@Override
		public void run() {
			try {
				while (read != null) {
					int ch;
					while ((ch = read.read()) != -1) {
						if (type == 0) System.out.print((char) ch);
						else  System.err.print((char) ch);
					}
				}
				read.close();
			} catch (IOException e) {

			}
		}

	}

}
