package com.artamonov.millionplanets.fight.presenter;

import com.artamonov.millionplanets.fight.FightActivityView;
import com.artamonov.millionplanets.model.User;
import com.google.firebase.firestore.DocumentSnapshot;

public interface FightActivityPresenter<V extends FightActivityView> {

    User getUserList();

    void calculateDamage();

    void calculateDamageFromEnemy();

    void calculateDamageToEnemy();

    void calculateLoot();

    Boolean fightFinished();

    void setLootTransferFinished(Boolean state);

    void setUserList(DocumentSnapshot doc);

    void setEnemyList(DocumentSnapshot doc);
}
