package com.service.zuul.filters;

import com.module.common.constants.JwtConstant;
import com.module.common.constants.ServiceConstant;
import com.module.common.error.ErrorCodes;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.scottxuan.web.result.ResultDto;
import com.service.zuul.auth.AutoMemory;
import com.service.zuul.enums.UrlType;
import com.service.zuul.utils.JwtUtil;
import com.service.zuul.utils.UriMatchUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author : pc
 * @date : 2020/8/27
 */
@Slf4j
@Component
public class AuthFilter extends ZuulFilter {

    static {
        /**
         * uri认证
         */
        AutoMemory.putAuth("/service-auth/api/v1/auth/**", UrlType.ANON);
        AutoMemory.putAuth("/*/api/v1/**", UrlType.AUTH);
        AutoMemory.putAuth("/**", UrlType.ANON);

        /**
         * uri鉴权
         */
//        AutoMemory.putPermission("/service-auth/api/v1/auth/token/refresh", "admin1", "admin2");
    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_ERROR_FILTER_ORDER + 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String uri = request.getRequestURI();
        return authMatch(uri);
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String accessToken = request.getHeader(JwtConstant.ACCESS_TOKEN);
        if (StringUtils.isBlank(accessToken)) {
            log.info("accessToken is blank");
            ResultDto<Object> dto = new ResultDto<>(ErrorCodes.SYS_ERROR_401);
            throw new ZuulException(dto.getMessage(), dto.getCode(), dto.getMessage());
        }
        try {
            Claims claims = JwtUtil.parseToken(accessToken);
            permissionMatch(claims, request.getRequestURI());
        } catch (ExpiredJwtException e) {
            log.info("accessToken is time out");
            ResultDto<Object> dto = new ResultDto<>(ErrorCodes.ACCESS_TOKEN_TIME_OUT);
            throw new ZuulException(dto.getMessage(), dto.getCode(), dto.getMessage());
        }
        return null;
    }

    /**
     * 认证校验
     * @param uri
     * @return
     */
    private boolean authMatch(String uri) {
        Map<String, UrlType> patternsMap = AutoMemory.getAuths();
        if (!uri.contains(ServiceConstant.SERVICE_API_COMMON)) {
            return false;
        }
        Set<String> patterns = patternsMap.keySet();
        if (patterns.isEmpty()) {
            return false;
        }
        for (String pattern : patterns) {
            boolean match = UriMatchUtil.doMatch(pattern, uri);
            if (match) {
                UrlType urlType = patternsMap.get(pattern);
                return urlType == UrlType.AUTH;
            }
        }
        return false;
    }

    /**
     * 鉴权
     * @param claims
     * @param uri
     * @throws ZuulException
     */
    private void permissionMatch(Claims claims, String uri) throws ZuulException {
        Map<String, List<String>> uriPermissionsMap = AutoMemory.getPermissions();
        if (uriPermissionsMap.isEmpty()) {
            return;
        }
        for (String patternUri : uriPermissionsMap.keySet()) {
            boolean match = UriMatchUtil.doMatch(patternUri, uri);
            if (!match) {
                continue;
            }
            List<String> uriPermissions = uriPermissionsMap.get(patternUri);
            for (String uriPermission : uriPermissions) {
                List<String> userPermissions = (List<String>)claims.get(JwtConstant.PERMISSIONS);
                if (!userPermissions.contains(uriPermission)){
                    log.info("user has no permission : {}",uriPermission);
                    ResultDto<Object> dto = new ResultDto<>(ErrorCodes.SYS_ERROR_403);
                    throw new ZuulException(dto.getMessage(), dto.getCode(), dto.getMessage());
                }
            }
        }
    }
}
