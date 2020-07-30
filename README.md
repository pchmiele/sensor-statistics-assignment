# Sensor Statistics Task

Create a command line program that calculates statistics from humidity sensor data.

### Requirements

 * any [JDK](https://github.com/graalvm/graalvm-ce-builds/releases/tag/vm-20.1.0) (> 8.0) - tested on OpenJDK 64-Bit GraalVM CE 19.3.0 
 * [sbt](https://www.scala-sbt.org/download.html)
 
### How to run
From project root directory:

```
sbt "run PATH_TO_DIR"
```
where PATH_TO_DIR is path to dir with csv files with sensor data.

Example run for data from assignment:
```
sbt "run src/it/resources/multipleValidFiles"
```

### How to test

* Unit tests
```
sbt test
```
* Integration tests
```
sbt it:test
```
* All available tests:
```
sbt test it:test
```

### Assumptions:

- When no data is provided (not existing dir or dir with empty files) there will be no sorted statistics for sensors list printed 
- CSV header is not parsed, so if entries are in the different format than (id,humidity) program may not display valid results
- Avg value is rounded to the closest integer value - it can be easily modified, but for readability I left it as is
- Program should be able to more or less effectively use memory (parse multiple large files with limited memory), unless the amount of sensors 
is so big that, program is not able to hold it in memory. Program uses Map(sensor -> statistics) to track it's internal state, so in case
when each entry in the file comes from a different sensor, and given XmX is smaller than file size it may result with OutOfMemory exception. 
- I didn't cover each part of application with unit tests, instead I added integration test to cover the e2e scenarios

### Additional info
I'm much more experienced with Cats/Cats-Effect/Monix | Akka Streams, I decided to use ZIO instead because I use it more and more often
for my side project and I'm satisfied with the results. Additionally, it seems that ZIO may be next game changer in Scala community (or maybe it is now)

### Background story

The sensors are in a network, and they are divided into groups. Each sensor submits its data to its group leader.
Each leader produces a daily report file for a group. The network periodically re-balances itself, so the sensors could
change the group assignment over time, and their measurements can be reported by different leaders. The program should
help spot sensors with highest average humidity.

## Input

- Program takes one argument: a path to directory
- Directory contains many CSV files (*.csv), each with a daily report from one group leader
- Format of the file: 1 header line + many lines with measurements
- Measurement line has sensor id and the humidity value
- Humidity value is integer in range `[0, 100]` or `NaN` (failed measurement)
- The measurements for the same sensor id can be in the different files

### Example

leader-1.csv
```
sensor-id,humidity
s1,10
s2,88
s1,NaN
```

leader-2.csv
```
sensor-id,humidity
s2,80
s3,NaN
s2,78
s1,98
```

## Expected Output

- Program prints statistics to StdOut
- It reports how many files it processed
- It reports how many measurements it processed
- It reports how many measurements failed
- For each sensor it calculates min/avg/max humidity
- `NaN` values are ignored from min/avg/max
- Sensors with only `NaN` measurements have min/avg/max as `NaN/NaN/NaN`
- Program sorts sensors by highest avg humidity (`NaN` values go last)

### Example

```
Num of processed files: 2
Num of processed measurements: 7
Num of failed measurements: 2

Sensors with highest avg humidity:

sensor-id,min,avg,max
s2,78,82,88
s1,10,54,98
s3,NaN,NaN,NaN
```

## Notes

- [done] (checked for multiple large files - 8 files 1GB each and with only 256 MB of memory) Single daily report file can be very large, and can exceed program memory
- [done] Program should only use memory for its internal state (no disk, no database)
- [done] Any open source library can be used (besides Spark) 
- [done] (ZIO used) Please use vanilla scala, akka-stream, monix or similar technology. 
- [done] (ZIO used) You're more than welcome to implement a purely functional solution using cats-effect, fs2 and/or ZIO to impress, 
  but this is not a mandatory requirement. 
- [done] (both unit and integration tests) Sensible tests are welcome
