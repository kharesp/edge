echo "Started metrics subscriber"
java -cp build/libs/*:libs/*:$EDGENT_HOME/target/java8/lib/*:$NDDSHOME/lib/java/nddsjava.jar com.rti.edge.types.metrics.HousePerfMetricsSubscriber 1 15
