<!-- Badges -->
[DiscordImg]: https://img.shields.io/discord/423771795523371019?color=%237289DA&label=Chat&logo=Discord&logoColor=white&style=plastic
[IssuesImg]: https://img.shields.io/github/issues/Andre601/PurrBot?label=Issues&logo=GitHub&style=plastic
[LicenseImg]: https://img.shields.io/github/license/Andre601/PurrBot.svg?label=License&logo=GitHub&style=plastic

<!-- Discord OAuth -->
[Full invite]: https://discordapp.com/oauth2/authorize?scope=bot&client_id=425382319449309197&permissions=537258048
[Basic invite]: https://discordapp.com/oauth2/authorize?scope=bot&client_id=425382319449309197&permissions=346176

<!-- Website links -->
[Website]: https://purrbot.site
[Discord]: https://purrbot.site/discord

<!-- GitHub links -->
[Issues]: https://github.com/Andre601/PurrBot/issues
[Wiki]: https://github.com/Andre601/PurrBot/wiki
[image]: https://github.com/Andre601/PurrBot/wiki/Welcome-images
[License]: https://github.com/Andre601/PurrBot/blob/master/LICENSE
[background]: https://github.com/Andre601/PurrBot/wiki/Welcome-images#backgrounds
[icon]: https://github.com/Andre601/PurrBot/wiki/Welcome-images#icons
[translations]: https://github.com/purrbot-site/Translations

<!-- Other links -->
[nekos.life]: https://nekos.life

<!-- Widgets -->
[widgetBotlist]: https://api.botlist.space/widget/425382319449309197/6  
[widgetDebl]: https://api.discordextremelist.xyz/v1/bot/425382319449309197/widget  

<!-- Botlists -->
[botlist]: https://botlist.space/bot/425382319449309197  
[discordBots]: https://discord.bots.gg/bots/425382319449309197  
[debl]: https://discordextremelist.xyz/bots/purr  
[lbots]: https://lbots.org/bots/Purr

[![DiscordImg]][Discord] [![IssuesImg]][Issues] [![LicenseImg]][License]

# \*Purr*
This bot was made to use the [nekos.life]-API but now moved to its very own API.

# Commands
**Default Prefix**: `.` (You can mention the bot to know the used prefix or change it with the prefix-command)

`Permission` is the permission you require in order to use the commands of that category.  
`<arguments>` are required, `[arguments]` are optional and `...` means you can use multiple arguments of the same type.

## Fun
**Permission**: `None`

| Command: | Arguments:          | Description:                                |
| -------- | ------------------- | ------------------------------------------- |
| Cuddle   | `<@user ...>`       | Cuddles the mentioned user(s).              |
| Holo     |                     | Gives an image of Holo from Spice and wolf. |
| Hug      | `<@user ...>`       | Hugs the mentioned user(s).                 |
| Kiss     | `<@user ...>`       | Kisses the mentioned user(s).               |
| Kitsune  |                     | Shows a image of a kitsune (foxgirl)        |
| Lick     | `<@user ...>`       | Licks the mentioned user(s).                |
| Neko     |                     | Shows a image of a neko (catgirl).          |
|          | `[--gif]`           | Shows a gif of a neko (catgirl).            |
| Pat      | `<@user ...>`       | Pats the mentioned user(s).                 |
| Poke     | `<@user ...>`       | Pokes the mentioned user(s).                |
| Senko    |                     | Shows a image of Senko-San.                 |
| Ship     | `<@user>`           | Ships you with the mentioned user.          |
|          | `<@user>` `[@user]` | Ships the first with the second user.       |
| Slap     | `<@user ...>`       | Slaps the mentioned user(s).                |
| Tickle   | `<@user ...>`       | Tickles the mentioned user(s).              |

## Guild
**Permission**: `Manage Server`

| Command: | Arguments:               | Description:                                                      |
| -------- | ------------------------ | ----------------------------------------------------------------- |
| Language |                          | Shows the currently used and all available languages.             |
|          | `set <language>`         | Sets the language for the Guild to a supported one.               |
|          | `reset`                  | Resets the language to English (en).                              |
| Prefix   | `set <prefix>`           | Sets the provided prefix for the Guild.                           |
|          | `reset`                  | Resets the prefix to the default one.                             |
| Welcome  |                          | Shows the current welcome-settings.                               |
|          | `bg set <background>`    | Sets the [background] of the image.                               |
|          | `bg reset`               | Resets the background to the default one (`color_white`)          |
|          | `channel set <#channel>` | Change the channel to the mentioned one.                          |
|          | `channel reset`          | Resets the channel.                                               |
|          | `color set <color>`      | Change the text color. Supported args are hex:rrggbb or rgb:r,g,b |
|          | `color reset`            | Resets the text color.                                            |
|          | `icon set <image>`       | Sets the [icon] of the image.                                     |
|          | `icon reset`             | Resets the icon.                                                  |
|          | `msg set <msg>`          | Changes the greeting message.                                     |
|          | `msg reset`              | Resets the greeting message to "Welcome {mention}!"               |

