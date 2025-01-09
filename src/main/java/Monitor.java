import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Monitor {
    private static final String EXCHANGE_NAME = "logs";

    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";
    
    public static void main(String[] argv) throws Exception {
        // Configuration de la connexion RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("Brocker-broker"); 
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
    
        // Déclaration de l'échange
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
    
        // Création d'une file temporaire
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");
    
        System.out.println(ANSI_YELLOW + " [*] En attente des messages. Appuyez sur CTRL+C pour quitter." + ANSI_RESET);
    
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
    
            // Affichage amélioré des messages
            if (message.contains("Batterie")) {
                System.out.println(ANSI_BLUE + "----------------------------------------" + ANSI_RESET);
                System.out.println(ANSI_GREEN + "Message reçu : " + message + ANSI_RESET);
                System.out.println(ANSI_BLUE + "----------------------------------------" + ANSI_RESET);
    
                // Si la batterie est faible, mettez en rouge
                if (message.contains("Batterie: 10.0%")) {
                    System.out.println(ANSI_RED + "⚠ Alerte : Batterie faible à 10% !" + ANSI_RESET);
                }
            } else {
                System.out.println(ANSI_BLUE + "---------------------------------------------------" + ANSI_RESET);
                System.out.println(ANSI_GREEN + "Message reçu : " + message + ANSI_RESET);
                System.out.println(ANSI_BLUE + "---------------------------------------------------" + ANSI_RESET);
            }
        };
    
        // Consommation de la file d'attente
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    }
}    