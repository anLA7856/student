package csust.student.database;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import csust.student.info.ChatMessage;
import csust.student.info.MessageItem;
/**
 * 操作聊天消息的数据库。
 * @author U-ANLA
 *
 */
public class MessageDB {
    public static final String MSG_DBNAME = "message.db";
    private SQLiteDatabase db;

    public MessageDB(Context context) {
        db = context.openOrCreateDatabase(MSG_DBNAME, Context.MODE_PRIVATE,
                null);
    }

    /**
     * 保存到数据库，并且是以特定“_id”样子表名称来保存。
     * @param id
     * @param entity
     */
    public void saveMsg(String id, ChatMessage entity) {
        db.execSQL("CREATE table IF NOT EXISTS _"
                + id
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,sender_id INTEGER,receiver_id INTEGER,chat_time TEXT,is_come TEXT, message TEXT,not_read INTERER)");
        db.execSQL(
                "insert into _"
                        + id
                        + " (sender_id,receiver_id,chat_time,message,not_read,is_come) values(?,?,?,?,?,?)",
                new Object[] { entity.getSenderId(), entity.getReceiveId(),
                        entity.getChatTime(), entity.getMessage(), entity.getNotRead(),entity.getIsCome()});
    }

    /**
     * 得到聊天数据，_id表名。
     * @param id
     * @param pager
     * @return
     */
    public List<ChatMessage> getMsg(String id, int pager) {
        List<ChatMessage> list = new LinkedList<ChatMessage>();
        int num = 10 * (pager + 1);// 本来是准备做滚动到顶端自动加载数据
        db.execSQL("CREATE table IF NOT EXISTS _"
                + id
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,sender_id INTEGER,receiver_id INTEGER,chat_time TEXT,is_come TEXT, message TEXT,not_read INTERER)");
        Cursor c = db.rawQuery("SELECT * from _" + id
                + " ORDER BY _id DESC LIMIT " + num, null);
        while (c.moveToNext()) {
        	ChatMessage cm = new ChatMessage();
        	cm.setId(c.getInt(c.getColumnIndex("_id")));
        	cm.setSenderId(c.getInt(c.getColumnIndex("sender_id")));
        	cm.setReceiveId(c.getInt(c.getColumnIndex("receive_id")));
        	cm.setChatTime(c.getString(c.getColumnIndex("chat_time")));
        	cm.setNotRead(c.getInt(c.getColumnIndex("not_read")));
        	cm.setIsCome(c.getString(c.getColumnIndex("is_come")));
        	cm.setMessage(c.getString(c.getColumnIndex("message")));
        	
           
            list.add(cm);
        }
        c.close();
        Collections.reverse(list);// 前后反转一下消息记录
        return list;
    }

    /**
     * 得到新数据的条数。
     * @param id
     * @return
     */
    public int getNewCount(String id) {
        db.execSQL("CREATE table IF NOT EXISTS _"
                + id
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,sender_id INTEGER,receiver_id INTEGER,chat_time TEXT,is_come TEXT, message TEXT,not_read INTERER)");
        Cursor c = db.rawQuery("SELECT not_read from _" + id + " where not_read=1",
                null);
        int count = c.getCount();
        // L.i("new message num = " + count);
        c.close();
        return count;
    }

    /**
     * 将已经得到的新消息都设为已读。
     * @param id
     */
    public void clearNewCount(String id) {
        db.execSQL("CREATE table IF NOT EXISTS _"
                + id
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,sender_id INTEGER,receiver_id INTEGER,chat_time TEXT,is_come TEXT, message TEXT,not_read INTERER)");
        db.execSQL("update _" + id + " set not_read=0 where not_read=1");
    }

    public void close() {
        if (db != null)
            db.close();
    }
    

    
    
}
