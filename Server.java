import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

// Construtor que recebe um objeto ServerSocket
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

// Inicia o servidor
    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("Um novo usuário foi conectado!");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
// Fecha o servidor
    public void closeServerSocket() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
// Cria um ServerSocket que escuta na porta 1234
        ServerSocket serverSocket = new ServerSocket(1234);

// Cria uma instância da classe Server usando o ServerSocket
        Server server = new Server(serverSocket);

// Inicia o servidor
        server.startServer();
    }
}
