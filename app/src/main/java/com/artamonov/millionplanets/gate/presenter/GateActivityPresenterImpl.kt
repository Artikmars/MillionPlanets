package com.artamonov.millionplanets.gate.presenter

import com.artamonov.millionplanets.gate.GateActivityView
import com.artamonov.millionplanets.model.Item
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.SpaceObjectType
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.RandomUtils
import com.artamonov.millionplanets.utils.getShipFuelInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.HashMap

class GateActivityPresenterImpl(private var getView: GateActivityView) : GateActivityPresenter<GateActivityView> {

    internal var firebaseFirestore = FirebaseFirestore.getInstance()
    private var firebaseUser: FirebaseUser? = null
    private var userList: User = User()
    private var objectModel: SpaceObject = SpaceObject()
    private var userDocument: DocumentReference? = null
    private var planetDocument: DocumentReference? = null

    override fun initFirebase() {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseFirestore = FirebaseFirestore.getInstance()
    }

    override fun getUserList(): User? {
        return userList
    }

    override fun getObjectModel(): SpaceObject? {
        return objectModel
    }

    override fun initData() {
        userDocument = firebaseFirestore.collection("Objects")
                .document(firebaseUser?.displayName!!)
        userDocument!!.get().addOnSuccessListener {
            doc ->
            userList = doc.toObject(User::class.java)!!
            getView.setUserData(userList)
            planetDocument = firebaseFirestore.collection("Objects")
                    .document(userList.locationName!!)
            planetDocument!!.get().addOnSuccessListener {
                doc2 ->
                objectModel = doc2.toObject(SpaceObject::class.java)!!
            }
        }
    }

    override fun updateIron(newAmount: Int) {

        var totalAmount: Long? = null
        val batch = firebaseFirestore.batch()
        val item = userList.cargo?.find { it.itemId == 4L }
            item?.let { totalAmount = item.itemAmount!! + newAmount
                if (totalAmount!! > userList.cargoCapacity!!) { getView.showCapacityError(); return }
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
                    batch.update(userDocument!!, "cargo", userList.cargo)
                    batch.commit()
                    getView.showUpdateIronToast(newAmount, totalAmount!!)
                    return
                }
        item.itemAmount = item.itemAmount!! + newAmount
        batch.update(userDocument!!, "cargo", userList.cargo)
        batch.commit()
        getView.showUpdateIronToast(newAmount, totalAmount!!)
            return
        }

        val iron = Item(4, newAmount.toLong())
        userList.cargo?.add(iron)
        batch.update(userDocument!!, "cargo", userList.cargo)
        batch.commit()
        getView.showUpdateIronToast(newAmount, totalAmount ?: newAmount.toLong())
    }
    private fun removeCurrentDebris() {
        planetDocument!!.delete().addOnSuccessListener {
            getView.debrisIsRemoved()
        }
    }

    private fun createNewDebris() {
        val debris = HashMap<String, Any>()
        val x = RandomUtils.getRandomCoordinate()
        val y = RandomUtils.getRandomCoordinate()
        debris["x"] = x
        debris["y"] = y
        debris["sumXY"] = x + y
        debris["type"] = "debris"
        debris["ironAmount"] = RandomUtils.getRandomDebrisIron()

        val collectionReferencePlanet = firebaseFirestore.collection("Objects")
        collectionReferencePlanet.document("Debris-" + RandomUtils.getRandomDebrisName(4))
                .set(debris)
    }

    override fun getUserIron(documentSnapshot: DocumentSnapshot?) {
        getView.setUserIron(userList, documentSnapshot)
    }

    private fun getFuel() {
        val fuelToFill = getShipFuelInfo(userList.ship!!) - userList.fuel!!
        val price = fuelToFill * 1000
        if (userList.money!! >= price) {
            userDocument!!.update("fuel", getShipFuelInfo(userList.ship!!))
            userDocument!!.update("money", userList.money!! - price)
            initData()
        } else {
            getView.showNotEnoughMoneyToBuyFuelWarning()
        }
    }

    override fun setObjectType() {
        when (userList.locationType) {
            SpaceObjectType.PLANET -> getView.openPlanetActivity()
            SpaceObjectType.USER -> {
                getView.setFightType()
                getView.openFightActivity(userList.locationName)
            }
            SpaceObjectType.FUEL -> getFuel()
            SpaceObjectType.DEBRIS -> getView.mineDebris()
            SpaceObjectType.METEORITE_FIELD -> getView.mineIron()
        }
    }
}