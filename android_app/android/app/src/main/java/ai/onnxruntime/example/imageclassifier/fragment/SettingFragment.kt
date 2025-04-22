package ai.onnxruntime.example.imageclassifier.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ai.onnxruntime.example.imageclassifier.R
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {
    private lateinit var database: RoomDB
    private lateinit var dao: MainDataDao
    private lateinit var btnClear: Button
    private lateinit var btnUpdate: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        btnClear = view.findViewById(R.id.btnClearRecords)
        btnUpdate = view.findViewById(R.id.btnUpdateModel)

        // **初始化数据库**
        database = RoomDB.getDatabase(requireContext())
        dao = database.mainDataDao()

        // **清空数据库按钮**
        btnClear.setOnClickListener {
            clearDatabase()
        }

        // **更新模型按钮**（暂时不执行任何操作）
        btnUpdate.setOnClickListener {
            Toast.makeText(requireContext(), "更新已完成", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    // **清空数据库**
    private fun clearDatabase() {
        lifecycleScope.launch {
            dao.clearAll() // 调用 DAO 方法清空数据库
            Toast.makeText(requireContext(), "数据库已清空", Toast.LENGTH_SHORT).show()
        }
    }
}