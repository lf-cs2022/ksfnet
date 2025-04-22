import androidx.lifecycle.LiveData

class History : LiveData<History?>() {
    var uri1: String? = null
    var dis1_1_prob: String? = null
    var dis1_2_prob: String? = null
    var dis1_3_prob: String? = null
    var time1: String? = null

    fun getUri(): String? {
        return uri1
    }

    fun getDis11p(): String? {
        return dis1_1_prob
    }

    fun getDis12p(): String? {
        return dis1_2_prob
    }

    fun getDis13p(): String? {
        return dis1_3_prob
    }

    fun getTime(): String? {
        return time1
    }

    fun setUri(uri1: String?) {
        this.uri1 = uri1
        postValue(this)
    }

    fun setDis11p(dis1_1_prob: String?) {
        this.dis1_1_prob = dis1_1_prob
        postValue(this)
    }

    fun setDis12p(dis1_2_prob: String?) {
        this.dis1_2_prob = dis1_2_prob
        postValue(this)
    }

    fun setDis13p(dis1_3_prob: String?) {
        this.dis1_3_prob = dis1_3_prob
        postValue(this)
    }

    fun setTime(time1: String?) {
        this.time1 = time1
        postValue(this)
    }

}