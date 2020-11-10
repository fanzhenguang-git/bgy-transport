/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.constant;

public class EntityConstant {

    private EntityConstant() {
    }

    /**
     * Generic constants
     */
    public static final String COLUMN_ID = "id";

    public static final String COLUMN_PRODUCT_ID = "product_id";

    public static final String COLUMN_DEVICE_ID = "device_id";

    public static final String COLUMN_ATTRIBUTE_ID = "attribute_id";

    public static final String COLUMN_NAME = "name";

    public static final String COLUMN_DISPLAY_NAME = "display_name";

    public static final String COLUMN_STATE = "state";

    public static final String COLUMN_TYPE = "type";

    public static final String COLUMN_CREATE_TIME = "create_time";

    public static final String COLUMN_UPDATE_TIME = "update_time";

    public static final String COLUMN_NODE_TYPE = "node_type";

    public static final String COLUMN_CREATED_AT = "created_at";

    public static final String COLUMN_UPDATED_AT = "updated_at";

    public static final String COLUMN_DETAILED_LOCATION = "detailed_location";

    public static final String COLUMN_LONGITUDE = "longitude";

    public static final String COLUMN_ORGANIZATION_ID = "organization_id";

    public static final String COLUMN_PHONE_NUM = "phone_num";

    public static final String COLUMN_ROLE_ID = "role_id";

    public static final String COLUMN_IDENTITY = "identity";

    public static final String COLUMN_SUPER_ADMIN = "super_admin";

    public static final String COLUMN_LOGIN_ADDRESS = "login_address";

    public static final String COLUMN_LOGIN_AT = "login_at";

    public static final String COLUMN_LOGIN_FIRST_TIME = "login_first_time";

    public static final String COLUMN_LOGIN_IP = "login_ip";

    public static final String COLUMN_CREDENTIAL = "credential";

    public static final String COLUMN_DESCRIPTION = "description";

    public static final String COLUMN_ADDRESS = "address";

    public static final String COLUMN_ENABLE = "enable";

    public static final String COLUMN_TENANT_ID = "tenant_id";

    public static final String COLUMN_LATITUDE = "latitude";

    public static final String COLUMN_SYSTEM_ROLE = "system_role";

    public static final String COLUMN_ONLY_TENANT = "only_tenant";

    public static final String COLUMN_SIGNAL_ID = "signal_id";

    public static final String COLUMN_SIGNAL_VAL = "signal_val";

    public static final String COLUMN_ALARM_TIME = "alarm_time";

    public static final String COLUMN_ALARM_LEVEL = "alarm_level";

    public static final String COLUMN_CONTENT = "content";

    /**
     * product
     */
    public static final String TABLE_PRODUCT = "product";

    /**
     * product_attribute
     */
    public static final String TABLE_PRODUCT_ATTRIBUTE = "product_attribute";

    public static final String COLUMN_DIRECTION = "direction";

    public static final String COLUMN_DATA_TYPE = "data_type";

    /**
     * device
     */
    public static final String TABLE_DEVICE = "device";

    public static final String COLUMN_PARENT_DEVICE_ID = "parent_device_id";

    public static final String COLUMN_ONLINE_STATUS = "online_status";

    public static final String COLUMN_TOKEN = "token";

    public static final String COLUMN_MANUFACTURER_ID = "manufacturer_id";

    public static final String MINIO_BUCKET = "deliver-server-qiji-hikvision";

    public static final String MINIO_BUCKET_BROADCAST = "deliver-server-lt-broadcast";

    /**
     * device raw
     */
    public static final String TABLE_DEVICE_RAW = "device_raw";

    /**
     * device data
     */
    public static final String TABLE_DEVICE_DATA = "device_data";

    public static final String COLUMN_BOOL_VALUE = "bool_value";

    public static final String COLUMN_INT_VALUE = "int_value";

    public static final String COLUMN_DOUBLE_VALUE = "double_value";

    public static final String COLUMN_STRING_VALUE = "string_value";

    /**
     * device alarm
     */
    public static final String TABLE_DEVICE_ALARM = "device_alarm";

    /**
     * device fire record
     */
    public static final String TABLE_DEVICE_FIRE_RECORD = "device_fire_record";

    public static final String COLUMN_BATTERY_VOLTAGE = "battery_voltage";

    public static final String COLUMN_BATTERY_PERCENT = "battery_percent";

    public static final String COLUMN_VERSION = "version";

