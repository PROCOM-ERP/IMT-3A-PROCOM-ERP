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
[new feature workflow diagram](contributing-workflow-git-new-feature.png).

### Create new release
When all features are ready to make a stable version, a release is created, following the
[new release workflow diagram](contributing-workflow-git-new-release.png).

### Hotfix production version
If a production stable version has any problem, the `hotfix` branch is created from the `main` to fix it, 
identified with the date (YYYY-MM-DD), and following the 
[hotfix workflow diagram](contributing-workflow-git-hotfix.png).

## Rules and recommendations
This project follows specific rules to ensure code quality and efficient collaboration.
The different workflows are described as sequence diagrams in the [workflows]() directory.
By following these practices, we can ensure that the codebase remains clean and organised,
and that each feature is fully tested before it's deployed.

### Create issue first!
This feature on GitHub allows actors to interact between them by tracking the different events during the project.
An `issue` concerns one `feature`, `release` or `hotfix`,
and has one (or many) actors (developer, reviewer) designated by `assignees`.
Before contributing, make sure there is an `issue` opened with you and your reviewer as `assignees`.

Each issue has to be tagged by labels to understand the context.
They are described in the [labels](https://github.com/BOPS38IMT/IMT-3A-PROCOM-ERP/labels) panel.
The moment to use them is explained in the different [workflows]().
If labels are missing, please contact the GitHub manager on
[Discord](https://discord.com/channels/1157244196332245096/1159476398155640872),
by prefixing the message with the tag `@Github`.

### Branch management
This git repository works as a **MonoRepository** for the project.
It means that a unique git will version all subprojects for each part, even if they are totally independent.
The main branches in this repository are:

- `main`: This branch is used for production-ready code.
  All the code in this branch is deployed to the production environment.
- `hotfix`: This branch is used for fixing critical problems on the `main`.
- `integration`: This branch is used for the integration of new features.
  After a feature is implemented and unit-tested thoroughly, it gets merged into this branch.

Other branches are created depending on what you are doing (see [workflows]()).

### Other good practices
- Always pull the latest changes from the `integration` branch before starting work on a new feature.
- Commit small, atomic changes that implement a single feature or fix a specific problem.
- Write clear, informative commit messages that describe the changes made.
- Always use pull requests to merge changes from a branch to another

### Documentation
In order to maintain consistency and clarity across the project, some documentation standards were adopted.

#### Java documentation
Java code documentation follows [JavaDoc standards](https://developer.atlassian.com/server/confluence/javadoc-standards/)
For instance:

```java
/**
 * Represents a collection of basic math operations.
 * The {@code MathOperation} class provides methods to perform basic arithmetic operations such as addition.
 * This class demonstrates how to document code using Javadoc, including class, fields, methods documentation, constants, and static methods.
 *
 * @since 0.1.0 (2023-11-02)
 * @author Your Name (from 2023-11-02 to 2024-03-31)
 * @version 1.1.0 (2024-03-31)
 */
public class MathOperation {

  /**
   * The default value to be added in operations. This constant demonstrates how to declare and document a constant field in Java.
   *
   * @since 1.1.0
   */
  public static final int DEFAULT_ADD_VALUE = 10;
    
  /**
   * An example field representing a numeric value. This could be used in future operations.
   * It's currently demonstrated for documentation purposes.
   *
   * @since 0.1.0
   */
  private int exampleField;

  /**
   * Gets the current value of the example field.
   * This getter method provides the value of the example field, demonstrating field access through a method.
   *
   * @return the current value of the example field.
   * @since 0.1.0
   */
  public int getExampleField() {
    return this.exampleField;
  }

  /**
   * Sets the example field with a new value.
   * This setter method demonstrates how to document a method that updates a field's value.
   *
   * @param newValue the new value to assign to the example field.
   * @since 0.1.0
   */
  public void setExampleField(int newValue) {
    this.exampleField = newValue;
  }
  
  /**
   * Calculates the sum of two integers.
   * This method provides a quick way to add two integer numbers.
   * It demonstrates the use of conditionals to enforce input constraints and exception handling to indicate errors.
   *
   * @param a the first integer to add. Must be non-negative.
   * @param b the second integer to add. Must be non-negative.
   * @return the sum of {@code a} and {@code b}.
   * @throws IllegalArgumentException if either {@code a} or {@code b} is negative, with a message specifying that both integers must be non-negative.
   * @since 0.1.0
   */
  public int add(int a, int b) throws IllegalArgumentException {
    if (a < 0 || b < 0) {
      throw new IllegalArgumentException("Both integers must be non-negative.");
    }
    return a + b;
  }

  /**
   * Adds the default value ({@value #DEFAULT_ADD_VALUE}) to the given integer.
   * This static method provides an example of using a class constant in arithmetic operations.
   * It demonstrates the utility of static methods in performing operations that don't require an instance of the class.
   *
   * @param a the integer to which the default value is added.
   * @return the sum of {@code a} and {@link #DEFAULT_ADD_VALUE}.
   * @since 1.1.0
   */
  public static int addDefaultValue(int a) {
    return a + DEFAULT_ADD_VALUE;
  }
}
```
