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

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonInteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Paginator{
    private final List<Message> pages = new ArrayList<>();
    private final long hookId, userId;
    private int index = 0;
    
    public Paginator(InteractionHook hook){
        this.hookId = hook.getInteraction().getIdLong();
        this.userId = hook.getInteraction().getUser().getIdLong();
    }
    
    public String getId(){
        return hookId + ":" + userId;
    }
    
    public Message getCurrent(){
        return pages.get(index);
    }
    
    public Paginator addPage(Message page){
        pages.add(page);
        return this;
    }
    
    public Paginator addPages(List<Message> pages){
        this.pages.addAll(pages);
        return this;
    }
    
    public void onButtonClick(ButtonInteraction interaction){
        if(interaction.getUser().getIdLong() != userId)
            return;
        
        String[] id = interaction.getComponentId().split(":");
        if(Long.parseUnsignedLong(id[0]) != hookId)
            return;
        
        String opreation = id[2];
        switch(opreation.toLowerCase(Locale.ROOT)){
            case "prev":
                interaction.getHook()
                    .editOriginal(getPrev())
                    .setActionRows(getButtons())
                    .queue();
                break;
            
            case "cancel":
                interaction.getHook()
                    .editOriginal(getCurrent())
                    .setActionRows()
                    .queue();
                break;
            
            case "next":
                interaction.getHook()
                    .editOriginal(getNext())
                    .setActionRows(getButtons())
                    .queue();
        }
    }
    
    public ActionRow getButtons(){
        return ActionRow.of(
            getPrevButton().withDisabled(isStart()),
            getCancelButton(),
            getNextButton().withDisabled(isEnd())
        );
    }
    
    private boolean isEnd(){
        return index == pages.size() - 1;
    }
    
    private boolean isStart(){
        return index == 0;
    }
    
    private Message getNext(){
        return isEnd() ? pages.get(index) : pages.get(++index);
    }
    
    private Message getPrev(){
        return isStart() ? pages.get(0) : pages.get(--index);
    }
    
    private Button getPrevButton(){
        return Button.secondary(getId() + ":prev", Emoji.fromUnicode("\u25C0"));
    }
    
    private Button getCancelButton(){
        return Button.danger(getId() + ":cancel", Emoji.fromUnicode("\u23F9"));
    }
    
    private Button getNextButton(){
        return Button.secondary(getId() + ":next", Emoji.fromUnicode("\u25B6"));
    }
}
