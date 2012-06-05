package com.jtxdriggers.android.ventriloid;

import java.util.HashMap;

public class Item {
	
	public short id, parent;
	public String name, phonetic, comment;
	public String status = "";
	public String indent = "";
	
	public class Channel extends Item {
		public short parent;
		public boolean reqPassword;
		
		public Channel() {
			this.id = 0;
			this.parent = 0;
			this.name = "Lobby";
			this.phonetic = "Lobby";
			this.comment = "";
			this.reqPassword = false;
		}
		
		public Channel(String name, String phonetic, String comment) {
			this.id = 0;
			this.parent = 0;
			this.name = name;
			this.phonetic = phonetic;
			this.comment = comment;
			this.reqPassword = false;
		}
		
		public Channel(short id, short parent, String name, String phonetic, String comment, boolean reqPassword) {
			this.id = id;
			this.parent = parent;
			this.name = name;
			this.phonetic = phonetic;
			this.comment = comment;
			this.reqPassword = reqPassword;
		}
		
		public HashMap<String, Object> toHashMap() {
			HashMap<String, Object> channel = new HashMap<String, Object>();
			channel.put("id", id);
			channel.put("parent", parent);
			channel.put("name", name);
			channel.put("phonetic", phonetic);
			channel.put("comment", comment);
			channel.put("reqPassword", reqPassword);
			channel.put("indent", indent);
			channel.put("status", status);
			
			return channel;
		}
		
		public String toString() {
			return "Channel ID: " + id + "\n"
				+ "Parent: " + parent + "\n"
				+ "Name: " + name + "\n"
				+ "Phonetic: " + phonetic + "\n"
				+ "Comment: " + comment + "\n"
				+ "Requires Password: " + reqPassword + "\n"
				+ "Indent: |" + indent + "|";
		}
	}
	
	public class User extends Item {
		
		public static final int XMIT_OFF = 0;
		public static final int XMIT_INIT = 1;
		public static final int XMIT_ON = 2;
		
		public String rank, url, integration;
		public int xmit = XMIT_OFF;
		
		public User(short id, short parent, String name, String phonetic, String rank, String comment, String url, String integration) {
			this.id = id;
			this.parent = parent;
			this.name = name;
			this.phonetic = phonetic;
			this.rank = rank;
			this.comment = comment;
			this.url = url;
			this.integration = integration;
		}
		
		public void setXmit(int xmit) {
			this.xmit = xmit;
		}
		
		private String formatRank(String rank) {
			if (rank.length() > 0)
				return "[" + rank + "] ";
			else
				return "";
		}
		
		public HashMap<String, Object> toHashMap() {
			HashMap<String, Object> user = new HashMap<String, Object>();
			user.put("name", name);
			user.put("rank", formatRank(rank));
			user.put("comment", formatComment(url, comment));
			user.put("integration", integration);
			user.put("indent", indent);
			user.put("status", status);
			user.put("xmit", xmit);
			
			return user;
		}
		
		public String toString() {
			return "User ID: " + id + "\n"
				+ "Parent: " + parent + "\n"
				+ "Name: " + name + "\n"
				+ "Phonetic: " + phonetic + "\n"
				+ "Rank: " + rank + "\n"
				+ "Comment: " + comment + "\n"
				+ "URL: " + url + "\n"
				+ "Integration: " + integration;
		}
	}
	
	private String formatComment(String url, String comment) {
		boolean hasUrl = url.length() > 0;
		boolean hasComment = comment.length() > 0;
		
		if (hasUrl && hasComment)
			return " (U: " + comment + ")";
		else if (hasUrl)
			return " (U:)";
		else if (hasComment)
			return " (" + comment + ")";
		
		return "";
	}

}