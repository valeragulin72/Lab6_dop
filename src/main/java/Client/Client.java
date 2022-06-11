package Client;

import Commands.*;
import Interaction.*;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Client {
    private static final UserInteraction interaction = new ConsoleInteraction();
    private static ServerInteraction serverInteraction;
    private static int port;
    private static String ip;
    private static Socket socket;
    private static int count;
    private static String reconnect;
    private static final Logger log = Logger.getLogger(Client.class.getName());


    public static void main(String[] args) throws InterruptedException {
        try {
            String[] argument = args[0].split(":");
            if (argument.length != 2) {
                throw new Exception();
            }
            ip = argument[0];
            port = Integer.parseInt(argument[1]);
        } catch (Exception e) {
            log.info("Incorrect address or port specified. Enter in format '*.*.*.*:****'");
            return;
        }
        connect();
        run();
    }


    public static void connect() throws InterruptedException {
        while (true) {
            try {
                log.info("Подключение к серверу.");
                socket = new Socket(ip, port);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                serverInteraction = new ServerInteraction(inputStream, outputStream);
                return;
            } catch (IOException e) {
                count++;
                log.info("Неудачное подключение к серверу " + e.getMessage());
                Thread.sleep(2000);
                if (count == 5) {
                    interaction.print(true, "Кажется, сервер умер. Будете пробовать подключиться заново? Введите 'да' или 'нет'.");
                    reconnect = interaction.getData();
                    if (reconnect.equals("да")) {
                        count = 0;
                        return;
                    } else if (reconnect.equals("нет")) {
                        System.exit(0);
                    } else interaction.print(true, "Введите 'да' или 'нет'!");
                }
            }
        }
    }

    public static void run() {
        interaction.print(true, "For a complete list of commands, type 'help'.");
        boolean run = true;
        boolean reconnect = false;
        while (run) {

            try {
                if (reconnect | socket.isClosed() | !socket.isConnected()) {
                    connect();
                    reconnect = false;
                }
                interaction.print(false, "\nEnter command: ");
                String potencialCommand = interaction.getData();

                if (potencialCommand == null) {
                    continue;
                }


                Command command = CommandExecution.proceedCommand(potencialCommand, false, interaction);
                if (command == null) {
                    continue;
                }
                if (command instanceof Exit) {
                    return;
                }
                if (command instanceof Preprocessing) {
                    ((Preprocessing) command).preprocess(interaction);
                }


                if (command instanceof ExecuteScript) {
                    boolean result = true;
                    try {
                        Stream<String> stream = Files.lines(Paths.get(((ExecuteScript) command).getArgument()));
                        List<String> list = stream.collect(Collectors.toList());

                        String line;
                        int lineNum = 0;
                        while (!list.isEmpty()) {
                            line = list.get(0);
                            list.remove(0);
                            try {
                                ScriptInteraction scriptInteraction = new ScriptInteraction(list);
                                Command command1 = CommandExecution.proceedCommand(line, true, scriptInteraction);
                                if (command1 == null) {
                                    continue;
                                }
                                if (command1 instanceof Exit) {
                                    return;
                                }
                                if (command1 instanceof Preprocessing) {
                                    ((Preprocessing) command1).preprocess(scriptInteraction);
                                }
                                try {

                                    serverInteraction.sendData(command1);
                                    Message message = (Message) serverInteraction.readData();
                                    if (!message.isSuccessful()) {
                                        throw new Exception();
                                    }

                                } catch (SocketTimeoutException e) {
                                    System.out.println("vremya vishlo");

                                }
                            } catch (Exception e) {
                                log.info("Error at " + lineNum);
                                result = false;
                                break;
                            }
                            lineNum ++;
                        }
                    } catch (FileNotFoundException e) {
                        interaction.print(true, "Entered file doesn't exist!");
                        continue;
                    }
                    if (result) {
                        log.info("Commands ran successfully!");
                    }
                    continue;
                }

                Boolean underRun = true;
                while (underRun) {
                    try {
                        serverInteraction.sendData(command);
                        Message message = (Message) serverInteraction.readData();
                        interaction.print(true, message.getText());
                        if (!message.isSuccessful()) {
                            run = false;
                        }
                        underRun = false;
                    } catch (SocketTimeoutException e) {
                        log.info("Error. " + e.getMessage());
                        interaction.print(true, "It seems server's very busy and can't answer. Would you try to reconnect? Enter 'yes' or 'no'.");
                        String resend = interaction.getData();
                        while (!resend.equals("yes")) {
                            if (resend.equals("no")) {
                                System.exit(0);
                            } else interaction.print(true, "Enter 'yes' or 'no'!");
                            resend = interaction.getData();
                        }
                    } catch (Exception e) {
                        log.info("Error. " + e.getMessage());
                        reconnect = true;
                        continue;
                    }
                }


            } catch (Exception e) {
                log.info("Error. " + e.getMessage());
            }
        }
    }
}
