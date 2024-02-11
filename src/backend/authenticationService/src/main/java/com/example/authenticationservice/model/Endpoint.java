package com.example.authenticationservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

@AllArgsConstructor
@Getter
public enum Endpoint {

  GET_V1_LOGIN_PROFILES(HttpMethod.GET,
          Path.V1_LOGIN_PROFILES,
          Permission.CanReadLoginProfile),
  GET_V1_LOGIN_PROFILES_ID(HttpMethod.GET,
          Path.V1_LOGIN_PROFILES_ANYONE,
          Permission.CanReadLoginProfile),
  GET_V1_LOGIN_PROFILES_ID_ACTIVATION(HttpMethod.GET,
          Path.V1_LOGIN_PROFILES_ANYONE_ACTIVATION,
          Permission.CanReadLoginProfile),
  POST_V1_LOGIN_PROFILES(HttpMethod.POST,
          Path.V1_LOGIN_PROFILES,
          Permission.CanCreateLoginProfile),
  PATCH_V1_LOGIN_PROFILES_ROLES(HttpMethod.PATCH,
          Path.V1_LOGIN_PROFILES_ANYONE_ROLES,
          Permission.CanModifyLoginProfileRoles),
  PATCH_V1_LOGIN_PROFILES_PASSWORD(HttpMethod.PATCH,
          Path.V1_LOGIN_PROFILES_ANYONE_PASSWORD,
          Permission.CanModifyLoginProfilePassword),
  PATCH_V1_LOGIN_PROFILES_ACTIVATION(HttpMethod.PATCH,
          Path.V1_LOGIN_PROFILES_ANYONE_ACTIVATION,
          Permission.CanDeactivateLoginProfile),
  PATCH_V1_LOGIN_PROFILES_EMAIL(HttpMethod.PATCH,
          Path.V1_LOGIN_PROFILES_ANYONE_EMAIL,
          Permission.CanModifyLoginProfileEmail),

  GET_V1_ROLES(HttpMethod.GET, Path.V1_ROLES, Permission.CanReadRole),
  GET_V1_ROLES_ID(HttpMethod.GET, Path.V1_ROLES_ANYONE, Permission.CanReadRole),
  GET_V1_ROLES_MICROSERVICES(HttpMethod.GET, Path.V1_ROLES_MICROSERVICES, Permission.CanReadRole),
  GET_V1_ROLES_ANYTHING(HttpMethod.GET, Path.V1_ROLES_ANYTHING, Permission.CanReadRole),
  POST_V1_ROLES(HttpMethod.POST, Path.V1_ROLES, Permission.CanCreateRole),
  PUT_V1_ROLES_ID(HttpMethod.PUT, Path.V1_ROLES_ANYONE, Permission.CanModifyRole),

  GET_V1_HELLO(HttpMethod.GET, Path.V1_HELLO, null),

  GET_V1_DOCS(HttpMethod.GET, Path.V1_DOCS_ANYTHING, null);

  private final HttpMethod httpMethod;
  private final String path;
  private final Permission permission;
}
