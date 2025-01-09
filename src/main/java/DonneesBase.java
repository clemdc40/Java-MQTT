import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class DonneesBase {
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        // Configuration RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("Brocker-broker"); // Hôte RabbitMQ dans Docker

        // Création de la connexion RabbitMQ
        com.rabbitmq.client.Connection rabbitConnection = factory.newConnection();
        Channel channel = rabbitConnection.createChannel();

        // Déclaration de l'échange et de la file d'attente
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println(" [*] En attente des messages. Appuyez sur CTRL+C pour quitter.");

        // Gestion des messages
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Reçu : '" + message + "'");
        
            try {
                String[] parts = message.split(", ");
                if (parts.length < 3) {
                    throw new IllegalArgumentException("Chargement de la voiture");
                }
        
                String batterie = parts[0].split(": ")[1].replace("%", "");
                String heure = parts[1].split(": ")[1];
                String temperature = parts[2].split(": ")[1].replace("°C", "");
        
                saveToDatabase(batterie, heure, temperature);
            } catch (Exception e) {
                System.err.println("Erreur de traitement du message : " + e.getMessage());
            }
        };
        

        // Consommation de la file d'attente
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    }

    private static void saveToDatabase(String batterie, String heure, String temperature) {
        String jdbcUrl = "jdbc:mysql://bdd:3306/Tesla?autoReconnect=true&useSSL=false"; // Hôte MySQL dans Docker
        String username = "root";
        String password = "root";

        String insertQuery = "INSERT INTO teslainfo (batterie, heure, temperature) VALUES (?, ?, ?)";

        try (java.sql.Connection dbConnection = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement preparedStatement = dbConnection.prepareStatement(insertQuery)) {

            preparedStatement.setFloat(1, Float.parseFloat(batterie));
            preparedStatement.setString(2, heure);
            preparedStatement.setFloat(3, Float.parseFloat(temperature));

            preparedStatement.executeUpdate();
            System.out.println(" [x] Données sauvegardées dans la base de données.");

        } catch (SQLException e) {
            System.err.println("Erreur lors de la sauvegarde dans la base de données : " + e.getMessage());
        }
    }
}
