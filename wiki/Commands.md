# General info
This page lists all available commands of the bot.  
The default prefix is `.` but can be changed with the [[prefix-command|Commands#prefix]]

> ℹ️ **Important**  
> - `Permission:` means discord-permissions which the executor of a command requires for the command.
> - To make the bot work properly are those permissions required.
>   - `Read Messages`
>   - `Send Messages`
>   - `Embed Links`
>   - `Read Message History`
> - Additional permissions required for a specific command are listed under `Bot Requires:`
> - `<argument>` are required and `[argument]` are optional.
> - If an argument contains `...` does this mean that multiple arguments of the same type can be used.

### [Fun](#category-fun)
- [Cuddle](#cuddle)
- [Holo](#holo)
- [Hug](#hug)
- [Kiss](#kiss)
- [Kitsune](#kitsune)
- [Lick](#lick)
- [Neko](#neko)
- [Pat](#pat)
- [Poke](#poke)
- [Ship](#ship)
- [Slap](#slap)
- [Tickle](#tickle)

### [Guild](#category-guild)
- [Language](#language)
- [Prefix](#prefix)
- [Welcome](#welcome)

### [Info](#category-info)
- [Emote](#emote)
- [Guild](#guild)
- [Help](#help)
- [Info](#info)
- [Invite](#invite)
- [Ping](#ping)
- [Quote](#quote)
- [Shards](#shards)
- [Stats](#stats)
- [User](#user)

### [NSFW](#category-nsfw)
- [Fuck](#fuck)
- [Lewd](#lewd)
- [Solo](#solo)
- [Threesome](#threesome)
- [Yurifuck](#yurifuck)

----
## Category: Fun

### Cuddle
> **Permission**: `None`  
> **Bot requires**: `None`  
> 
> **Aliases**:
> - `Cuddles`
> - `Snuggle`
> - `Snuggles`
> - `Squeeze`
> 
> **Usage**:
> - `.Cuddle <@user ...>`

Lets you cuddle one or multiple mentioned users.

----
### Holo
> **Permission**: `None`  
> **Bot requires**: `None`
> 
> **Aliases**:
> - `spiceandwolf`  
> 
> **Usage**:
> - `.Holo`

Gives an image of Holo from the manga and anime "Spice & Wolf". ([Example](https://purrbot.site/img/sfw/holo/img/holo_001.jpg))

----
### Hug
> **Permission**: `None`  
> **Bot requires**: `None`  
> 
> **Aliases**:
> - `Hugging`  
> 
> **Usage**:
> - `.Hug <@user ...>`

Lets you hug one or multiple mentioned user.

----
### Kiss
> **Permission**: `None`  
> **Bot requires**: `None`  
> 
> **Aliases**:
> - `Kissu`
> - `Love`  
> 
> **Usage**:
> - `.Kiss <@user ...>`

Lets you kiss one or multiple mentioned user.

----
### Kitsune
> **Permission**: `None`  
> **Bot requires**: `None`  
> 
> **Aliases**:
> - `Foxgirl`  
> 
> **Usage**:
> - `.Kitsune`

Gives an image of a kitsune (fox girl). ([Example](https://purrbot.site/img/sfw/kitsune/img/kitsune_001.jpg))

----
### Lick
> **Permission**: `None`  
> **Bot requires**: `None`
> 
> **Aliases**: `None`  
>
> **Usage**:
> - `.Lick <@user ...>`

Lets you lick one or multiple mentioned user.

----
### Neko
> **Permission**: `None`  
> **Bot requires**: `None`
> 
> **Aliases**:
> - `Catgirl`
> 
> **Usage**:
> - `.Neko`
> - `.Neko [--gif]`

Gives an image of a Neko (catgirl). ([Example](https://purrbot.site/img/sfw/neko/img/neko_001.jpg))  
`[--gif]` returns a gif of a Neko.

----
### Pat
> **Permission**: `None`  
> **Bot requires**: `None`  
> 
> **Aliases**:
> - `Patting`
> - `Pet`
> 
> **Usage**:
> - `.Pat <@user ...>`

Lets you pat one or multiple mentioned user.

----
### Poke
> **Permission**: `None`  
> **Bot requires**: `None`  
> 
> **Aliases**:
> - `Poking`
> 
> **Usage**:
> - `.Poke <@user ...>`

Lets you poke one or multiple mentioned user.

----
### Ship
> **Permission**: `None`  
> **Bot requires**:
> - `Attach files` (optional for showing a image)  
> 
> **Aliases**:
> - `Shipping`
> 
> **Usage**:
> - `.Ship <@user>`
> - `.Ship <@user1> [@user2]`

Will make the bot show how likely you match with someone else.  
You can optionally mention a second user to instead ship those two with each other.

----
### Slap
> **Permission**: `None`  
> **Bot requires**: `None`  
> 
> **Aliases**: `None`
> 
> **Usage**:
> - `.Slap <@user ...>`

Lets you slap one or multiple mentioned user.

----
### Tickle
> **Permission**: `None`  
> **Bot requires**: `None`  
> 
> **Aliases**: `None`
> 
> **Usage**:
> - `.Tickle <@user ...>`

Lets you tickle one or multiple users.

----
## Category: Guild

### Language
> **Permission**: `Manage Server`  
> **Bot requires**: `None`
>
> **Aliases**:
> - `Lang`
>
> **Usage**:
> - `.Lang [set <language>]`
> - `.Lang [reset]`

Lets you change or reset the current language set in a Discord.  
Supported are all languages listed [here](https://github.com/purrbot-site/Translations#supported-languages)

----
### Prefix
> **Permission**: `Manage server`  
> **Bot requires**: `None`  
> 
> **Aliases**: `None`
> 
> **Usage**:
> - `.Prefix <set <prefix>>`
> - `.Prefix <reset>`

Lets you change or reset the bots prefix for your guild. (Default one is `.`)

----
### Welcome
> **Permission**: `Manage server`  
> **Bot requires**:
> - `Attach files` (Used for welcome-images. See [[Welcome images]] for examples)
> 
> **Aliases**: `None`
> 
> **Usage**:
> - `.Welcome [bg set <background>]`
> - `.Welcome [bg reset]`
> - `.Welcome [channel set <#channel>]`
> - `.Welcome [channel reset]`
> - `.Welcome [color set <color>]`
> - `.Welcome [color reset]`
> - `.Welcome [icon set <icon>]`
> - `.Welcome [icon reset]`
> - `.Welcome [msg set <message>]`
> - `.Welcome [msg reset]`

Lets you set different parts of Purr's welcome-feature.  
Providing no arguments will show what the current settings are.

**About the arguments**:
- `[bg set <background>]`: Sets the background of the image.
- `[bg reset]`: Resets the background.
- `[channel set <#channel>]`: Sets the channel where welcome messages are posted to the mentioned one.
- `[channel reset]`: Resets the channel, effectively disabling the welcome feature.
- `[color set <color>]`: Sets the text color of the text in the image.  
Color can either be `rgb:` with rgb values (`rgb:255,0,0`) or `hex:` with hexadecimal values (`hex:ff0000`)
- `[color reset]`: Resets the color.
- `[icon set <icon>]`: Changes the icon to the provided one. Go to [[Welcome images]] for a list.
- `[icon reset]`: Resets the icon back to the default one ([[Purr|Welcome-images#purr]])
- `[msg set <message>]`: Sets the message to the provided one. The message is shown with the image and you can use placeholders (See below).
- `[msg reset]`: Resets the message back to the normal `Welcome {mention}!`

**Placeholders** (Usable in the `msg set <message>` argument):
- `{count}`: The current member-count of the guild.
- `{guild}`: The name of the guild.
- `{mention}`: The joined user as mention.
- `{name}`: The name of the joined user.

----
## Category: Info

### Emote
> **Permission**: `None`  
> **Bot requires**:
> - `Read message history` (For the `-search` option)
> 
> **Aliases**:
> - `E`
> 
> **Arguments**:
> - `.Emote <:emote:>`
> - `.Emote <--search>`

Gives information about a provided emote.  
If the `--search` argument is used will the bot check the past 100 messages for any emote and return the first one it can find.

----
### Guild
> **Permission**: `None`  
> **Bot requires**: `None`  
> 
> **Aliases**:
> - `Guildinfo`
> - `Server`
> - `Serverinfo`
> 
> **Usage**:
> - `.Guild`

Provides basic information about the guild.

----
### Help
> **Permission**: `None`  
> **Bot requires**:
> - `Add reactions`  
> 
> **Aliases**:
> - `Command`
> - `Commands`
> 
> **Usage**:
> - `.Help`
> - `.Help [command]`

Lists all available commands, or gives information about one, when specified.

----
### Info
> **Permission**: `None`  
> **Bot requires**: `None`  
> 
> **Aliases**:
> - `Infos`
> - `Information`
> 
> **Usage**:
> - `.Info`
> - `.Info [--dm]`

Provides basic information about the bot.  
You can use `--dm` to send this info into your DMs.

----
### Invite
> **Permission**: `None`  
> **Bot requires**: `None`  
> 
> **Aliases**:
> - `Links`
> 
> **Usage**:
> - `.Invite`
> - `.Invite [--dm]`

Sends you links to invite the bot, or join the support-Discord.  
Use `--dm` to send the links to your DMs.

----
### Ping
> **Permission**: `None`  
> **Bot requires**: `None`  
> 
> **Aliases**: `None`
> 
> **Usage**:
> - `.Ping`

Returns the delay of editing the message, the delay to the API and the WebSocket delay in milliseconds.

----
### Quote
> **Permission**: `None`  
> **Bot requires**:
> - `See message history` (For your channel and/or the mentioned channel)  
> 
> **Aliases**: `None`
> 
> **Usage**:
> - `.Quote <MessageID>`
> - `.Quote <MessageID> [#channel]`

Quotes a message from a user, using the [PurrBotAPI](https://purrbot.site/api) to generate an image looking like a Discord-message.  
`<MessageID>` is the ID of the message that should be quoted.  
`[#channel]` is only needed, if the message is in a channel that isn't the same as the one you execute the command in.

The bot won't quote messages from NSFW channels when the channel you're using the command in isn't one.

----
### Shards
> **Permission**: `None`  
> **Bot requires**: `None`
>
> **Aliases**:
> - `Shardinfo`
>
> **Usage**:
> - `.Shard`

Lists all shards, their status, ping and Guild count.

----
### Stats
> **Permission**: `None`  
> **Bot requires**: `None`  
> 
> **Aliases**:
> - `Stat`
> - `Statistic`
> - `Statistics`
> 
> **Usage**:
> - `.Stats`

Shows statistics of the bot.

----
### User
> **Permission**: `None`  
> **Bot requires**: `None`  
> 
> **Aliases**:
> - `Member`
> - `Userinfo`
> - `Userstats`
> 
> **Usage**:
> - `.User [@user]`

Shows information about yourself or a mentioned user.

----
## Category: NSFW

### Fuck
> **Permission**: `None`  
> **Bot requires**: `None`  
> 
> **Aliases**:
> - `Sex`
> 
> **Usage**:
> - `.Fuck <@user>`

**This command can only be run in NSFW-labeled channels!**

Askes the mentioned user to have sex with you.  
The mentioned user can accept or deny the request by clicking on the corresponding reactions of the message.

----
### Lewd
> **Permission**: `None`  
> **Bot requires**: `None`
> 
> **Aliases**: `None`
> 
> **Usage**:
> - `.Lewd`
> - `.Lewd --gif`

**This command can only be run in NSFW-labeled channels!**

Gives an image of a lewd Neko.  
`--gif` returns a gif of a lewd neko.

----
### Solo
> **Permission**: `None`  
> **Bot requires**: `None`  
> 
> **Aliases**:
> - `girl`
> 
> **Usage**:
> - `.Solo`

**This command can only be run in NSFW-labeled channels!**

Gives a gif of a girl *playing* with herself.

----
### Threesome
> **Permission**: `None`  
> **Bot requires**: `None`
>
> **Aliases**:
> - `3some`
>
> **Usage**:
> - `.Threesome <@user1> <@user2>`
> - `.Threesome <@user1> <@user2> --fff`
> - `.Threesome <@user1> <@user2> --mmf`

**This command can only be run in NSFW-labeled channels!**

Similar to [Fuck](#fuck) but allows you to ask two people at once.  
By default is the gif with 1 male and 2 females but can be changed using the following args:  
- `--fff` for only females.
- `--mmf` for 2 males and 1 female

----
### Yurifuck
> **Permission**: `None`  
> **Bot requires**: `None`  
> 
> **Aliases**:
> - `Yfuck`
> - `Ysex`
> - `Yurifuck`
> - `Yurisex`
>
> **Usage**:
> - `.Yurifuck <@user>`

**This command can only be run in NSFW-labeled channels!**

Similar to [Fuck](#fuck), but with two females.  
The mentioned user can accept or deny the request by clicking on the corresponding reactions of the message.
