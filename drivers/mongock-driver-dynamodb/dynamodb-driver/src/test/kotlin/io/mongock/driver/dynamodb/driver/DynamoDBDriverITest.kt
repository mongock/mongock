package io.mongock.driver.dynamodb.driver

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mongock.api.exception.MongockException
import io.mongock.driver.api.entry.ChangeEntry
import io.mongock.driver.api.entry.ChangeState
import io.mongock.driver.api.entry.ChangeType
import io.mongock.driver.dynamodb.util.When
import io.mongock.driver.dynamodb.util.should
import io.mongock.driver.dynamodb.util.DynamoDBTestCompanion
import io.mongock.driver.dynamodb.util.change1
import io.mongock.driver.dynamodb.util.change2
import io.mongock.driver.dynamodb.util.changeFailed
import java.util.*

class DynamoDBDriverITest : DescribeSpec({
    val companion = DynamoDBTestCompanion()

    if (companion.transactionServerEnabled) {
        companion.start()
        context("transactions") {
            should("execute the transaction successfully") {
                val driver = companion.getDriver()
                driver.migrationRepositoryName = "driver-transaction-1"
                driver.initialize()
                driver.prepareForExecutionBlock()
                val changeEntryService = driver.changeEntryService
                driver.transactioner.get().executeInTransaction {
                    changeEntryService.saveOrUpdate(change1)
                    changeEntryService.saveOrUpdate(change2)
                }
                companion.isInserted(driver.migrationRepositoryName, change1) shouldBe true
                companion.isInserted(driver.migrationRepositoryName, change2) shouldBe true
            }
            When("one item fails") {
                should("rollback") {
                    val driver = companion.getDriver()
                    driver.migrationRepositoryName = "driver-transaction-rollback"
                    driver.initialize()
                    driver.prepareForExecutionBlock()
                    val changeEntryService = driver.changeEntryService
                    shouldThrow<MongockException> {
                        driver.transactioner.get().executeInTransaction {
                            changeEntryService.saveOrUpdate(change1)
                            changeEntryService.saveOrUpdate(change2)
                            changeEntryService.saveOrUpdate(changeFailed)
                        }
                    }
                    companion.isInserted(driver.migrationRepositoryName, change1) shouldBe false
                    companion.isInserted(driver.migrationRepositoryName, change2) shouldBe false
                }
            }
            should("be able to execute 25 writetransaction items") {
                val driver = companion.getDriver()
                driver.migrationRepositoryName = "driver-transaction-25-items"
                driver.initialize()
                driver.prepareForExecutionBlock()
                val changeEntryService = driver.changeEntryService
                val listAdded = ArrayList<ChangeEntry>(25)
                driver.transactioner.get().executeInTransaction {
                    for (i in 1..25) {
                        println("$i)changeEntry ")
                        val changeEntry = changeEntry("c$i")
                        listAdded.add(changeEntry)
                        changeEntryService.saveOrUpdate(changeEntry)
                    }
                }
                listAdded.forEach { companion.isInserted(driver.migrationRepositoryName, it) shouldBe true }

            }
        }
    } else {
        context(">>TRANSACTION TESTS DISABLED") {

        }
    }

})

private fun changeEntry(changeId:String) :ChangeEntry {
    return  ChangeEntry(
        "executionId",
        changeId,
        "author-c1",
        Date(),
        ChangeState.EXECUTED,
        ChangeType.EXECUTION,
        "changeLogClass",
        "changeSetMethod",
        0L,
        "executionHostname",
        mapOf("this" to "that")
    )
}