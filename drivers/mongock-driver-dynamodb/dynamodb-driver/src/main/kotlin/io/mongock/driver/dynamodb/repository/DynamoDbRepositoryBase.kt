package io.mongock.driver.dynamodb.repository

import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import io.mongock.driver.api.common.EntityRepository
import io.mongock.utils.field.FieldInstance

open class DynamoDbRepositoryBase<DOMAIN_CLASS>(private val dynamoDB: DynamoDB,
                                                private val tableName: String,
                                                private val keySchemaElements: List<KeySchemaElement>,
                                                private val attributeDefinitions: List<AttributeDefinition>):EntityRepository<DOMAIN_CLASS, Item> {

    @Synchronized
    override  fun initialize() {
        TODO("Not yet implemented")
    }

    override fun mapFieldInstances(fieldInstanceList: MutableList<FieldInstance>?): Item {
        TODO("Not yet implemented")
    }
}