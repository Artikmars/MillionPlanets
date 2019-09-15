package com.artamonov.millionplanets.move;

public interface MoveActivityView {

    void setSnackbarError(int errorMessage);

    void buyFuel(int fuel, int money);

    void setProgressBar(boolean state);
}
