package com.rti.edge.analytics;

import java.util.Arrays;
import org.apache.edgent.providers.direct.DirectProvider;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.edge.edgent.DDSSubscriber;
import com.rti.edge.edgent.DDSSubscriberConnector;
import com.rti.edge.types.reading.Reading;
import com.rti.edge.types.reading.ReadingTypeSupport;

public class Processor {
	public static int HOUSE_ID ;

	public static void main(String args[]){
		if(args.length<2){
			System.out.println("args: houseId window_length_secs");
			return;
		}
		HOUSE_ID=Integer.parseInt(args[0]);
		Integer window_length_secs= Integer.parseInt(args[1]);

		System.out.format("Started Processor for house id:%d.\nWill compute average load over %d seconds window size.\n",
				HOUSE_ID,window_length_secs);

		process(window_length_secs);
	}

	public static void process(int window_length_secs)
	{
		DirectProvider dp=new DirectProvider();
		Topology topology= dp.newTopology();
		TStream<Reading> houseStream= houseStream(topology);
		new HouseReadingsProcessor(topology,houseStream,window_length_secs).process();

		dp.submit(topology);
		
	}
	
	private static TStream<Reading> houseStream(Topology topology){
		DDSSubscriberConnector<Reading> readingSubscriber = null;
		try {
			readingSubscriber = new DDSSubscriberConnector<Reading>("Readings", ReadingTypeSupport.get_instance(),
					"house_id=%0",new StringSeq(Arrays.asList(Integer.toString(HOUSE_ID))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		DDSSubscriber<Reading> consumer = new DDSSubscriber<Reading>(topology, readingSubscriber);
		return consumer.events();
	}
}
