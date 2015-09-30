# AndroidWearSensorLoggor

    This is a Smart Watch Sensor Loggor.
    It can record the sensor data of the Android watch/phone, such as:
        TYPE_ACCELEROMETER;
        TYPE_MAGNETIC_FIELD;
        TYPE_ROTATION_VECTOR;
        TYPE_GYROSCOPE_UNCALIBRATED;
        TYPE_ORIENTATION;
        TYPE_GRAVITY;
        TYPE_GAME_ROTATION_VECTOR;
    
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