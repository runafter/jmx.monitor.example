### Usage
1. run jmx.monitor.example.target with vmargs
```
java -Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.port=5555 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false jmx.monitor.example.target.Main
```

2. run jmx.monitor.example with remote jmx information
```
java jmx.monitor.example.Main 127.0.0.1 5555
```

### Restriction
- supported only Oracle JVM
