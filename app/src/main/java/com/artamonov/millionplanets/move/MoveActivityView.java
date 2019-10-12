package com.artamonov.millionplanets.move;

public interface MoveActivityView {

    void setSnackbarError(int errorMessage);

    void buyFuel(Long fuel, Long money);

    void setProgressBar(boolean state);
}
