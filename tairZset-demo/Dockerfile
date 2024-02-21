FROM 10.0.1.120:8000/ops/jdk17:v3
RUN mkdir /app
WORKDIR /app

add /target/tairZset-demo-1.0-SNAPSHOT.jar /app/tairZset-demo-1.0-SNAPSHOT.jar
CMD ["java","-jar","-Xms128m","-XX:+UseG1GC","tairZset-demo-1.0-SNAPSHOT.jar"]
