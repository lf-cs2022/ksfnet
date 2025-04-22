package ai.onnxruntime.example.imageclassifier.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import ai.onnxruntime.example.imageclassifier.R
import ai.onnxruntime.example.imageclassifier.databinding.FragmentHistoryBinding
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var itemList: MutableList<MainData>

    private lateinit var database: RoomDB
    private lateinit var dao: MainDataDao

//    private var _binding: FragmentHistoryBinding? = null
//    private val binding get() = _binding!!


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
//        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return inflater.inflate(R.layout.fragment_history, container, false)
//        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
//        if (flag==0){
//            viewModel.uri1.observe(viewLifecycleOwner, Observer { uri1 ->
//                // 这里可以更新 UI，例如设置 TextView 的文本
//                Glide.with(this).asBitmap() // 加载为
//                    .fitCenter()
//                    .load(uri1)
//                    .into(binding.imageView3)
//            })
//
//            viewModel.dis11p.observe(viewLifecycleOwner, Observer { dis11p ->
//                // 这里可以更新 UI，例如设置 TextView 的文本
//                binding.dis11Prob.text=dis11p
//            })
//            viewModel.time1.observe(viewLifecycleOwner, Observer { time1 ->
//                // 这里可以更新 UI，例如设置 TextView 的文本
//                binding.time1Val.text=time1
//            })
//            viewModel.dis12p.observe(viewLifecycleOwner, Observer { dis12p ->
//                // 这里可以更新 UI，例如设置 TextView 的文本
//                binding.dis12Prob.text=dis12p
//            })
//            viewModel.dis13p.observe(viewLifecycleOwner, Observer { dis13p ->
//                // 这里可以更新 UI，例如设置 TextView 的文本
//                binding.dis13Prob.text=dis13p
//            })
//            flag=1
//        }
//        if(flag==1){
//            viewModel.uri2.observe(viewLifecycleOwner, Observer { uri2 ->
//                // 这里可以更新 UI，例如设置 TextView 的文本
//                Glide.with(this).asBitmap() // 加载为
//                    .fitCenter()
//                    .load(uri2)
//                    .into(binding.imageView4)
//            })
//            viewModel.time2.observe(viewLifecycleOwner, Observer { time2 ->
//                // 这里可以更新 UI，例如设置 TextView 的文本
//                binding.time2Val.text=time2
//            })
//            viewModel.dis21p.observe(viewLifecycleOwner, Observer { dis21p ->
//                // 这里可以更新 UI，例如设置 TextView 的文本
//                binding.dis21Prob.text=dis21p
//            })
//            viewModel.dis22p.observe(viewLifecycleOwner, Observer { dis22p ->
//                // 这里可以更新 UI，例如设置 TextView 的文本
//                binding.dis22Prob.text=dis22p
//            })
//            viewModel.dis23p.observe(viewLifecycleOwner, Observer { dis23p ->
//                // 这里可以更新 UI，例如设置 TextView 的文本
//                binding.dis23Prob.text=dis23p
//            })
//            flag=2
//        }
//
//
//    }

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    recyclerView = view.findViewById(R.id.recycle_view)
    recyclerView.layoutManager = LinearLayoutManager(requireContext())

    database = RoomDB.getDatabase(requireContext())
    dao = database.mainDataDao()
    // 从数据库加载数据
//    val itemDao = Room.databaseBuilder(
//        requireContext(),
//        RoomDB::class.java, "app-database"
//    ).build().mainDataDao()

//    val itemDao = RoomDB.getDatabase(requireContext()).mainDataDao()


    dao.getAllItems().observe(viewLifecycleOwner, Observer { items ->
        Log.d("DatabaseTest", "数据数量: ${items.size}")

//        if (!::itemAdapter.isInitialized) {
//            itemAdapter = ItemAdapter(requireContext(), items.toMutableList()) { item ->
//                deleteItem(item) // 调用删除方法
//            }
//            recyclerView.adapter = itemAdapter
//        } else {
//            itemAdapter.updateData(items) // 更新 RecyclerView 数据
//        }

        itemList = items.toMutableList()
        itemAdapter = ItemAdapter(requireContext(), items.toMutableList()) { item ->
            deleteItem(item) // 调用删除方法
        }
        recyclerView.adapter = itemAdapter
    })
    Log.d("FragmentTest", "YourFragment 加载成功")

}
    private fun deleteItem(item: MainData) {
        lifecycleScope.launch {
            dao.deleteById(item.id) // 从数据库删除
            itemList.remove(item) // 从列表中移除
            itemAdapter.updateData(itemList) // 更新 RecyclerView
        }
    }


//    override fun onDestroyView() {
//        // 首先调用super.onDestroyView()，以确保Fragment的基类也执行其销毁逻辑
//        super.onDestroyView()
//        // 将_binding设置为null，释放与视图绑定相关的资源
//        _binding = null
//    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
                HistoryFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}