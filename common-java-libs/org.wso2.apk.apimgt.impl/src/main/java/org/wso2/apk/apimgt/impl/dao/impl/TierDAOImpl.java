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

package org.wso2.apk.apimgt.impl.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.apk.apimgt.api.APIManagementException;
import org.wso2.apk.apimgt.api.ErrorHandler;
import org.wso2.apk.apimgt.api.ExceptionCodes;
import org.wso2.apk.apimgt.impl.dao.TierDAO;
import org.wso2.apk.apimgt.impl.dao.constants.SQLConstants;
import org.wso2.apk.apimgt.impl.dto.TierPermissionDTO;
import org.wso2.apk.apimgt.impl.utils.APIMgtDBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class TierDAOImpl implements TierDAO {

    private static final Log log = LogFactory.getLog(TierDAOImpl.class);
    private static TierDAOImpl INSTANCE = new TierDAOImpl();

    private TierDAOImpl() {

    }

    public static TierDAOImpl getInstance() {
        return INSTANCE;
    }

    private void handleExceptionWithCode(String msg, Throwable t, ErrorHandler code) throws APIManagementException {
        log.error(msg, t);
        throw new APIManagementException(msg, code);
    }

    private void handleException(String msg, Throwable t) throws APIManagementException {

        log.error(msg, t);
        throw new APIManagementException(msg, t);
    }


    @Override
    public void updateTierPermissions(String tierName, String permissionType, String roles, String organization)
            throws APIManagementException {

        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement insertOrUpdatePS = null;
        ResultSet resultSet = null;
        int tierPermissionId = -1;

        try {
            conn = APIMgtDBUtil.getConnection();
            conn.setAutoCommit(false);

            String getTierPermissionQuery = SQLConstants.GET_TIER_PERMISSION_ID_SQL;
            ps = conn.prepareStatement(getTierPermissionQuery);
            ps.setString(1, tierName);
            ps.setString(2, organization);
            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                tierPermissionId = resultSet.getInt("TIER_PERMISSIONS_ID");
            }

            if (tierPermissionId == -1) {
                String query = SQLConstants.ADD_TIER_PERMISSION_SQL;
                insertOrUpdatePS = conn.prepareStatement(query);
                insertOrUpdatePS.setString(1, tierName);
                insertOrUpdatePS.setString(2, permissionType);
                insertOrUpdatePS.setString(3, roles);
                insertOrUpdatePS.setString(4, organization);
                insertOrUpdatePS.execute();
            } else {
                String query = SQLConstants.UPDATE_TIER_PERMISSION_SQL;
                insertOrUpdatePS = conn.prepareStatement(query);
                insertOrUpdatePS.setString(1, tierName);
                insertOrUpdatePS.setString(2, permissionType);
                insertOrUpdatePS.setString(3, roles);
                insertOrUpdatePS.setInt(4, tierPermissionId);
                insertOrUpdatePS.setString(5, organization);
                insertOrUpdatePS.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            handleException("Error in updating tier permissions: " + e.getMessage(), e);
        } finally {
            APIMgtDBUtil.closeAllConnections(ps, conn, resultSet);
            APIMgtDBUtil.closeAllConnections(insertOrUpdatePS, null, null);
        }
    }

    @Override
    public void deleteThrottlingPermissions(String tierName, String organization) throws APIManagementException {
        int tierPermissionId = -1;
        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQLConstants.GET_THROTTLE_TIER_PERMISSION_ID_SQL)) {
            ps.setString(1, tierName);
            ps.setString(2, organization);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    tierPermissionId = resultSet.getInt("THROTTLE_TIER_PERMISSIONS_ID");
                }
            }
            if (tierPermissionId != -1) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(SQLConstants
                        .DELETE_THROTTLE_TIER_PERMISSION_SQL)) {
                    preparedStatement.setInt(1, tierPermissionId);
                    preparedStatement.setString(2, organization);
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            handleExceptionWithCode("Error in deleting tier permissions: " + e.getMessage(), e,
                    ExceptionCodes.APIMGT_DAO_EXCEPTION);
        }
    }

    @Override
    public Set<TierPermissionDTO> getTierPermissions(String organization) throws APIManagementException {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        Set<TierPermissionDTO> tierPermissions = new HashSet<TierPermissionDTO>();

        try {
            String getTierPermissionQuery = SQLConstants.GET_TIER_PERMISSIONS_SQL;

            conn = APIMgtDBUtil.getConnection();
            ps = conn.prepareStatement(getTierPermissionQuery);
            ps.setString(1, organization);

            resultSet = ps.executeQuery();
            while (resultSet.next()) {
                TierPermissionDTO tierPermission = new TierPermissionDTO();
                tierPermission.setTierName(resultSet.getString("TIER"));
                tierPermission.setPermissionType(resultSet.getString("PERMISSIONS_TYPE"));
                String roles = resultSet.getString("ROLES");
                if (roles != null && !roles.isEmpty()) {
                    String roleList[] = roles.split(",");
                    tierPermission.setRoles(roleList);
                }
                tierPermissions.add(tierPermission);
            }
        } catch (SQLException e) {
            handleException("Failed to get Tier permission information ", e);
        } finally {
            APIMgtDBUtil.closeAllConnections(ps, conn, resultSet);
        }
        return tierPermissions;
    }

    @Override
    public TierPermissionDTO getThrottleTierPermission(String tierName, String organization) throws APIManagementException {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        TierPermissionDTO tierPermission = null;
        try {
            String getTierPermissionQuery = SQLConstants.GET_THROTTLE_TIER_PERMISSION_SQL;
            conn = APIMgtDBUtil.getConnection();
            ps = conn.prepareStatement(getTierPermissionQuery);

            ps.setString(1, tierName);
            ps.setString(2, organization);

            resultSet = ps.executeQuery();
            while (resultSet.next()) {
                tierPermission = new TierPermissionDTO();
                tierPermission.setTierName(tierName);
                tierPermission.setPermissionType(resultSet.getString("PERMISSIONS_TYPE"));
                String roles = resultSet.getString("ROLES");
                if (roles != null) {
                    String roleList[] = roles.split(",");
                    tierPermission.setRoles(roleList);
                }
            }
        } catch (SQLException e) {
            handleExceptionWithCode("Failed to get Tier permission information for Tier " + tierName, e,
                    ExceptionCodes.APIMGT_DAO_EXCEPTION);
        } finally {
            APIMgtDBUtil.closeAllConnections(ps, conn, resultSet);
        }
        return tierPermission;
    }

    @Override
    public void updateThrottleTierPermissions(String tierName, String permissionType, String roles, String organization)
            throws APIManagementException {

        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement insertOrUpdatePS = null;
        ResultSet resultSet = null;
        int tierPermissionId = -1;

        try {
            conn = APIMgtDBUtil.getConnection();
            conn.setAutoCommit(false);

            String getTierPermissionQuery = SQLConstants.GET_THROTTLE_TIER_PERMISSION_ID_SQL;
            ps = conn.prepareStatement(getTierPermissionQuery);
            ps.setString(1, tierName);
            ps.setString(2, organization);
            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                tierPermissionId = resultSet.getInt("THROTTLE_TIER_PERMISSIONS_ID");
            }

            if (tierPermissionId == -1) {
                String query = SQLConstants.ADD_THROTTLE_TIER_PERMISSION_SQL;
                insertOrUpdatePS = conn.prepareStatement(query);
                insertOrUpdatePS.setString(1, tierName);
                insertOrUpdatePS.setString(2, permissionType);
                insertOrUpdatePS.setString(3, roles);
                insertOrUpdatePS.setString(4, organization);
                insertOrUpdatePS.execute();
            } else {
                String query = SQLConstants.UPDATE_THROTTLE_TIER_PERMISSION_SQL;
                insertOrUpdatePS = conn.prepareStatement(query);
                insertOrUpdatePS.setString(1, tierName);
                insertOrUpdatePS.setString(2, permissionType);
                insertOrUpdatePS.setString(3, roles);
                insertOrUpdatePS.setInt(4, tierPermissionId);
                insertOrUpdatePS.setString(5, organization);
                insertOrUpdatePS.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            handleExceptionWithCode("Error in updating tier permissions: " + e.getMessage(), e,
                    ExceptionCodes.APIMGT_DAO_EXCEPTION);
        } finally {
            APIMgtDBUtil.closeAllConnections(ps, conn, resultSet);
            APIMgtDBUtil.closeAllConnections(insertOrUpdatePS, null, null);
        }
    }

    @Override
    public Set<TierPermissionDTO> getThrottleTierPermissions(String organization) throws APIManagementException {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        Set<TierPermissionDTO> tierPermissions = new HashSet<TierPermissionDTO>();

        try {
            String getTierPermissionQuery = SQLConstants.GET_THROTTLE_TIER_PERMISSIONS_SQL;

            conn = APIMgtDBUtil.getConnection();
            ps = conn.prepareStatement(getTierPermissionQuery);
            ps.setString(1, organization);

            resultSet = ps.executeQuery();
            while (resultSet.next()) {
                TierPermissionDTO tierPermission = new TierPermissionDTO();
                tierPermission.setTierName(resultSet.getString("TIER"));
                tierPermission.setPermissionType(resultSet.getString("PERMISSIONS_TYPE"));
                String roles = resultSet.getString("ROLES");
                if (roles != null && !roles.isEmpty()) {
                    String roleList[] = roles.split(",");
                    tierPermission.setRoles(roleList);
                }
                tierPermissions.add(tierPermission);
            }
        } catch (SQLException e) {
            handleException("Failed to get Tier permission information ", e);
        } finally {
            APIMgtDBUtil.closeAllConnections(ps, conn, resultSet);
        }
        return tierPermissions;
    }


}
