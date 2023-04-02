/*
 *  Copyright 2018 - 2022 Andre601
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

package site.purrbot.bot.manager.string;

import java.util.Map;

public class StringReplacer{
    
    public static String replace(String input, String target, Object replacement){
        if(input == null || input.isEmpty() || target == null || target.isEmpty() || replacement == null)
            return input;
        
        StringBuilder output = new StringBuilder(input);
        int index = 0;
        while((index = output.indexOf(target, index)) != -1){
            output.replace(index, index + target.length(), String.valueOf(replacement));
            index += String.valueOf(output).length();
        }
        
        return output.toString();
    }
    
    public static String replace(String input, Map<String, Object> replacements){
        StringBuilder output = new StringBuilder(input);
        int index;
        for(Map.Entry<String, Object> entry : replacements.entrySet()){
            index = 0;
            while((index = output.indexOf(entry.getKey(), index)) != -1){
                output.replace(index, index + entry.getKey().length(), String.valueOf(entry.getValue()));
                index += String.valueOf(entry.getValue()).length();
            }
        }
        
        return output.toString();
    }
}
