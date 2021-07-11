import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import 	android.content.ContentValues
import android.database.Cursor
import android.database.SQLException
import android.util.Log
import com.example.myapplication.MyStudyLifeTasksProperty
import com.example.myapplication.TableEnteryItem

//creating the database logic, extending the SQLiteOpenHelper base class
class DatabaseHandler(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 3
        private val DATABASE_NAME = "TasksDatabase"

        private val TABLE_TASKS = "TasksTable"
        private val TABLE_MSL_TASKS = "MslTasksTable"
        private val TABLE_TOKEN = "TokenTable"

        private val KEY_TOKEN = "token"

        // Table key`s
        private val KEY_GUID = "guid"
        private val KEY_UPDATED_TIME = "updated_at"
        private val KEY_IMAGE_RESOURCE = "address_of_round"
        private val KEY_SUBJECT_NAME = "some_subject_name"
        private val KEY_TASK_TEXT = "text"
        private val KEY_DATE_TO_DO_TEXT = "due_time_text"
        private val KEY_DUE_DATE = "due_time"
        private val KEY_PERCENT_OF_DONE = "percent"
        private val KEY_IM_VEIW_NEAR_RECENT = "address_of_image"
        private val KEY_PERCENT_OF_DONE_TEXT_COLOR = "color"

        // Msl table key`s
        // KEY_GUID a a primary key
        // KEY_UPDATED_TIME a text
        // KEY_DUE_DATE a text
        private val KEY_COMPLETED_AT = "completed_at"
        private val KEY_CREATED_AT = "created_at"
        private val KEY_DELETED_AT = "deleted_at"
        private val KEY_DETAIL = "detail"
        private val KEY_EXAM_GUID = "exam_guid"
        private val KEY_PROGRESS = "progress"
        private val KEY_SCHOOL_ID = "school_id"
        private val KEY_TIMESTAMP = "timestamp"
        private val KEY_SUBJECT_GUID = "subject_guid"
        private val KEY_TITLE = "title"
        private val KEY_TYPE = "type"
        private val KEY_USER_ID = "user_id"


    }

    override fun onCreate(db: SQLiteDatabase) {
        //creating table with fields
        val CREATE_TASKS_TABLE = ("CREATE TABLE " + TABLE_TASKS + "("
                + KEY_GUID + " TEXT PRIMARY KEY," + KEY_UPDATED_TIME + " TEXT,"
                + KEY_IMAGE_RESOURCE + " INTEGER," + KEY_PERCENT_OF_DONE_TEXT_COLOR + " INTEGER,"
                + KEY_IM_VEIW_NEAR_RECENT + " INTEGER," + KEY_SUBJECT_NAME + " TEXT,"
                + KEY_TASK_TEXT + " TEXT," + KEY_DATE_TO_DO_TEXT + " TEXT," + KEY_DUE_DATE + " TEXT,"
                + KEY_PERCENT_OF_DONE + " TEXT" + ")")

        val CREATE_MSL_TASKS_TABLE = ("CREATE TABLE " + TABLE_MSL_TASKS + "("
                + KEY_GUID + " TEXT PRIMARY KEY," + KEY_COMPLETED_AT + " TEXT,"
                + KEY_CREATED_AT + " TEXT," + KEY_DELETED_AT + " TEXT," + KEY_UPDATED_TIME + " TEXT,"
                + KEY_DETAIL + " TEXT," + KEY_DUE_DATE + " TEXT," + KEY_EXAM_GUID + " TEXT,"
                + KEY_PROGRESS + " INTEGER," + KEY_SCHOOL_ID + " TEXT," + KEY_SUBJECT_GUID + " TEXT,"
                + KEY_TITLE + " TEXT," + KEY_TYPE + " TEXT," + KEY_USER_ID + " INTEGER,"
                + KEY_TIMESTAMP + " REAL" + ")")

        val CREATE_TOKEN_TABLE = ("CREATE TABLE " + TABLE_TOKEN + "("
                + "id" + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TOKEN + " TEXT" + ")")

        db.execSQL(CREATE_MSL_TASKS_TABLE)
        db.execSQL(CREATE_TASKS_TABLE)
        db.execSQL(CREATE_TOKEN_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MSL_TASKS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TOKEN")
        onCreate(db)
    }

    fun clearAllTask() {
        val db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MSL_TASKS")
        onCreate(db)
    }

    fun addTask(item: TableEnteryItem): Long {
        val db = this.writableDatabase

        val taskTableContentValues = ContentValues()

        // task table
        taskTableContentValues.put(KEY_GUID, item.GUID)
        taskTableContentValues.put(KEY_UPDATED_TIME, item.UpdatedTime)
        taskTableContentValues.put(KEY_IMAGE_RESOURCE, item.imageResource)
        taskTableContentValues.put(KEY_SUBJECT_NAME, item.Subject_name)
        taskTableContentValues.put(KEY_TASK_TEXT, item.Task_text)
        taskTableContentValues.put(KEY_DATE_TO_DO_TEXT, item.Date_to_do)
        taskTableContentValues.put(KEY_DUE_DATE, item.__dateForOrderBy)
        taskTableContentValues.put(KEY_PERCENT_OF_DONE, item.Percent_of_done)
        taskTableContentValues.put(KEY_PERCENT_OF_DONE_TEXT_COLOR, item.Percent_of_done_text_color)
        taskTableContentValues.put(KEY_IM_VEIW_NEAR_RECENT, item.ImViewNearPercent)

        val success = db.insert(TABLE_TASKS, null, taskTableContentValues)
        Log.e("Debug", "Adding Item database status $success")
        db.close()
        return success
    }

    fun addMslTask(item: MyStudyLifeTasksProperty): Long {
        val db = this.writableDatabase

        val mslTaskTableContentValues = ContentValues()

        // msl task table
        mslTaskTableContentValues.put(KEY_GUID, item.guid)
        mslTaskTableContentValues.put(KEY_COMPLETED_AT, item.completed_at)
        mslTaskTableContentValues.put(KEY_UPDATED_TIME, item.updated_at)
        mslTaskTableContentValues.put(KEY_CREATED_AT, item.created_at)
        mslTaskTableContentValues.put(KEY_DELETED_AT, item.deleted_at)
        mslTaskTableContentValues.put(KEY_DETAIL, item.detail)
        mslTaskTableContentValues.put(KEY_DUE_DATE, item.due_date)
        mslTaskTableContentValues.put(KEY_PROGRESS, item.progress)
        mslTaskTableContentValues.put(KEY_EXAM_GUID, item.exam_guid)
        mslTaskTableContentValues.put(KEY_SCHOOL_ID, item.school_id)
        mslTaskTableContentValues.put(KEY_TIMESTAMP, item.timestamp)
        mslTaskTableContentValues.put(KEY_SUBJECT_GUID, item.subject_guid)
        mslTaskTableContentValues.put(KEY_TITLE, item.title)
        mslTaskTableContentValues.put(KEY_TYPE, item.type)
        mslTaskTableContentValues.put(KEY_USER_ID, item.user_id)

        val success = db.insert(TABLE_MSL_TASKS, null, mslTaskTableContentValues)
        Log.e("Debug", "Adding Item database status $success")
        db.close()
        return success
    }

    fun viewTasks(): ArrayList<TableEnteryItem> {

        val taskList: ArrayList<TableEnteryItem> = ArrayList<TableEnteryItem>()

        val selectQuery = "SELECT * FROM $TABLE_TASKS ORDER BY $KEY_DUE_DATE, $KEY_GUID"

        val db = this.readableDatabase
        var cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (err: SQLException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var imageResource: Int
        var Subject_name: String
        var Task_text: String
        var Date_to_do_text: String
        var Percent_of_done: String
        var Percent_of_done_text_color: Int
        var ImViewNearPercent: Int
        var GUID: String
        var UpdatedTime: String
        var Due_date: String?

        if (cursor.moveToFirst()) {
            do {
                imageResource = cursor.getInt(cursor.getColumnIndex(KEY_IMAGE_RESOURCE))
                Subject_name = cursor.getString(cursor.getColumnIndex(KEY_SUBJECT_NAME))
                Task_text = cursor.getString(cursor.getColumnIndex(KEY_TASK_TEXT))
                Date_to_do_text = cursor.getString(cursor.getColumnIndex(KEY_DATE_TO_DO_TEXT))
                Percent_of_done = cursor.getString(cursor.getColumnIndex(KEY_PERCENT_OF_DONE))
                Percent_of_done_text_color = cursor.getInt(cursor.getColumnIndex(KEY_PERCENT_OF_DONE_TEXT_COLOR))
                ImViewNearPercent = cursor.getInt(cursor.getColumnIndex(KEY_IM_VEIW_NEAR_RECENT))
                GUID = cursor.getString(cursor.getColumnIndex(KEY_GUID))
                UpdatedTime = cursor.getString(cursor.getColumnIndex(KEY_UPDATED_TIME))
                Due_date = cursor.getString(cursor.getColumnIndex(KEY_DUE_DATE))


                val item = TableEnteryItem(imageResource=imageResource, Subject_name=Subject_name,
                        Task_text=Task_text, Date_to_do=Date_to_do_text, Percent_of_done=Percent_of_done,
                        Percent_of_done_text_color=Percent_of_done_text_color,
                        ImViewNearPercent=ImViewNearPercent, GUID=GUID, UpdatedTime=UpdatedTime,
                        __dateForOrderBy=Due_date)
                taskList.add(item)
            } while (cursor.moveToNext())
        }
        //return ArrayList(taskList.sortedByDescending { it.UpdatedTime })
        db.close()
        return taskList

    }

    fun viewMslTasks(): ArrayList<MyStudyLifeTasksProperty> {

        val mslTaskList: ArrayList<MyStudyLifeTasksProperty> = ArrayList<MyStudyLifeTasksProperty>()

        val selectQuery = "SELECT * FROM $TABLE_MSL_TASKS ORDER BY $KEY_DUE_DATE, $KEY_GUID"

        val db = this.readableDatabase
        var cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (err: SQLException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var completed_at: String?
        var created_at: String
        var deleted_at: String?
        var detail: String?
        var due_date: String?
        var exam_guid: String?
        var guid: String
        var progress: Int
        var school_id: String?
        var timestamp: Float
        var subject_guid: String
        var title: String
        var type: String
        var updated_at: String?
        var user_id: Int

        if (cursor.moveToFirst()) {
            do {
                completed_at = cursor.getString(cursor.getColumnIndex(KEY_COMPLETED_AT))
                created_at = cursor.getString(cursor.getColumnIndex(KEY_CREATED_AT))
                deleted_at = cursor.getString(cursor.getColumnIndex(KEY_DELETED_AT))
                detail = cursor.getString(cursor.getColumnIndex(KEY_DETAIL))
                due_date = cursor.getString(cursor.getColumnIndex(KEY_DUE_DATE))
                exam_guid = cursor.getString(cursor.getColumnIndex(KEY_EXAM_GUID))
                progress = cursor.getInt(cursor.getColumnIndex(KEY_PROGRESS))
                guid = cursor.getString(cursor.getColumnIndex(KEY_GUID))
                school_id = cursor.getString(cursor.getColumnIndex(KEY_SCHOOL_ID))
                timestamp = cursor.getFloat(cursor.getColumnIndex(KEY_TIMESTAMP))
                subject_guid = cursor.getString(cursor.getColumnIndex(KEY_SUBJECT_GUID))
                title = cursor.getString(cursor.getColumnIndex(KEY_TITLE))
                type = cursor.getString(cursor.getColumnIndex(KEY_TYPE))
                updated_at = cursor.getString(cursor.getColumnIndex(KEY_UPDATED_TIME))
                user_id = cursor.getInt(cursor.getColumnIndex(KEY_USER_ID))

                val item = MyStudyLifeTasksProperty(completed_at=completed_at, created_at=created_at,
                        deleted_at=deleted_at, detail=detail, due_date=due_date, exam_guid=exam_guid,
                        progress=progress, guid=guid, school_id=school_id, subject_guid=subject_guid,
                        timestamp=timestamp, title=title, type=type, updated_at=updated_at, user_id=user_id)
                mslTaskList.add(item)
            } while (cursor.moveToNext())
        }
        //return ArrayList(mslTaskList.sortedWith(compareBy { it.due_date }))
        db.close()
        return mslTaskList
    }

    fun viewToken(): String {

        val selectQuery = "SELECT * FROM $TABLE_TOKEN LIMIT 1"

        val db = this.readableDatabase
        var cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (err: SQLException) {
            db.execSQL(selectQuery)
            return ""
        }

        var token = ""

        if (cursor.moveToFirst()) {
            token = cursor.getString(cursor.getColumnIndex(KEY_TOKEN))
        }

        db.close()
        return token
    }

    fun updateToken(token: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(KEY_TOKEN, token)

        // delete entire table
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_TOKEN")

        // create again
        val CREATE_TOKEN_TABLE = ("CREATE TABLE " + TABLE_TOKEN + "("
                + "id" + " INT PRIMARY KEY," + KEY_TOKEN + " TEXT" + ")")
        db.execSQL(CREATE_TOKEN_TABLE)

        // add token
        val success = db.insert(TABLE_TOKEN, null, contentValues)
        Log.e("Debug", "Updating token status - $success")
        //db.close()
        return success
    }

    fun updateTask(item: TableEnteryItem): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_GUID, item.GUID)
        contentValues.put(KEY_UPDATED_TIME, item.UpdatedTime)
        contentValues.put(KEY_IMAGE_RESOURCE, item.imageResource)
        contentValues.put(KEY_SUBJECT_NAME, item.Subject_name)
        contentValues.put(KEY_TASK_TEXT, item.Task_text)
        contentValues.put(KEY_DATE_TO_DO_TEXT, item.Date_to_do)
        contentValues.put(KEY_PERCENT_OF_DONE, item.Percent_of_done)
        contentValues.put(KEY_PERCENT_OF_DONE_TEXT_COLOR, item.Percent_of_done_text_color)
        contentValues.put(KEY_IM_VEIW_NEAR_RECENT, item.ImViewNearPercent)

        val success = db.update(TABLE_TASKS, contentValues, KEY_GUID + "=" + "'" + item.GUID + "'", null)
        Log.e("Debug", "Updating database status $success")
        db.close()
        return success
    }

    fun deleteTask(item: TableEnteryItem): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_GUID, item.GUID)
        Log.e("Debug", KEY_GUID + "=" + item.GUID)
        val success = db.delete(TABLE_TASKS, KEY_GUID + "=" + "'" +  item.GUID + "'", null)

        db.close()
        return success
    }

    fun deleteMslTask(item: MyStudyLifeTasksProperty): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_GUID, item.guid)
        Log.e("Debug", KEY_GUID + "=" + item.guid)
        val success = db.delete(TABLE_MSL_TASKS, KEY_GUID + "=" + "'" +  item.guid + "'", null)

        db.close()
        return success
    }

}

