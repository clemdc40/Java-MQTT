import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ModelePrediction {

    public static void main(String[] args) {
        List<Float> donneesBatterie = recupererDonneesBatterieDepuisBDD();
        System.out.println("Données sur la batterie : " + donneesBatterie);

        // Séparer les augmentations et les diminutions
        List<Float> augmentations = new ArrayList<>();
        List<Float> diminutions = new ArrayList<>();

        classerChangementsBatterie(donneesBatterie, augmentations, diminutions);

        System.out.println("Augmentations : " + augmentations);
        System.out.println("Diminutions : " + diminutions);

        // Calcul du temps de charge
        float tempsCharge = estimerTempsDeCharge(augmentations);
        System.out.println("Temps estimé de charge (minutes) : " + tempsCharge);
    }

    public static List<Float> recupererDonneesBatterieDepuisBDD() {
        String urlBDD = "jdbc:mysql://bdd:3306/Tesla?autoReconnect=true&useSSL=false";
        String utilisateur = "root";
        String motDePasse = "root";

        String requete = "SELECT batterie FROM teslainfo";
        List<Float> donneesBatterie = new ArrayList<>();

        try (Connection connexion = DriverManager.getConnection(urlBDD, utilisateur, motDePasse);
             Statement statement = connexion.createStatement();
             ResultSet resultat = statement.executeQuery(requete)) {

            while (resultat.next()) {
                Float batterie = resultat.getFloat("batterie");
                donneesBatterie.add(batterie);
            }

            System.out.println("Récupération des données réussie.");

        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
            e.printStackTrace();
        }

        return donneesBatterie;
    }

    public static void classerChangementsBatterie(List<Float> donneesBatterie, List<Float> augmentations, List<Float> diminutions) {
        for (int i = 1; i < donneesBatterie.size(); i++) {
            float precedente = donneesBatterie.get(i - 1);
            float actuelle = donneesBatterie.get(i);

            if (actuelle > precedente) {
                augmentations.add(actuelle);
            } else if (actuelle < precedente) {
                diminutions.add(actuelle); 
            }
        }
    }


    public static float estimerTempsDeCharge(List<Float> augmentations) {
        float tempsTotal = 0.0f; 
        float vitesseDeCharge = 1.0f; 
        for (int i = 1; i < augmentations.size(); i++) {
            float precedente = augmentations.get(i - 1);
            float actuelle = augmentations.get(i);
            float difference = actuelle - precedente;

            if (difference > 0) {
                tempsTotal += difference / vitesseDeCharge;
            }
        }

        return tempsTotal;
    }
}