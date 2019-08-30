/*
 * Copyright (C) 2015 Peter Gregus for GravityBox Project (C3C076@xda)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.retrox.aodmod.hooks;

import android.Manifest.permission;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import java.util.List;

public class PermissionGranter {
    public static final String TAG = "GB:PermissionGranter";
    public static final boolean DEBUG = false;

    public static final String SYSTEM_UI = "com.android.systemui";

    private static final String CLASS_PERMISSION_MANAGER_SERVICE = "com.android.server.pm.permission.PermissionManagerService";
    private static final String CLASS_PERMISSION_CALLBACK = "com.android.server.pm.permission.PermissionManagerInternal.PermissionCallback";
    private static final String CLASS_PACKAGE_PARSER_PACKAGE = "android.content.pm.PackageParser.Package";
    private static final String PERM_ACCESS_SURFACE_FLINGER = "android.permission.ACCESS_SURFACE_FLINGER";

    private static void log(String message) {
        XposedBridge.log(TAG + ": " + message);
    }

    public static void initAndroid(final ClassLoader classLoader) {
        try {
            final Class<?> pmServiceClass = XposedHelpers.findClass(CLASS_PERMISSION_MANAGER_SERVICE, classLoader);

            XposedHelpers.findAndHookMethod(pmServiceClass, "grantPermissions",
                    CLASS_PACKAGE_PARSER_PACKAGE, boolean.class, String.class,
                    CLASS_PERMISSION_CALLBACK, new XC_MethodHook() {
                @SuppressWarnings("unchecked")
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    final String pkgName = (String) XposedHelpers.getObjectField(param.args[0], "packageName");
                    final Object extras = XposedHelpers.getObjectField(param.args[0], "mExtras");
                    final Object ps = XposedHelpers.callMethod(extras, "getPermissionsState");
                    final List<String> grantedPerms =
                            (List<String>) XposedHelpers.getObjectField(param.args[0], "requestedPermissions");
                    final Object settings = XposedHelpers.getObjectField(param.thisObject, "mSettings");
                    final Object permissions = XposedHelpers.getObjectField(settings, "mPermissions");

                    // SystemUI
                    if (SYSTEM_UI.equals(pkgName)) {

                        if (!grantedPerms.contains(permission.INTERNET)) {
                            final Object p = XposedHelpers.callMethod(permissions, "get",
                                    permission.INTERNET);
                            XposedHelpers.callMethod(ps, "grantInstallPermission", p);
                        }

                        if (!grantedPerms.contains(permission.WRITE_EXTERNAL_STORAGE)) {
                            final Object p = XposedHelpers.callMethod(permissions, "get",
                                    permission.WRITE_EXTERNAL_STORAGE);
                            XposedHelpers.callMethod(ps, "grantInstallPermission", p);
                        }

                        if (!grantedPerms.contains(permission.READ_EXTERNAL_STORAGE)) {
                            final Object p = XposedHelpers.callMethod(permissions, "get",
                                    permission.READ_EXTERNAL_STORAGE);
                            XposedHelpers.callMethod(ps, "grantInstallPermission", p);
                        }
                    }
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("Grant Permisson Failed");
            XposedBridge.log(t);
        }
    }
}
