package com.grinder.game.entity.agent.npc.monster

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.name
import com.grinder.game.entity.agent.player.Player

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/10/2019
 * @version 1.0
 */
enum class MonsterRace(vararg val keywords: String) {

    UNDEFINED,

    ARZINIAN("arzinian"),
    AVIANSIE("aviansie", "kree'arra", "flight kilisa", "flockleader geerin", "wingman skree"),
    BEAR("bear"),
    FIERY("adamant dragon", "undead combat dummy", "black dragon", "blue dragon", "bronze dragon", "brutal ", "fire elemental", "galvek", "fire giant", "green dragon", "lava dragon", "mithril dragon", "pyrefiend", "red dragon", "rune dragon", "steel dragon", "vorkath"), // The special effect of pearl bolts (e) will have 33.3% increased damage.
    XERICIAN("deathly ranger", "abyssal portal", "deathly mage", "great olm", "glowing crystal", "guardian", "ice demon", "muttadile", "lizardman shaman", "scavenger beast",
        "tekton", "skeletal mystic", "vanguard", "vasa nistirio", "vespine soldier"), // The Twisted bow's damage modifier caps at 350 Magic within the Chambers of Xeric, as opposed to the default of 250.
    DAGANNOTH("dagannoth"),
    GOLEM("golem"),
    COMBAT_DUMMY("combat dummy", "undead combat dummy"),
    PEST_PORTAL("portal"),
    UNDEAD_COMBAT_DUMMY("undead combat dummy"),
    DEMON("demon", "bloodveld", "doomion", "cerberus", "flaming pyrelord", "icefiend", "ice demon", "pyrefiend", "waterfiend", "undead combat dummy", "nechryael", "imp", "chronozon", "nezikchened", "agrith-naar", "agrith-naar", "chronozon", "abyssal sire", "k'ril tsutsaroth", "skotizo", "balfrug kreeyath", "zakl'n gritch", "tstanon karlak", "nezikchened", "demonic gorilla"),
    DRAKE("drake"),
    DRAGON("dragon", "merodach", "alchemical hydra", "ancient wyvern", "drake", "galvek", "great olm", "colossal hydra", "long-tailed wyvern", "wyrm", "skeletal wyvern", "spitting wyvern", "taloned wyvern", "vorkath"),
    GIANT("giant", "obor", "cyclops"),
    GOBLIN("goblin"),
    WARRIORS_GUILD("cyclops", " armour"),
    HELL_HOUND("hellhound"),
    REVENANTS("revenant"),
    HUMAN("man", "guard", "warrior", "monk", "priest"),
    HYDRA("hydra"),
    KALPHITE("kalphite", "scarab mage", "scarab swarm", "scarabs"), // Keris will deal 33% bonus damage. There is also a 1/51 chance of dealing triple damage.
    KURASK("kurask"),
    OLM("great olm"),
    SCORPION("scorpion"),
    UNDEAD("ankou", "undead combat dummy", "crushing hand", "deviant spectre", "abhorrent spectre", "forgotten soul", "pestilent bloat", "repugnant spectre", "skeletal mystic", "slash bash", " soul", "tree spirit", "crawling hand", "banshee", "aberrant spectre", "ghast", "skogre", "nazastarool", "tree spirit", "ahrim", "dharok", "guthan", "karil", "torag", "verac", "pestilent bloat", "mi-gor", "ghost", "mummy", "shade", "skeleton", "bash", "skogre", "tortured soul", "tree spirit", "undead", "vet'ion", "vorkath", "zombie", "zombified", "revenant", "zogre"),
    SCABARITES("scarab", "locust rider"),
    SPIDER("spider"),
    SHADE("shade"),
    ELEMENTAL("elemental"),
    TROLL("troll"),
    TUROTH("turoth"),
    TZHAAR("tzhaar", "tz-"),
    WYRM("wyrm"),
    WYVERN("wyvern"),
    MONKEY("monkey", "gorilla");

    companion object {

        fun getRacesFor(agent: Agent) : List<MonsterRace> {
            if(agent is NPC) {
                val name = agent.name()
                return values().filter { isRace(name, it) }
            } else if(agent is Player) {
                return listOf(HUMAN)
            }
            return emptyList()
        }

        fun isRace(agent: Agent, race: MonsterRace): Boolean {
            if(agent is NPC) {
                return isRace(agent.name(), race)
            } else if(agent is Player)
                return race == HUMAN
            return false
        }

        fun isAnyRace(agent: Agent, vararg races: MonsterRace): Boolean {
            if(agent is NPC) {
                val name = agent.name()
                return races.any { isRace(name, it) }
            } else if(agent is Player)
                return races.contains(HUMAN)
            return false
        }

        fun isRace(name: String, race: MonsterRace): Boolean {
            return race.keywords.any { name.contains(it, true) }
        }
    }
}