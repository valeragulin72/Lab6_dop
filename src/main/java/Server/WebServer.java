package Server;

import Client.Client;
import Xml.Xml;
import Commands.*;
import Movie.*;
import Interaction.*;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Objects;
import java.util.logging.Logger;

public class WebServer {
    private static final UserInteraction interaction = new ConsoleInteraction();
    private static Hashtable<String, Movie> collection = new Hashtable<>();
    public static File file;
    public static final int port = 8088;
    private static LocalDateTime creationDate;
    private static LocalDateTime initDate;
    private static final Logger log = Logger.getLogger(Client.class.getName());


    private final static String HEADERS =
            "HTTP/1.1\n" +
                    "Host: 0.0.0.0" + "\n" +
                    "Content-Type: text/html\n" +
                    "Content-Length: %s\n";

    public static void main(String[] args) throws Exception {
        if (Arrays.stream(args).count() != 0) {
            String fileName = args[0];
            if (fileName != null) {
                file = new File(fileName);
                log.info("File found!\n\nLaunch preparation.");
                if (!prepare()) {
                    log.info("Launch stop.");
                    return;
                }
            } else {
                interaction.print(true, "File not found or incorrect input.");
            }
        } else {
            interaction.print(true, "File not found or incorrect input.");
        }

        try {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(Xml.toXml(new HashtableInfo(collection, creationDate)));
                    fileWriter.flush();
                } catch (Exception e) {
                    log.info(e.getMessage());
                }
                log.info("Server stop.");
            }));

        } catch (Exception e) {
            log.info("Failed to set exit condition.");
            return;
        }



        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            log.info(String.format("Unable to start server (%s)%n", e.getMessage()));
            return;
        }
        InetAddress inetAddress = serverSocket.getInetAddress();
        log.info("Server launched at: " + inetAddress.getHostAddress());
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                log.info(String.format("Client %s:%s connected!",socket.getInetAddress(),socket.getPort()));
                ClientSession session = new ClientSession(socket);
                new Thread(session).start();


            } catch (IOException e) {
                log.info("Connection lost! ");
            }
        }
    }

    private static void uploadInformation() throws FileNotFoundException, IllegalAccessException, NoSuchFieldException {
        log.info("Uploading file " + file);
        HashtableInfo hashtableInfo = Xml.fromXml(file);
        collection = Objects.requireNonNull(hashtableInfo).getCollection();
        creationDate = hashtableInfo.getCreationDate();
        log.info("Collection upload successfully!\n");
    }

    private static boolean prepare() {
        try {
            uploadInformation();
        } catch (FileNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            if (e instanceof NoSuchFieldException || e instanceof IllegalAccessException || e instanceof NullPointerException) {
                log.info("Problems processing the file. Data not read. We create a new file.");
            }
            initDate = LocalDateTime.now();
            FileWriter fileWriter;
            try {
                fileWriter = new FileWriter(file);
                fileWriter.close();
            } catch (IOException ex) {
                log.info("The file could not be created, there are insufficient permissions, or the format of the file name is incorrect.");
                log.info("Error message: " + ex.getMessage());
                return false;
            }
        }
        return true;
    }
}
