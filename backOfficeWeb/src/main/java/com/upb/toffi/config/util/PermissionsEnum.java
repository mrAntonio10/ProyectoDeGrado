package com.upb.toffi.config.util;

public class PermissionsEnum {
    /*
    createOperation = "CREATE";
    updateOperation = "UPDATE";
    deleteOperation = "DELETE";
    viewOperation = "VIEW";
    */

    public enum EnterprisePermissions {
        CREATE,
        UPDATE,
        DELETE,
        VIEW
    }

    public enum BranchOfficePermissions {
        CREATE,
        UPDATE,
        DELETE,
        VIEW
    }

    public enum UserPermissions {
        CREATE,
        UPDATE,
        DELETE,
        VIEW
    }

    public enum WarehousePermission {
        CREATE,
        UPDATE,
        DELETE,
        VIEW,
        REPORT
    }

    public enum SalesPanelPermission {
        CREATE,
        UPDATE,
        DELETE,
        VIEW,
        REPORT
    }

    public enum RolResourcesPermissions {
        UPDATE,
        VIEW
    }
}
