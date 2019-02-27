package com.andre601.purrbot.util.constants;

/**
 * Class for commonly used IDs.
 */
public enum IDs {

    //Accounts
    ANDRE_601  ("204232208049766400"),
    EVELIEN    ("433894627553181696"),
    LILYSCARLET("218772716814204930"),
    KORBO      ("116595448357060610"),

    // Bots
    PURR   ("425382319449309197"),

    // Support guild
    GUILD("423771795523371019"),

    // Current version
    VERSION("3.3.8");

    private String id;

    IDs(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

}
