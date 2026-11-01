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
        val h = engine.hashCode("OH-TEST-CODE")
        assertEquals(64, h.length)
        assertFalse(h.contains("TEST"))
        assertEquals(h, engine.hashCode("OH-TEST-CODE"))
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
        val now = 1_000_000L
        repo.provisionOwner("Owner", "password123", now).getOrThrow()
        val issued = repo.issueCode(
            permissionIds = listOf(Permission.HUB_BASIC.id, Permission.WORKSPACE.id),
            validFromEpochMs = now,
            validToEpochMs = now + 10_000,
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

        // After expiry
        assertFalse(repo.require(Permission.HUB_BASIC, now + 20_000))
    }

    @Test
    fun revokeBlocksActivation() = runBlocking {
        val repo = InMemoryAccessRepository(engine)
        val now = 5_000L
        repo.provisionOwner("Owner", "password123", now).getOrThrow()
        val issued = repo.issueCode(
            listOf(Permission.HUB_BASIC.id),
            now,
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
        val now = 10_000L
        repo.provisionOwner("Owner", "password123", now).getOrThrow()
        val issued = repo.issueCode(
            listOf(Permission.DEVELOPER_TOOLS.id),
            now,
            now + 1_000,
            null,
            "dev",
            now,
        ).getOrThrow()
        repo.renewCode(issued.code.id, now + 50_000, now).getOrThrow()
        val codes = repo.listCodes()
        assertEquals(now + 50_000, codes.first().validToEpochMs)
    }

    @Test
    fun usageLimitEnforced() {
        val user = LocalUser("u1", "U", false, emptyMap())
        val issued = engine.issueCodeWithAudit(
            "o", listOf(Permission.HUB_BASIC.id), 0, null, 1, "once", 1,
        ).getOrThrow()
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
