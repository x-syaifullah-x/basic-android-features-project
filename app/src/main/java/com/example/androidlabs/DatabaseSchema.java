package com.example.androidlabs;

public class DatabaseSchema {
    public static class Messages {
        public static final String NAME = "message";

        public static class Columns {
            public static final String ID = "message_id";
            public static final String IS_SEND = "message_is_send";
            public static final String TEXT = "message_text";
        }
    }
}
