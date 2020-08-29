package com.service.zuul.service;

import io.jsonwebtoken.Claims;

/**
 * @author : pc
 * @date : 2020/8/27
 */
public interface JwtParseService {
    /**
     * token解析
     * @param token
     * @return
     */
    Claims parseToken(String token);
}
