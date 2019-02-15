package com.hansollee.mydays.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

/**
 * Created by kevin-ee on 2019-02-01.
 */

@Entity(tableName = "records",
    indices = arrayOf(Index(value = ["date"])))
data class Record(@ColumnInfo(name = "date") val date: LocalDate,
                  @ColumnInfo(name = "from_time") val fromTime: LocalTime,
                  @ColumnInfo(name = "to_time") val toTime: LocalTime,
                  @ColumnInfo(name = "task_description") val task: String) : Parcelable {

    @PrimaryKey(autoGenerate = true) var id = 0L

    constructor(parcel: Parcel) : this(
        parcel.readSerializable() as LocalDate,
        parcel.readSerializable() as LocalTime,
        parcel.readSerializable() as LocalTime,
        parcel.readString()
    ) {
        this.id = parcel.readLong()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeSerializable(date)
        dest.writeSerializable(fromTime)
        dest.writeSerializable(toTime)
        dest.writeString(task)
        dest.writeLong(id)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Record> {
        override fun createFromParcel(parcel: Parcel): Record {
            return Record(parcel)
        }

        override fun newArray(size: Int): Array<Record?> {
            return arrayOfNulls(size)
        }
    }
}