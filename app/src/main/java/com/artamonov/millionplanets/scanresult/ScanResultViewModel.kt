package com.artamonov.millionplanets.scanresult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList
import kotlin.math.abs

class ScanResultViewModel : ViewModel() {

    internal var objectModelList: MutableList<SpaceObject>? = null
    internal var userList = User()
    private val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private var userLiveData: MutableLiveData<User> = MutableLiveData()
    private var openPlanetLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var openGateLiveData: MutableLiveData<Int> = MutableLiveData()
    private var openMoveLiveData: MutableLiveData<Int> = MutableLiveData()
    private var objectModelLiveData: MutableLiveData<MutableList<SpaceObject>> = MutableLiveData()
    private var userRepository: UserRepository = UserRepository()
    private val userCollection = FirebaseFirestore.getInstance().collection("Objects")

    init {
        loadUser()
    }

    fun getUser(): LiveData<User> {
        return userLiveData
    }

    fun getObject(): LiveData<MutableList<SpaceObject>> {
        return objectModelLiveData
    }

    fun getOpenPlanetLiveData(): LiveData<Boolean> {
        return openPlanetLiveData
    }

    fun getOpenGateLiveData(): LiveData<Int> {
        return openGateLiveData
    }

    fun getOpenMoveLiveData(): LiveData<Int> {
        return openMoveLiveData
    }

    private fun loadData() {
        userCollection.whereLessThanOrEqualTo(
                "sumXY",
                userList.sumXY!! + userList.scanner_capacity!!)
                .whereGreaterThanOrEqualTo(
                        "sumXY",
                        userList.sumXY!! - userList.scanner_capacity!!)
                .get().addOnSuccessListener { queryDocumentSnapshots ->
                    objectModelList = ArrayList()
                    for (document in queryDocumentSnapshots) {

                        val objectModel = document.toObject(SpaceObject::class.java)
                        objectModel.name = document.id

                        //  Distinguish between (2;8) and (3;7)
                        if (objectModel.sumXY == userList.sumXY) {
                            objectModel.distance = abs(objectModel.x?.toInt()!! - userList.x!!.toInt())
                        } else { objectModel.distance = abs(objectModel.sumXY?.toInt()!! -
                                userList.sumXY!!.toInt())
                        }

                        if (objectModel.distance <= userList.scanner_capacity!!) {
                            objectModelList?.add(objectModel)
                        }
                    }

                    // Excluding the user itself in the search
                    for (i in objectModelList!!.indices) {
                        if (objectModelList!![i].name == firebaseUser?.displayName) {
                            objectModelList!!.removeAt(i)
                            break
                        }
                    }

                    objectModelList?.sortWith(Comparator { objectModel, t1 ->
                        objectModel.distance - t1.distance
                    })
                    objectModelLiveData.value = objectModelList
                }
    }

    private fun loadUser() {
        userRepository.getUserFromFirestore(userName = firebaseUser?.displayName!!).addOnSuccessListener { doc ->
            userLiveData.value = doc
            userList = doc
            loadData() }
    }

    fun onObjectClicked(pos: Int) {
        userList.moveToLocationName = objectModelList!![pos].name
        userList.moveToLocationType = objectModelList!![pos].type
        userList.moveToObjectDistance = objectModelList!![pos].distance.toLong()
        userCollection.document(firebaseUser?.displayName!!).set(userList)

        if (objectModelList!![pos].distance == 0) {
            when (objectModelList!![pos].type) {
                "planet" -> {
                    openPlanetLiveData.value = true
                }
                else -> { openGateLiveData.value = pos }
            }
        } else {
            openMoveLiveData.value = pos
        }
    }
}
