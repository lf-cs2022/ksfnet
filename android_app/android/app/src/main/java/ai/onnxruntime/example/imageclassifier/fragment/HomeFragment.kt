package ai.onnxruntime.example.imageclassifier.fragment

import SharedViewModel
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.example.imageclassifier.ORTAnalyzer
import ai.onnxruntime.example.imageclassifier.R
import ai.onnxruntime.example.imageclassifier.Result
import ai.onnxruntime.example.imageclassifier.databinding.FragmentHomeBinding
import ai.onnxruntime.example.imageclassifier.preProcess
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.SystemClock
import android.renderscript.ScriptGroup.Binding
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.opencv.android.OpenCVLoader
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.exp


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var dis_bs_val: String? = null
    private var dis_rs_val: String? = null
    private var dis_rr_val: String? = null

    private lateinit var database: RoomDB
    private lateinit var dao: MainDataDao




    private lateinit var viewModel: SharedViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
//    private lateinit var binding: FragmentHomeBinding

    private val backgroundExecutor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }
    private val labelData: List<String> by lazy { readLabels() }
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    private var ortEnv: OrtEnvironment? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var enableQuantizedModel: Boolean = false
    private var cameraFlag=0

    val CAMERA_PERMISSION_REQUEST_CODE = 101

    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = RoomDB.getDatabase(requireContext())
        dao = database.mainDataDao()

        // 先注册 ActivityResultLauncher
        pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
//            Glide.with(this).load(uri).into(binding.imageView)

                Glide.with(this)
                    .asBitmap() // 加载为
                    .fitCenter()
                    .load(uri) // 图片 URL
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            // 图片加载成功后的回调
                            Log.d("GlideExample", "Bitmap loaded: ${resource.width}x${resource.height}")
                            // 在此处理 Bitmap
                            binding.imageView.setImageBitmap(resource)

                            runmodel(resource)

                            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                            val newData = MainData(imageUrl = uri.toString(),
                                timeInfo = sdf.format(Date()),
                                bsInfo = "褐条病概率：$dis_bs_val",
                                rrInfo = "赤腐病概率：$dis_rr_val",
                                rsInfo = "环斑病概率：$dis_rs_val"
                                )

                            lifecycleScope.launch(Dispatchers.IO) {
                                try {
//                                    val newData = MainData(imageUrl = "abc", textInfo = "111")
                                    dao.insertData(newData)
                                    Log.d("DatabaseTest", "数据已插入: $newData")
                                } catch (e: Exception) {
                                    Log.e("DatabaseTest", "数据库插入失败: ${e.message}")
                                }
//
                            }
                            Toast.makeText(requireContext(), "数据已存储", Toast.LENGTH_SHORT).show()
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            // 当资源被清理时调用
                            Log.d("GlideExample", "Bitmap resource cleared")
                        }
                    })
            }
            else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = RoomDB.getDatabase(requireContext())
        dao = database.mainDataDao()

//        val detected_item_1 = view.findViewById<TextView>(R.id.detected_item_1)
//        val detected_item_2 = view.findViewById<TextView>(R.id.detected_item_2)
//        val detected_item_3 = view.findViewById<TextView>(R.id.detected_item_3)
//        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
//        detected_item_1.visibility=View.GONE
//        detected_item_2.visibility=View.GONE
//        detected_item_3.visibility=View.GONE
        binding.detectedItem1.visibility=View.GONE

        ortEnv = OrtEnvironment.getEnvironment()
        OpenCVLoader.initDebug();
        binding.viewFinder.visibility =View.GONE
        binding.detectedItem1.visibility=View.GONE
        binding.detectedItem2.visibility= View.GONE
        binding.detectedItem3.visibility=View.GONE
        binding.inferenceTime.visibility=View.GONE
        binding.camera.setOnClickListener(View.OnClickListener {
            // Request Camera permission

            if (binding.imageView.visibility == View.VISIBLE) {
                binding.imageView.visibility= View.GONE
                binding.viewFinder.visibility =View.VISIBLE
            }
            binding.detectedItem1.visibility=View.VISIBLE
            binding.detectedItem2.visibility= View.VISIBLE
            binding.detectedItem3.visibility=View.VISIBLE
            binding.inferenceTime.visibility=View.VISIBLE
            checkAndRequestCameraPermission()

        })
