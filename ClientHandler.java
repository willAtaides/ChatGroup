import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
	public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String clientUsername;

// Construtor que recebe um objeto Socket para lidar com um cliente
	public ClientHandler(Socket socket) {
		try {
			this.socket = socket;

// Configura os fluxos de entrada e saída para comunicação com o cliente			
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

// Lê o nome de usuário do cliente			
			this.clientUsername = bufferedReader.readLine();

// Adiciona este ClientHandler à lista de manipuladores de cliente			
			clientHandlers.add(this);

// Envia uma mensagem para todos os clientes informando que este cliente entrou no chat			
			broadcastMessage("SERVER: " + clientUsername + " entrou no chat!");
		} catch (IOException e) {
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}

	@Override
	public void run() {
		String messageFromClient;
		while (socket.isConnected()) {
			try {

// Lê as mensagens do cliente				
				messageFromClient = bufferedReader.readLine();

// Envia a mensagem para todos os outros clientes				
				broadcastMessage(messageFromClient);
			} catch (IOException e) {
				closeEverything(socket, bufferedReader, bufferedWriter);
				break;
			}
		}
	}

// Método para transmitir uma mensagem para todos os clientes
	public void broadcastMessage(String messageToSend) {
		for (ClientHandler clientHandler : clientHandlers) {
			try {
				if (clientHandler != this) {
					clientHandler.bufferedWriter.write(messageToSend);
					clientHandler.bufferedWriter.newLine();
					clientHandler.bufferedWriter.flush();
				}
			} catch (IOException e) {
				closeEverything(socket, bufferedReader, bufferedWriter);
			}
		}
	}

// Método para remover este ClientHandler da lista e informar que o cliente saiu do chat
	public void removeClientHandler() {
		clientHandlers.remove(this);
		broadcastMessage("SERVER: " + clientUsername + " saiu do chat!");
	}

// Método para fechar todos os recursos associados ao cliente
	public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
		removeClientHandler();
		try {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (bufferedWriter != null) {
				bufferedWriter.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
