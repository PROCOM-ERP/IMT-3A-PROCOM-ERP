# Use our application as an educational model

Once it has been deployed, see [DEPLOYING.md](DEPLOYING.md), you can use the application freely, 
or follow the typical usage scenarios below, which illustrate the concepts of distributed systems.
Application databases are filled to simulate a textile company named "Text Ile", with 30 employees, 
3 of whom are administrators.
By un-deploying and deploying the app again, 
databases are restored to the with the minimum required setup for scenarios.

_NOTE_: The Admin panel is accessible through the /admin url extension (e.g., https://domain.name/admin).
Domain name is given at the end of the execution of the deployment script.

## Scenarios table of contents

- [User access management](#user-access-management)
- [Adding and modifying a user profile](#adding-and-modifying-a-user-profile)
- [Adding and modifying a role](#adding-and-modifying-a-role)
- [Placing and viewing an order](#placing-and-viewing-an-order)

## User access management

### Scenario description

**As Maxime Durand** (Admin **A00004**) **I will** change **Mathieu Faure** (**A00020**) roles, 
and deactivate **Elise Morin** (**A00025**) user profile.

### Steps

1. **Maxime Durand** log in (**Mathieu Faure** and **Elise Morin** also do this in parallel on another browser).
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

## Adding and modifying a role

### Scenario description

**As Maxime Durand** (Admin **A00004**) **I will** create the **approver** role in the **order service**,
to allow **Gabriel Roux** (**A00016**) to approve orders (**CanModifyOrder**).

### Steps

1. **Maxime Durand** log in.
2. **Maxime Durand** create the **approver** role.
   🐳 ***Shutdown and restart of the order service in the meantime.*** 🐳
   📋 ***Consult the logs dashboard to see the message exchanges in the network, 
   aimed at creating the new role in the services.*** 📋
3. **Maxime Durand** activate the **approver** role in the **order service**.
   🐳 ***Shutdown and restart of the authentication service in the meantime.*** 🐳
   📋 ***Consult the logs dashboard to see the message exchanges in the network,
   aimed at activating the new role.***  📋
4. **Maxime Durand** add the **CanModifyOrder** permission to the **approver** role in the **order service**.
5. **Maxime Durand** add the **approver** role to **Gabriel Roux**, in order to allow him to approve orders.
   📋 ***Check the log dashboard to see when the old tokens for A00016 expire.*** 📋

## Placing and viewing an order

### Scenario description

**As Anaïs Robin** (**A00023**) **I will** create a first order, view it, 
and **Gabriel Roux** (**A00016**) will approve it.
**Then, as Anaïs Robin** (Admin **A00023**) **I will** create a second order, but I will be the approver this time.

### Steps

1. **Anaïs Robin** log in (**Gabriel Roux** also does this in parallel on another browser).
2. **Anaïs Robin** creates a new order with **Wool Supplier Co.**,
   who have given her the quote **WSC12345** to order **800** units of **Wool Ball** at €5.50**, 
   and **500** units of **Cotton Ball** at €3.00**. 
   She will also enter her personal details (**Robin, Anaïs, anais.robin@example.com, +33 2 89 90 91 92**), 
   and the delivery address (**180, Kerlaurent, Guipavas, France, 29490, Bretagne**).
   📋***Consult the dashboard of logs to see the exchanges used to recover the manager of A00023, 
   so that he becomes the approver of the newly created order.*** 📋
3. **Anaïs Robin** consults her orders and sees the one that has just been created.
4. **Gabriel Roux** approves the commissioning of **Anaïs Robin**.
5. **Anaïs Robin** creates a new order with **Wool Supplier Co.**, 
   who has given her the quote **WSC67890** to order **200** units of **knitting for silk** at €8.99**. 
   This time, **Anaïs Robin** does not need to enter her details as they are automatically filled in.
   🐳 ***Shutdown of the directory service in the meantime.*** 🐳
   📋***Check the dashboard logs to see the exchanges used to recover the manager of A00023, 
   so that he becomes the approver of the newly created command, but which fails.*** 📋
6. **Anaïs Robin** consults her orders and sees the one that has just been created, 
   but realises that this time she can approve it herself.
7. **Anaïs Robin** approves the first stage of her own order.
8. **Gabriel Roux** becomes the approver of the second **Anaïs Robin** order, 
   and approves the second stage of her order.
   🐳 ***Restart of the directory service in the meantime.*** 🐳
   📋***Check the dashboard logs to see the exchanges used to recover the manager of A00023,
   so that he becomes the approver of the user A00023 command, but which succeed this time.*** 📋
