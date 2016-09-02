package com.rti.edge.edgent;

import java.util.concurrent.TimeUnit;

import org.apache.edgent.providers.direct.DirectProvider;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;

import com.rti.edge.types.reading.Reading;
import com.rti.edge.types.reading.ReadingTypeSupport;

public class Example {
	 public static void main(String args[]){
		if(args.length<1){
			System.out.println("enter mode of operation: pub|sub");
			return;
		}
		if(args[0].equals("sub")) {
			exampleSubscriber();
		}else if(args[0].equals("pub")){
			examplePublisher();
		}else{
			System.out.println("unrecognized argument");
		}
	 }
	 public static void exampleSubscriber(){
		 DirectProvider dp = new DirectProvider();
		 Topology top = dp.newTopology();
		 DDSSubscriberConnector<Reading> readingSubscriber= new DDSSubscriberConnector<Reading>("Readings",
				 Reading.class,ReadingTypeSupport.get_instance());
		 DDSSubscriber<Reading> consumer= new DDSSubscriber<Reading>(top,readingSubscriber);
		 TStream<Reading> readings= consumer.poll(10, TimeUnit.MILLISECONDS);
		 readings.print();
		 dp.submit(top);
	 }
	 public static void examplePublisher(){
		 DirectProvider dp = new DirectProvider();
		 Topology top = dp.newTopology();
		 DDSPublisherConnector<Reading> readingPublisher= new DDSPublisherConnector<Reading>("Readings",
				 Reading.class,ReadingTypeSupport.get_instance());
		 DDSPublisher<Reading> publisher=new DDSPublisher<Reading>(readingPublisher);
		 TStream<Reading> readings= top.poll(()-> {
			 return new Reading();
		 },1,TimeUnit.SECONDS);
		 publisher.publish(readings);
		 dp.submit(top);

	 }
	 
}
