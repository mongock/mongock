package io.mongock.driver.dynamodb.springboot.config

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import io.mongock.api.config.MongockConfiguration
import io.mongock.driver.api.driver.ConnectionDriver
import io.mongock.driver.dynamodb.driver.DynamoDBDriver
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import


@Configuration
@Import(DynamoDBConfiguration::class)
@ConditionalOnExpression("\${mongock.enabled:true}")
open class DynamoDBSpringbootContext {

    @Bean
    open fun connectionDriver(
        client: AmazonDynamoDBClient,
        mongockConfig: MongockConfiguration,
        dynamoDBConfig: DynamoDBConfiguration?
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