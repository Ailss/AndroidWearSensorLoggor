# AndroidWearSensorLoggor
    Android

    This is a Smart Watch Sensor Loggor.
    It can record the sensor data of the Android watch/phone, such as:
        TYPE_ACCELEROMETER;
        TYPE_MAGNETIC_FIELD;
        TYPE_ROTATION_VECTOR;
        TYPE_GYROSCOPE_UNCALIBRATED;
        TYPE_ORIENTATION;
        TYPE_GRAVITY;
        TYPE_GAME_ROTATION_VECTOR;
    It records the data in TXT. Then we can use AndroidWearDataProcess to process the data
    AndroidWearDataProcess: https://github.com/lylalala/AndroidWearDataProcess.git
    
    environment: Android
--------------------------------------------------------------------------------
    
    这是一个记录安卓手机/手表传感器数据的工程。
    AndroidStudio下开发的。

--------------------------------------------------------------------------------
    使用说明：
    Wear文件下的Wear是用于安卓手表的代码。（作者的手表是LG Watch）
    其中：
    MainActivity：实现了主界面（开始和结束按钮）；注释中有获得本机所有传感器的代码；
    SensorLoggingService:调用传感器的相关代码；
    SensorLoggingAsyncTask:数据记录的相关代码，可以修改要记录的传感器的种类等信息。

    开始记录后3s后开始采集数据（第一次震动）
    然后3s进行方向标定（手臂伸直朝前，表面向上，结束时第二次震动）
    接着5s让用户将手移动到初始位置（5s到第三次震动）
    第三次震动后用户可以随意做动作