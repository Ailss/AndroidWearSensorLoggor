# AndroidWearSensorLoggor
---
##语言与环境
* Android
* AndroidStudio下开发
</br>
</br>

##英文简介
* This is a Smart Watch Sensor Loggor.
* It can record the sensor data of the Android watch/phone, such as:
TYPE_ACCELEROMETER;
TYPE_MAGNETIC_FIELD;
TYPE_ROTATION_VECTOR;
TYPE_GYROSCOPE_UNCALIBRATED;
TYPE_ORIENTATION;
TYPE_GRAVITY;
TYPE_GAME_ROTATION_VECTOR;
* It records the data in TXT. Then we can use AndroidWearDataProcess to process the data
* AndroidWearDataProcess: <https://github.com/lylalala/AndroidWearDataProcess.git>

</br>
</br>

##中文简介与使用说明
###这是一个记录安卓手机/手表传感器数据的工程。
###内容说明：
- Wear文件下的Wear是用于安卓手表的代码。（作者的手表是LG Watch）
- MainActivity：实现了主界面（开始和结束按钮）；注释中有获得本机所有传感器的代码；
- SensorLoggingService:调用传感器的相关代码；
- SensorLoggingAsyncTask:数据记录的相关代码，可以修改要记录的传感器的种类等信息。

###使用说明：
- 开始记录后3s后开始采集数据（第一次震动）
- 然后3s进行方向标定（手臂伸直朝前，表面向上，结束时第二次震动）
- 接着5s让用户将手移动到初始位置（5s到第三次震动）
- 第三次震动后用户可以随意做动作

</br>
</br>

##问题反馈
在使用中有任何问题，欢迎反馈给我，可以用以下联系方式跟我交流

* email: <liuyang070424@gmail.com>
* QQ: 359250464