## Info
**Permission**: `None`

| Command: | Arguments:                 | Description:                                                                       |
| -------- | -------------------------- | ---------------------------------------------------------------------------------- |
| Emote    | `<:emote:>`                | Displays info about an emote.                                                      |
|          | `[--search]`               | The bot will try to find an emote in the last 100 messages.                        |
| Guild    |                            | Shows info about the guild.                                                        |
| Help     |                            | Will display all commands available.                                               |
|          | `[command]`                | Shows info about the provided command.                                             |
| Info     |                            | Shows some info about the bot.                                                     |
|          | `[--dm]`                   | Sends the info in DM.                                                              |
| Invite   |                            | Shows you some links.                                                              |
|          | `[--dm]`                   | Sends you the links in DM.                                                         |
| Ping     |                            | Checks the ping. (Time the bot takes to edit the message, rest-API and websocket.) |
| Quote    | `<messageId>`              | Quotes a message from the same channel the command was used in.                    |
|          | `<messageId>` `[#channel]` | Quotes a message from the mentioned channel.                                       |
| Stats    |                            | Shows stats about the Bot. (Discords she's online, Text and VoiceChannels, etc.)   |
| User     |                            | Gives info about you.                                                              |
|          | `[@user]`                  | Gives info about the mentioned user.                                               |

## NSFW
**Permission**: `None`  
**Extra**: This command ONLY works in NSFW-labeled channels.

| Command:  | Arguments:                      | Description:                                                                        |
| --------- | ------------------------------- | ----------------------------------------------------------------------------------- |
| Fuck      | `<@user>`                       | Sends a invite to a user to have sex with you. They can accept or deny the request. |
|           | `<@user>` `[--anal]`            | Gives you a gif with anal sex.                                                      |
| Lewd      |                                 | Shows a image of a lewd neko (catgirl).                                             |
|           | `[--gif]`                       | Shows a gif of a lewd neko (catgirl).                                               |
| Pussylick | `<@user>`                       | Similar to `.fuck`, but lets you lick someone's pussy (if they have one that is...) |
| Solo      |                                 | Gives a gif of a girl *playing* with herself.                                       |
| Threesome | `<@user1>` `<@user2>`           | Similar to `.fuck`, but lets you have sex with 2 people.                            |
|           | `<@user1>` `<@user2>` `[--fff]` | Gives you a gif with females only instead of the default 1 male, 2 females.         |
|           | `<@user1>` `<@user2>` `[--mmf]` | Gives you a gif with 2 males and 1 female instead of the default 1 male, 2 females. |
| Yurifuck  | `<@user>`                       | Similar to `.fuck`, but with females only \>wO                                      |

# Translations
Version 2.5 of \*Purr* added an option to have a per-guild language set.  
We are happy about any translations being made for the bot.

If you want to contribute to the translations of the bot (Adding your own, or correcting others), take a look at the [GitHub Repository][translations] for this.

# Inviting the bot
The bot has two invite-links: A full invite and a basic invite.

## Full invite
The link for the full invite gives \*Purr* all permissions, to work properly and without any issues.
The permissions include:
- Managing messages: Will be used to delete certain commands of users like `.invite` or `.info`
- Adding reactions: Nothing really special. Just makes \*Purr*'s responses a bit different.
- Attach files: Used for the welcome-channel (`.welcome`), to create a image for the joined user.
- Use of external emojis: For using external (custom) emojis.
- All permissions of the basic invite.

## Basic invite
The basic invite will only give the most necessary permissions for \*Purr* to work correctly.
This permissions are:
- See messages: Let \*Purr* see all channels, that don't have channel-specific permissions.
- See message history: Let \*Purr* see the message history of a channel.
- Send messages: Let her send messages.
- Embed Links: \*Purr* won't work without this permission!

# Botlists
\*Purr* can be found on different Botlist sites.  
Feel free to upvote or add her to your favourites to show your support.

### [Botlist.space][botlist]
> Explore hundreds of Discord bots in our bot list for your next big server using our large selection of popular bots.

![widgetBotlist]

### [Discord.bots.gg][discordBots]
> The original Discord bot list, find the right bot for your server today.

### [Discordextremelist.xyz][debl]
> Discord's unbiased list, giving small bots and small servers a big chance!

### [LBots.org][lbots]
> A bot listing website that loves lewds~

# Usefull Links
* [Discord Server][Discord]
* [Full invite] (Recommended)
* [Basic Invite]
* [Website]
* [Wiki]
