package com.laosuye.mychat.common.commm.constant;

public class RedisKey {

    private static final String BASE_KEY = "mychat:chat";

    /**
     * 用户token 的key
     */
    public static final String USER_TOKEN_STRING = "userToken:uid%d";

    public static String getKey(String key,Object... o){
        return BASE_KEY + String.format(key,o);
    }
}
