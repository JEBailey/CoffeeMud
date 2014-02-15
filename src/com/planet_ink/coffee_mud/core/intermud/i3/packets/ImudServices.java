package com.planet_ink.coffee_mud.core.intermud.i3.packets;
import com.planet_ink.coffee_mud.Libraries.interfaces.ChannelsLibrary.CMChannel;
/**
 * com.planet_ink.coffee_mud.core.intermud.i3.packets.ImudServices
 * Copyright (c) 1996 George Reese
 * This source code may not be modified, copied,
 * redistributed, or used in any fashion without the
 * express written consent of George Reese.
 * 
 * The interface for a intermud services daemon
 */

/**
 * This interface prescribes methods that need to
 * be implemented by a class in the mudlib.  These
 * methods do mudlib specific handling of intermud
 * packets as well as provide the Imaginary Intermud 3
 * System with mudlib specific information.
 * @author George Reese (borg@imaginary.com)
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
public interface ImudServices {
	/**
	 * Handles an incoming I3 packet asynchronously.
	 * An implementation should make sure that asynchronously
	 * processing the incoming packet will not have any
	 * impact, otherwise you could end up with bizarre
	 * behaviour like an intermud chat line appearing
	 * in the middle of a room description.  If your
	 * mudlib is not prepared to handle multiple threads,
	 * just stack up incoming packets and pull them off
	 * the stack during your main thread of execution.
	 * @param packet the incoming packet
	 */
	public abstract void receive(Packet packet);

	/**
	 * @return an enumeration of channels this mud subscribes to
	 */
	public abstract java.util.Enumeration getChannels();

	/**
	 * Given a I3 channel name, this method should provide
	 * the local name for that channel.
	 * Example:
	 * <PRE>
	 * if( str.equals("imud_code") ) return "intercre";
	 * </PRE>
	 * @param str the remote name of the desired channel
	 * @return the local channel name for a remote channel
	 * @see #getRemoteChannel
	 */
	public abstract String getLocalChannel(String str);

	/**
	 * @return the software name and version
	 */
	public abstract String getMudVersion();

	/**
	 * @return the name of this mud
	 */
	public abstract String getMudName();
	
	/**
	 * Returns the mask of a remote channel
	 * @param str the remote channel
	 * @return the mask;
	 */
	public String getRemoteMask(String str);
	
	/**
	 * Add a new channel
	 * @param chan the channel to add
	 * @return true if no conflicts
	 */
	public boolean addChannel(CMChannel chan);
	
	/**
	 * Remove a channel
	 * @param remoteChannelName the i3 channel to remove
	 * @return true if remove worked
	 */
	public boolean delChannel(String remoteChannelName);
	
	/**
	 * @return the status of this mud
	 */
	public abstract String getMudState();
	/**
	 * @return the player port for this mud
	 */
	public abstract int getMudPort();

	/**
	 * @return the last packet received time
	 */
	public abstract long getLastPacketReceivedTime();

	/**
	 * Given a local channel name, returns the remote
	 * channel name.
	 * Example:
	 * <PRE>
	 * if( str.equals("intercre") ) return "imud_code";
	 * </PRE>
	 * @param str the local name of the desired channel
	 * @return the remote name of the specified local channel
	 */
	public abstract String getRemoteChannel(String str);
}
