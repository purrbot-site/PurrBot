/*
 * Copyright 2019 Andre601
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

package site.purrbot.bot.constants;

public enum Roles {

    // Roles for upvoting (discordbots.org and botlist.space) or for adding it to their favourites (lbots.org)
    VOTER("475335831649910785"),

    // Level roles.
    VERY_ADDICTED  ("449280939839979530"),
    SUPER_ADDICTED ("424194307881435177"),
    ULTRA_ADDICTED ("541671949831766037"),
    HYPER_ADDICTED ("549988541539680266"),
    MASTER_ADDICTED("549988641464909827");

    private String id;

    Roles(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }
}
