
package org.echoes.backend;

/**
 * @author amin
 * This Superclass should not be instantiated. Known subclasses: MessageDoc or PostDoc.
 * Use should follow these example formats:
 * LuceneDoc ld = new PostDoc(args)
 * Determine doc type by:  ld.type.equals(PostDoc.TYPE)
 * Cast to correct subclass as follows: PostDoc pd = (PostDoc) ld
 */
public class LuceneDoc {
	protected long id, from, to, created_time;
	protected String message, type;
	
	public static final String ID ="id", FROM="from", TO="to", CREATED_TIME = "created_time", MESSAGE="message", TYPE = "LuceneDoc";
	
	public LuceneDoc(long id, long from, long to, long created_time,
			String message) {
		this.id = id;
		this.from = from;
		this.to = to;
		this.created_time = created_time;
		this.message = message;
		this.type= LuceneDoc.TYPE;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFrom() {
		return from;
	}

	public void setFrom(long from) {
		this.from = from;
	}

	public long getTo() {
		return to;
	}

	public void setTo(long to) {
		this.to = to;
	}

	public long getCreated_time() {
		return created_time;
	}

	public void setCreated_time(long created_time) {
		this.created_time = created_time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}