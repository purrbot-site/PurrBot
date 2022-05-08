[Translation Project]: https://github.com/purrbot-site/Translations
[Wiki]: https://docs.purrbot.site/bot
[Doc-repository]: https://github.com/purrbot-site/Doc
[Discord]: https://purrbot.site/discord

## About
I appreciate any contribution that helps making this bot better.  
But before you start working on things and make a PR, read this page here first.

### Translations
This is not the place to commit your translations to.  
To participate into translating the bot, join the [Discord] and ask to be added towards the [Translation Project].

Any Pull requests towards the lang files in this repository will be denied.

### Wiki
If you want to help towards the [wiki], head over to the [Doc-repository] and make sure to follow the guidelines mentioned on the Readme file.

### Bot
When you want to commit changes to the bot, make sure to follow specific rules.

#### Avoid static methods
The bot tries its best to not use static methods. Instead are classes loaded through DI (Dependency injection).  
For example do we access the DBUtil through `PurrBot#getDbUtil()` and not through static methods.

The only exception are constants like IDs or links, which are `final static`

#### New commands
New commands should be added as their own class in the right path. The name of the class is always `Cmd` followed by the command name.  
For example would the command Hello become `CmdHello.java` as class name.

The class itself has to implement the `Command` interface of the project and override the `run` method.  
This method provides important information like the Guild and TextChannel the command was executed in, who executed it and any possible arguments that where provided.

Additionally will you need to Add a constructor that adds the main class (`PurrBot`) as sort of dependency.

And finally will you need to annotate the class with the `@CommandDescription` annotation, to set the name, description, triggers and `@CommandAttribute`s.  
The mentioned attributes need to contain the following things:  

| Key      | Value                     | Description                                                               |
| -------- | ------------------------- | ------------------------------------------------------------------------- |
| category | `<category name>`         | Category to wich this command belongs.                                    |
| usage    | `<all possible commands>` | Command options displayed in `.help <command>`. Use `{p}` for the prefix. |
| help     | `<single command>`        | Will be displayed in the help menu. Use `{p}` for the prefix.             |

The final class could look like this:  
```java
package site.purrbot.bot.commands.example;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.OldCommand;

@CommandDescription(
        name = "Hello",
        description =
                "Makes the bot say hello",
        triggwes = {"hello", "hi"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}hello"),
                @CommandAttribute(key = "help", value = "{p}hello")
        }
)
public class CmdHelp implements Command{

    private final PurrBot bot;
    
    public CmdHelp(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        tc.sendMessage(bot.getMsg(guild.getId(), "purr.fun.hello.message")).queue();
    }
}
```
