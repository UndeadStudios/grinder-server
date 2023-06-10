package com.grinder.game.content.miscellaneous.donating

/**
 * TODO: add documentation
 * TODO: improve this design
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   23/04/2020
 * @version 1.0
 */
class PurchaseRewards {
        var premiumPoints = 0
        var extraPremiumPoints = 0
        var giveDiceRank = false
        var mysteryBoxes = 0
        var participationPoints = 0
        var votingTickets = 0

        override fun toString(): String {
                return "PurchaseRewards(premiumPoints=$premiumPoints, extraPremiumPoints=$extraPremiumPoints, giveDiceRank=$giveDiceRank, mysteryBoxes=$mysteryBoxes, participationPoints=$participationPoints, votingTickets=$votingTickets)"
        }
}