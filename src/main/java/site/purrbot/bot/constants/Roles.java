package site.purrbot.bot.constants;

public enum Roles {

    // Roles for upvoting (discordbots.org and botlist.space) or for adding it to their favourites (lbots.org)
    FAVOURITE     ("550817930489626633"),
    UPVOTE_BOTLIST("588717938849349645"),
    UPVOTE_DBL    ("475335831649910785"),

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
