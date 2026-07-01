# 📚 Book Recommender

Applicazione Java **client-server** per la gestione e raccomandazione di libri. Gli utenti possono cercare libri, creare librerie personali, aggiungere valutazioni e ricevere suggerimenti da altri lettori tramite un'interfaccia grafica JavaFX.

---

## 🛠️ Stack tecnologico

| Tecnologia | Ruolo |
|---|---|
| Java 11+ | Linguaggio principale |
| JavaFX | Interfaccia grafica (GUI) |
| PostgreSQL | Database relazionale |
| JDBC | Connessione Java ↔ Database |
| Apache Maven | Build e gestione dipendenze |
| Architettura Client-Server | Comunicazione via socket |

---

## 🏗️ Architettura

Il progetto è diviso in due componenti separati:

- **Server** — gestisce la logica di business, l'accesso al database PostgreSQL e accetta connessioni in entrata dai client
- **Client** — fornisce l'interfaccia grafica all'utente e comunica con il server tramite socket

```
book-recommender/
├── src/main/java/
│   ├── model/       # Entità: Libro, Utente, Libreria, Valutazione, Suggerimento
│   ├── server/      # Logica server e accesso al database (DATABASE.java)
│   ├── client/      # Servizio client e comunicazione socket
│   ├── view/        # Controller JavaFX per ogni schermata
│   └── app/         # Entry point dell'applicazione
├── src/main/resources/
│   ├── schema.sql   # Schema del database PostgreSQL
│   └── view/        # File CSS per la GUI
├── bin/             # Jar precompilati pronti all'uso
└── doc/             # Manuali tecnico e utente, Javadoc
```

---

## ⚙️ Prerequisiti

- Java Development Kit (JDK) 11 o superiore
- Apache Maven
- PostgreSQL in esecuzione su `localhost:5432`

---

## 🗄️ Setup Database

1. Avvia PostgreSQL sul tuo sistema
2. Crea un database (es. `bookrecommender`)
3. Esegui lo schema SQL per creare le tabelle:

```bash
psql -U postgres -d bookrecommender -f src/main/resources/schema.sql
```

4. Alla prima avvio, il server chiederà la password del database PostgreSQL

---

## 🔨 Compilazione

Dalla directory principale del progetto (dove si trova `pom.xml`):

```bash
mvn package
```

I jar vengono generati nella cartella `target/`.

---

## ▶️ Esecuzione

L'applicazione richiede **due terminali separati**.

**Terminale 1 — Avvia il Server:**
```bash
java -jar bin/book-recommender-Server.jar
```

**Terminale 2 — Avvia il Client:**
```bash
java -jar bin/book-recommender-Client.jar
```

Il server si mette in ascolto e il client avvia l'interfaccia grafica.

---

## 🔧 Risoluzione problemi

### `Connection to localhost:5432 refused`
PostgreSQL non è avviato. Su macOS con Homebrew:
```bash
brew services start postgresql
```

### `Address already in use: bind`
Un'istanza del server è già in esecuzione. Trova e termina il processo:

```bash
# macOS / Linux
lsof -i :8080
kill -9 <PID>

# Windows
netstat -aon | findstr "8080"
taskkill /F /PID <PID>
```

---

## 📄 Documentazione

Nella cartella `doc/` sono disponibili:
- **Manuale Utente** — guida all'utilizzo dell'applicazione
- **Manuale Tecnico** — architettura, scelte progettuali e dettagli implementativi
- **Javadoc** — documentazione dell'API (`doc/apidocs/index.html`)

---

## 👨‍💻 Autori

Progetto universitario — tutti i collaboratori sono elencati in [`autori.txt`](autori.txt)
