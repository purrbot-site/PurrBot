<!-- Badges -->
[DiscordImg]: https://img.shields.io/discord/423771795523371019.svg?colorB=%237289DA
[IssuesImg]: https://img.shields.io/github/issues/Andre601/PurrBot.svg
[LicenseImg]: https://img.shields.io/github/license/Andre601/PurrBot.svg
[License]: https://github.com/Andre601/PurrBot/blob/master/LICENSE

<!-- Discord OAuth -->
[Full invite]: https://discordapp.com/oauth2/authorize?scope=bot&client_id=425382319449309197&permissions=537259072
[Basic invite]: https://discordapp.com/oauth2/authorize?scope=bot&client_id=425382319449309197&permissions=85056

<!-- Website links -->
[Website]: https://purrbot.site
[Discord]: https://purrbot.site/discord

<!-- GitHub links -->
[Issues]: https://github.com/Andre601/PurrBot/issues
[Wiki]: https://github.com/Andre601/PurrBot/wiki
[image]: https://github.com/Andre601/PurrBot/wiki/Welcome-images

<!-- Other links -->
[nekos.life]: https://nekos.life
[whatthecommit]: https://whatthecommit.com

<!-- Widgets -->
[widgetBotlist]: https://api.botlist.space/widget/425382319449309197/6  
[widgetDbl]: https://discordbots.org/api/widget/425382319449309197.svg  

<!-- Botlists -->
[botlist]: https://botlist.space/bot/425382319449309197  
[dbl]: https://discordbots.org/bot/425382319449309197  
[discordBots]: https://discord.bots.gg/bots/425382319449309197  
[lbots]: https://lbots.org/bots/Purr

[![DiscordImg]][Discord] [![IssuesImg]][Issues] [![LicenseImg]][License]

# \*Purr*
This bot was made to use the [nekos.life]-API.  
What started as a simple and easy bot became quite a big bot, that is now even on the official Discord of Nekos.life!

# Commands
**Default Prefix**: `.` (You can also mention the bot or change the prefix with the prefix command.)

`Permission` is the permission you require to use the commands of that category.  
`<arguments>` are required, `[arguments]` are optional and `...` means you can use multiple arguments of the same type.

## Fun
**Permission**: `None`

| Command: | Arguments:          | Description:                                                          |
| -------- | ------------------- | --------------------------------------------------------------------- |
| Cuddle   | `<@user ...>`       | Cuddles the mentioned user(s).                                        |
| Fakegit  |                     | Creates a fake commit-message with help of [whatthecommit]            |
| Holo     |                     | Gives an image of Holo from Spice and wolf.                           |
| Hug      | `<@user ...>`       | Hugs the mentioned user(s).                                           |
| Kiss     | `<@user ...>`       | Kisses the mentioned user(s).                                         |
| Kitsune  |                     | Shows a image of a kitsune (foxgirl)                                  |
| Lick     | `<@user ...>`       | Licks the mentioned user(s).                                          |
| Neko     |                     | Shows a image of a neko from nekos.life.                              |
|          | `[--gif]`           | Shows a gif-image of a neko from nekos.life.                          |
|          | `[--slide]`         | Creates a slideshow with 30 images (Combination with --gif possible). |
| Pat      | `<@user ...>`       | Pats the mentioned user(s).                                           |
| Poke     | `<@user ...>`       | Pokes the mentioned user(s).                                          |
| Ship     | `<@user>` `[@user]` | Ships you (or another user) with someone.                             |
| Slap     | `<@user ...>`       | Slaps the mentioned user(s).                                          |
| Tickle   | `<@user ...>`       | Tickles the mentioned user(s).                                        |

## Guild
**Permission**: `Manage Server`

| Command: | Arguments:                   | Description:                                                      |
| -------- | ---------------------------- | ----------------------------------------------------------------- |
| Prefix   |                              | Shows the current prefix for the Discord.                         |
|          | `set <prefix>`               | Sets the provided prefix for the Guild.                           |
|          | `reset`                      | Resets the prefix to the default one.                             |
| Welcome  |                              | Shows the current welcome-settings.                               |
|          | `channel set <#channel>`     | Change the channel to the mentioned one.                          |
|          | `channel reset`              | Resets the channel.                                               |
|          | `color set <color>`          | Change the text color. Supported args are hex:rrggbb or rgb:r,g,b |
|          | `color reset`                | Resets the text color.                                            |
|          | `image set <image>`          | Changes the image. `<image>` can be a [image] from the wiki.      |
|          | `image reset`                | Resets the image.                                                 |
|          | `msg set <msg>`              | Changes the greeting message.                                     |
|          | `msg reset`                  | Resets the greeting message to "Welcome {mention}!"               |
|          | `test [image] [color] [msg]` | Creates a test-image with an optional image and text color.       |

