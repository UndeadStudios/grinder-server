package com.grinder.game.model.passages.link;

import com.google.gson.annotations.Expose;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Position;
import com.grinder.game.model.passages.Passage;
import com.grinder.game.model.passages.PassageRequirement;
import com.grinder.game.model.passages.PassageState;

public final class PassageLink {

    @Expose
    private final PassageLinkData closedData;

    @Expose
    private final PassageLinkData openedData;

    private Passage asPassage;

    public PassageLink(GameObject closed, GameObject opened) {
        closedData = closed != null ? new PassageLinkData(closed.getId(), closed.getFace(), closed.getPosition().clone()) : null;
        openedData = opened != null ? new PassageLinkData(opened.getId(), opened.getFace(), opened.getPosition().clone()) : null;
    }

    public Passage transformPassage(Passage oldPassage) {
        if (asPassage != null) {
            return asPassage;
        }
        var closed = createObject(oldPassage, PassageState.CLOSED);
        var opened = createObject(oldPassage, PassageState.OPENED);
        Passage newPassage = new Passage(oldPassage.getCategory(), closed, opened, oldPassage.getSoundId(PassageState.CLOSED), oldPassage.getSoundId(PassageState.OPENED), oldPassage.getAnimation()
                , oldPassage.getMode(), oldPassage.getType(), oldPassage.getCurrentState());
        return asPassage =  transferState(oldPassage, newPassage);
    }

    private Passage transferState(Passage oldPassage, Passage newPassage) {
        newPassage.setLocked(oldPassage.isLocked());
        newPassage.setBroken(oldPassage.isBroken());
        newPassage.setRevertTime(oldPassage.getRevertTime());
        newPassage.setCost(oldPassage.getCost());

        newPassage.onEnter(oldPassage.getOnEnter());
        newPassage.onLeave(oldPassage.getOnLeave());
        newPassage.onOpen(oldPassage.getOnOpen());
        newPassage.onClose(oldPassage.getOnClose());
        newPassage.onClick(oldPassage.getOnClick());
        newPassage.onPaymentFail(oldPassage.getOnPaymentFail());
        newPassage.onPaymentSuccess(oldPassage.getOnPaymentSuccess());

        newPassage.addRequirement(oldPassage.getRequirements().toArray(PassageRequirement[]::new));
        return newPassage;
    }

    public GameObject createObject(Passage mainPassage, PassageState state) {
        var shape = mainPassage.getData().get(state).getShape();
        return DynamicGameObject.createPublic(getId(state), getPosition(state), shape, getFace(state));
    }

    public int getId(PassageState state) {
        var data = getData(state);
        if (data == null) {
            return -1;
        }
        return data.getId();
    }

    public Position getPosition(PassageState state) {
        var data = getData(state);
        if (data == null) {
            return null;
        }
        return data.getPosition();
    }

    public int getFace(PassageState state) {
        var data = getData(state);
        if (data == null) {
            return 0;
        }
        return data.getFace();
    }

    public PassageLinkData getData(PassageState state) {
        return state == PassageState.CLOSED ? closedData : openedData;
    }
}