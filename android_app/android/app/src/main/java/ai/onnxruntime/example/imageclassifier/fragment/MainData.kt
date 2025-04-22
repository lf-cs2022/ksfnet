package ai.onnxruntime.example.imageclassifier.fragment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "table_name")

data class MainData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageUrl: String, // 图片 URL
    val timeInfo: String,  // 文字信息
    val bsInfo: String,
    val rrInfo: String,
    val rsInfo: String
)

//public data class MainData:Serializable {
//    @PrimaryKey(autoGenerate = true)
//    private var ID:Int=0
//
//    @ColumnInfo(name = "text")
//    private var text:String=""
//
//    public fun getID(): Int {
//        return ID
//    }
//
//    public fun getText(): String {
//        return text
//    }
//
//    public fun setID(id:Int) {
//        this.ID=id
//    }
//
//    public fun setText(text:String) {
//        this.text=text
//    }
//
//
//}
