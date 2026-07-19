package com.example.dsamaster.di

import android.content.Context
import androidx.room.Room
import com.example.dsamaster.core.database.DsaDatabase
import com.example.dsamaster.data.ActivityRepositoryImpl
import com.example.dsamaster.data.ContentRepositoryImpl
import com.example.dsamaster.data.FlashcardRepositoryImpl
import com.example.dsamaster.domain.ActivityRepository
import com.example.dsamaster.domain.ContentRepository
import com.example.dsamaster.domain.FlashcardRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DsaDatabase =
        Room.databaseBuilder(context, DsaDatabase::class.java, "dsa_master.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun topicDao(db: DsaDatabase) = db.topicDao()
    @Provides fun lessonDao(db: DsaDatabase) = db.lessonDao()
    @Provides fun flashcardDao(db: DsaDatabase) = db.flashcardDao()
    @Provides fun quizDao(db: DsaDatabase) = db.quizDao()
    @Provides fun problemDao(db: DsaDatabase) = db.problemDao()
    @Provides fun patternDao(db: DsaDatabase) = db.patternDao()
    @Provides fun studyDayDao(db: DsaDatabase) = db.studyDayDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds abstract fun contentRepository(impl: ContentRepositoryImpl): ContentRepository
    @Binds abstract fun flashcardRepository(impl: FlashcardRepositoryImpl): FlashcardRepository
    @Binds abstract fun activityRepository(impl: ActivityRepositoryImpl): ActivityRepository
}
