package io.mongock.driver.dynamodb.repository

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.mongock.api.exception.MongockException
import io.mongock.driver.api.entry.ChangeEntry
import io.kotest.matchers.shouldBe
import io.mongock.driver.dynamodb.util.DynamoDBTestCompanion
import io.mongock.driver.dynamodb.util.When
import io.mongock.driver.dynamodb.util.and
import io.mongock.driver.dynamodb.util.should
import io.mongock.driver.dynamodb.util.change1
import io.mongock.driver.dynamodb.util.change1_u
import io.mongock.driver.dynamodb.util.change2
import io.mongock.driver.dynamodb.util.change3
import io.mongock.driver.dynamodb.util.repoExtraConfig



class DynamoDBChangeEntryRepositoryITest : DescribeSpec({
    val companion = DynamoDBTestCompanion()

    companion.start()

    /**
     * INITIALIZE
     */
    context("initialize") {
        When("table/collection is not crated") {
            and("indexCreation == false") {
                should("throw MongockException") {
                    val exception = shouldThrow<MongockException> {
                        companion.getChangeService("for-initialize-1", false, repoExtraConfig).initialize()
                    }
                    exception.message shouldStartWith "Table creation not allowed, but not created or wrongly created for table"
                }
            }
            and("indexCreation == true") {
                should("create table/collection") {
                    companion.getChangeService("for-initialize-2", true, repoExtraConfig).initialize()
                    companion.checkTableIsCreated("for-initialize-2")
                }
            }
        }
        When(
            "table/collection IS crated(initialize)",
            { companion.createChangeEntryTable("for-initialize-3") }) {
            and("indexCreation == false") {
                should("be just OK") {
                    companion.getChangeService("for-initialize-3", false, repoExtraConfig).initialize()
                }
            }
            and("indexCreation == true") {
                should("be just OK") {
                    companion.getChangeService("for-initialize-3", false, repoExtraConfig).initialize()
                }
            }
        }
    }

    /**
     * SAVE OR UPDATE
     */
    context("saveOrUpdate") {
        When("changeEntry table is empty", { companion.createChangeEntryTable("for-save-1") }) {
            should("add new changeEntry") {
                val repo = companion.getChangeService("for-save-1", true, repoExtraConfig)
                repo.saveOrUpdate(change1)
                companion.isInserted("for-save-1", change1) shouldBe true
            }
        }

        When( "WHEN changeEntry table contains c1, c2 and c3", { companion.createInsert("for-save-2", change1, change2, change3) }) {
            and("c1 is saved again") {
                should("updates c1") {
                    val repo = companion.getChangeService("for-save-2", true, repoExtraConfig)
                    repo.saveOrUpdate(change1_u)
                    companion.isInserted("for-save-2", change1) shouldBe true
                    val result = companion.getChangeEntry("for-save-2", change1_u)
                    result!!.changeId shouldBe change1.changeId
                    result.executionId shouldBe change1.executionId
                    result.author shouldBe change1.author
                    result.changeLogClass shouldBe change1_u.changeLogClass
                }
            }

        }

        When("transaction is enabled") {
            should("add transactWriteItem to list") {
                val repo = companion.getChangeService("for-save-transaction-4", true, repoExtraConfig) as DynamoDBChangeEntryRepository
                repo.initialize()
                val transactionItems = DynamoDBTransactionItems()
                repo.transactionItems = transactionItems
                repo.saveOrUpdate(change1)
                repo.saveOrUpdate(change2)
                transactionItems.items
                    .map { it.put.item[ChangeEntry.KEY_CHANGE_ID]!!.s }
                    .forEach { it shouldBeIn listOf(change1.changeId, change2.changeId)  }
            }

        }
    }

    /**
     * GET ENTRIES LOG
     */
    context("getEntriesLog") {
        When("changeEntry table is empty", { companion.createChangeEntryTable("for-entries-1") }) {
            should("return emptyList") {
                val repo = companion.getChangeService("for-entries-1", true, repoExtraConfig)
                repo.entriesLog.size shouldBe 0
            }
        }

        When("WHEN changeEntry not empty", { companion.createInsert("for-save-2", change1, change2, change3) }) {
            should("return the items in the table") {
                val repo = companion.getChangeService("for-save-2", true, repoExtraConfig)
                val items = repo.entriesLog
                items.size shouldBe 3
                items.forEach{
                    it.changeId shouldBeIn listOf<String>(change1.changeId, change2.changeId, change3.changeId)
                }
            }


        }
    }


//    companion.stopContainer()
})


