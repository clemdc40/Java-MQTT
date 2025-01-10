import requests
import matplotlib.pyplot as plt

url = "http://localhost:8085/api/charges"
response = requests.get(url)

if response.status_code == 200:
    data = response.json()
    heures = [entry["heureFormatee"] for entry in data]
    batterie = [entry["batterie"] for entry in data]
    index_continu = list(range(len(heures)))

    plt.figure(figsize=(12, 6))
    plt.plot(index_continu, batterie, linestyle='-', color='b')
    plt.xticks(index_continu[::5], heures[::5], rotation=45)
    plt.xlabel('Heure')
    plt.ylabel('Niveau de batterie (%)')
    plt.title("Évolution continue de la batterie au fil du temps")
    plt.grid(True)
    plt.tight_layout()
    plt.show()
else:
    print(f"Erreur lors de la récupération des données : {response.status_code}")
