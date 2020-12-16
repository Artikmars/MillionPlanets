package com.artamonov.millionplanets.gate.models

import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.User

data class GateViewState(
    val fetchStatus: FetchGateStatus,
    val user: User,
    val spaceObject: SpaceObject
)

sealed class FetchGateStatus {
    object OpenPlanetActivity : FetchGateStatus()
    object ShowCapacityError : FetchGateStatus()
    object DebrisIsRemoved : FetchGateStatus()
    object ShowNotEnoughMoneyToBuyFuelWarning : FetchGateStatus()
    object SetFightType : FetchGateStatus()
    object MineDebris : FetchGateStatus()
    object MineIron : FetchGateStatus()
    object DefaultState : FetchGateStatus()
}
