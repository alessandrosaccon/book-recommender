===========================================
Istruzioni per il Progetto Book Recommender
===========================================

Questo file README fornisce le istruzioni necessarie per la compilazione, il testing e l'esecuzione del progetto "Book Recommender".

----------------------------------------
# Prerequisiti
----------------------------------------

Prima di procedere, assicurati di avere installato sul tuo sistema:

1.  Java Development Kit (JDK) (versione 11 o successiva consigliata)
2.  Apache Maven

----------------------------------------
# Compilazione e Packaging (per OS)
----------------------------------------

Questo è un progetto Maven. Per compilare il codice, eseguire i test e creare i file .jar eseguibili, devi aprire un terminale, navigare nella directory principale del progetto (dove si trova il file pom.xml) ed eseguire il comando.

---
# Su Windows
---
1. Apri il "Prompt dei comandi" (cercandolo nel menu Start).
2. Naviga alla cartella del progetto (es. `cd C:\Utenti\TuoNome\Progetto\book-recommender`).
3. Esegui il comando:

>>> mvn package <<<

---
# Su macOS
---
1. Apri l'applicazione "Terminale" (da Applicazioni > Utility).
2. Naviga alla cartella del progetto (es. `cd /Users/TuoNome/Progetto/book-recommender`).
3. Esegui il comando:

>>> mvn package <<<

---
# Su Linux (es. Ubuntu)
---
1. Apri il "Terminale" (spesso con Ctrl+Alt+T).
2. Naviga alla cartella del progetto (es. `cd /home/TuoNome/Progetto/book-recommender`).
3. Esegui il comando:

>>> mvn package <<<

---
Al termine, dovresti trovare i file `book-recommender-Server.jar` e `book-recommender-Client.jar` nella sottocartella `target/`.

----------------------------------------
# Esecuzione dell'Applicazione (per OS)
----------------------------------------

L'applicazione è suddivisa in un Server e un Client. Devi aprire **due terminali separati** ed eseguirli dalla directory principale del progetto.

---
# Su Windows
---
1. **Terminale 1 (Server):** Apri un "Prompt dei comandi" e digita:
>>> java -jar bin/book-recommender-Server.jar <<<

2. **Terminale 2 (Client):** Apri un **secondo** "Prompt dei comandi" e digita:
>>> java -jar bin/book-recommender-Client.jar <<<

---
# Su macOS
---
1. **Terminale 1 (Server):** Apri un "Terminale" e digita:
>>> java -jar bin/book-recommender-Server.jar <<<

2. **Terminale 2 (Client):** Apri una **seconda** finestra o tab del "Terminale" e digita:
>>> java -jar bin/book-recommender-Client.jar <<<

---
# Su Linux
---
1. **Terminale 1 (Server):** Apri un "Terminale" e digita:
>>> java -jar bin/book-recommender-Server.jar <<<

2. **Terminale 2 (Client):** Apri una **seconda** finestra o tab del "Terminale" e digita:
>>> java -jar bin/book-recommender-Client.jar <<<

---
Il server si metterà in ascolto e il client avvierà l'interfaccia grafica (GUI)

========================================
# Risoluzione Errori
========================================

----------------------------------------
Errore: "Address already in use: bind"
----------------------------------------

Questo errore si verifica se tenti di avviare il Server quando un altro processo (probabilmente una precedente esecuzione del server non terminata correttamente) sta già utilizzando la porta richiesta (es. 8080).

Soluzione Rapida: Trova e Chiudi il Processo.

---
Su Windows
---
Apri il Prompt dei comandi (o PowerShell) come amministratore.

1. Trova quale processo sta usando la porta (es. 8080), digitando:

>>> netstat -aon | findstr "8080" <<<

Questo comando ti mostrerà una o più righe. Guarda l'ultima colonna: è il PID (Process ID), un numero (es. 12345).

2. Ora, chiudi forzatamente quel processo digitando:

>>> taskkill /F /PID TUO_NUMERO_PID <<<

(Sostituisci TUO_NUMERO_PID con il numero che hai trovato, es. taskkill /F /PID 12345)

---
Su macOS o Linux
---
Apri il Terminale.

1. Trova il processo che usa la porta (es. 8080):

>>> lsof -i :8080 <<<

Questo ti mostrerà una riga con il nome del processo (es. java) e il suo PID (un numero nella seconda colonna).

2. Chiudi forzatamente quel processo digitando:

>>> kill -9 TUO_NUMERO_PID <<<

(Sostituisci TUO_NUMERO_PID con il numero che hai trovato).