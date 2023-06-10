package com.grinder.game.content.cluescroll.task;

/**
 * A task agent that appears upon task completion and rewards another clue
 * scroll.
 * 
 * @author Pb600
 * @author Stan van der Bend
 */
public class ClueTaskAgent {

	private final boolean isCombative;
	private final boolean isValid;

	private final int combativeAgentNpcId;
	private final int agentNpcId;

	public ClueTaskAgent(int combativeAgentNpcId, int agentNpcId) {
		this.isCombative = combativeAgentNpcId > 0;
		this.isValid = agentNpcId > 0;
		this.combativeAgentNpcId = combativeAgentNpcId;
		this.agentNpcId = agentNpcId;
	}

	public boolean hasCombativeForm() {
		return isCombative;
	}
	boolean hasAgent() {
		return isValid;
	}

	int getCombativeAgentNpcId() {
		return combativeAgentNpcId;
	}
	int getAgentNpcId() {
		return agentNpcId;
	}

	@Override
	public String toString() {
		return "TaskAgent [aggressiveAgent=" + combativeAgentNpcId + ", agent=" + agentNpcId + ", aggressive=" + isCombative + ", isValid=" + isValid + "]";
	}

}
