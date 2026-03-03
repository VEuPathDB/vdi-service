package vdi.service.rest.model;

import vdi.model.meta.UserID;

public record UserDetails(
  UserID userID,
  String firstName,
  String lastName,
  String email,
  String organization
) {
}
