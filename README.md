[![Discord Bots](https://discordbots.org/api/widget/425382319449309197.svg)](https://discordbots.org/bot/425382319449309197)

# \*Purr*
This bot was made to use the [nekos.life](https://nekos.life)-API.  
What started as a simple and easy bot became quite a big bot, that is now even on the official Discord of Nekos.life!

# Commands
The default command-prefix is `.` but can be changed with the prefix-command.  
`<arguments>` are required and `[arguments]` are optional.

## Informative
**Permission**: No permissions required.
```
Command: | Arguments:  | Description:

Help     |             | Will display all commands available.
         | [command]   | Shows info about the provided command.
Info     |             | DMs you info about the bot.
         | [-here]     | Shows the info in the channel you run the command.
Invite   |             | DMs you the invite-link.
         | [-here]     | Shows the Invite in the channel you currently are.
Quote    | <messageID> | Quotes a message from a user. The message needs to be in the same channel!
Server   |             | Shows info about the Discord, the bot is currently in.
Stats    |             | Shows stats about the Bot. (Discords she's online, Text and VoiceChannels, ect.)
User     |             | Gives info about you.
         | [@user]     | Gives info about the mentioned user.
```

## Fun
**Permission**: No permission required.
```
Command: | Arguments:  | Description:

Cuddle   | <@user ...> | Cuddles the mentioned user(s).
Hug      | <@user ...> | Hugs the mentioned user(s).
Kiss     | <@user ...> | Kisses the mentioned user(s).
Neko     |             | Shows a image of a neko from nekos.life.
         | [-gif]      | Shows a gif-image of a neko from nekos.life.
         | [-slide]    | Creates a slideshow with 30 images (Can be combined with the -gif argument).
Pat      | <@user ...> | Pats the mentioned user(s).
Slap     | <@user ...> | Slaps the mentioned user(s).
Tickle   | <@user ...> | Tickles the mentioned user(s).
```

## NSFW
**Permission**: No permission required.  
**Extra**: This command ONLY works in NSFW-labeled channels.
```
Command: | Arguments: | Description:

Lewd     |            | Shows a image of a lewd neko from nekos.life.
         | [-gif]     | Shows a gif-image of a lewd neko from nekos.life.
         | [-slide]   | Creates a slideshow with 30 images (Can be combined with the -gif argument)
lesbian  |            | Returns a gif of lesbian.
```

## Server
**Permission**: `MANAGE_SERVER` permission is required for the user.
```
Command: | Arguments:              | Description:

Prefix   |                         | Shows the current prefix for the Discord.
         | set <prefix>            | Sets the provided prefix for the Guild.
         | reset                   | Resets the prefix to the default one.
Welcome  |                         | Shows the current welcome-settings (welcome-channel, image and textcolor).
         | color <color>           | Sets the textcolor of the welcome-image. Format is either rgb:r,g,b or hex:rrggbb
         | set <ChannelID> [image] | Sets the welcome-channel to the provided one with a optional image.
         | reset                   | Resets the welcome-settings.
         | test <image> [color]    | Creates a test-image with the provided image and (optionally) textcolor.
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

# Usefull Links
* [Discord Server](https://discord.gg/NB7AFqn)
* [Recommended (Full) Invite](https://discordapp.com/oauth2/authorize?client_id=425382319449309197&permissions=322624&scope=bot)
* [Basic Invite](https://discordapp.com/oauth2/authorize?client_id=425382319449309197&permissions=19456&scope=bot)
