/*
 *  Copyright 2018 - 2021 Andre601
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 *  and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all copies or substantial
 *  portions of the Software.
 *  
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 *  OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
        return Button.danger(getId() + ":cancel", Emoji.fromUnicode("\uD83D\uDEAE"));
    }
    private Button getNextButton(){
        return Button.secondary(getId() + ":next", Emoji.fromUnicode("\u25B6"));
    }
}
