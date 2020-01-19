The welcome channel allows you to greet people with a message and image when they join your Discord.

## Notes
Before you set up the channel, make sure you have made the following checks first:
- You have `manage server` permission or are the Owner of the Discord.
- The Bot has `read messages`, `send messages` and `attach files` permission in the channel where welcome messages should be posted.

Also for simplicity reasons will we use the default prefix (`.`) here.  
If you have changed your prefix, use the one that you set instead.

## Step 1: Set a channel
> **Required step?** Yes  
> **Default**: `None`

You first have to set a channel, before you can greet people.  
To do that, run `.welcome channel set #channel` where `#channel` is the channel you want to use for greeting people.

Reset this using `.welcome channel reset`

## Step 2: Set a background
> **Required step?** No  
> **Default**: [[color_white|Welcome Images#color_white]]

Set a background that will be used on the image.  
The syntax is `.welcome bg set <background>` where `<background>` is one of the [[available backgrounds|Welcome Images#backgrounds]].

Reset this using `.welcome bg reset`

## Step 3: Set an icon
> **Required step?** No  
> **Default**: [[purr|Welcome Images#purr]]

You can set an icon, which is shown on the right side of the image.  
Use `.welcome icon set <icon>` where `<icon>` is one of the [[available icons|Welcome Images#icons]].

Reset this using `.welcome icon reset`

## Step 4: Set a text color
> **Required step?** No  
> **Default**: `hex:000000`

The default font color isn't visible on all backgrounds. For that can you change it with `.welcome color set <color>`.  
`<color>` has to be either `hex:rrggbb` or `rgb:r,g,b`.

Reset this using `.welcome color reset`

## Step 5: Set a message
> **Required step?** No  
> **Default**: `Welcome {mention}!`

You can set your very own welcome message that is shown next to the image.  
To do that run `.welcome msg set <message>` where `<message>` can be anything you want.  
You can also use placeholders:
- `{count}` The member count of the Discord
- `{guild}` The name of the Discord
- `{mention}` The joined user as a mention
- `{name}` The name of the joined user

Reset this using `.welcome msg reset`

## Final Step: Testing
> **Required step?** No  
> **Default**: `Uses saved values`

You can see the current image by just using `.welcome` without any arguments.  
The image will be shown at the bottom of the embed.
