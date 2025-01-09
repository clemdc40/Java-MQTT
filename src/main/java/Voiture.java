/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Utilisateur
 */
public class Voiture {
    public float batterie;
    public String heureFormatee;
    public int heure;
    public int minutes;
    public float temperature;
    public float acceleration;

    public Voiture(float laBatterie, String uneHeureFormatee, float uneTemperature, float uneAcceleration){
        this.batterie = laBatterie;
        this.heureFormatee = uneHeureFormatee;
        this.temperature = uneTemperature;
        this.acceleration = uneAcceleration;
        this.heure = 00;
        this.minutes = 00;
    }

    public void diminuerBatterie(boolean aLeChauffage){
        if(aLeChauffage == true){
            try{
                Thread.sleep(500);
                this.batterie = getBatterie() - 1;
                incrementerHeure(5);
                this.temperature = this.temperature + 0.5f;
            }catch(Exception e){
                System.err.println("Exception : " + e);
            }
        }else{
            try{
                Thread.sleep(600);
                this.batterie = getBatterie() - 1;
                incrementerHeure(6);
                this.temperature = this.temperature - 0.2f;
            }catch(Exception e){
                System.err.println("Exception : " + e);
            }
        }
        System.out.println("Batterie : " + getBatterie() + "%, heures : " + formaterHeure() + ", temperature : " + this.temperature + "°C");
        envoyerMessageMettreACharger();
    }
    
    public void augmenterBatterie(boolean Superchargeur){
        if(Superchargeur == true){
            try{
                Thread.sleep(10);
                this.batterie = this.batterie + 1;
                incrementerHeure(1);
                System.out.println("Batterie : " + getBatterie() + "%, heures : " + formaterHeure());
            }catch(Exception e){
                System.err.println("Exception : " + e);
            }
        }else{
            try{
                Thread.sleep(800);
                this.batterie = this.batterie + 1;
                incrementerHeure(20);
                System.out.println("Batterie : " + getBatterie() + "%, heures : " + formaterHeure());
            }catch(Exception e){
                System.err.println("Exception : " + e);
            }
        }
    }
    
    public void incrementerHeure(int laMinute) {
        this.minutes += laMinute;

        if (this.minutes >= 60) {
            this.heure += this.minutes / 60; 
            this.minutes = this.minutes % 60; 
        }

        if (this.heure >= 24) {
            this.heure = this.heure % 24; 
        }
    }

    
    public String formaterHeure(){
        return this.heure + "H" + this.minutes;
    }
    
    public void envoyerMessageMettreACharger(){
        if(this.batterie == 10){
            System.err.println("Alerte : Chargez le vehicule");
        }
        if(this.batterie == 0){
            System.err.println("Le véhicule est déchargé");
        }
    }
    

    public float getAcceleration(){
        return acceleration;
    }
    public float getBatterie() {
        return batterie;
    }

    public String getHeure() {
        return heureFormatee;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setBatterie(float batterie) {
        this.batterie = batterie;
    }

    public void setHeure(String heure) {
        this.heureFormatee = heure;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
    
    public void setAcceleration(float uneAcceleration){
        this.acceleration = uneAcceleration;
    }

}
