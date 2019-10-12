package com.artamonov.millionplanets.fight;

import com.artamonov.millionplanets.model.User;

public interface FightActivityView {

    void calculateLoot(String nicknameGet, String nicknameLose, String ship);

    void setFightLog(int hpDamage, int shieldDamage, int enemyHpDamage, int enemyShieldDamage);

    void setProgressBar(boolean state);

    void setUserData(User userList);

    void setEnemyData(User userList);

    void showLootSnackbar(Boolean isYouWon, String ship);

    void showYouWonMessage();

    void showEnemyWonMessage();

    void startTimer();
}
