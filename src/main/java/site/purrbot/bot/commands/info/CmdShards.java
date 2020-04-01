/*
 * Copyright 2018 - 2020 Andre601
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang.StringUtils;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CommandDescription(
        name = "Shards",
        description = "Display",
        triggers = {"shard", "shards", "shardinfo"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "{p}shards"),
                @CommandAttribute(key = "help", value = "{p}shards")
        }
)
public class CmdShards implements Command{
    private PurrBot bot;
    
    public CmdShards(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void execute(Message msg, String s){
        List<String> headers = new ArrayList<>();
        
        headers.add("ID");
        headers.add("Status");
        headers.add("Ping");
        headers.add("Guilds");
        
        List<List<String>> table = new ArrayList<>();
        int id = msg.getJDA().getShardInfo().getShardId();
        final List<JDA> shards = new ArrayList<>(bot.getShardManager().getShards());
        Collections.reverse(shards);
        
        for(final JDA jda : shards){
            final List<String> row = new ArrayList<>();
            final int shardId = jda.getShardInfo().getShardId();
            
            row.add((id == shardId ? "(You) " : "") + shardId);
            row.add(getStatus(jda));
            row.add(String.valueOf(jda.getGatewayPing()));
            row.add(String.valueOf(jda.getGuildCache().size()));
            
            table.add(row);
            
            if(table.size() == 20){
                msg.getTextChannel().sendMessage(createTable(headers, table)).queue();
                table = new ArrayList<>();
            }
        }
        
        if(!table.isEmpty())
            msg.getTextChannel().sendMessage(createTable(headers, table)).queue();
    }
    
    private String createTable(List<String> headers, List<List<String>> table){
        final StringBuilder builder = new StringBuilder();
        final int[] widths = new int[headers.size()];
        
        for(int i = 0; i < headers.size(); i++){
            if(headers.get(i).length() > widths[i])
                widths[i] = headers.get(i).length();
        }
        
        for(final List<String> row : table){
            for(int i = 0; i < row.size(); i++){
                final String cell = row.get(i);
                if(cell.length() > widths[i])
                    widths[i] = cell.length();
            }
        }
        
        builder.append("```").append("prolog").append("\n");
        final StringBuilder formatLineL = new StringBuilder("│");
        final StringBuilder formatLineR = new StringBuilder("│");
        for(final int width : widths){
            formatLineL.append(" %-").append(width).append("s │");
            formatLineR.append(" %").append(width).append("s │");
        }
        formatLineL.append("\n");
        formatLineR.append("\n");
        builder.append(getSeparators("┌", "┬", "┐", "─", widths));
        builder.append(String.format(formatLineL.toString(), headers.toArray()));
        
        builder.append(getSeparators("╞", "╪", "╡", "═", widths));
        for(final List<String> row : table)
            builder.append(String.format(formatLineR.toString(), row.toArray()));
    
        builder.append(getSeparators("└", "┴", "┘", "─", widths));
        builder.append("```");
        
        return builder.toString();
    }
    
    private String getSeparators(String left, String middle, String right, String line, int... widths){
        boolean first = true;
        final StringBuilder builder = new StringBuilder();
        for(final int size : widths){
            if(first){
                first = false;
                builder.append(left).append(StringUtils.repeat(line, size + 2));
            }else{
                builder.append(middle).append(StringUtils.repeat(line, size + 2));
            }
        }
        
        return builder.append(right).append("\n").toString();
    }
    
    private String getStatus(JDA jda){
        return firstUppercase(jda.getStatus().toString().replace("_", " "));
    }
    
    private String firstUppercase(String word){
        return Character.toString(word.charAt(0)).toUpperCase() + word.substring(1).toLowerCase();
    }
}
