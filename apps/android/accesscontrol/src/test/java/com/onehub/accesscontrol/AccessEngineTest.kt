package com.onehub.accesscontrol

import com.onehub.accesscontrol.engine.AccessEngine
import com.onehub.accesscontrol.model.LocalUser
import com.onehub.accesscontrol.model.Permission
import com.onehub.accesscontrol.repo.InMemoryAccessRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AccessEngineTest {
    private val engine = AccessEngine()

    @Test
    fun hashIsStableAndNotPlaintext() {
        val h = engine.hashCodeValue("OH-TEST-CODE")
        assertEquals(64, h.length)
        assertFalse(h.contains("TEST"))
        assertEquals(h, engine.hashCodeValue("OH-TEST-CODE"))
    }

    @Test
    fun cannotIssueAdminToolsViaCode() {
        val r = engine.issueCodeWithAudit(
            ownerId = "o1",
            permissionIds = listOf(Permission.ADMIN_TOOLS.id),
            validFromEpochMs = 0,
            validToEpochMs = null,
            maxUses = 1,
            label = "bad",
            nowEpochMs = 1,
        )
        assertTrue(r.isFailure)
    }

    @Test
    fun activateAndExpire() = runBlocking {
        val repo = InMemoryAccessRepository(engine)
        val now = System.currentTimeMillis()
        repo.provisionOwner("Owner", "password123", now).getOrThrow()
        val issued = repo.issueCode(
            permissionIds = listOf(Permission.HUB_BASIC.id, Permission.WORKSPACE.id),
            validFromEpochMs = now - 1_000,
            validToEpochMs = now + 60_000,
            maxUses = 2,
            label = "trial",
            nowEpochMs = now,
        ).getOrThrow()

        val user = repo.createUser("Alice")
        repo.switchUser(user.id)
        repo.activateCode(issued.plaintextOnce, now).getOrThrow()
        assertTrue(repo.require(Permission.HUB_BASIC, now))
        assertTrue(repo.require(Permission.WORKSPACE, now))
        assertFalse(repo.require(Permission.ADMIN_TOOLS, now))

        // Engine-level expiry check (independent of wall-clock strip)
        val alice = repo.currentUser()!!
        assertFalse(engine.hasPermission(alice, Permission.HUB_BASIC, now + 120_000))
    }

    @Test
    fun revokeBlocksActivation() = runBlocking {
        val repo = InMemoryAccessRepository(engine)
        val now = System.currentTimeMillis()
        repo.provisionOwner("Owner", "password123", now).getOrThrow()
        val issued = repo.issueCode(
            listOf(Permission.HUB_BASIC.id),
            now - 1_000,
            now + 1_000_000,
            5,
            "x",
            now,
        ).getOrThrow()
        repo.revokeCode(issued.code.id, now).getOrThrow()
        val user = repo.createUser("Bob")
        repo.switchUser(user.id)
        assertTrue(repo.activateCode(issued.plaintextOnce, now).isFailure)
    }

    @Test
    fun renewExtendsValidity() = runBlocking {
        val repo = InMemoryAccessRepository(engine)
        val now = System.currentTimeMillis()
        repo.provisionOwner("Owner", "password123", now).getOrThrow()
        val issued = repo.issueCode(
            listOf(Permission.DEVELOPER_TOOLS.id),
            now - 1_000,
            now + 1_000,
            null,
            "dev",
            now,
        ).getOrThrow()
        val until = now + 50_000
        repo.renewCode(issued.code.id, until, now).getOrThrow()
        val codes = repo.listCodes()
        assertEquals(until, codes.first().validToEpochMs)
    }

    @Test
    fun usageLimitEnforced() {
        val user = LocalUser("u1", "U", false, emptyMap())
        val pair = engine.issueCodeWithAudit(
            "o", listOf(Permission.HUB_BASIC.id), 0, null, 1, "once", 1,
        ).getOrThrow()
        val issued = pair.first
        val first = engine.activateCode(issued.plaintextOnce, listOf(issued.code), user, 2).getOrThrow()
        val second = engine.activateCode(
            issued.plaintextOnce,
            listOf(first.code),
            first.user,
            3,
        )
        assertTrue(second.isFailure)
    }

    @Test
    fun ownerHasAllPermissions() {
        val owner = LocalUser("o", "Owner", true, emptyMap())
        assertTrue(engine.hasPermission(owner, Permission.ADMIN_TOOLS, 0))
        assertTrue(engine.hasPermission(owner, Permission.FEATURE_PHOTOCLEANER, 0))
    }
}
