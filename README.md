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
  - Intellij IDEA 2023.1
  - Visual Studio Code
  - NeoVim
- **Programming languages**:
  - HTML5 / CCS3 / JavaScript (ECMA Script TODO)
  - Java 17 or 18
  - SQL (PostgreSQL)
- **Libraries / Frameworks / Platforms**:
  - React JS
  - Spring Boot 3 framework (Java version with Apache Maven 4.0)
  - PostgreSQL
  - Docker
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
│   ├── CODEOWNERS                     detail which team-member has to review which part of the repository changes
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
│   └── databases/                     
│       ├── db-1/
│       │   └── ...
│       └── db-n/
│           └── ...
└── docs/
    └── workflows/
        ├── workflow-git-new-feature.png
        ├── workflow-git-new-release.png
        └── workflow-git-hotfix.png
```

## Organisation functioning
TODO

## Contributing
[CONTRIBUTING.md](CONTRIBUTING.md) file explains how to contribute to the project,
and what are the contributing rules.

## Roadmap
The different steps to follow are described in the [BACKLOG.md](BACKLOG.md) file.
They are assigned by the Product Owner of the project (see [Contributors](#contributors) section)

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
