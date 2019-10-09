package com.artamonov.millionplanets.fight;

import com.artamonov.millionplanets.model.User;

public interface FightActivityView {

    void setSnackbarError(int errorMessage);

    void setProgressBar(boolean state);

    void setUserData(User userList);

    void setEnemyData(User userList);

    void showYouWonMessage();

    void showEnemyWonMessage();
}
