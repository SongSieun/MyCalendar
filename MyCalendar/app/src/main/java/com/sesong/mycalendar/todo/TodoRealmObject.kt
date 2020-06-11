package com.sesong.mycalendar.todo

import io.realm.RealmObject

class TodoRealmObject : RealmObject() {
    var title: String = ""
    var content: String = ""
    var date: String = ""

    override fun toString(): String {
        return "TodoRealmObject{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                '}'
    }
}