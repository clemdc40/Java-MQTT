import requests
import numpy as np
import matplotlib.pyplot as plt
import tensorflow as tf
from sklearn.preprocessing import MinMaxScaler
import sys
import io

# Encodage UTF-8
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

url = "http://localhost:8085/api/charges"
response = requests.get(url)

if response.status_code == 200:
    data = response.json()
    
    batterie = np.array([entry["batterie"] for entry in data]).reshape(-1, 1)
    
    scaler = MinMaxScaler()
    batterie_scaled = scaler.fit_transform(batterie)

    def create_sequences(data, seq_length):
        x, y = [], []
        for i in range(len(data) - seq_length):
            x.append(data[i:i + seq_length])
            y.append(data[i + seq_length])
        return np.array(x), np.array(y)

    seq_length = 300  # Longueur des séquences
    X, y = create_sequences(batterie_scaled, seq_length)

    # Modèle amélioré
    model = tf.keras.Sequential([
        tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(128, return_sequences=True), input_shape=(seq_length, 1)),
        tf.keras.layers.Dropout(0.2),
        tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(64)),
        tf.keras.layers.Dense(32, activation='relu'),
        tf.keras.layers.Dense(1)
    ])

    model.compile(optimizer='adam', loss='mse')

    model.fit(X, y, epochs=8, batch_size=32, verbose=1)

    future_predictions = []
    current_seq = batterie_scaled[-seq_length:]

    for _ in range(200): 
        prediction = model.predict(current_seq.reshape(1, seq_length, 1), verbose=0)
        future_predictions.append(prediction[0, 0])
        current_seq = np.append(current_seq[1:], prediction, axis=0)

    future_predictions = scaler.inverse_transform(np.array(future_predictions).reshape(-1, 1)).flatten()

    heures = [entry["heureFormatee"] for entry in data]
    index_continu = list(range(len(heures)))

    plt.figure(figsize=(12, 6))
    plt.plot(index_continu, batterie, linestyle='-', color='b', label='Données réelles')
    plt.plot(range(len(batterie), len(batterie) + len(future_predictions)),
             future_predictions, linestyle='--', color='r', label='Prédictions')
    
    plt.xlabel('Temps')
    plt.ylabel('Niveau de batterie (%)')
    plt.title("Évolution de la batterie avec prédiction améliorée")
    plt.legend()
    plt.grid(True)
    plt.tight_layout()
    plt.show()

else:
    print(f"Erreur lors de la récupération des données : {response.status_code}")
