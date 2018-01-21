package core;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import util.SECRET;

public class Main {

    public static void main(String[] args){

        JDABuilder builder = new JDABuilder(AccountType.BOT);

        builder.setToken(SECRET.TOKEN);
        builder.setAutoReconnect(true);
        

    }

}
