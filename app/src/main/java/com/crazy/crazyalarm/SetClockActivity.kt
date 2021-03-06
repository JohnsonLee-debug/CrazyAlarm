package com.crazy.crazyalarm

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.crazy.crazyalarm.clockUtils.AlarmManagerUtil
import com.crazy.crazyalarm.databinding.ActivitySetClockBinding
import com.crazy.crazyalarm.view.SelectCloseModePopUp
import com.crazy.crazyalarm.view.SelectCycleFlagPopup
import com.crazy.crazyalarm.view.SelectNoticeFlagPopup
import java.util.*

typealias NoticeFlag = Int
typealias Mode = Int

class SetClockActivity : AppCompatActivity() , View.OnClickListener{
    private lateinit var binding: ActivitySetClockBinding
    private lateinit var timePickerDialog: TimePickerDialog
    companion object{
        private var cycle: AlarmManagerUtil.CycleFlag = AlarmManagerUtil.Once
        private var cycleDaysOfWeek: Int? = null
        private var noticeFlag: NoticeFlag = AlarmManagerUtil.BothSoundAndVibrator
        private var mode: Mode = AlarmManagerUtil.NormMode
        private var hour: Int? = null
        private var minute: Int? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetClockBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val instance = Calendar.getInstance()
        val hourOfDay = instance.get(Calendar.HOUR_OF_DAY)
        val currMinute = instance.get(Calendar.MINUTE)
        val onTimeSetListener = { _: TimePicker, _hour:Int, _minute: Int ->
            runOnUiThread{
                binding.dateTv.text = String.format("%02d:%02d",_hour, _minute)
            }
            hour = _hour
            minute = _minute
        }
        timePickerDialog = TimePickerDialog(this, onTimeSetListener, hourOfDay, currMinute, true)
        binding.dateTv.setOnClickListener(this)
        binding.ringRl.setOnClickListener(this)
        binding.closeRl.setOnClickListener(this)
        binding.repeatRl.setOnClickListener(this)
        binding.setBtn.setOnClickListener(this)
    }
    private fun selectNoticeWay(){
        val noticePopup = SelectNoticeFlagPopup(this)
        runOnUiThread {
            noticePopup.showPopup(binding.root)
        }
        noticePopup.setOnclickListener { flag: NoticeFlag ->
            when (flag) {
                AlarmManagerUtil.OnlyVibrator -> {
                    runOnUiThread {
                        binding.tvRingValue.text = "??????"
                    }
                    noticeFlag = AlarmManagerUtil.OnlyVibrator
                }
                AlarmManagerUtil.OnlySound -> {
                    runOnUiThread {
                        binding.tvRingValue.text = "??????"
                    }
                    noticeFlag = AlarmManagerUtil.OnlySound
                }
                AlarmManagerUtil.BothSoundAndVibrator->{
                    runOnUiThread {
                        binding.tvRingValue.text = "???????????????"
                    }
                    noticeFlag = AlarmManagerUtil.BothSoundAndVibrator
                }
            }
            runOnUiThread {
                noticePopup.dismiss()
            }
        }
    }
    private fun selectCycle(){
        val cycleFlagPopup = SelectCycleFlagPopup(this)
        runOnUiThread{
            cycleFlagPopup.showPopup(binding.root)
        }
        cycleFlagPopup.setOnClickListener { flag: Int, ret: String->
            when(flag) {
                // ??????
                7->{
                    val repeat = ret.toInt()
                    val repeatVal = parseRepeat(repeat, 0)
                    runOnUiThread{
                        binding.tvRepeatValue.text = repeatVal
                    }
                    cycle = AlarmManagerUtil.Weekly
                    cycleDaysOfWeek = ret.toInt()
                    cycleFlagPopup.dismiss()
                }
                // ??????
                8->{
                    runOnUiThread{
                        binding.tvRepeatValue.text = "??????"
                    }
                    cycle = AlarmManagerUtil.Daily
                    cycleFlagPopup.dismiss()
                }
                // ????????????
                9->{
                    runOnUiThread{
                        binding.tvRepeatValue.text = "????????????"
                    }
                    cycle = AlarmManagerUtil.Once
                    cycleFlagPopup.dismiss()
                }
            }
        }
    }

