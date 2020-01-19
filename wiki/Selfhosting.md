[PurrBot#setup (L193-L271)]: https://github.com/Andre601/PurrBot/blob/master/src/main/java/site/purrbot/bot/PurrBot.java#L193-L271
[PurrBot#startUpdates (L275-L298)]: https://github.com/Andre601/PurrBot/blob/master/src/main/java/site/purrbot/bot/PurrBot.java#L275-L298
[PurrBot#startUpdate (L518-L531)]: https://github.com/Andre601/PurrBot/blob/master/src/main/java/site/purrbot/bot/PurrBot.java#L518-L531
[MIT-License]: https://github.com/Andre601/PurrBot/blob/master/LICENSE

[RethinkDB]: https://rethinkdb.com

[IDs.java]: https://github.com/Andre601/PurrBot/blob/master/src/main/java/site/purrbot/bot/constants/IDs.java
[Emotes.java]: https://github.com/Andre601/PurrBot/blob/master/src/main/java/site/purrbot/bot/constants/Emotes.java
[Roles.java]: https://github.com/Andre601/PurrBot/blob/master/src/main/java/site/purrbot/bot/constants/Roles.java
[lang-files]: https://github.com/Andre601/PurrBot/tree/master/src/main/resources/lang

Some of you may want to selfhost the bot on your own VPS/server.  
This page explains how you can selfhost the bot for yourself.

## Requirements
Before you can start hosting it, you have to make sure, that the following requirements are met.
- [RethinkDB] setup and running.
- Java 8 installed (The bot may work on later versions, but it's not guaranteed).

## Settings
After you made sure you have setup everything in the [[Requirements|Selfhosting#requirements]], you can start making settings.

### RethinkDB
In RethinkDB, you have to create a database and a table, where the info for guilds are stored.  
You then take the names of the database and the table and add them to the corresponding option in the [[config.json|Selfhosting#configjson]]

### Changes to code
You can't just fork the repo, compile the code and run the bot. This will most likely cause errors on start.  
In order to use the bot, you have to remove (or comment out) certain section of the bot, to prevent errors to happen.
Those parts can be found at those locations:

- [PurrBot#setup (L193-L271)]
- [PurrBot#startUpdates (L275-L298)]
- [PurrBot#startUpdate (L518-L531)]

This should pretty much prevent any kind of errors.

Next do you have to update [IDs.java], [Emotes.java] and [Roles.java] with the values of your choice.

### Config.json
The config.json has a lot of different settings in it.  
By default does the config look like this:  
> ```json
> {
>   "bot-token": "TOKEN",
>   
>   "beta": false,
>   
>   "webhooks": {
>     "guild": "guild-webhook-url",
>     "log": "log-webhook-url",
>     "vote": "vote-webhook-url"
>   },
>   
>   "tokens": {
>     "blaze": "blaze-token",
>     
>     "top-gg": "top-token",
>     "discord-bots-gg": "dbgg-token",
>     "lbots-org": "lbots-token",
>     "botlist-space": "botlist-token",
>     "discordextremelist-xyz": "debl-token"
>   },
> 
>
>   "database": {
>     "ip": "127.0.0.1",
>     "name": "DatabaseName",
>     "guildTable": "GuildTable",
>     "memberTable": "MemberTable"
>   }
> }
> ```
Not all settings are required to be filled out, in order to let the bot work properly.  
When you followed the steps in the [changes to code](#changes-to-code) section, will you only need to set the following values:

| Option:          | Value required:                                                      |
| ---------------------- | -------------------------------------------------------------------- |
| `bot-token`            | Valid Bot-token of your Bot-application to login.                    |
| `webhooks.guild`       | A URL to a Discord webhook for logging joins and leaves of the bot.  |
| `webhooks.log`         | A URL to a Discord webhook for logging dis/reconnects of the bot.    |
| `blaze-token`          | Valid Authentication-token for the image-API of BlazeDev (Builderb). |
| `database.ip`          | Domain/IP of the RethinkDB server.                                   |
| `database.name`        | Name of the database you created.                                    |
| `database.guildTable`  | Name of the table you created to store guild settings.               |
| `database.memberTable` | Name of the table you created to store member information.           |

### random.json
> Most if not all responses have been moved over to the various [lang-files] of the bot.  
> Edit those if you want to personalize the responses.

If you want to personalize certain messages, can you edit the random.json to your liking.  
The default file looks like this:  
> ```json
> {
>   "accept_fuck_msg": [
>     "Which bedroom %s?",
>     "\\*moves with her hand over %s's body* Lets get some privacy sweetie. 😘"
>   ],
>   "blacklist": [
>     "578951136288178216",
>     "591723340130156545",
>     "525029165192183808",
>     "610874380112625735"
>   ],
>   "deny_fuck_msg": [
>     "I'm sorry %s, but I can't right now...",
>     "Can you wait a lil more %s? I don't have time right now.",
>     "\\*gives a kiss* I can't %s. Sorry."
>   ],
>   "kiss_img": [
>     "https://purrbot.site/images/kiss/kiss_001.jpeg",
>     "https://purrbot.site/images/kiss/kiss_002.jpg",
>     "https://purrbot.site/images/kiss/kiss_003.gif",
>     "https://purrbot.site/images/kiss/kiss_004.gif",
>     "https://purrbot.site/images/kiss/kiss_005.gif",
>     "https://purrbot.site/images/kiss/kiss_006.gif",
>     "https://purrbot.site/images/kiss/kiss_007.gif",
>     "https://purrbot.site/images/kiss/kiss_008.gif",
>     "https://purrbot.site/images/kiss/kiss_009.gif",
>     "https://purrbot.site/images/kiss/kiss_010.gif",
>     "https://purrbot.site/images/kiss/kiss_011.gif",
>     "https://purrbot.site/images/kiss/kiss_012.gif",
>     "https://purrbot.site/images/kiss/kiss_013.gif"
>   ],
>   "no_nsfw_msg": [
>     "Please be a naughty person in a NSFW channel %s.",
>     "Nonono! You be a nice guy and go to a NSFW channel with that %s!",
>     "Is it me or does this channel not have a NSFW-label %s?",
>     "Nice try. But you can only use those commands in NSFW channels %s.",
>     "Please be dirty in a NSFW channel %s.",
>     "Trust me %s, it's much more fun to use those in a NSFW channel. :3",
>     "What are you trying to do? Keep your naughtyness in a NSFW channel %s!",
>     "Those naughty things can only be seen and used in NSFW channels %s.",
>     "Error: NoNSFWChannelFoundException (Not actually an error, but I guess you get the hint %s)."
>   ],
>   "shutdown_img": [
>     "https://purrbot.site/images/shutdown/shutdown_001.png",
>     "https://purrbot.site/images/shutdown/shutdown_002.jpg",
>     "https://purrbot.site/images/shutdown/shutdown_003.jpeg",
>     "https://purrbot.site/images/shutdown/shutdown_004.png",
>     "https://purrbot.site/images/shutdown/shutdown_005.jpg",
>     "https://purrbot.site/images/shutdown/shutdown_006.jpg",
>     "https://purrbot.site/images/shutdown/shutdown_007.png",
>     "https://purrbot.site/images/shutdown/shutdown_008.jpg",
>     "https://purrbot.site/images/shutdown/shutdown_009.jpeg",
>     "https://purrbot.site/images/shutdown/shutdown_010.jpeg",
>     "https://purrbot.site/images/shutdown/shutdown_011.jpeg",
>     "https://purrbot.site/images/shutdown/shutdown_012.gif",
>     "https://purrbot.site/images/shutdown/shutdown_013.gif",
>     "https://purrbot.site/images/shutdown/shutdown_014.gif",
>     "https://purrbot.site/images/shutdown/shutdown_015.gif"
>   ],
>   "shutdown_msg": [
>     "Time for a nap UwU",
>     "Bye bye!",
>     "I feel so tired...",
>     "\\*yawns* I go and take a nap now.",
>     "See you later. :3"
>   ],
>   "startup_msg": [
>     "Loading cute nekos...",
>     "Slowly waking up.",
>     "Give Andre_601 headaches",
>     "[Placeholder_text]",
>     "Breaking stuff... again",
>     "Hey look! I'm a startup message!",
>     "Connecting to da interwebz"
>   ],
>   "welcome_bg": [
>     "color_black",
>     "color_blue",
>     "color_green",
>     "color_grey",
>     "color_red",
>     "color_white",
>     "gradient",
>     "gradient_blue",
>     "gradient_dark_red",
>     "gradient_green",
>     "gradient_orange",
>     "gradient_red"
>   ],
>   "welcome_icon": [
>     "neko_hug",
>     "neko_smile",
>     "purr",
>     "snuggle",
>     "senko",
>     "shiro",
>     "holo"
>   ]
> }

## Final words and information
With this things done, you should be able to host and run the bot on your own.  
Please keep in mind, that when hosting the bot on your own, you agree, that:
- You follow the [MIT-License]
- You **don't** ask for support of your self-hosted bot (I won't provide any support)
- You **don't** claim the bot, nor it's code as your very own (Basicly the same as following the MIT-license)