//        // Request Camera permission
//        if (allPermissionsGranted()) {
//            startCamera()
//        } else {
//            ActivityCompat.requestPermissions(
//                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
//            )
//        }

        binding.photo.setOnClickListener(View.OnClickListener {
            if(cameraFlag==1){
                backgroundExecutor.shutdown()

                cameraFlag=0
            }
            if (binding.imageView.visibility == View.GONE) {
                binding.imageView.visibility = View.VISIBLE
                binding.viewFinder.visibility =View.GONE
            }
            binding.detectedItem1.visibility=View.VISIBLE
            binding.detectedItem2.visibility= View.VISIBLE
            binding.detectedItem3.visibility=View.VISIBLE
            binding.inferenceTime.visibility=View.VISIBLE

            binding.textView2.visibility=View.GONE
            binding.textView3.visibility=View.GONE
            binding.textView4.visibility=View.GONE

            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        })
    }

    fun checkAndRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 如果没有权限，则请求权限
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            // 如果权限已经授予，直接启用摄像头
            startCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，继续执行打开摄像头的操作
                startCamera()
            } else {
                // 权限被拒绝，提示用户
                Toast.makeText(requireContext(), "摄像头权限被拒绝", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun running(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = FragmentHomeBinding.inflate(layoutInflater)
//        val view = binding.root
//        setContentView(view)
        ortEnv = OrtEnvironment.getEnvironment()
        OpenCVLoader.initDebug();
        binding.viewFinder.visibility =View.GONE
        binding.detectedItem1.visibility=View.GONE
        binding.detectedItem2.visibility= View.GONE
        binding.detectedItem3.visibility=View.GONE
        binding.inferenceTime.visibility=View.GONE
        binding.camera.setOnClickListener(View.OnClickListener {
            // Request Camera permission

                if (binding.imageView.visibility == View.VISIBLE) {
                    binding.imageView.visibility= View.GONE
                    binding.viewFinder.visibility =View.VISIBLE
                }
                binding.detectedItem1.visibility=View.VISIBLE
                binding.detectedItem2.visibility= View.VISIBLE
                binding.detectedItem3.visibility=View.VISIBLE
                binding.inferenceTime.visibility=View.VISIBLE
                startCamera()

        })
//        // Request Camera permission
//        if (allPermissionsGranted()) {
//            startCamera()
//        } else {
//            ActivityCompat.requestPermissions(
//                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
//            )
//        }

        binding.photo.setOnClickListener(View.OnClickListener {
            if(cameraFlag==1){
                backgroundExecutor.shutdown()

                cameraFlag=0
            }
            if (binding.imageView.visibility == View.GONE) {
                binding.imageView.visibility = View.VISIBLE
                binding.viewFinder.visibility =View.GONE
            }
            binding.detectedItem1.visibility=View.VISIBLE
            binding.detectedItem2.visibility= View.VISIBLE
            binding.detectedItem3.visibility=View.VISIBLE
            binding.inferenceTime.visibility=View.VISIBLE
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        })

//        binding.enableQuantizedmodelToggle.setOnCheckedChangeListener { _, isChecked ->
//            enableQuantizedModel = isChecked
//            setORTAnalyzer()
//        }
    }

    private fun startCamera() {
        if (cameraFlag==0){
            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

            cameraProviderFuture.addListener(Runnable {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                // Preview
                binding.viewFinder.scaleType = PreviewView.ScaleType.FIT_CENTER
                val preview = Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                    }

                imageCapture = ImageCapture.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .build()

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                try {
                    cameraProvider.unbindAll()

                    cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture, imageAnalysis
                    )
                } catch (exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                }

                setORTAnalyzer()
            }, ContextCompat.getMainExecutor(requireContext()))
        }
        cameraFlag=1
    }

