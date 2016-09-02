package com.rti.edge.edgent;

import java.util.concurrent.TimeUnit;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;

import com.rti.dds.infrastructure.Copyable;

public  class DDSSubscriber<T extends Copyable> {
	private Topology topology;
	private DDSSubscriberConnector<T> dr;

	public DDSSubscriber(Topology t,DDSSubscriberConnector<T> connector){
		topology=t;
		dr=connector;
	}
	public TStream<T> events(){
		return topology.events(dr);
	}
	public TStream<T> generate(){
		return topology.generate(dr).flatMap(l-> l);
	}
	public TStream<T> poll(long period,TimeUnit unit){
		return topology.poll(()->{
			return dr.take(); 
		},period,unit).flatMap(l -> l);
	}

}
