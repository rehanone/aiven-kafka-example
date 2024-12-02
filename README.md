# Apache Kafka Streaming Service with FS2 and Cats Effect on Aiven Hosted Kafka Broker

This project demonstrates how to use [fs2](fs2.io) and 
[Cats Effect](https://typelevel.org/cats-effect/) in Scala for 
connecting to a Kafka broker on [Aiven](aiven.io). The code shows the 
use of Kafka producer and consumer functionality, ensuring a robust and 
scalable approach to streaming and processing data in a functional programming style.


Table of contents
=================

<!--ts-->
* [Summary](#summary)
* [Solution Architecture](#solution-architecture)
* [Aiven Setup](#aiven-setup)
* [Building the Project](#building-the-project)
* [Usage Instructions](#usage-instructions)
* [License](#license)
<!--te-->

Summary
=======

This project showcases how to build a Kafka-based streaming application using 
the FS2 library (Functional Streams for Scala) and Cats Effect for managing side effects. 
It includes an example for both Kafka Producer and Kafka Consumer, connecting to an Aiven 
hosted Kafka broker.

Aiven provides managed cloud services for Kafka, ensuring high availability, 
scalability, and ease of integration. In this example, we use Aiven's Kafka service 
to demonstrate secure and reliable Kafka communication with a functional and reactive 
programming paradigm using FS2 and Cats Effect.

Solution Architecture
=====================

The solution consists of the following components:

1. **Kafka Consumer:**
    * Consumes messages from a specified Kafka topic.
    * Uses FS2 streams to consume messages in a scalable and non-blocking manner.
    * Processes consumed messages and applies any necessary business logic.

2. **Kafka Producer:**
    * Produces messages to a specified Kafka topic.
    * Uses FS2 for stream processing and Cats Effect for managing the effects.
    * Sends data asynchronously with built-in backpressure and error handling.

3. **Kafka Broker (Aiven):**
    * The Kafka service is hosted on Aiven, providing a managed, secure, and scalable Kafka cluster.
    * Configuration and authentication to the Kafka broker are handled securely using provided credentials (username, password, Kafka endpoint).

4. **Security & Connectivity:**
    * The project connects to the Kafka broker securely over TLS.
    * Authentication details such as username, password, and SSL certificates are used for a secure connection to the broker.

5. **Diagram**
    * The following diagram shows the high level architecture of the solution
    * ![Alt text here](diagrams/aiven-kafka-example.svg)

Aiven Setup
===========

The solution deployment on Aiven consists of the following steps:

1. Setup Aiven Kafka:

    * Create new Aiven project and setup Kafka service using the [Getting Started](https://aiven.io/docs/products/kafka/get-started) guide.
    * Create the following Kafka topics for this demo:
        * `pizza-orders.json`
        * `pizza-orders.ack.json`

2. Event Source:
    * Use [Python Fake Data Producer for Apache Kafka](https://github.com/Aiven-Labs/python-fake-data-producer-for-apache-kafka) to create stream of fake **pizza-orders** on topic `pizza-orders.json`.
    * Example command to generate the initial load:
      ```shell
        python3 main.py \
        --security-protocol ssl \
        --cert-folder ./kafkaCerts/ \
        --host kafka-<name>.aivencloud.com \
        --port 14844 \
        --topic-name pizza-orders.json \
        --nr-messages 0 \
        --max-waiting-time 0 \
        --subject pizza
      ``` 

Building the Project
====================

This project is built using SBT (Scala Build Tool). Below are the instructions to build and run the project.

### Prerequisites:

   * Java 17+
   * SBT (Scala Build Tool)
   * Kafka (Aiven Managed Kafka Instance)
   * Scala 3.x or higher

### Steps to Build:

1. Install JDK:

   * Setup JDK on your system using this [Installation Guide](https://docs.oracle.com/en/java/javase/21/install/overview-jdk-installation.html).
   * Ensure the JDK is installed and ready:
     ```shell
       java -version
     ``` 

2. Clone the repository:
     ```shell
       git clone https://github.com/rehanone/aiven-kafka-example.git
     ``` 
3. Build:
   * Compile the code using:
     ```shell
       ./sbt clean compile
     ``` 

Usage Instructions
==================

### Kafka Configuration:

Ensure you have the correct configuration details from your Aiven Kafka instance. These include:

   * **Kafka Broker URI:** (e.g., my-kafka-cluster-12345.aivenkafka.com:9093)
   * **Kafka Username:** (e.g., my-kafka-username)
   * **Kafka Password:** (e.g., my-kafka-password)
   * **SSL Certificates:** (for secure TLS connection)

You will need to set these values in your `application.conf` or pass them as environment variables.

1. Create keystore and truststore:
   * Use the openssl utility to create the keystore with the `service.key` and `service.cert` files downloaded previously:
     ```shell
       openssl pkcs12 -export -inkey service.key -in service.cert -out client.keystore.p12 -name service_key
     ``` 
     Enter a password to protect the keystore and the key, when prompted.

   * In the folder where the certificates are stored, use the keytool utility to create the truststore with the `ca.pem` file as input:
     ```shell
       keytool -import -file ca.pem -alias CA -keystore client.truststore.jks
     ``` 
     Enter a password to protect the trust stores and reply yes to confirm trusting the CA certificate, when prompted.

   * The result are the keystore named `client.keystore.p12` and truststore named `client.truststore.jks` that can be used for client applications configuration in the next steps.

2. Create directory `./secret` in the directory where you cloned the project and move the files `client.keystore.p12` and `client.truststore.jks` in it.
 
3. The environment variables example:
   ```shell
     export AIVEN_KAFKA_CONSUMER_TRUSTSTORE_LOCATION=.secrets/client.truststore.jks
     export AIVEN_KAFKA_CONSUMER_TRUSTSTORE_PASSWORD="aiven-test"
     export AIVEN_KAFKA_CONSUMER_KEYSTORE_LOCATION=".secrets/client.keystore.p12"
     export AIVEN_KAFKA_CONSUMER_KEYSTORE_PASSWORD="aiven-test"
   
     export AIVEN_KAFKA_PRODUCER_TRUSTSTORE_LOCATION=.secrets/client.truststore.jks
     export AIVEN_KAFKA_PRODUCER_TRUSTSTORE_PASSWORD="aiven-test"
     export AIVEN_KAFKA_PRODUCER_KEYSTORE_LOCATION=".secrets/client.keystore.p12"
     export AIVEN_KAFKA_PRODUCER_KEYSTORE_PASSWORD="aiven-test"
   ```

2. Run the service:
   ```shell
     ./sbt run "aiven.kafka.App"
   ```

License
=======

This project is licensed under the MIT License - see the LICENSE file for details.
