package com.rti.edge.analytics;

import org.apache.edgent.providers.direct.DirectProvider;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;

import com.rti.edge.edgent.DDSSubscriber;
import com.rti.edge.edgent.DDSSubscriberConnector;
import com.rti.edge.types.reading.Reading;
import com.rti.edge.types.reading.ReadingTypeSupport;

public class ReadingsReceiver {
	public static void main(String args[]){
		DirectProvider dp = new DirectProvider();
		Topology top = dp.newTopology();
		DDSSubscriberConnector<Reading> readingSubscriber = null;
		try {
			readingSubscriber = new DDSSubscriberConnector<Reading>("Readings", ReadingTypeSupport.get_instance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		DDSSubscriber<Reading> consumer = new DDSSubscriber<Reading>(top, readingSubscriber);
		TStream<Reading> readings = consumer.events();
		readings.print();
		dp.submit(top);
	}

}
