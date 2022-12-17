package org.wit.animarker.models

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList
import java.lang.reflect.Type
import com.google.gson.*
import org.wit.animarker.helpers.exists
import org.wit.animarker.helpers.read
import org.wit.animarker.helpers.write
import timber.log.Timber
import timber.log.Timber.i
import java.nio.file.Files.exists

const val JSON_FILE1 = "users.json"
val gsonBuilder2: Gson = GsonBuilder().setPrettyPrinting()
    .create()
val listType1: Type = object : TypeToken<ArrayList<UserModel>>() {}.type

fun generateRandomUserId(): Long {
    return Random().nextLong()
}

class UserJSONStore (private val context: Context) : UserStore {
    var users = mutableListOf<UserModel>()

    init {
        if (exists(context, JSON_FILE1)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<UserModel> {
        logAll()
        return users
    }

    override fun create(user: UserModel) {
        user.userId = generateRandomUserId()
        users.add(user)
        serialize()
    }

    override fun login(userEmail: String, userPassword: String) : Boolean {
        val users = findAll()
        var auth = false
        for(user in users) {
            if (user.userEmail == userEmail && user.userPassword == userPassword) {
                auth = true
                i("PASSWORD MATCHES")
                break
            } else {
                auth = false
                i("NO MATCHING PASSWORD")
            }
        }
        return auth
    }


// TODO use if allowing user to update details

    override fun update(user: UserModel) {
        var foundUser: UserModel? = users.find{ u -> u.userId == user.userId}
        if (foundUser != null) {
            foundUser.firstName = user.firstName
            foundUser.lastName = user.lastName
            foundUser.userEmail = user.userEmail
            foundUser.userPassword = user.userPassword
            logAll()
        }
        serialize()
    }

    override fun delete(user: UserModel) {
        users.remove(user)
        serialize()
    }

    private fun serialize() {
        val jsonString = gsonBuilder2.toJson(users, listType1)
        write(context, JSON_FILE1, jsonString)
    }
    private fun deserialize() {
        val jsonString = read(context, JSON_FILE1)
        users = gsonBuilder2.fromJson(jsonString, listType1)
    }

    private fun logAll() {
        users.forEach { Timber.i("$it") }
    }
}