    public static final String COLUMN_DEFENSE = "defense";

    public static final String COLUMN_HEART = "heart";

    public static final String COLUMN_SNR = "snr";

    public static final String COLUMN_ALARM_INFO = "alarm_info";

    public static final String COLUMN_SIGNAL_INFO = "signal_info";

    public static final String COLUMN_OPEN_COVER = "open_cover";

    public static final String COLUMN_GET_WATER = "get_water";

    public static final String COLUMN_ICC_ID = "icc_id";

    public static final String COLUMN_CELL_ID = "cell_id";

    public static final String COLUMN_IMEI = "IMEI";

    public static final String COLUMN_IMSI = "IMSI";

    public static final String COLUMN_SNRTYPE_INFO = "snr_type";

    public static final String COLUMN_DEVICE_NAME = "device_name";

    /**
     * device trash record
     */
    public static final String TABLE_DEVICE_TRASH_RECORD = "device_trash_record";

    public static final String COLUMN_DEVICE_SN = "device_sn";

    public static final String COLUMN_LOCATION = "location";

    public static final String COLUMN_NETWORK_QUALITY = "network_quality";

    public static final String COLUMN_LATITUDE_VALUE = "latitude_value";

    public static final String COLUMN_LONGITUDE_VALUE = "longitude_value";

    public static final String COLUMN_SATELLITES_NUMBER = "satellites_number";

    public static final String COLUMN_BATTERY_STATUS = "battery_status";

    public static final String COLUMN_CHARGING_STATUS = "charging_status";

    public static final String COLUMN_DOOR_STATUS = "door_status";

    public static final String COLUMN_OPEN_NUMBER = "open_number";

    public static final String COLUMN_TRASH_STATUS = "trash_status";

    public static final String COLUMN_CLEAR_NUMBER = "clear_number";

    public static final String COLUMN_TRASH_CAPACITY = "trash_capacity";

    public static final String COLUMN_COMPRESS_NUMBER = "compress_number";

    public static final String COLUMN_FAULT_CODE = "fault_code";

    public static final String COLUMN_IS_FULL = "is_Full";

    /**
     * device Geomagnetic record
     */
    public static final String TABLE_DEVICE_GEOMAGNETIC = "device_geomagnetic";

    public static final String COLUMN_BATTERY = "battery";

    public static final String COLUMN_DEVICE_STATE = "device_state";

    public static final String COLUMN_VEHICLE_STATE = "vehicle_state";

    public static final String COLUMN_GEOGRAPHIC_LOCATION = "geographic_location";

    /**
     * device Geomagnetic record
     */
    public static final String TABLE_DEVICE_COVER_RECORD = "device_cover_record";

    public static final String COLUMN_VOLTAGE_VALUE = "voltage_value";

    public static final String COLUMN_LEVEL = "level";

    public static final String COLUMN_SIGNAL_INTENSITY = "signal_intensity";

    public static final String COLUMN_COVER_MARK = "cover_mark";

    public static final String COLUMN_SWITCH_SIGN = "switch_sign";

    public static final String COLUMN_LOCK_STATUS = "lock_status";

    public static final String COLUMN_IS_NORMAL = "is_normal";

    /**
     * device camera record
     */
    public static final String DEVICE_CAMERA_RECORD = "device_camera_record";

    public static final String CAMERA_ID = "id";

    public static final String CAMERA_CREATE_TIME = "create_time";

    public static final String CAMERA_DEVICE_ID = "device_id";

    public static final String CAMERA_PRODUCT_ID = "product_id";

    public static final String CAMERA_DEV_SN = "dev_sn";

    public static final String CAMERA_DEV_TYPE = "dev_type";

    public static final String CAMERA_STREAM_URL = "stream_url";

    public static final String CAMERA_IP = "ip";

    public static final String CAMERA_PHOTO_ID = "photo_id";

    public static final String CAMERA_IS_DELETE = "is_delete";

    public static final String CAMERA_ONLINE = "online";

    public static final String CAMERA_SUB_TYPE = "sub_type";

    public static final String CAMERA_GW_SN = "gw_sn";

    public static final String CAMERA_USER_ID = "user_id";

    public static final String CAMERA_MAC = "mac";

    public static final String CAMERA_UPDATE_TIME = "update_time";

    /**
     * device pressure record
     */
    public static final String TABLE_DEVICE_PRESSURE_RECORD = "device_pressure_record";

    public static final String COLUMN_MPA = "mpa";

