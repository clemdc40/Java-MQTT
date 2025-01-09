import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Tesla {

    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws Exception {
        // Configuration de RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("Brocker-broker"); // Assurez-vous que le nom est correct
        Connection connection = factory.newConnection();
             Channel channel = connection.createChannel();

            // Déclaration de l'échange
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

            // Initialisation de la voiture
            Voiture uneVoiture = new Voiture(100, "12:00", 18, 1);
            boolean aCharger = false;
            boolean aChauffer = false;

            while (true) {
                // Gestion de la batterie
                if (!aCharger) {
                    uneVoiture.diminuerBatterie(aChauffer);
                    if (uneVoiture.getBatterie() < 10) { // Si batterie faible
                        System.out.println("Alerte : Batterie faible. Demande de charge...");
                        String demande = "voulez vous charger la voiture";
                        channel.basicPublish(EXCHANGE_NAME, "", null, demande.getBytes("UTF-8"));
                        aCharger = true;
                    }
                } else {
                    uneVoiture.augmenterBatterie(false); // Simulation de charge
                    System.out.println("Chargement...");
                    if (uneVoiture.getBatterie() >= 100) { // Charge complète
                        System.out.println("Charge terminée.");
                        aCharger = false;
                    }
                }

                // Gestion de la température
                if (uneVoiture.getTemperature() < 18) {
                    aChauffer = true;
                } else if (uneVoiture.getTemperature() > 21) {
                    aChauffer = false;
                }

                // Construire le message
                String message = String.format(
                        "Batterie: %.1f%%, Heure: %s, Température: %.1f°C",
                        uneVoiture.getBatterie(),
                        uneVoiture.formaterHeure(),
                        uneVoiture.getTemperature()
                );

                // Publier le message
                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
                System.out.println(" [x] Envoyé : '" + message + "'");

            
        }
    }
}
