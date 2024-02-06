package com.example.authenticationservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

@AllArgsConstructor
@Getter
public enum Endpoint {

  GET_V1_LOGIN_PROFILES(HttpMethod.GET,
          Path.V1_LOGIN_PROFILES,
          Permission.CanReadEmployee),
  GET_V1_LOGIN_PROFILES_ID(HttpMethod.GET,
          Path.V1_LOGIN_PROFILES_ANYONE,
          Permission.CanReadEmployee),
  GET_V1_LOGIN_PROFILES_ID_ENABLE(HttpMethod.GET,
          Path.V1_LOGIN_PROFILES_ANYONE_ENABLE,
          Permission.CanReadEmployee),
  POST_V1_LOGIN_PROFILES(HttpMethod.POST,
          Path.V1_LOGIN_PROFILES,
          Permission.CanCreateEmployee),
  PATCH_V1_LOGIN_PROFILES_ROLES(HttpMethod.PATCH,
          Path.V1_LOGIN_PROFILES_ANYONE_ROLES,
          Permission.CanModifyEmployeeRoles),
  PATCH_V1_LOGIN_PROFILES_PASSWORD(HttpMethod.PATCH,
          Path.V1_LOGIN_PROFILES_ANYONE_PASSWORD,
          Permission.CanModifyEmployeePassword),
  PATCH_V1_LOGIN_PROFILES_ENABLE(HttpMethod.PATCH,
          Path.V1_LOGIN_PROFILES_ANYONE_ENABLE,
          Permission.CanDeactivateEmployee),
  PATCH_V1_LOGIN_PROFILES_EMAIL(HttpMethod.PATCH,
          Path.V1_LOGIN_PROFILES_ANYONE_EMAIL,
          Permission.CanModifyEmployeeEmail),

  GET_V1_ROLES(HttpMethod.GET, Path.V1_ROLES, Permission.CanReadRole),
  GET_V1_ROLES_ID(HttpMethod.GET, Path.V1_ROLES_ANYONE, Permission.CanReadRole),
  POST_V1_ROLES(HttpMethod.POST, Path.V1_ROLES, Permission.CanCreateRole),
  PATCH_V1_ROLES_PERMISSIONS(HttpMethod.PATCH, Path.V1_ROLES_ANYONE_PERMISSIONS,
          Permission.CanModifyRolePermissions),
  PATCH_V1_ROLES_ENABLE(HttpMethod.PATCH, Path.V1_ROLES_ANYONE_ENABLE,
          Permission.CanDeactivateRole),

  GET_V1_PERMISSIONS(HttpMethod.GET, Path.V1_PERMISSIONS,
          Permission.CanReadPermission),

  GET_V1_HELLO(HttpMethod.GET, Path.V1_HELLO, null),
  GET_V1_RABBITHELLO(HttpMethod.GET, Path.V1_RABBITHELLO, null),

  GET_V1_DOCS(HttpMethod.GET, Path.V1_DOCS_ANYTHING, null);

  private final HttpMethod httpMethod;
  private final String path;
  private final Permission permission;
}
