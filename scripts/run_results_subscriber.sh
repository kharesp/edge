echo "Started results subscriber"
java -cp build/libs/*:libs/*:$EDGENT_HOME/target/java8/lib/*:$NDDSHOME/lib/java/nddsjava.jar com.rti.edge.types.houseAvg.HouseAvgSubscriber 1 15
