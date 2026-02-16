package vdi.service.rest.conversion;

import vdi.service.rest.generated.model.DatasetOwnerImpl;
import vdi.service.rest.model.UserDetails;

public class DatasetOwner extends DatasetOwnerImpl {
  public DatasetOwner(UserDetails user) {
    setUserId(user.getUserID().toLong());
    setFirstName(user.getFirstName());
    setLastName(user.getLastName());
    setEmail(user.getEmail());
    setAffiliation(user.getOrganization());
  }
}
