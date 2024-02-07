# Security

Voici un guide rapide portant sur la sécurité, le chiffrement, etc au sein de notre projet d'ERP modulaire.
Déstinés aux parties prenantes et membres du projet, non aux utilisateurs finaux.

### Pré-requis

- Assurez-vous que **OpenSSL** est installé sur votre système pour la gestion des certificats SSL.
- Assurez-vous que **keytool**(outil préinstallé par java, à vérifier sur votre machine en essayant de lancer un `keytool --version`) est installé sur votre système pour la gestion des certificats SSL.

### Utilisation de `security_setup.sh`

Ce script génere (s'il n'en trouve pas déjà) une clé pour la CA, un certificat, et un trust store. Ensuite une clé et un certificat (signé par la CA) pour chaque service demandé.
Il répartit les trust store (fichiers `.p12`) dans chaque service (a chaque service son trust store uniquement), le trust store de la CA (fichier `.jks` dans le répertoire system, qui est passé a tous les services au démarrage).
Il vient ensuite placer toutes les clés et certificats, ainsi que demandes de signatures (fichiers `.key`, `.crt`, `.csr`) dans des répertoires créés pour l'occasion au sein du répetoire `./security/`.

1. **Lancement du Script** :
   - Ouvrez un terminal, et placez vous dans le répertoire de securité `./src/security`
   - Exécutez le script avec la commande : `./security_setup.sh`.
   - Entrez le nombre de services demandés (avec authentication, directory, gateway, et message-broker on est déjà à 4 par exemple)
   - Entrez le nom de chaque service, un par un.

Attention : Le service s'occupant de RabbitMQ (Queues/Event Management) doit s'appeler `message-broker` pour que le script fonctionne et que les clés soient placés aux bons endroits

2. **Génération des Certificats** :

   - Le script va générer des certificats pour les services spécifiés.
   - Il créera des clés, des demandes de signature de certificat (CSR), et signera ces CSR.

3. **Configuration des Keystores** :
   - Le script s'occupe également de la création des keystores nécessaires pour les services, ainsi que de leur distribution

### Utilisation de `clean_security.sh`

Ce script fait l'inverse du premier, et essaie de trouver les clés, certificats, demande de signature, trust store, etc aux endroits auxuels ils sont sensé être en fonction du nombre de service et leur nom (même prompt que le script précédent)
Il va tout mettre dans un dossier `./security/archives/Y-M-D-h-m`, et si l'option `--CA` est séléctionnée, alors les cles et certificats de la CA seront aussi archivée (on peut vouloir simplement refaire les certficats des services en gardant la même CA)

- Ce script sert à nettoyer ou à réinitialiser les configurations établies par `security_setup.sh`.
- Pour l'utiliser, exécutez : `./clean_security.sh`.
- Une option `--CA` est disponnible afin d'archiver aussi les clés et le trust store de l'autorité de certification.

Pour l'éxecution sur Windows du script powershell `./clean_security.ps1`, l'option à rentrer est -IncludeCA

### Utilisation de `docker_secrets.sh`

Ce script génère en local les secrets nécessaire au déploiement de l'application.
Il n'y a pas d'options particulières.
Pour des raisons évidentes de sécurité, il n'est pas géré dans ce répertoire git.
