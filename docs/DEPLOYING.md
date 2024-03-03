# :rocket: Deploying the application

Here is the complete guide on how to deploy your ERP.
We mainly use scripts to deploy this application, although we are researching other deployment tools.

> There also is a version for Windows developers, of all scripts in PowerShell

> Check the Warning section at the end of this guide in case you're having an issue

### :exclamation: Requirements :exclamation:

- Insure you have :whale: Docker installed. On Unix systems, run `docker --version`.
- Insure you have a :whale: Docker daemon running. On Windows check if :whale: Docker desktop app is running, on Unix systems,run: `sudo systemctl status docker`.
- Insure you have :lock: OpenSSL installed. Run: `openssl --version`.
- Insure you have :coffee: Java installed (we use keytools to generate some trust stores for the application).
  - Insure you have :lock: keytool installed. Run: `keytool --version`.
- Finally, insure you have expect installed. Run: `expect --version`.

## :hammer: **For Development** :

- Insure you have the script `./security/docker_secrets_files.sh` :key:.
- Execute the script `./security/docker_secrets_files.sh`.
- Execute the script `./deploy.sh`, and if it's your first execution, add the `--sec` option to the command, as such: `./deploy.sh --sec`. This will generate all security certificates needed to run the app only if it doesn't detect existing ones (see [Security Guide](./security/README.md) if you encounter any issues regarding .jks, .p12, .crt or .pem files).

  > If you want to see available options, execute `./deploy.sh --help`.

  > If you want to renew certificates and already have a deployed instance, or simply already have certificates in your services and want to change them, execute `./security/clean_security.sh`

  > If you would prefer to pull images from a docker registry on :whale: [Docker Hub](https://hub.docker.com) instead of building them locally, you can simply add `--pull "registry_or_username/repository"` as well as a version to pull from `--version [X.X.X | latest]`, such as: `./deploy.sh --pull "gachille/erp" --version 1.0.0`.

  > If you would like to push you local images to a docker registry on :whale: [Docker Hub](https://hub.docker.com), you can simply add `--push "registry_or_username/repository"`, such as: `./deploy --push "gachille/erp"`. It will automatically tag the images, and push one image with multiple tags, all named after the services, as not to bring chaos to the repository.

## :gear: **For Production** :

- Insure you have the script `./security/docker_secrets.sh` :key:.
- Execute the deploy script with the `--swarm` option, as such: `./deploy.sh --swarm`, and if it's your first execution, add the `--sec` option to the command, as such: `./deploy.sh --swarm --sec`. This will generate all security certificates needed to run the app only if it doesn't detect existing ones (see [Security Guide](./security/README.md) if you encounter any issues regarding .jks, .p12, .crt or .pem files).

This is going to activate :ship: Swarm mode :sailboat: for :whale: Docker, and create a node manager. Then it will deploy the services and load balance between them, restart them if they have errors etc.

> If you want to renew certificates and already have a deployed instance, or simply already have certificates in your services and want to change them, execute `./security/clean_security.sh`

> If you would prefer to pull images from a docker registry on :whale: [Docker Hub](https://hub.docker.com) instead of building them locally, you can simply add `--pull "registry_or_username/repository"` as well as a version to pull from `--version [X.X.X | latest]`, such as: `./deploy.sh --pull "gachille/erp" --version 1.0.0`.

> If you would like to push you local images to a docker registry on :whale: [Docker Hub](https://hub.docker.com), you can simply add `--push "registry_or_username/repository"`, such as: `./deploy.sh --push "gachille/erp"`. It will automatically tag the images, and push one image with multiple tags, all named after the services, as not to bring chaos to the repository.

# :recycle: Undeploying the application

Hopefully, things should be running smoothly now. If you want to undeploy the system, execute the `undeploy.sh` script using : `./undeploy.sh` command, this one doesn't have particular options

## :warning: Warning !

Having trouble deploying ? Maybe you're entering the good credentials, but still can't access the application ?
In order to make sure services can communicate, after you just deployed the app, when it's your first time accessing the [frontend](https://localhost:3000/), please accept the prompt to proceed with adding an exception for the site, as it's certificates have yet to be validated by a known authority (it costs a certain sum of money). Be careful to do the same with the gateway using this [link](https://localhost:8041/api/authentication/v1/hello), so that the gateway can safely communicate information to the frontend.

The command to make a file executable (in order to execute a .sh script for example), is `chmod x file_name.sh`.
