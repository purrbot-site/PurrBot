/*
 * The below code originates from https://github.com/Almighty-Alpaca/JDA-Butler
 * and does not fall under the MIT license of this Project, but the original
 * License of its source.
 *
 * A copy of the original License header is added below for your convenience.
 *
 * ================================================================================
 *
 * Copyright 2016-2021 Aljoscha Grebe & Florian Spieß
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package site.purrbot.bot.util.pagination;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.components.ButtonInteraction;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ButtonListener implements EventListener{
    private final FixedSizeCache<String, Consumer<? super ButtonInteraction>> listeners = new FixedSizeCache<>(100);
    
    @Override
    public void onEvent(@NotNull GenericEvent event){
        if(event instanceof ButtonClickEvent)
            onButton((ButtonClickEvent)event);
    }
    
    private void onButton(ButtonClickEvent event){
        Consumer<? super ButtonInteraction> callback = listeners.find(prefix -> event.getComponentId().startsWith(prefix));
        if(callback == null){
            event.reply("This menu timed out").setEphemeral(true).queue();
            return;
        }
        
        event.deferEdit().queue();
        callback.accept(event);
    }
    
    public void addListener(String prefix, Consumer<? super ButtonInteraction> callback){
        listeners.add(prefix, callback);
    }
}
