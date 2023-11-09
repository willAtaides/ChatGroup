import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

// Construtor que recebe um objeto Socket e um nome de usuário
    public Client(Socket socket, String username) {
        try {
            this.socket = socket;

// Configura os fluxos de entrada e saída para comunicação com o servidor
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

// Método para enviar mensagens para o servidor
    public void sendMessage() {
        try {

// Envia o nome de usuário para o servidor            
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {

// Lê mensagens do usuário e as envia para o servidor                
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            scanner.close(); 
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

// Método para ouvir e exibir mensagens do servidor
    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                while (socket.isConnected()) {
                    try {
// Lê e exibe mensagens recebidas do servidor                        
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

// Método para fechar todos os recursos associados ao cliente
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Entre com seu nome para o chat em grupo: ");
        String username = scanner.nextLine();

// Cria um Socket para se conectar ao servidor (no endereço "localhost" e porta 1234)        
        Socket socket = new Socket("localhost", 1234);

// Cria uma instância do cliente com o Socket e o nome de usuário   
        Client client = new Client(socket, username);

// Inicia a thread para enviar mensagens para o servidor
        client.listenForMessage();
        client.sendMessage();
        
        
        scanner.close();
    }
}
