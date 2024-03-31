# ROADMAP

## Metadata
- **Timestamp**: 2024-03-30
- **Last update**: 2024-03-31
- **Current Version**: 1.1.0

## Upcoming Milestones

### Q2 2024 - CI/CD and Kubernetes Deployment

- **CI/CD Pipeline Development**: a CI/CD pipeline will be implemented to automate the testing, 
  building, and deployment processes.
- **Kubernetes Adaptation**: The system architecture will adapt to support deployment on Kubernetes, 
  providing insights into container orchestration and its importance in distributed systems.

## Next potential steps

### Frontend and Service Expansion

- **UI/UX enhancement**: Harmonising and finalising the site's styling.
- **Inventory Service Frontend**: Design and develop the frontend for the Inventory Service, 
  enhancing the user experience and system functionality.
- **New Services Development**: Begin development on additional modules, such as HR, Accounting, Supply Chain or CRM. 
  This phase will also explore potential services that pose unique distributed system challenges, 
  encouraging contributions that expand the ERP's scope.
  Also optimise interactions with the database by using pagination and Data Projection to retrieve only what is necessary.
- **Database migration**: Add NoSQL databases for OrderService and InventoryService (excluding RBAC tables remaining in SQL).
- **Consensus example**: Implement a consensus mechanism for requesting and retrieving user information in the network.

### Reliability

- **Checking user input in the frontend**: Add a complete check of the forms entered by the user,
  in conjunction with the documentation of the API Rest backend services.
- **Security measures**: Implement CORS configuration, CSRF protection, Jwt token encryption,
  secure Jwt session data and storage, and an API rate limiting system.
- **Backend optimisation**: Study and modify the types of collections returned by GET endpoints, 
  to optimise processing and add sorting to improve readability. 
  Optimise interaction with the database using pagination and Data Projection.
- **AMQP message reliability**: Fine-tuned management of AMQP message processing errors in Spring, 
  including the use of dead-letter queues. An example is available for `roles.init` in the authentication service.
- **Declaration of AMQP item names**: Harmonise and add the names of RabbitMQ elements (queues, patterns, exchanges), 
  to environment variables (.env) and then inject them into the Java code.
- **Service Discovery**: Set up a Service Discovery to detect the services up, 
  and manage interaction with the frontend for dynamic display of available service.

### Documentation

- **Code documentation**: Completing and generating the Javadoc for each service. 
  Introduce a documentation standard for the Frontend and comment on the code with.
- **UML Diagrams**: Formalise UML class and sequence diagrams from existing code (React, Java, SQL).
- **System distribution problematics**: Carry out an in-depth analysis of the consequences of distributing this system, 
  and document the problems with potential solutions, including design choices.

### Educational Content Enhancement

- **Practical Session Creation**: Compile scenarios from the USAGE.md documentation into practical sessions. 
  These sessions will be designed to challenge students to address and solve distributed system issues, 
  enhancing their learning experience.

## How to Contribute

We welcome contributions from students, educators, and professionals. Whether it's by developing new features, 
enhancing existing ones, or creating educational content, your input is valuable. 
Please refer to our [CONTRIBUTING.md](./contributing/CONTRIBUTING.md) guide for more details on how to get involved.

## Suggestions

We are always looking for new ideas to improve both the ERP system and its educational utility. 
If you have suggestions for new features or educational content, please submit them as a Feature Request Issue on our [GitHub repository](https://github.com/PROCOM-ERP/IMT-3A-PROCOM-ERP/issues).
