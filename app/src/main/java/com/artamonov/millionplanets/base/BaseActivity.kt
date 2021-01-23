package com.artamonov.millionplanets.base

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

    @Inject lateinit var firebaseFirestore: FirebaseFirestore
    @Inject lateinit var firebaseUser: FirebaseUser
    @Inject lateinit var firebaseAuth: FirebaseAuth
    @Inject lateinit var userDocument: DocumentReference
}
