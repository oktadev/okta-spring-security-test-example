# Testing Spring Security Example

This repository contains all the code for [Better Testing with Spring Security Test][blog], illustrating request mocking for OIDC code flow, JWT authorization, and OpaqueToken authorization integration tests.

**Prerequisites**:

- [Java 11+](https://openjdk.java.net/install/index.html)
- [Okta CLI](https://cli.okta.com/)

## Getting Started

To install this example, run the following commands:

```bash
git clone https://github.com/oktadev/okta-spring-security-test-example.git
cd okta-spring-security-test-example
```

## Configure the api-gateway

```shell
cd api-gateway
```

Using the [Okta CLI](https://cli.okta.com/), register for a free developer account:

```shell
okta register
```

Provide the required information. Once you complete the registration, create an OIDC application with the following command:

```shell
okta apps create
```

You will be prompted to select the following options:

- Application name: api-gateway
- Type of Application: Web
- Type of Application: Okta Spring Boot Starter
- Redirect URI: Default
- Post Logout Redirect URI: Default

The Okta CLI will create the client application and configure the issuer, clientId and clientSecret in `src/main/resources/application.properties`. Update the `issuer`, `client-id` and `client-secret` in `application.yml`. Delete `application.properties`.

```yaml
okta:
  oauth2:
    issuer: https://{yourOktaDomain}/oauth2/default
    client-id: {clientId}
    client-secret: {clientSecret}
```

## Configure the listings microservice

Update the `issuer` in `application.yml`.

```yaml
okta:
  oauth2:
    issuer: https://{yourOktaDomain}/oauth2/default
```

## Configure the theaters microservice

```shell
cd theaters
```

With Okta CLI, create a client application as illustrated before, and provide the following settings:

- Application name: theaters
- Type of Application: Web
- Type of Application: Spring Boot
- Redirect URI: Default
- Post Logout Redirect URI: Default

Update the `issuer`, `client-id` and `client-secret` in `application.yml`, from the values in `application.properties`. Delete `application.properties`.

## Configure the MongoDB Data files

Get the MongoDB dump files `theaters.bson`, `theaters.metadata.json` from [Github](https://github.com/huynhsamha/quick-mongo-atlas-datasets/tree/master/dump/sample_mflix). Also get the MongoDB dump files `listingsAndReviews.bson`, `listingsAndreviews.metadata.json` from [GitHub](https://github.com/huynhsamha/quick-mongo-atlas-datasets/tree/master/dump/sample_airbnb). Place all files in the same folder. Then update `docker/docker-compose.yml` `/db-dump` volume mapping for the `mongo` service to set the dumps folder.

```yaml
volumes:
  - ./initdb.sh:/docker-entrypoint-initdb.d/initdb.sh
  - <path-to-files-you-downloaded>:/db-dump
```

## Build each application's image

Go through each project and build its Docker image with the following command:

```shell
./mvnw spring-boot:build-image
```

## Run the applications with Docker Compose

```shell
cd docker
docker-compose up
```

Go to `http://localhost:8080/userdata` and login to Okta. Copy the `accessToken` and set as an environment variable:

```shell
ACCESS_TOKEN={accessToken}

http POST http://localhost:8080/listing name=test "Authorization:Bearer ${ACCESS_TOKEN}"
```

You will see the following response:
```
HTTP/1.1 403 Forbidden
WWW-Authenticate: Bearer error="insufficient_scope", 
  error_description="The request requires higher privileges than provided by the access token.", 
  error_uri="https://tools.ietf.org/html/rfc6750#section-3.1"
```

Configure the required groups `listing_admin` and `theater_admin` in the Okta dashboard, and add the `groups` claim to the `accessToken` as detailed in the blog post.

## Links

This example uses the following open source libraries from Okta:

* [Okta CLI](https://github.com/okta/okta-cli)
* [Okta Spring Boot Starter](https://github.com/okta/okta-spring-boot)

## Help

Please post any questions as comments on [this example's blog post][blog], or use our [Okta Developer Forums](https://devforum.okta.com/).

## License

Apache 2.0, see [LICENSE](LICENSE).

[blog]: https://developer.okta.com/blog/2021/05/19/spring-security-testing
