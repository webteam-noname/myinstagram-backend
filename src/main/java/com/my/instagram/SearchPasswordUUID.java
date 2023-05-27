package com.my.instagram;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;


public class SearchPasswordUUID {
    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {





        /*
        System.out.println("암호화 전: " + text);
        System.out.println("secretKey: " + secretKey);

        System.out.println("-------------------------------------------------------");

        System.out.println("Hmac-MD5(Base64): " + HmacAndBase64(secretKey, text, "HmacMD5"));
        System.out.println("Hmac-SHA1(Base64): " + HmacAndBase64(secretKey, text, "HmacSHA1"));
        System.out.println("Hmac-SHA224(Base64): " + HmacAndBase64(secretKey, text, "HmacSHA224"));
        System.out.println("Hmac-SHA256(Base64): " + HmacAndBase64(secretKey, text, "HmacSHA256"));
        System.out.println("Hmac-SHA384(Base64): " + HmacAndBase64(secretKey, text, "HmacSHA384"));
        System.out.println("Hmac-SHA512(Base64): " + HmacAndBase64(secretKey, text, "HmacSHA512"));

        System.out.println("-------------------------------------------------------");*/
    }

    public static String HmacAndBase64(String secret, String data, String Algorithms) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {

        //1. SecretKeySpec 클래스를 사용한 키 생성
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes("utf-8"), Algorithms);

        //2. 지정된  MAC 알고리즘을 구현하는 Mac 객체를 작성합니다.
        Mac hasher = Mac.getInstance(Algorithms);

        //3. 키를 사용해 이 Mac 객체를 초기화
        hasher.init(secretKey);

        //3. 암호화 하려는 데이터의 바이트의 배열을 처리해 MAC 조작을 종료
        byte[] hash = hasher.doFinal(data.getBytes());

        //4. Base 64 Encode to String
        return Base64.encodeBase64String(hash);
    }
}
