package site.purrbot.bot.constants;

public enum IDs {

    ANDRE_601 ("204232208049766400"),
    EVELIEN   ("433894627553181696"),
    KORBO     ("116595448357060610"),
    KAPPACHINO("416177108897759233"),

    PURR   ("425382319449309197"),
    SNUGGLE("439829950686822410"),

    GUILD("423771795523371019");

    private String id;

    IDs(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
