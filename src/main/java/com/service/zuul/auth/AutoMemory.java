package com.service.zuul.auth;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.service.zuul.enums.UrlType;

import java.util.List;
import java.util.Map;

/**
 * @author scottxuan
 */
public class AutoMemory {
    private static final Map<String, UrlType> AUTHS;
    private static final Map<String, List<String>> PERMISSIONS;

    static {
        AUTHS = Maps.newLinkedHashMap();
        PERMISSIONS = Maps.newHashMap();
    }

    public static void putAuth(String url, UrlType urlType){
        AUTHS.put(url,urlType);
    }

    public static void putPermission(String url, String... permissions){
        AutoMemory.PERMISSIONS.put(url, Lists.newArrayList(permissions));
    }

    public static Map<String, UrlType> getAuths() {
        return AUTHS;
    }

    public static Map<String, List<String>> getPermissions() {
        return PERMISSIONS;
    }
}
