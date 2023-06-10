package com.grinder.net.update;

import com.grinder.net.channel.ChannelRequest;
import com.grinder.net.codec.filestore.OnDemandRequest;
import io.netty.channel.Channel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;


/**
 * Dispatches update requests to worker threads.
 *
 * @author Graham
 */
public final class UpdateDispatcher {

	/**
	 * The maximum size of a queue before requests are rejected.
	 */
	private static final int MAXIMUM_QUEUE_SIZE = 1024;

	/**
	 * A queue for pending 'on-demand' requests.
	 */
	private final BlockingQueue<ComparableChannelRequest<OnDemandRequest>> demand = new PriorityBlockingQueue<>();

	/**
	 * Dispatches an 'on-demand' request.
	 *
	 * @param channel The channel.
	 * @param request The request.
	 */
	public void dispatch(Channel channel, OnDemandRequest request) {
		if (demand.size() >= MAXIMUM_QUEUE_SIZE) {
			channel.close();
		}
		demand.add(new ComparableChannelRequest<>(channel, request));
	}

	/**
	 * Gets the next 'on-demand' request from the queue, blocking if none are available.
	 *
	 * @return The 'on-demand' request.
	 * @throws InterruptedException If the thread is interrupted.
	 */
	ChannelRequest<OnDemandRequest> nextOnDemandRequest() throws InterruptedException {
		return demand.take();
	}

}