//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
//    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundExecutor.shutdown()
        ortEnv?.close()
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            if (allPermissionsGranted()) {
//                startCamera()
//            } else {
//                Toast.makeText(
//                    this,
//                    "Permissions not granted by the user.",
//                    Toast.LENGTH_SHORT
//                ).show()
//                finish()
//            }
//
//        }
//    }

    private fun updateUI(result: Result) {
        if (result.detectedScore.isEmpty())
            return
        getActivity()?.runOnUiThread {
    //            binding.percentMeter.progress = (result.detectedScore[0] * 100).toInt()
            binding.detectedItem1.text = labelData[result.detectedIndices[0]]
            binding.detectedItemValue1.text = "%.2f%%".format(result.detectedScore[0] * 100)
            binding.detectedItem2.text = labelData[result.detectedIndices[1]]
            binding.detectedItemValue2.text = "%.2f%%".format(result.detectedScore[1] * 100)
            binding.detectedItem3.text = labelData[result.detectedIndices[2]]
            binding.detectedItemValue3.text = "%.2f%%".format(result.detectedScore[2] * 100)
    //            if (result.detectedIndices.size > 1) {
    //                binding.detectedItem2.text = labelData[result.detectedIndices[1]]
    //                binding.detectedItemValue2.text = "%.2f%%".format(result.detectedScore[1] * 100)
    //            }
    //
    //            if (result.detectedIndices.size > 2) {
    //                binding.detectedItem3.text = labelData[result.detectedIndices[2]]
    //                binding.detectedItemValue3.text = "%.2f%%".format(result.detectedScore[2] * 100)
    //            }

            binding.inferenceTimeValue.text = result.processTimeMs.toString() + "ms"
        }
    }

    // Read MobileNet V2 classification labels
    private fun readLabels(): List<String> {
//        return resources.openRawResource(R.raw.imagenet_classes).bufferedReader().readLines()
        return resources.openRawResource(R.raw.sugarcane_class_en).bufferedReader().readLines()
    }

    // Read ort model into a ByteArray, run in background
    private suspend fun readModel(): ByteArray = withContext(Dispatchers.IO) {
//        val modelID =
//            if (enableQuantizedModel) R.raw.mobilenetv2_int8 else R.raw.mobilenetv2_fp32
        val modelID = R.raw.model_3
        resources.openRawResource(modelID).readBytes()
    }

    private fun readModel1(): ByteArray{
//        val modelID =
//            if (enableQuantizedModel) R.raw.mobilenetv2_int8 else R.raw.mobilenetv2_fp32
        val modelID = R.raw.model_3
        return  resources.openRawResource(modelID).readBytes()
    }

    // Create a new ORT session in background
    private suspend fun createOrtSession(): OrtSession? = withContext(Dispatchers.Default) {
        ortEnv?.createSession(readModel())
    }

    // Create a new ORT session and then change the ImageAnalysis.Analyzer
    // This part is done in background to avoid blocking the UI
    private fun setORTAnalyzer(){
        scope.launch {
            imageAnalysis?.clearAnalyzer()
            imageAnalysis?.setAnalyzer(
                backgroundExecutor,
                ORTAnalyzer(createOrtSession(), ::updateUI)
            )
        }
    }



    private fun runmodel(image: Bitmap){
        // Convert the input image to bitmap and resize to 224x224 for model input
        val bitmap = image.let { Bitmap.createScaledBitmap(it, 224, 224, false) }
        val ortSession: OrtSession?= ortEnv?.createSession(readModel1())



        if (bitmap != null) {


            val imgData = preProcess(bitmap)
            val inputName = ortSession?.inputNames?.iterator()?.next()
            val shape = longArrayOf(1, 3, 224, 224)
            val env = OrtEnvironment.getEnvironment()
            env.use {
                val tensor = OnnxTensor.createTensor(env, imgData, shape)
                val startTime = SystemClock.uptimeMillis()
                tensor.use {
                    val output = ortSession?.run(Collections.singletonMap(inputName, tensor))
                    val processTimeMs= SystemClock.uptimeMillis() - startTime+35
                    output.use {

                        @Suppress("UNCHECKED_CAST")
                        val rawOutput = ((output?.get(0)?.value) as Array<FloatArray>)[0]
                        val probabilities = sigmoid(rawOutput)
                        val top3=getTop3(probabilities)
                        binding.inferenceTimeValue.text=processTimeMs.toString()+"ms"
                        binding.detectedItem1.text=labelData[0]
                        binding.detectedItemValue1.text= "%.2f%%".format(probabilities[0] * 100)
                        binding.detectedItem2.text=labelData[1]
                        binding.detectedItemValue2.text= "%.2f%%".format(probabilities[1] * 100)
                        binding.detectedItem3.text=labelData[2]
                        binding.detectedItemValue3.text= "%.2f%%".format(probabilities[2] * 100)
//                        Log.d("ONNXRuntime", "Predicted class: " + top3[0])
                        Log.d("ONNXRuntime", "Prediction Done" )
                        dis_bs_val="%.2f%%".format(probabilities[0] * 100)
                        dis_rr_val="%.2f%%".format(probabilities[1] * 100)
                        dis_rs_val="%.2f%%".format(probabilities[2] * 100)
                    }
                }
            }
        }

    }

    private fun sigmoid(modelResult: FloatArray): FloatArray {
        val labelVals = modelResult.copyOf()
        return labelVals.map { 1 / (1 + kotlin.math.exp(-it.toDouble())).toFloat() }.toFloatArray()
    }

    private fun softMax(modelResult: FloatArray): FloatArray {
        val labelVals = modelResult.copyOf()
        val max = labelVals.max()
        var sum = 0.0f

        // Get the reduced sum
        for (i in labelVals.indices) {
            labelVals[i] = exp(labelVals[i] - max)
            sum += labelVals[i]
        }

        if (sum != 0.0f) {
            for (i in labelVals.indices) {
                labelVals[i] /= sum
            }
        }
        return labelVals
    }

    private fun getTop3(labelVals: FloatArray): List<Int> {
        var indices = mutableListOf<Int>()
        for (k in 0..2) {
            var max: Float = 0.0f
            var idx: Int = 0
            for (i in 0..labelVals.size - 1) {
                val label_val = labelVals[i]
                if (label_val > max && !indices.contains(i)) {
                    max = label_val
                    idx = i
                }
            }

            indices.add(idx)
        }

        return indices.toList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_home, container, false)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.getRoot()
    }

    override fun onDestroyView() {
        // 首先调用super.onDestroyView()，以确保Fragment的基类也执行其销毁逻辑
        super.onDestroyView()
        // 将_binding设置为null，释放与视图绑定相关的资源
        _binding = null
    }

    companion object {
        public const val TAG = "ORTImageClassifier"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

}