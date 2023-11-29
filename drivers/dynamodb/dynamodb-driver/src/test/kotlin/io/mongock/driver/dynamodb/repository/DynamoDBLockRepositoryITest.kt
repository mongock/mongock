package io.mongock.driver.dynamodb.repository

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldStartWith
import io.mongock.api.exception.MongockException
import io.mongock.driver.core.lock.LockPersistenceException
import io.mongock.driver.dynamodb.util.DynamoDBTestCompanion
import io.mongock.driver.dynamodb.util.When
import io.mongock.driver.dynamodb.util.and
import io.mongock.driver.dynamodb.util.lockOwner1Expired
import io.mongock.driver.dynamodb.util.lockOwner1NotExpired
import io.mongock.driver.dynamodb.util.lockOwner1NotExpiredUpdated
import io.mongock.driver.dynamodb.util.lockOwner2NotExpired
import io.mongock.driver.dynamodb.util.repoExtraConfig
import io.mongock.driver.dynamodb.util.should


class DynamoDBLockRepositoryITest : DescribeSpec({
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
                        companion.getLockRepository("lock-t-initialize-1", false, repoExtraConfig).initialize()
                    }
                    exception.message shouldStartWith "Table creation not allowed, but not created or wrongly created for table"
                }
            }
            and("indexCreation == true") {
                should("create table/collection") {
                    companion.getLockRepository("lock-t-initialize-2", true, repoExtraConfig).initialize()
                    companion.checkTableIsCreated("lock-t-initialize-2")
                }
            }
        }
        When(
            "table/collection IS crated(initialize)",
            { companion.createLockTable("lock-t-initialize-3") }) {
            and("indexCreation == false") {
                should("be just OK") {
                    companion.getLockRepository("lock-t-initialize-3", false, repoExtraConfig).initialize()
                }
            }
            and("indexCreation == true") {
                should("be just OK") {
                    companion.getLockRepository("lock-t-initialize-3", false, repoExtraConfig).initialize()
                }
            }
        }
    }
    /**
     * INSERT-UPDATE
     */
    context("insertUpdate") {
        When("LockEntry table is empty", { companion.createLockTable("lock-t-save-1") }) {
            should("insert lock") {
                val repo = companion.getLockRepository("lock-t-save-1", true, repoExtraConfig)
                repo.insertUpdate(lockOwner1NotExpired)
                companion.isInserted("lock-t-save-1", lockOwner1NotExpired) shouldBe true
            }
        }

        When("WHEN lock is held for owner-1", { companion.createInsert("lock-t-save-2", lockOwner1NotExpired) }) {
            and("it's not expired") {
                should("allow owner-1 to extend the lock") {
                    val repo = companion.getLockRepository("lock-t-save-2", true, repoExtraConfig)
                    repo.insertUpdate(lockOwner1NotExpiredUpdated)
                    companion.getLockEntry(
                        "lock-t-save-2",
                        lockOwner1NotExpiredUpdated
                    )!!.expiresAt shouldBe lockOwner1NotExpiredUpdated.expiresAt

                }
                should("not allow owner-2 to acquire lock") {
                    val repo = companion.getLockRepository("lock-t-save-2", true, repoExtraConfig)
                    shouldThrow<LockPersistenceException> {
                        repo.insertUpdate(lockOwner2NotExpired)
                    }
                }
            }
            and("it's expired", { companion.createInsert("lock-t-save-3", lockOwner1Expired) }) {
                should("allow another owner to acquire lock") {
                    val repo = companion.getLockRepository("lock-t-save-3", true, repoExtraConfig)
                    companion.isInserted("lock-t-save-3", lockOwner1Expired) shouldBe true
                    repo.insertUpdate(lockOwner2NotExpired)
                    companion.getLockEntry(
                        "lock-t-save-3",
                        lockOwner2NotExpired
                    )!!.expiresAt shouldBe lockOwner2NotExpired.expiresAt
                }

            }


        }
    }
    /**
     * UPDATE-IF-SAME-OWNER
     */
    context("updateIfSameOwner") {
        When("LockEntry table is empty", { companion.createLockTable("lock-t-save-4") }) {
            should("not allow  to updateIfSameOwner") {
                val repo = companion.getLockRepository("lock-t-save-4", true, repoExtraConfig)
                shouldThrow<LockPersistenceException> {
                    repo.updateIfSameOwner(lockOwner1NotExpired)
                }
            }
        }
        When("owner-1 has expired lock in table", { companion.createInsert("lock-t-save-5", lockOwner1Expired) }) {
            should("not allow other owner to updateIfSameOwner") {
                val repo = companion.getLockRepository("lock-t-save-5", true, repoExtraConfig)
                shouldThrow<LockPersistenceException> {
                    repo.updateIfSameOwner(lockOwner2NotExpired)
                }
            }
            should("allow same owner to updateIfSameOwner") {
                val repo = companion.getLockRepository("lock-t-save-5", true, repoExtraConfig)
                repo.insertUpdate(lockOwner1NotExpired)
                companion.isInserted("lock-t-save-5", lockOwner1NotExpired) shouldBe true
            }
        }
    }
    /**
     * FIND-BY-KEY
     */
    context("findByKey") {
        When("lock table is empty", { companion.createLockTable("lock-t-save-6") }) {
            should("return null") {
                companion.getLockRepository("lock-t-save-6", true, repoExtraConfig).findByKey("anyKey") shouldBe null
            }
        }
        When("lock is in table", {companion.createInsert("lock-t-save-7", lockOwner1Expired)}) {
            should("return lock") {
                companion.getLockRepository("lock-t-save-7", true, repoExtraConfig).findByKey(lockOwner1Expired.key) shouldNotBe null
            }
        }
    }
    /**
     * REMOVE-BY-KEY-AND--OWNER
     */
    context("removeByKeyAndOwner") {
        When("lock table is empty", { companion.createLockTable("lock-t-save-8") }) {
            should("should be just OK") {
                companion.getLockRepository("lock-t-save-8", true, repoExtraConfig).removeByKeyAndOwner("anyKey", "anyOwner")
            }
        }
        When("lock is in table", {companion.createInsert("lock-t-save-9", lockOwner1NotExpired)}) {
            should("remove lock") {
                companion.isInserted("lock-t-save-9", lockOwner1NotExpired) shouldBe true
                companion.getLockRepository("lock-t-save-9", true, repoExtraConfig).removeByKeyAndOwner(lockOwner1NotExpired.key, lockOwner1NotExpired.owner)
                companion.isInserted("lock-t-save-9", lockOwner1NotExpired) shouldBe false
            }
        }
    }

})


