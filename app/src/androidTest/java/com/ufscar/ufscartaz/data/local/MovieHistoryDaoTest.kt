package com.ufscar.ufscartaz.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ufscar.ufscartaz.data.model.MovieHistoryEntry
import com.ufscar.ufscartaz.data.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MovieHistoryDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: MovieHistoryDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        dao = db.movieHistoryDao()

        // Inserir um usuário de teste (necessário por causa da foreign key)
        runBlocking {
            db.userDao().insertUser(
                User(
                    id = 1L,
                    name = "Teste",
                    email = "teste@ufscar.br",
                    password = "123456", // senha fictícia
                    avatarPexelsId = null,
                    avatarUrl = null
                )
            )
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRetrieveHistoryEntriesOrderedByTimestamp() = runBlocking {
        val userId = 1L

        // Inserir 3 entradas com timestamps personalizados
        val entries = listOf(
            MovieHistoryEntry(userId = userId, movieId = 100, timestamp = 1000),
            MovieHistoryEntry(userId = userId, movieId = 101, timestamp = 2000),
            MovieHistoryEntry(userId = userId, movieId = 102, timestamp = 3000)
        )

        entries.forEach { dao.insertEntry(it) }

        val history = dao.getHistoryForUser(userId).first()

        Assert.assertEquals(3, history.size)
        Assert.assertEquals(102, history[0].movieId) // mais recente
        Assert.assertEquals(101, history[1].movieId)
        Assert.assertEquals(100, history[2].movieId) // mais antigo
    }

    @Test
    fun deleteHistoryOlderThanTimestamp() = runBlocking {
        val userId = 1L
        dao.insertEntry(MovieHistoryEntry(userId = userId, movieId = 100, timestamp = 1000))
        dao.insertEntry(MovieHistoryEntry(userId = userId, movieId = 101, timestamp = 2000))

        val deleted = dao.deleteOldEntries(1500)
        Assert.assertEquals(1, deleted)

        val remaining = dao.getHistoryForUser(userId).first()
        Assert.assertEquals(1, remaining.size)
        Assert.assertEquals(101, remaining[0].movieId)
    }

    @Test
    fun clearUserHistoryWorks() = runBlocking {
        val userId = 1L
        dao.insertEntry(MovieHistoryEntry(userId = userId, movieId = 100))
        dao.insertEntry(MovieHistoryEntry(userId = userId, movieId = 101))

        dao.clearHistoryForUser(userId)

        val remaining = dao.getHistoryForUser(userId).first()
        Assert.assertTrue(remaining.isEmpty())
    }
}
