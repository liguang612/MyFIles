package com.example.myfiles

import android.content.Intent
import android.os.Bundle
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfiles.adapter.FileAdapter
import com.example.myfiles.data.RequestCode
import com.example.myfiles.databinding.ActivityMainBinding
import com.example.myfiles.model.FileModel
import java.io.File
import java.util.Stack
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fileAdapter: FileAdapter

    private lateinit var notAllowedDialog: AlertDialog
    private lateinit var permissionDialog: AlertDialog

    private var stackFiles: Stack<List<FileModel>> = Stack()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

        if (checkPermission()) loadFiles(Environment.getExternalStorageDirectory().absolutePath)
        else permissionDialog.show()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (stackFiles.empty()) finish()
                else loadBackFiles()
            }
        })
    }

    private fun initView() {
        val naDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_not_allowed, null)

        notAllowedDialog = AlertDialog.Builder(this)
            .setView(naDialogView)
            .setPositiveButton("OK", {_, _ -> })
            .create()

        val pDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_not_permit, null)
        permissionDialog = AlertDialog.Builder(this)
            .setView(pDialogView)
            .setPositiveButton("Đồng ý") { _, _ -> if (checkPermission()) requestPermission() }
            .setNegativeButton("Từ chối") { _, _ -> finish() }
            .create()

        fileAdapter = FileAdapter(this, emptyList()) { fileModel ->
            when {
                fileModel.isDirectory -> loadFiles(fileModel.path)
                fileModel.name.endsWith(".txt") -> openTextFile(fileModel)
                else -> notAllowedDialog.show()
            }
        }

        binding.rvListFile.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = fileAdapter
        }
    }

    private fun checkPermission() : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                return true;
            }
        }
        return false;
    }
    private fun requestPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
        startActivityForResult(intent, RequestCode.REQUEST_PERMISSION)
    }

    private fun loadFiles(path: String) {
        val directory = File(path)
        val files = directory.listFiles()?.map {
            file -> FileModel(
                name = file.name,
                path = file.absolutePath,
                isDirectory = file.isDirectory
            )
        }?.sortedWith(compareBy({!it.isDirectory}, {it.name.lowercase() }))
            ?: emptyList()

        stackFiles.push(files)
        fileAdapter.setFiles(files)
    }
    private fun loadBackFiles() {
        stackFiles.pop()
        fileAdapter.setFiles(stackFiles.peek())
    }

    private fun openTextFile(fileModel: FileModel) {
        val it = Intent(this, DocumentActivity::class.java)
        it.putExtra("path", fileModel.path)

        startActivity(it)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RequestCode.REQUEST_PERMISSION -> {
                if (checkPermission()) loadFiles(Environment.getExternalStorageDirectory().absolutePath)
                else permissionDialog.show()
            }
        }
    }
}