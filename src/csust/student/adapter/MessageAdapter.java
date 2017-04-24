package csust.student.adapter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import csust.student.activity.R;
import csust.student.info.MessageItem;
import csust.student.myview.GifTextView;
import csust.student.utils.SharePreferenceUtil;
import csust.student.utils.TimeUtil;

/**
 * @desc发送消息的adapter
 * @date: 2015年7月3日 下午4:40:54 QQ2050542273
 * @email:15162925211@163.com
 */
@SuppressLint("NewApi")
public class MessageAdapter extends BaseAdapter {

	public static final Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");
	public static final int MESSAGE_TYPE_INVALID = -1;
	public static final int MESSAGE_TYPE_MINE_TETX = 0x00;
	public static final int MESSAGE_TYPE_MINE_IMAGE = 0x01;
	public static final int MESSAGE_TYPE_MINE_AUDIO = 0x02;
	public static final int MESSAGE_TYPE_OTHER_TEXT = 0x03;
	public static final int MESSAGE_TYPE_OTHER_IMAGE = 0x04;
	public static final int MESSAGE_TYPE_OTHER_AUDIO = 0x05;
	public static final int MESSAGE_TYPE_TIME_TITLE = 0x07;
	public static final int MESSAGE_TYPE_HISTORY_DIVIDER = 0x08;
	private static final int VIEW_TYPE_COUNT = 9;

	private Context mContext;
	private LayoutInflater mInflater;
	private List<MessageItem> mMsgList;
	private SharePreferenceUtil mSpUtil;

	private long mPreDate;


	public MessageAdapter(Context context, List<MessageItem> msgList) {
		this.mContext = context;
		mMsgList = msgList;
		mInflater = LayoutInflater.from(context);
		//mSpUtil = PushApplication.getInstance().getSpUtil();
	}

	public void removeHeadMsg() {
		if (mMsgList.size() - 10 > 10) {
			for (int i = 0; i < 10; i++) {
				mMsgList.remove(i);
			}
			notifyDataSetChanged();
		}
	}

	public void setmMsgList(List<MessageItem> msgList) {
		mMsgList = msgList;
		notifyDataSetChanged();
	}

	public void upDateMsg(MessageItem msg) {
		mMsgList.add(msg);
		notifyDataSetChanged();
	}

	public void upDateMsgByList(List<MessageItem> list) {
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				mMsgList.add(list.get(i));
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mMsgList.size();
	}

	@Override
	public Object getItem(int position) {
		return mMsgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		int type = getItemViewType(position);
		MessageHolderBase holder = null;
		if (null == convertView && null != mInflater) {
			holder = new MessageHolderBase();
			switch (type) {
			case MESSAGE_TYPE_MINE_TETX: {
				convertView = mInflater.inflate(
						R.layout.chat_mine_text_message_item, parent, false);
				holder = new TextMessageHolder();
				convertView.setTag(holder);
				fillTextMessageHolder((TextMessageHolder) holder, convertView);
				break;
			}
		
			case MESSAGE_TYPE_OTHER_TEXT: {
				convertView = mInflater
						.inflate(R.layout.chat_other_text_message_item,
								parent, false);
				holder = new TextMessageHolder();
				convertView.setTag(holder);
				fillTextMessageHolder((TextMessageHolder) holder, convertView);
				break;
			}

			default:
				break;
			}
		} else {
			holder = (MessageHolderBase) convertView.getTag();
		}

		final MessageItem mItem = mMsgList.get(position);
		if (mItem != null) {
			int msgType = mItem.getMsgType();
			if (msgType == MessageItem.MESSAGE_TYPE_TEXT) {
				handleTextMessage((TextMessageHolder) holder, mItem, parent);

			}
		}

		return convertView;
	}

	private void handleTextMessage(final TextMessageHolder holder,
			final MessageItem mItem, final View parent) {
		handleBaseMessage(holder, mItem);

		// 文字
		holder.msg.insertGif(convertNormalStringToSpannableString(mItem
				.getMessage() + " "));

	}



