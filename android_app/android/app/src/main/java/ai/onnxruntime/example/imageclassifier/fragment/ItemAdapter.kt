package ai.onnxruntime.example.imageclassifier.fragment

import ai.onnxruntime.example.imageclassifier.R

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ItemAdapter(private val context: Context,
                  private var items: List<MainData>,
                  private val onDeleteClick: (MainData) -> Unit
) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_row_main, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val item = items[position]
        // 加载图片（使用 Glide 或 Picasso）
        Glide.with(context).asBitmap().fitCenter().load(item.imageUrl.toUri()).into(holder.imageView)
        holder.timeView.text = item.timeInfo
        holder.bsView.text = item.bsInfo
        holder.rrView.text = item.rrInfo
        holder.rsView.text = item.rsInfo
        holder.btDel.setOnClickListener({
            onDeleteClick(item)
        })
    }

    override fun getItemCount() = items.size

    fun updateData(newList: List<MainData>) {
        items = newList
        notifyDataSetChanged()
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.im_plant)
        val timeView: TextView = view.findViewById(R.id.his_time)
        val bsView: TextView = view.findViewById(R.id.his_bs)
        val rrView: TextView = view.findViewById(R.id.his_rr)
        val rsView: TextView = view.findViewById(R.id.his_rs)
        val btDel: ImageView = view.findViewById(R.id.bt_delete)
    }

}