    public static final String COLUMN_LOW = "low";

    /**
     * device camera alarm
     */
    public static final String DEVICE_CAMERA_ALARM = "device_camera_alarm";

    public static final String CAMERA_ALARM_ID = "id";

    public static final String CAMERA_ALARM_CREATE_TIME = "create_time";

    public static final String CAMERA_ALARM_ALARM_ID = "alarm_id";

    public static final String CAMERA_ALARM_DEVICE_ID = "device_id";

    public static final String CAMERA_MARK_ID = "mark_id";

    public static final String CAMERA_ALARM_PHOTO_ID = "photo_id";

    public static final String CAMERA_ALARM_IS_DELETE = "is_delete";

    public static final String CAMERA_ALARM_UPDATE_TIME = "update_time";

    /**
     * device environment record
     */
    public static final String TABLE_DEVICE_ENVIRONMENT_RECORD = "device_environment_record";

    public static final String COLUMN_DEVICE_TYPE = "device_type";

    public static final String COLUMN_USER_ID = "user_id";

    public static final String COLUMN_GW_SN = "gw_sn";

    public static final String COLUMN_ONLINE = "online";

    public static final String COLUMN_STATUS = "status";

    public static final String COLUMN_PM_25 = "pm25";

    public static final String COLUMN_PM_10 = "pm10";

    public static final String COLUMN_TEMPERATURE = "temperature";

    public static final String COLUMN_HUMIDITY = "humidity";

    public static final String COLUMN_AIR_PRESSURE = "air_pressure";

    public static final String COLUMN_NOISE = "noise";

    public static final String COLUMN_WIND_SPEED = "wind_speed";

    public static final String COLUMN_WIND_DIRECTION = "wind_direction";

    public static final String COLUMN_LUX = "lux";

    /**
     * device lamp record
     */
    public static final String TABLE_DEVICE_LAMP_RECORD = "device_lamp_record";

    public static final String COLUMN_TIME_POLICY = "time_policy";

    public static final String COLUMN_BRIGHTNESS = "brightness";

    public static final String COLUMN_CURRENT = "current";

    public static final String COLUMN_VOLTAGE = "voltage";

    public static final String COLUMN_ACTIVE_POWER = "active_power";

    public static final String COLUMN_ACTIVE_TOTAL_ENERGY = "active_total_energy";

    /**
     * park report
     */
    public static final String TABLE_PARK_REPORT = "park_report";

    public static final String COLUMN_ENTER_TIME = "enter_time";

    public static final String COLUMN_LEAVE_TIME = "leave_time";

    public static final String COLUMN_PARK_NAME = "park_name";

    public static final String COLUMN_COVER_ID = "cover_id";

    /**
     * bind device
     */
    public static final String TABLE_BIND_DEVICE = "bind_device";

    public static final String COLUMN_PRODUCT_TYPE = "product_type";

    public static final String COLUMN_BIND_DEVICE_ID = "bind_device_id";

    public static final String COLUMN_BIND_PRODUCT_ID = "bind_product_id";

    public static final String COLUMN_BIND_PRODUCT_TYPE = "bind_product_TYPE";

    /**
     * device screen display
     */
    public static final String TABLE_DEVICE_SCREEN_DISPLAY = "device_screen_display";

    public static final String COLUMN_ALARM_ID = "alarm_id";

    public static final String COLUMN_DISPLAY_CONTENT = "content";

    /**
     * transport config
     */
    public static final String TABLE_TRANSPORT_CONFIG = "transport_config";

    public static final String COLUMN_PRODUCT_NAME = "product_name";

    public static final String COLUMN_CONFIG_NAME = "config_name";

    public static final String COLUMN_CONFIG_VALUE = "config_value";

    /**
     * organization
     */
    public static final String TABLE_ORGANIZATION = "organization";

    /**
     * tenant
     */
    public static final String TABLE_TENANT = "tenant";

    /**
     * user
     */
    public static final String TABLE_USER = "user";

    /**
     * role
     */
    public static final String TABLE_ROLE = "role";

    /**
     * device_lora
     */
    public static final String TABLE_DEVICE_LORA = "device_lora";

    public static final String COLUMN_UP_DATA = "up_data";

    public static final String COLUMN_DEVICE_SERIAL = "device_serial";

    public static final String COLUMN_MOTE_EUI = "mote_eui";

    public static final String COLUMN_GW_EUI = "gw_eui";
}
