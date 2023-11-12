# IMT-3A-PROCOM-ERP

## Description
TODO

## Metadata
- **Timestamp**: 2023-11-02
- **Last update**: 2023-11-11
- **Status**: In development
- **Current Version**: 0.0.1-SNAPSHOT
- **Supported Platforms**: Windows (Linux and macOS untested)
- **Development Environment**:
  - [Intellij IDEA 2023.1](https://www.jetbrains.com/idea/)
  - [Visual Studio Code](https://code.visualstudio.com)
  - [Neovim](https://neovim.io)
- **Programming languages**:
  - HTML5 / CCS3 / JavaScript
  - Java 17 or 18
  - SQL (PostgreSQL 13)
- **Libraries / Frameworks / Platforms**:
  - [NPM](https://www.npmjs.com)
  - [React JS](https://fr.legacy.reactjs.org)
  - [Apache Maven](https://maven.apache.org)
  - [Spring Boot 3](https://spring.io/projects/spring-boot)
  - [PostgreSQL](https://www.postgresql.org)
  - [Docker](https://www.docker.com)
  - [Postman](https://www.postman.com)
- **Compatibility Issues** : None known at this time

## Repository architecture

```
IMT-3A-PROCOM-ERP/                     this repository
├── README.md                          this document
├── BACKLOG.md                         actions to realise (features, releases, fixes, etc)
├── CONTRIBUTING.md                    explain how to contribute to the project, by respecting some rules
├── CHANGELOG.md                       tracked changes in the project's lifecycle
├── docker-compose.yml                 Docker containers build script to simulate the project
├── pom.xml                            Maven parent configuration file for all backend services (modules)
├── .env                               environment variables of the projet
├── .github/                           used by GitHub for CI/CD processes
│   ├── CODEOWNERS                     detail which team-member has to review which changes
│   └── workflows/                     all CI/CD auto-running tasks depending on the triggered event
│       └── ...
├── src/                               all the source code of the project
│   ├── frontend/                      code for the UI
│   │   └── react-project/             subproject with frontend code
│   ├── backend/                       code for microservices
│   │   ├── service-1/                 one service
│   │   │   ├── Dockerfile             Docker container for the service
│   │   │   ├── pom.xml                Maven child configuration file for the service
│   │   │   ├── src/                   backend source code
│   │   │   └── ...
│   │   └── service-n/                 another service
│   │       └── ...
│   └── databases/                     databases scripts and configurations
│       ├── db-1/                      one database
│       │   ├── Dockerfile             Docker container for the database if required
│       │   ├── reset.sql              script to reset all tables in a relationnal database
│       │   └── init.sql               script to create tables in a relationnal database
│       └── db-n/                      another database
│           └── ...
└── docs/                              all technical and GitHub workflows documentation
    └── workflows/                     GitHub workflows as sequence diagrams
        └── workflow-git-some-action.png         sequence diagram for a specific action to realise
```

## Organisation functioning
The [PROCOM-ERP GitHub organisation](https://github.com/PROCOM-ERP) owns this repository,
meaning that you have to be a member to contribute.
In the organisation, several teams are created in order to affect responsibility, like code review, to members.
They are used in this project in the [CODEOWNERS](.github/CODEOWNERS) file,
to auto-create review requirement when changes impact specific parts of the repository.
For instance: if `backend-owner` team owns `src/backend/`,
any changes in this directory will need a review acceptation from a member of the team before being merged.

## Contributing
[CONTRIBUTING.md](CONTRIBUTING.md) file explains how to contribute to the project,
and what are the contributing rules.

## Roadmap
The different steps to follow are described in the [BACKLOG.md](BACKLOG.md) file.
They are assigned by the Product Owner of the project (see [Contributors](#contributors) section).

## Suggestions
Any enhancement idea can be suggested in the [Suggestions](BACKLOG.md#suggestions) section of the BACKLOG file.

## Licences
NA

## Acknowledgements
Many thanks to the different contributors for their contribution to the project.
Find their names and missions in [Contributors](#contributors) section.

## Contributors
- Thibaut RUZICKA: *Role* TODO
- Gaël CUDENNEC: *Role* TODO
- Antoine DAGORN: *Role* TODO
- Arthur MAQUIN: *Role* TODO
- Aïna DIROU: *Role* TODO
