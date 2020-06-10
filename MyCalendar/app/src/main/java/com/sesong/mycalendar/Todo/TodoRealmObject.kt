package com.sesong.mycalendar.Todo

class TodoRealmObject : RealmObject() {
    var title: String? = null
    var content: String? = null
    var date: String? = null

    override fun toString(): String {
        return "TodoRealmObject{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                '}'
    }
}