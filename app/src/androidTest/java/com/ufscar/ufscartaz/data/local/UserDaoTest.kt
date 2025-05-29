package com.ufscar.ufscartaz.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ufscar.ufscartaz.data.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = db.userDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertUser_andRetrieveByEmail() = runBlocking {
        val user = User(name = "Ana", email = "ana@ufscar.br", password = "senha123")
        userDao.insertUser(user)

        val result = userDao.getUserByEmail("ana@ufscar.br")
        assertNotNull(result)
        assertEquals("Ana", result?.name)
    }

    @Test
    fun insertUser_andRetrieveByEmailAndPassword() = runBlocking {
        val user = User(name = "João", email = "joao@ufscar.br", password = "abc123")
        userDao.insertUser(user)

        val result = userDao.getUserByEmailAndPassword("joao@ufscar.br", "abc123")
        assertNotNull(result)
        assertEquals("João", result?.name)
    }

    @Test
    fun insertUser_duplicateEmail_shouldReplace() = runBlocking {
        val user1 = User(id = 1, name = "Maria", email = "maria@ufscar.br", password = "senha1")
        val user2 = User(id = 1, name = "Maria Atualizada", email = "maria@ufscar.br", password = "novaSenha")

        userDao.insertUser(user1)
        userDao.insertUser(user2)

        val result = userDao.getUserByEmail("maria@ufscar.br")
        assertNotNull(result)
        assertEquals("Maria Atualizada", result?.name)
        assertEquals("novaSenha", result?.password)
    }

    @Test
    fun updateUserAvatar_shouldPersist() = runBlocking {
        val user = User(name = "Carlos", email = "carlos@ufscar.br", password = "pass")
        val userId = userDao.insertUser(user)

        userDao.updateUserAvatar(userId, avatarPexelsId = 999, avatarUrl = "https://pexels.com/avatar.png")

        val result = userDao.getUserByEmail("carlos@ufscar.br")
        assertEquals(999, result?.avatarPexelsId)
        assertEquals("https://pexels.com/avatar.png", result?.avatarUrl)
    }

    @Test
    fun getAllUsers_shouldEmitUsers() = runBlocking {
        val user1 = User(name = "A", email = "a@ufscar.br", password = "123")
        val user2 = User(name = "B", email = "b@ufscar.br", password = "456")

        userDao.insertUser(user1)
        userDao.insertUser(user2)

        val users = userDao.getAllUsers().first()
        assertEquals(2, users.size)
    }

    @Test
    fun emailExists_shouldReturnTrueIfExists() = runBlocking {
        val user = User(name = "Pedro", email = "pedro@ufscar.br", password = "senha")
        userDao.insertUser(user)

        val exists = userDao.emailExists("pedro@ufscar.br")
        assertTrue(exists)
    }

    @Test
    fun emailExists_shouldReturnFalseIfNotExists() = runBlocking {
        val exists = userDao.emailExists("naoexiste@ufscar.br")
        assertFalse(exists)
    }
}
