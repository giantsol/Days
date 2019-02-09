package com.hansollee.mydays.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hansollee.mydays.SECONDS_PER_DAY
import com.hansollee.mydays.toEpochSecond
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

/**
 * Created by kevin-ee on 2019-02-01.
 */

@Entity(tableName = "tasks",
    indices = arrayOf(Index(value = ["start", "end"])))
data class Task(@ColumnInfo(name = "start") val startDateTime: LocalDateTime,
                @ColumnInfo(name = "end") val endDateTime: LocalDateTime?,
                @ColumnInfo(name = "task_description") val desc: String,
                @ColumnInfo(name = "color_int") val colorInt: Int) : Parcelable {

    @PrimaryKey(autoGenerate = true) var id = 0L

    constructor(parcel: Parcel) : this(
        parcel.readSerializable() as LocalDateTime,
        parcel.readSerializable() as LocalDateTime,
        parcel.readString(),
        parcel.readInt()
    ) {
        this.id = parcel.readLong()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeSerializable(startDateTime)
        dest.writeSerializable(endDateTime)
        dest.writeString(desc)
        dest.writeInt(colorInt)
        dest.writeLong(id)
    }

    override fun describeContents(): Int = 0

    // Task는 여러 개의 date에 걸쳐있을 수 있다.
    // 파라미터 date에 해당되는 Task인지
    fun belongsToDate(date: LocalDate): Boolean {
        val dateSeconds = date.toEpochSecond()
        val tomorrowDateSeconds = dateSeconds + SECONDS_PER_DAY
        val startSeconds = startDateTime.toEpochSecond()
        val endSeconds = endDateTime?.toEpochSecond()
        return (startSeconds in dateSeconds.until(tomorrowDateSeconds)) ||
            (startSeconds < dateSeconds && endSeconds != null && endSeconds > dateSeconds)
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}