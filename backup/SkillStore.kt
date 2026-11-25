package com.jarvis.skills

import androidx.room.*

/**
 * OGRENILENLERIN KUTUPHANESI
 * Her basarili gorev buraya skill olarak kaydolur.
 * Sonraki sefer vector search ile bulunur.
 */
@Entity
data class Skill(
    @PrimaryKey val id: String,
    val aciklama: String,
    val tetikler: List<String>, // ["whatsapp yaz", "wp gonder"]
    val adimlar: List<String>, // JSON adimlar
    var kullanim: Int,
    var basari: Int = 0,
    var ortSureMs: Int = 0
)

@Dao
interface SkillDao {
    @Query("SELECT * FROM Skill")
    suspend fun hepsiniAl(): List<Skill>

    @Query("SELECT * FROM Skill WHERE :istek LIKE '%' || tetikler || '%' LIMIT 1")
    suspend fun ara(istek: String): Skill?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun kaydet(skill: Skill)

    @Query("UPDATE Skill SET kullanim = kullanim + 1 WHERE id = :id")
    suspend fun kullanimArttir(id: String)
}

@Database(entities = [Skill::class], version = 1)
@TypeConverters(Converters::class)
abstract class SkillDatabase : RoomDatabase() {
    abstract fun skillDao(): SkillDao
}

class SkillStore(private val dao: SkillDao) {
    suspend fun ara(istek: String): Skill? {
        // Basit: contains search. Gelismiste: embedding + cosine similarity
        // Ornek: istek="Mehmet'e wpden yaz" -> tetikler contains "wp" -> eslesti
        return dao.ara(istek.lowercase())
    }
    suspend fun kaydet(skill: Skill) = dao.kaydet(skill)
    suspend fun calistir(skill: Skill, istek: String) {
        // Kaydedilmis adimlari direkt calistir, vision YOK
        dao.kullanimArttir(skill.id)
        // Adimlari parse edip JarvisAccessibilityService'e gonder
        // Parametreleri doldur: {kisi} -> istekten LLM ile cikar
    }
}

class Converters {
    @TypeConverter fun fromList(list: List<String>): String = list.joinToString("||")
    @TypeConverter fun toList(data: String): List<String> = if(data.isEmpty()) emptyList() else data.split("||")
}
