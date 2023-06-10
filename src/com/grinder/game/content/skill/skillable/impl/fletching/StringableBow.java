package com.grinder.game.content.skill.skillable.impl.fletching;

import com.grinder.game.model.Animation;

import java.util.HashMap;
import java.util.Map;

import static com.grinder.util.ItemID.*;

/**
 * Represents all bows which can be strung.
 *
 * @author Professor Oak
 */
public enum StringableBow {

    // Regular bows
    SB(SHORTBOW_U_, BOW_STRING, SHORTBOW, 5, 10, new Animation(6678)),
    SL(LONGBOW_U_, BOW_STRING, LONGBOW, 10, 20, new Animation(6684)),

    OSB(OAK_SHORTBOW_U_, BOW_STRING, OAK_SHORTBOW, 20, 33, new Animation(6679)),
    OSL(OAK_LONGBOW_U_, BOW_STRING, OAK_LONGBOW, 25, 50, new Animation(6685)),

    WSB(WILLOW_SHORTBOW_U_, BOW_STRING, WILLOW_SHORTBOW, 35, 66, new Animation(6680)),
    WLB(WILLOW_LONGBOW_U_, BOW_STRING, WILLOW_LONGBOW, 40, 83, new Animation(6686)),

    MASB(MAPLE_SHORTBOW_U_, BOW_STRING, MAPLE_SHORTBOW, 50, 100, new Animation(6681)),
    MASL(MAPLE_LONGBOW_U_, BOW_STRING, MAPLE_LONGBOW, 55, 116, new Animation(6687)),

    YSB(YEW_SHORTBOW_U_, BOW_STRING, YEW_SHORTBOW, 65, 135, new Animation(6682)),
    YLB(YEW_LONGBOW_U_, BOW_STRING, YEW_LONGBOW, 70, 150, new Animation(6688)),

    MSB(MAGIC_SHORTBOW_U_, BOW_STRING, MAGIC_SHORTBOW, 80, 166, new Animation(6683)),
    MSL(MAGIC_LONGBOW_U_, BOW_STRING, MAGIC_LONGBOW, 85, 183, new Animation(6689)),

    // Crossbows
    BCBOW(BRONZE_CROSSBOW_U_, CROSSBOW_STRING, BRONZE_CROSSBOW, 9, 12, new Animation(6671)),
    ICBOW(IRON_CROSSBOW_U_, CROSSBOW_STRING, IRON_CROSSBOW, 39, 44, new Animation(6673)),
    SCBOW(STEEL_CROSSBOW_U_, CROSSBOW_STRING, STEEL_CROSSBOW, 46, 54, new Animation(6674)),
    MCBOW(MITHRIL_CROSSBOW_U_, CROSSBOW_STRING, MITH_CROSSBOW, 54, 64, new Animation(6675)),
    ACBOW(ADAMANT_CROSSBOW_U_, CROSSBOW_STRING, ADAMANT_CROSSBOW, 61, 82, new Animation(6676)),
    RCBOW(RUNITE_CROSSBOW_U_, CROSSBOW_STRING, RUNE_CROSSBOW, 69, 100, new Animation(6677)),
    DCBOW(21921, CROSSBOW_STRING, 21902, 78, 70, new Animation(7961));

    static Map<Integer, StringableBow> unstrungBows = new HashMap<Integer, StringableBow>();

    static {
        for (StringableBow l : StringableBow.values()) {
            unstrungBows.put(l.getItemId(), l);
        }
    }

    private final int itemId, bowStringId, result, levelReq, exp;
    private final Animation animation;

    StringableBow(int itemId, int bowStringId, int result, int levelReq, int exp, Animation animation) {
        this.itemId = itemId;
        this.bowStringId = bowStringId;
        this.result = result;
        this.levelReq = levelReq;
        this.exp = exp;
        this.animation = animation;
    }

    public int getItemId() {
        return itemId;
    }

    public int getBowStringId() {
        return bowStringId;
    }

    public int getResult() {
        return result;
    }

    public int getLevelReq() {
        return levelReq;
    }

    public int getExp() {
        return exp;
    }

    public Animation getAnimation() {
        return animation;
    }
}
