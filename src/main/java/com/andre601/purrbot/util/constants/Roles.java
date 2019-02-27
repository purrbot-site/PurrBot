package com.andre601.purrbot.util.constants;

/**
 * Class for commonly used Roles on the bots support-guild.
 */
public enum Roles {

    VOTER          ("475335831649910785"),
    VERY_ADDICTED  ("449280939839979530"),
    SUPER_ADDICTED ("424194307881435177"),
    ULTRA_ADDICTED ("541671949831766037"),
    HYPER_ADDICTED ("549988541539680266"),
    MASTER_ADDICTED("549988641464909827");

    private String role;

    Roles(String role){
        this.role = role;
    }

    public String getRole(){
        return this.role;
    }

}
