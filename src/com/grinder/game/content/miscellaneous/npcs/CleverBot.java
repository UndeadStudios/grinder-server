package com.grinder.game.content.miscellaneous.npcs;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;

import com.grinder.game.World;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.Server;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.Position;
import com.grinder.util.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CleverBot extends NPC {

    private final String API_KEY = "CC97squ5DtztKunPotNMqhYWNhA";

    public ChatterBotSession botBrain;
    private final static ScriptEngineManager mgr = new ScriptEngineManager();
    private final static ScriptEngine engine = mgr.getEngineByName("JavaScript");
    public static List<CleverBot> aiBotList = new ArrayList<>();
    private boolean isListening;
    private boolean isThinking;
    private long lastExcuse;
    private final String npcName;

    private long lastAnswerTime = 0;
    private long lastRandomSentenceTime = 0;

    private final String[] randomSentences = {
            "I am looking for a chat...",
            "It's quite boring here.",
            "Anyone interested for a chit chat with me?",
            "I can understand humans.",
            "Do you guys have Tinder?",
            "Keep it going!",
            //"Those gambles are going well so far.",
            "What's up babies.",
            "Chat with me!",
    };

    public CleverBot(int npcID, Position position) {
        super(npcID, position);
        this.npcName = "Cleverbot";
        this.setWalkableArea(15);
        setSession();
    }

    /**
     * Send message the player that spoke with eir.
     *
     * @param player:  Client instance of the player.
     * @param message: Message that player said.
     */
    private void sendMessage(Player player, String message) {
        player.sendMessage(npcName + ":<col=" + 0x007d99 + "><shad=0> " + message);
    }

    /**
     * Answer the player that is talking close to eir.
     *
     * @param speech: What player said;
     * @param player: Client that said {@code speech};
     */
    public void answer(final String speech, final Player player) {

        if (this.isThinking) {
            return;
        }

        if (player.getRights() == PlayerRights.DEVELOPER) {
            if (speech.charAt(0) == '=' && speech.charAt(speech.length() - 1) == '?') {
                String answer = solveAnswer(speech);
                if (answer != null) {
                    say(answer);
                    lastAnswerTime = System.currentTimeMillis();
                    return;
                }
            }

            if (speech.equalsIgnoreCase("-rs")) {
                player.sendMessage("Started a new Bot Session");
                setSession();
                return;
            }
        }

        new Thread() {
            @Override
            public void run() {
                setPriority(Thread.MIN_PRIORITY);
                try {
                    String answer = botBrain.think(speech);
                    if (answer.toLowerCase().contains("cleverbot")) {
                        answer = answer.replaceAll("Cleverbot", npcName);
                    }
                    if (answer.toLowerCase().contains("robot")) {
                        answer = answer.replaceAll("robot", npcName);
                    }
                    setPositionToFace(player.getPosition());
                    say(answer);
                    lastAnswerTime = System.currentTimeMillis();
                } catch (Exception e) {
                    if (System.currentTimeMillis() - lastExcuse > 200) {
                        lastExcuse = System.currentTimeMillis();
                        String excuse = getExcuse();
                        if (excuse != null) {
                            setPositionToFace(player.getPosition());
                            say(excuse);
                            lastAnswerTime = System.currentTimeMillis();
                        }
                    }
                    return;
                }

                CleverBot.this.isThinking = false;
            }
        }.start();
    }

    /**
     * Solve a math expression and give the result.
     *
     * @param expression: Math expression to be solved.
     * @return: Solved expression in the String Format.
     */
    public static String solveAnswer(String expression) {
        String mathExpression = null;
        mathExpression = expression.substring(1, expression.indexOf("?"));
        if (mathExpression.contains("math"))
            mathExpression = mathExpression.replaceAll("math", "Math");
        if (mathExpression.toLowerCase().contains("pi")) {
            mathExpression = mathExpression.replaceAll("pi", String.valueOf(Math.PI));
        }
        if (mathExpression.toLowerCase().contains("e")) {
            mathExpression = mathExpression.replaceAll("e", String.valueOf(Math.E));
        }
        if (mathExpression != null) {
            try {
                String answer = String.valueOf(engine.eval(mathExpression));
                if (!answer.contains("null"))
                    return String.valueOf(answer);
            } catch (ScriptException e) {
                return "l2math";
            }
        }
        return null;
    }

    /**
     * Get a random excuse to do not answer player when service is not working.
     *
     * @return: Random excuse.
     */
    private static String getExcuse() {
        int random = Misc.random(28);
        switch (random) {
            case 0:
                return "Bah! I don't feel like talking...";
            case 1:
                return "Ugh not today please...";
            case 2:
                return "I'm feeling depressed, I want to be alone please...";
            case 3:
                return "Aww come on! Leave me alone... Please!";
            case 4:
                return "I'm not on the mood dude...";
            case 5:
                return "I'm feeling sick, I need to rest...";
            case 6:
                return "I'm not listening! Lalalala...";
            case 7:
                return "Come on... Respect my privacy.";
            case 8:
                return "Ugh... Wait... My mom's calling me! Brb!";
            case 9:
                return "Today I'm hearing soo low!";
            case 10:
                return "Hey! I'm busy studing here!";
            case 11:
                return "Oh god! What are you doing here?!? You didn't see it!";
            case 12:
                return "Leave please! My boyfriend is comming!";
            case 13:
                return "Can't talk now, I'm about to leave!";
            case 14:
                return "Hey, silence please!";
            case 15:
                return "Did you ever feel like someone is behind you?";
            case 16:
                return "Hey, no offence but... I'm with a headache...";
            case 18:
                return "Busy betting with my pet!";
            case 19:
                return "I learnt a spell... That makes people stay quiet!";
            case 20:
                return "I'm resting, this place is so nice...";
            case 21:
                return "People gambling and talking around all the place!";
            case 22:
                return "There is a more quiet place?";
            case 23:
                return "Know what?!? I'm Van Gogh, no ears baby!";//ROFL
            case 24:
                return "Shhh! I heard Santa comming!";
            case 25:
                return "Ohh is that late? I'm leaving, bye!";
            case 26:
                return "We could stay quiet and look at the stars.";
            case 27:
                return "This is not a madhouse! Ok... This is... ";
            case 28:
                return "Wow! Wait that's not what it looks like!";

        }
        return null;
    }

    public void setSession() {
        try {
            botBrain = Server.getFactory().create(ChatterBotType.CLEVERBOT, API_KEY).createSession();
            setListening(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isListening() {
        return isListening;
    }

    public CleverBot setListening(boolean isListening) {
        this.isListening = isListening;
        return this;
    }

    public CleverBot create(int npcId, Position position) {
        return new CleverBot(npcId, position);
    }

    public void speechSequence() {

        // Check if 60 seconds have passed since last answer time.
        if ((System.currentTimeMillis() - lastAnswerTime) > (60 * 10000)) {
            // Check if 60 seconds have passed since last random sentence time.
            if ((System.currentTimeMillis() - lastRandomSentenceTime) > (10 * 10000)) {
                String randomSentence = randomSentences[new Random().nextInt(randomSentences.length)];

                this.say(randomSentence);
                lastRandomSentenceTime = System.currentTimeMillis();
            }
        }
    }

    public static CleverBot generateBot(int npcId, Position position) {
        CleverBot cleverBot = new CleverBot(npcId, position);
        World.getNpcAddQueue().add(cleverBot);
        aiBotList.add(cleverBot);

        return cleverBot;
    }
}