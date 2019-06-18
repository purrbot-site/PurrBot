# About
As a developer do I (Andre601) appreciate any contribution that improves the bot in a positive way.  
However I still want to keep a certain consistency and "style" in the code.

Please read this document completely because not reading it may lead to your PR/issue being closed without any changes being made.

# Style
I follow a certain style when coding and appreciate if you follow it too.

## Javadoc comments
I use Javadoc comments on all public methods I use to describe what it does and perhaps returns.  
This isn't actually needed since my bot isn't an API nor offers one. However I still want public methods to have comments.

### Format
Please follow this format when making a new public method or making a previously private one public:  
```java
/**
 * Put a description here that explains what the method does.
 *
 * @param  param
 *         Description of the parameter.
 *
 * @return When the method returns stuff, tell what it returns.
 *
 * @see {@link #linkToMethod(String)} when the method is a shortcut of another.
 */
public String getSomething(String param){
    return linkToMethod(param);
}
```

### Linking
Please link any used methods/entities/... that a method uses through the `{@link path.to.source.here Source}` option.

**Example**:  
The method `getEmbed()` returns a EmbedBuilder from JDA (Path: `net.dv8tion.jda.core.EmbedBuilder`) so the Javadoc comment looks like this:  
```java
/**
 * Gets an {@link net.dv8tion.jda.core.EmbedBuilder EmbedBuilder} to use.
 *
 * @return An {@link net.dv8tion.jda.core.EmbedBuilder EmbedBuilder}
 */
public EmbedBuilder getEmbed(){
    return new EmbedBuilder();
}
 
```

## Access methods
The recode (v5.0.0) changed how the bot accesses certain classes and methods.  
Instead of making a static method, please try to make a normal one and create and get an instance of the class through the main class.

## Final words
Again I'm happy for any contribution that improves my code in good way, but follow the above examples.
