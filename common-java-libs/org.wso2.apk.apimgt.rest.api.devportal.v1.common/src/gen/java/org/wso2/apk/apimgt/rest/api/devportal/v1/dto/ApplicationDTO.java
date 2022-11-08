package org.wso2.apk.apimgt.rest.api.devportal.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.wso2.apk.apimgt.rest.api.devportal.v1.dto.ApplicationKeyDTO;
import org.wso2.apk.apimgt.rest.api.devportal.v1.dto.ScopeInfoDTO;
import javax.validation.constraints.*;


import io.swagger.annotations.*;
import java.util.Objects;



public class ApplicationDTO   {
  
  private String applicationId;

  private String name;

  private String throttlingPolicy;

  private String description;


public enum TokenTypeEnum {

    OAUTH(String.valueOf("OAUTH")), JWT(String.valueOf("JWT"));


    private String value;

    TokenTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static TokenTypeEnum fromValue(String value) {
        for (TokenTypeEnum b : TokenTypeEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

  private TokenTypeEnum tokenType = TokenTypeEnum.JWT;

  private String status = "";

  private List<String> groups = null;

  private Integer subscriptionCount;

  private List<ApplicationKeyDTO> keys = null;

  private Map<String, String> attributes = null;

  private List<ScopeInfoDTO> subscriptionScopes = null;

  private String owner;

  private Boolean hashEnabled;

  private String createdTime;

  private String updatedTime;


  /**
   **/
  public ApplicationDTO applicationId(String applicationId) {
    this.applicationId = applicationId;
    return this;
  }

  
  @ApiModelProperty(example = "01234567-0123-0123-0123-012345678901", value = "")
  @JsonProperty("applicationId")
  public String getApplicationId() {
    return applicationId;
  }
  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }


  /**
   **/
  public ApplicationDTO name(String name) {
    this.name = name;
    return this;
  }

  
  @ApiModelProperty(example = "CalculatorApp", required = true, value = "")
  @JsonProperty("name")
  @NotNull
 @Size(min=1,max=100)  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }


  /**
   **/
  public ApplicationDTO throttlingPolicy(String throttlingPolicy) {
    this.throttlingPolicy = throttlingPolicy;
    return this;
  }

  
  @ApiModelProperty(example = "Unlimited", required = true, value = "")
  @JsonProperty("throttlingPolicy")
  @NotNull
 @Size(min=1)  public String getThrottlingPolicy() {
    return throttlingPolicy;
  }
  public void setThrottlingPolicy(String throttlingPolicy) {
    this.throttlingPolicy = throttlingPolicy;
  }


  /**
   **/
  public ApplicationDTO description(String description) {
    this.description = description;
    return this;
  }

  
  @ApiModelProperty(example = "Sample calculator application", value = "")
  @JsonProperty("description")
 @Size(max=512)  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }


  /**
   * Type of the access token generated for this application.  **OAUTH:** A UUID based access token **JWT:** A self-contained, signed JWT based access token which is issued by default. 
   **/
  public ApplicationDTO tokenType(TokenTypeEnum tokenType) {
    this.tokenType = tokenType;
    return this;
  }

  
  @ApiModelProperty(example = "JWT", value = "Type of the access token generated for this application.  **OAUTH:** A UUID based access token **JWT:** A self-contained, signed JWT based access token which is issued by default. ")
  @JsonProperty("tokenType")
  public TokenTypeEnum getTokenType() {
    return tokenType;
  }
  public void setTokenType(TokenTypeEnum tokenType) {
    this.tokenType = tokenType;
  }