    private fun parseRepeat(r: Int, flag: Int): String {
        var repeat = r
        var cycle = ""
        var weeks = ""
        if (repeat == 0) {
            repeat = 127
        }
        if (repeat % 2 == 1) {
            cycle = "??????"
            weeks = "1"
        }
        if (repeat % 4 >= 2) {
            if ("" == cycle) {
                cycle = "??????"
                weeks = "2"
            } else {
                cycle = "$cycle,??????"
                weeks = "$weeks,2"
            }
        }
        if (repeat % 8 >= 4) {
            if ("" == cycle) {
                cycle = "??????"
                weeks = "3"
            } else {
                cycle = "$cycle,??????"
                weeks = "$weeks,3"
            }
        }
        if (repeat % 16 >= 8) {
            if ("" == cycle) {
                cycle = "??????"
                weeks = "4"
            } else {
                cycle = "$cycle,??????"
                weeks = "$weeks,4"
            }
        }
        if (repeat % 32 >= 16) {
            if ("" == cycle) {
                cycle = "??????"
                weeks = "5"
            } else {
                cycle = "$cycle,??????"
                weeks = "$weeks,5"
            }
        }
        if (repeat % 64 >= 32) {
            if ("" == cycle) {
                cycle = "??????"
                weeks = "6"
            } else {
                cycle = "$cycle,??????"
                weeks = "$weeks,6"
            }
        }
        if (repeat / 64 == 1) {
            if ("" == cycle) {
                cycle = "??????"
            } else {
                cycle = "$cycle,??????"
                weeks = "$weeks,7"
            }
        }
        if (cycle == "??????,??????,??????,??????,??????,??????,??????"){
            cycle = "??????"
        }
        return if (flag == 0) cycle else weeks
    }

    private fun setClock(){
        if (hour != null && minute != null){
            when(cycle){
                is AlarmManagerUtil.Daily,AlarmManagerUtil.Once->{
                    val id = AlarmManagerUtil.generateId(this)
                    AlarmManagerUtil.setAlarm(
                        this,
                        hour!!,
                        minute!!,
                        cycle,
                        "????????????",
                        id,
                        id,
                        cycleDaysOfWeek!!,
                        noticeFlag,
                        0,
                        mode
                    )
                }
                is AlarmManagerUtil.Weekly->{
                    val weekStr = parseRepeat(cycleDaysOfWeek!!, 1)
                    val days = weekStr.split(",")
                    val pid = AlarmManagerUtil.generateId(this)
                    for((i, day) in days.withIndex()){
                        if(i == 0){
                            AlarmManagerUtil.setAlarm(
                                this,
                                hour!!,
                                minute!!,
                                cycle,
                                "????????????",
                                pid,
                                pid,
                                cycleDaysOfWeek!!,
                                noticeFlag,
                                day.toInt(),
                                mode
                            )
                        } else{
                            AlarmManagerUtil.setAlarm(
                                this,
                                hour!!,
                                minute!!,
                                cycle,
                                "????????????",
                                AlarmManagerUtil.generateId(this),
                                pid,
                                cycleDaysOfWeek!!,
                                noticeFlag,
                                day.toInt(),
                                mode
                            )
                        }
                    }
                }
            }
            Toast.makeText(this, "??????????????????", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this, "??????????????????????????????", Toast.LENGTH_LONG).show()
        }
    }
    private fun selectCloseMode(){
        val closePopup = SelectCloseModePopUp(this)
        runOnUiThread{
            closePopup.showPopup(binding.root)
        }
        closePopup.setOnClickListener { flag:Mode->
            when (flag) {
                AlarmManagerUtil.NormMode -> {
                    runOnUiThread{
                        binding.tvCloseValue.setText("????????????")
                    }
                }
                AlarmManagerUtil.MathMode -> {
                    runOnUiThread{
                        binding.tvCloseValue.setText("????????????")
                    }
                }
                AlarmManagerUtil.JigsawMode -> {
                    runOnUiThread{
                        binding.tvCloseValue.setText("????????????")
                    }
                }
                AlarmManagerUtil.ScanMode -> {
                    runOnUiThread{
                        binding.tvCloseValue.setText("????????????")
                    }
                }
            }
            mode = flag
            closePopup.dismiss()
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.date_tv->{
                timePickerDialog.show()
            }
            R.id.repeat_rl->{
                selectCycle()
            }
            R.id.ring_rl->{
                selectNoticeWay()
            }
            R.id.close_rl->{
                selectCloseMode()
            }
            R.id.set_btn->{
                setClock()
            }
        }
    }
}