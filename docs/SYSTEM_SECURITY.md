# Security

Here is a quick guide focusing on security, encryption, etc., within our modular ERP project. Intended for stakeholders and project members, not for end-users.

### Prerequisites

- Ensure that **OpenSSL** is installed on your system for managing SSL certificates. Verify on your machine by trying to run `openssl --version` or `openssl -v`.
- Ensure that **keytool** (a pre-installed tool by Java) is installed on your system for managing SSL certificates. Verify on your machine by trying to run `keytool`. Otherwise, install a JDK, such as openjdk-17-jre-headless.
- Ensure you have the `generate_certificate_passwords.sh` script (or `generate_certificate_passwords.ps1` on Windows) present in the `./security` directory. It is essential for using the `security_setup.sh` script.

### Using `security_setup.sh`

This script generates (if not already present) a key for the CA, a certificate, and a trust store. Then, a key and a certificate (signed by the CA) for each requested service. It distributes the trust stores (`.p12` files) to each service (each service gets its trust store only), and the CA's trust store (`.jks` file in the system directory, which is passed to all services at startup). It then places all keys and certificates, as well as certificate signing requests (`.key`, `.crt`, `.csr` files) in directories created for the occasion within the `./security/` directory.

1. **Launching the Script**:

   - Execute the script with the command: `./security/security_setup.sh`. It is automatically launched by the deployment script if the `--sec` option is used.

2. **Generating Certificates**:

   - The script will generate certificates for the specified services.
   - It will create keys, certificate signing requests (CSRs), and sign these CSRs.

3. **Configuring Keystores**:
   - The script also takes care of creating the necessary keystores for the services, as well as their distribution across all services in your `./src/backend` and `./src/frontend` folders.

### Using `clean_security.sh`

This script does the opposite of the first one and tries to find keys, certificates, signing requests, trust stores, etc., in the places where they are supposed to be based on the number of services and their names (same prompt as the previous script). It will move everything to a folder `./security/archives/Y-M-D-h-m`, and if the `--CA` option is selected, then the CA's keys and certificates will also be archived (one might want to simply redo the service certificates while keeping the same CA).

- This script is used to clean or reset configurations established by `security_setup.sh`.
- To use it, execute: `./security/clean_security.sh`.
- A `--CA` option is available to also archive the CA's keys and trust store.

For executing the PowerShell script `./security/clean_security.ps1` on Windows, the option to enter is -IncludeCA

### Using `docker_secrets.sh`

This script generates locally the necessary secrets for deploying the application. There are no specific options. For obvious security reasons, it is not managed in this git repository.

### Using `docker_secrets_files.sh`

This script generates locally the secret files necessary for deploying the application, within the `./security/secrets/` folder. There are no specific options. For obvious security reasons, it is not managed in this git repository.
