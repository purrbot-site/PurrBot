[![](https://discordbots.org/api/widget/425382319449309197.svg)](https://discordbots.org/bot/425382319449309197)
[![](https://discordbots.co.uk/api/bots/425382319449309197.svg)](https://discordbots.co.uk/bots/425382319449309197)

# \*Purr*
This bot was made to use the [nekos.life](https://nekos.life)-API.  
What started as a simple and easy bot became quite a big bot, that is now even on the official Discord of Nekos.life!

# Commands
The default command-prefix is `.` but can be changed with the prefix-command.  
`<arguments>` are required and `[arguments]` are optional.

## Fun
**Permission**: No permission required.
```
Command: | Arguments:  | Description:

Cuddle   | <@user ...> | Cuddles the mentioned user(s).
Fakegit  |             | Creates a fake commit-message with help of https://whatthecommit.com
Hug      | <@user ...> | Hugs the mentioned user(s).
Kiss     | <@user ...> | Kisses the mentioned user(s).
Kitsune  |             | Shows a image of a kitsune (foxgirl)
Neko     |             | Shows a image of a neko from nekos.life.
         | [-gif]      | Shows a gif-image of a neko from nekos.life.
         | [-slide]    | Creates a slideshow with 30 images (Can be combined with the -gif argument).
Pat      | <@user ...> | Pats the mentioned user(s).
Slap     | <@user ...> | Slaps the mentioned user(s).
Tickle   | <@user ...> | Tickles the mentioned user(s).
```

## Guild
**Permission**: `MANAGE_SERVER` permission is required for the user.
```
Command: | Arguments:              | Description:

Debug    |                         | Creates a debug-file on debug.scarsz.me
Prefix   |                         | Shows the current prefix for the Discord.
         | set <prefix>            | Sets the provided prefix for the Guild.
         | reset                   | Resets the prefix to the default one.
Welcome  |                         | Shows the current welcome-settings (welcome-channel, image and textcolor).
         | channel set <#channel>  | Change the channel to the mentioned one.
         | channel reset           | Resets the channel.
         | color set <color>       | Change the text color. Supported args are hex:rrggbb or rgb:r,g,b
         | color reset             | Resets the text color.
         | image set <image>       | Changes the image.
         | image reset             | Resets the image.
         | test [image] [color]    | Creates a test-image with an optional image and text color.
```

## Info
**Permission**: No permissions required.
```
Command: | Arguments:             | Description:

Emote    | <:emote:>              | Displays info about an emote.
Guild    |                        | Shows info about the guild.
Help     |                        | Will display all commands available.
         | [command]              | Shows info about the provided command.
Info     |                        | Shows some info about the bot.
         | [-dm]                  | Sends the info in DM.
Invite   |                        | Shows you some links.
         | [-dm]                  | Sends you the links in DM.
Ping     |                        | Checks the ping. (Time the bot takes to edit the message.)
         | [-api]                 | Checks the ping to the Discord-API.
Quote    | <messageID> <#channel> | Quotes a message from a user. The message needs to be in the mentioned channel!
Stats    |                        | Shows stats about the Bot. (Discords she's online, Text and VoiceChannels, etc.)
User     |                        | Gives info about you.
         | [@user]                | Gives info about the mentioned user.
```

## NSFW
**Permission**: No permission required.  
**Extra**: This command ONLY works in NSFW-labeled channels.
```
Command: | Arguments: | Description:

Fuck     | <@user>    | Sends a invite to a user to have sex with you. He/she can accept it with >accept
Lesbian  |            | Returns a gif of lesbian.
Lewd     |            | Shows a image of a lewd neko from nekos.life.
         | [-gif]     | Shows a gif-image of a lewd neko from nekos.life.
         | [-slide]   | Creates a slideshow with 30 images (Can be combined with the -gif argument)
```

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
\*Purr* now has a integrated Votelistener that gives you a reward (currently just a role) on the [support-guild](https://discord.gg/NB7AFqn).
Just make sure to be on the guild and vote for the bot, to receive the special role.
Future rewards may be added.

# Usefull Links
* [Discord Server](https://discord.gg/NB7AFqn)
* [Recommended (Full) Invite](https://discordapp.com/oauth2/authorize?client_id=425382319449309197&permissions=322624&scope=bot)
* [Basic Invite](https://discordapp.com/oauth2/authorize?client_id=425382319449309197&permissions=19456&scope=bot)
* [Web dashboard](https://purrbot.site) | [Source code](https://github.com/Andre601/PurrBot-Dashboard)
* [Wiki](https://github.com/Andre601/PurrBot/wiki)
