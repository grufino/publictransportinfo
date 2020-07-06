# Mobimeo Data Team Engineering Challenge

## Problem

In the fictional city of Verspaetung, public transport is notoriously unreliable. To tackle the problem, the city council has decided to make the public transport timetable and delay information public, opening up opportunities for innovative use cases.

You are given the task of writing a web API to expose the Verspaetung public transport information.

As a side note, the city of Verspaetung has been built on a strict grid - all location information can be assumed to be from a cartesian coordinate system.

## Data

The Verspaetung public transport information is consist of 2 parts: static and dynamic.

The static information is comprised of 3 CSV files:
- `data/lines.csv` - the public transport lines.
- `data/stops.csv` - the stops along each line.
- `data/times.csv` - the time vehicles arrive & depart at each stop. The timestamp is in the format of `HH:MM:SS`.

Line delay information is dynamic and provided in real time via Kafka topic.

In order to obtain the data:
- Ensure you environment has `docker` and `docker-compose` installed and docker daemon is running.
- Run the delay information provider `docker-compose up data-producer` (it should start 3 containers: zookeeper, kafka broker and delay generator).
- Connect to the broker using bootstrap server `localhost:9092` and subscribe to the topic `delays`.
- You should receive messages in the following format:
    - Record key is a line name. The key type is `String` and it can be any `line_name` from `lines.csv`.
    - Record value is a current line delay in minutes. The value type is `java.lang.Integer`. It can be any non negative number.

## Challenge

Build a web API which provides the following features:

- Find a vehicle for a given time and X & Y coordinates
- Return the vehicle arriving next at a given stop
- Indicate if a given line is currently delayed

Endpoints should be available via port `8081`

## Implementation

Please complete the challenge in Scala. You're free to use frameworks or libraries of your choice.

Make sure to follow general software development best practices and best practices of the respective language.

## Delivery

Create a git repository and share the code with us through GitHub or zip file.
