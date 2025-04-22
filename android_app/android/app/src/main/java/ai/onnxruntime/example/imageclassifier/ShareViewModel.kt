import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _uri1 = MutableLiveData<Uri>()
    val uri1: LiveData<Uri> get() = _uri1

    fun updateUri1(newUri1: Uri) {
        _uri1.value = newUri1
    }

    private val _uri2 = MutableLiveData<Uri>()
    val uri2: LiveData<Uri> get() = _uri2

    fun updateUri2(newUri2: Uri) {
        _uri2.value = newUri2
    }

    private val _time1 = MutableLiveData<String>()
    val time1: LiveData<String> get() = _time1

    fun updateTime1(newTime1: String) {
        _time1.value = newTime1
    }

    private val _dis11p = MutableLiveData<String>()
    val dis11p: LiveData<String> get() = _dis11p

    fun updateDis11p(newDis11p: String) {
        _dis11p.value = newDis11p
    }

    private val _dis12p = MutableLiveData<String>()
    val dis12p: LiveData<String> get() = _dis12p

    fun updateDis12p(newDis12p: String) {
        _dis12p.value = newDis12p
    }
    private val _dis13p = MutableLiveData<String>()
    val dis13p: LiveData<String> get() = _dis13p

    fun updateDis13p(newDis13p: String) {
        _dis13p.value = newDis13p
    }

    private val _time2 = MutableLiveData<String>()
    val time2: LiveData<String> get() = _time2

    fun updateTime2(newTime2: String) {
        _time2.value = newTime2
    }

    private val _dis21p = MutableLiveData<String>()
    val dis21p: LiveData<String> get() = _dis21p

    fun updateDis21p(newDis21p: String) {
        _dis21p.value = newDis21p
    }

    private val _dis22p = MutableLiveData<String>()
    val dis22p: LiveData<String> get() = _dis22p

    fun updateDis22p(newDis22p: String) {
        _dis22p.value = newDis22p
    }
    private val _dis23p = MutableLiveData<String>()
    val dis23p: LiveData<String> get() = _dis23p

    fun updateDis23p(newDis23p: String) {
        _dis23p.value = newDis23p
    }
}

//class SharedViewModel : ViewModel() {
//    private val history: History = History()
//    private var ls: MutableList<History>? = null
//
//    val listHistory: List<Any>?
//        get() {
//            ls = ArrayList<History>()
//            return ls
//        }
//
//}