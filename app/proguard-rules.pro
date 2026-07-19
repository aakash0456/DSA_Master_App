# Room, Hilt, Compose and WorkManager ship consumer rules; nothing extra is
# required for this app today. Keep entity names readable in crash logs:
-keepclassmembers class com.example.dsamaster.core.database.** { *; }
