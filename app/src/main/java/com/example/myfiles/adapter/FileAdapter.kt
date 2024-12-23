package com.example.myfiles.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myfiles.R
import com.example.myfiles.databinding.ItemFileBinding
import com.example.myfiles.model.FileModel

class FileAdapter(private val context: Context, private var files: List<FileModel>, private val onItemClick: (FileModel) -> Unit)
    : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    class FileViewHolder(val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding)
    }

    override fun getItemCount(): Int = files.size

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]
        val tvFileName = holder.binding.tvFileName

        tvFileName.text = file.name
        if (file.isDirectory)
            tvFileName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_folder, 0, 0, 0)
        else {
            tvFileName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_file, 0, 0, 0)
            tvFileName.setTextColor(context.getColor(R.color.black))
        }

        holder.binding.tvFileName.setOnClickListener { onItemClick(file) }
    }

    fun setFiles(files: List<FileModel>) {
        this.files = files;
        notifyDataSetChanged()
    }
}