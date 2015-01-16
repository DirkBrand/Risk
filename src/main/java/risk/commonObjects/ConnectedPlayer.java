package risk.commonObjects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectedPlayer {

	private boolean ai = false;
	private String path = "";

	private int id;
	private String name;

	private int portNR;
	private InetAddress hostAddress;

	private Socket socket;

	private BufferedReader input;
	private PrintWriter output;
	
	private int playerOrderNumber;

	public ConnectedPlayer(int id, String name) { // For AI players
		this.setId(id);
		this.name = name;
	}

	public ConnectedPlayer(Socket client, int id, int portNR, InetAddress host) {
		this.setId(id);
		this.socket = client;
		this.setHostAddress(host);
		this.setPortNR(portNR);
		try {
			input = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			output = new PrintWriter(client.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setPlayerOrderNumber(1);
	}

	public void send(String mes) {
		output.println(mes);
	}

	public Socket getSocket() {
		return socket;
	}

	public PrintWriter getOutput() {
		return output;
	}

	public BufferedReader getInput() {
		return input;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void closeAll() {
		try {
			if (input != null)
				input.close();
			if (output != null)
				output.close();
			if (socket != null && !socket.isClosed())
				socket.close();

		} catch (IOException e) {
			System.err.println("Close Error!");
			System.err.println("IOException:  " + e);
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPortNR() {
		return portNR;
	}

	public void setPortNR(int portNR) {
		this.portNR = portNR;
	}

	public InetAddress getInetAddress() {
		return hostAddress;
	}

	public void setHostAddress(InetAddress hostAddress) {
		this.hostAddress = hostAddress;
	}

	public void startIO() {
		try {
			input = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean isAI() {
		return ai;
	}

	public void setAI(boolean ai) {
		this.ai = ai;
	}

	public void setPath(String p) {
		this.path = p;
	}

	public String getPath() {
		return path;
	}

	public int getPlayerOrderNumber() {
		return playerOrderNumber;
	}

	public void setPlayerOrderNumber(int playerOrderNumber) {
		this.playerOrderNumber = playerOrderNumber;
	}
	
	@Override
	public String toString() {
		return getId() + " " + getName();
	}
}
