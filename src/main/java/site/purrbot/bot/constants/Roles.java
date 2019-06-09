package site.purrbot.bot.constants;

public enum Roles {

    // Roles for voting or favouriting the bot
    UPVOTED   ("475335831649910785"),
    FAVOURITED("550817930489626633"),

    // Level roles.
    ADDICTED       ("423772991520768011"),
    VERY_ADDICTED  ("449280939839979530"),
    SUPER_ADDICTED ("424194307881435177"),
    ULTRA_ADDICTED ("541671949831766037"),
    HYPER_ADDICTED ("549988541539680266"),
    MASTER_ADDICTED("549988641464909827");

    private String id;

    Roles(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }
}
