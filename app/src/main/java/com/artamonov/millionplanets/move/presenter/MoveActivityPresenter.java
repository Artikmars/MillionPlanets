package com.artamonov.millionplanets.move.presenter;

import com.artamonov.millionplanets.model.User;
import com.artamonov.millionplanets.move.MoveActivityView;
import com.google.firebase.firestore.DocumentSnapshot;

public interface MoveActivityPresenter<V extends MoveActivityView> {

    void getFuel();

    User getUserList();

    void setUserList(DocumentSnapshot doc);

    void ifEnoughFuelToJump();
}
