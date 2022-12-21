package org.wit.animarker.models

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.wit.animarker.helpers.*
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*

const val JSON_FILE = "animarkers.json"
val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting()
    .registerTypeAdapter(Uri::class.java, UriParser())
    .create()
val listType: Type = object : TypeToken<ArrayList<AnimarkerModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class AnimarkerJSONStore(private val context: Context) : AnimarkerStore {

    var animarkers = mutableListOf<AnimarkerModel>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<AnimarkerModel> {
        logAll()
        return animarkers
    }

    override fun findAll(animarkersList: MutableLiveData<List<AnimarkerModel>>) {
        TODO("Not yet implemented")
    }

    override fun findAll(userid: String, animarkersList: MutableLiveData<List<AnimarkerModel>>) {
        TODO("Not yet implemented")
    }

    override fun create(animarker: AnimarkerModel) {
        animarker.id = generateRandomId()
        animarkers.add(animarker)
        serialize()
    }

    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, animarker: AnimarkerModel) {
        TODO("Not yet implemented")
    }

    override fun update(animarker: AnimarkerModel) {
        var foundAnimarker: AnimarkerModel? = animarkers.find{ l -> l.id == animarker.id}
        if (foundAnimarker != null) {
            foundAnimarker.title = animarker.title
            foundAnimarker.destination = animarker.destination
            foundAnimarker.description = animarker.description
            foundAnimarker.date = animarker.date
            foundAnimarker.image = animarker.image
            logAll()
        }
        serialize()
    }

    override fun update(userid: String, animarkerid: String, animarker: AnimarkerModel) {
        TODO("Not yet implemented")
    }

    override fun delete(animarker: AnimarkerModel) {
        animarkers.remove(animarker)
        serialize()
    }

    override fun delete(userid: String, animarkerid: String) {
        TODO("Not yet implemented")
    }

    override fun findById(
        userid: String,
        animarkerid: String,
        animarker: MutableLiveData<AnimarkerModel>
    ) {
        TODO("Not yet implemented")
    }

    override fun updateImageRef(userid: String, toString: String) {
        TODO("Not yet implemented")
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(animarkers, listType)
        Timber.i("JSON STRING == " + jsonString)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        animarkers = gsonBuilder.fromJson(jsonString, listType)
    }

    private fun logAll() {
        animarkers.forEach { Timber.i("$it") }
    }
}

class UriParser : JsonDeserializer<Uri>,JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}