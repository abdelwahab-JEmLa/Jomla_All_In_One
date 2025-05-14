package Z_CodePartageEntreApps.Proto.Learning.DataBases

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class RealmDataBase {
    var childsLists: RealmList<Produit> = realmListOf()

    class Produit : RealmObject {
        @PrimaryKey
        var bsonObjectId: ObjectId = ObjectId()
        var infosKey: String = "nom"
        var childsList: RealmList<Client> = realmListOf()

        class Client : RealmObject {
            @PrimaryKey
            var bsonObjectId: ObjectId = ObjectId()
            var childsList: RealmList<TypeTarification> = realmListOf()

            class TypeTarification : RealmObject {
                @PrimaryKey
                var bsonObjectId: ObjectId = ObjectId()
                var childsList: RealmList<Tarification> = realmListOf()

                class Tarification : RealmObject {
                    @PrimaryKey
                    var bsonObjectId: ObjectId = ObjectId()
                }
            }
        }
    }
}
