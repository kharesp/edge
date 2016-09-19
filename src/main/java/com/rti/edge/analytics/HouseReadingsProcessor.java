package com.rti.edge.analytics;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;
import com.rti.edge.types.householdAvg.HouseholdAvg;
import com.rti.edge.types.reading.Reading;

public class HouseReadingsProcessor {
	private Topology topology;
	private TStream<Reading> houseStream;
	private int window_length_secs;

	public HouseReadingsProcessor(Topology topology, TStream<Reading> houseStream, int window_length_secs) {
		this.topology = topology;
		this.houseStream = houseStream;
		this.window_length_secs = window_length_secs;
	}

	public void process() {
		List<TStream<Reading>> houseHold_streams = houseStream.
				split(20, r -> r.household_id); // assuming a max of 20 households	

		List<TStream<HouseholdAvg>> houseHold_avgLoad_streams = new LinkedList<TStream<HouseholdAvg>>();
		for (TStream<Reading> houseHold_stream : houseHold_streams) {
			TStream<HouseholdAvg> houseHold_avgLoad_stream = new HouseHoldReadingsProcessor(topology, houseHold_stream,
					window_length_secs).process();
			houseHold_avgLoad_streams.add(houseHold_avgLoad_stream);
		}
		TStream<HouseholdAvg> result = houseHold_avgLoad_streams.get(0)
				.union(new HashSet<TStream<HouseholdAvg>>(houseHold_avgLoad_streams));
		result.print();

	}

}
