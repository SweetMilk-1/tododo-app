package ru.tododo.data.settings.impl

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import ru.tododo.data.settings.api.AppSettings
import java.io.InputStream
import java.io.OutputStream

internal object AppSettingsSerializer : Serializer<AppSettings> {

    override val defaultValue: AppSettings = AppSettings.DEFAULT

    override suspend fun readFrom(input: InputStream): AppSettings =
        try {
            Json.decodeFromString(AppSettings.serializer(), input.readBytes().decodeToString())
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read Settings", serialization)
        }

    override suspend fun writeTo(t: AppSettings, output: OutputStream) {
        output.write(Json.encodeToString(AppSettings.serializer(), t).encodeToByteArray())
    }
}