package com.odradek.keycloak.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.keycloak.representations.AccessToken;

@Getter
@Setter
public class OdradekUser extends AccessToken {

  @JsonProperty("companyId")
  private String companyId;

  @JsonProperty("companyName")
  private String companyName;

  public boolean isServiceAccount() {
    return this.otherClaims.containsKey("clientId");
  }

  public String getServiceAccountId() {
    Object serviceAccountId = this.getOtherClaims().get("clientId");
    if (serviceAccountId == null) return null;
    return serviceAccountId.toString();
  }
}
