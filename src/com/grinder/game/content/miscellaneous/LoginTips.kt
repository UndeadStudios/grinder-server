package com.grinder.game.content.miscellaneous

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.util.Misc

object LoginTips {

    /**
     * List of rare items that will sent in item dialogue interface
     * to remind players to vote, it will always pick a random item.
     * Includes best in game weapons, armours, and many others!
     */
    private val RARE_ITEMS_POOL = intArrayOf(6585, 11128, 6524, 6528, 6568, 2417, 2415, 8842, 7462, 7462, 7462, 11838, 21009, 12954, 10551, 20223, 20226,
            11826, 11828, 11832, 11836, 20716, 11907, 12856, 12877, 12883, 12881, 4151, 4587, 5698, 4585, 4087, 3140, 4675, 9185, 7158, 4153, 11235, 11808, 2581,
            3204, 11284, 11770, 11772, 6737, 6733, 7398, 7399, 7400, 6916, 6918, 6920, 6922, 6924, 10446, 10448, 10450, 12197, 12273, 6889, 4224, 11759,  // Muddy items end
            // Rare items start
            15156, 11804, 12821, 15152, 3488, 13235, 20716, 10350, 11665, 6737, 1057, 19481, 20997, 6465, 21003, 1048, 11832, 13652, 15245, 15227, 15228, 15239, 13074,
            22978, 10828, 11284, 6916, 22284, 4212, 10551, 21902, 13271, 21021, 22296, 22542, 11791, 22109, 12424, 5698, 21298, 12315, 12311, 19687, 23101, 23073, 24160, 24271, 24419, 24440, 24444,
            15156, 11804, 12821, 15152, 3488, 13235, 20716, 10350, 11665, 6737, 1057, 19481, 20997, 6465, 21003, 1048, 11832, 13652, 15245, 15227, 15228, 15239, 13074,
            22978, 10828, 11284, 6916, 22284, 4212, 10551, 21902, 13271, 21021, 22296, 22542, 11791, 22109, 12424, 5698, 21298, 12315, 12311, 19687, 23101, 23073, 24160, 24271, 24419, 24440, 24444,
            15156, 11804, 12821, 15152, 3488, 13235, 2071, 10350, 11665, 6737, 1057, 19481, 20997, 6465, 21003, 1048, 11832, 13652, 15245, 15227, 15228, 15239, 13074,
            22978, 10828, 11284, 6916, 22284, 4212, 10551, 21902, 13271, 21021, 22296, 22542, 11791, 22109, 12424, 5698, 21298, 12315, 12311, 19687, 23101, 23073, 24160, 24271, 24419, 24440, 24444,
            15156, 11804, 12821, 15152, 3488, 13235, 20716, 10350, 11665, 6737, 1057, 19481, 20997, 6465, 21003, 1048, 11832, 13652, 15245, 15227, 15228, 15239, 13074,
            22978, 10828, 11284, 6916, 22284, 4212, 10551, 21902, 13271, 21021, 22296, 22542, 11791, 22109, 12424, 5698, 21298, 12315, 12311, 19687, 23101, 23073, 24160, 24271, 24419, 24440, 24444,  // Rare items end
            10362, 1506, 7122, 7124, 7409, 7126, 7128, 7130, 7132, 7134, 10390, 10364, 10366, 10368, 10370, 10372, 10374, 10376, 10378, 10380, 10382, 10384, 10386, 10388, 6188, 12629, 13097, 6862, 6863, 6885, 7114,
            7116, 7928, 7929, 7930, 7931, 13099, 13095, 13103, 13105, 6853, 6856, 6857, 6858, 6859, 6861, 7136, 7138, 3057, 3058, 3059, 3060, 3061, 6180, 6181, 6182, 6184, 6185, 6186, 6187, 6188, 6654,
            6655, 6656, 7534, 7535, 2633, 12447, 12449, 12445, 2635, 2637, 2639, 2641, 7394, 7390, 7386, 9634, 9636, 9638, 9640, 9642, 9644, 10631, 7592, 7593, 7594, 7595, 7596, 10150, 7396, 7392, 7388, 2460, 2462, 2462, 2464, 2466, 2468, 2470,
            2472, 2474, 2476, 2631, 12451, 12453, 12455, 2645, 2647, 2649, 6856, 2952, 9946, 9944, 9945, 9921, 9922, 9923, 9924, 9925, 10069, 10063, 10061, 9241, 6773, 7141, 7142,
            7388, 2460, 2462, 2462, 2464, 2466, 2468, 2470, 2472, 2474, 2476,
            1025, 2633, 2635, 2637,
            7136, 7138, 3057, 3058, 3059, 3060, 3061, 2952, 6180, 6181, 6182, 6184, 6185, 6186, 6187, 6188, 6654, 6655, 6656, 7534, 7535, 1025, 2633, 2635, 2637, 2639, 2641, 7394, 7390, 7386, 7396, 7392, 7388)

    @JvmStatic
    fun sendLoginTip(player: Player?) {
/*        if (player != null) {
            DialogueBuilder(DialogueType.NPC_STATEMENT)
                .setNpcChatHead(277)
                .setText("Merry Christmas to you " + player.username +"!", "Have fun and happy holidays.")
                .start(player!!)
        }*/
/*        when (Misc.random(3)) {
            0 -> DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(5442)
                    .setText("@red@Make sure that your account is secure!", "In order to make your account secure you can set a", "bank pin, and make sure to use a unique", "password you don't use anywhere else.")
                    .start(player!!)
            1 -> DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(3308)
                    .setText("@red@Are you starter or want to learn more about GrinderScape?", "Take a look at our wiki! You can access through quest tab!")
                    .start(player!!)
            2 -> DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(5981)
                    .setText("@red@Play minigames during events and get great rewards!", "You can come take a look at my store.", "I'll be promoting minigames events offering extra rewards.", "You can meet me in Edgeville for more...")
                    .start(player!!)
            else -> DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                    .setItem(RARE_ITEMS_POOL[Misc.getRandomInclusive(RARE_ITEMS_POOL.size - 1)], 200)
                    .setText("By voting everyday you get voting points!", "You can spend your vote points for valueable items", "and awesome outfits with vote exchanger at Edgeville!")
                    .start(player!!)
        }*/
    }
}