package com.artamonov.millionplanets.fight;

public interface FightActivityView {

    void setSnackbarError(int errorMessage);

    void buyFuel(int fuel, int money);

    void setProgressBar(boolean state);
}
