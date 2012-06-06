package com.jtxdriggers.android.ventriloid;

import java.util.ArrayList;

public class ItemData {

	private ArrayList<Item.Channel> channels = new ArrayList<Item.Channel>();
	private Item.Channel currentChannel; 
	private ArrayList<ArrayList<Item.User>> users = new ArrayList<ArrayList<Item.User>>();
	private ArrayList<Item.User> currentUsers = new ArrayList<Item.User>(); 
	
	public ItemData() {
		Item i = new Item();
		channels.add(i.new Channel());
		currentChannel = i.new Channel();
		users.add(new ArrayList<Item.User>());
	}
	
	public void addChannel(Item.Channel channel) {
		for (int i = 0; i < channels.size(); i++) {
			if (channels.get(i).id == channel.parent) {
				channel.indent = channels.get(i).indent + "     ";
				i++;
				
				while (i < channels.size() && channels.get(i).parent == channel.parent)
					i++;
				
				channels.add(i, channel);
				users.add(i, new ArrayList<Item.User>());
				return;
			}
		}
	}
	
	public void addCurrentUser(Item.User user) {
		if (user.parent == VentriloInterface.getuserchannel(VentriloInterface.getuserid())) {
			user.indent = "     ";
			currentUsers.add(user);
		}
	}
	
	public void addUser(Item.User user) {
		for (int i = 0; i < channels.size(); i++) {
			if (channels.get(i).id == user.parent) {
				user.indent = channels.get(i).indent + "     ";
				users.get(i).add(user);
				return;
			}
		}
	}
	
	public ArrayList<Item.Channel> getChannels() {
		return channels;
	}
	
	public ArrayList<Item.Channel> getCurrentChannel() {
		ArrayList<Item.Channel> c = new ArrayList<Item.Channel>();
		c.add(currentChannel);
		return c;
	}
	
	public Item.User getCurrentUserById(short id) {
		for (int i = 0; i < currentUsers.size(); i++) {
			if (currentUsers.get(i).id == id)
				return currentUsers.get(i);
		}
		return null;
	}
	
	public ArrayList<ArrayList<Item.User>> getCurrentUsers() {
		ArrayList<ArrayList<Item.User>> u = new ArrayList<ArrayList<Item.User>>();
		u.add(new ArrayList<Item.User>());
		for (int i = 0; i < currentUsers.size(); i++) {
			u.get(0).add(currentUsers.get(i));
		}
		return u;
	}
	
	public ArrayList<ArrayList<Item.User>> getUsers() {
		return users;
	}
	
	public Item.User getUserById(short id) {
		for (int i = 0; i < channels.size(); i++) {
			for (int j = 0; j < users.get(i).size(); j++) {
				if (users.get(i).get(j).id == id)
					return users.get(i).get(j);
			}
		}
		return null;
	}
	
	public void removeCurrentUser(short id) {
		for (int i = 0; i < currentUsers.size(); i++) {
			if (currentUsers.get(i).id == id) {
				currentUsers.remove(i);
				return;
			}
		}
	}
	
	public void removeUser(short id) {
		for (int i = 0; i < channels.size(); i++) {
			for (int j = 0; j < users.get(i).size(); j++) {
				if (users.get(i).get(j).id == id) {
					users.get(i).remove(j);
					return;
				}
			}
		}
	}
	
	public void setCurrentChannel(short id) {
		currentUsers.clear();
		for (int i = 0; i < channels.size(); i++) {
			if (channels.get(i).id == id) {
				currentChannel = channels.get(i);
				
				for (int j = 0; j < users.get(i).size(); j++) {
					currentUsers.add(users.get(i).get(j));
				}
				return;
			}
		}
	}
	
	public void setLobby(Item.Channel lobby) {
		channels.set(0, lobby);
		currentChannel = lobby;
	}
	
	public void setXmit(short id, int xmit) {
		Item.User u = getUserById(id);
		u.xmit = xmit;
		if (u.parent == VentriloInterface.getuserchannel(VentriloInterface.getuserid()))
			getCurrentUserById(id).xmit = xmit;
	}
	
}
