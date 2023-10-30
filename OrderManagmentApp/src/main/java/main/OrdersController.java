package main;

import com.rabbitmq.client.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import com.rabbitmq.client.Envelope;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrdersController {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8443;
    private static final int DATAPORT = 10001;
    private static final String KEY_STORE_PATH = "./keystore.jks";
    private static final String KEY_STORE_PASSWORD = "securemdp";

    private String username, password;
    private final static String QUEUE_NAME = "orders";
    private String message="";
    @FXML
    TextArea orderTextArea;

    @FXML
    Button getOrderButton;

    private static String unescape(String text) {
        return text.replace("&apos;", "'");
    }
    public void getOrderClick() throws IOException, TimeoutException {
        Connection connection = ConnectionFactoryUtil.createConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        int prefetchCount = 1; // Adjust this value as needed
        channel.basicQos(prefetchCount);


        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {

                message = new String(body, "UTF-8");
                System.out.println("Message received: '" + message + "'");

                Pattern stringPattern = Pattern.compile("<string>(.*?)</string>");
                Matcher stringMatcher = stringPattern.matcher(message);
                List<Product> products = new ArrayList<>();
                // Store the email address
                String email = "";
                if (stringMatcher.find()) {
                    String stringContent = stringMatcher.group(1);

                    // Define the regex pattern to match products within the <string> content
                    String productPattern = "Product\\{name=([^,]+), id=(\\d+), volume=(\\d+), description=([^}]+)\\}";

                    // Create a pattern object and a matcher for product matching
                    Pattern productRegex = Pattern.compile(productPattern);
                    Matcher productMatcher = productRegex.matcher(stringContent);

                    // Find and store products
                    while (productMatcher.find()) {
                        String name = unescape(productMatcher.group(1));
                        int id = Integer.parseInt(productMatcher.group(2));
                        int volume = Integer.parseInt(productMatcher.group(3));
                        String description = unescape(productMatcher.group(4));

                        products.add(new Product(name, id, volume, description));
                    }

                    // Define the regex pattern to match the email address within the <string> content
                    String emailPattern = "Email address:(\\S+)";

                    // Create a pattern object and a matcher for email matching
                    Pattern emailRegex = Pattern.compile(emailPattern);
                    Matcher emailMatcher = emailRegex.matcher(stringContent);


                    if (emailMatcher.find()) {
                        email = emailMatcher.group(1);
                    }
                }

     // Print the extracted products and email

                String finalOutput="";
                for (Product product : products) {
                    finalOutput+= "Product Name: " + product.getName() + " Description: " + product.getDescription() + "\n";
                }

                finalOutput+="Buyers Email: " + email;
                orderTextArea.setText(finalOutput);

            }


        };



        channel.basicConsume(QUEUE_NAME, false, consumer);
    }

    public void declineClick() throws IOException {
        String email = extractEmail(orderTextArea.getText());
        saveOrder(orderTextArea.getText()+" DECLINED");
        orderTextArea.setText("");
        //sendMail(email, "Order Result", "Your order is declined!");
    }

    public void acceptClick()throws IOException
    {
        String email = extractEmail(orderTextArea.getText());
        saveOrder(orderTextArea.getText()+" ACCEPTED");
        orderTextArea.setText("");
        //sendMail(email, "Order Result", "Your order is accepted!");
    }

    public String extractEmail(String input)
    {
        Pattern pattern = Pattern.compile("Buyers Email: ([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4})");
        Matcher matcher = pattern.matcher(input);

        // Find the first matching email address in the input string
        if (matcher.find()) {
            String email = matcher.group(1); // Group 1 captures the email address
            System.out.println("Email: " + email);
            return email;
        } else {
            System.out.println("No email found in the input string.");
            return "";
        }
    }


    private Properties loadMailConfig() throws FileNotFoundException, IOException {
        Properties serverprop = new Properties();
        serverprop.load(new FileInputStream(new File("./server.properties")));
        String mailProvider = serverprop.getProperty("mail_provider");

        System.out.println("Koristi se " + mailProvider);

        Properties mailProp = new Properties();
        mailProp.load(new FileInputStream(new File("./mail" + File.separator + mailProvider + ".properties")));

        username = mailProp.getProperty("username");
        password = mailProp.getProperty("password");
        return mailProp;
    }

    private boolean sendMail(String to, String title, String body) throws FileNotFoundException, IOException {

        Properties props = loadMailConfig();

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(title);
            message.setText(body);
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            Logger.getLogger(OrdersController.class.getName()).setUseParentHandlers(false);
            Logger.getLogger(OrdersController.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
            return false;
        }
    }

    public void saveOrder(String orderResult)
    {
        System.setProperty("javax.net.ssl.trustStore", KEY_STORE_PATH);
        System.setProperty("javax.net.ssl.trustStorePassword", KEY_STORE_PASSWORD);

        try {
            // Create an SSLSocketFactory
            SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();

            // Create an SSLSocket and connect to the server
            SSLSocket clientSocket = (SSLSocket) sf.createSocket(HOST, DATAPORT);

            // Get input and output streams for communication with the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

            // Send a message to the server
            writer.println(orderResult);


            String serverResponse = reader.readLine();

            if(serverResponse.equals("OK"))
            {
                System.out.println("File written succesfully");
            }

            clientSocket.close();

        } catch (IOException e) {
            Logger.getLogger(OrdersController.class.getName()).setUseParentHandlers(false);
            Logger.getLogger(OrdersController.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
        }
    }
}
