package com.icplaza.sdk.common;

public class EnvInstance {
    private static EnvBase env = null;

    public static void setEnv(EnvBase the_env) {
        env = the_env;
    }

    public static EnvBase getEnv() {
        return env;
    }
}
