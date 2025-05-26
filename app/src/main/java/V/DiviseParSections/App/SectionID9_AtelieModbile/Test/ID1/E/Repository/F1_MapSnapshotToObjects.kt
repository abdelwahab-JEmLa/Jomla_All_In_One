package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.TypeTarificationEnumT2
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.getKeyFireBase
import com.google.firebase.database.DataSnapshot
import kotlin.reflect.KClass

inline fun <reified T : Any> getDatas(
    snapshot: DataSnapshot,
    kClass: KClass<T>,
    results: MutableList<T>
) {
    println("=== getDatas Debug ===")
    println("Snapshot key: ${snapshot.key}")
    println("Snapshot exists: ${snapshot.exists()}")
    println("Children count: ${snapshot.childrenCount}")
    println("Target class: ${kClass.simpleName}")

    for (childSnap in snapshot.children) {
        try {
            println("--- Processing child: ${childSnap.key} ---")

            when (T::class) {
                A_ProduitInfos::class -> {
                    val produit = mapToProduitInfos(childSnap)
                    if (produit != null) {
                        results.add(produit as T)
                        println("Successfully mapped A_ProduitInfos: ${produit.nomArticleFinale}")
                    }
                }
                D_TarificationInfos::class -> {
                    val tarification = mapToTarificationInfos(childSnap)
                    if (tarification != null) {
                        results.add(tarification as T)
                        println("Successfully mapped D_TarificationInfos: ${tarification.nom}")
                    }
                }
                else -> {
                    // Fallback to reflection-based mapping
                    val instance = mapUsingReflection<T>(childSnap, kClass)
                    if (instance != null) {
                        results.add(instance)
                        println("Successfully mapped using reflection: ${kClass.simpleName}")
                    }
                }
            }
        } catch (e: Exception) {
            println("Error mapping child ${childSnap.key}: ${e.message}")
            e.printStackTrace()
            // Continue processing other items
        }
    }

    println("=== Final Results ===")
    println("Total mapped items: ${results.size}")
    println("====================")
}

fun mapToProduitInfos(childSnap: DataSnapshot): A_ProduitInfos? {
    return try {
        val idArticle = childSnap.child("idArticle").getValue(Long::class.java) ?: 0L
        val nomArticleFinale = childSnap.child("nomArticleFinale").getValue(String::class.java) ?: ""
        val keyFireBase = childSnap.key ?: getKeyFireBase(idArticle, nomArticleFinale)

        println("Mapping A_ProduitInfos - ID: $idArticle, Name: $nomArticleFinale, Key: $keyFireBase")

        A_ProduitInfos(
            idArticle = idArticle,
            nomArticleFinale = nomArticleFinale,
            classementCate = childSnap.child("classementCate").getValue(Double::class.java) ?: 0.0,
            nomArab = childSnap.child("nomArab").getValue(String::class.java) ?: "",
            autreNomDarticle = childSnap.child("autreNomDarticle").getValue(String::class.java),
            nmbrCat = childSnap.child("nmbrCat").getValue(Int::class.java) ?: 0,
            couleur1 = childSnap.child("couleur1").getValue(String::class.java),
            idcolor1 = childSnap.child("idcolor1").getValue(Long::class.java) ?: 0L,
            couleur2 = childSnap.child("couleur2").getValue(String::class.java),
            idcolor2 = childSnap.child("idcolor2").getValue(Long::class.java) ?: 0L,
            couleur3 = childSnap.child("couleur3").getValue(String::class.java),
            idcolor3 = childSnap.child("idcolor3").getValue(Long::class.java) ?: 0L,
            couleur4 = childSnap.child("couleur4").getValue(String::class.java),
            idcolor4 = childSnap.child("idcolor4").getValue(Long::class.java) ?: 0L,
            nomCategorie2 = childSnap.child("nomCategorie2").getValue(String::class.java),
            nmbrUnite = childSnap.child("nmbrUnite").getValue(Int::class.java) ?: 0,
            nmbrCaron = childSnap.child("nmbrCaron").getValue(Int::class.java) ?: 0,
            affichageUniteState = childSnap.child("affichageUniteState").getValue(Boolean::class.java) ?: false,
            commmentSeVent = childSnap.child("commmentSeVent").getValue(String::class.java),
            afficheBoitSiUniter = childSnap.child("afficheBoitSiUniter").getValue(String::class.java),
            monPrixAchat = childSnap.child("monPrixAchat").getValue(Double::class.java) ?: 0.0,
            clienPrixVentUnite = childSnap.child("clienPrixVentUnite").getValue(Double::class.java) ?: 0.0,
            minQuan = childSnap.child("minQuan").getValue(Int::class.java) ?: 0,
            monBenfice = childSnap.child("monBenfice").getValue(Double::class.java) ?: 0.0,
            monPrixVent = childSnap.child("monPrixVent").getValue(Double::class.java) ?: 0.0,
            neaon2 = childSnap.child("neaon2").getValue(String::class.java) ?: "",
            idCategorie = childSnap.child("idCategorie").getValue(Double::class.java) ?: 0.0,
            catalogeParentID = childSnap.child("catalogeParentID").getValue(Long::class.java) ?: 0L,
            funChangeImagsDimention = childSnap.child("funChangeImagsDimention").getValue(Boolean::class.java) ?: false,
            nomCategorie = childSnap.child("nomCategorie").getValue(String::class.java) ?: "",
            neaon1 = childSnap.child("neaon1").getValue(Double::class.java) ?: 0.0,
            lastUpdateState = childSnap.child("lastUpdateState").getValue(String::class.java) ?: "",
            cartonState = childSnap.child("cartonState").getValue(String::class.java) ?: "",
            dateCreationCategorie = childSnap.child("dateCreationCategorie").getValue(String::class.java) ?: "",
            prixDeVentTotaleChezClient = childSnap.child("prixDeVentTotaleChezClient").getValue(Double::class.java) ?: 0.0,
            benficeTotaleEntreMoiEtClien = childSnap.child("benficeTotaleEntreMoiEtClien").getValue(Double::class.java) ?: 0.0,
            benificeTotaleEn2 = childSnap.child("benificeTotaleEn2").getValue(Double::class.java) ?: 0.0,
            monPrixAchatUniter = childSnap.child("monPrixAchatUniter").getValue(Double::class.java) ?: 0.0,
            monPrixVentUniter = childSnap.child("monPrixVentUniter").getValue(Double::class.java) ?: 0.0,
            benificeClient = childSnap.child("benificeClient").getValue(Double::class.java) ?: 0.0,
            monBeneficeUniter = childSnap.child("monBeneficeUniter").getValue(Double::class.java) ?: 0.0,
            diponibilityState = childSnap.child("diponibilityState").getValue(String::class.java) ?: "",
            cLeDataOuvertDuParentList = childSnap.child("cLeDataOuvertDuParentList").getValue(Boolean::class.java) ?: false,
            articleHaveUniteImages = childSnap.child("articleHaveUniteImages").getValue(Boolean::class.java) ?: false,
            itsNewArrivale = childSnap.child("itsNewArrivale").getValue(Boolean::class.java) ?: false,
            imageDimention = childSnap.child("imageDimention").getValue(String::class.java) ?: "",
            idForSearchArticles = childSnap.child("idForSearchArticles").getValue(Long::class.java) ?: 0L,
            keyFireBase = keyFireBase,
            timestamps = childSnap.child("timestamps").getValue(Long::class.java) ?: System.currentTimeMillis(),
            needUpdate = childSnap.child("needUpdate").getValue(Boolean::class.java) ?: true
        )
    } catch (e: Exception) {
        println("Error in mapToProduitInfos: ${e.message}")
        e.printStackTrace()
        null
    }
}

