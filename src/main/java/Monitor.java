import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Monitor {
    private static final String EXCHANGE_NAME = "logs";

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

            System.out.println(" [*] En attente des messages. Appuyez sur CTRL+C pour quitter.");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
            
                // Vérifie si le message demande si on veut charger la voiture
                if (message.equalsIgnoreCase("voulez vous charger la voiture")) {
                    System.out.println("\nReçu : " + message);
                    System.out.println("Répondez par 'oui' ou 'non' :");
            
                    // Lecture de la réponse utilisateur
                    try (java.util.Scanner scanner = new java.util.Scanner(System.in)) {
                        String reponse = scanner.nextLine();
            
                        // Vérifie la réponse et affiche une action
                        if (reponse.equalsIgnoreCase("oui")) {
                            System.out.println("Vous avez choisi de charger la voiture.");
                            // Ajoute ici l'envoi du message pour charger la voiture, si nécessaire
                        } else if (reponse.equalsIgnoreCase("non")) {
                            System.out.println("Vous avez choisi de ne pas charger la voiture.");
                            // Ajoute ici une action spécifique pour "non", si nécessaire
                        } else {
                            System.out.println("Réponse invalide. Veuillez répondre par 'oui' ou 'non'.");
                        }
                    }
                } else {
                    // Gère les autres types de messages normalement
                    System.out.println("Message reçu : " + message);
                }
            };
            
            

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        
        }
}