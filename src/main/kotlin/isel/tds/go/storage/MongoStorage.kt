package isel.tds.go.storage

import com.mongodb.MongoWriteException
import isel.tds.go.mongo.*

class MongoStorage<Key, Data>(
    collectionName:  String,
    driver: MongoDriver,
    private val serializer: Serializer<Data>
    ) : Storage<Key, Data>{

    data class Doc(
        val _id: String,
        val data: String
    )

    val docs = driver.getCollection<Doc>(collectionName)

    private fun Doc(key: Key, data: Data) = Doc(key.toString(), serializer.serialize(data))

    override fun create(key: Key, data: Data) {
        try {
            docs.insertDocument(Doc(key, data))
        } catch (e: MongoWriteException) {
            throw IllegalStateException("$key already exists")
        }
    }

    override fun read(key: Key): Data? {
        return docs.getDocument(key.toString())?.let { serializer.deserialize(it.data) }
    }

    override fun update(key: Key, data: Data) {
        check(docs.replaceDocument(key.toString(), Doc(key, data))) { "Doc $key does not exist to update" }
    }

    override fun delete(key: Key) {
        check(docs.deleteDocument(key.toString())) { "Doc $key does not exist to delete" }
    }
}

