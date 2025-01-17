/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.apk.apimgt.api.model.subscription;

import org.wso2.apk.apimgt.api.dto.ConditionDTO;
import org.wso2.apk.apimgt.api.model.policy.PolicyConstants;
import org.wso2.apk.apimgt.api.model.policy.QuotaPolicy;

import java.util.List;

/**
 * Entity for keeping details related to ConditionGroups.
 */
public class APIPolicyConditionGroup {

    private int policyId = -1;
    private String quotaType;
    private QuotaPolicy quotaPolicy;
    private int conditionGroupId = -1;
    private List<ConditionDTO> conditionDTOS;

    public int getPolicyId() {

        return policyId;
    }

    public void setPolicyId(int policyId) {

        this.policyId = policyId;
    }

    public int getConditionGroupId() {

        return conditionGroupId;
    }

    public void setConditionGroupId(int conditionGroupId) {

        this.conditionGroupId = conditionGroupId;
    }

    public String getQuotaType() {

        return quotaType;
    }

    public void setQuotaType(String quotaType) {

        this.quotaType = quotaType;
    }

    public List<ConditionDTO> getConditionDTOS() {

        return conditionDTOS;
    }

    public void setConditionDTOS(List<ConditionDTO> conditionDTOS) {

        this.conditionDTOS = conditionDTOS;
    }

    public boolean isContentAware() {

        if (PolicyConstants.BANDWIDTH_TYPE.equals(quotaType)) {
            return true;
        }
        if (conditionDTOS != null) {
            conditionDTOS.stream().anyMatch(conditionDTO ->
                            PolicyConstants.BANDWIDTH_TYPE.equals(quotaType)
                                           );
            return false;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        APIPolicyConditionGroup conditionGroup = (APIPolicyConditionGroup) obj;
        return conditionGroup.policyId == policyId &&
                conditionGroup.conditionGroupId == conditionGroupId;

    }

    public QuotaPolicy getQuotaPolicy() {
        return quotaPolicy;
    }

    public void setQuotaPolicy(QuotaPolicy quotaPolicy) {
        this.quotaPolicy = quotaPolicy;
    }

    @Override
    public int hashCode() {

        return (policyId == -1 || conditionGroupId == -1) ? super.hashCode() : policyId * conditionGroupId;
    }
}
