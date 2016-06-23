# DHPClient

This module provides a client for the Philips DHP API.
Currently it supports DHP release 1.2.0.0 only.

## Usage

The project currently provides clients for the following DHP API's:
 
- User Management
- Subscription Management
- Authentication Management


## Maven setup

Currently there's no central artifact repository to store build versions of this library.
To use this module in your project include it as a module like so:

```
// pom.xml

...

<modules>
    <module>../../lib/dhpclient/</module>
</modules>

...

<dependencies>
    <dependency>
        <groupId>com.philips</groupId>
        <artifactId>dhpclient</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>

...

```

## Caveats

This project has implicit dependencies on the following modules:

* joda-time
* guava
* spring-web
* jackson

Be sure to add compatible versions of these modules if your project does not already include them.
