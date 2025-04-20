package com.mine.flowpay.data.dao
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mine.flowpay.data.Users

@Dao
interface UserDao {
    // Create
    @Insert
    fun insertUser(user: Users)

    //Get user by ID
    @Query("SELECT * FROM users WHERE user_id = :userId")
    fun getUserById(userId: Long): Users?

    //Get user by email
    @Query("SELECT * FROM users WHERE email = :email")
    fun getUserByEmail(email: String): Users?

    //Get user by username
    @Query("SELECT * FROM users WHERE username = :username")
    fun getUserByUsername(username: String): Users?

    // Get all users
    @Query("SELECT * FROM users")
    fun getAllUsers(): List<Users>

    //Update
    @Update
    fun updateUser(user: Users)

    //Delete
    @Delete
    fun deleteUser(user: Users)
}