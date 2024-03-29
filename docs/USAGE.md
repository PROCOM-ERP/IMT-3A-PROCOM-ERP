# Use our application as an educational model

Once it has been deployed, see [DEPLOYING.md](DEPLOYING.md), you can use the application freely, 
or follow the typical usage scenarios below, which illustrate the concepts of distributed systems.

## User access management

### Scenario description

**As Maxime Durand** (Admin **A00004**) **I will** change **Mathieu Faure** (**A00020**) roles, 
and deactivate **Elise Morin** (**A00025**) user profile.

### Steps

1. **Maxime Durand** log in (**Mathieu Faure** and **Elise Morin** also does this in parallel on another browser).
2. **Maxime Durand** change **Mathieu Faure** roles to prevent him from placing orders.
   📋 ***Check the log dashboard to see when the old tokens for A00020 expire.*** 📋
3. Try to create an order with **Mathieu Faure** by clicking on the `new order` button. 
   It will not work, and you will be redirected on the `log in` page.
4. **Maxime Durand** deactivate the **Elise Morin** user profile to no longer allow her access to the ERP.
   🐳 ***Shutdown and restart of the directory service in the meantime.*** 🐳
   📋 ***Check the log dashboard to see if A00025 has been deactivated once the directory has been restarted.*** 📋
5. **Elise Morin** tries to consult the directory but is redirected to the authentication page.
6. Authentication impossible for **Elise Morin**.

## Adding and modifying a user profile

### Scenario description

**As Maxime Durand** (Admin **A00004**) **I will** create a new user (**Jean Bonboeur A00031**).
**Then, as Jean Bonboeur** (**A00031**) **I will** change my password and personal details.

### Steps

1. **Maxime Durand** log in.
2. **Maxime Durand** creates the new user **Jean Bonboeur** (**Bonboeur, Jean, jean.bonboeur@example.com, 
   +33 2 21 22 23 24, Supply Chain Analyst, Texte Ile, Logistics**).
   📋 ***Check the log dashboard to see the login and password of the new user A00031.*** 📋
3. **Jean Bonboeur** changes his password.
   🐳 ***Shutdown and restart of the directory service in the meantime.*** 🐳
   📋 ***Consult the logs dashboard to see the exchanges of messages in the network, 
   aimed at causing the new user's token to expire A00031.*** 📋
4. **Jean Bonboeur** is redirected to the `log in` page to log in again.
5. **Jean Bonboeur** update his email address.
   📋 ***Check the logs dashboard to see the messages exchanged in the network, 
   to change the email address of new user A00031.*** 📋