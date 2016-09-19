h_id=$1
dataFile=$2
echo "Publishing data for house id: $h_id"
java -cp build/libs/*:libs/*:$EDGENT_HOME/target/java8/lib/*:$NDDSHOME/lib/java/nddsjava.jar com.rti.edge.types.reading.ReadingPublisher 0 Readings $dataFile