## Info
**Permission**: `None`

| Command: | Arguments:                 | Description:                                                                           |
| -------- | -------------------------- | -------------------------------------------------------------------------------------- |
| Emote    | `<:emote:>`                | Displays info about an emote.                                                          |
|          | `[--search]`               | The bot will try to find an emote in the last 100 messages.                            |
| Guild    |                            | Shows info about the guild.                                                            |
| Help     |                            | Will display all commands available.                                                   |
|          | `[command]`                | Shows info about the provided command.                                                 |
| Info     |                            | Shows some info about the bot.                                                         |
|          | `[--dm]`                   | Sends the info in DM.                                                                  |
| Invite   |                            | Shows you some links.                                                                  |
|          | `[--dm]`                   | Sends you the links in DM.                                                             |
| Ping     |                            | Checks the ping. (Time the bot takes to edit the message.)                             |
|          | `[--api]`                  | Checks the ping to the Discord-API.                                                    |
| Quote    | `<messageID>` `[#channel]` | Quotes a message. It has to be in the same channel or the channel has to be mentioned. |
| Stats    |                            | Shows stats about the Bot. (Discords she's online, Text and VoiceChannels, etc.)       |
| User     |                            | Gives info about you.                                                                  |
|          | `[@user]`                  | Gives info about the mentioned user.                                                   |

## NSFW
**Permission**: `None`  
**Extra**: This command ONLY works in NSFW-labeled channels.

| Command: | Arguments:  | Description:                                                                          |
| -------- | ----------- | ------------------------------------------------------------------------------------- |
| Fuck     | `<@user>`   | Sends a invite to a user to have sex with you. He/she can accept or deny the request. |
| Lesbian  |             | Returns a gif of lesbian.                                                             |
| Lewd     |             | Shows a image of a lewd neko from nekos.life.                                         |
|          | `[--gif]`   | Shows a gif-image of a lewd neko from nekos.life.                                     |
|          | `[--slide]` | Creates a slideshow with 30 images (Combination with --gif possible)                  |
| Yurifuck | `<@user>`   | Similar like .fuck, but with females only >wO                                         |

# Inviting the bot
The bot has two invite-links: A full invite and a basic invite.

## Full invite
The link for the full invite gives \*Purr* all permissions, to work properly and without any issues.
The permissions include:
* Managing messages: Will be used to delete certain commands of users like `.invite` or `.info`
* Adding reactions: Nothing really special. Just makes \*Purr*'s responses a bit different.
* Attach files: Used for the welcome-channel (`.welcome`), to create a image for the joined user.
* Use of external emojis: For using external (custom) emojis.
* All permissions of the basic invite.

## Basic invite
The basic invite will only give the most necessary permissions for \*Purr* to work correctly.
This permissions are:
* See messages: Let \*Purr* see all channels, that don't have channel-specific permissions.
* Send messages: Let her send messages.
* Embed Links: \*Purr* won't work without this permission!

# Voting
\*Purr* has a integrated Votelistener that gives you a reward (currently just a role) on the [Discord].  
Just make sure to be on the guild and vote for the bot, to receive the special role.  
Future rewards may be added.

# Botlists
\*Purr* can be found on different Botlist sites.  
Feel free to upvote or add her to your favourites to show your support.

### [Botlist.space][botlist]
> Explore hundreds of Discord bots in our bot list for your next big server using our large selection of popular bots.

![widgetBotlist]

### [Discordbots.org][dbl]
> Spice up your Discord experience with our diverse range of Discord bots

![widgetDbl]

### [Discord.bots.gg][discordBots]
> The original Discord bot list, find the right bot for your server today.

### [LBots.org][lbots]
> A bot listing website that loves lewds~

# Usefull Links
* [Discord Server][Discord]
* [Full invite] (Recommended)
* [Basic Invite]
* [Website]
* [Wiki]
