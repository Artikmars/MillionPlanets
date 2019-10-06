package com.artamonov.millionplanets.fight.presenter;

import com.artamonov.millionplanets.fight.FightActivityView;
import com.artamonov.millionplanets.model.User;
import com.google.firebase.firestore.DocumentSnapshot;

public interface FightActivityPresenter<V extends FightActivityView> {

    void getFuel();

    User getUserList();

    void setUserList(DocumentSnapshot doc);

    void ifEnoughFuelToJump();
}
