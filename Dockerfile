
FROM eclipse-temurin:21-jdk AS buildstage 
RUN apt-get update && apt-get install -y maven

WORKDIR /app

RUN mkdir -p /root/.m2 && printf '<settings>\n  <mirrors>\n    <mirror>\n      <id>google-maven-central</id>\n      <name>Google Maven Central Mirror</name>\n      <url>https://maven-central.storage-download.googleapis.com/maven2/</url>\n      <mirrorOf>central</mirrorOf>\n    </mirror>\n  </mirrors>\n</settings>\n' > /root/.m2/settings.xml

COPY pom.xml .
COPY src /app/src
RUN mvn clean package -DskipTests -Dhttp.keepAlive=false -Dmaven.wagon.http.pool=false

FROM eclipse-temurin:21-jdk

COPY --from=buildstage /app/target/*.jar /app/app.jar

EXPOSE 8080

RUN mkdir -p /app/efs

CMD ["java", "-jar", "/app/app.jar"]
