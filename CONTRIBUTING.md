[Translations]: https://github.com/purrbot-site/Translations
[Wiki]: https://docs.purrbot.site/bot
[Doc-repository]: https://github.com/purrbot-site/Doc

## About
I appreciate any contribution that helps making this bot better.  
But before you start working on things and make a PR, read this page here first.

### Translations
This is not the place to commit your translations to.  
If you want to help translating your bot to another language, go to the [Translation Repository][Translations] and PR your changes there.

### Wiki
If you want to help towards the [wiki], head over to the [Doc-repository] and make sure to follow the guidelines mentioned on the Readme file.

### Bot
When you want to commit changes to the bot, make sure to follow specific rules.

#### Avoid static methods
The bot tries its best to not use static methods. Instead are classes loaded through DI (Dependency injection).  
For example do we access the DBUtil through `PurrBot#getDbUtil()` and not through static methods.
