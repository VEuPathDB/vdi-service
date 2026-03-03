package vdi.service.rest.conversion;

import vdi.service.rest.generated.model.DatasetOwnerImpl;
import vdi.service.rest.model.UserDetails;

public class DatasetOwner extends DatasetOwnerImpl {
  public DatasetOwner(UserDetails user) {
    setUserId(user.userID().toLong());
    setFirstName(user.firstName());
    setLastName(user.lastName());
    setEmail(user.email());
    setAffiliation(user.organization());
  }
}
