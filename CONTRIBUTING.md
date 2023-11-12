# Contributing

## Description
This guide explains how to contribute to the project [IMT-3A-PROCOM-ERP](https://github.com/PROCOM-ERP/IMT-3A-PROCOM-ERP),
as a [PROCOM-ERP GitHub organisation](https://github.com/PROCOM-ERP)'s member.

## Metadata
- **Timestamp**: 2023-11-11
- **Last update**: 2023-11-12

## Development environment setup

### Join the organisation
To join the [PROCOM-ERP GitHub organisation](https://github.com/PROCOM-ERP),
you have to accept an invitation from the organisation's administrator.
If you don't receive the invitation yet, please contact the GitHub manager on
[Discord](https://discord.com/channels/1157244196332245096/1159476398155640872),
by prefixing the message with the tag `@Github`.  

Next, create a `Personal Access Token (classic)` with at least `repo`, `workflow`, `write:org`, and `gist` scopes.
You can follow the [official GitHub tutorial](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#creating-a-personal-access-token-classic).
Save it secretly somewhere to use it in the future (e.g., to clone the repository).

### Getting the source code
To get and edit the source code on your own computer, 
import the repository with: `git clone https://github.com/PROCOM-ERP/IMT-3A-PROCOM-ERP.git`. TODO
Choose `Token Authentication` and the previous personal token you just created.

### Install the dependencies
Depending on the part of the project you are working on, 
there is a specific protocol to follow in order to install the related dependencies.

#### Frontend
TODO

#### Backend
[Apache Maven](https://maven.apache.org/index.html) is used as the package manager for the backend part of the project.
Follow those steps to install all the backend dependencies:
1. Download [Apache Maven 3.95](https://maven.apache.org/download.cgi) bin (zip or tar.gz)
2. Follow the [official guide](https://maven.apache.org/install.html) to install it
3. Move to the project root directory in the console
4. Run `mvn dependency:resolve`

## Contributing

### Adding new feature
The **feature-branch workflow** is used to implement new features.
For each new feature, dedicated branches are created, following the
[new feature workflow diagram](docs/workflows/workflow-git-new-feature.png).

### Create new release
When all features are ready to make a stable version, a release is created, following the
[new release workflow diagram](docs/workflows/workflow-git-new-release.png).

### Hotfix production version
If a production stable version has any problem, the `hotfix` branch is created from the `master` to fix it, 
identified with the date (YYYY-MM-DD), and following the 
[hotfix workflow diagram](docs/workflows/workflow-git-hotfix.png).

## Rules and recommandations
This project follows specific rules to ensure code quality and efficient collaboration.
The different workflows are described as sequence diagrams in the [workflows](docs/workflows) directory.
By following these practices, we can ensure that the codebase remains clean and organised,
and that each feature is fully tested before it's deployed.

### Create issue first!
This feature on GitHub allows actors to interact between them by tracking the different events during the project.
An `issue` concerns one `feature`, `release` or `hotfix`,
and has one (or many) actors (developer, reviewer) designated by `assignees`.
Before contributing, make sure there is an `issue` opened with you and your reviewer as `assignees`.

Each issue has to be tagged by labels to understand the context.
They are described in the [labels](https://github.com/BOPS38IMT/IMT-3A-PROCOM-ERP/labels) panel.
The moment to use them is explained in the different [workflows](docs/workflows).
If labels are missing, please contact the GitHub manager on
[Discord](https://discord.com/channels/1157244196332245096/1159476398155640872),
by prefixing the message with the tag `@Github`.

### Format for naming issues, branches, and pull requests
Each action, to realise in the project, is explicitly mentioned in the [backlog](BACKLOG.md), 
and got a unique formatted identifier. 
Each workflow asks to create items with identifier (`{id}`), depending on the [backlog's](BACKLOG.md) action.
Make sure to use the good one.

### Branch management
This git repository works as a **MonoRepository** for the project.
It means that a unique git will version all subprojects for each part, even if they are totally independent.
The main branches in this repository are:

- `master`: This branch is used for production-ready code.
  All the code in this branch is deployed to the production environment.
- `hotfix`: This branch is used for fixing critical problems on the `master`.
- `integration`: This branch is used for the integration of new features.
  After a feature is implemented and unit-tested thoroughly, it gets merged into this branch.

Other branches are created depending on what you are doing (see [workflows](docs/workflows)).

### Other good practices
- Always pull the latest changes from the `integration` branch before starting work on a new feature.
- Commit small, atomic changes that implement a single feature or fix a specific problem.
- Write clear, informative commit messages that describe the changes made.
- Always use pull requests to merge changes from a branch to another

### Documentation
In order to maintain consistency and clarity across the project, some documentation standards were adopted.

#### React documentation
TODO

#### Java documentation
Java code documentation follows [JavaDoc standards](https://developer.atlassian.com/server/confluence/javadoc-standards/)
For instance:
```java
public class MathOperations {
    
    /**
     * Calculates the sum of two integers.
     * This method provides a quick way to add two integer numbers.
     *
     * @param a the first integer to add
     * @param b the second integer to add
     * @return the sum of the two integers
     * @throws IllegalArgumentException if either of the integers is negative
     */
    public int add(int a, int b) throws IllegalArgumentException {
        if (a < 0 || b < 0) {
            throw new IllegalArgumentException("Both integers must be non-negative.");
        }
        return a + b;
    }
    
}
```

## Communicate with the team
To talk with the different project contributors,
a [Discord server](https://discord.com/channels/1157244196332245096)
is available with dedicated channels, depending on the action to realise.
