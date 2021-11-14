import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.mongock.api.exception.MongockException


class DynamoDBChangeEntryRepositoryITest : DescribeSpec({
    val companion = DynamoDBTestCompanion()

    companion.startContainer()

    /**
     * INITIALIZE
     */
    context("initialize") {
        When("table/collection is not crated") {
            and("indexCreation == false") {
                should("throw MongockException") {
                    val exception = shouldThrow<MongockException> {
                        companion.getChangeService("for-initialize-1", false).initialize()
                    }
                    exception.message shouldStartWith "Table creation not allowed, but not created or wrongly created for table"
                }
            }
            and("indexCreation == true") {
                should("create table/collection") {
                    companion.getChangeService("for-initialize-2", true).initialize()
                    companion.checkTableIsCreated("for-initialize-2")
                }
            }
        }
        When(
            "table/collection IS crated(initialize)",
            { companion.createChangeEntryTable("for-initialize-3") }) {
            and("indexCreation == false") {
                should("be just OK") {
                    companion.getChangeService("for-initialize-3", false).initialize()
                }
            }
            and("indexCreation == true") {
                should("be just OK") {
                    companion.getChangeService("for-initialize-3", false).initialize()
                }
            }
        }
    }

    /**
     * saveOrUpdate
     */
    context("saveOrUpdate") {
        When("changeEntry table is empty", { companion.createChangeEntryTable("for-save-1") }) {
            should("add new changeEntry") {
                val repo = companion.getChangeService("for-save-1", true)
                repo.saveOrUpdate(c1)
                companion.isInserted("for-save-1", c1) shouldBe true
            }
        }

        When(
            "WHEN changeEntry table contains c1, c2 and c3",
            { companion.createInsert("for-save-2", c1, c2, c3) }) {
            and("c1 is saved again") {
                should("update c1") {
                    val repo = companion.getChangeService("for-save-2", true)
                    repo.saveOrUpdate(c1_updated)
                    companion.isInserted("for-save-2", c1) shouldBe true
                    val result = companion.getChangeEntry("for-save-2", c1_updated)
                    result!!.changeId shouldBe c1.changeId
                    result.executionId shouldBe c1.executionId
                    result.author shouldBe c1.author
                    result.changeLogClass shouldBe c1_updated.changeLogClass
                }
            }

        }
    }

    context("getEntriesLog") {
        When("changeEntry table is empty", { companion.createChangeEntryTable("for-entries-1") }) {
            should("return emptyList") {
                val repo = companion.getChangeService("for-entries-1", true)
                repo.entriesLog.size shouldBe 0
            }
        }

        When("WHEN changeEntry not empty", { companion.createInsert("for-save-2", c1, c2, c3) }) {
            should("return the items in the table") {
                val repo = companion.getChangeService("for-save-2", true)
                val items = repo.entriesLog
                items.size shouldBe 3
                items.forEach{
                    it.changeId shouldBeIn listOf<String>(c1.changeId, c2.changeId, c3.changeId)
                }
            }


        }
    }


//    companion.stopContainer()
})


