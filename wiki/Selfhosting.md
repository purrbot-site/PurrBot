[PurrBot#startUpdates]: https://github.com/Andre601/PurrBot/blob/master/src/main/java/site/purrbot/bot/PurrBot.java#L154-L171
[PurrBot#startUpdate]: https://github.com/Andre601/PurrBot/blob/master/src/main/java/site/purrbot/bot/PurrBot.java#L375-L385
[MIT-License]: https://github.com/Andre601/PurrBot/blob/master/LICENSE

[RethinkDB]: https://rethinkdb.com

[IDs.java]: https://github.com/Andre601/PurrBot/blob/master/src/main/java/site/purrbot/bot/constants/IDs.java
[Emotes.java]: https://github.com/Andre601/PurrBot/blob/master/src/main/java/site/purrbot/bot/constants/Emotes.java
[Roles.java]: https://github.com/Andre601/PurrBot/blob/master/src/main/java/site/purrbot/bot/constants/Roles.java
[Links.java]: https://github.com/Andre601/PurrBot/blob/master/src/main/java/site/purrbot/bot/constants/Links.java

[random.json]: https://github.com/Andre601/PurrBot/blob/master/src/main/resources/random.json
[lang-files]: https://github.com/Andre601/PurrBot/blob/master/src/main/resources/lang

Some of you may want to selfhost the bot on your own VPS/server.  
This page explains how you can selfhost the bot for yourself.

## Requirements
Before you can run the bot will you need to make sure that the following requirements are met.

### RethinkDB
You need to have [RethinkDB] installed and running on your server, where the bot will later run.  
This also includes creating databases and tables to later set in the config.json.

### Java
You need **at least** Java 8 to be installed and working. Newer versions of Java should work too.

## Setup
When you made sure, that the [requirements](#requirements) are met, can you continue with preparing the code.

### Clone the repository
Clone/Fork this repository, if you didn't already and open it in your preferred IDE.

### Make changes to code
You need to alter specific parts of the code, to prevent errors from appearing.  
Alternatively could you set "beta" in the config.json to true, to set the bot as Beta-Bot, disabling certain functionalities.

If you want to alter the code, make changes to the following sections by either removing or commenting them out:
- [PurrBot#startUpdates] (Lines 154-171)
- [PurrBot#startUpdate] (Lines 375-385)

### Update some classes
You need to update values in specific classes to make your version work without issues.  
Namely you have to alter the content of the following classes:
- [IDs.java] (Contains various IDs of users or Guilds)
- [Emotes.java] (Contains different emotes used in commands)
- [Roles.java] (Contains roles for things like the support Guild's join roles)
- [Links.java] (Contains various links of the bot)

### Build jar file
When you're done with your changes, make sure to execute `gradlew clean shadowJar` to build a shaded jar containing all dependnencies required.

### Config.json
The `config.json` is the core file of the bot in which you set various different information that will be used by the bot.  
On first startup will it generate with the below default values.

<details>
  <summary>Default config.json (Click to Show/Hide)</summary>
  
```json
{
  "bot-token": "TOKEN",
  
  "beta": false,
  
  "webhooks": {
    "guild": "guild-webhook-url",
    "log": "log-webhook-url"
  },
  
  "tokens": {
    "blaze": "blaze-token",
    
    "discord-bots-gg": "dbgg-token",
    "lbots-org": "lbots-token",
    "botlist-space": "botlist-token",
    "discordextremelist-xyz": "debl-token"
  },
  
  "database": {
    "ip": "127.0.0.1",
    "name": "DatabaseName",
    "guildTable": "GuildTable"
  }
}
```
</details>

Note that you don't have to set values for every option in the config.json.  
If you followed the previous step on preparing the bot will you only need to set the following options:

| Option:                | Value required:                                                     |
| ---------------------- | ------------------------------------------------------------------- |
| `bot-token`            | Valid Bot-token of your Bot-application to login.                   |
| `webhooks.guild`       | A URL to a Discord webhook for logging joins and leaves of the bot. |
| `webhooks.log`         | A URL to a Discord webhook for logging dis/reconnects of the bot.   |
| `blaze-token`          | Valid Authentication-token for the image-API of Fluxpoint.dev       |
| `database.ip`          | Domain/IP of the RethinkDB server.                                  |
| `database.name`        | Name of the database you created.                                   |
| `database.guildTable`  | Name of the table you created to store guild settings.              |

### Other files
The bot also has other files, which you can alter to your liking.
- [random.json] contains various messages and links used for (often) random responses.
- Various [lang-files] used for the different command responses of the bot.

## Final words and information
With this things done, you should be able to host and run the bot on your own.

Note that by selfhosting the bot, you agree to the below conditions:
- You follow the [MIT-License] of the bot.
  - This includes to **not** claim the code of the bot as your own and to provide credit to the original author.
- You acknowledge that you won't receive support for your selfhosted bot by the original author.