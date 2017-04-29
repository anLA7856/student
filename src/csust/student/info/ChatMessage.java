package csust.student.info;

import java.sql.Date;

/**
 * 聊天实体类。
 * 
 * @author U-ANLA
 *
 */
public class ChatMessage {
	private Integer id;
	private Integer senderId;
	private Integer receiveId;
	private String message;
	private String chatTime;
	private Integer notRead;
	private String isCome;
	private String name;
	private String headPic;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeadPic() {
		return headPic;
	}

	public void setHeadPic(String headPic) {
		this.headPic = headPic;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSenderId() {
		return senderId;
	}

	public void setSenderId(Integer senderId) {
		this.senderId = senderId;
	}

	public Integer getReceiveId() {
		return receiveId;
	}

	public void setReceiveId(Integer receiveId) {
		this.receiveId = receiveId;
	}

	public String getChatTime() {
		return chatTime;
	}

	public void setChatTime(String chatTime) {
		this.chatTime = chatTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getNotRead() {
		return notRead;
	}

	public void setNotRead(Integer notRead) {
		this.notRead = notRead;
	}

	public String getIsCome() {
		return isCome;
	}

	public void setIsCome(String isCome) {
		this.isCome = isCome;
	}

	@Override
	public String toString() {
		return "ChatMessage [id=" + id + ", senderId=" + senderId
				+ ", receiveId=" + receiveId + ", message=" + message
				+ ", chatTime=" + chatTime + ", notRead=" + notRead
				+ ", isCome=" + isCome + ", name=" + name + ", headPic="
				+ headPic + "]";
	}

	public ChatMessage(Integer id, Integer senderId, Integer receiveId,
			String message, String chatTime, Integer notRead, String isCome,
			String name, String headPic) {
		super();
		this.id = id;
		this.senderId = senderId;
		this.receiveId = receiveId;
		this.message = message;
		this.chatTime = chatTime;
		this.notRead = notRead;
		this.isCome = isCome;
		this.name = name;
		this.headPic = headPic;
	}

	public ChatMessage() {
		super();
	}
	
	
	

}
