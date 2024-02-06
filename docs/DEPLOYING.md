# Deploying the application

## Lancement de l'Application et Workflows classiques

1. **Avec Maven** :

   - Assurez-vous d'avoir Maven installé.
   - Exécutez `mvn clean install` pour construire l'application. (se placer dans un répertoire contenant un fichier `pom.xml`)

2. **Avec Docker** :
   - Assurez-vous que Docker est installé.
   - Assurez-vous que les certificats de sécurité sont présents au sein du projet. S'ils ne le sont pas encore, suivez le [guide de sécurité](https://github.com/PROCOM-ERP/IMT-3A-PROCOM-ERP/tree/master/docs/security)
   - Assurez-vous que les secrets docker sont bien générés par le script (non gitté) `./src/security/docker_secrets.sh`, pour vous en assurez, vous pouvez relancer le script, ou bien éxecuter `docker secrets ls`.
   - Lancez l'application en utilisant le script `./src/system/deploy.sh`. (se placer dans le repertoire root du projet, qui contient le fichier `docker-compose.yaml`).
     - Le script de base `deploy.sh` construit et déploie les images telles qu'indiquées dans le compose avec les versions indiquée dans le .env.
     - Il permet néanmoins aussi de construire et pousser les images dans un registre docker distant, à utiliser alors de cette façon : `./src/system/deploy.sh --push "username/repo_name"` ici on a un registre Docker hub, par défaut.
       Il demande de s'authentifier, vous devrez donc créer un compte sur [Docker Hub](https://hub.docker.com)
     - Il permet de la même façon de pull les images sur un registre docker distant, à utiliser de manière similaire : `./src/system/deploy.sh --pull "username/repo_name"`