	private void handleBaseMessage(MessageHolderBase holder,
			final MessageItem mItem) {
		holder.time.setText(TimeUtil.getChatTime(mItem.getDate()));
		holder.time.setVisibility(View.VISIBLE);
//		holder.head.setBackgroundResource(PushApplication.heads[mItem
//				.getHeadImg()]);

		holder.progressBar.setVisibility(View.GONE);
		holder.progressBar.setProgress(50);

		holder.time.setVisibility(View.VISIBLE);
		//


	}

	private void fillBaseMessageholder(MessageHolderBase holder,
			View convertView) {
		holder.head = (ImageView) convertView.findViewById(R.id.icon);
		holder.time = (TextView) convertView.findViewById(R.id.datetime);
		// holder.msg = (GifTextView) convertView.findViewById(R.id.textView2);
		holder.rlMessage = (RelativeLayout) convertView
				.findViewById(R.id.relativeLayout1);
		// holder.ivphoto = (ImageView) convertView
		// .findViewById(R.id.iv_chart_item_photo);
		holder.progressBar = (ProgressBar) convertView
				.findViewById(R.id.progressBar1);
		// holder.voiceTime = (TextView) convertView
		// .findViewById(R.id.tv_voice_time);
		holder.flPickLayout = (FrameLayout) convertView
				.findViewById(R.id.message_layout);
	}

	private void fillTextMessageHolder(TextMessageHolder holder,
			View convertView) {
		fillBaseMessageholder(holder, convertView);
		holder.msg = (GifTextView) convertView.findViewById(R.id.textView2);
	}





	private static class MessageHolderBase {
		ImageView head;
		TextView time;
		ImageView imageView;
		ProgressBar progressBar;
		RelativeLayout rlMessage;
		FrameLayout flPickLayout;
	}

	private static class TextMessageHolder extends MessageHolderBase {
		/**
		 * 文字消息体
		 */
		GifTextView msg;
	}





	/**
	 * 另外一种方法解析表情将[表情]换成fxxx
	 * 
	 * @param message
	 *            传入的需要处理的String
	 * @return
	 */
	private String convertNormalStringToSpannableString(String message) {
		String hackTxt;
		if (message.startsWith("[") && message.endsWith("]")) {
			hackTxt = message + " ";
		} else {
			hackTxt = message;
		}

		Matcher localMatcher = EMOTION_URL.matcher(hackTxt);
		while (localMatcher.find()) {
			String str2 = localMatcher.group(0);
//			if (PushApplication.getInstance().getFaceMap().containsKey(str2)) {
//				String faceName = mContext.getResources().getString(
//						PushApplication.getInstance().getFaceMap().get(str2));
//				CharSequence name = options(faceName);
//				message = message.replace(str2, name);
//			}

		}
		return message;
	}

	/**
	 * 取名字f010
	 * 
	 * @param faceName
	 */
	private CharSequence options(String faceName) {
		int start = faceName.lastIndexOf("/");
		CharSequence c = faceName.subSequence(start + 1, faceName.length() - 4);
		return c;
	}

	static class ViewHolder {

		ImageView head;
		TextView time;
		GifTextView msg;
		ImageView imageView;
		ProgressBar progressBar;
		TextView voiceTime;
		ImageView ivphoto;
		RelativeLayout rlMessage;
		FrameLayout flPickLayout;
	}

	@Override
	public int getItemViewType(int position) {
		// logger.d("chat#getItemViewType -> position:%d", position);
		try {
			if (position >= mMsgList.size()) {
				return MESSAGE_TYPE_INVALID;
			}

			MessageItem item = mMsgList.get(position);
			if (item != null) {
				boolean comMeg = item.isComMeg();
				int type = item.getMsgType();
				if (comMeg) {
					// 接受的消息
					switch (type) {
					case MessageItem.MESSAGE_TYPE_TEXT: {
						return MESSAGE_TYPE_OTHER_TEXT;
					}

					default:
						break;
					}
				} else {
					// 发送的消息
					switch (type) {
					case MessageItem.MESSAGE_TYPE_TEXT: {
						return MESSAGE_TYPE_MINE_TETX;

					}

					default:
						break;
					}
				}
			}
			return MESSAGE_TYPE_INVALID;
		} catch (Exception e) {
			Log.e("fff", e.getMessage());
			return MESSAGE_TYPE_INVALID;
		}
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT;
	}

}