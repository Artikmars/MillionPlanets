package com.artamonov.millionplanets.move

interface MoveActivityView {
    fun setSnackbarError(errorMessage: Int)
    fun buyFuel(fuel: Long, money: Long)
    fun setProgressBar(state: Boolean)
}