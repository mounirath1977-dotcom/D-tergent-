package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AdDao {
    // 7 days window: only retrieve ads where createdAt >= cutoffTime, limited to 20
    @Query("SELECT * FROM ads WHERE createdAt >= :cutoffTime ORDER BY createdAt DESC LIMIT 20")
    fun getActiveAds(cutoffTime: Long): Flow<List<AdEntity>>

    @Query("SELECT COUNT(*) FROM ads WHERE createdAt >= :cutoffTime")
    suspend fun getActiveAdsCount(cutoffTime: Long): Int

    @Query("SELECT * FROM ads ORDER BY createdAt DESC")
    fun getAllAdsAdmin(): Flow<List<AdEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAd(ad: AdEntity): Long

    @Query("DELETE FROM ads WHERE id = :adId")
    suspend fun deleteAdById(adId: Long): Int

    // Purge expired ads older than 7 days (createdAt < cutoffTime)
    @Query("DELETE FROM ads WHERE createdAt < :cutoffTime")
    suspend fun deleteExpiredAds(cutoffTime: Long): Int

    @Query("SELECT * FROM ads WHERE sellerUserId = :userId AND createdAt >= :cutoffTime ORDER BY createdAt DESC")
    fun getAdsByUser(userId: String, cutoffTime: Long): Flow<List<AdEntity>>
}
