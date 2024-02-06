@echo off
setlocal enabledelayedexpansion

:: Subroutine to create Docker secret or skip if it already exists
:create_secret
set "secret_name=%1"
set "secret_value=%2"

:: Check if the secret already exists
docker secret ls | findstr /C:"%secret_name%" >nul
if errorlevel 1 (
    :: Create the secret
    echo|set /p="%secret_value%" | docker secret create %secret_name% -
    echo Secret '%secret_name%' created.
) else (
    echo Secret '%secret_name%' already exists. Skipping...
)
goto :eof

:: Main script starts here
:: Example usage of create_secret subroutine for a single secret
call :create_secret "SECURITY_SERVICES_SHARED_KEY" "MpoUIF+TE2QFX88Z3lliMeqqIX++xABtsIVJwTqE0fs="
call :create_secret "SECURITY_JWT_SECRET_KEY" "+w5PL27Cnf+TvmwrY/adImJGqrA8qSDZwvwiyB6fTt8="
call :create_secret "SECURITY_JWT_CLAIMS_KEY_ROLES" "jwtClaimRoles"
call :create_secret "SECURITY_TRUST_STORE_PASSWORD" "super-secure-password-for-trust-store"
call :create_secret "SECURITY_TRUST_STORE_ALIAS" "ca_cert"
call :create_secret "DB_LOCAL_TEST_SERVICE_USER" "pguser"
call :create_secret "DB_LOCAL_TEST_SERVICE_PASSWORD" "pgpwd"
call :create_secret "DB_AUTHENTICATION_SERVICE_USER" "pguser"
call :create_secret "DB_AUTHENTICATION_SERVICE_PASSWORD" "pgpwd"
call :create_secret "DB_DIRECTORY_SERVICE_USER" "pguser"
call :create_secret "DB_DIRECTORY_SERVICE_PASSWORD" "pgpwd"
call :create_secret "BACKEND_MESSAGE_BROKER_SERVICE_USERNAME" "rabbit_guest"
call :create_secret "BACKEND_MESSAGE_BROKER_SERVICE_PASSWORD" "rabbit_guest_password"
call :create_secret "BACKEND_AUTHENTICATION_SERVICE_HTTPS_KEY_STORE_ALIAS" "authentication"
call :create_secret "BACKEND_AUTHENTICATION_SERVICE_HTTPS_KEY_STORE_PASSWORD" "procom-erp-authentication-service-secure-keystore"
call :create_secret "BACKEND_GATEWAY_SERVICE_HTTPS_KEY_STORE_ALIAS" "gateway"
call :create_secret "BACKEND_GATEWAY_SERVICE_HTTPS_KEY_STORE_PASSWORD" "procom-erp-gateway-service-secure-keystore"
call :create_secret "BACKEND_MESSAGE_BROKER_SERVICE_HTTPS_KEY_STORE_ALIAS" "message-broker"
call :create_secret "BACKEND_MESSAGE_BROKER_SERVICE_HTTPS_KEY_STORE_PASSWORD" "procom-erp-message-broker-service-secure-keystore"
call :create_secret "BACKEND_DIRECTORY_SERVICE_HTTPS_KEY_STORE_ALIAS" "directory"
call :create_secret "BACKEND_DIRECTORY_SERVICE_HTTPS_KEY_STORE_PASSWORD" "procom-erp-directory-service-secure-keystore"

endlocal
