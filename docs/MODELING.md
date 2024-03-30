# Simplified modeling report

This report sets out the architecture of this distributed ERP system, 
with a schematic diagram of the various nodes in the system and their respective responsibilities. 
A topology diagram of the message system for internal communications is also provided.

_NOTE: This brief report is not a complete design documentation, 
and is only intended to give an overview of the system as a distributed system._

## Diagrams table of contents

- [System Schematic Diagram](#system-global-functioning)
- [Message System Topology](#message-system-topology)

## System global functioning

This ERP application is based on microservices, each of which is a distinct application with a particular role. 
These microservices each run in a Docker container, and communicate either via the HTTP protocol 
(following the RESTful API model), or via the AMQP protocol (exchanges with RabbitMQ only). 
The diagram below illustrates the infrastructure and the different application technologies of this distributed system, 
at the network node scale.

![See System Schematic Diagram](diagrams/system-schematic-diagram.png)
<p align="center">System Schematic Diagram</p>

In this distributed system, the SPA Web microservice enables the user to access the app functionalities using an HMI. 
It is currently the only front-end service. 
The Gateway API acts as a link between HTTP requests from the Frontend, 
and routes them to the REST API of the appropriate backend microservice: 
**Authentication**, **Directory**, **Order** or **Inventory**.

The authentication service manages user access to the application. 
Using the RBAC pattern, the service generates connection tokens, after a BasicAuth authentication (username, password), 
enabling the client to access the application for 1 hour. After this time, they must reconnect. 
If security updates are carried out by the administrators, the tokens may expire earlier than expected. 
This prevents obsolete access authorisations from being maintained.



## Message system topology
