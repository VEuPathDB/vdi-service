package vdi.service.rest.model;

import org.gusdb.oauth2.client.veupathdb.UserInfo;
import vdi.model.meta.UserID;

public record UserDetails(
  UserID userID,
  String firstName,
  String lastName,
  String email,
  String organization
) {
  public UserDetails(UserInfo info) {
    this(
      UserID.newUserID(info.getUserId()),
      info.getFirstName(),
      info.getLastName(),
      info.getEmail(),
      info.getOrganization()
    );
  }
}
