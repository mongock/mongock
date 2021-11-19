package io.mongock.driver.dynamodb.springboot.config

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import io.mongock.api.config.MongockConfiguration
import io.mongock.driver.api.driver.ConnectionDriver
import org.springframework.context.annotation.Bean
import io.mongock.driver.dynamodb.driver.DynamoDBDriver

class DynamoDBSpringbootContext {

    @Bean
    fun connectionDriver(
        client: AmazonDynamoDBClient,
        mongockConfig: MongockConfiguration,
        dynamoDBConfig: DynamoDBConfiguration
    ): ConnectionDriver {
        val driver = DynamoDBDriver.withLockStrategy(
            client,
            mongockConfig.lockAcquiredForMillis,
            mongockConfig.lockQuitTryingAfterMillis,
            mongockConfig.lockTryFrequencyMillis
        )
        driver.provisionedThroughput = dynamoDBConfig?.provisionedThroughput
        return driver;
    }


}