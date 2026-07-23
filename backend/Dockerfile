FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/app.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-Xms128m -Xmx200m -XX:MaxMetaspaceSize=128m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]