package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.util.debug.DebugType;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-27
 */
public class DebugCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Toggles combat debugging.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        int[] ids = new int[]{13, 15, 389, 391, 394, 399, 619, 620, 621, 812, 841, 842, 843, 874, 1193, 1265, 1268, 1696, 1697, 1698, 1699, 1700, 1701, 1719, 1720, 1994, 1996, 2333, 2334, 2553, 2554, 2555, 2594, 2595, 2693, 2755, 2757, 2758, 2759, 2760, 2763, 2811, 2817, 2819, 2820, 2829, 2831, 2833, 2835, 2836, 2837, 2838, 2839, 2840, 2879, 2880, 2881, 2890, 3071, 3114, 3115, 3170, 3171, 3181, 3182, 3185, 3186, 3187, 3191, 3265, 3266, 3356, 3358, 3359, 3360, 3364, 3365, 3366, 3369, 3370, 3371, 3372, 3397, 3419, 3421, 3422, 3423, 3450, 3451, 3452, 3547, 3555, 3630, 3639, 3640, 3641, 3652, 3705, 3804, 3806, 3807, 3838, 3867, 3872, 3874, 3926, 3945, 3972, 4018, 4019, 4029, 4030, 4071, 4080, 4169, 4173, 4180, 4181, 4182, 4183, 4188, 4191, 4255, 4341, 4365, 4366, 4367, 4381, 4382, 4551, 4719, 4721, 4727, 4731, 4779, 4795, 4804, 4841, 4857, 4885, 4943, 4949, 4953, 4955, 4965, 4971, 5256, 5257, 5258, 5259, 5349, 5355, 5379, 5417, 5524, 5525, 5611, 5620, 5714, 5716, 5746, 5864, 5907, 6068, 6083, 6096, 6103, 6111, 6394, 6396, 6397, 6398, 6399, 6400, 6401, 6402, 6403, 6404, 6405, 6406, 6408, 6409, 6424, 6425, 6490, 6529, 6530, 6554, 6601, 6603, 6604, 6605, 6606, 6607, 6608, 6609, 6610, 6611, 6655, 6703, 6704, 6705, 6706, 6707, 6708, 6709, 6710, 6711, 6723, 6724, 6848, 6849, 6999, 7083, 7122, 7190, 7197, 7198, 7741, 7967, 8056, 8172};



        //anim 1697 <- slid throghg crack
        //3356
        // 3364
        // anim 3419, 3421, 3422
        // anim 3652 staff
        //anim 3867
        // 3872, 3838?
        //4019, 4029, 4030
        // 4173
        // 4365, 4366, 4367
        // 4841
        // 4943, 4949, 4953, 4965, 4961, /
        // 5255-5259 hunter
        // 5714, 5761,
        // 5746, spade
        // 6394-6409
        // 7122, 6603, 6604, 6605, 6606, 6607, 6608, 6609, 6610, 6611,
        // 6655 elvarg
        // 6705-6711 <- barehand
        // 6711 big jump
        //
//        int delay = 2;
//        for(int id: ids){
//            TaskManager.submit(delay, () -> {
//                System.out.println(""+id);
//                player.say(""+id);
//                player.performAnimation(new Animation(id));
//            });
//            delay += 5;
//        }

        new DialogueBuilder(DialogueType.OPTION)
                .firstOption("Combat", futurePlayer -> futurePlayer.toggleDebugging(DebugType.COMBAT))
                .secondOption("Toggle off", futurePlayer -> futurePlayer.toggleDebugging(DebugType.NONE))
                .start(player);

    }

    @Override
    public boolean canUse(Player player) {
        return player.getRights().isStaff();
    }
}
