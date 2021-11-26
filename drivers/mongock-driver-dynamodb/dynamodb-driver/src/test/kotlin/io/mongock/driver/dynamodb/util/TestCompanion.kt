package io.mongock.driver.dynamodb.util

import io.kotest.core.spec.style.scopes.DescribeSpecContainerContext
import io.kotest.core.test.TestContext
import io.mongock.driver.api.driver.ConnectionDriver
import io.mongock.driver.api.entry.ChangeEntry
import io.mongock.driver.api.entry.ChangeEntryService
import io.mongock.driver.core.lock.LockEntry
import io.mongock.driver.core.lock.LockRepository


interface TestCompanion<in REPO_EXTRA_CONFIG> {

    fun start()

    fun stopContainer()

    fun checkTableIsCreated(tableName: String)

    fun getDriver(): ConnectionDriver

    /**
     * ChangeEntryService
     */
    fun getChangeService(tableName: String, indexCreation: Boolean, t:REPO_EXTRA_CONFIG): ChangeEntryService

    fun createChangeEntryTable(tableName: String)

    fun insertChangeEntries(tableName: String, vararg entries: ChangeEntry)

    fun createInsert(tableName: String, vararg entries: ChangeEntry)

    fun isInserted(tableName: String, changeEntry: ChangeEntry): Boolean

    fun getChangeEntry(tableName: String, changeEntry: ChangeEntry): ChangeEntry?

    /**
     * LOCK
     */
    fun getLockRepository(tableName: String, indexCreation: Boolean, t:REPO_EXTRA_CONFIG): LockRepository

    fun createLockTable(tableName: String)

    fun isInserted(tableName: String, lockEntry: LockEntry): Boolean

    fun getLockEntry(tableName: String, lockEntry: LockEntry): LockEntry?

    fun insertLockEntries(tableName: String, vararg entries: LockEntry)

    fun createInsert(tableName: String, vararg entries: LockEntry)


}


suspend fun DescribeSpecContainerContext.tearDown(
    name: String,
    test: suspend TestContext.() -> Unit
) {
    it("[tear-down]: $name", test)
}

suspend fun DescribeSpecContainerContext.should(
    name: String,
    test: suspend TestContext.() -> Unit
) {
    it("SHOULD $name", test)
}

suspend fun DescribeSpecContainerContext.xshould(
    name: String,
    test: suspend TestContext.() -> Unit
) {
    xit("SHOULD $name", test)
}

suspend fun DescribeSpecContainerContext.and(
    name: String,
    test: suspend TestContext.() -> Unit
) {
    describe("AND $name", test)
}

suspend fun DescribeSpecContainerContext.and(
    name: String,
    preTest: () -> Unit,
    test: suspend TestContext.() -> Unit
) {
    preTest()
    and(name, test)
}

suspend fun DescribeSpecContainerContext.When(
    name: String,
    test: suspend TestContext.() -> Unit
) {
    describe("WHEN $name", test)
}

suspend fun DescribeSpecContainerContext.xWhen(
    name: String,
    test: suspend TestContext.() -> Unit
) {
    xdescribe("WHEN $name", test)
}

suspend fun DescribeSpecContainerContext.When(
    name: String,
    preTest: () -> Unit,
    test: suspend TestContext.() -> Unit
) {
    preTest()
    When(name, test)
}

suspend fun DescribeSpecContainerContext.xWhen(
    name: String,
    preTest: () -> Unit,
    test: suspend TestContext.() -> Unit
) {
    xWhen(name, test)
}

suspend fun DescribeSpecContainerContext.describe(
    name: String,
    preTest: () -> Unit,
    test: suspend TestContext.() -> Unit
) {
    preTest()
    describe(name, test)
}
