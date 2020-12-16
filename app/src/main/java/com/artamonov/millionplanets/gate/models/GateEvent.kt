package com.artamonov.millionplanets.gate.models

sealed class GateEvent {
    object SetObjectType : GateEvent()
    object UpdateIron : GateEvent()
}
