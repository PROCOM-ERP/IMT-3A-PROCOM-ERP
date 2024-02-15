# :rocket: Deploying the application

Here is the complete guide on how to deploy your ERP.
We mainly use scripts to deploy this application, although we are researching other deployment tools.

> There also is a version for Windows developers, of all scripts in PowerShell

### :exclamation: Requirements :exclamation:

- Insure you have :whale: Docker installed. On Unix systems, run `docker --version`
- Insure you have a :whale: Docker daemon running. On Windows check if :whale: Docker desktop app is running, on Unix systems,run: `sudo systemctl status docker`
- Insure you have :lock: OpenSSL installed. Run: `openssl --version`
- Insure you have :coffee: Java installed (we use keytools to generate some trust stores for the application)

## :hammer: **For Development** :

- Insure you have the script `./src/security/docker_secrets_files.sh` :key:.
- Execute the script `./src/security/docker_secrets_files.sh`.
- Execute the script `./deploy.sh`, and if it's your first execution, add the `--sec` option to the command, as such: `./deploy.sh --sec`. This will generate all security certificates needed to run the app only if it doesn't detect existing ones.

  > If you want to renew certificates and already have a deployed instance, or simply already have certificates in your services and want to change them, execute `./src/security/clean_security.sh`

  > If you would prefer to pull images from a docker registry on :whale: [Docker Hub](https://hub.docker.com) instead of building them locally, you can simply add `--pull "registry_or_username/repository"`, such as: `./deploy --pull "gachille/erp"`.

  > If you would like to push you local images to a docker registry on :whale: [Docker Hub](https://hub.docker.com), you can simply add `--push "registry_or_username/repository"`, such as: `./deploy --push "gachille/erp"`. It will automatically tag the images, and push one image with multiple tags, all named after the services, as not to bring chaos to the repository.

## :gear: **For Production** :

- Insure you have the script `./src/security/docker_secrets.sh` :key:.
- Execute the deploy script with the `--swarm` option, as such: `./deploy.sh --swarm`, and if it's your first execution, add the `--sec` option to the command, as such: `./deploy.sh --swarm --sec`. This will generate all security certificates needed to run the app only if it doesn't detect existing ones.

This is going to activate :ship: Swarm mode :sailboat: for :whale: Docker, and create a node manager. Then it will deploy the services and load balance between them, restart them if they have errors etc.

> If you want to renew certificates and already have a deployed instance, or simply already have certificates in your services and want to change them, execute `./src/security/clean_security.sh`

> If you would prefer to pull images from a docker registry on :whale: [Docker Hub](https://hub.docker.com) instead of building them locally, you can simply add `--pull "registry_or_username/repository"`, such as: `./deploy --pull "gachille/erp"`.

> If you would like to push you local images to a docker registry on :whale: [Docker Hub](https://hub.docker.com), you can simply add `--push "registry_or_username/repository"`, such as: `./deploy --push "gachille/erp"`. It will automatically tag the images, and push one image with multiple tags, all named after the services, as not to bring chaos to the repository.
