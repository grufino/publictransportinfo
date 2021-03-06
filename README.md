# Design Decisions

Because the dataset is small and there are no requirements about persistence I chose to save CSV data in spark in memory, to also be a little faster to bootstrap, tho a scalable solution for this data model would probably involve a RDBMS and a chache layer.

Note: I realized when I was almost done that storing the delay on the stop dataframe instead of the time dataframe would be a smarter decision, since delays are not associated to time but to a stop (not so easy to change), also I assumed that delays don't stack up but maybe this was a wrong assumption, no details were provided (easy to change). 

Note 2: I know polling instead of subscribing to a topic and also auto committing are not the best practices, but we are not on concurrent threads so for the sake of the example I guess that's ok.

### Running

You need to download and install sbt for this application to run.

Once you have sbt installed, the following at the command prompt will start up Play in development mode:

```bash
sbt run
```

Play will start up on the HTTP port at <http://localhost:8081/>.

The Static data will be automatically loaded from the files, and a worker will be started to read from the Kafka Topic with delays info (check that the topic is up). 

### Usage

To GET a vehicle for a given time and X & Y coordinates (if you are running dev mode, first request takes a few seconds as it loads the static data and starts consumer)

```bash
# (local time is acquired automatically in case query param not provided)
http://localhost:8081/v1/publicTransport/vehicleForStop/1?time=05:00:00
{"lineName":"M4","stopId":1,"time":"05:01"}
```

Likewise, you can also GET directly from the stopId:

```bash
# (local time is acquired automatically in case query param not provided)
curl http://localhost:8081/v1/publicTransport/vehicleForLocation/1/1?time=05:00:00
{"lineName":"M4","stopId":0,"time":"05:00"}
```

and also check if a specific line is delayed:

```bash
curl http://localhost:8081/v1/publicTransport/delay/S75
{"lineName":"S75","delayInSeconds":1080,"isDelayed":true}
```