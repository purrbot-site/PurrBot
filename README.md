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

<!-- Other links -->
[nekos.life]: https://nekos.life

<!-- Widgets -->
[widgetBotlist]: https://api.botlist.space/widget/425382319449309197/6  
[widgetTop]: https://top.gg/api/widget/425382319449309197.svg  
[widgetdebl]: https://discordextremelist.xyz/api/bot/425382319449309197/widget  

<!-- Botlists -->
[botlist]: https://botlist.space/bot/425382319449309197  
[top]: https://top.gg/bot/425382319449309197  
[discordBots]: https://discord.bots.gg/bots/425382319449309197  
[debl]: https://discordextremelist.xyz/bots/purr  
[lbots]: https://lbots.org/bots/Purr

<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[contributorBadge]: https://img.shields.io/badge/Contributors_✨-5-green.svg?style=plastic
<!-- ALL-CONTRIBUTORS-BADGE:END -->

[![DiscordImg]][Discord] [![IssuesImg]][Issues] [![LicenseImg]][License] [![contributorBadge]](#contributors-)

# \*Purr*
This bot was made to use the [nekos.life]-API but now moved to its very own API.

# Commands
**Default Prefix**: `.` (You can mention the bot to know the used prefix or change it with the prefix-command)

`Permission` is the permission you require in order to use the commands of that category.  
`<arguments>` are required, `[arguments]` are optional and `...` means you can use multiple arguments of the same type.

## Fun
**Permission**: `None`

| Command: | Arguments:          | Description:                                               |
| -------- | ------------------- | ---------------------------------------------------------- |
| Cuddle   | `<@user ...>`       | Cuddles the mentioned user(s).                             |
| Holo     |                     | Gives an image of Holo from Spice and wolf.                |
| Hug      | `<@user ...>`       | Hugs the mentioned user(s).                                |
| Kiss     | `<@user ...>`       | Kisses the mentioned user(s).                              |
| Kitsune  |                     | Shows a image of a kitsune (foxgirl)                       |
| Lick     | `<@user ...>`       | Licks the mentioned user(s).                               |
| Neko     |                     | Shows a image of a neko (catgirl).                         |
|          | `[--gif]`           | Shows a gif of a neko (catgirl).                           |
| Pat      | `<@user ...>`       | Pats the mentioned user(s).                                |
| Poke     | `<@user ...>`       | Pokes the mentioned user(s).                               |
| Ship     | `<@user>` `[@user]` | Ships you (or another user) with someone.                  |
| Slap     | `<@user ...>`       | Slaps the mentioned user(s).                               |
| Tickle   | `<@user ...>`       | Tickles the mentioned user(s).                             |

## Guild
**Permission**: `Manage Server`

| Command: | Arguments:                   | Description:                                                      |
| -------- | ---------------------------- | ----------------------------------------------------------------- |
| Prefix   | `set <prefix>`               | Sets the provided prefix for the Guild.                           |
|          | `reset`                      | Resets the prefix to the default one.                             |
| Welcome  |                              | Shows the current welcome-settings.                               |
|          | `bg set <background>`        | Sets the [background] of the image.                               |
|          | `bg reset`                   | Resets the background to the default one (`color_white`)          |
|          | `channel set <#channel>`     | Change the channel to the mentioned one.                          |
|          | `channel reset`              | Resets the channel.                                               |
|          | `color set <color>`          | Change the text color. Supported args are hex:rrggbb or rgb:r,g,b |
|          | `color reset`                | Resets the text color.                                            |
|          | `icon set <image>`           | Sets the [icon] of the image.                                     |
|          | `icon reset`                 | Resets the icon.                                                  |
|          | `msg set <msg>`              | Changes the greeting message.                                     |
|          | `msg reset`                  | Resets the greeting message to "Welcome {mention}!"               |

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
| Ping     |                            | Checks the ping. (Time the bot takes to edit the message, rest-API and websocket.)     |
| Quote    | `<messageID>` `[#channel]` | Quotes a message. It has to be in the same channel or the channel has to be mentioned. |
| Stats    |                            | Shows stats about the Bot. (Discords she's online, Text and VoiceChannels, etc.)       |
| User     |                            | Gives info about you.                                                                  |
|          | `[@user]`                  | Gives info about the mentioned user.                                                   |

## NSFW
**Permission**: `None`  
**Extra**: This command ONLY works in NSFW-labeled channels.

| Command:  | Arguments:            | Description:                                                                        |
| --------- | --------------------- | ----------------------------------------------------------------------------------- |
| Fuck      | `<@user>`             | Sends a invite to a user to have sex with you. They can accept or deny the request. |
| Lewd      |                       | Shows a image of a lewd neko (catgirl).                                             |
|           | `[--gif]`             | Shows a gif of a lewd neko (catgirl).                                               |
| Solo      |                       | Gives a gif of a girl *playing* with herself.                                       |
| Threesome | `<@user1>` `<@user2>` | Similar to `.fuck`, but lets you have sex with 2 people.                            |
|           | `[--fff]`             | Gives you a gif with females only instead of the default 1 male, 2 females.         |
|           | `[--mmf]`             | Gives you a gif with 2 males and 1 female instead of the default 1 male, 2 females. |
| Yurifuck  | `<@user>`             | Similar to `.fuck`, but with females only \>wO                                      |

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
\*Purr* has a integrated Vote listener that gives you a reward (currently just a role) on the [Discord].  
Just make sure to be on the guild and vote for the bot, to receive the special role.  
Future rewards may be added.

# Botlists
\*Purr* can be found on different Botlist sites.  
Feel free to upvote or add her to your favourites to show your support.

### [Botlist.space][botlist]
> Explore hundreds of Discord bots in our bot list for your next big server using our large selection of popular bots.

![widgetBotlist]

### [top.gg][top] (Formerly discordbots.org)
> Spice up your Discord experience with our diverse range of Discord bots

![widgetTop]

### [Discord.bots.gg][discordBots]
> The original Discord bot list, find the right bot for your server today.

### [Discordextremelist.xyz][debl]
> Discord's unbiased list, giving small bots and small servers a big chance!

![widgetDebl]

### [LBots.org][lbots]
> A bot listing website that loves lewds~

# Usefull Links
* [Discord Server][Discord]
* [Full invite] (Recommended)
* [Basic Invite]
* [Website]
* [Wiki]

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://nekos.life"><img src="https://avatars2.githubusercontent.com/u/21271709?v=4" width="100px;" alt=""/><br /><sub><b>Tails</b></sub></a><br /><a href="https://github.com/Andre601/PurrBot/commits?author=IamTails" title="Code">💻</a></td>
    <td align="center"><a href="https://rainestormee.github.io"><img src="https://avatars1.githubusercontent.com/u/26597492?v=4" width="100px;" alt=""/><br /><sub><b>Ryan Arrowsmith</b></sub></a><br /><a href="https://github.com/Andre601/PurrBot/commits?author=rainestormee" title="Code">💻</a></td>
    <td align="center"><a href="http://linkedin.dv8tion.net"><img src="https://avatars1.githubusercontent.com/u/1479909?v=4" width="100px;" alt=""/><br /><sub><b>Austin Keener</b></sub></a><br /><a href="#plugin-DV8FromTheWorld" title="Plugin/utility libraries">🔌</a></td>
    <td align="center"><a href="https://www.piggypiglet.me"><img src="https://avatars3.githubusercontent.com/u/11957313?v=4" width="100px;" alt=""/><br /><sub><b>PiggyPiglet</b></sub></a><br /><a href="https://github.com/Andre601/PurrBot/commits?author=PiggyPiglet" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/Katinor"><img src="https://avatars3.githubusercontent.com/u/16473891?v=4" width="100px;" alt=""/><br /><sub><b>Oh Hyejun</b></sub></a><br /><a href="#translation-Katinor" title="Translation">🌍</a></td>
  </tr>
</table>

<!-- markdownlint-enable -->
<!-- prettier-ignore-end -->
<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
