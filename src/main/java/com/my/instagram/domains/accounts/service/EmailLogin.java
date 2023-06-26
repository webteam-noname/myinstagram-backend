package com.my.instagram.domains.accounts.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class EmailLogin {
    private final static Map<String, TempAccountInfo> mapEmailLogin = new HashMap<>();

    public void putEmailLogin(String uidb, String accessToken, String username){
        TempAccountInfo tempAccountInfo = new TempAccountInfo(accessToken, username);
        mapEmailLogin.put(uidb, tempAccountInfo);
    }

    public boolean isRightTempAccessToken(String uidb, String accessToken){
        return mapEmailLogin.get(uidb).accessToken.equals(accessToken);
    }

    public String getEamilLoginRealUsername(String uidb){
        return mapEmailLogin.get(uidb).username;
    }

    public void increaseAutoCount(String uidb){
        mapEmailLogin.get(uidb).increaseAutoCount();
    }

    // 현재 방식이 얼만큼의 성능이 나오는지 테스트 해보기
    public void deleteTempAccounts(String uidb) {
        TempAccountInfo tempAccountInfo = mapEmailLogin.get(uidb);
        String username = tempAccountInfo.username;
        Set<String> TempLoginKeys = mapEmailLogin.keySet();

        for (String uidbKey : TempLoginKeys) {
            if(mapEmailLogin.get(uidbKey).username == username){
                mapEmailLogin.remove(uidbKey);
            }
        }
    }

    public void isAutoCountOverFirstExistsException(String uidb){
        if(mapEmailLogin.get(uidb).getAutoCount() > 0){
            throw new RuntimeException("이메일 임시 로그인은 한번만 가능합니다.");
        }
    }

    class TempAccountInfo{
        private String accessToken;
        private String username;
        private int autoCount;

        private TempAccountInfo(){

        }

        private TempAccountInfo(String accessToken, String username){
            this.username = username;
            this.accessToken = accessToken;
            this.autoCount = 0;
        }

        private void increaseAutoCount(){
            autoCount++;
        }

        private int getAutoCount(){
            return this.autoCount;
        }
    }
}
