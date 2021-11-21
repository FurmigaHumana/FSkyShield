package a;

import java.security.AccessControlException;
import java.security.Permission;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class AgentSecurity extends SecurityManager {

    @Override
    public void checkPermission(Permission perm, Object context) {
        checkPermission(perm);
    }

    @Override
    public void checkPermission(Permission perm) {
        if ("setSecurityManager".equals(perm.getName())) {
            throw new AccessControlException("Restricted Action", perm);
        }
    }
}