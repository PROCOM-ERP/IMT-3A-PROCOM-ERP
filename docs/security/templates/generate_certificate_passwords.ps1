param (
    [string]$serviceName
)
if ($serviceName == "CA"){
    $securePassword = ConvertTo-SecureString -String "ENTER_YOUR_CA_PASSWORD" -Force -AsPlainText
} else {
    $securePassword = ConvertTo-SecureString -String "ENTER_YOUR_PASSWORD" -Force -AsPlainText
}

# Output the password for consumption by calling script
$securePassword
