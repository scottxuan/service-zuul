package com.service.zuul.filters;

import com.google.common.collect.Lists;
import com.module.common.constants.ServiceConstant;
import com.module.common.error.ErrorCodes;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.scottxuan.base.exception.BizException;
import com.scottxuan.base.exception.ExceptionUtils;
import com.scottxuan.web.result.ResultDto;
import com.service.zuul.service.JwtParseService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author : pc
 * @date : 2020/8/27
 */
@Slf4j
@Component
public class AuthFilter extends ZuulFilter {

    @Autowired
    private JwtParseService jwtParseService;

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
        if (!uri.contains(ServiceConstant.SERVICE_API_COMMON)){
            return false;
        }
//        List<String> list = Lists.newArrayList();
//        if (list.contains(uri)) {
//            return true;
//        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String accessToken = request.getHeader("accessToken");
        if (StringUtils.isBlank(accessToken)) {
            log.error("accessToken is blank");
            ResultDto<Object> dto = new ResultDto<>(ErrorCodes.SYS_ERROR_401);
            throw new ZuulException(dto.getMessage(), dto.getCode(), dto.getMessage());
        }
        try {
            jwtParseService.parseToken(accessToken);
        } catch (ExpiredJwtException e) {
            log.error("no search accessToken");
            ResultDto<Object> dto = new ResultDto<>(ErrorCodes.ACCESS_TOKEN_TIME_OUT);
            throw new ZuulException(dto.getMessage(), dto.getCode(), dto.getMessage());
        }
        return null;
    }
}
