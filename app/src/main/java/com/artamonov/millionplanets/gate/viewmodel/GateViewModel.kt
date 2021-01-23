package com.artamonov.millionplanets.gate.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import com.artamonov.millionplanets.base.BaseVM
import com.artamonov.millionplanets.gate.models.FetchGateStatus
import com.artamonov.millionplanets.gate.models.GateAction
import com.artamonov.millionplanets.gate.models.GateEvent
import com.artamonov.millionplanets.gate.models.GateViewState
import com.artamonov.millionplanets.model.Item
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.SpaceObjectType
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.RandomUtils
import com.artamonov.millionplanets.utils.getShipFuelInfo
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Random

class GateViewModel @ViewModelInject constructor(
    private var firebaseFirestore: FirebaseFirestore,
    private var userDocument: DocumentReference
)
    : BaseVM<GateViewState, GateAction, GateEvent>() {

    private var userList: User = User()
    private var objectModel: SpaceObject = SpaceObject()
    private var planetDocument: DocumentReference? = null

    init { init() }

    private fun init() {
        userDocument.get().addOnSuccessListener {
            doc ->
            userList = doc.toObject(User::class.java)!!
            viewState = GateViewState(fetchStatus = FetchGateStatus.DefaultState, user = userList,
                    spaceObject = objectModel)
            planetDocument = firebaseFirestore.collection("Objects")
                    .document(userList.locationName!!)
            planetDocument!!.get().addOnSuccessListener {
                doc2 ->
                objectModel = doc2.toObject(SpaceObject::class.java)!!
                viewState = GateViewState(fetchStatus = FetchGateStatus.DefaultState, user = userList,
                        spaceObject = objectModel)
            }
        }
    }

    private fun updateUserData() {
        userDocument.get().addOnSuccessListener {
            doc ->
            userList = doc.toObject(User::class.java)!!
            viewState = GateViewState(fetchStatus = FetchGateStatus.DefaultState, user = userList,
            spaceObject = objectModel)
            }
        }

    private fun updateIron(newAmount: Int) {

        var totalAmount: Long? = null
        val batch = firebaseFirestore.batch()
        val item = userList.cargo?.find { it.itemId == 4L }
            item?.let { totalAmount = item.itemAmount!! + newAmount
                if (totalAmount!! > userList.cargoCapacity!!) {
                    viewState = viewState.copy(fetchStatus = FetchGateStatus.ShowCapacityError)
                    return }
                if (userList.locationType == SpaceObjectType.DEBRIS) {
                    if (totalAmount!! > objectModel.ironAmount) {
                        item.itemAmount = item.itemAmount!! +
                                objectModel.ironAmount
                        removeCurrentDebris()
                        createNewDebris()
                    } else {
                        item.itemAmount = item.itemAmount!! + newAmount
                        batch.update(planetDocument!!, "ironAmount", objectModel.ironAmount -
                                newAmount)
                    }
                    batch.update(userDocument, "cargo", userList.cargo)
                    batch.commit()
                    viewAction = GateAction.ShowUpdateIronToast(newAmount, totalAmount!!)
                    return
                }
        item.itemAmount = item.itemAmount!! + newAmount
        batch.update(userDocument, "cargo", userList.cargo)
        batch.commit()
                viewAction = GateAction.ShowUpdateIronToast(newAmount, totalAmount!!)
                return
        }

        val iron = Item(4, newAmount.toLong())
        userList.cargo?.add(iron)
        batch.update(userDocument, "cargo", userList.cargo)
        batch.commit()
        viewAction = GateAction.ShowUpdateIronToast(newAmount, totalAmount ?: newAmount.toLong())
    }
    private fun removeCurrentDebris() {
        planetDocument!!.delete().addOnSuccessListener {
            viewState = viewState.copy(fetchStatus = FetchGateStatus.DebrisIsRemoved)
        }
    }

    private fun createNewDebris() {
        val debris = HashMap<String, Any>()
        val x = RandomUtils.randomCoordinate
        val y = RandomUtils.randomCoordinate
        debris["x"] = x
        debris["y"] = y
        debris["sumXY"] = x + y
        debris["type"] = "debris"
        debris["ironAmount"] = RandomUtils.randomDebrisIron

        val collectionReferencePlanet = firebaseFirestore.collection("Objects")
        collectionReferencePlanet.document("Debris-" + RandomUtils.getRandomDebrisName(4))
                .set(debris)
    }

    private fun getFuel() {
        val fuelToFill = getShipFuelInfo(userList.ship!!) - userList.fuel!!
        val price = fuelToFill * 1000
        if (userList.money!! >= price) {
            userDocument.update("fuel", getShipFuelInfo(userList.ship!!))
            userDocument.update("money", userList.money!! - price)
            updateUserData()
        } else {
            viewState = viewState.copy(fetchStatus = FetchGateStatus.ShowNotEnoughMoneyToBuyFuelWarning)
        }
    }

    private fun setObjectType() {
        when (userList.locationType) {
            SpaceObjectType.PLANET -> viewState = viewState.copy(fetchStatus = FetchGateStatus.OpenPlanetActivity)
            SpaceObjectType.USER -> {
                viewState = viewState.copy(fetchStatus = FetchGateStatus.SetFightType)
                viewAction = GateAction.OpenFightActivity(userList.locationName)
            }
            SpaceObjectType.FUEL -> getFuel()
            SpaceObjectType.DEBRIS -> viewState = viewState.copy(fetchStatus = FetchGateStatus.MineDebris)
            SpaceObjectType.METEORITE_FIELD -> viewState = viewState.copy(fetchStatus = FetchGateStatus.MineIron)
        }
    }

    override fun obtainEvent(viewEvent: GateEvent) {
        when (viewEvent) {
            is GateEvent.SetObjectType -> { setObjectType() }
            is GateEvent.UpdateIron -> { updateIron() }
        }
    }

    private fun updateIron() {
        val random = Random().nextDouble() * 100
        if (random in 0.0..30.0) {
            viewAction = GateAction.ShowMineFailedSnackbar
        }
        if (random in 30.0..40.0) {
            updateIron(1)
        }
        if (random in 40.0..60.0) {
            updateIron(2)
        }
        if (random in 60.0..80.0) {
            updateIron(3)
        }
        if (random in 80.0..90.0) {
            updateIron(5)
        }
        if (random in 90.0..95.0) {
            updateIron(10)
        }
        if (random in 95.0..97.5) {
            updateIron(50)
        }
        if (random in 97.5..100.0) {
            updateIron(100)
        }
    }
}