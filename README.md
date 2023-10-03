# IMT-3A-PROCOM-ERP

## Recommandations

### Git Workflow TODO
This project follows a specific Git workflow to ensure code quality and efficient collaboration.
The main branches in this repository are:

- **master**: This branch is for production-ready code.
  All the code in this branch is deployed to the production environment.
- **development**: This branch is for the integration of new features.
  After a feature is implemented and tested thoroughly, it gets merged into this branch.

For the development of new features, we follow a feature-branch workflow:

1. For each new feature, a new branch is created from the 'development' branch, named depending on the feature.
2. The feature is developed on this new branch.
3. Unit tests are written and run to ensure that the feature works as expected.
4. Once the feature is tested and ready, it gets merged back into the 'development' branch.
5. Integration tests are run on the 'development' branch.
6. If all tests pass, the 'development' branch gets merged into the 'master' branch, and the feature is deployed to production.

We also encourage the following best practices:

- Always pull the latest changes from the 'development' branch before starting work on a new feature.
- Commit small, atomic changes that implement a single feature or fix a specific issue.
- Write clear, informative commit messages that describe the changes made.
- When you're ready to merge your feature branch back into the 'development' branch, use a pull request so that your code can be reviewed by others.

By following these practices, we can ensure that our codebase remains clean and organised,
and that each feature is fully tested before it's deployed.

### Documentation
In order to maintain consistency and clarity across the project, we have adopted ...

[Here]() is an example of documentation.

```
```

### Updating
When you update anything in the project, it is recommended to update :
- All [README.md](README.md) sections impacted by new changes
- [requirements.txt](requirements.txt)when you add new librairies or
  change current ones.

### Adding features


## Licences
This project is the exclusive property of [...](). All rights reserved.  
The source code and associated documentation of this project cannot be copied, reproduced, modified, posted, or
redistributed without explicit permission from [...]().  
Ask **...** for more details.

The [...]() code from [...](...) that we used in this
project is licensed under the license ....  
The terms of this licence can be found on [...]().

## Acknowledgements
Many thanks to the different contributors for their contribution to the project.
Find their names and missions in [Contributors](#contributors) section.

## Contributors
- Thibaut RUZICKA: *Role*
- Gaël CUDENNEC: *Role*
- Antoine DAGORN: *Role*
- Arthur MAQUIN: *Role*
- Aïna DIROU: *Role*
