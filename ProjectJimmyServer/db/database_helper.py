import sqlite3
import os
from operator import itemgetter

# This module contains functions for database interaction


SOUND_LIST_DB_FILENAME = os.path.abspath("db/sound_list.db")


def create_sound_table():
    db_connection = sqlite3.connect(SOUND_LIST_DB_FILENAME)
    db_cursor = db_connection.cursor()
    db_cursor.execute("DROP TABLE IF EXISTS sound;")
    
    sql_command = """
    CREATE TABLE IF NOT EXISTS sound (
    sound_id INTEGER PRIMARY KEY,
    sound_name VARCHAR(50),
    playable INTEGER,
    UNIQUE(sound_name));"""

    db_cursor.execute(sql_command)
    db_connection.commit()

    db_cursor.close()
    db_connection.close()


def populate_sound_table(sounds):
    db_connection = sqlite3.connect(SOUND_LIST_DB_FILENAME)
    db_cursor = db_connection.cursor()

    for sound_path in sounds:
        sql_insert_command = """
        INSERT OR IGNORE INTO sound (sound_id, sound_name, playable)
        VALUES (NULL, ?, 1);"""

        db_cursor.execute(sql_insert_command, [str(sound_path)])
        db_connection.commit()

    prune_sound_table(sounds)

    db_cursor.close()
    db_connection.close()


def prune_sound_table(sounds):
    db_connection = sqlite3.connect(SOUND_LIST_DB_FILENAME)
    db_cursor = db_connection.cursor()

    sql_select_command = """
    SELECT sound_name FROM sound;
    """
    output = db_cursor.execute(sql_select_command)
    current_tuple_list = output.fetchall();
    current_list = [str(x[0]) for x in current_tuple_list]

    current_sound_set = set(current_list)
    new_sound_set = set(sounds)
    deleted_sounds = list(current_sound_set - new_sound_set)

    for sound_name in deleted_sounds:
        sql_delete_command = """
        DELETE FROM sound WHERE sound_name=?;"""
        db_cursor.execute(sql_delete_command, [sound_name])
        db_connection.commit()

    db_cursor.close()
    db_connection.close()


def sounds_table_to_json():
    db_connection = sqlite3.connect(SOUND_LIST_DB_FILENAME)
    db_cursor = db_connection.cursor()

    db_query = db_cursor.execute("SELECT * FROM sound")
    column_names = [d[0] for d in db_query.description]
    sound_list = [dict(zip(column_names, r)) for r in db_query.fetchall()]
    for sound in sound_list:
        sound_file = os.path.basename(sound.get('sound_name'))
        sound_file = os.path.splitext(sound_file)[0]
        sound_file = sound_file.replace("_", " ").title()
        sound['sound_name'] = str(sound_file)

    db_cursor.close()
    db_connection.close()

    sound_list = sorted(sound_list, key=itemgetter('sound_name'))

    return sound_list 


def get_sound_list():
    db_connection = sqlite3.connect(SOUND_LIST_DB_FILENAME)
    db_cursor = db_connection.cursor()

    db_cursor.execute("SELECT sound_name FROM sound WHERE playable = ?", [1])
    results = db_cursor.fetchall()
    result_list = [sound_tuple[0] for sound_tuple in results]

    db_cursor.close()
    db_connection.close()

    return result_list


def update_sound_enabled(sound_id, is_enabled):
    db_connection = sqlite3.connect(SOUND_LIST_DB_FILENAME)
    db_cursor = db_connection.cursor()

    db_cursor.execute("UPDATE sound SET playable = ? WHERE sound_id = ?", [is_enabled, sound_id])

    db_connection.commit()

    db_cursor.close()
    db_connection.close()



