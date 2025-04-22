package ai.onnxruntime.example.imageclassifier.fragment


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MainDataDao {

    @Query("SELECT * FROM table_name")
    fun getAllItems(): LiveData<List<MainData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(data: MainData)

    @Query("DELETE FROM table_name WHERE id = :itemId")
    suspend fun deleteById(itemId: Int)

    @Query("DELETE FROM table_name")
    suspend fun clearAll() // 清空数据库

}
