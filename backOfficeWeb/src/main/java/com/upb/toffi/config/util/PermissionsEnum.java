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

    public enum RolResourcesPermissions {
        UPDATE,
        VIEW
    }
}