fun mapToTarificationInfos(childSnap: DataSnapshot): D_TarificationInfos? {
    return try {
        val id = childSnap.child("id").getValue(Long::class.java) ?: 0L
        val nom = childSnap.child("nom").getValue(String::class.java) ?: ""
        val keyFireBase = childSnap.key ?: getKeyFireBase(id, nom)

        println("Mapping D_TarificationInfos - ID: $id, Name: $nom, Key: $keyFireBase")

        // Map the enum field safely
        val typeTarificationEnumString = childSnap.child("typeTarificationEnumT2Correspond").getValue(String::class.java) ?: "PRIX_BASE"
        val typeTarificationEnum = try {
            TypeTarificationEnumT2.valueOf(typeTarificationEnumString)
        } catch (e: IllegalArgumentException) {
            println("Warning: Unknown enum value '$typeTarificationEnumString', using default PRIX_BASE")
            TypeTarificationEnumT2.PRIX_BASE
        }

        D_TarificationInfos(
            id = id,
            idParentProduit = childSnap.child("idParentProduit").getValue(Long::class.java) ?: 0L,
            typeTarificationEnumT2Correspond = typeTarificationEnum,
            parentIdClient = childSnap.child("parentIdClient").getValue(Long::class.java) ?: 0L,
            prixCurrency = childSnap.child("prixCurrency").getValue(Double::class.java) ?: 0.0,
            timestamps = childSnap.child("timestamps").getValue(Long::class.java) ?: System.currentTimeMillis(),
            nom = nom,
            needUpdate = childSnap.child("needUpdate").getValue(Boolean::class.java) ?: true,
            keyFireBase = keyFireBase
        )
    } catch (e: Exception) {
        println("Error in mapToTarificationInfos: ${e.message}")
        e.printStackTrace()
        null
    }
}

// Public inline function for reflection-based mapping
inline fun <reified T : Any> mapUsingReflection(
    childSnap: DataSnapshot,
    kClass: KClass<T>
): T? {
    return try {
        // This would be implemented based on your specific reflection needs
        // For now, returning null as a safe fallback
        println("Reflection-based mapping not implemented for ${kClass.simpleName}")
        null
    } catch (e: Exception) {
        println("Error in reflection mapping: ${e.message}")
        e.printStackTrace()
        null
    }
}
