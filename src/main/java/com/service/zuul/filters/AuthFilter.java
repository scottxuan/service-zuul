package com.service.zuul.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.scottxuan.base.exception.BizException;
import com.scottxuan.web.result.ResultDto;
import com.service.zuul.service.JwtParseService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : pc
 * @date : 2020/8/27
 */
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
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String accessToken = request.getHeader("accessToken");
        try {
            jwtParseService.parseToken(accessToken);
        } catch (ExpiredJwtException e) {

        }
        return null;
    }
}
