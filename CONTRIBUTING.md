## About
I appreciate any contribution that helps making this bot better.  
But before you start working on things and make a PR, read this page here first.

### Translations
This is not the place to commit your translations to.  
If you want to help translating your bot to another language, go to the [Translation Repository] and PR your changes there.

### Wiki
You can help improve the [Wiki] by commiting changes to the [wiki folder] of this repository.  
Try to follow the general syntax of a page and make it as good looking as possible.

### Bot
When you want to commit changes to the bot, make sure to follow specific rules.

#### Avoid static methods
The bot tries its best to not use static methods. Instead are classes loaded through DI (Dependency injection).  
For example do we access the DBUtil through `PurrBot#getDbUtil()` and not through static methods.
