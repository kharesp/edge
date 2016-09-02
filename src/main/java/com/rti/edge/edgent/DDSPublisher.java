package com.rti.edge.edgent;

import org.apache.edgent.topology.TSink;
import org.apache.edgent.topology.TStream;

public class DDSPublisher<T> {
	private DDSPublisherConnector<T> connector;
	public DDSPublisher(DDSPublisherConnector<T> connector){
		this.connector=connector;
	}
	public TSink<T> publish(TStream<T> stream){
		return stream.sink(connector);
	}
}
