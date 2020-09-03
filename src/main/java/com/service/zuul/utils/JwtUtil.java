package com.service.zuul.utils;

import com.scottxuan.base.utils.RSAUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.PublicKey;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author : pc
 * @date : 2020/8/27
 */
public class JwtUtil{
    private static PublicKey publicKey = null;
    private static final AtomicBoolean PUBLIC_KEY_IS_LOAD = new AtomicBoolean(true);

    public static Claims parseToken(String token) {
        if(PUBLIC_KEY_IS_LOAD.compareAndSet(true,false) || publicKey == null){
            publicKey = getPublicKey();
        }
        Jws<Claims> claimsJws = Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(token);
        return claimsJws.getBody();
    }

    public static PublicKey getPublicKey() {
        BufferedReader bufferedReader = null;
        PublicKey publicKey = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(this.getClass().getResource("/publicKey.key").getPath()));
            StringBuilder privateKeyBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                privateKeyBuilder.append(line);
            }
            String publicKeyString = privateKeyBuilder.toString().trim();
            publicKeyString = publicKeyString.replaceAll("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll("-----END PUBLIC KEY-----", "")
                    .replaceAll("\r", "")
                    .replaceAll("\n", "")
                    .replaceAll("\r\n", "");
            publicKey = RSAUtils.getPublicKey(publicKeyString);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return publicKey;
    }
}
