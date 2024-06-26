# :rocket: Deploying the application

**Here is the complete guide on how to deploy your ERP.
We mainly use scripts to deploy this application, although we are researching other deployment tools.**

Accessing the webpage :[Frontend](#frontend)
Accessing the system logs: [Logs](#logs)

All `.sh` scripts have a `.ps1` version for Windows. But make sure to run docker commands and shell scripts in either a WSL, or in a UNIX based system.
(Your IDE's terminal often supports bash)

## Deployment modes

There are two different ways of using our system :

1. [Compose mode](#compose-mode)
2. [Swarm mode](#swarm-mode)

> [!TIP]
> Compose vs Swarm : Explanation
>
> > Compose will use docker compose, Swarm will use docker stack deploy. The key difference is that Swarm is a system in which we create and manage nodes. We can then deploy services on individual or multiple nodes, load balance between them, and much more. Swarm will automatically start new containers that will launch again services that just failed. It also has the capability of creating replicas for services, to improve fault tolerance for your system.
> > Here we only created a basic Swarm deploy, to test the capabilities and evaluate what a distributed ERP could find useful in Swarm. We did not have the necessary time to develop that aspect.

There also is the possibility of not launching Logs/Monitoring stack, by running the deploy script with the option `--no-logs`.

There are multiple scripts you need to have, that are not followed within this repository because of security reasons, but you can find their template in the `./docs/security/templates` directory.

## Requirements

> [!IMPORTANT]
>
> 1. Insure you have :whale: Docker installed. On Unix systems, run `docker --version`.
> 2. Insure you have a :whale: Docker daemon running. On Windows check if :whale: Docker desktop app is running, on Unix systems,run: `sudo systemctl status docker`.
> 3. Insure you have :lock: OpenSSL installed. Run: `openssl --version` or `openssl -v`.
> 4. Insure you have a :coffee: Java JDK installed (we use keytools to generate some trust stores for the application).
>    > Insure you have :lock: keytool installed. Run: `keytool`. If not, download on your system an openjdk17-jre-healess for example.
> 5. Finally, insure you have expect installed. Run: `expect --version`.
>    > If not, download it using your system's package manager. If using a WSL, sometimes `sudo apt-get install expect` can not find the package, you'll have to `sudo apt update && sudo apt upgrade`.
> 6. Insure you have the script `./security/generate_certificate_password.sh`.

## Warning

> [!WARNING]
>
> Having trouble deploying ? Maybe you're entering the good credentials, but still can't access the application ?
>
> > In order to make sure services can communicate, after you just deployed the app, when it's your first time accessing the [frontend](https://localhost:3000/), please accept the prompt to proceed with adding an exception for the site, as its certificates have yet to be validated by a known authority (it costs a certain sum of money). Be careful to do the same with the gateway using this [link](https://localhost:8041/api/authentication/v1/hello), so that the gateway can safely communicate information to the frontend.
>
> The command to make a file executable (in order to execute a .sh script for example), is `chmod +x file_name.sh`.
>
> If you're having a problem with \r files and you're on Windows, I invite you to execute this command: `sed -i 's/\r$//' .\*.sh && sed -i 's/\r$//' .\system\mvnw`.
>
> See [Security Guide](./SYSTEM_SECURITY.md) if you encounter any issues regarding `.jks`, `.p12`, `.crt` or `.pem` files. Often you'll only need to add options `--clean-sec "CA" --sec` to your deployment.

---

# **Compose mode**

> [!IMPORTANT]
>
> - Insure you have the script `./security/docker_secrets_files.sh` :key:.

Execute the script `./security/docker_secrets_files.sh`.
Execute the script `./deploy.sh`, and if it's your first execution, add the `--sec` option to the command, as such: `./deploy.sh --sec`. This will generate all security certificates needed to run the app only if it doesn't detect existing ones.

> [!NOTE]
> If you want to see available options, execute `./deploy.sh --help`.

> [!TIP]
> If you want to renew certificates and already have a deployed instance, or simply already have certificates in your services and want to change them, execute `./deploy.sh --clean-sec "CA" --sec`
>
> If you would prefer to pull images from a docker registry on :whale: [Docker Hub](https://hub.docker.com) instead of building them locally, you can simply add `--pull "registry_or_username/repository"` as well as a version to pull from `--version "X.X.X | latest"`, such as: `./deploy.sh --pull "gachille/erp" --version 1.0.0`.
> If you would like to push you local images to a docker registry on :whale: [Docker Hub](https://hub.docker.com), you can simply add `--push "registry_or_username/repository"`, such as: `./deploy --push "gachille/erp"`. It will automatically tag the images, and push one image with multiple tags, all named after the services, as not to bring chaos to the repository.

---

# **Swarm mode**

> [!IMPORTANT]
>
> - Insure you have the script `./security/docker_secrets.sh` :key:.

Execute the deploy script with the `--swarm` option, as such: `./deploy.sh --swarm`, and if it's your first execution, add the `--sec` option to the command, as such: `./deploy.sh --swarm --sec`. This will generate all security certificates needed to run the app only if it doesn't detect existing ones (see [Security Guide](./security/README.md) if you encounter any issues regarding .jks, .p12, .crt or .pem files).

This is going to activate :ship: Swarm mode :sailboat: for :whale: Docker, and create a node manager. Then it will deploy the services and load balance between them, restart them if they have errors etc.

> [!NOTE]
> If you want to see available options, execute `./deploy.sh --help`.

> [!TIP]
> If you want to renew certificates and already have a deployed instance, or simply already have certificates in your services and want to change them, execute `./deploy.sh --clean-sec "CA" --sec`
>
> If you would prefer to pull images from a docker registry on :whale: [Docker Hub](https://hub.docker.com) instead of building them locally, you can simply add `--pull "registry_or_username/repository"` as well as a version to pull from `--version "X.X.X | latest"`, such as: `./deploy.sh --pull "gachille/erp" --version 1.0.0`.
> If you would like to push you local images to a docker registry on :whale: [Docker Hub](https://hub.docker.com), you can simply add `--push "registry_or_username/repository"`, such as: `./deploy.sh --push "gachille/erp"`. It will automatically tag the images, and push one image with multiple tags, all named after the services, as not to bring chaos to the repository.

---

# :tada: Accessing the application :tada:

> [!CAUTION]
> See [Warning section](#warning) for information about browser certificates, because for the first time ou launch our ERP, you will have to accept 2 certificates as exceptions.

## Frontend

The frontend, our client-side view of the ERP, is located [here](https://localhost:3000/).

![Frontend](./images/front-login.png "Login")

## Logs

The logs and monitoring site, Elastic (our server-side view of all exchanges made in the ERP) is located [here](http://localhost:5601/).

> [!TIP]
> To watch the logs in the best Dashboard there is, click on the Burger Menu on the top left.
> ![Log access Step 1 image](./images/accessing-logs-Step1.png "Step1")
>
> Then Discover.
>
> ![Log access Step 2 image](./images/accessing-logs-Step2.png "Step2")
>
> On the top right, click on "Open".
>
> ![Log access Step 3 image](./images/accessing-logs-Step3.png "Step3")
>
> You'll then find the ERP-Dashboard, that you can customize even more if you'd like.
>
> ![Log access Step 4 image](./images/accessing-logs-Step4.png "Step4")
>
> Here it is all setup
>
> ![Log access All Setup image](./images/accessing-logs-AllSetup.png "All setup !")
>
> A lot more is possible with Elastic stack, such as integrations of agent directly within java code, rabbitMQ server, etc, different metrics...

---

# :recycle: Undeploying the application

Hopefully, things should be running smoothly now. If you want to undeploy the system, execute the `undeploy.sh` script using : `./undepoy.sh` command, this one doesn't have particular options
