package org.echoes.backend;

public class MessageDoc extends LuceneDoc {

	public static final String TYPE = "MessageDoc";
	
	public MessageDoc(long id, long from, long to, long created_time,
			String message) {
		super(id, from, to, created_time, message);
		this.type = MessageDoc.TYPE;
	}

}
