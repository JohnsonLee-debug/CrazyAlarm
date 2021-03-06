package com.crazy.crazyalarm.clockUtils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.crazy.crazyalarm.R
import com.crazy.crazyalarm.databinding.DialogSimpleBinding

class SimpleDialog(context: Context) : Dialog(context){
    companion object{
        private const val default_width = WindowManager.LayoutParams.WRAP_CONTENT
        private const val default_height = WindowManager.LayoutParams.WRAP_CONTENT
        const val TYPE_TWO_BT = 2
        const val TYPE_NO_BT = 0;
    }
    var dialogSimpleBinding: DialogSimpleBinding
    val mContext: Context = context
    init {
        dialogSimpleBinding = DialogSimpleBinding.bind(
            LayoutInflater.from(mContext).inflate(R.layout.dialog_simple, null))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(dialogSimpleBinding.root)
        setTitle("测试标题")
        setMsg("测试信息")
        dialogSimpleBinding.dialogMessage.clearFocus()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return false
        }
        return super.onKeyDown(keyCode, event)
    }
    fun setConfirmListener(listener: View.OnClickListener) {
        dialogSimpleBinding.dialogConfirm.setOnClickListener(listener)
    }
    fun setTitle(title: String) {
        dialogSimpleBinding.dialogTitle.text = title
    }
    fun setMsg(msg: String) {
        dialogSimpleBinding.dialogMessage.text = msg
    }
}