  /**
   **/
  public ApplicationDTO status(String status) {
    this.status = status;
    return this;
  }

  
  @ApiModelProperty(example = "APPROVED", value = "")
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }
  public void setStatus(String status) {
    this.status = status;
  }


  /**
   **/
  public ApplicationDTO groups(List<String> groups) {
    this.groups = groups;
    return this;
  }

  
  @ApiModelProperty(example = "[]", value = "")
  @JsonProperty("groups")
  public List<String> getGroups() {
    return groups;
  }
  public void setGroups(List<String> groups) {
    this.groups = groups;
  }

  public ApplicationDTO addGroupsItem(String groupsItem) {
    if (this.groups == null) {
      this.groups = new ArrayList<>();
    }
    this.groups.add(groupsItem);
    return this;
  }


  /**
   **/
  public ApplicationDTO subscriptionCount(Integer subscriptionCount) {
    this.subscriptionCount = subscriptionCount;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("subscriptionCount")
  public Integer getSubscriptionCount() {
    return subscriptionCount;
  }
  public void setSubscriptionCount(Integer subscriptionCount) {
    this.subscriptionCount = subscriptionCount;
  }


  /**
   **/
  public ApplicationDTO keys(List<ApplicationKeyDTO> keys) {
    this.keys = keys;
    return this;
  }

  
  @ApiModelProperty(example = "[]", value = "")
  @JsonProperty("keys")
  public List<ApplicationKeyDTO> getKeys() {
    return keys;
  }
  public void setKeys(List<ApplicationKeyDTO> keys) {
    this.keys = keys;
  }

  public ApplicationDTO addKeysItem(ApplicationKeyDTO keysItem) {
    if (this.keys == null) {
      this.keys = new ArrayList<>();
    }
    this.keys.add(keysItem);
    return this;
  }


  /**
   **/
  public ApplicationDTO attributes(Map<String, String> attributes) {
    this.attributes = attributes;
    return this;
  }

  
  @ApiModelProperty(example = "{}", value = "")
  @JsonProperty("attributes")
  public Map<String, String> getAttributes() {
    return attributes;
  }
  public void setAttributes(Map<String, String> attributes) {
    this.attributes = attributes;
  }


  public ApplicationDTO putAttributesItem(String key, String attributesItem) {
    if (this.attributes == null) {
      this.attributes = new HashMap<>();
    }
    this.attributes.put(key, attributesItem);
    return this;
  }

  /**
   **/
  public ApplicationDTO subscriptionScopes(List<ScopeInfoDTO> subscriptionScopes) {
    this.subscriptionScopes = subscriptionScopes;
    return this;
  }

  
  @ApiModelProperty(example = "[]", value = "")
  @JsonProperty("subscriptionScopes")
  public List<ScopeInfoDTO> getSubscriptionScopes() {
    return subscriptionScopes;
  }
  public void setSubscriptionScopes(List<ScopeInfoDTO> subscriptionScopes) {
    this.subscriptionScopes = subscriptionScopes;
  }

  public ApplicationDTO addSubscriptionScopesItem(ScopeInfoDTO subscriptionScopesItem) {
    if (this.subscriptionScopes == null) {
      this.subscriptionScopes = new ArrayList<>();
    }
    this.subscriptionScopes.add(subscriptionScopesItem);
    return this;
  }


  /**
   * Application created user 
   **/
  public ApplicationDTO owner(String owner) {
    this.owner = owner;
    return this;
  }

  
  @ApiModelProperty(example = "admin", value = "Application created user ")
  @JsonProperty("owner")
  public String getOwner() {
    return owner;
  }
  public void setOwner(String owner) {
    this.owner = owner;
  }


  /**
   **/
  public ApplicationDTO hashEnabled(Boolean hashEnabled) {
    this.hashEnabled = hashEnabled;
    return this;
  }

  
  @ApiModelProperty(example = "false", value = "")
  @JsonProperty("hashEnabled")
  public Boolean getHashEnabled() {
    return hashEnabled;
  }
  public void setHashEnabled(Boolean hashEnabled) {
    this.hashEnabled = hashEnabled;
  }


  /**
   **/
  public ApplicationDTO createdTime(String createdTime) {
    this.createdTime = createdTime;
    return this;
  }

  
  @ApiModelProperty(example = "1651555310208", value = "")
  @JsonProperty("createdTime")
  public String getCreatedTime() {
    return createdTime;
  }
  public void setCreatedTime(String createdTime) {
    this.createdTime = createdTime;
  }


  /**
   **/
  public ApplicationDTO updatedTime(String updatedTime) {
    this.updatedTime = updatedTime;
    return this;
  }

  
  @ApiModelProperty(example = "1651555310208", value = "")
  @JsonProperty("updatedTime")
  public String getUpdatedTime() {
    return updatedTime;
  }
  public void setUpdatedTime(String updatedTime) {
    this.updatedTime = updatedTime;
  }



  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApplicationDTO application = (ApplicationDTO) o;
    return Objects.equals(applicationId, application.applicationId) &&
        Objects.equals(name, application.name) &&
        Objects.equals(throttlingPolicy, application.throttlingPolicy) &&
        Objects.equals(description, application.description) &&
        Objects.equals(tokenType, application.tokenType) &&
        Objects.equals(status, application.status) &&
        Objects.equals(groups, application.groups) &&
        Objects.equals(subscriptionCount, application.subscriptionCount) &&
        Objects.equals(keys, application.keys) &&
        Objects.equals(attributes, application.attributes) &&
        Objects.equals(subscriptionScopes, application.subscriptionScopes) &&
        Objects.equals(owner, application.owner) &&
        Objects.equals(hashEnabled, application.hashEnabled) &&
        Objects.equals(createdTime, application.createdTime) &&
        Objects.equals(updatedTime, application.updatedTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(applicationId, name, throttlingPolicy, description, tokenType, status, groups, subscriptionCount, keys, attributes, subscriptionScopes, owner, hashEnabled, createdTime, updatedTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApplicationDTO {\n");
    
    sb.append("    applicationId: ").append(toIndentedString(applicationId)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    throttlingPolicy: ").append(toIndentedString(throttlingPolicy)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    tokenType: ").append(toIndentedString(tokenType)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    groups: ").append(toIndentedString(groups)).append("\n");
    sb.append("    subscriptionCount: ").append(toIndentedString(subscriptionCount)).append("\n");
    sb.append("    keys: ").append(toIndentedString(keys)).append("\n");
    sb.append("    attributes: ").append(toIndentedString(attributes)).append("\n");
    sb.append("    subscriptionScopes: ").append(toIndentedString(subscriptionScopes)).append("\n");
    sb.append("    owner: ").append(toIndentedString(owner)).append("\n");
    sb.append("    hashEnabled: ").append(toIndentedString(hashEnabled)).append("\n");
    sb.append("    createdTime: ").append(toIndentedString(createdTime)).append("\n");
    sb.append("    updatedTime: ").append(toIndentedString(updatedTime)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
